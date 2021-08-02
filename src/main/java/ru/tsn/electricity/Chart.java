package ru.tsn.electricity;

import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Chart {
    //КВт
    List<BigDecimal> office = new ArrayList<>();
    List<BigDecimal> house = new ArrayList<>();
    List<BigDecimal> itp = new ArrayList<>();
    List<BigDecimal> parking = new ArrayList<>();
    List<BigDecimal> flats = new ArrayList<>();
    List<BigDecimal> common = new ArrayList<>();
    //₽
    List<BigDecimal> commonMoney = new ArrayList<>();
    List<BigDecimal> flatsMoney = new ArrayList<>();
    List<BigDecimal> itpMoney = new ArrayList<>();
    List<BigDecimal> houseMoney = new ArrayList<>();
    List<BigDecimal> officeMoney = new ArrayList<>();
    List<BigDecimal> parkingMoney = new ArrayList<>();

    List<BigDecimal> totalHouseMoney = new ArrayList<>();
    List<BigDecimal> exposeHouse = new ArrayList<>();
    List<BigDecimal> exposeParking = new ArrayList<>();
}
