package pt.ulusofona.cd.store.events;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
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

    public void sendSupplierDeactivatedEvent(Supplier supplier) {

        SupplierDeactivatedEvent event = new SupplierDeactivatedEvent(supplier.getId().toString(),
                supplier.getCompanyName(),
                supplier.getEmail(),
                supplier.isActive(),
                java.time.LocalDateTime.now());

        kafkaTemplate.send(supplierDeactivatedEventsTopic, supplier.getId().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        System.out.println("Sent message=[" + event + "]");
                    } else {
                        System.err.println("Unable to send message=[" + event + "] due to : " + ex.getMessage());
                    }
                });
    }


    @Value("${supplier.events.report-production-events}")
    private String supplierReportProductionEventsTopic;

    public void sendSupplierReportProductionEvent(ReportProductionEvent reportProductionEvent) {
        kafkaTemplate.send(supplierReportProductionEventsTopic, UUID.randomUUID().toString(), reportProductionEvent)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        System.out.println("Sent message=[" + reportProductionEvent + "]");
                    } else {
                        System.err.println("Unable to send message=[" + reportProductionEvent + "] due to : " + ex.getMessage());
                    }
                });
    }

}