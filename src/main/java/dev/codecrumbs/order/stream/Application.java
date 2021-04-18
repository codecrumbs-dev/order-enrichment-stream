package dev.codecrumbs.order.stream;

import dev.codecrumbs.order.stream.model.Order;
import dev.codecrumbs.order.stream.model.Product;
import dev.codecrumbs.order.stream.model.SaleSummary;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@SpringBootApplication
@Slf4j
@EnableJdbcRepositories
public class Application extends AbstractJdbcConfiguration {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public Function<KStream<Integer, Order>, KStream<Integer, SaleSummary>> enrichOrder(CustomerRepository customers, ProductRepository products) {
        return input -> input
                .peek((s, order) -> log.info("Received order [{}] for enrichment.  Order is [{}].", s, order))
                .map((orderNumber, order) -> {
                    List<Pair<Product, Integer>> productQuantities = order.getItems().stream()
                            .map(item -> Pair.of(products.findById(item.getProductId()).orElseThrow(), item.getQuantity()))
                            .collect(Collectors.toList());
                    return new KeyValue<>(orderNumber, new SaleSummary(
                            customers.findById(order.getCustomerId()).orElseThrow().getName(),
                            productQuantities.stream().map(productAndQuantity -> productAndQuantity.getLeft().getDescription())
                                    .collect(Collectors.toList()),
                            productQuantities.stream().map(
                                    productAndQuantity -> productAndQuantity.getLeft()
                                            .getUnitPrice()
                                            .multiply(BigDecimal.valueOf(productAndQuantity.getRight()))
                            ).reduce(BigDecimal.ZERO, BigDecimal::add)
                    ));
                })
                .peek((key, value) -> log.info("Enriched order [{}].  Summary is [{}].", key, value));
    }

}
