package pt.ulusofona.cd.store.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.ulusofona.cd.store.client.OrderClient;
import pt.ulusofona.cd.store.client.ProductClient;
import pt.ulusofona.cd.store.dto.MetricsDto;
import pt.ulusofona.cd.store.dto.ProductDto;
import pt.ulusofona.cd.store.dto.SupplierRequest;
import pt.ulusofona.cd.store.exception.SupplierNotFoundException;
import pt.ulusofona.cd.store.mapper.SupplierMapper;
import pt.ulusofona.cd.store.model.Supplier;
import pt.ulusofona.cd.store.repository.SupplierRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SupplierService {

    private final SupplierRepository supplierRepository;
    private final ProductClient productClient;
    private final OrderClient orderClient;

    @Transactional
    public Supplier createSupplier(SupplierRequest request) {
        // Check if email already exists
        if (supplierRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists: " + request.getEmail());
        }
        Supplier supplier = SupplierMapper.toEntity(request);
        return supplierRepository.save(supplier);
    }

    public Supplier getSupplierById(UUID id) {
        return supplierRepository.findById(id)
                .orElseThrow(() -> new SupplierNotFoundException("Supplier not found with id: " + id));
    }

    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findAll();
    }

    public List<Supplier> getActiveSuppliers() {
        return supplierRepository.findByIsActiveTrue();
    }

    public List<Supplier> getSuppliersByCountry(String country) {
        return supplierRepository.findByCountry(country);
    }

    @Transactional
    public Supplier updateSupplier(UUID id, SupplierRequest supplierDetails) {
        Supplier supplier = getSupplierById(id);

        supplier.setCompanyName(supplierDetails.getCompanyName());
        supplier.setEmail(supplierDetails.getEmail());
        supplier.setAddress(supplierDetails.getAddress());
        supplier.setPhoneNumber(supplierDetails.getPhoneNumber());
        supplier.setContactPerson(supplierDetails.getContactPerson());
        supplier.setTaxNumber(supplierDetails.getTaxNumber());
        supplier.setCountry(supplierDetails.getCountry());
        supplier.setCity(supplierDetails.getCity());
        supplier.setEstablishedDate(supplierDetails.getEstablishedDate());

        return supplierRepository.save(supplier);
    }

    @Transactional
    public Supplier activateSupplier(UUID id) {
        Supplier supplier = getSupplierById(id);
        supplier.setActive(true);
        return supplierRepository.save(supplier);
    }

    //TODO o modo de Alterar estado de um supplier pode ser melhorado para ser mais seguro e mais eficiente usando pedidos maiores em vez de varios pedidos
    @Transactional
    public Supplier deactivateSupplier(UUID id) {
        Supplier supplier = getSupplierById(id);

        boolean blocked;
        try {
            blocked = productClient.supplierHasBlockedProducts(id);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao comunicar com o serviço de produtos", e);
        }

        if (blocked) {
            throw new IllegalStateException(
                    "Não é possível desativar o fornecedor: existem produtos com encomendas pendentes"
            );
        }

        productClient.discontinueProductsBySupplier(id);

        supplier.setActive(false);
        return supplierRepository.save(supplier);
    }



    @Transactional
    public void deleteSupplier(UUID id) {
        Supplier supplier = getSupplierById(id);
        supplierRepository.delete(supplier);
    }

    public List<ProductDto> getProductsBySupplier(UUID supplierId) {
        try {
            return productClient.getProductsBySupplier(supplierId.toString());
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch products for supplier: " + e.getMessage());
        }
    }

    public List<MetricsDto> getMetricsByProduct(UUID id) {

        // 1️⃣ Verificar se o supplier existe
        supplierRepository.findById(id)
                .orElseThrow(() -> new SupplierNotFoundException("Supplier not found with id: " + id));

        // 2️⃣ Obter os produtos deste supplier
        List<ProductDto> products = productClient.getProductsBySupplier(id.toString());

        // 3️⃣ Extrair só os IDs dos produtos
        List<UUID> productIds = products.stream()
                .map(ProductDto::getId)
                .toList();

        // 4️⃣ Enviar a lista de IDs para o order-service e obter métricas
        List<MetricsDto> metrics = orderClient.getOrdersCountByProducts(productIds);

        // 5️⃣ Devolver o resultado ao controller
        return metrics;
    }

}