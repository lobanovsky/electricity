package ru.tsn.electricity;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Result {
    private String month;

    private BigDecimal credit;
    private BigDecimal debit;
}
