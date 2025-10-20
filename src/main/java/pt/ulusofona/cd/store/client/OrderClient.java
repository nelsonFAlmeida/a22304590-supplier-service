package pt.ulusofona.cd.store.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import pt.ulusofona.cd.store.dto.MetricsDto;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "order-service", url = "http://order-service:8083")
public interface OrderClient {

    @PostMapping("/orders/metrics/products")
    List<MetricsDto> getOrdersCountByProducts(@RequestBody List<UUID> productIds);


}
