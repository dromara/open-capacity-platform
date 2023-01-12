package com.open.capacity.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Order {
    private Long Id;
    private Integer type;
    private BigDecimal price;
}
