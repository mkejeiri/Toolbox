package elearning.wiremock.brewery.web.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CreditCardDto extends BaseItem {

    @Builder
    public CreditCardDto(UUID id, Integer version, OffsetDateTime createdDate,
                         OffsetDateTime lastModifiedDate) {
        super(id, version, createdDate, lastModifiedDate);
    }

    private Integer cardNumber;
    private LocalDate expirationDate;
    private Integer cvv;
    private Boolean primary;
}
