package pt.ulusofona.cd.store.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import pt.ulusofona.cd.store.dto.ProductDto;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "product-service", url = "http://product-service:8081")
public interface ProductClient {

    @GetMapping("/api/v1/products/supplier/{supplierId}")
    List<ProductDto> getProductsBySupplier(@PathVariable("supplierId") String supplierId);

    @GetMapping("/api/v1/products/{supplierId}/products/blocked")
    Boolean supplierHasBlockedProducts(@PathVariable("supplierId") UUID supplierId);

    @PutMapping("/api/v1/suppliers/{supplierId}/products/discontinue")
    ResponseEntity<Void> discontinueProductsBySupplier(@PathVariable UUID supplierId);


}