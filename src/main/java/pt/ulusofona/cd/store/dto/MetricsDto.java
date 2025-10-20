package pt.ulusofona.cd.store.dto;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetricsDto {
    private UUID id;
    @Min(0)
    private int numberOfOrders;
}