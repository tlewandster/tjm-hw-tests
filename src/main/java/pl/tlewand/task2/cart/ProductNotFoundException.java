package pl.tlewand.task2.cart;

public class ProductNotFoundException extends RuntimeException {

    private final String productId;

    public ProductNotFoundException(String productId) {
        super("Produkt nie znaleziony: " + productId);
        this.productId = productId;
    }

    public String getProductId() {
        return productId;
    }
}
