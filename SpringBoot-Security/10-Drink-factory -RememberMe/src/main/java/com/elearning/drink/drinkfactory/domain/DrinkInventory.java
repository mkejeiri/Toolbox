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
public class DrinkInventory extends BaseEntity{

    @Builder
    public DrinkInventory(UUID id, Long version, Timestamp createdDate, Timestamp lastModifiedDate, Drink drink,
                          Integer quantityOnHand) {
        super(id, version, createdDate, lastModifiedDate);
        this.drink = drink;
        this.quantityOnHand = quantityOnHand;
    }

    @ManyToOne
    private Drink drink;

    private Integer quantityOnHand = 0;
}
