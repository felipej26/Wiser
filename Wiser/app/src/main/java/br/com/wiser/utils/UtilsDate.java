package br.com.wiser.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UtilsDate {

    public static final String DDMMYYYY = "dd/MM/yyyy";
    public static final String DDMMYYYY_HHMM = "dd/MM/yyyy HH:mm";
    public static final String DDMMYYYY_HHMMSS = "dd/MM/yyyy HH:mm:ss";
    public static final String HHMM = "HH:mm";
    public static final String YYYYMMDD_HHMMSSZ = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    public static String formatDate(Date data, String formatData) {
        return new SimpleDateFormat(formatData).format(data);
    }

    public static Date parseDateJson(String data) throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").parse(data.replaceAll("Z$", "+0000"));
    }
}
