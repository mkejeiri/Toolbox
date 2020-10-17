package com.supplychain.drinkmicroservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DrinkDto {
    private UUID id;
    private Integer version;
    private OffsetDateTime createdAt;
    private OffsetDateTime modifiedAt;
    private String drinkName;
    private DrinkStyleEnum drinkStyle;
    private Long upc;
    private BigDecimal price;
    private Integer quantityOnHand;
}
