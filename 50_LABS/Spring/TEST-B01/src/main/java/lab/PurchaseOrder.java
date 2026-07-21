package lab;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "purchase_orders")
public class PurchaseOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private long version;

    @Column(name = "order_number", nullable = false, unique = true, length = 100)
    private String orderNumber;

    @Column(nullable = false, length = 30)
    private String status;

    @OneToMany(
            mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<PurchaseOrderLine> lines = new ArrayList<PurchaseOrderLine>();

    protected PurchaseOrder() {
    }

    public PurchaseOrder(String orderNumber, String status) {
        this.orderNumber = orderNumber;
        this.status = status;
    }

    public void addLine(String sku, int quantity) {
        PurchaseOrderLine line = new PurchaseOrderLine(sku, quantity);
        line.attachTo(this);
        lines.add(line);
    }

    public Long getId() {
        return id;
    }

    public long getVersion() {
        return version;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<PurchaseOrderLine> getLines() {
        return Collections.unmodifiableList(lines);
    }
}
