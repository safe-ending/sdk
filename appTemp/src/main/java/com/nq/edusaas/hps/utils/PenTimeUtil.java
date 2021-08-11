package com.nq.edusaas.hps.utils;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

/**
 * author : LiQiu
 * e-mail : lq@eningqu.com
 * date   : 2018/12/20 21:55
 * desc   :
 * version: 1.0
 */
public class PenTimeUtil {
    private static final String TAG = "TimeUtil";

    public static final String FORMAT_DATE_EN = "yyyy-MM-dd";
    public static final String FORMAT_DATE_CN = "yyyy年MM月dd日";

    public static final String FORMAT_TIME_CN = "yyyy年MM月dd HH时mm分ss秒";
    public static final String FORMAT_TIME_CN_2 = "yyyy年MM月dd HH时mm分";
    public static final String FORMAT_TIME_CN_3 = "MM月dd日 HH:mm";
    public static final String FORMAT_TIME_EN = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT_TIME_EN_2 = "yyyy-MM-dd HH:mm";
    public static final String FORMAT_TIME_EN_3 = "MM-dd HH:mm";

    public static final String FORMAT_DAY_CN = "HH时mm分ss秒";
    public static final String FORMAT_DAY_CN_2 = "HH时mm分";
    public static final String FORMAT_DAY_EN = "HH:mm:ss";
    public static final String FORMAT_DAY_EN_2 = "HH:mm";
    public static final String FORMAT_DAY_EN_3 = "hh:mm:ss";

    public static final String FORMAT_DAY_ASR = "MM月dd日";
    public static final String FORMAT_DAY_ASR2 = "MM-dd HH:mm";

    public static final String FORMAT_TIME = "yyyy-MM-dd HH:mm:ss.SSS";

    private static final SimpleDateFormat SDF = new SimpleDateFormat();

    /**
     * 在之前
     */
    public static final int TIME_BEFORE = 1;
    /**
     * 在中间
     */
    public static final int TIME_ING = 2;
    /**
     * 在之后
     */
    public static final int TIME_AFTER = 3;

    public static String recordAsrInfoTime(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_TIME_CN_2);
        sdf.setTimeZone(new SimpleTimeZone(0, "GMT"));
        return sdf.format(time);
    }

//    public static String recordTime(long timestamp) {
//        long time = timestamp / 1000;
//        if (time < 10) {
//            return "00:0" + time;
//        } else if (time < 60) {
//            return "00:" + time;
//        } else if (time < 10 * 60) {
//            return "0" + time / 60 + ":" + time % 60;
//        } else if (time < 60 * 60) {
//            return time / 60 + ":" + time % 60;
//        } else {
//            return time / 60 / 60 + ":" + time / 60 % 60 + ":" + time % (3600) % 60;
//        }
//    }

    public static String recordTime(long timestamp) {
        long second = timestamp / 1000;
        if (second <= 0) {
            return "00:00";
        }

        long hours = second / (60 * 60);
        if (hours > 0) {
            second -= hours * (60 * 60);
        }

        long minutes = second / 60;
        if (minutes > 0) {
            second -= minutes * 60;
        }

        String s = minutes >= 10 ? (minutes + "") : ("0" + minutes);
        String s1 = second >= 10 ? (second + "") : ("0" + second);
        if (hours <= 0) {
            return s + ":" + s1;
        } else {
            return (hours >= 10 ? (hours + "")
                    : ("0" + hours) + ":" + s + ":"
                    + s1);
        }
    }

    public static String asrRecordTime(long timestamp) {
        SimpleDateFormat SDF = new SimpleDateFormat(FORMAT_DAY_ASR);
        SDF.setTimeZone(new SimpleTimeZone(0, "GMT"));
        return SDF.format(timestamp);
    }

    public static String asrRecordTime2(long timestamp) {
        SimpleDateFormat SDF = new SimpleDateFormat(FORMAT_DAY_ASR2);
        SDF.setTimeZone(TimeZone.getDefault());
        return SDF.format(timestamp);
    }

    /**
     * string型时间转换
     *
     * @param timeFormat 时间格式
     * @param timestamp  时间
     * @return 刚刚  x分钟  小时前  ...
     */
    public static String convertTime(String timeFormat, long timestamp) {
        try {
            Date date = new Date();
            date.setTime(timestamp);

            return format(timeFormat, date);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, e.getMessage());
            return "";
        }
    }

    private static String format(String timeFormat, Date date) {
        SDF.setTimeZone(TimeZone.getDefault());
        SDF.applyPattern(timeFormat);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(timeFormat);

        return simpleDateFormat.format(date);
    }

    /**
     * 计算上一个时间离当前时间间隔
     *
     * @param timestamp 时间
     * @return 刚刚  x分钟  一天内  ...
     */
    public static String intervalTime(long timestamp) {
        return intervalTime(timestamp, false);
    }

    /**
     * 计算上一个时间离当前时间间隔
     *
     * @param timestamp    时间
     * @param includeAfter 时间
     * @return 刚刚  x分钟  一天内  ...
     */
    public static String intervalTime(long timestamp, boolean includeAfter) {
        String timeStr;

        long interval = (System.currentTimeMillis() - timestamp) / 1000;
        if (!includeAfter || interval >= 0) {
            if (interval <= 60) { //1分钟内 服务端的时间 可能和本地的有区别 所以小于0的 对于这个情况全部都显示刚刚
                timeStr = "刚刚";
            } else if (interval < 60 * 60) { // 1小时内
                timeStr = interval / 60 + "分钟前";
            } else if (interval < 24 * 60 * 60) { // 一天内
                timeStr = interval / (60 * 60) + "小时前";
            } else if (interval < 30 * 24 * 60 * 60) { // 天前
                timeStr = interval / (24 * 60 * 60) + "天前";
            } else {
                Date date = new Date();
                date.setTime(timestamp);

                timeStr = format(FORMAT_DATE_CN, date);
            }
        } else {
            return intervalAfterTime(timestamp);
        }

        return timeStr;
    }

    /**
     * int型时间转换 比较距离结束
     *
     * @param timestamp 时间
     * @return 刚刚  x分钟  一天后  ...
     */
    public static String intervalAfterTime(long timestamp) {
        String timeStr;

        long interval = (timestamp - System.currentTimeMillis()) / 1000;
        if (interval <= 60) { //1分钟内 服务端的时间 可能和本地的有区别 所以小于0的 对于这个情况全部都显示刚刚
            timeStr = "刚刚";
        } else if (interval < 60 * 60) { // 1小时内
            timeStr = interval / 60 + "分钟后";
        } else if (interval < 24 * 60 * 60) { // 一天内
            timeStr = interval / (60 * 60) + "小时后";
        } else if (interval < 30 * 24 * 60 * 60) { // 天前
            timeStr = interval / (24 * 60 * 60) + "天后";
        } else if (interval < 12 * 30 * 24 * 60 * 60) { // 月前
            timeStr = interval / (30 * 24 * 60 * 60) + "月后";
        } else if (interval < 12 * 30 * 24 * 60 * 60 * 3) { // 年前
            timeStr = interval / (12 * 30 * 24 * 60 * 60) + "年后";
        } else {
            Date date = new Date();
            date.setTime(interval);

            timeStr = format(FORMAT_DATE_CN, date);
        }

        return timeStr;
    }

    /**
     * 将long型时间转为固定格式的时间字符串
     *
     * @param longTime 时间
     * @return {@link PenTimeUtil#FORMAT_TIME_EN}
     */
    public static String convertToTime(long longTime) {
        return convertToTime(FORMAT_DAY_EN, longTime);
    }

    /**
     * 将long型时间转为固定格式的时间字符串
     *
     * @param timeformat 时间格式
     * @param longTime   时间
     * @return timeformat
     */
    public static String convertToTime(String timeformat, long longTime) {
        Date date = new Date(longTime);
        return convertToTime(timeformat, date);
    }

    /**
     * 将long型时间转为固定格式的时间字符串
     *
     * @param timeformat 时间格式
     * @param longTime   时间
     * @return timeformat
     */
    public static String convertToDifftime(String timeformat, long longTime) {
        Date date = new Date(longTime);  //时间差需要注意，Date还是按系统默认时区，而format格式化处来的字符串是GMT，所以要重置时间差。
        SDF.setTimeZone(TimeZone.getTimeZone("GMT+0"));
        SDF.applyPattern(timeformat);
        return SDF.format(date);
    }

    /**
     * 将Date型时间转为固定格式的时间字符串
     *
     * @param timeformat 时间格式
     * @param date       时间
     * @return timeformat
     */
    public static String convertToTime(String timeformat, Date date) {
        return format(timeformat, date);
    }

    /**
     * 将Calendar型时间转为固定格式的时间字符串
     *
     * @param timeformat 时间格式
     * @param calendar   时间
     * @return timeformat
     */
    public static String convertToTime(String timeformat, Calendar calendar) {
        return format(timeformat, calendar.getTime());
    }

    /**
     * 将String类型时间转为long类型时间
     *
     * @param timeformat 解析格式
     * @param timestamp  yyyy-MM-dd HH:mm:ss
     * @return 时间
     */
    public static long covertToLong(String timeformat, String timestamp) {
        try {
            Date date = SDF.parse(timestamp);
            return date.getTime();
        } catch (ParseException e) {
            Log.e(TAG, e.getMessage());
            return -1;
        }
    }

    /**
     * long型时间转换
     *
     * @param longTime 长整型时间
     * @return 2013年7月3日 18:05(星期三)
     */
    public static String convertDayOfWeek(String timeFormat, long longTime) {
        Calendar c = Calendar.getInstance(); // 日历实例
        c.setTime(new Date(longTime));

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int date = c.get(Calendar.DATE);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        String h = hour > 9 ? String.valueOf(hour) : "0" + hour;
        int minute = c.get(Calendar.MINUTE);
        String m = minute > 9 ? String.valueOf(minute) : "0" + minute;
        return String.format(Locale.getDefault(), timeFormat, year, month + 1, date, h, m, converToWeek(c.get(Calendar.DAY_OF_WEEK)));
    }

    /**
     * 转换数字的星期为字符串的
     *
     * @param w 星期
     * @return 星期x
     */
    private static String converToWeek(int w) {
        String week = null;

        switch (w) {
            case 1:
                week = "星期日";
                break;
            case 2:
                week = "星期一";
                break;
            case 3:
                week = "星期二";
                break;
            case 4:
                week = "星期三";
                break;
            case 5:
                week = "星期四";
                break;
            case 6:
                week = "星期五";
                break;
            case 7:
                week = "星期六";
                break;
        }

        return week;
    }

    /**
     * 计算时间是否在区间内
     *
     * @param time  time
     * @param time1 time
     * @param time2 time
     * @return {@link PenTimeUtil#TIME_BEFORE}{@link PenTimeUtil#TIME_ING}{@link PenTimeUtil#TIME_AFTER}
     */
    public static int betweenTime(long time, long time1, long time2) {
        if (time1 > time2) {  //时间1大
            long testTime = time1;
            time1 = time2;
            time2 = testTime;
        }

        //已经过去
        if (time1 > time) {
            return TIME_BEFORE;
        } else if (time2 < time) {
            return TIME_AFTER;
        } else {
            return TIME_ING;
        }
    }

    /**
     * @param date
     * @param day  想要获取的日期与传入日期的差值 比如想要获取传入日期前四天的日期 day=-4即可
     * @return
     */
    public static Date getSomeDay(Date date, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, day);
        return calendar.getTime();
    }

    /**
     * 日期差天数、小时、分钟、秒数组
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static long[] getDisTime(Date startDate, Date endDate) {
        long timesDis = Math.abs(startDate.getTime() - endDate.getTime());
        long day = timesDis / (1000 * 60 * 60 * 24);
        long hour = timesDis / (1000 * 60 * 60) - day * 24;
        long min = timesDis / (1000 * 60) - day * 24 * 60 - hour * 60;
        long sec = timesDis / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60;
        return new long[]{day, hour, min, sec};
    }

    /**
     * 日期差天数
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static long getDisDay(Date startDate, Date endDate) {
        long[] dis = getDisTime(startDate, endDate);
        long day = dis[0];
        if (dis[1] > 0 || dis[2] > 0 || dis[3] > 0) {
            day += 1;
        }
        return day;
    }

    /**
     * 日期差文字描述
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static String getDisTimeStr(Date startDate, Date endDate) {
        long[] dis = getDisTime(startDate, endDate);
        return new StringBuilder().append(dis[0]).append("天").append(dis[1]).append("小时").append(dis[2]).append("分钟")
                .append(dis[3]).append("秒").toString();
    }

    public static String getTimeLength(int second) {
        if (second < 60) {
            return second + "秒";
        } else if (second < 60 * 60) {
            return second / 60 + "分" + second % 60 + "秒";
        } else {
            return second / 3600 + "小时" + second % 3600 / 60 + "分" + second % 3600 % 60 + "秒";
        }
    }
}
