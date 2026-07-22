package lab;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=false"
})
class PurchaseOrderServiceTransactionTest {

    @Autowired
    private PurchaseOrderService service;

    @Autowired
    private PurchaseOrderRepository repository;

    @BeforeEach
    void cleanDatabase() {
        repository.deleteAll();
        repository.flush();
    }

    @Test
    void serviceIsWrappedByTransactionProxy() {
        assertThat(AopUtils.isAopProxy(service)).isTrue();
    }

    @Test
    void successfulServiceCallCommits() {
        service.create("ORD-SERVICE-COMMIT");

        assertThat(repository.findByOrderNumber("ORD-SERVICE-COMMIT"))
                .isPresent();
    }

    @Test
    void failingServiceCallRollsBackWithoutTestTransaction() {
        assertThatThrownBy(() -> service.createAndFail("ORD-SERVICE-ROLLBACK"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("simulated failure");

        assertThat(repository.findByOrderNumber("ORD-SERVICE-ROLLBACK"))
                .isEmpty();
    }
}
