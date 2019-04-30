package io.jyasonx.jsxpay.util;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.Date;
import java.util.Locale;

/**
 * Utility methods for formatting & parsing dates.
 */
public class DateUtils {

    // 1970.01.01 UTC (Unix System 1st day as well as in Java world)
    private static final ZonedDateTime UNIX_FIRST_DAY = ZonedDateTime
            .of(1970, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);

    private static final int BEGIN_INDEX = 0;
    private static final int DATE_LENGTH = 8;
    private static final int DATE_TIME_LENGTH = 14;
    private static final int MILLI = 1000;
    private static final int DAY_IN_SECONDS = 24 * 60 * 60;

    public static final DateTimeFormatter YEAR_MONTH = of("yyyyMM");
    public static final DateTimeFormatter P_YEAR_MONTH = of("yyyy-MM");
    public static final DateTimeFormatter DATE = of("yyyyMMdd");
    public static final DateTimeFormatter Y_DATE = of("yyMMdd");
    public static final DateTimeFormatter P_DATE = of("yyyy-MM-dd");
    public static final DateTimeFormatter D_DATE = of("yyyy.MM.dd");
    public static final DateTimeFormatter DATE_TIME = of("yyyyMMddHHmmss");
    public static final DateTimeFormatter P_DATE_TIME = of("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter DATE_TIME_S = of("yyyyMMddHHmmssSSS");
    public static final DateTimeFormatter P_DATE_TIME_S = of("yyyy-MM-dd HH:mm:ss.SSS");
    public static final DateTimeFormatter DATE_TIME_H = of("yyyyMMddHH");

    private static WeekFields WEEK_FIELDS = WeekFields.of(Locale.getDefault());

    private DateUtils() {
        // private constructor for util class...
    }

    /**
     * Creates an instance of {@code DateTimeFormatter} from pattern.
     *
     * @param format the date pattern
     * @return the {@code DateTimeFormatter}
     */
    public static DateTimeFormatter of(String format) {
        return DateTimeFormatter.ofPattern(format);
    }

    /**
     * Parses a date string that formatted as 'yyyyMMdd' to {@code LocalDate}.
     *
     * @param date the date string
     * @return the {@code LocalDate}
     */
    public static LocalDate parse(String date) {
        return date.length() > DATE_LENGTH
                ? LocalDate.parse(date.substring(BEGIN_INDEX, DATE_LENGTH), DATE)
                : LocalDate.parse(date, DATE);
    }

    /**
     * Parses a date string that formatted as 'yyyy-MM-dd' to {@code LocalDate}.
     *
     * @param date the date string
     * @return the {@code LocalDate}
     */
    public static LocalDate parsePretty(String date) {
        return LocalDate.parse(date, P_DATE);
    }

    /**
     * Parses a date time string that formatted as 'yyyyMMdd' to {@code LocalDateTime}.
     *
     * @param date the date time string
     * @return the {@code LocalDateTime}
     */
    public static LocalDateTime parseDate(String date) {
        return parse(date).atStartOfDay();
    }

    /**
     * Parses a date time string that formatted as 'yyyyMMddHHmmss' to {@code LocalDateTime}.
     *
     * @param dateTime the date time string
     * @return the {@code LocalDateTime}
     */
    public static LocalDateTime parseDateTime(String dateTime) {
        return dateTime.length() > DATE_TIME_LENGTH
                ? LocalDateTime.parse(dateTime.substring(BEGIN_INDEX, DATE_TIME_LENGTH), DATE_TIME)
                : LocalDateTime.parse(dateTime, DATE_TIME);
    }

    /**
     * Parses a date time string that formatted as 'yyyy-MM-dd HH:mm:ss' to {@code LocalDateTime}.
     *
     * @param dateTime the date time string
     * @return the {@code LocalDateTime}
     */
    public static LocalDateTime parsePrettyDateTime(String dateTime) {
        return LocalDateTime.parse(dateTime, P_DATE_TIME);
    }

    /**
     * Parses a date time string that formatted as 'yyyyMMddHH' to {@code LocalDateTime}.
     *
     * @param dateTime the date time string
     * @return the {@code LocalDateTime}
     */
    public static LocalDateTime parseDateTimeHour(String dateTime) {
        return LocalDateTime.parse(dateTime, DATE_TIME_H);
    }

    /**
     * Parses a date time string that formatted as 'yyyy-MM-dd HH:mm:ss.SSS' to {@code
     * LocalDateTime}.
     *
     * @param dateTime the date time string
     * @return the {@code LocalDateTime}
     */
    public static LocalDateTime parsePrettyPreciseDateTime(String dateTime) {
        return LocalDateTime.parse(dateTime, P_DATE_TIME_S);
    }

    /**
     * Gets the current date as a string.
     *
     * @return the date string formatted as 'yyyyMMdd'
     */
    public static String now() {
        return LocalDate.now().format(DATE);
    }

    /**
     * Gets the current date as a string.
     *
     * @return the date string formatted as 'yyyy-MM-dd'
     */
    public static String prettyNow() {
        return LocalDate.now().format(P_DATE);
    }

    /**
     * Gets the current date as a string.
     *
     * @return the date string formatted as 'yyyy_MM_dd'
     */
    public static String dotNow() {
        return LocalDate.now().format(D_DATE);
    }

    /**
     * Gets the current time as a string.
     */
    public static String currentTimeStamp() {
        return String.valueOf(System.currentTimeMillis());
    }

    /**
     * Gets the current date & time as a string.
     *
     * @return the date string formatted as 'yyyyMMddHHmmss'
     */
    public static String nowTime() {
        return LocalDateTime.now().format(DATE_TIME);
    }

    /**
     * Gets the current date & time as a string.
     *
     * @return the date string formatted as 'yyyy-MM-dd HH:mm:ss'
     */
    public static String prettyNowTime() {
        return LocalDateTime.now().format(P_DATE_TIME);
    }

    /**
     * Gets the current date & time precisely as a string.
     *
     * @return the date string formatted as 'yyyyMMddHHmmssSSS'
     */
    public static String preciseNow() {
        return LocalDateTime.now().format(DATE_TIME_S);
    }

    /**
     * Gets the current date & time precisely as a string.
     *
     * @return the date string formatted as 'yyyy-MM-dd HH:mm:ss.SSS'
     */
    public static String prettyPreciseNow() {
        return LocalDateTime.now().format(P_DATE_TIME_S);
    }

    /**
     * Gets the current year as a string.
     */
    public static String getYear() {
        return String.valueOf(LocalDate.now().getYear());
    }

    /**
     * Gets the start time of today.
     *
     * @return the {@code LocalDateTime}
     */
    public static LocalDateTime getStartOfToday() {
        return LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
    }

    /**
     * Gets the end time of today.
     *
     * @return the {@code LocalDateTime}
     */
    public static LocalDateTime getEndOfToday() {
        return LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
    }

    /**
     * Gets the start time of date.
     *
     * @return the {@code LocalDateTime}
     */
    public static LocalDateTime getStartOfDate(LocalDate date) {
        return LocalDateTime.of(date, LocalTime.MIN);
    }

    /**
     * Gets the end time of date.
     *
     * @return the {@code LocalDateTime}
     */
    public static LocalDateTime getEndOfDate(LocalDate date) {
        return LocalDateTime.of(date, LocalTime.MAX);
    }

    /**
     * Get the local time by time string.
     */
    public static LocalTime parseLocalTime(String localTime) {
        return LocalTime.parse(localTime);
    }

    /**
     * Detects whether a day is weekend or not.
     *
     * @param dayOfWeek the day of week
     * @return true if it's weekend day, false otherwise
     */
    public static boolean isWeekend(DayOfWeek dayOfWeek) {
        return DayOfWeek.SATURDAY.equals(dayOfWeek) || DayOfWeek.SUNDAY.equals(dayOfWeek);
    }

    /**
     * Gets the date from epoch seconds.
     *
     * @param date the epoch seconds.
     * @return the {@code LocalDate}
     */
    public static LocalDate parseFromEpochSeconds(int date) {
        Clock clock = Clock.systemDefaultZone();
        ZoneOffset offset = clock.getZone().getRules().getOffset(clock.instant());

        // overflow caught later
        int epochSec = date + offset.getTotalSeconds();
        int epochDay = Math.floorDiv(epochSec, DAY_IN_SECONDS);

        return LocalDate.ofEpochDay(epochDay);
    }

    /**
     * Converts a {@code LocalDateTime} to {@code Date}.
     */
    public static Date toDate(LocalDateTime dateTime) {
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = dateTime.atZone(zone).toInstant();
        return Date.from(instant);
    }

    /**
     * Converts a {@code LocalDate} to {@code Date}.
     */
    public static Date toDate(LocalDate date) {
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = date.atStartOfDay().atZone(zone).toInstant();
        return Date.from(instant);
    }

    /**
     * Converts a {@link Date} to {@link LocalDateTime}.
     */
    public static LocalDateTime toLocalDateTime(Date date) {
        Instant instant = date.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        return LocalDateTime.ofInstant(instant, zone);
    }

    /**
     * Converts a {@code LocalDateTime} to seconds.
     */
    public static long getSeconds(LocalDateTime dateTime) {
        return toDate(dateTime).getTime() / MILLI;
    }

    /**
     * Formats {@link Date} to string according to the specific format passed in.
     *
     * @param date      {@link Date}
     * @param formatter refer to {@link DateTimeFormatter}
     * @return the formatted date string.
     */
    public static String formatDate(Date date, DateTimeFormatter formatter) {
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return localDate.format(formatter);
    }

    /**
     * Get the milliseconds (in long value) between 1970-01-01 UTC and @param time.
     */
    public static long getLong(LocalDateTime time) {
        // The future is the mill seconds between expected execution time with 1970-01-01 UTC.
        return Duration.between(DateUtils.UNIX_FIRST_DAY,
                time.atZone(ZoneId.systemDefault())).toMillis();
    }

    /**
     * Get the first day of the month.
     */
    public static LocalDateTime getFirstDayOfMonth() {
        LocalDate initial = LocalDate.now();
        LocalDate start = initial.withDayOfMonth(1);
        return LocalDateTime.of(start, LocalTime.MIN);
    }

    /**
     * Get the last day of the month.
     */
    public static LocalDateTime getLastDayOfMonth() {
        LocalDate initial = LocalDate.now();
        LocalDate end = initial.withDayOfMonth(initial.lengthOfMonth());
        return LocalDateTime.of(end, LocalTime.MAX);
    }

    /**
     * Get the first day of the year.
     */
    public static LocalDateTime getFirstDayOfYear() {
        LocalDate initial = LocalDate.now();
        LocalDate start = initial.withDayOfYear(1);
        return LocalDateTime.of(start, LocalTime.MIN);
    }

    /**
     * Get the last day of the year.
     */
    public static LocalDateTime getLastDayOfYear() {
        LocalDate initial = LocalDate.now();
        LocalDate end = initial.withDayOfYear(initial.lengthOfYear());
        return LocalDateTime.of(end, LocalTime.MAX);
    }

    /**
     * 获取当前时间是一年中的第几周.
     */
    public static int getWeekBasedYear(LocalDate date) {
        return date.get(WEEK_FIELDS.weekOfYear());
    }

    /**
     * Date to LocalDateTime.
     */
    public static LocalDateTime date2LocalTime(Date date) {
        if (date == null) {
            return null;
        }
        Instant instant = date.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        return LocalDateTime.ofInstant(instant, zone);
    }

    public static Date localDateTime2Date(LocalDateTime localDateTime) {
        if (null == localDateTime) {
            return null;
        }
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
        return Date.from(zonedDateTime.toInstant());
    }

}
