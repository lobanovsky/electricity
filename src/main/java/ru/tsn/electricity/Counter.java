package ru.tsn.electricity;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.StringJoiner;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Counter {

    private String flat;
    private String number;
    private LocalDateTime date;
    private BigDecimal t1;
    private BigDecimal t2;
    private BigDecimal t3;
    private BigDecimal total;


    @Override
    public String toString() {
        return new StringJoiner(", ", Counter.class.getSimpleName() + "[", "]")
                .add("flat='" + flat + "'")
                .add("number='" + number + "'")
                .add("date=" + date)
                .add("t1=" + t1)
                .add("t2=" + t2)
                .add("t3=" + t3)
                .add("total=" + total)
                .toString();
    }
}
