package dev.codecrumbs.order.stream;

import ch.qos.logback.classic.util.LogbackMDCAdapter;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import lombok.Data;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.beans.BeanProperty;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@SpringBootApplication
public class Application {

    private static final Map<Integer, Product> PRODUCTS =
            Set.of(new Product(345, "Apples"))
                    .stream()
                    .collect(Collectors.toMap(Product::getId, p -> p));
    private static final Map<Integer, Customer> CUSTOMERS =
            Set.of(new Customer(123, "John Smith"))
                    .stream()
                    .collect(Collectors.toMap(Customer::getId, c -> c));

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public Function<KStream<String, Order>, KStream<String, SaleSummary>> enrichOrder() {
        return input -> input
                .map((orderNumber, order) -> new KeyValue<>(orderNumber, summarise(order)));
    }

    private SaleSummary summarise(Order order) {
        return new SaleSummary(
                findCustomer(order.customerId).getName(),
                order.items.stream()
                        .map(item -> findProduct(item.productId).getDescription())
                        .collect(Collectors.toList()),
                "1.25"
        );
    }

    private Customer findCustomer(String id) {
        return CUSTOMERS.get(id);
    }

    private Product findProduct(int id) {
        return PRODUCTS.get(id);
    }

    @Data
    public static class Order {
        private final String customerId;
        private final Set<OrderItem> items;
    }
    @Data
    public static class OrderItem {
        private final int productId;
        private final int quantity;
    }
    @Data
    public static class Product {
        private final int id;
        private final String description;
    }
    @Data
    public static class Customer {
        private final int id;
        private final String name;
    }
    @Data
    public static class SaleSummary {
        private final String customerName;
        private final List<String> descriptions;
        private final String value;
    }
}
