package lab;

import org.h2.jdbcx.JdbcDataSource;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.support.DefaultTransactionStatus;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public final class TransactionManagementLab {

    private TransactionManagementLab() {
    }

    public static void main(String[] args) throws Exception {
        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(TxLabConfiguration.class)) {

            Schema schema = context.getBean(Schema.class);
            schema.reset();

            printProxy("requiredOuterService", context.getBean(RequiredOuterService.class));
            printProxy("requiresNewOuterService", context.getBean(RequiresNewOuterService.class));
            printProxy("nestedImportService", context.getBean(NestedImportService.class));

            experimentRequiredUnexpectedRollback(context, schema);
            experimentRequiresNew(context, schema);
            experimentNestedSavepoint(context, schema);
            experimentCheckedExceptions(context, schema);
            experimentTransactionTemplate(context, schema);
            experimentSynchronizationAndEvents(context, schema);
            experimentThreadBoundary(context);
            experimentIsolation(context);
            experimentOutboxAtomicity(context, schema);

            CountingTransactionManager txManager =
                    context.getBean(CountingTransactionManager.class);
            System.out.println("\n=== PHYSICAL TRANSACTION COUNTERS ===");
            System.out.println("begins    = " + txManager.begins());
            System.out.println("commits   = " + txManager.commits());
            System.out.println("rollbacks = " + txManager.rollbacks());
        }
    }

    private static void experimentRequiredUnexpectedRollback(
            AnnotationConfigApplicationContext context,
            Schema schema
    ) {
        System.out.println("\n=== REQUIRED + UNEXPECTED ROLLBACK ===");
        schema.clearBusinessTables();

        try {
            context.getBean(RequiredOuterService.class)
                    .createOrderAndCatchPaymentFailure("ORD-REQ");
        } catch (UnexpectedRollbackException error) {
            System.out.println("caller sees = " + error.getClass().getSimpleName());
        }

        System.out.println("orders committed   = " + schema.count("orders"));
        System.out.println("payments committed = " + schema.count("payments"));
    }

    private static void experimentRequiresNew(
            AnnotationConfigApplicationContext context,
            Schema schema
    ) {
        System.out.println("\n=== REQUIRES_NEW INDEPENDENT COMMIT ===");
        schema.clearBusinessTables();

        try {
            context.getBean(RequiresNewOuterService.class)
                    .createOrderThenFail("ORD-NEW");
        } catch (BusinessFailure error) {
            System.out.println("outer failure = " + error.getMessage());
        }

        System.out.println("orders committed = " + schema.count("orders"));
        System.out.println("audit committed  = " + schema.count("audit_log"));
    }

    private static void experimentNestedSavepoint(
            AnnotationConfigApplicationContext context,
            Schema schema
    ) {
        System.out.println("\n=== NESTED SAVEPOINT ===");
        schema.clearBusinessTables();

        context.getBean(NestedImportService.class)
                .importRows("GOOD-1", "BAD-2", "GOOD-3");

        List<String> rows = schema.queryStrings(
                "select row_value from imported_row order by id"
        );
        System.out.println("committed rows = " + rows);
    }

    private static void experimentCheckedExceptions(
            AnnotationConfigApplicationContext context,
            Schema schema
    ) {
        System.out.println("\n=== CHECKED EXCEPTION ROLLBACK RULES ===");
        schema.clearBusinessTables();

        CheckedExceptionService service =
                context.getBean(CheckedExceptionService.class);

        try {
            service.defaultCheckedException("DEFAULT-CHECKED");
        } catch (IOException error) {
            System.out.println("default method threw IOException");
        }

        try {
            service.rollbackForCheckedException("ROLLBACK-CHECKED");
        } catch (IOException error) {
            System.out.println("rollbackFor method threw IOException");
        }

        System.out.println(
                "DEFAULT-CHECKED committed = "
                        + schema.exists("tx_marker", "marker", "DEFAULT-CHECKED")
        );
        System.out.println(
                "ROLLBACK-CHECKED committed = "
                        + schema.exists("tx_marker", "marker", "ROLLBACK-CHECKED")
        );
    }

    private static void experimentTransactionTemplate(
            AnnotationConfigApplicationContext context,
            Schema schema
    ) {
        System.out.println("\n=== TRANSACTION TEMPLATE ===");
        schema.clearBusinessTables();

        ProgrammaticService service =
                context.getBean(ProgrammaticService.class);

        service.store("TEMPLATE-COMMIT", false);
        service.store("TEMPLATE-ROLLBACK", true);

        System.out.println(
                "template committed marker = "
                        + schema.exists("tx_marker", "marker", "TEMPLATE-COMMIT")
        );
        System.out.println(
                "template rollback marker  = "
                        + schema.exists("tx_marker", "marker", "TEMPLATE-ROLLBACK")
        );
    }

    private static void experimentSynchronizationAndEvents(
            AnnotationConfigApplicationContext context,
            Schema schema
    ) {
        System.out.println("\n=== SYNCHRONIZATION + TRANSACTIONAL EVENT ===");
        schema.clearBusinessTables();

        CompletionListener listener =
                context.getBean(CompletionListener.class);
        listener.reset();

        context.getBean(CommitCallbackService.class)
                .commitWithCallbacks("CALLBACK-1");

        System.out.println("afterCommit callbacks = " + listener.afterCommitCount());
        System.out.println("events after commit   = " + listener.eventCount());
    }

    private static void experimentThreadBoundary(
            AnnotationConfigApplicationContext context
    ) throws InterruptedException {
        System.out.println("\n=== THREAD BOUNDARY ===");
        context.getBean(ThreadBoundaryService.class)
                .showWorkerTransactionState();
    }

    private static void experimentIsolation(
            AnnotationConfigApplicationContext context
    ) {
        System.out.println("\n=== ISOLATION INTROSPECTION ===");
        context.getBean(IsolationService.class)
                .serializableOperation();
    }

    private static void experimentOutboxAtomicity(
            AnnotationConfigApplicationContext context,
            Schema schema
    ) {
        System.out.println("\n=== OUTBOX ATOMICITY ===");
        schema.clearBusinessTables();

        OutboxWriter writer = context.getBean(OutboxWriter.class);
        writer.createOrder("ORD-OUTBOX-OK", false);

        try {
            writer.createOrder("ORD-OUTBOX-FAIL", true);
        } catch (BusinessFailure error) {
            System.out.println("failed writer rolled back");
        }

        System.out.println(
                "successful order exists = "
                        + schema.exists("orders", "order_no", "ORD-OUTBOX-OK")
        );
        System.out.println(
                "successful outbox exists = "
                        + schema.exists(
                                "outbox_message",
                                "aggregate_id",
                                "ORD-OUTBOX-OK"
                        )
        );
        System.out.println(
                "failed order exists     = "
                        + schema.exists("orders", "order_no", "ORD-OUTBOX-FAIL")
        );
        System.out.println(
                "failed outbox exists    = "
                        + schema.exists(
                                "outbox_message",
                                "aggregate_id",
                                "ORD-OUTBOX-FAIL"
                        )
        );

        context.getBean(OutboxRelay.class).relayOnce();

        System.out.println(
                "published outbox rows   = "
                        + schema.queryInt(
                                "select count(*) from outbox_message "
                                        + "where status='PUBLISHED'"
                        )
        );
    }

    private static void printProxy(String name, Object bean) {
        System.out.println(
                name
                        + ": proxy=" + AopUtils.isAopProxy(bean)
                        + ", cglib=" + AopUtils.isCglibProxy(bean)
                        + ", target="
                        + AopUtils.getTargetClass(bean).getSimpleName()
        );
    }

    @Configuration
    @EnableTransactionManagement(proxyTargetClass = true)
    static class TxLabConfiguration {

        @Bean
        DataSource dataSource() {
            JdbcDataSource dataSource = new JdbcDataSource();
            dataSource.setURL(
                    "jdbc:h2:mem:txlab;"
                            + "DB_CLOSE_DELAY=-1;"
                            + "LOCK_TIMEOUT=5000"
            );
            dataSource.setUser("sa");
            dataSource.setPassword("");
            return dataSource;
        }

        @Bean
        JdbcTemplate jdbcTemplate(DataSource dataSource) {
            return new JdbcTemplate(dataSource);
        }

        @Bean
        CountingTransactionManager transactionManager(DataSource dataSource) {
            CountingTransactionManager manager =
                    new CountingTransactionManager(dataSource);
            manager.setNestedTransactionAllowed(true);
            manager.setValidateExistingTransaction(true);
            return manager;
        }

        @Bean
        TransactionTemplate transactionTemplate(
                PlatformTransactionManager transactionManager
        ) {
            return new TransactionTemplate(transactionManager);
        }

        @Bean
        Schema schema(JdbcTemplate jdbc) {
            return new Schema(jdbc);
        }

        @Bean
        RequiredInnerService requiredInnerService(JdbcTemplate jdbc) {
            return new RequiredInnerService(jdbc);
        }

        @Bean
        RequiredOuterService requiredOuterService(
                JdbcTemplate jdbc,
                RequiredInnerService inner
        ) {
            return new RequiredOuterService(jdbc, inner);
        }

        @Bean
        AuditService auditService(JdbcTemplate jdbc) {
            return new AuditService(jdbc);
        }

        @Bean
        RequiresNewOuterService requiresNewOuterService(
                JdbcTemplate jdbc,
                AuditService auditService
        ) {
            return new RequiresNewOuterService(jdbc, auditService);
        }

        @Bean
        NestedRowWorker nestedRowWorker(JdbcTemplate jdbc) {
            return new NestedRowWorker(jdbc);
        }

        @Bean
        NestedImportService nestedImportService(
                JdbcTemplate jdbc,
                NestedRowWorker worker
        ) {
            return new NestedImportService(jdbc, worker);
        }

        @Bean
        CheckedExceptionService checkedExceptionService(JdbcTemplate jdbc) {
            return new CheckedExceptionService(jdbc);
        }

        @Bean
        ProgrammaticService programmaticService(
                JdbcTemplate jdbc,
                TransactionTemplate transactionTemplate
        ) {
            return new ProgrammaticService(jdbc, transactionTemplate);
        }

        @Bean
        CompletionListener completionListener() {
            return new CompletionListener();
        }

        @Bean
        CommitCallbackService commitCallbackService(
                JdbcTemplate jdbc,
                ApplicationEventPublisher publisher,
                CompletionListener listener
        ) {
            return new CommitCallbackService(jdbc, publisher, listener);
        }

        @Bean
        ThreadBoundaryService threadBoundaryService(JdbcTemplate jdbc) {
            return new ThreadBoundaryService(jdbc);
        }

        @Bean
        IsolationService isolationService(JdbcTemplate jdbc) {
            return new IsolationService(jdbc);
        }

        @Bean
        OutboxWriter outboxWriter(JdbcTemplate jdbc) {
            return new OutboxWriter(jdbc);
        }

        @Bean
        OutboxRelay outboxRelay(
                JdbcTemplate jdbc,
                TransactionTemplate transactionTemplate
        ) {
            return new OutboxRelay(jdbc, transactionTemplate);
        }
    }

    static final class Schema {

        private final JdbcTemplate jdbc;

        Schema(JdbcTemplate jdbc) {
            this.jdbc = jdbc;
        }

        void reset() {
            jdbc.execute("drop all objects");

            jdbc.execute(
                    "create table orders ("
                            + "id bigint generated by default as identity primary key,"
                            + "order_no varchar(100) not null unique)"
            );
            jdbc.execute(
                    "create table payments ("
                            + "id bigint generated by default as identity primary key,"
                            + "order_no varchar(100) not null)"
            );
            jdbc.execute(
                    "create table audit_log ("
                            + "id bigint generated by default as identity primary key,"
                            + "message varchar(255) not null)"
            );
            jdbc.execute(
                    "create table imported_row ("
                            + "id bigint generated by default as identity primary key,"
                            + "row_value varchar(100) not null)"
            );
            jdbc.execute(
                    "create table tx_marker ("
                            + "id bigint generated by default as identity primary key,"
                            + "marker varchar(100) not null unique)"
            );
            jdbc.execute(
                    "create table outbox_message ("
                            + "id varchar(36) primary key,"
                            + "aggregate_id varchar(100) not null,"
                            + "event_type varchar(100) not null,"
                            + "payload varchar(1000) not null,"
                            + "status varchar(30) not null)"
            );
        }

        void clearBusinessTables() {
            jdbc.update("delete from outbox_message");
            jdbc.update("delete from tx_marker");
            jdbc.update("delete from imported_row");
            jdbc.update("delete from audit_log");
            jdbc.update("delete from payments");
            jdbc.update("delete from orders");
        }

        int count(String table) {
            return queryInt("select count(*) from " + table);
        }

        int queryInt(String sql) {
            Integer value = jdbc.queryForObject(sql, Integer.class);
            return value == null ? 0 : value;
        }

        boolean exists(String table, String column, String value) {
            Integer count = jdbc.queryForObject(
                    "select count(*) from " + table
                            + " where " + column + " = ?",
                    Integer.class,
                    value
            );
            return count != null && count > 0;
        }

        List<String> queryStrings(String sql) {
            return jdbc.query(
                    sql,
                    (resultSet, rowNumber) -> resultSet.getString(1)
            );
        }
    }

    static class RequiredOuterService {

        private final JdbcTemplate jdbc;
        private final RequiredInnerService inner;

        RequiredOuterService(JdbcTemplate jdbc, RequiredInnerService inner) {
            this.jdbc = jdbc;
            this.inner = inner;
        }

        @Transactional
        public void createOrderAndCatchPaymentFailure(String orderNo) {
            jdbc.update("insert into orders(order_no) values (?)", orderNo);

            try {
                inner.reserveThenFail(orderNo);
            } catch (PaymentRejected error) {
                System.out.println("outer caught = " + error.getMessage());
            }

            System.out.println(
                    "outer returns normally, rollbackOnly is hidden until commit"
            );
        }
    }

    static class RequiredInnerService {

        private final JdbcTemplate jdbc;

        RequiredInnerService(JdbcTemplate jdbc) {
            this.jdbc = jdbc;
        }

        @Transactional
        public void reserveThenFail(String orderNo) {
            jdbc.update("insert into payments(order_no) values (?)", orderNo);
            throw new PaymentRejected("payment rejected");
        }
    }

    static class RequiresNewOuterService {

        private final JdbcTemplate jdbc;
        private final AuditService auditService;

        RequiresNewOuterService(
                JdbcTemplate jdbc,
                AuditService auditService
        ) {
            this.jdbc = jdbc;
            this.auditService = auditService;
        }

        @Transactional
        public void createOrderThenFail(String orderNo) {
            jdbc.update("insert into orders(order_no) values (?)", orderNo);
            auditService.record("created " + orderNo);
            throw new BusinessFailure("outer business failure");
        }
    }

    static class AuditService {

        private final JdbcTemplate jdbc;

        AuditService(JdbcTemplate jdbc) {
            this.jdbc = jdbc;
        }

        @Transactional(propagation = Propagation.REQUIRES_NEW)
        public void record(String message) {
            jdbc.update("insert into audit_log(message) values (?)", message);
        }
    }

    static class NestedImportService {

        private final JdbcTemplate jdbc;
        private final NestedRowWorker worker;

        NestedImportService(JdbcTemplate jdbc, NestedRowWorker worker) {
            this.jdbc = jdbc;
            this.worker = worker;
        }

        @Transactional
        public void importRows(String... values) {
            for (String value : values) {
                try {
                    worker.importOne(value);
                } catch (InvalidRow error) {
                    System.out.println("nested row rolled back = " + value);
                }
            }

            jdbc.update(
                    "insert into tx_marker(marker) values (?)",
                    "NESTED-OUTER-COMPLETED"
            );
        }
    }

    static class NestedRowWorker {

        private final JdbcTemplate jdbc;

        NestedRowWorker(JdbcTemplate jdbc) {
            this.jdbc = jdbc;
        }

        @Transactional(propagation = Propagation.NESTED)
        public void importOne(String value) {
            jdbc.update(
                    "insert into imported_row(row_value) values (?)",
                    value
            );

            if (value.startsWith("BAD")) {
                throw new InvalidRow(value);
            }
        }
    }

    static class CheckedExceptionService {

        private final JdbcTemplate jdbc;

        CheckedExceptionService(JdbcTemplate jdbc) {
            this.jdbc = jdbc;
        }

        @Transactional
        public void defaultCheckedException(String marker) throws IOException {
            jdbc.update("insert into tx_marker(marker) values (?)", marker);
            throw new IOException("checked failure");
        }

        @Transactional(rollbackFor = IOException.class)
        public void rollbackForCheckedException(String marker)
                throws IOException {
            jdbc.update("insert into tx_marker(marker) values (?)", marker);
            throw new IOException("checked failure with rollback rule");
        }
    }

    static class ProgrammaticService {

        private final JdbcTemplate jdbc;
        private final TransactionTemplate template;

        ProgrammaticService(JdbcTemplate jdbc, TransactionTemplate template) {
            this.jdbc = jdbc;
            this.template = template;
        }

        void store(String marker, boolean rollback) {
            template.execute(status -> {
                jdbc.update("insert into tx_marker(marker) values (?)", marker);

                if (rollback) {
                    status.setRollbackOnly();
                }

                return null;
            });
        }
    }

    static class CommitCallbackService {

        private final JdbcTemplate jdbc;
        private final ApplicationEventPublisher publisher;
        private final CompletionListener listener;

        CommitCallbackService(
                JdbcTemplate jdbc,
                ApplicationEventPublisher publisher,
                CompletionListener listener
        ) {
            this.jdbc = jdbc;
            this.publisher = publisher;
            this.listener = listener;
        }

        @Transactional
        public void commitWithCallbacks(String marker) {
            jdbc.update("insert into tx_marker(marker) values (?)", marker);

            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                            listener.recordDirectAfterCommit();
                            System.out.println("direct afterCommit callback");
                        }
                    }
            );

            publisher.publishEvent(new MarkerCommittedEvent(marker));
        }
    }

    static class CompletionListener {

        private final AtomicInteger directAfterCommit = new AtomicInteger();
        private final AtomicInteger transactionEvents = new AtomicInteger();

        void reset() {
            directAfterCommit.set(0);
            transactionEvents.set(0);
        }

        void recordDirectAfterCommit() {
            directAfterCommit.incrementAndGet();
        }

        @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
        public void onMarkerCommitted(MarkerCommittedEvent event) {
            transactionEvents.incrementAndGet();
            System.out.println(
                    "transaction event after commit = " + event.marker
            );
        }

        int afterCommitCount() {
            return directAfterCommit.get();
        }

        int eventCount() {
            return transactionEvents.get();
        }
    }

    static class ThreadBoundaryService {

        private final JdbcTemplate jdbc;

        ThreadBoundaryService(JdbcTemplate jdbc) {
            this.jdbc = jdbc;
        }

        @Transactional
        public void showWorkerTransactionState() throws InterruptedException {
            jdbc.queryForObject("select 1", Integer.class);

            System.out.println(
                    "caller thread active = "
                            + TransactionSynchronizationManager
                                    .isActualTransactionActive()
            );

            ExecutorService executor = Executors.newSingleThreadExecutor(
                    runnable -> new Thread(runnable, "tx-worker")
            );
            CountDownLatch done = new CountDownLatch(1);

            executor.execute(() -> {
                System.out.println(
                        "worker thread active = "
                                + TransactionSynchronizationManager
                                        .isActualTransactionActive()
                );
                done.countDown();
            });

            done.await(5, TimeUnit.SECONDS);
            executor.shutdown();
        }
    }

    static class IsolationService {

        private final JdbcTemplate jdbc;

        IsolationService(JdbcTemplate jdbc) {
            this.jdbc = jdbc;
        }

        @Transactional(isolation = Isolation.SERIALIZABLE)
        public void serializableOperation() {
            jdbc.queryForObject("select 1", Integer.class);

            Integer isolation = TransactionSynchronizationManager
                    .getCurrentTransactionIsolationLevel();

            System.out.println("Spring isolation code = " + isolation);
        }
    }

    static class OutboxWriter {

        private final JdbcTemplate jdbc;

        OutboxWriter(JdbcTemplate jdbc) {
            this.jdbc = jdbc;
        }

        @Transactional
        public void createOrder(String orderNo, boolean fail) {
            jdbc.update("insert into orders(order_no) values (?)", orderNo);

            jdbc.update(
                    "insert into outbox_message("
                            + "id, aggregate_id, event_type, payload, status"
                            + ") values (?, ?, ?, ?, ?)",
                    UUID.randomUUID().toString(),
                    orderNo,
                    "OrderCreated",
                    "{\"orderNo\":\"" + orderNo + "\"}",
                    "PENDING"
            );

            if (fail) {
                throw new BusinessFailure("outbox writer failure");
            }
        }
    }

    static class OutboxRelay {

        private final JdbcTemplate jdbc;
        private final TransactionTemplate template;

        OutboxRelay(JdbcTemplate jdbc, TransactionTemplate template) {
            this.jdbc = jdbc;
            this.template = template;
        }

        void relayOnce() {
            List<String> ids = jdbc.query(
                    "select id from outbox_message "
                            + "where status='PENDING' order by id",
                    (resultSet, rowNumber) -> resultSet.getString(1)
            );

            for (String id : ids) {
                System.out.println("publish event id = " + id);

                template.execute(status -> {
                    jdbc.update(
                            "update outbox_message "
                                    + "set status='PUBLISHED' where id=?",
                            id
                    );
                    return null;
                });
            }
        }
    }

    static class CountingTransactionManager
            extends DataSourceTransactionManager {

        private final AtomicInteger beginCount = new AtomicInteger();
        private final AtomicInteger commitCount = new AtomicInteger();
        private final AtomicInteger rollbackCount = new AtomicInteger();

        CountingTransactionManager(DataSource dataSource) {
            super(dataSource);
        }

        @Override
        protected void doBegin(
                Object transaction,
                TransactionDefinition definition
        ) {
            beginCount.incrementAndGet();
            System.out.println(
                    "TX begin propagation="
                            + definition.getPropagationBehavior()
                            + " isolation="
                            + definition.getIsolationLevel()
            );
            super.doBegin(transaction, definition);
        }

        @Override
        protected void doCommit(DefaultTransactionStatus status) {
            commitCount.incrementAndGet();
            System.out.println("TX commit");
            super.doCommit(status);
        }

        @Override
        protected void doRollback(DefaultTransactionStatus status) {
            rollbackCount.incrementAndGet();
            System.out.println("TX rollback");
            super.doRollback(status);
        }

        int begins() {
            return beginCount.get();
        }

        int commits() {
            return commitCount.get();
        }

        int rollbacks() {
            return rollbackCount.get();
        }
    }

    static final class MarkerCommittedEvent {
        final String marker;

        MarkerCommittedEvent(String marker) {
            this.marker = marker;
        }
    }

    static final class PaymentRejected extends RuntimeException {
        PaymentRejected(String message) {
            super(message);
        }
    }

    static final class InvalidRow extends RuntimeException {
        InvalidRow(String value) {
            super("invalid row " + value);
        }
    }

    static final class BusinessFailure extends RuntimeException {
        BusinessFailure(String message) {
            super(message);
        }
    }
}
