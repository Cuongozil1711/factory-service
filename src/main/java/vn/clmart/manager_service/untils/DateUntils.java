package vn.clmart.manager_service.untils;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUntils {

    public static final String FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";

    public static Date minDate() {
        try {
            String MIN_DATE = "1000-01-01T00:00:00+0700";
            return new SimpleDateFormat(FORMAT).parse(MIN_DATE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Date getStartOfDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DATE);
        cal.set(year, month, day, 0, 0, 0);
        return new Date(cal.getTime().getTime());
    }

    public static Date getEndOfDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DATE);
        cal.set(year, month, day, 23, 59, 59);
        return new Date(cal.getTime().getTime());
    }

    public static Date getStartOfDateMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        cal.set(year, month, cal.getActualMinimum(Calendar.DATE), 0, 0, 0);
        return cal.getTime();
    }

    public static Date getEndOfDateMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        cal.set(year, month, cal.getActualMaximum(Calendar.DATE), 23, 59, 59);
        return cal.getTime();
    }

    public static Date maxDate() {
        try {
            String MAX_DATE = "9999-12-31T23:59:59+0700";
            return new SimpleDateFormat(FORMAT).parse(MAX_DATE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Calendar toDate(Date date) {
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            return cal;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
