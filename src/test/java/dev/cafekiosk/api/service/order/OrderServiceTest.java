package dev.cafekiosk.api.service.order;

import static dev.cafekiosk.domain.product.ProductType.*;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import dev.cafekiosk.IntegrationTestSupport;
import dev.cafekiosk.api.service.order.request.OrderCreateServiceRequest;
import dev.cafekiosk.api.service.order.response.OrderResponse;
import dev.cafekiosk.api.service.product.response.ProductResponse;
import dev.cafekiosk.domain.product.Product;
import dev.cafekiosk.domain.product.ProductRepository;
import dev.cafekiosk.domain.product.ProductSellingStatus;
import dev.cafekiosk.domain.product.ProductType;
import dev.cafekiosk.domain.stock.Stock;
import dev.cafekiosk.domain.stock.StockRepository;

class OrderServiceTest extends IntegrationTestSupport {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StockRepository stockRepository;

    @DisplayName("주문번호 리스트를 받아 주문을 생성한다.")
    @Test
    void createOrder() {
        // given
        Product product1 = createProduct("001", HANDMADE, 4000);
        Product product2 = createProduct("002", HANDMADE, 4500);
        Product product3 = createProduct("003", BAKERY, 7000);
        productRepository.saveAll(List.of(product1, product2, product3));
        OrderCreateServiceRequest request = OrderCreateServiceRequest.builder()
                .productNumbers(List.of("001", "002"))
                .build();

        // when
        LocalDateTime registeredDateTime = LocalDateTime.now();
        OrderResponse orderResponse = orderService.createOrder(request, registeredDateTime);

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

    @DisplayName("중복된 주문번호로 주문을 생성할 수 있다.")
    @Test
    void createOrderWithDuplicateProductNumbers() {
        // given
        Product product1 = createProduct("001", HANDMADE, 4000);
        Product product2 = createProduct("002", HANDMADE, 4500);
        Product product3 = createProduct("003", BAKERY, 7000);
        productRepository.saveAll(List.of(product1, product2, product3));
        OrderCreateServiceRequest request = OrderCreateServiceRequest.builder()
                .productNumbers(List.of("001", "001", "002"))
                .build();

        // when
        LocalDateTime registeredDateTime = LocalDateTime.now();
        OrderResponse orderResponse = orderService.createOrder(request, registeredDateTime);

        // then
        assertThat(orderResponse.getId()).isNotNull();
        assertThat(orderResponse)
                .extracting(OrderResponse::getRegisteredDateTime, OrderResponse::getTotalPrice)
                .containsExactlyInAnyOrder(registeredDateTime, 12500);
        assertThat(orderResponse.getProducts()).hasSize(3)
                .extracting(ProductResponse::getProductNumber, ProductResponse::getPrice)
                .containsExactlyInAnyOrder(
                        tuple("001", 4000),
                        tuple("001", 4000),
                        tuple("002", 4500)
                );
    }

    @DisplayName("재고와 관련된 상품이 포함되어 있는 주문번호 리스트를 받아 주문을 생성한다.")
    @Test
    void createOrderWithStock() {
        // given
        Product product1 = createProduct("001", BOTTLE, 1000);
        Product product2 = createProduct("002", BAKERY, 2000);
        Product product3 = createProduct("003", HANDMADE, 6000);
        productRepository.saveAll(List.of(product1, product2, product3));

        Stock stock1 = Stock.create("001", 2);
        Stock stock2 = Stock.create("002", 2);
        stockRepository.saveAll(List.of(stock1, stock2));

        OrderCreateServiceRequest request = OrderCreateServiceRequest.builder()
                .productNumbers(List.of("001", "001", "002", "003"))
                .build();

        // when
        LocalDateTime registeredDateTime = LocalDateTime.now();
        OrderResponse orderResponse = orderService.createOrder(request, registeredDateTime);

        // then
        assertThat(orderResponse.getId()).isNotNull();
        assertThat(orderResponse)
                .extracting(OrderResponse::getRegisteredDateTime, OrderResponse::getTotalPrice)
                .containsExactlyInAnyOrder(registeredDateTime, 10000);
        assertThat(orderResponse.getProducts()).hasSize(4)
                .extracting(ProductResponse::getProductNumber, ProductResponse::getPrice)
                .containsExactlyInAnyOrder(
                        tuple("001", 1000),
                        tuple("001", 1000),
                        tuple("002", 2000),
                        tuple("003", 6000)
                );

        List<Stock> stocks = stockRepository.findAll();
        assertThat(stocks).hasSize(2)
                .extracting(Stock::getProductNumber, Stock::getQuantity)
                .containsExactlyInAnyOrder(
                        tuple("001", 0),
                        tuple("002", 1)
                );
    }

    @DisplayName("재고와 관련된 상품이 포함되어 있는 주문번호 리스트를 받아 주문을 생성할 때 재고가 부족하면 예외가 발생한다.")
    @Test
    void createOrderWithStockEx() {
        // given
        Product product1 = createProduct("001", BOTTLE, 1000);
        Product product2 = createProduct("002", BAKERY, 2000);
        Product product3 = createProduct("003", HANDMADE, 6000);
        productRepository.saveAll(List.of(product1, product2, product3));

        Stock stock1 = Stock.create("001", 1);
        Stock stock2 = Stock.create("002", 1);
        stockRepository.saveAll(List.of(stock1, stock2));

        LocalDateTime registeredDateTime = LocalDateTime.now();
        OrderCreateServiceRequest request = OrderCreateServiceRequest.builder()
                .productNumbers(List.of("001", "001", "002", "003"))
                .build();

        // when
        // then
        assertThatThrownBy(() -> orderService.createOrder(request, registeredDateTime))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("재고 수량이 부족합니다.");
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
