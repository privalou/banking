package io.privalou.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class TransferModel {

    private Long senderId;

    private Long receiverId;

    private BigDecimal amount;
}
