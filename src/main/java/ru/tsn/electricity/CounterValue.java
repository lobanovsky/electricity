package ru.tsn.electricity;

import lombok.*;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.math.BigDecimal;
import java.util.StringJoiner;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CounterValue {
    private BigDecimal t1 = BigDecimal.ZERO;
    private BigDecimal t2 = BigDecimal.ZERO;
    private BigDecimal t3 = BigDecimal.ZERO;

    @Override
    public String toString() {
        return new StringJoiner(", ")
                .add("T1=" + t1)
                .add("T2=" + t2)
                .add("T3=" + t3)
                .toString();
    }

    public CounterValue sub(CounterValue subtrahend) {
        return CounterValue.builder()
                .t1(t1.subtract(subtrahend.getT1()))
                .t2(t2.subtract(subtrahend.getT2()))
                .t3(t3.subtract(subtrahend.getT3()))
                .build();
    }

    public CounterValue add(CounterValue augend) {
        return CounterValue.builder()
                .t1(t1.add(augend.getT1()))
                .t2(t2.add(augend.getT2()))
                .t3(t3.add(augend.getT3()))
                .build();
    }

    public CounterValue multiply(TariffValue multiplicand) {
        return CounterValue.builder()
                .t1(t1.multiply(multiplicand.getT1()))
                .t2(t2.multiply(multiplicand.getT2()))
                .t3(t3.multiply(multiplicand.getT3()))
                .build();
    }

    public BigDecimal sum() {
        return t1.add(t2).add(t3);
    }
}
