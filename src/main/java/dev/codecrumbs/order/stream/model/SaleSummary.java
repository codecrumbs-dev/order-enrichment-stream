package dev.codecrumbs.order.stream.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaleSummary {
    private String customerName;
    private List<String> itemsDescriptions;
    private BigDecimal value;
}
