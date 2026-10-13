package pl.tlewand.task1;

public class StringUtils {

    static boolean isPalindrome(String text){
        if (text == null || text.isBlank()){
            return true;
        }
        text = text.toLowerCase();
        String reversedText = new StringBuilder(text).reverse().toString();
        return text.equals(reversedText);
    }
}
