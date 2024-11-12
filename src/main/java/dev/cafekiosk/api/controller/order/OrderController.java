package dev.cafekiosk.api.controller.order;

import java.time.LocalDateTime;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import dev.cafekiosk.api.ApiResponse;
import dev.cafekiosk.api.controller.order.request.OrderCreateRequest;
import dev.cafekiosk.api.service.order.OrderService;
import dev.cafekiosk.api.service.order.response.OrderResponse;

@RequiredArgsConstructor
@RestController
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/api/v1/orders")
    public ApiResponse<OrderResponse> createOrder(@Valid @RequestBody OrderCreateRequest orderCreateRequest) {
        OrderResponse orderResponse = orderService.createOrder(orderCreateRequest.toServiceRequest(), LocalDateTime.now());
        return ApiResponse.ok(orderResponse);
    }

}
