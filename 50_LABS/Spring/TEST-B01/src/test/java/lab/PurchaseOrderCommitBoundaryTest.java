package lab;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=false"
})
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class PurchaseOrderCommitBoundaryTest {

    @Autowired
    private PurchaseOrderRepository repository;

    @Test
    void explicitCommitCanBeVerifiedOutsideTestTransaction() {
        repository.save(new PurchaseOrder("ORD-EXPLICIT-COMMIT", "NEW"));

        assertThat(TestTransaction.isActive()).isTrue();
        TestTransaction.flagForCommit();
        TestTransaction.end();

        assertThat(TestTransaction.isActive()).isFalse();
        assertThat(repository.findByOrderNumber("ORD-EXPLICIT-COMMIT"))
                .isPresent();

        TestTransaction.start();
    }

    @Test
    void defaultTestTransactionCanBeExplicitlyFlaggedForRollback() {
        repository.save(new PurchaseOrder("ORD-EXPLICIT-ROLLBACK", "NEW"));

        TestTransaction.flagForRollback();
        TestTransaction.end();

        assertThat(repository.findByOrderNumber("ORD-EXPLICIT-ROLLBACK"))
                .isEmpty();

        TestTransaction.start();
    }
}
