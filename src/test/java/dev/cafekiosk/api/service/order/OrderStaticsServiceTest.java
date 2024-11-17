package dev.cafekiosk.api.service.order;

import static dev.cafekiosk.domain.product.ProductType.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import dev.cafekiosk.IntegrationTestSupport;
import dev.cafekiosk.domain.history.mail.MailSendHistory;
import dev.cafekiosk.domain.history.mail.MailSendHistoryRepository;
import dev.cafekiosk.domain.order.Order;
import dev.cafekiosk.domain.order.OrderProductRepository;
import dev.cafekiosk.domain.order.OrderRepository;
import dev.cafekiosk.domain.order.OrderStatus;
import dev.cafekiosk.domain.product.Product;
import dev.cafekiosk.domain.product.ProductRepository;
import dev.cafekiosk.domain.product.ProductSellingStatus;
import dev.cafekiosk.domain.product.ProductType;

class OrderStaticsServiceTest extends IntegrationTestSupport {

    @Autowired
    private OrderStaticsService orderStaticsService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderProductRepository orderProductRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MailSendHistoryRepository mailSendHistoryRepository;

    @AfterEach
    void tearDown() {
        orderProductRepository.deleteAllInBatch();
        orderRepository.deleteAllInBatch();
        productRepository.deleteAllInBatch();
        mailSendHistoryRepository.deleteAllInBatch();
    }

    @DisplayName("결제완료 주문들을 가져와서 메일을 전송한다.")
    @Test
    void sendOrderStaticsMail() {
        // given
        LocalDateTime now = LocalDateTime.of(2023, 3, 5, 10, 0);

        Product product1 = createProduct("001", HANDMADE, 1000);
        Product product2 = createProduct("002", HANDMADE, 2000);
        Product product3 = createProduct("003", HANDMADE, 3000);
        List<Product> products = List.of(product1, product2, product3);
        productRepository.saveAll(products);

        createPaymentCompletedOrder(products, LocalDateTime.of(2023, 3, 4, 23, 59, 59));
        createPaymentCompletedOrder(products, now);
        createPaymentCompletedOrder(products, LocalDateTime.of(2023, 3, 5, 23, 59, 59));
        createPaymentCompletedOrder(products, LocalDateTime.of(2023, 3, 6, 0, 0));

        // stubbing
        when(mailSendClient.sendEmail(any(String.class), any(String.class), any(String.class), any(String.class)))
                .thenReturn(true);

        // when
        boolean result = orderStaticsService.sendOrderStaticsMail(LocalDate.of(2023, 3, 5), "test@test.com");

        // then
        assertThat(result).isTrue();

        List<MailSendHistory> histories = mailSendHistoryRepository.findAll();
        assertThat(histories).hasSize(1)
                .extracting("content")
                .contains("총 매출 합계는 12000원 입니다.");
    }

    private void createPaymentCompletedOrder(List<Product> products, LocalDateTime now) {
        Order order = Order.builder()
                .products(products)
                .orderStatus(OrderStatus.PAYMENT_COMPLETE)
                .registeredDateTime(now)
                .build();
        orderRepository.save(order);
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
