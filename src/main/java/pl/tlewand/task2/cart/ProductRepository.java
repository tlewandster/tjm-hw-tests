package pl.tlewand.task2.cart;

import java.util.Optional;

public interface ProductRepository {

    Optional<Product> findById(String productId);
}
