package dev.cafekiosk.api.controller.order;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import dev.cafekiosk.ControllerTestSupport;
import dev.cafekiosk.api.controller.order.request.OrderCreateRequest;

class OrderControllerTest extends ControllerTestSupport {

    @DisplayName("상품 번호 리스트를 통해 주문을 요청한다.")
    @Test
    void createOrder() throws Exception {
        // given
        OrderCreateRequest request = OrderCreateRequest.builder()
                .productNumbers(List.of("001", "002"))
                .build();

        // when
        // then
        mockMvc.perform(
                        post("/api/v1/orders")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"));
    }

    @DisplayName("신규 주문을 등록할 때 상품 리스트는 1개 이상이어야 한다.")
    @Test
    void createOrderWithoutProductNumbers() throws Exception {
        // given
        OrderCreateRequest request = OrderCreateRequest.builder()
                .productNumbers(List.of())
                .build();

        // when
        // then
        mockMvc.perform(
                        post("/api/v1/orders")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("상품 번호 리스트는 필수입니다."))
                .andExpect(jsonPath("$.data").isEmpty());
    }

}
