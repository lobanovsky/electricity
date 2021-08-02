package ru.tsn.electricity;

import lombok.*;

import java.math.BigDecimal;
import java.util.StringJoiner;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TariffValue {
    private BigDecimal t1;
    private BigDecimal t2;
    private BigDecimal t3;

    @Override
    public String toString() {
        return new StringJoiner(", ")
                .add("Tariff T1=" + t1 + "₽")
                .add("Tariff T2=" + t2 + "₽")
                .add("Tariff T3=" + t3 + "₽")
                .toString();
    }
}
