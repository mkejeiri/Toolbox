package com.supplychain.mssdrink.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DrinkDto {
    @JsonProperty("drinkId")
    @Null
    private UUID id;
    @NotNull
    private Long version;

    //@JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ssZ", shape=JsonFormat.Shape.STRING)
    private OffsetDateTime createdAt;

    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ssZ", shape=JsonFormat.Shape.STRING)
    private OffsetDateTime modifiedAt;
    @NotBlank
    private String drinkName;

    @NotBlank
    private String drinkStyle;

    @Positive
    private Long upc;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal price;
    private Integer quantityOnHand;

    //Example of JsonSerializer, check out LocalDateSerializer class where the "JsonGenerator" write the custom value
    @JsonSerialize(using = LocalDateSerializer.class)
    //Example of JsonDeserializer, check out LocalDateDeserializer class where the jsonParser.readValueAs(String.class) the custom value
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate myLocalDate;

}
