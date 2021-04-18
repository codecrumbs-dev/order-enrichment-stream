package dev.codecrumbs.order.stream;

import dev.codecrumbs.order.stream.model.Product;
import org.springframework.data.repository.CrudRepository;

public interface ProductRepository extends CrudRepository<Product, Integer> {
}
