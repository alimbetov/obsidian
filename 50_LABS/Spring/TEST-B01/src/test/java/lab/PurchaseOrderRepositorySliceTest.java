package lab;

import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import javax.persistence.EntityManagerFactory;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=false",
        "spring.jpa.properties.hibernate.generate_statistics=true"
})
class PurchaseOrderRepositorySliceTest {

    @Autowired
    private PurchaseOrderRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void sliceLoadsRepositoryButNotRegularService() {
        assertThat(applicationContext.getBeansOfType(PurchaseOrderRepository.class))
                .isNotEmpty();
        assertThat(applicationContext.getBeansOfType(PurchaseOrderService.class))
                .isEmpty();
    }

    @Test
    void flushAndClearProveDatabaseRoundTrip() {
        PurchaseOrder order = newOrder("ORD-ROUND-TRIP", "NEW");
        PurchaseOrder saved = repository.save(order);

        repository.flush();
        entityManager.clear();

        PurchaseOrder reloaded = repository.findById(saved.getId())
                .orElseThrow(() -> new AssertionError("order not found"));

        assertThat(reloaded).isNotSameAs(saved);
        assertThat(reloaded.getOrderNumber()).isEqualTo("ORD-ROUND-TRIP");
        assertThat(reloaded.getLines()).hasSize(2);
    }

    @Test
    void duplicateBusinessKeyFailsWhenSqlIsFlushed() {
        repository.save(new PurchaseOrder("ORD-DUPLICATE", "NEW"));
        repository.flush();
        entityManager.clear();

        repository.save(new PurchaseOrder("ORD-DUPLICATE", "NEW"));

        assertThatThrownBy(repository::flush)
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void dirtyCheckingPersistsManagedChangesWithoutRepositorySave() {
        PurchaseOrder saved = repository.saveAndFlush(
                new PurchaseOrder("ORD-DIRTY", "NEW")
        );
        entityManager.clear();

        PurchaseOrder managed = repository.findById(saved.getId())
                .orElseThrow(() -> new AssertionError("order not found"));
        managed.setStatus("PAID");

        entityManager.flush();
        entityManager.clear();

        PurchaseOrder reloaded = repository.findById(saved.getId())
                .orElseThrow(() -> new AssertionError("order not found"));
        assertThat(reloaded.getStatus()).isEqualTo("PAID");
    }

    @Test
    void entityGraphPreventsNPlusOneRegression() {
        repository.saveAll(Arrays.asList(
                newOrder("ORD-N1-1", "NEW"),
                newOrder("ORD-N1-2", "NEW"),
                newOrder("ORD-N1-3", "NEW")
        ));
        repository.flush();
        entityManager.clear();

        Statistics statistics = statistics();
        statistics.clear();

        List<PurchaseOrder> lazyOrders =
                repository.findByStatusOrderByIdAsc("NEW");
        lazyOrders.forEach(order -> order.getLines().size());
        long lazyStatements = statistics.getPrepareStatementCount();

        entityManager.clear();
        statistics.clear();

        List<PurchaseOrder> fetchedOrders =
                repository.findAllByStatusOrderByIdAsc("NEW");
        fetchedOrders.forEach(order -> order.getLines().size());
        long graphStatements = statistics.getPrepareStatementCount();

        assertThat(lazyStatements).isEqualTo(4L);
        assertThat(graphStatements).isEqualTo(1L);
    }

    @Test
    void pageExecutesContentAndCountQueries() {
        repository.saveAll(Arrays.asList(
                new PurchaseOrder("ORD-PAGE-1", "NEW"),
                new PurchaseOrder("ORD-PAGE-2", "NEW"),
                new PurchaseOrder("ORD-PAGE-3", "NEW")
        ));
        repository.flush();
        entityManager.clear();

        Statistics statistics = statistics();
        statistics.clear();

        Page<PurchaseOrder> page = repository.findPageByStatus(
                "NEW",
                PageRequest.of(0, 2)
        );

        assertThat(page.getContent()).hasSize(2);
        assertThat(page.getTotalElements()).isEqualTo(3L);
        assertThat(statistics.getPrepareStatementCount()).isEqualTo(2L);
    }

    private Statistics statistics() {
        Statistics statistics = entityManagerFactory
                .unwrap(SessionFactory.class)
                .getStatistics();
        statistics.setStatisticsEnabled(true);
        return statistics;
    }

    private static PurchaseOrder newOrder(String number, String status) {
        PurchaseOrder order = new PurchaseOrder(number, status);
        order.addLine("SKU-A", 1);
        order.addLine("SKU-B", 2);
        return order;
    }
}
