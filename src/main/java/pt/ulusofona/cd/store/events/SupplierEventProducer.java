package pt.ulusofona.cd.store.events;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import pt.ulusofona.cd.store.dto.MessageEnvelope;
import pt.ulusofona.cd.store.dto.ReportProductionEvent;
import pt.ulusofona.cd.store.dto.SupplierDeactivatedEvent;
import pt.ulusofona.cd.store.model.Supplier;


import java.util.UUID;

@Service
public class SupplierEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${supplier.events.supplier-deactivated-events}")
    private String supplierDeactivatedEventsTopic;

    public SupplierEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendSupplierDeactivatedEvent(SupplierDeactivatedEvent payload) {

        MessageEnvelope<SupplierDeactivatedEvent> envelope = new MessageEnvelope<>(
                UUID.randomUUID(),
                "SupplierDeactivatedEvent",
                java.time.Instant.now(),
                payload.getSupplierId(),
                "supplier-service:update",
                payload
        );

        kafkaTemplate.send(supplierDeactivatedEventsTopic, payload.getSupplierId(), envelope)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        System.out.println("Published SupplierDeactivated event for supplier: " + payload.getSupplierId());
                    } else {
                        System.err.println("Failed to publish SupplierDeactivated event: " + ex.getMessage());
                    }
                });
    }

    @Value("${supplier.events.report-production-events}")
    private String supplierReportProductionEventsTopic;

    public void sendSupplierReportProductionEvent(ReportProductionEvent payload) {

        MessageEnvelope<ReportProductionEvent> envelope = new MessageEnvelope<>(
                UUID.randomUUID(),
                "ReportProductionEvent",
                java.time.Instant.now(),
                payload.getOrderId(),
                "supplier-service:update",
                payload
        );

        kafkaTemplate.send(supplierReportProductionEventsTopic, payload.getSupplierId(), envelope)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        System.out.println("Published ReportProduction event for supplier: " + payload.getSupplierId());
                    } else {
                        System.err.println("Failed to publish ReportProduction event: " + ex.getMessage());
                    }
                });
    }

}