package elearning.sfg.beer.brewery.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
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
public class BeerOrderLineDto {

    @JsonProperty("id")
    private UUID id = null;

    @JsonProperty("version")
    private Integer version = null;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ", shape = JsonFormat.Shape.STRING)
    @JsonProperty("createdDate")
    private OffsetDateTime createdDate = null;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ", shape = JsonFormat.Shape.STRING)
    @JsonProperty("lastModifiedDate")
    private OffsetDateTime lastModifiedDate = null;

    private String upc;
    private String beerName;
    private String beerStyle;
    private UUID beerId;
    private Integer orderQuantity = 0;
    /*
     * Allocated quantity represents the quantity of the product reserved to be used in the pending sale
     * and/or work orders. These orders are not yet completed yet but are authorised, and for successful completion,
     * items are reserved to avoid double selling. Allocated quantity is used to determine Available quantity
     * for Quoting, Ordering, and Picking.
     * */
    private Integer quantityAllocated;
    private BigDecimal price;
}