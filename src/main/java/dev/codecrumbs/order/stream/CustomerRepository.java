package dev.codecrumbs.order.stream;

import dev.codecrumbs.order.stream.model.Customer;
import org.springframework.data.repository.CrudRepository;

public interface CustomerRepository extends CrudRepository<Customer, Integer> {
}
