package dev.cafekiosk.domain.product;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ProductTypeTest {

    @DisplayName("상품 타입이 재고관련 타입인지를 체크한다.")
    @Test
    void containStockType() {
        // given
        // when
        boolean handmade = ProductType.containStockType(ProductType.HANDMADE);
        boolean bakery = ProductType.containStockType(ProductType.BAKERY);
        boolean bottle = ProductType.containStockType(ProductType.BOTTLE);

        // then
        assertThat(handmade).isFalse();
        assertThat(bakery).isTrue();
        assertThat(bottle).isTrue();
    }

}
