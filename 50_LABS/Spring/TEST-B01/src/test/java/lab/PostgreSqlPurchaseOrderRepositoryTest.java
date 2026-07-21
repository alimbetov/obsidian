package lab;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Testcontainers
@DataJpaTest(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=false"
})
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
class PostgreSqlPurchaseOrderRepositoryTest {

    @Container
    static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>(
                    DockerImageName.parse("postgres:15-alpine")
            )
                    .withDatabaseName("testing_lab")
                    .withUsername("testing_lab")
                    .withPassword("testing_lab");

    @DynamicPropertySource
    static void registerDatabaseProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.datasource.driver-class-name", POSTGRES::getDriverClassName);
        registry.add("spring.jpa.database-platform", () -> "org.hibernate.dialect.PostgreSQL10Dialect");
    }

    @Autowired
    private PurchaseOrderRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void nativeIlikeQueryUsesRealPostgreSqlDialect() {
        repository.save(new PurchaseOrder("Order-AbC-100", "NEW"));
        repository.save(new PurchaseOrder("ORDER-xyz-200", "NEW"));
        repository.flush();
        entityManager.clear();

        List<PurchaseOrder> result =
                repository.searchOrderNumberIgnoreCase("abc");

        assertThat(result)
                .extracting(PurchaseOrder::getOrderNumber)
                .containsExactly("Order-AbC-100");
    }

    @Test
    void uniqueConstraintIsEnforcedByPostgreSql() {
        repository.save(new PurchaseOrder("ORD-PG-DUP", "NEW"));
        repository.flush();
        entityManager.clear();

        repository.save(new PurchaseOrder("ORD-PG-DUP", "NEW"));

        assertThatThrownBy(repository::flush)
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
