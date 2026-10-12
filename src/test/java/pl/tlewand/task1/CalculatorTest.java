package pl.tlewand.task1;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CalculatorTest {

    private final Calculator calculator = new Calculator();

    @Test
    void ShouldSubtractTwoPositiveNumbers() {
        assertEquals(2, calculator.subtract(5, 3));
    }

    @Test
    void shouldReturnNegativeResult() {
        assertEquals(-2, calculator.subtract(3, 5));
    }

    @Test
    void shouldReturnSameNumberWhenSubtractingZero(){
        assertEquals(5, calculator.subtract(5,0));
    }

    @Test
    void shouldSubtractFromZero() {
        assertEquals(-5, calculator.subtract(0, 5));
    }

    @Test
    void shouldSubtractNegativeNumbers() {
        assertEquals(-1, calculator.subtract(-3, -2));
    }

}
