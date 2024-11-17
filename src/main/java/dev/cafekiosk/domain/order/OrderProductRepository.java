package dev.cafekiosk.domain.order;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.cafekiosk.domain.orderproduct.OrderProduct;

public interface OrderProductRepository extends JpaRepository<OrderProduct, Long> {

}
