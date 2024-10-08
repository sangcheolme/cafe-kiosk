package dev.cafekiosk.api.controller.order.request;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderCreateRequest {

    private List<String> productNumbers;

}
