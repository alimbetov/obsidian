package lab;

import org.hibernate.LazyInitializationException;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.LockModeType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.RollbackException;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class DataJpaLab {

    private DataJpaLab() {
    }

    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(JpaLabConfiguration.class)) {

            SeedService seedService = context.getBean(SeedService.class);
            JpaExperiments experiments = context.getBean(JpaExperiments.class);
            AuthorRepository repository = context.getBean(AuthorRepository.class);
            EntityManagerFactory emf = context.getBean(EntityManagerFactory.class);

            seedService.resetAndSeed();
            List<Long> ids = repository.findAll(Sort.by("id")).stream()
                    .map(Author::getId)
                    .collect(java.util.stream.Collectors.toList());
            Long firstId = ids.get(0);
            Long secondId = ids.get(1);

            System.out.println("=== REPOSITORY PROXY ===");
            System.out.println("class       = " + repository.getClass().getName());
            System.out.println("is AOP proxy = " + AopUtils.isAopProxy(repository));

            experiments.identityMap(firstId);
            experiments.dirtyChecking(firstId);
            experiments.detachAndMerge(firstId);
            experiments.repositorySaveDetached(firstId);
            lazyInitializationOutsideTransaction(repository, secondId);
            flushTimeConstraint(experiments, firstId);
            experiments.nPlusOne();
            experiments.fetchJoin();
            experiments.entityGraph();
            experiments.projection();
            experiments.dynamicSpecification();
            experiments.pageVersusSlice();
            experiments.pessimisticLock(firstId);
            experiments.bulkDmlLeavesManagedStateStale(secondId);
            optimisticConflict(emf, firstId);

            System.out.println("\n=== FINAL DATABASE STATE ===");
            repository.findAll(Sort.by("id")).forEach(author ->
                    System.out.println(
                            "Author{id=" + author.getId()
                                    + ", version=" + author.getVersion()
                                    + ", name='" + author.getName() + "'}"
                    )
            );
        }
    }

    private static void lazyInitializationOutsideTransaction(
            AuthorRepository repository,
            Long authorId
    ) {
        System.out.println("\n=== LAZY INITIALIZATION OUTSIDE TRANSACTION ===");
        Author detached = repository.findById(authorId)
                .orElseThrow(() -> new IllegalStateException("author not found"));

        try {
            System.out.println("book count = " + detached.getBooks().size());
        } catch (LazyInitializationException error) {
            System.out.println("expected = " + error.getClass().getSimpleName());
            System.out.println("reason   = detached entity has no open persistence context");
        }
    }

    private static void flushTimeConstraint(
            JpaExperiments experiments,
            Long authorId
    ) {
        System.out.println("\n=== FLUSH-TIME CONSTRAINT FAILURE ===");
        try {
            experiments.persistInvalidBookAndFlush(authorId);
        } catch (RuntimeException error) {
            Throwable root = rootCause(error);
            System.out.println("failure surfaced at flush = " + root.getClass().getSimpleName());
        }
    }

    private static void optimisticConflict(
            EntityManagerFactory emf,
            Long authorId
    ) {
        System.out.println("\n=== OPTIMISTIC LOCK CONFLICT ===");

        EntityManager first = emf.createEntityManager();
        EntityManager second = emf.createEntityManager();

        try {
            first.getTransaction().begin();
            Author firstCopy = first.find(Author.class, authorId);

            second.getTransaction().begin();
            Author secondCopy = second.find(Author.class, authorId);
            secondCopy.setName(secondCopy.getName() + "-TX2");
            second.getTransaction().commit();
            System.out.println("TX2 committed version = " + secondCopy.getVersion());

            firstCopy.setName(firstCopy.getName() + "-TX1");
            try {
                first.getTransaction().commit();
                System.out.println("unexpected: TX1 committed");
            } catch (RollbackException error) {
                System.out.println("TX1 conflict = " + rootCause(error).getClass().getSimpleName());
            }
        } finally {
            if (first.getTransaction().isActive()) {
                first.getTransaction().rollback();
            }
            if (second.getTransaction().isActive()) {
                second.getTransaction().rollback();
            }
            first.close();
            second.close();
        }
    }

    private static Throwable rootCause(Throwable error) {
        Throwable current = error;
        while (current.getCause() != null && current.getCause() != current) {
            current = current.getCause();
        }
        return current;
    }

    @Configuration
    @EnableTransactionManagement(proxyTargetClass = true)
    @EnableJpaRepositories(
            basePackageClasses = DataJpaLab.class,
            considerNestedRepositories = true
    )
    static class JpaLabConfiguration {

        @Bean
        DataSource dataSource() {
            DriverManagerDataSource dataSource = new DriverManagerDataSource();
            dataSource.setDriverClassName("org.h2.Driver");
            dataSource.setUrl(
                    "jdbc:h2:mem:datajpalab;"
                            + "DB_CLOSE_DELAY=-1;"
                            + "MODE=PostgreSQL;"
                            + "DATABASE_TO_LOWER=TRUE"
            );
            dataSource.setUsername("sa");
            dataSource.setPassword("");
            return dataSource;
        }

        @Bean
        LocalContainerEntityManagerFactoryBean entityManagerFactory(
                DataSource dataSource
        ) {
            HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
            adapter.setGenerateDdl(true);
            adapter.setShowSql(false);

            LocalContainerEntityManagerFactoryBean factory =
                    new LocalContainerEntityManagerFactoryBean();
            factory.setDataSource(dataSource);
            factory.setJpaVendorAdapter(adapter);
            factory.setAnnotatedClasses(Author.class, Book.class);
            factory.setJpaPropertyMap(hibernateProperties());
            return factory;
        }

        private Map<String, Object> hibernateProperties() {
            Map<String, Object> properties = new HashMap<String, Object>();
            properties.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
            properties.put("hibernate.hbm2ddl.auto", "create-drop");
            properties.put("hibernate.generate_statistics", "true");
            properties.put("hibernate.format_sql", "true");
            properties.put("hibernate.use_sql_comments", "true");
            properties.put("hibernate.jdbc.batch_size", "20");
            properties.put("hibernate.order_inserts", "true");
            properties.put("hibernate.order_updates", "true");
            return properties;
        }

        @Bean
        PlatformTransactionManager transactionManager(
                EntityManagerFactory entityManagerFactory
        ) {
            return new JpaTransactionManager(entityManagerFactory);
        }

        @Bean
        SeedService seedService(AuthorRepository repository) {
            return new SeedService(repository);
        }

        @Bean
        JpaExperiments jpaExperiments(
                AuthorRepository repository,
                EntityManager entityManager,
                EntityManagerFactory entityManagerFactory
        ) {
            return new JpaExperiments(repository, entityManager, entityManagerFactory);
        }
    }

    public interface AuthorSummary {
        Long getId();

        String getName();
    }

    public interface AuthorRepository
            extends JpaRepository<Author, Long>, JpaSpecificationExecutor<Author> {

        @Query("select distinct a from Author a left join fetch a.books order by a.id")
        List<Author> findAllWithBooks();

        @EntityGraph(attributePaths = "books")
        List<Author> findAllByOrderByIdAsc();

        List<AuthorSummary> findByNameContainingIgnoreCaseOrderByIdAsc(String name);

        Page<Author> findPageByNameContainingIgnoreCase(
                String name,
                Pageable pageable
        );

        Slice<Author> findSliceByNameContainingIgnoreCase(
                String name,
                Pageable pageable
        );

        @Modifying(flushAutomatically = true, clearAutomatically = false)
        @Query("update Author a set a.name = :name where a.id = :id")
        int renameWithoutClearing(Long id, String name);

        @Lock(LockModeType.PESSIMISTIC_WRITE)
        @Query("select a from Author a where a.id = :id")
        Optional<Author> findLockedById(Long id);
    }

    static class SeedService {

        private final AuthorRepository repository;

        SeedService(AuthorRepository repository) {
            this.repository = repository;
        }

        @Transactional
        public void resetAndSeed() {
            repository.deleteAllInBatch();

            Author alice = new Author("Alice");
            alice.addBook(new Book("JPA Identity Map"));
            alice.addBook(new Book("Dirty Checking"));

            Author bob = new Author("Bob");
            bob.addBook(new Book("Spring Data Repositories"));
            bob.addBook(new Book("Dynamic Specifications"));

            Author carol = new Author("Carol");
            carol.addBook(new Book("N plus One"));
            carol.addBook(new Book("Entity Graphs"));

            repository.saveAll(Arrays.asList(alice, bob, carol));
            repository.flush();
        }
    }

    static class JpaExperiments {

        private final AuthorRepository repository;
        private final EntityManager entityManager;
        private final Statistics statistics;

        JpaExperiments(
                AuthorRepository repository,
                EntityManager entityManager,
                EntityManagerFactory entityManagerFactory
        ) {
            this.repository = repository;
            this.entityManager = entityManager;
            this.statistics = entityManagerFactory
                    .unwrap(SessionFactory.class)
                    .getStatistics();
            this.statistics.setStatisticsEnabled(true);
        }

        @Transactional(readOnly = true)
        public void identityMap(Long authorId) {
            System.out.println("\n=== PERSISTENCE CONTEXT IDENTITY MAP ===");
            entityManager.clear();
            statistics.clear();

            Author first = entityManager.find(Author.class, authorId);
            Author second = entityManager.find(Author.class, authorId);

            System.out.println("same Java instance = " + (first == second));
            System.out.println("managed            = " + entityManager.contains(first));
            System.out.println("SQL statements     = " + statistics.getPrepareStatementCount());
        }

        @Transactional
        public void dirtyChecking(Long authorId) {
            System.out.println("\n=== DIRTY CHECKING WITHOUT SAVE ===");
            entityManager.clear();

            Author managed = entityManager.find(Author.class, authorId);
            String newName = managed.getName() + "-DIRTY";
            managed.setName(newName);

            System.out.println("repository.save called = false");
            entityManager.flush();
            entityManager.clear();

            Author reloaded = entityManager.find(Author.class, authorId);
            System.out.println("database value = " + reloaded.getName());
        }

        @Transactional
        public void detachAndMerge(Long authorId) {
            System.out.println("\n=== DETACH AND MERGE ===");
            entityManager.clear();

            Author original = entityManager.find(Author.class, authorId);
            entityManager.detach(original);
            original.setName(original.getName() + "-DETACHED");
            entityManager.flush();

            String beforeMerge = entityManager.createQuery(
                            "select a.name from Author a where a.id = :id",
                            String.class
                    )
                    .setParameter("id", authorId)
                    .getSingleResult();

            Author managedCopy = entityManager.merge(original);
            entityManager.flush();

            System.out.println("original managed      = " + entityManager.contains(original));
            System.out.println("merge result managed  = " + entityManager.contains(managedCopy));
            System.out.println("same object           = " + (original == managedCopy));
            System.out.println("DB before merge       = " + beforeMerge);
            System.out.println("DB after merge        = " + managedCopy.getName());
        }

        @Transactional
        public void repositorySaveDetached(Long authorId) {
            System.out.println("\n=== REPOSITORY SAVE OF DETACHED ENTITY ===");
            entityManager.clear();

            Author detached = entityManager.find(Author.class, authorId);
            entityManager.detach(detached);
            detached.setName(detached.getName() + "-SAVE");

            Author returned = repository.save(detached);
            repository.flush();

            System.out.println("returned == argument = " + (returned == detached));
            System.out.println("argument managed     = " + entityManager.contains(detached));
            System.out.println("returned managed     = " + entityManager.contains(returned));
        }

        @Transactional
        public void persistInvalidBookAndFlush(Long authorId) {
            Author author = entityManager.find(Author.class, authorId);
            author.addBook(new Book(null));
            System.out.println("invalid child added in memory");
            entityManager.flush();
        }

        @Transactional(readOnly = true)
        public void nPlusOne() {
            System.out.println("\n=== N+1 QUERY PROBLEM ===");
            entityManager.clear();
            statistics.clear();

            List<Author> authors = repository.findAll(Sort.by("id"));
            int books = 0;
            for (Author author : authors) {
                books += author.getBooks().size();
            }

            System.out.println("authors = " + authors.size());
            System.out.println("books   = " + books);
            System.out.println("SQL statements = " + statistics.getPrepareStatementCount());
        }

        @Transactional(readOnly = true)
        public void fetchJoin() {
            System.out.println("\n=== FETCH JOIN ===");
            entityManager.clear();
            statistics.clear();

            List<Author> authors = repository.findAllWithBooks();
            int books = authors.stream()
                    .mapToInt(author -> author.getBooks().size())
                    .sum();

            System.out.println("authors = " + authors.size());
            System.out.println("books   = " + books);
            System.out.println("SQL statements = " + statistics.getPrepareStatementCount());
        }

        @Transactional(readOnly = true)
        public void entityGraph() {
            System.out.println("\n=== ENTITY GRAPH ===");
            entityManager.clear();
            statistics.clear();

            List<Author> authors = repository.findAllByOrderByIdAsc();
            int books = authors.stream()
                    .mapToInt(author -> author.getBooks().size())
                    .sum();

            System.out.println("authors = " + authors.size());
            System.out.println("books   = " + books);
            System.out.println("SQL statements = " + statistics.getPrepareStatementCount());
        }

        @Transactional(readOnly = true)
        public void projection() {
            System.out.println("\n=== INTERFACE PROJECTION ===");
            List<AuthorSummary> rows =
                    repository.findByNameContainingIgnoreCaseOrderByIdAsc("a");

            for (AuthorSummary row : rows) {
                System.out.println(
                        "projection class=" + row.getClass().getName()
                                + ", id=" + row.getId()
                                + ", name=" + row.getName()
                );
            }
        }

        @Transactional(readOnly = true)
        public void dynamicSpecification() {
            System.out.println("\n=== DYNAMIC SPECIFICATION ===");

            String optionalName = "a";
            Long optionalMinId = 1L;

            Specification<Author> specification = Specification.where(null);

            if (optionalName != null) {
                specification = specification.and((root, query, builder) ->
                        builder.like(
                                builder.lower(root.get("name")),
                                "%" + optionalName.toLowerCase() + "%"
                        )
                );
            }

            if (optionalMinId != null) {
                specification = specification.and((root, query, builder) ->
                        builder.greaterThanOrEqualTo(root.get("id"), optionalMinId)
                );
            }

            List<Author> result = repository.findAll(
                    specification,
                    Sort.by("id").ascending()
            );

            System.out.println(
                    "matched = " + result.stream()
                            .map(Author::getName)
                            .collect(java.util.stream.Collectors.toList())
            );
        }

        @Transactional(readOnly = true)
        public void pageVersusSlice() {
            System.out.println("\n=== PAGE VS SLICE ===");
            Pageable pageable = PageRequest.of(0, 2, Sort.by("id"));

            entityManager.clear();
            statistics.clear();
            Page<Author> page = repository.findPageByNameContainingIgnoreCase(
                    "a",
                    pageable
            );
            long pageStatements = statistics.getPrepareStatementCount();

            entityManager.clear();
            statistics.clear();
            Slice<Author> slice = repository.findSliceByNameContainingIgnoreCase(
                    "a",
                    pageable
            );
            long sliceStatements = statistics.getPrepareStatementCount();

            System.out.println("page content       = " + page.getNumberOfElements());
            System.out.println("page total         = " + page.getTotalElements());
            System.out.println("page SQL statements = " + pageStatements);
            System.out.println("slice content      = " + slice.getNumberOfElements());
            System.out.println("slice has next     = " + slice.hasNext());
            System.out.println("slice SQL statements = " + sliceStatements);
        }

        @Transactional
        public void pessimisticLock(Long authorId) {
            System.out.println("\n=== PESSIMISTIC WRITE LOCK ===");
            Author author = repository.findLockedById(authorId)
                    .orElseThrow(() -> new IllegalStateException("author not found"));
            System.out.println("lock mode = " + entityManager.getLockMode(author));
        }

        @Transactional
        public void bulkDmlLeavesManagedStateStale(Long authorId) {
            System.out.println("\n=== BULK DML AND STALE PERSISTENCE CONTEXT ===");
            entityManager.clear();

            Author managed = entityManager.find(Author.class, authorId);
            String oldName = managed.getName();
            String newName = "BULK-RENAMED-" + authorId;

            int updated = repository.renameWithoutClearing(authorId, newName);
            String databaseName = String.valueOf(
                    entityManager.createNativeQuery(
                                    "select name from authors where id = :id"
                            )
                            .setParameter("id", authorId)
                            .getSingleResult()
            );

            System.out.println("updated rows       = " + updated);
            System.out.println("managed object     = " + managed.getName());
            System.out.println("database row       = " + databaseName);
            System.out.println("managed is stale   = " + oldName.equals(managed.getName()));

            entityManager.clear();
            Author reloaded = entityManager.find(Author.class, authorId);
            System.out.println("after clear/reload = " + reloaded.getName());
        }
    }

    @Entity(name = "Author")
    @Table(name = "authors")
    public static class Author {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Version
        private long version;

        @Column(nullable = false, unique = true, length = 200)
        private String name;

        @OneToMany(
                mappedBy = "author",
                cascade = CascadeType.ALL,
                orphanRemoval = true,
                fetch = FetchType.LAZY
        )
        private List<Book> books = new ArrayList<Book>();

        protected Author() {
        }

        Author(String name) {
            this.name = name;
        }

        public Long getId() {
            return id;
        }

        public long getVersion() {
            return version;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<Book> getBooks() {
            return books;
        }

        public void addBook(Book book) {
            books.add(book);
            book.author = this;
        }
    }

    @Entity(name = "Book")
    @Table(name = "books")
    public static class Book {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false, length = 300)
        private String title;

        @ManyToOne(fetch = FetchType.LAZY, optional = false)
        @JoinColumn(name = "author_id", nullable = false)
        private Author author;

        protected Book() {
        }

        Book(String title) {
            this.title = title;
        }

        public Long getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public Author getAuthor() {
            return author;
        }
    }
}
