package com.elearning.drink.drinkfactory.domain;

import com.elearning.drink.drinkfactory.web.model.DrinkStyleEnum;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


@Getter
@Setter
@Entity
@NoArgsConstructor
public class Drink extends BaseEntity {

    @Builder
    public Drink(UUID id, Long version, Timestamp createdDate, Timestamp lastModifiedDate, String drinkName,
                 DrinkStyleEnum drinkStyle, String upc, Integer minOnHand,
                 Integer quantityToBrew, BigDecimal price, Set<DrinkInventory> drinkInventory) {
        super(id, version, createdDate, lastModifiedDate);
        this.drinkName = drinkName;
        this.drinkStyle = drinkStyle;
        this.upc = upc;
        this.minOnHand = minOnHand;
        this.quantityToBrew = quantityToBrew;
        this.price = price;
        this.drinkInventory = drinkInventory;
    }

    private String drinkName;
    private DrinkStyleEnum drinkStyle;

    @Column(unique = true)
    private String upc;

    /**
     * Min on hand qty - used to trigger brew
     */
    private Integer minOnHand;
    private Integer quantityToBrew;
    private BigDecimal price;

    @OneToMany(mappedBy = "drink", cascade = CascadeType.ALL)
    @Fetch(FetchMode.JOIN)
    private Set<DrinkInventory> drinkInventory = new HashSet<>();
}
