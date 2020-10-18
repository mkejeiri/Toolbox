package com.supplychain.mssdrink.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DrinkDto {
    @Null
    private UUID id;
    @NotNull
    private Long version;

    private OffsetDateTime createdAt;
    private OffsetDateTime modifiedAt;
    @NotBlank
    private String drinkName;

    @NotBlank
    private String drinkStyle;

    @Positive
    private Long upc;
    private BigDecimal price;
    private Integer quantityOnHand;
}
