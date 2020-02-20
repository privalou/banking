package io.privalou.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class AccountModel {
    private Long id;
    private BigDecimal balance;
}
