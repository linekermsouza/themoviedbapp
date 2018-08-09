package com.udacity.lineker.themoviedb.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    public static String fromYYYYMMDDtoDDMMYYYY(String from) {
        SimpleDateFormat formatterCame = new SimpleDateFormat("yyyy-mm-dd");
        SimpleDateFormat formatterShow = new SimpleDateFormat("dd/MM/yyyy");

        String release = "";
        try {
            Date date = formatterCame.parse(from);
            release = formatterShow.format(date);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return release;
    }
}
