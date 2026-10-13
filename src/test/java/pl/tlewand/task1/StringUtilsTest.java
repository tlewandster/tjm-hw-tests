package pl.tlewand.task1;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StringUtilsTest {
    @Test
    void shouldReturnTrue_whenTextIsPalindrome(){
        assertTrue(StringUtils.isPalindrome("kajak"));
    }

    @Test
    void shouldReturnTrue_whenPalindromeHasMixedCase(){
        assertTrue(StringUtils.isPalindrome("Kajak"));
    }

    @Test
    void shouldReturnFalse_whenTextIsNotPalindrome(){
        assertFalse(StringUtils.isPalindrome("java"));
    }

    @Test
    void shouldReturnTrue_whenTextIsEmpty(){
        assertTrue(StringUtils.isPalindrome(""));
    }

    @Test
    void shouldReturnTrue_whenTextNull(){
        assertTrue(StringUtils.isPalindrome(null));
    }
}
