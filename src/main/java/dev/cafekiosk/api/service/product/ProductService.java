package dev.cafekiosk.api.service.product;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.cafekiosk.api.service.product.request.ProductCreateServiceRequest;
import dev.cafekiosk.api.service.product.response.ProductResponse;
import dev.cafekiosk.domain.product.Product;
import dev.cafekiosk.domain.product.ProductRepository;
import dev.cafekiosk.domain.product.ProductSellingStatus;
import lombok.RequiredArgsConstructor;

/**
 * CQRS - Command / Query
 */
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ProductService {

    private final ProductRepository productRepository;

    // 동시성 이슈 -> UUID 고려
    @Transactional
    public ProductResponse createProduct(ProductCreateServiceRequest productCreateRequest) {
        String nextProductName = createNextProductName();

        Product product = productCreateRequest.toEntity(nextProductName);
        productRepository.save(product);

        return ProductResponse.of(product);
    }

    private String createNextProductName() {
        String latestProductNumber = productRepository.findLatestProductNumber();
        if (latestProductNumber == null) {
            return "001";
        }

        int nextProductNumberInt = Integer.parseInt(latestProductNumber) + 1;
        return String.format("%03d", nextProductNumberInt);
    }

    public List<ProductResponse> getSellingProducts() {
        List<Product> products = productRepository.findAllBySellingStatusIn(ProductSellingStatus.forDisplay());
        return products.stream()
                .map(ProductResponse::of)
                .toList();
    }
}
