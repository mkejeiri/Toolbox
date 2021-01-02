package elearning.sfg.beer.brewery.events;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ValidateOrderResult {
    private UUID orderId;
    private boolean isValid;
}
