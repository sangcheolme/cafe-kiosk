package dev.cafekiosk.api.service.order;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import dev.cafekiosk.api.service.mail.MailService;
import dev.cafekiosk.domain.order.Order;
import dev.cafekiosk.domain.order.OrderRepository;
import dev.cafekiosk.domain.order.OrderStatus;

@RequiredArgsConstructor
@Service
public class OrderStaticsService {

    private final OrderRepository orderRepository;
    private final MailService mailService;

    public boolean sendOrderStaticsMail(LocalDate orderDate, String email) {
        // 해당 일자의 결제 완료된 주문들을 가져와서
        List<Order> orders = orderRepository.findOrdersBy(
                orderDate.atStartOfDay(),
                orderDate.plusDays(1).atStartOfDay(),
                OrderStatus.PAYMENT_COMPLETE
        );

        // 총 매충 합계를 계산하고
        int totalAmount = orders.stream()
                .mapToInt(Order::getTotalPrice)
                .sum();

        // 메일 전송
        boolean result = mailService.sendMail(
                "no-reply@cafekiosk.com",
                email,
                String.format("[매출통계] %s", orderDate),
                String.format("총 매출 합계는 %s원 입니다.", totalAmount)
        );

        if (!result) {
            throw new IllegalArgumentException("매출 통계 메일 전송에 실패했습니다.");
        }

        return true;
    }

}
