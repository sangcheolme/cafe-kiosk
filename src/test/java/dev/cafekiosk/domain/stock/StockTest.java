package dev.cafekiosk.domain.stock;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StockTest {

    @DisplayName("재고에 수량이 충분하면 재고를 차감시킨다.")
    @Test
    void reduceQuantity() {
        // given
        Stock stock = Stock.create("001", 1);

        // when
        stock.reduceQuantity(1);

        // then
        assertThat(stock.getQuantity()).isZero();
    }

    @DisplayName("재고에 수량이 충분하지 않으면 예외가 발생한다.")
    @Test
    void reduceQuantityEx() {
        // given
        // when
        Stock stock = Stock.create("001", 1);

        // then
        assertThatThrownBy(() -> stock.reduceQuantity(2)).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("재고 수량이 부족합니다.");
    }

}
