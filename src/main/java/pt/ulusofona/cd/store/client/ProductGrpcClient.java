package pt.ulusofona.cd.store.client;

import org.springframework.grpc.client.GrpcChannelFactory;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import pt.ulusofona.cd.store.CountDiscontinuedProductsRequest;
import pt.ulusofona.cd.store.ProductCountResponse;
import pt.ulusofona.cd.store.ProductServiceGrpc;


@Service
public class ProductGrpcClient {

    private final ProductServiceGrpc.ProductServiceBlockingStub blockingStub;

    @Autowired
    public ProductGrpcClient(GrpcChannelFactory channelFactory) {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("product-service", 9090)
                .usePlaintext()
                .build();
        this.blockingStub = ProductServiceGrpc.newBlockingStub(channel);
    }

    public int countDiscontinuedProducts(String supplierId, boolean isDiscontinued) {
        System.out.println("called countDiscontinuedProducts");
        try {
            CountDiscontinuedProductsRequest request = CountDiscontinuedProductsRequest.newBuilder()
                    .setSupplierId(supplierId)
                    .setIsDiscontinued(isDiscontinued)
                    .build();

            System.out.println("Sending gRPC request for supplier: " + supplierId);
            ProductCountResponse response = blockingStub.countDiscontinuedProductsBySupplier(request);
            System.out.println("Received gRPC response: " + response.getCount());

            return response.getCount();
        } catch (Exception e) {
            System.err.println("gRPC call failed: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to call product service", e);
        }
    }
}