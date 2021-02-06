package com.elearning.drink.drinkfactory.domain;

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
public class DrinkOrderLine extends BaseEntity {

    @Builder
    public DrinkOrderLine(UUID id, Long version, Timestamp createdDate, Timestamp lastModifiedDate,
                          DrinkOrder drinkOrder, Drink drink, Integer orderQuantity,
                          Integer quantityAllocated) {
        super(id, version, createdDate, lastModifiedDate);
        this.drinkOrder = drinkOrder;
        this.drink = drink;
        this.orderQuantity = orderQuantity;
        this.quantityAllocated = quantityAllocated;
    }

    @ManyToOne
    private DrinkOrder drinkOrder;

    @ManyToOne
    private Drink drink;

    private Integer orderQuantity = 0;
    private Integer quantityAllocated = 0;
}
