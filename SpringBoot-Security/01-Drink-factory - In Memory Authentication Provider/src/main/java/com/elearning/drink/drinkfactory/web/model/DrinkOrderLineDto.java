package com.elearning.drink.drinkfactory.web.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DrinkOrderLineDto extends BaseItem {

    @Builder
    public DrinkOrderLineDto(UUID id, Integer version, OffsetDateTime createdDate, OffsetDateTime lastModifiedDate,
                             UUID drinkId, Integer orderQuantity) {
        super(id, version, createdDate, lastModifiedDate);
        this.drinkId = drinkId;
        this.orderQuantity = orderQuantity;
    }

    private UUID drinkId;
    private Integer orderQuantity = 0;
}
