package pt.ulusofona.cd.store.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportProductionEvent {
    private String supplierId;
    private String email;
    private String orderId;
    private int quantity;
    private LocalDateTime productionDate;
}
