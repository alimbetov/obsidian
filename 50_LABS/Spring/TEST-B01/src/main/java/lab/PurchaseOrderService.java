package lab;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PurchaseOrderService {

    private final PurchaseOrderRepository repository;

    public PurchaseOrderService(PurchaseOrderRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public PurchaseOrder create(String orderNumber) {
        PurchaseOrder order = new PurchaseOrder(orderNumber, "NEW");
        order.addLine("SKU-1", 1);
        order.addLine("SKU-2", 2);
        return repository.save(order);
    }

    @Transactional
    public void createAndFail(String orderNumber) {
        create(orderNumber);
        throw new IllegalStateException("simulated failure");
    }

    @Transactional
    public void changeStatus(Long id, String status) {
        PurchaseOrder order = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("order not found"));
        order.setStatus(status);
    }
}
