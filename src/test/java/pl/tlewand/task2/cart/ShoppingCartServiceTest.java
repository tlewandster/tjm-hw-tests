package pl.tlewand.task2.cart;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ShoppingCartService - testy mockowania")
public class ShoppingCartServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private DiscountService discountService;

    @InjectMocks
    private ShoppingCartService shoppingCartService;

    @Nested
    @DisplayName("calculateTotal()")
    class CartCalculateTests {

        @Test
        void shouldCalculatePriceForSingleProductWithoutDiscount() {
            // given
            String customerId = "cust-01";
            List<CartItem> items = List.of(new CartItem("prod-01", 3));

            when(productRepository.findById(anyString()))
                    .thenReturn(Optional.of(new Product("prod-01", "milk", 3.30)));
            when(discountService.getDiscountForCustomer(anyString()))
                    .thenReturn(0.0);
            // when
            double result = shoppingCartService.calculateTotal(customerId, items);
            //then
            assertThat(result).isCloseTo(9.9, within(0.01));
        }

        @Test
        void shouldApply10PercentDiscountForVipCustomers() {
            // given
            String customerId = "cust-02";
            List<CartItem> items = List.of(new CartItem("prod-02", 5));

            when(productRepository.findById(anyString()))
                    .thenReturn(Optional.of(new Product("prod-02", "oil", 2.0)));
            when(discountService.getDiscountForCustomer(anyString()))
                    .thenReturn(0.1);
            // when
            double result = shoppingCartService.calculateTotal(customerId, items);
            //then
            assertThat(result).isCloseTo(9.0, within(0.01));
        }

    }
}