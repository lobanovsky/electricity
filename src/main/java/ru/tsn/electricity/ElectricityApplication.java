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
    private static final TariffValue TARIFF_4 = new TariffValue(BigDecimal.valueOf(6.52), BigDecimal.valueOf(1.88), BigDecimal.valueOf(5.43));
    private static final TariffValue TARIFF_5 = new TariffValue(BigDecimal.valueOf(8.23), BigDecimal.valueOf(2.62), BigDecimal.valueOf(5.66));

    private Chart chart = new Chart();

    //Выставили платежки в домовладельце
    public static final BigDecimal SEPTEMBER_23 = BigDecimal.valueOf(189760.84);
    public static final BigDecimal OCTOBER_23 = BigDecimal.valueOf(187402.5);
    public static final BigDecimal NOVEMBER_23 = BigDecimal.valueOf(190256.6);
    public static final BigDecimal DECEMBER_23 = BigDecimal.valueOf(242447.71);
    public static final BigDecimal JANUARY_24 = BigDecimal.valueOf(227244.81);
    public static final BigDecimal FEBRUARY_24 = BigDecimal.valueOf(238946.97);
    public static final BigDecimal MARCH_24 = BigDecimal.valueOf(202240.89);
    public static final BigDecimal APRIL_24 = BigDecimal.valueOf(193014.15);
    public static final BigDecimal MAY_24 = BigDecimal.valueOf(189553.10);

    public static final BigDecimal SEPTEMBER_23_PARKING = BigDecimal.valueOf(20442.22);
    public static final BigDecimal OCTOBER_23_PARKING = BigDecimal.valueOf(24946.69);
    public static final BigDecimal NOVEMBER_23_PARKING = BigDecimal.valueOf(25492.06);
    public static final BigDecimal DECEMBER_23_PARKING = BigDecimal.valueOf(34457.20);
    public static final BigDecimal JANUARY_24_PARKING = BigDecimal.valueOf(34647.20);
    public static final BigDecimal FEBRUARY_24_PARKING = BigDecimal.valueOf(38864.67);
    public static final BigDecimal MARCH_24_PARKING = BigDecimal.valueOf(34901.71);
    public static final BigDecimal APRIL_24_PARKING = BigDecimal.valueOf(31612.16);
    public static final BigDecimal MAY_24_PARKING = BigDecimal.valueOf(32593.21);

    public static void main(String[] args) {
        SpringApplication.run(ElectricityApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        final Map<String, Counter> january_24 = read("etc/2024-01.xlsx");
        final Map<String, Counter> february_24 = read("etc/2024-02.xlsx");
        final Map<String, Counter> march_24 = read("etc/2024-03.xlsx");
        final Map<String, Counter> april_24 = read("etc/2024-04.xlsx");
        final Map<String, Counter> may_24 = read("etc/2024-05.xlsx");


        final List<Map<String, Counter>> allCounters = List.of(
                january_24,
                february_24,
                march_24,
                april_24,
                may_24
        );

        if (isEqualsCounterSize(allCounters)) return;

        final List<Result> result = List.of(
                calculate(january_24, february_24, "февраль 24", FEBRUARY_24, FEBRUARY_24_PARKING, TARIFF_5),
                calculate(february_24, march_24, "март 24", MARCH_24, MARCH_24_PARKING, TARIFF_5),
                calculate(march_24, april_24, "апрель 24", APRIL_24, APRIL_24_PARKING, TARIFF_5),
                calculate(april_24, may_24, "май 24", MAY_24, MAY_24_PARKING, TARIFF_5)
        );

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
                "январь_24",
                "февраль_24",
                "март_24",
                "апрель_24",
                "май_24"
        );
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
            log.warn("Количество счётчиков отличается в месяцах: " + counts.size());
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
//            log.info("file:" + fileName + "flat: " + flat + " cellType: " + cell.getCellType() + " cellValue: " + cell.getStringCellValue());
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
