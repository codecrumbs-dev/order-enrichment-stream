package dev.codecrumbs.order.stream;

import com.github.javafaker.Faker;
import lombok.Data;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Set;
import java.util.function.Function;

@SpringBootApplication
public class Application {
    private static final Faker FAKER = new Faker();

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public Function<KStream<String, Order>, KStream<String, SaleSummary>> process() {
        return input -> input
                .map((orderNumber, order) -> new KeyValue<>(orderNumber, summarise(order)));
    }

    private SaleSummary summarise(Order order) {
        return new SaleSummary(
                String.format("%s-%s", FAKER.company().name(), order.customerId),
                order.totalItems(),
                FAKER.commerce().price()
        );
    }

    @Data
    public static class Order {
        private final String customerId;
        private final Set<OrderItem> items;

        public int totalItems() {
            return items.stream()
                    .map(item -> item.quantity)
                    .mapToInt(Integer::intValue)
                    .sum();
        }
    }
    @Data
    public static class OrderItem {
        private final int productId;
        private final int quantity;
    }
    @Data
    public static class SaleSummary {
        private final String customerName;
        private final int totalItems;
        private final String value;
    }

}
