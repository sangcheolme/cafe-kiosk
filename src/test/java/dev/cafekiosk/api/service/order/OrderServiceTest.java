package dev.cafekiosk.api.service.order;

import static dev.cafekiosk.domain.product.ProductType.*;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import dev.cafekiosk.api.controller.order.request.OrderCreateRequest;
import dev.cafekiosk.api.service.order.response.OrderResponse;
import dev.cafekiosk.api.service.product.response.ProductResponse;
import dev.cafekiosk.domain.product.Product;
import dev.cafekiosk.domain.product.ProductRepository;
import dev.cafekiosk.domain.product.ProductSellingStatus;
import dev.cafekiosk.domain.product.ProductType;

@ActiveProfiles("test")
@Transactional
@SpringBootTest
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductRepository productRepository;

    @DisplayName("주문번호 리스트를 받아 주문을 생성한다.")
    @Test
    void createOrder() {
        // given
        Product product1 = createProduct("001", HANDMADE, 4000);
        Product product2 = createProduct("002", HANDMADE, 4500);
        Product product3 = createProduct("003", BAKERY, 7000);
        productRepository.saveAll(List.of(product1, product2, product3));
        OrderCreateRequest orderCreateRequest = OrderCreateRequest.builder()
                .productNumbers(List.of("001", "002"))
                .build();

        // when
        LocalDateTime registeredDateTime = LocalDateTime.now();
        OrderResponse orderResponse = orderService.createOrder(orderCreateRequest, registeredDateTime);

        // then
        assertThat(orderResponse.getId()).isNotNull();
        assertThat(orderResponse)
                .extracting(OrderResponse::getRegisteredDateTime, OrderResponse::getTotalPrice)
                .containsExactlyInAnyOrder(registeredDateTime, 8500);
        assertThat(orderResponse.getProducts()).hasSize(2)
                .extracting(ProductResponse::getProductNumber, ProductResponse::getPrice)
                .containsExactlyInAnyOrder(
                        tuple("001", 4000),
                        tuple("002", 4500)
                );
    }

    private Product createProduct(String productNumber, ProductType type, int price) {
        return Product.builder()
                .productNumber(productNumber)
                .type(type)
                .sellingStatus(ProductSellingStatus.SELLING)
                .name("상품 이름")
                .price(price)
                .build();
    }

}
