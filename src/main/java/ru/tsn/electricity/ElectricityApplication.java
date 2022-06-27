package ru.tsn.electricity;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@SpringBootApplication
public class ElectricityApplication implements CommandLineRunner {
    //1 offices (1)
    private final static String OFFICE_1 = "21649058";
    private final static String OFFICE_2 = "21661592";
    //split offices
    private final static String OFFICE_3_1 = "21156035";
    private final static String OFFICE_3_2 = "21156031";
    private final static String OFFICE_5_1 = "21722661";
    private final static String OFFICE_5_2 = "21156030";
    private final static String OFFICE_8_1 = "20301456";
    //80 house
    private final static String HOUSE_1 = "21957686";
    private final static String HOUSE_2 = "22006354";
    //40 parking
    private final static String PARKING_1 = "19488251";
    private final static String PARKING_2 = "19488273";
    //15 itp
    private final static String ITP_1 = "20314256";
    private final static String ITP_2 = "20314260";

    private final static Set<String> COMMON_COUNTERS = Set.of("21957686",
            "22006354",
            "21697853",
            "21219234",
            "21661592",
            "21649058",
            "20352986",
            "21156035",
            "21722661",
            "21156030",
            "21156031",
            "20301456",
            "19488273",
            "19488251",
            "20314260",
            "20314256",
            "20313393");

    private final static Set<String> IN_HOUSE = Set.of(HOUSE_1, HOUSE_2);
    private final static Set<String> IN_PARKING = Set.of(PARKING_1, PARKING_2);
    private final static Set<String> IN_ITP = Set.of(ITP_1, ITP_2);
    private final static Set<String> IN_OFFICE = Set.of(OFFICE_1, OFFICE_2);
    private final static Set<String> OFFICE_SPLIT = Set.of(OFFICE_3_1, OFFICE_3_2, OFFICE_5_1, OFFICE_5_2, OFFICE_8_1);
    private final static Set<String> OFFICE_3 = Set.of(OFFICE_3_1, OFFICE_3_2);
    private final static Set<String> OFFICE_5 = Set.of(OFFICE_5_1, OFFICE_5_2);
    private final static Set<String> OFFICE_8 = Set.of(OFFICE_8_1);

    private final static Set<String> INDIVIDUAL_COUNTERS = new HashSet<>();

    public static final BigDecimal ONE_K = BigDecimal.ONE;
    public static final BigDecimal OFFICE_K = BigDecimal.valueOf(1);
    public static final BigDecimal HOUSE_K = BigDecimal.valueOf(80);
    public static final BigDecimal ITP_K = BigDecimal.valueOf(15);
    public static final BigDecimal PARKING_K = BigDecimal.valueOf(40);

    private static final TariffValue TARIFF_1 = new TariffValue(BigDecimal.valueOf(5.58), BigDecimal.valueOf(1.50), BigDecimal.valueOf(4.65));
    private static final TariffValue TARIFF_2 = new TariffValue(BigDecimal.valueOf(5.84), BigDecimal.valueOf(1.63), BigDecimal.valueOf(4.87));
    private static final TariffValue TARIFF_3 = new TariffValue(BigDecimal.valueOf(6.18), BigDecimal.valueOf(1.74), BigDecimal.valueOf(5.15));

    private Chart chart = new Chart();

    //Выставили платежки в домовладельце
    public static final BigDecimal APRIL = BigDecimal.valueOf(123820.21);
    public static final BigDecimal MAY = BigDecimal.valueOf(134891.26);
    public static final BigDecimal JUNE = BigDecimal.valueOf(138349.58);
    public static final BigDecimal JULE = BigDecimal.valueOf(151732.64);
    public static final BigDecimal AUGUST = BigDecimal.valueOf(132480.28);
    public static final BigDecimal SEPTEMBER = BigDecimal.valueOf(149224.14);
    public static final BigDecimal OCTOBER = BigDecimal.valueOf(199525.33);
    public static final BigDecimal NOVEMBER = BigDecimal.valueOf(177696.37);
    public static final BigDecimal DECEMBER = BigDecimal.valueOf(184130.94);
    public static final BigDecimal JANUARY_21 = BigDecimal.valueOf(222063.86);
    public static final BigDecimal FEBRUARY_21 = BigDecimal.valueOf(204454.29);
    public static final BigDecimal MARCH_21 = BigDecimal.valueOf(169764.43);
    public static final BigDecimal APRIL_21 = BigDecimal.valueOf(178820.4);
    public static final BigDecimal MAY_21 = BigDecimal.valueOf(157013.59);
    public static final BigDecimal JUNE_21 = BigDecimal.valueOf(149582.62);
    public static final BigDecimal JULE_21 = BigDecimal.valueOf(176356.26);
    public static final BigDecimal AUGUST_21 = BigDecimal.valueOf(162451.12);
    public static final BigDecimal SEPTEMBER_21 = BigDecimal.valueOf(192711.9);
    public static final BigDecimal OCTOBER_21 = BigDecimal.valueOf(192711.9);
    public static final BigDecimal NOVEMBER_21 = BigDecimal.valueOf(188889.27);
    public static final BigDecimal DECEMBER_21 = BigDecimal.valueOf(212139.74);
    public static final BigDecimal JANUARY_22 = BigDecimal.valueOf(199268.2);
    public static final BigDecimal FEBRUARY_22 = BigDecimal.valueOf(169766.66);
    public static final BigDecimal MARCH_22 = BigDecimal.valueOf(184689.87);
    public static final BigDecimal APRIL_22 = BigDecimal.valueOf(188785.04);
    public static final BigDecimal MAY_22 = BigDecimal.valueOf(0);

    public static final BigDecimal APRIL_PARKING = BigDecimal.valueOf(26699.72);
    public static final BigDecimal MAY_PARKING = BigDecimal.valueOf(19353.55);
    public static final BigDecimal JUNE_PARKING = BigDecimal.valueOf(26474.40);
    public static final BigDecimal JULE_PARKING = BigDecimal.valueOf(28008.56);
    public static final BigDecimal AUGUST_PARKING = BigDecimal.valueOf(27111.37);
    public static final BigDecimal SEPTEMBER_PARKING = BigDecimal.valueOf(28376.45);
    public static final BigDecimal OCTOBER_PARKING = BigDecimal.valueOf(31371.54);
    public static final BigDecimal NOVEMBER_PARKING = BigDecimal.valueOf(27079);
    public static final BigDecimal DECEMBER_PARKING = BigDecimal.valueOf(30058.33);
    public static final BigDecimal JANUARY_21_PARKING = BigDecimal.valueOf(30878.21);
    public static final BigDecimal FEBRUARY_21_PARKING = BigDecimal.valueOf(25611.16);
    public static final BigDecimal MARCH_21_PARKING = BigDecimal.valueOf(24868.47);
    public static final BigDecimal APRIL_21_PARKING = BigDecimal.valueOf(22626.06);
    public static final BigDecimal MAY_21_PARKING = BigDecimal.valueOf(19360.80);
    public static final BigDecimal JUNE_21_PARKING = BigDecimal.valueOf(15066.76);
    public static final BigDecimal JULE_21_PARKING = BigDecimal.valueOf(13997.53);
    public static final BigDecimal AUGUST_21_PARKING = BigDecimal.valueOf(14881.03);
    public static final BigDecimal SEPTEMBER_21_PARKING = BigDecimal.valueOf(16116.28);
    public static final BigDecimal OCTOBER_21_PARKING = BigDecimal.valueOf(16116.28);
    public static final BigDecimal NOVEMBER_21_PARKING = BigDecimal.valueOf(19385.17);
    public static final BigDecimal DECEMBER_21_PARKING = BigDecimal.valueOf(25441.36);
    public static final BigDecimal JANUARY_22_PARKING = BigDecimal.valueOf(27587.36);
    public static final BigDecimal FEBRUARY_22_PARKING = BigDecimal.valueOf(24635.68);
    public static final BigDecimal MARCH_22_PARKING = BigDecimal.valueOf(26564.86);
    public static final BigDecimal APRIL_22_PARKING = BigDecimal.valueOf(27167.4);
    public static final BigDecimal MAY_22_PARKING = BigDecimal.valueOf(0);

    public static void main(String[] args) {
        SpringApplication.run(ElectricityApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        final Map<String, Counter> march = read("etc/03.xlsx");
        final Map<String, Counter> april = read("etc/04.xlsx");
        final Map<String, Counter> may = read("etc/05.xlsx");
        final Map<String, Counter> june = read("etc/06.xlsx");
        final Map<String, Counter> jule = read("etc/07.xlsx");
        final Map<String, Counter> august = read("etc/08.xlsx");
        final Map<String, Counter> september = read("etc/09.xlsx");
        final Map<String, Counter> october = read("etc/10.xlsx");
        final Map<String, Counter> november = read("etc/11.xlsx");
        final Map<String, Counter> december = read("etc/12.xlsx");
        final Map<String, Counter> january_21 = read("etc/21-01.xlsx");
        final Map<String, Counter> february_21 = read("etc/21-02.xlsx");
        final Map<String, Counter> march_21 = read("etc/21-03.xlsx");
        final Map<String, Counter> april_21 = read("etc/21-04.xlsx");
        final Map<String, Counter> may_21 = read("etc/21-05.xlsx");
        final Map<String, Counter> june_21 = read("etc/21-06.xlsx");
        final Map<String, Counter> jule_21 = read("etc/21-07.xlsx");
        final Map<String
                , Counter> august_21 = read("etc/21-08.xlsx");
        final Map<String, Counter> september_21 = read("etc/2021-09.xlsx");
        final Map<String, Counter> october_21 = read("etc/2021-10.xlsx");
        final Map<String, Counter> november_21 = read("etc/2021-11.xlsx");
        final Map<String, Counter> december_21 = read("etc/2021-12.xlsx");
        final Map<String, Counter> january_22 = read("etc/2022-01.xlsx");
        final Map<String, Counter> february_22 = read("etc/2022-02.xlsx");
        final Map<String, Counter> march_22 = read("etc/2022-03.xlsx");
        final Map<String, Counter> april_22 = read("etc/2022-04.xlsx");
        final Map<String, Counter> may_22 = read("etc/2022-05.xlsx");

        final List<Map<String, Counter>> allCounters = List.of(march,
                april,
                may,
                june,
                jule,
                august,
                september,
                october,
                november,
                december,
                january_21,
                february_21,
                march_21,
                april_21,
                may_21,
                june_21,
                jule_21,
                august_21,
                september_21,
                october_21,
                november_21,
                december_21,
                january_22,
                february_22,
                march_22,
                april_22,
                may_22);

        if (isEqualsCounterSize(allCounters)) return;

        final List<Result> result = List.of(
                calculate(march, april, "апрель", APRIL, APRIL_PARKING, TARIFF_1),
                calculate(april, may, "май", MAY, MAY_PARKING, TARIFF_1),
                calculate(may, june, "июнь", JUNE, JUNE_PARKING, TARIFF_1),
                calculate(june, jule, "июль", JULE, JULE_PARKING, TARIFF_2),
                calculate(jule, august, "август", AUGUST, AUGUST_PARKING, TARIFF_2),
                calculate(august, september, "сентябрь", SEPTEMBER, SEPTEMBER_PARKING, TARIFF_2),
                calculate(september, october, "октябрь", OCTOBER, OCTOBER_PARKING, TARIFF_2),
                calculate(october, november, "ноябрь", NOVEMBER, NOVEMBER_PARKING, TARIFF_2),
                calculate(november, december, "декабрь", DECEMBER, DECEMBER_PARKING, TARIFF_2),
                calculate(december, january_21, "январь 21", JANUARY_21, JANUARY_21_PARKING, TARIFF_2),
                calculate(january_21, february_21, "февраль 21", FEBRUARY_21, FEBRUARY_21_PARKING, TARIFF_2),
                calculate(february_21, march_21, "март 21", MARCH_21, MARCH_21_PARKING, TARIFF_2),
                calculate(march_21, april_21, "апрель 21", APRIL_21, APRIL_21_PARKING, TARIFF_2),
                calculate(april_21, may_21, "май 21", MAY_21, MAY_21_PARKING, TARIFF_2),
                calculate(may_21, june_21, "июнь 21", JUNE_21, JUNE_21_PARKING, TARIFF_2),
                calculate(june_21, jule_21, "июль 21", JULE_21, JULE_21_PARKING, TARIFF_3),
                calculate(jule_21, august_21, "август 21", AUGUST_21, AUGUST_21_PARKING, TARIFF_3),
                calculate(august_21, september_21, "сентябрь 21", SEPTEMBER_21, SEPTEMBER_21_PARKING, TARIFF_3),
                calculate(september_21, october_21, "октябрь 21", OCTOBER_21, OCTOBER_21_PARKING, TARIFF_3),
                calculate(october_21, november_21, "ноябрь 21", NOVEMBER_21, NOVEMBER_21_PARKING, TARIFF_3),
                calculate(november_21, december_21, "декабрь 21", DECEMBER_21, DECEMBER_21_PARKING, TARIFF_3),
                calculate(december_21, january_22, "январь 22", JANUARY_22, JANUARY_22_PARKING, TARIFF_3),
                calculate(january_22, february_22, "февраль 22", FEBRUARY_22, FEBRUARY_22_PARKING, TARIFF_3),
                calculate(february_22, march_22, "март 22", MARCH_22, MARCH_22_PARKING, TARIFF_3),
                calculate(march_22, april_22, "апрель 22", APRIL_22, APRIL_22_PARKING, TARIFF_3),
                calculate(april_22, may_22, "май 22", MAY_22, MAY_22_PARKING, TARIFF_3));

        log.info("---  ---");
        log.info("--- Результат по месяцам ---");
        BigDecimal sumDebit = BigDecimal.ZERO;
        BigDecimal sumCredit = BigDecimal.ZERO;

        for (Result r : result) {
            if (r.getCredit().equals(BigDecimal.ZERO)) {
                continue;
            }
            log.info("[{}] В МосЭнергоСбыт: [{}]₽, В квитанциях [{}], Разница [{}]", r.getMonth(), r.getDebit(), r.getCredit(), r.getCredit().subtract(r.getDebit()));
            sumDebit = sumDebit.add(r.getDebit());
            sumCredit = sumCredit.add(r.getCredit());
        }

        log.info("Итого, в МосЭнергоСбыт [{}]₽, в квитанциях [{}]₽, разница [{}]₽",
                sumDebit,
                sumCredit,
                sumCredit.subtract(sumDebit));

        createChart();
    }


    private String row(List<BigDecimal> list, String name) {
        List<String> result = new ArrayList<>();
        result.add(name);
        result.addAll(list.stream()
                .map(BigDecimal::toString)
                .map(item -> StringUtils.replace(item, ".", ","))
                .collect(Collectors.toList()));
        return String.join(";", result);
    }

    private void createChart() throws IOException {
        final List<String> months = List.of("Месяц/Потребитель",
                "апрель",
                "май",
                "июнь",
                "июль",
                "август",
                "сентябрь",
                "октябрь",
                "ноябрь",
                "декабрь",
                "январь_21",
                "февраль_21",
                "март_21",
                "апрель_21",
                "май_21",
                "июнь_21",
                "июль_21",
                "август_21",
                "сентябрь_21",
                "октябрь_21",
                "ноябрь_21",
                "декабрь_21",
                "январь_22",
                "февраль_22",
                "март_22",
                "апрель_22",
                "май_22");
        List<String> linesValue = new ArrayList<>();
        linesValue.add(String.join(";", months));
        linesValue.add(row(chart.getOffice(), "Офисы k=1"));
        linesValue.add(row(chart.getHouse(), "Дом k=80"));
        linesValue.add(row(chart.getParking(), "Паркинг k=40"));
        linesValue.add(row(chart.getItp(), "ИТП k=15"));
        Files.write(Paths.get("etc/chartValue.csv"), linesValue);

        List<String> linesMoney = new ArrayList<>();
        linesMoney.add(String.join(";", months));
        linesMoney.add(row(chart.getOfficeMoney(), "Офисы k=1"));
        linesMoney.add(row(chart.getHouseMoney(), "Дом k=80"));
        linesMoney.add(row(chart.getParkingMoney(), "Паркинг k=40"));
        linesMoney.add(row(chart.getItpMoney(), "ИТП k=15"));
        Files.write(Paths.get("etc/chartMoney.csv"), linesMoney);

        List<String> linesTotalExpose = new ArrayList<>();
        linesTotalExpose.add(String.join(";", months));
        linesTotalExpose.add(row(chart.getExposeHouse(), "Дом (Выставили)"));
        linesTotalExpose.add(row(chart.getTotalHouseMoney(), "Дом (Оплатили)"));
        linesTotalExpose.add(row(chart.getExposeParking(), "Паркинг (Выставили)"));
        linesTotalExpose.add(row(chart.getParkingMoney(), "Паркинг (Оплатили)"));
        Files.write(Paths.get("etc/chartTotalMoney.csv"), linesTotalExpose);
    }

    private Result calculate(Map<String, Counter> startMonth, Map<String, Counter> endMonth, String month, BigDecimal exposedHouse, BigDecimal exposedParking, TariffValue tariff) {
        log.info("\n");
        log.info("--- {} --- {}", month, tariff);

        final CounterValue offices3 = value(startMonth, endMonth, OFFICE_3, OFFICE_K);
        log.info("Офисы-3: T1=[{}], T2=[{}], T3=[{}]", offices3.getT1(), offices3.getT2(), offices3.getT3());
        final CounterValue offices5 = value(startMonth, endMonth, OFFICE_5, OFFICE_K);
        log.info("Офисы-5: T1=[{}], T2=[{}], T3=[{}]", offices5.getT1(), offices5.getT2(), offices5.getT3());
        final CounterValue offices8 = value(startMonth, endMonth, OFFICE_8, OFFICE_K);
        log.info("Офисы-8: T1=[{}], T2=[{}], T3=[{}]", offices8.getT1(), offices8.getT2(), offices8.getT3());

        final CounterValue inOffice = value(startMonth, endMonth, IN_OFFICE, OFFICE_K);
        final CounterValue officesSplit = value(startMonth, endMonth, OFFICE_SPLIT, OFFICE_K);
        final BigDecimal subtract = inOffice.getT1().subtract(officesSplit.getT1());
        log.info("Внимание потеря на офисах: IN_OFFICE [{}]КВт – OFFICE_SPLIT [{}]КВт = [{}]КВт, [{}]₽", inOffice.getT1(), officesSplit.getT1(), subtract, subtract.multiply(tariff.getT3()));
        CounterValue inOfficeT3 = CounterValue.builder()
                .t1(BigDecimal.ZERO)
                .t2(BigDecimal.ZERO)
                .t3(inOffice.getT1())
                .build();
        log.info("[k=1] Офисы: [{}]КВт, [{}]₽", inOffice.getT1(), money(inOfficeT3, tariff));
        chart.getOffice().add(inOffice.sum());

        final CounterValue inHouse = value(startMonth, endMonth, IN_HOUSE, HOUSE_K);
        log.info("[k=80] Дом: [{}]КВт, [{}]₽", inHouse, money(inHouse, tariff));
        chart.getHouse().add(inHouse.sum());

        final CounterValue inParking = value(startMonth, endMonth, IN_PARKING, PARKING_K);
        log.info("[k=40] Паркинг: [{}]КВт, [{}]₽", inParking, money(inParking, tariff));
        chart.getParking().add(inParking.sum());

        final CounterValue inItp = value(startMonth, endMonth, IN_ITP, ITP_K);
        log.info("[k=15] ИТП: [{}]КВт, [{}]₽", inItp, money(inItp, tariff));
        chart.getItp().add(inItp.sum());

        log.info("Валидация индивидуальных счётчиков");
        initIndividualCounters(endMonth);
        validation(startMonth, endMonth, INDIVIDUAL_COUNTERS, month);

        final CounterValue flats = value(startMonth, endMonth, INDIVIDUAL_COUNTERS, ONE_K);
        log.info("Квартиры: [{}]КВт", flats);
        chart.getFlats().add(flats.sum());

        final CounterValue commonHouseAndItp = inHouse.sub(flats).add(inItp);
        log.info("Экс.усл: (дом - квартиры + итп) [{}]КВт", inHouse.sub(flats).add(inItp));
        chart.getCommon().add(commonHouseAndItp.sum());

        final BigDecimal total = money(inOfficeT3, tariff)
                .add(money(inHouse, tariff))
                .add(money(inParking, tariff))
                .add(money(inItp, tariff));
        final BigDecimal totalExposed = exposedHouse.add(exposedParking);
        log.info("[{}] В МосЭнергоСбыт [{}]₽, в квитанциях [{}]₽, разница [{}]₽", month, total, totalExposed, totalExposed.subtract(total));

        chart.getExposeHouse().add(exposedHouse);
        chart.getExposeParking().add(exposedParking);

        return Result.builder()
                .month(month)
                .credit(totalExposed)
                .debit(total)
                .build();
    }

    private BigDecimal money(CounterValue value, TariffValue tariff) {
        return value.multiply(tariff).sum();
    }

    private void initIndividualCounters(Map<String, Counter> map) {
        final Set<String> all = new HashSet<>(map.keySet());
        all.removeAll(COMMON_COUNTERS);
        INDIVIDUAL_COUNTERS.clear();
        INDIVIDUAL_COUNTERS.addAll(all);
    }

    private boolean validation(Map<String, Counter> start, Map<String, Counter> end, Set<String> counters, String month) {
        List<Counter> counterResult = new ArrayList<>();
        for (String number : counters) {
            final BigDecimal valueT1 = value(start, end, number, TariffEnum.T1);
            final BigDecimal valueT2 = value(start, end, number, TariffEnum.T2);
            final BigDecimal valueT3 = value(start, end, number, TariffEnum.T3);
            info(number, valueT1);
            info(number, valueT2);
            info(number, valueT3);
            final Counter counter = start.get(number);
            if (counter == null) {
                log.info("Counter with number [{}] is null", counter);
            }
            counterResult.add(Counter.builder()
                    .flat(counter != null ? counter.getFlat() : "***")
                    .number(number)
                    .t1(valueT1)
                    .t2(valueT2)
                    .t3(valueT3)
                    .total(valueT1.add(valueT2).add(valueT3))
                    .build());
        }
        counterResult.sort(Comparator.comparing(Counter::getTotal));
        List<String> lines = counterResult.stream()
                .map(Counter::toString)
                .collect(Collectors.toList());

        try {
            Files.write(Paths.get("etc/" + month + ".txt"), lines);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return false;
    }

    private boolean info(String number, BigDecimal value) {
        boolean valid = true;
        final BigDecimal MAX = BigDecimal.valueOf(700);
        final BigDecimal MIN = BigDecimal.ZERO;
        if (value.compareTo(MAX) > 0) {
            log.error("!!! Counter [{}], value [{}] > [{}]", number, value, MAX);
            valid = false;
        }
        if (value.compareTo(MIN) < 0) {
            log.error("!!! Counter [{}], value [{}] < [{}]", number, value, MIN);
            valid = false;
        }
        return valid;
    }


    private CounterValue value(Map<String, Counter> start, Map<String, Counter> end, Set<String> counters, BigDecimal k) {
        final CounterValue sum = new CounterValue();
        for (String number : counters) {
            sum.setT1(sum.getT1().add(value(start, end, number, TariffEnum.T1)));
            sum.setT2(sum.getT2().add(value(start, end, number, TariffEnum.T2)));
            sum.setT3(sum.getT3().add(value(start, end, number, TariffEnum.T3)));
        }
        sum.setT1(sum.getT1().multiply(k));
        sum.setT2(sum.getT2().multiply(k));
        sum.setT3(sum.getT3().multiply(k));
        return sum;
    }

    private BigDecimal value(Map<String, Counter> start, Map<String, Counter> end, String counter, TariffEnum tariff) {
        if (end.get(counter) == null) {
            log.warn("xxx End Counter [{}] [{}] not found", counter, tariff);
            return BigDecimal.ZERO;
        }
        if (start.get(counter) == null) {
            log.warn("xxx Start Counter [{}] [{}] not found", counter, tariff);
            return BigDecimal.ZERO;
        }
        return switch (tariff) {
            case T1 -> end.get(counter).getT1().subtract(start.get(counter).getT1());
            case T2 -> end.get(counter).getT2().subtract(start.get(counter).getT2());
            case T3 -> end.get(counter).getT3().subtract(start.get(counter).getT3());
        };
    }

    private boolean isEqualsCounterSize(List<Map<String, Counter>> maps) {
        Set<Integer> counts = new HashSet<>();
        for (Map<String, Counter> map : maps) {
            counts.add(map.size());
        }
        if (counts.size() > 1) {
            log.warn("Количество счётчиков отличается в месяцах");
            return true;
        }
        return false;
    }


    public Map<String, Counter> read(String fileName) throws IOException {
        final int FLAT = 3;
        final int NUMBER = 4;
        final int DATE = 5;
        final int TOTAL = 6;
        final int T1 = 7;
        final int T2 = 8;
        final int T3 = 9;
        Map<String, Counter> counters = new LinkedHashMap<>();
        File myFile = new File(fileName);
        FileInputStream fis = new FileInputStream(myFile);
        XSSFWorkbook workbook = new XSSFWorkbook(fis);
        XSSFSheet sheet = workbook.getSheetAt(0);

        for (Row row : sheet) {
            final String flat = row.getCell(FLAT).getStringCellValue().trim();
            Cell cell = row.getCell(NUMBER);
            final String number = row.getCell(NUMBER).getStringCellValue().trim();
            final LocalDateTime date = row.getCell(DATE).getLocalDateTimeCellValue();
            final BigDecimal t1 = BigDecimal.valueOf(row.getCell(T1).getNumericCellValue()).setScale(3, RoundingMode.HALF_UP);
            final BigDecimal t2 = BigDecimal.valueOf(row.getCell(T2).getNumericCellValue()).setScale(3, RoundingMode.HALF_UP);
            final BigDecimal t3 = BigDecimal.valueOf(row.getCell(T3).getNumericCellValue()).setScale(3, RoundingMode.HALF_UP);
            final BigDecimal total = BigDecimal.valueOf(row.getCell(TOTAL).getNumericCellValue()).setScale(3, RoundingMode.HALF_UP);
            final Counter counter = Counter.builder()
                    .flat(flat)
                    .number(number)
                    .date(date)
                    .t1(t1)
                    .t2(t2)
                    .t3(t3)
                    .total(total)
                    .build();
            counters.put(number, counter);
        }
        log.info("Read [{}], counters [{}] is done", fileName, counters.size());
        return counters;
    }

}
