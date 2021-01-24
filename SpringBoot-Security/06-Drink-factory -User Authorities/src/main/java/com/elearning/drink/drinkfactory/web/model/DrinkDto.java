package com.elearning.drink.drinkfactory.web.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DrinkDto extends BaseItem {

    @Builder
    public DrinkDto(UUID id, Integer version, OffsetDateTime createdDate, OffsetDateTime lastModifiedDate, String drinkName,
                    DrinkStyleEnum drinkStyle, String upc, Integer quantityOnHand, BigDecimal price) {
        super(id, version, createdDate, lastModifiedDate);
        this.drinkName = drinkName;
        this.drinkStyle = drinkStyle;
        this.upc = upc;
        this.quantityOnHand = quantityOnHand;
        this.price = price;
    }

    private String drinkName;
    private DrinkStyleEnum drinkStyle;
    private String upc;
    private Integer quantityOnHand;

    @JsonFormat(shape= JsonFormat.Shape.STRING)
    private BigDecimal price;

}
