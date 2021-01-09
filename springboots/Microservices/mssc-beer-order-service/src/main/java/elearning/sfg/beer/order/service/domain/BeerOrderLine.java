package elearning.sfg.beer.order.service.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.sql.Timestamp;
import java.util.UUID;


@Getter
@Setter
@NoArgsConstructor
@Entity
public class BeerOrderLine extends BaseEntity {

    @ManyToOne
    private BeerOrder beerOrder;
    private UUID beerId;
    private String upc;
    private Integer orderQuantity = 0;
    /*
     * Allocated quantity represents the quantity of the product reserved to be used in the pending sale
     * and/or work orders. These orders are not yet completed yet but are authorised, and for successful completion,
     * items are reserved to avoid double selling. Allocated quantity is used to determine Available quantity
     * for Quoting, Ordering, and Picking.
     * */
    private Integer quantityAllocated = 0;

    @Builder
    public BeerOrderLine(UUID id, Long version, Timestamp createdDate, Timestamp lastModifiedDate,
                         BeerOrder beerOrder, UUID beerId, String upc, Integer orderQuantity,
                         Integer quantityAllocated) {
        super(id, version, createdDate, lastModifiedDate);
        this.beerOrder = beerOrder;
        this.beerId = beerId;
        this.upc = upc;
        this.orderQuantity = orderQuantity;
        this.quantityAllocated = quantityAllocated;
    }
}
