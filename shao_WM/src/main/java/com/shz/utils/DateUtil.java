package com.shz.utils;

public class DateUtil {
    public static int dayOfMonth(String year, String month) {
        int thisYear = Integer.parseInt(year);
        int thisMonth = Integer.parseInt(month);
        switch (thisMonth) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                return 31;
            case 4:
            case 6:
            case 9:
            case 11:
                return 30;
            case 2:
                if (thisYear % 400 == 0 || (thisYear % 100 != 0 && thisYear % 4 == 0)) {
                    return 29;
                } else {
                    return 28;
                }
        }
        return 0;
    }
}
