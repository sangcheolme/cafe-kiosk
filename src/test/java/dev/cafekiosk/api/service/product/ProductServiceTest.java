package dev.cafekiosk.api.service.product;

import static dev.cafekiosk.domain.product.ProductSellingStatus.*;
import static dev.cafekiosk.domain.product.ProductType.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import dev.cafekiosk.IntegrationTestSupport;
import dev.cafekiosk.api.service.product.request.ProductCreateServiceRequest;
import dev.cafekiosk.api.service.product.response.ProductResponse;
import dev.cafekiosk.domain.product.Product;
import dev.cafekiosk.domain.product.ProductRepository;
import dev.cafekiosk.domain.product.ProductSellingStatus;
import dev.cafekiosk.domain.product.ProductType;

class ProductServiceTest extends IntegrationTestSupport {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @DisplayName("신규 상품을 등록한다. 상품 번호는 가장 최근 상품의 상품번홍에서 1 증가한 값이다")
    @Test
    void createProduct() {
        // given
        Product product1 = createProduct("001", HANDMADE, SELLING, "아메리카노", 4000);
        productRepository.saveAll(List.of(product1));

        ProductCreateServiceRequest request = ProductCreateServiceRequest.builder()
                .type(HANDMADE)
                .sellingStatus(SELLING)
                .name("카푸치노")
                .price(5000)
                .build();

        // when
        ProductResponse productResponse = productService.createProduct(request);

        // then
        assertThat(productResponse)
                .extracting(ProductResponse::getProductNumber,
                        ProductResponse::getType,
                        ProductResponse::getSellingStatus,
                        ProductResponse::getName,
                        ProductResponse::getPrice)
                .containsExactly("002", HANDMADE, SELLING, "카푸치노", 5000);

        List<Product> products = productRepository.findAll();
        assertThat(products).hasSize(2)
                .extracting(Product::getProductNumber,
                        Product::getType,
                        Product::getSellingStatus,
                        Product::getName,
                        Product::getPrice)
                .contains(
                        tuple("001", HANDMADE, SELLING, "아메리카노", 4000),
                        tuple("002", HANDMADE, SELLING, "카푸치노", 5000)
                );
    }

    @DisplayName("신규 상품을 등록한다. 기존 상품이 존재하지 않았다면 상품번호는 001번이 된다.")
    @Test
    void createProductFirst() {
        // given
        ProductCreateServiceRequest request = ProductCreateServiceRequest.builder()
                .type(HANDMADE)
                .sellingStatus(SELLING)
                .name("카푸치노")
                .price(5000)
                .build();

        // when
        ProductResponse productResponse = productService.createProduct(request);

        // then
        assertThat(productResponse)
                .extracting(ProductResponse::getProductNumber,
                        ProductResponse::getType,
                        ProductResponse::getSellingStatus,
                        ProductResponse::getName,
                        ProductResponse::getPrice)
                .containsExactly("001", HANDMADE, SELLING, "카푸치노", 5000);

        List<Product> products = productRepository.findAll();
        assertThat(products).hasSize(1)
                .extracting(Product::getProductNumber,
                        Product::getType,
                        Product::getSellingStatus,
                        Product::getName,
                        Product::getPrice)
                .containsExactlyInAnyOrder(
                        tuple("001", HANDMADE, SELLING, "카푸치노", 5000)
                );
    }

    private Product createProduct(String productNumber, ProductType type, ProductSellingStatus sellingStatus, String name, int price) {
        return Product.builder()
                .productNumber(productNumber)
                .type(type)
                .sellingStatus(sellingStatus)
                .name(name)
                .price(price)
                .build();
    }

}
