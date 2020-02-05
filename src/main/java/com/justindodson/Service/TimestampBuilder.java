package com.justindodson.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class TimestampBuilder {
    private static final String DATE_FORMAT = "MM/dd/yyyy HH:mm:ss";
    private static final DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);

    private Date today;
    private long currentTimestamp;

    public TimestampBuilder() {
        this.today = new Date();
        this.currentTimestamp = today.getTime() / 1000L;
        getNextFriday();
    }


    public Date getNextFriday() {
        int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        Date nextFridayDate = new Date();
        Calendar cal = new GregorianCalendar();
        cal.setTime(nextFridayDate);

        if(currentDay <= 5) {
            int daysToAdd = 6 - currentDay;
            cal.add(Calendar.DATE, daysToAdd);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
        } else if(currentDay == 6) {
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
        } else  if(currentDay == 7) {
            cal.add(Calendar.DATE, 6);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
        }

        nextFridayDate = cal.getTime();

        Calendar cal1 = Calendar.getInstance();
        cal1.set(2020, 0,9, 18, 00, 00);
        Date date = cal1.getTime();
        System.out.println(dateFormat.format(date));
        System.out.println(date.getTime() / 1000L);

        Instant instant = Instant.ofEpochSecond(1578614400);
        Date time = Date.from(instant);
        System.out.println(dateFormat.format(time));

        return nextFridayDate;
    }
}
