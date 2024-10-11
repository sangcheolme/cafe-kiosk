package dev.cafekiosk.api.service.order;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.cafekiosk.api.controller.order.request.OrderCreateRequest;
import dev.cafekiosk.api.service.order.response.OrderResponse;
import dev.cafekiosk.domain.order.Order;
import dev.cafekiosk.domain.order.OrderRepository;
import dev.cafekiosk.domain.product.Product;
import dev.cafekiosk.domain.product.ProductRepository;
import dev.cafekiosk.domain.product.ProductType;
import dev.cafekiosk.domain.stock.Stock;
import dev.cafekiosk.domain.stock.StockRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class OrderService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final StockRepository stockRepository;

    @Transactional
    public OrderResponse createOrder(OrderCreateRequest orderCreateRequest, LocalDateTime registeredDateTime) {
        List<String> productNumbers = orderCreateRequest.getProductNumbers();
        List<Product> products = findProductsBy(productNumbers);

        Order order = Order.create(products, registeredDateTime);
        Order savedOrder = orderRepository.save(order);

        detectStockQuantities(products);

        return OrderResponse.of(savedOrder);
    }

    private List<Product> findProductsBy(List<String> productNumbers) {
        List<Product> products = productRepository.findAllByProductNumberIn(productNumbers);
        Map<String, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getProductNumber, p -> p));

        return productNumbers.stream()
                .map(productMap::get)
                .toList();
    }

    /**
     * 재고 감소 -> 동시성 문제 고민
     * optimistic lock / pessimistic lock / ...
     */
    private void detectStockQuantities(List<Product> products) {
        List<String> stockProductNumbers = extractStockProductNumbers(products);

        Map<String, Stock> stockMap = createStockMapBy(stockProductNumbers);
        Map<String, Long> productCountingMap = createCountingMapBy(stockProductNumbers);

        for (String stockProductNumber : new HashSet<>(stockProductNumbers)) {
            Stock stock = stockMap.get(stockProductNumber);
            stock.reduceQuantity(productCountingMap.get(stockProductNumber).intValue());
        }
    }

    private static List<String> extractStockProductNumbers(List<Product> products) {
        return products.stream()
                .filter(product -> ProductType.containStockType(product.getType()))
                .map(Product::getProductNumber)
                .toList();
    }

    private Map<String, Stock> createStockMapBy(List<String> stockProductNumbers) {
        List<Stock> stocks = stockRepository.findByProductNumberIn(stockProductNumbers);
        return stocks.stream()
                .collect(Collectors.toMap(Stock::getProductNumber, s -> s));
    }

    private static Map<String, Long> createCountingMapBy(List<String> stockProductNumbers) {
        return stockProductNumbers.stream()
                .collect(Collectors.groupingBy(p -> p, Collectors.counting()));
    }
}
