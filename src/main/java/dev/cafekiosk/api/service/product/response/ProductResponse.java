package dev.cafekiosk.api.service.product.response;

import lombok.Builder;
import lombok.Getter;

import dev.cafekiosk.domain.product.Product;
import dev.cafekiosk.domain.product.ProductSellingStatus;
import dev.cafekiosk.domain.product.ProductType;

@Getter
@Builder
public class ProductResponse {

    private Long id;
    private String productNumber;
    private ProductType type;
    private ProductSellingStatus sellingStatus;
    private String name;
    private int price;

    public static ProductResponse of(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .productNumber(product.getProductNumber())
                .type(product.getType())
                .sellingStatus(product.getSellingStatus())
                .name(product.getName())
                .price(product.getPrice())
                .build();
    }

}
