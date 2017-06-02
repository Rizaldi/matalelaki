package com.radiomatalelaki.util.database;

/**
 * Created by rianpradana on 5/26/17.
 */


import com.radiomatalelaki.api.Events;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EventDateUtils {

    private static final String DB_DATE_FORMAT = "yyyy-MM-dd HH:mm";

    /**
     * Gets start date.
     *
     * @param e          the event
     * @param dateFormat the date format
     * @return the start date
     * @throws ParseException the parse exception
     */
    public static Date getStartDate(Events e, String dateFormat) throws ParseException {
        SimpleDateFormat sdfInput = new SimpleDateFormat(dateFormat);
        return sdfInput.parse(e.getStart_date_time());
    }

    /**
     * Gets start date.
     *
     * @param e the event
     * @return the start date with default date format
     * @throws ParseException the parse exception
     */
    public static Date getStartDate(Events e) throws ParseException {
        return getStartDate(e, DB_DATE_FORMAT);
    }

    /**
     * Gets end date.
     *
     * @param e          the event
     * @param dateFormat the date format
     * @return the end date
     * @throws ParseException the parse exception
     */
    public static Date getEndDate(Events e, String dateFormat) throws ParseException {
        SimpleDateFormat sdfInput = new SimpleDateFormat(dateFormat);
        return sdfInput.parse(e.getEnd_date_time());
    }

    /**
     * Gets end date.
     *
     * @param e the event
     * @return the end date with default date format
     * @throws ParseException the parse exception
     */
    public static Date getEndDate(Events e) throws ParseException {
        return getEndDate(e, DB_DATE_FORMAT);
    }

    /**
     * Is same day.
     *
     * @param e          the event
     * @param dateFormat the date format
     * @return the boolean
     * @throws ParseException the parse exception
     */
    public static boolean isSameDay(Events e, String dateFormat) throws ParseException {
        Date d1 = getStartDate(e, dateFormat);
        Date d2 = getEndDate(e, dateFormat);
        SimpleDateFormat sdfOut = new SimpleDateFormat("DD");
        return sdfOut.format(d1).equals(sdfOut.format(d2));
    }

    /**
     * Is same day.
     *
     * @param e the event
     * @return the boolean
     * @throws ParseException the parse exception
     */
    public static boolean isSameDay(Events e) throws ParseException {
        return  isSameDay(e, DB_DATE_FORMAT);
    }

    /**
     * Is same month.
     *
     * @param e          the event
     * @param dateFormat the date format
     * @return the boolean
     * @throws ParseException the parse exception
     */
    public static boolean isSameMonth(Events e, String dateFormat) throws ParseException {
        Date d1 = getStartDate(e, dateFormat);
        Date d2 = getEndDate(e, dateFormat);
        SimpleDateFormat sdfOut = new SimpleDateFormat("MM");
        return sdfOut.format(d1).equals(sdfOut.format(d2));
    }

    /**
     * Is same month.
     *
     * @param e the event
     * @return the boolean
     * @throws ParseException the parse exception
     */
    public static boolean isSameMonth(Events e) throws ParseException {
        return  isSameMonth(e, DB_DATE_FORMAT);
    }

    /**
     * Is same year.
     *
     * @param e          the event
     * @param dateFormat the date format
     * @return the boolean
     * @throws ParseException the parse exception
     */
    public static boolean isSameYear(Events e, String dateFormat) throws ParseException {
        Date d1 = getStartDate(e, dateFormat);
        Date d2 = getEndDate(e, dateFormat);
        SimpleDateFormat sdfOut = new SimpleDateFormat("yyyy");
        return sdfOut.format(d1).equals(sdfOut.format(d2));
    }

    /**
     * Is same year.
     *
     * @param e the event
     * @return the boolean
     * @throws ParseException the parse exception
     */
    public static boolean isSameYear(Events e) throws ParseException {
        return  isSameYear(e, DB_DATE_FORMAT);
    }

    /**
     * Is same time.
     *
     * @param e          the event
     * @param dateFormat the date format
     * @return the boolean
     * @throws ParseException the parse exception
     */
    public static boolean isSameTime(Events e, String dateFormat) throws ParseException {
        Date d1 = getStartDate(e, dateFormat);
        Date d2 = getEndDate(e, dateFormat);
        SimpleDateFormat sdfOut = new SimpleDateFormat("HH:mm");
        return sdfOut.format(d1).equals(sdfOut.format(d2));
    }

    /**
     * Is same time boolean.
     *
     * @param e the event
     * @return the boolean
     * @throws ParseException the parse exception
     */
    public static boolean isSameTime(Events e) throws ParseException {
        return  isSameTime(e, DB_DATE_FORMAT);
    }

    /**
     * Is same day month year time boolean.
     *
     * @param e          the event
     * @param dateFormat the date format
     * @return the boolean
     * @throws ParseException the parse exception
     */
    public static boolean isSameDayMonthYearTime(Events e, String dateFormat) throws ParseException {
        return isSameDay(e, dateFormat) && isSameMonth(e, dateFormat) && isSameYear(e, dateFormat) && isSameTime(e, dateFormat);
    }

    /**
     * Is same day month year time boolean.
     *
     * @param e the event
     * @return the boolean
     * @throws ParseException the parse exception
     */
    public static boolean isSameDayMonthYearTime(Events e) throws ParseException {
        return isSameDay(e, DB_DATE_FORMAT) && isSameMonth(e, DB_DATE_FORMAT) && isSameYear(e, DB_DATE_FORMAT) && isSameTime(e, DB_DATE_FORMAT);
    }

    /**
     * Is same day month year boolean.
     *
     * @param e          the event
     * @param dateFormat the date format
     * @return the boolean
     * @throws ParseException the parse exception
     */
    public static boolean isSameDayMonthYear(Events e, String dateFormat) throws ParseException {
        return isSameDay(e, dateFormat) && isSameMonth(e, dateFormat) && isSameYear(e, dateFormat);
    }

    /**
     * Is same day, month and year.
     *
     * @param e the e
     * @return the boolean
     * @throws ParseException the parse exception
     */
    public static boolean isSameDayMonthYear(Events e) throws ParseException {
        return isSameDay(e, DB_DATE_FORMAT) && isSameMonth(e, DB_DATE_FORMAT) && isSameYear(e, DB_DATE_FORMAT);
    }

    /**
     * Is same month and year.
     *
     * @param e          the event
     * @param dateFormat the date format
     * @return the boolean
     * @throws ParseException the parse exception
     */
    public static boolean isSameMonthYear(Events e, String dateFormat) throws ParseException {
        return isSameMonth(e, dateFormat) && isSameYear(e, dateFormat);
    }

    /**
     * Is same month and year.
     *
     * @param e the event
     * @return the boolean
     * @throws ParseException the parse exception
     */
    public static boolean isSameMonthYear(Events e) throws ParseException {
        return isSameMonth(e, DB_DATE_FORMAT) && isSameYear(e, DB_DATE_FORMAT);
    }

    /**
     * Gets pretty printed string date.
     *
     * @param e          the event
     * @param dateFormat the date format
     * @return the pretty string
     * @throws ParseException the parse exception
     */
    public static String getPrettyString(Events e, String dateFormat) throws ParseException {
        SimpleDateFormat out;
        String strOut;
        if (isSameDayMonthYearTime(e, dateFormat)) {
            out = new SimpleDateFormat("EEEE, MMMM d, yyyy HH:mm");
            strOut = out.format(getStartDate(e, dateFormat));
        } else if (isSameDayMonthYear(e, dateFormat)) {
            out = new SimpleDateFormat("EEEE, MMMM d, yyyy");
            strOut = out.format(getStartDate(e, dateFormat));
            out = new SimpleDateFormat("HH:mm");
            strOut += " "+out.format(getStartDate(e, dateFormat));
            strOut += " - "+out.format(getEndDate(e, dateFormat));
        } else if (isSameMonthYear(e, dateFormat)) {
            out = new SimpleDateFormat("MMMM, yyyy");
            strOut = out.format(getStartDate(e, dateFormat));
            out = new SimpleDateFormat("EEEE d HH:mm");
            strOut = out.format(getStartDate(e, dateFormat)) + " - " + out.format(getEndDate(e, dateFormat)) + " " + strOut;
        } else if (isSameYear(e, dateFormat)) {
            out = new SimpleDateFormat("yyyy");
            strOut = out.format(getStartDate(e, dateFormat));
            out = new SimpleDateFormat("EEEE d MMMM HH:mm");
            strOut = out.format(getStartDate(e, dateFormat)) + " - " + out.format(getEndDate(e, dateFormat)) + " " + strOut;
        } else {
            out = new SimpleDateFormat("EEEE, MMMM d, yyyy HH:mm");
            strOut = out.format(getStartDate(e, dateFormat)) + " - " + out.format(getEndDate(e, dateFormat));
        }
        return capitalizeString(strOut);
    }

    /**
     * Gets pretty printed string date.
     *
     * @param e the event
     * @return the pretty string
     * @throws ParseException the parse exception
     */
    public static String getPrettyString(Events e) throws ParseException {
        return capitalizeString(getPrettyString(e, DB_DATE_FORMAT));
    }

    /* Capitalize the first character of each word */
    private static String capitalizeString(String string) {
        char[] chars = string.toLowerCase().toCharArray();
        boolean found = false;
        for (int i = 0; i < chars.length; i++) {
            if (!found && Character.isLetter(chars[i])) {
                chars[i] = Character.toUpperCase(chars[i]);
                found = true;
            } else if (Character.isWhitespace(chars[i]) || chars[i]=='.' || chars[i]=='\'') { // You can add other chars here
                found = false;
            }
        }
        return String.valueOf(chars);
    }

}

