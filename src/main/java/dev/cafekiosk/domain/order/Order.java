package dev.cafekiosk.domain.order;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import dev.cafekiosk.domain.BaseEntity;
import dev.cafekiosk.domain.orderproduct.OrderProduct;
import dev.cafekiosk.domain.product.Product;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "orders")
@Entity
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    private int totalPrice;

    private LocalDateTime registeredDateTime;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderProduct> orderProducts = new ArrayList<>();

    private Order(List<Product> products, LocalDateTime registeredDateTime) {
        this.registeredDateTime = registeredDateTime;
        this.orderStatus = OrderStatus.INIT;
        this.orderProducts = addOrderProduct(products);
        this.totalPrice = calculateTotalPrice();
    }

    private List<OrderProduct> addOrderProduct(List<Product> products) {
        return products.stream()
                .map(product -> new OrderProduct(product, this))
                .toList();
    }

    public static Order create(List<Product> products, LocalDateTime registeredDateTime) {
        return new Order(products, registeredDateTime);
    }

    private int calculateTotalPrice() {
        return orderProducts.stream()
                .mapToInt(OrderProduct::getProductPrice)
                .sum();
    }
}
