package lab;

import org.aopalliance.intercept.MethodInterceptor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.aop.Advisor;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.framework.AopContext;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.SimpleTransactionStatus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public final class AopAndProxyLab {

    private AopAndProxyLab() {
    }

    public static void main(String[] args) throws Exception {
        runProgrammaticProxySelection();
        runSpringAopContext();
    }

    private static void runProgrammaticProxySelection() {
        System.out.println("\n=== PROGRAMMATIC JDK VS CGLIB ===");

        PaymentPort target = new PaymentTarget();

        ProxyFactory jdkFactory = new ProxyFactory(target);
        jdkFactory.addAdvice((MethodInterceptor) invocation -> {
            System.out.println("JDK advice before " + invocation.getMethod().getName());
            try {
                return invocation.proceed();
            } finally {
                System.out.println("JDK advice after " + invocation.getMethod().getName());
            }
        });

        PaymentPort jdkProxy = (PaymentPort) jdkFactory.getProxy();
        System.out.println("jdk proxy class = " + jdkProxy.getClass().getName());
        System.out.println("is JDK proxy = " + AopUtils.isJdkDynamicProxy(jdkProxy));
        jdkProxy.pay("P-JDK");

        ProxyFactory cglibFactory = new ProxyFactory(target);
        cglibFactory.setProxyTargetClass(true);
        cglibFactory.addAdvice((MethodInterceptor) invocation -> {
            System.out.println("CGLIB advice before " + invocation.getMethod().getName());
            try {
                return invocation.proceed();
            } finally {
                System.out.println("CGLIB advice after " + invocation.getMethod().getName());
            }
        });

        PaymentTarget cglibProxy = (PaymentTarget) cglibFactory.getProxy();
        System.out.println("cglib proxy class = " + cglibProxy.getClass().getName());
        System.out.println("is CGLIB proxy = " + AopUtils.isCglibProxy(cglibProxy));
        cglibProxy.pay("P-CGLIB");
    }

    private static void runSpringAopContext() throws Exception {
        System.out.println("\n=== SPRING AOP CONTEXT ===");

        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(AopLabConfiguration.class)) {

            ObservedService observed = context.getBean(ObservedService.class);
            inspectProxy("ObservedService", observed);

            System.out.println("\n-- external advised call --");
            observed.observed("external");

            System.out.println("\n-- self invocation: internal advice is skipped --");
            observed.outerCallsObserved();

            System.out.println("\n-- AopContext re-entry: advice runs, but coupling is explicit --");
            observed.outerCallsThroughCurrentProxy();

            System.out.println("\n-- final method: CGLIB cannot advise it --");
            observed.finalObserved();

            System.out.println("\n-- private method: call remains inside target --");
            observed.callPrivate();

            LoggingTransactionManager txManager =
                    context.getBean(LoggingTransactionManager.class);
            TransactionalPaymentService payment =
                    context.getBean(TransactionalPaymentService.class);
            PaymentBatchFacade batch = context.getBean(PaymentBatchFacade.class);

            inspectProxy("TransactionalPaymentService", payment);

            System.out.println("\n-- external @Transactional call --");
            payment.processOne("TX-EXTERNAL");

            System.out.println("\n-- self-invoked @Transactional call: no new transaction --");
            int beforeSelf = txManager.begunCount();
            payment.selfInvoke("TX-SELF");
            System.out.println("transactions started by self invocation = " +
                    (txManager.begunCount() - beforeSelf));

            System.out.println("\n-- collaborator calls cross proxy for each item --");
            int beforeBatch = txManager.begunCount();
            batch.process(Arrays.asList("TX-1", "TX-2", "TX-3"));
            System.out.println("transactions started by batch = " +
                    (txManager.begunCount() - beforeBatch));

            AsyncNotificationService notification =
                    context.getBean(AsyncNotificationService.class);
            inspectProxy("AsyncNotificationService", notification);

            System.out.println("\n-- external @Async call --");
            String externalThread = notification.sendAsync("ASYNC-EXTERNAL")
                    .get(5, TimeUnit.SECONDS);
            System.out.println("external async executed on = " + externalThread);

            System.out.println("\n-- self-invoked @Async call --");
            String selfThread = notification.selfInvoke("ASYNC-SELF")
                    .get(5, TimeUnit.SECONDS);
            System.out.println("self-invoked async executed on = " + selfThread);
        }
    }

    private static void inspectProxy(String name, Object bean) {
        System.out.println("\n" + name + " runtime class = " + bean.getClass().getName());
        System.out.println("is AOP proxy = " + AopUtils.isAopProxy(bean));
        System.out.println("is JDK proxy = " + AopUtils.isJdkDynamicProxy(bean));
        System.out.println("is CGLIB proxy = " + AopUtils.isCglibProxy(bean));
        System.out.println("target class = " + AopUtils.getTargetClass(bean).getName());

        if (bean instanceof Advised) {
            Advisor[] advisors = ((Advised) bean).getAdvisors();
            for (int i = 0; i < advisors.length; i++) {
                System.out.println("advisor[" + i + "] = " + advisors[i]);
            }
        }
    }
}

interface PaymentPort {
    String pay(String paymentId);
}

class PaymentTarget implements PaymentPort {
    @Override
    public String pay(String paymentId) {
        System.out.println("target processes " + paymentId);
        return "OK:" + paymentId;
    }
}

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@interface Observed {
}

@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@EnableTransactionManagement(proxyTargetClass = true)
@EnableAsync(proxyTargetClass = true)
class AopLabConfiguration {

    @Bean
    static TracingAspect tracingAspect() {
        return new TracingAspect();
    }

    @Bean
    static AuditAspect auditAspect() {
        return new AuditAspect();
    }

    @Bean
    ObservedService observedService() {
        return new ObservedService();
    }

    @Bean
    LoggingTransactionManager transactionManager() {
        return new LoggingTransactionManager();
    }

    @Bean
    TransactionalPaymentService transactionalPaymentService() {
        return new TransactionalPaymentService();
    }

    @Bean
    PaymentBatchFacade paymentBatchFacade(
            TransactionalPaymentService transactionalPaymentService
    ) {
        return new PaymentBatchFacade(transactionalPaymentService);
    }

    @Bean
    AsyncNotificationService asyncNotificationService() {
        return new AsyncNotificationService();
    }

    @Bean(name = "notificationExecutor")
    Executor notificationExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix("notification-");
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(2);
        executor.setQueueCapacity(10);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(5);
        executor.initialize();
        return executor;
    }
}

@Aspect
@Order(10)
class TracingAspect {

    @Around("@annotation(lab.Observed)")
    public Object trace(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("TRACE enter " + joinPoint.getSignature().toShortString());
        try {
            return joinPoint.proceed();
        } finally {
            System.out.println("TRACE exit  " + joinPoint.getSignature().toShortString());
        }
    }
}

@Aspect
@Order(20)
class AuditAspect {

    @Around("@annotation(lab.Observed)")
    public Object audit(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("AUDIT enter " + joinPoint.getSignature().toShortString());
        try {
            Object result = joinPoint.proceed();
            System.out.println("AUDIT success");
            return result;
        } catch (Throwable error) {
            System.out.println("AUDIT failure " + error.getClass().getSimpleName());
            throw error;
        } finally {
            System.out.println("AUDIT exit");
        }
    }
}

class ObservedService {

    @Observed
    public String observed(String source) {
        System.out.println("target observed from " + source);
        return source;
    }

    public void outerCallsObserved() {
        observed("self");
    }

    public void outerCallsThroughCurrentProxy() {
        ((ObservedService) AopContext.currentProxy()).observed("AopContext");
    }

    @Observed
    public final void finalObserved() {
        System.out.println("target final method");
    }

    public void callPrivate() {
        privateObserved();
    }

    @Observed
    private void privateObserved() {
        System.out.println("target private method");
    }
}

class LoggingTransactionManager implements PlatformTransactionManager, Ordered {
    private final AtomicInteger begun = new AtomicInteger();

    @Override
    public TransactionStatus getTransaction(TransactionDefinition definition) {
        int id = begun.incrementAndGet();
        System.out.println("TX begin #" + id +
                " propagation=" + definition.getPropagationBehavior());
        return new LoggingStatus(id);
    }

    @Override
    public void commit(TransactionStatus status) {
        LoggingStatus loggingStatus = (LoggingStatus) status;
        System.out.println("TX commit #" + loggingStatus.id);
    }

    @Override
    public void rollback(TransactionStatus status) {
        LoggingStatus loggingStatus = (LoggingStatus) status;
        System.out.println("TX rollback #" + loggingStatus.id);
    }

    int begunCount() {
        return begun.get();
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    private static final class LoggingStatus extends SimpleTransactionStatus {
        private final int id;

        private LoggingStatus(int id) {
            this.id = id;
        }
    }
}

class TransactionalPaymentService {

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processOne(String paymentId) {
        System.out.println("target saves " + paymentId);
    }

    public void selfInvoke(String paymentId) {
        processOne(paymentId);
    }
}

class PaymentBatchFacade {
    private final TransactionalPaymentService paymentService;

    PaymentBatchFacade(TransactionalPaymentService paymentService) {
        this.paymentService = paymentService;
    }

    void process(Iterable<String> paymentIds) {
        for (String paymentId : paymentIds) {
            paymentService.processOne(paymentId);
        }
    }
}

class AsyncNotificationService {

    @Async("notificationExecutor")
    public CompletableFuture<String> sendAsync(String notificationId) {
        String thread = Thread.currentThread().getName();
        System.out.println("send " + notificationId + " on " + thread);
        return CompletableFuture.completedFuture(thread);
    }

    public CompletableFuture<String> selfInvoke(String notificationId) {
        return sendAsync(notificationId);
    }
}
