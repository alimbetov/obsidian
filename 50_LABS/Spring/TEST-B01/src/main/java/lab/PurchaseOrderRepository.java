package lab;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {

    Optional<PurchaseOrder> findByOrderNumber(String orderNumber);

    List<PurchaseOrder> findByStatusOrderByIdAsc(String status);

    @EntityGraph(attributePaths = "lines")
    List<PurchaseOrder> findAllByStatusOrderByIdAsc(String status);

    Page<PurchaseOrder> findPageByStatus(String status, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from PurchaseOrder p where p.id = :id")
    Optional<PurchaseOrder> findLockedById(@Param("id") Long id);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("update PurchaseOrder p set p.status = :status where p.id = :id")
    int updateStatus(
            @Param("id") Long id,
            @Param("status") String status
    );

    @Query(
            value = "select * from purchase_orders "
                    + "where order_number ilike concat('%', :fragment, '%') "
                    + "order by id",
            nativeQuery = true
    )
    List<PurchaseOrder> searchOrderNumberIgnoreCase(
            @Param("fragment") String fragment
    );
}
