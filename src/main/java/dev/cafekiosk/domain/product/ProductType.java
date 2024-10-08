package dev.cafekiosk.domain.product;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductType {

    HANDMADE("제조 음료"),
    BOTTLE("병 음료"),
    BAKERY("베이커리");

    private final String text;

    public static boolean containStockType(ProductType productType) {
        return productType.equals(BOTTLE) || productType.equals(BAKERY);
    }
}
