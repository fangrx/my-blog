package com.nonelonely.common.utils;

import com.alibaba.fastjson.JSON;

import org.springframework.util.StringUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * 日期工具类
 *
 * @author nonelonely
 * @date 2020/6/9 10:46 上午
 */
public class DateUtils {

    private DateUtils() {
    }


    /**
     * 时间戳转字符串
     *
     * @param millis 时间戳
     * @return 格式字符串
     */
    public static String longToStrDate(long millis) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return df.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault()));
    }

    /**
     * 转换UTC时间为北京时间
     *
     * @param utcDate
     * @return
     */
    public static String formatUTCDate(String utcDate, String format) {
        if (StringUtils.isEmpty(utcDate)) {
            return null;
        }
        if (StringUtils.isEmpty(format)) {
            format = "yyyy-MM-dd";
        }
        utcDate = utcDate.replace("Z", " UTC");
        SimpleDateFormat orgDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS Z");
        try {
            Date d = orgDateFormat.parse(utcDate);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
            return simpleDateFormat.format(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return utcDate;
    }


    /**
     * 判断时间是否在时间段内
     *
     * @param nowTime
     * @param beginTime
     * @param endTime
     * @return
     */
    public static boolean belongCalendar(Date nowTime, Date beginTime, Date endTime) {

        // ：转化开始时间为 凌晨时分秒 00:00:00
        beginTime = DateUtils.getTemp(beginTime.getTime());

        Calendar date = Calendar.getInstance();
        date.setTime(nowTime);
        Calendar begin = Calendar.getInstance();
        begin.setTime(beginTime);
        Calendar end = Calendar.getInstance();
        end.setTime(endTime);
        if (date.after(begin) && date.before(end)) {
            return true;
        } else if (nowTime.compareTo(beginTime) == 0 || nowTime.compareTo(endTime) == 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断某时间段属于上午下午还是全天
     *
     * @param start 开始时间
     * @param end   结束时间
     * @return 1 是上午  2 是下午  3 全天  0 错误
     * @throws ParseException
     */
    private static int checkForenoon(Date start, Date end) throws ParseException {
        String sday = dateFormatStr(start);
        Date middle = strFormatDate(sday + " 12:00:00"); //得到当天中午数据
        if (start.compareTo(middle) < 0 && end.compareTo(middle) == 0) { //上午 start:2017-09-07 00:00:00.0 end:2017-09-07 12:00:00.0
            return 1;
        }
        if (start.compareTo(middle) == 0 && end.compareTo(middle) > 0) { //下午 2016-08-05 12:00:00.0 end:2016-08-05 23:59:59.0
            return 2;
        }
        if (start.compareTo(middle) < 0 && end.compareTo(middle) > 0) {//全天 start:2017-09-06 00:00:00.0 end:2017-09-06 23:59:59.0
            return 3;
        }
        return 0;
    }

    /**
     * 日期格式化
     *
     * @param date return  格式化字符串如 2017-09-07
     * @param date
     */
    public static String dateFormatStr(Date date) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String time = format.format(date);
//		System.out.println("时间:" + time);
        return time;
    }


    /**
     * 字符串日期转为Date
     *
     * @param ld
     * @throws ParseException
     */
    private static Date strFormatDate(String ld) throws ParseException {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date lendDate = format.parse(ld);
//		System.out.println(lendDate);
        return lendDate;
    }

    /**
     * 增加或减少天数
     *
     * @param date
     * @param type
     * @param day
     * @return
     */
    public static Date addDateOneDay(Date date, int type, int day) {
        if (null == date) {
            return date;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(date);   //设置当前日期
        if (type == 1) {
            c.add(Calendar.DATE, day); //日期加N天

        } else {
            c.add(Calendar.DATE, -day); //日期减N天
        }
        date = c.getTime();


        return date;
    }

    /**
     * 增加或减少天数
     *
     * @param date
     * @param type
     * @param day
     * @return
     */
    public static String addDateOneDayStr(Date date, int type, int day) {

        Calendar c = Calendar.getInstance();
        c.setTime(date);   //设置当前日期
        if (type == 1) {
            c.add(Calendar.DATE, day); //日期加N天

        } else {
            c.add(Calendar.DATE, -day); //日期减N天
        }
        date = c.getTime();

        return dateFormatStr(date);
    }

    /**
     * 按照参数format的格式，日期转字符串
     *
     * @param date
     * @param format
     * @return
     */
    public static String date2Str(Date date, String format) {
        if (date != null) {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return sdf.format(date);
        } else {
            return "";
        }
    }


    /**
     * 获取当前时间 属于哪个时间段
     *
     * @param startTime
     * @return 1 ：上午  2：下午  3：晚上  0 : 未知
     */
    public static int checkDay(Date startTime) {
        SimpleDateFormat df = new SimpleDateFormat("HH");
        String str = df.format(startTime);
        //获取时间所属上午、下午、晚上
        int a = Integer.parseInt(str);
        if (a >= 0 && a <= 11) {
            return 1;
        } else if (a >= 12 && a <= 17) {
            return 2;
        }
        if (a >= 18 && a <= 24) {
            return 3;
        }
        return 0;
    }

    public static Date getTemp(Long nowTime) {
/*        long daySecond = 60 * 60 * 24;
        long dayTime = nowTime - (nowTime + 8 * 3600) % daySecond;
        return new Date(dayTime);*/

        long zero = nowTime - (nowTime + TimeZone.getDefault().getRawOffset()) % (1000 * 3600 * 24);
        return new Date(zero);
    }

    /**
     * 计算两个时间戳差的天数加一，同一天返回为1
     * @param startTimestamp 开始时间
     * @param endTimestamp 结束时间
     * @return
     */
    public static int getDifferentDays(Long startTimestamp, Long endTimestamp) {
        try {
            System.out.println("计算两个时间戳差的天数{}参数{}\"" + JSON.toJSONString(startTimestamp)+"=========="+ JSON.toJSONString(endTimestamp));

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            //转换成整天
            String startStr = formatter.format(startTimestamp);
            Date startDate = formatter.parse(startStr);
            String endStr = formatter.format(endTimestamp);
            Date endDate = formatter.parse(endStr);
            int days = (int)((endDate.getTime()-startDate.getTime())/(3600*1000*24));
            return Math.abs(days)+1;
        }catch (Exception e){
            System.out.println("时间计算天数差异常{}" + JSON.toJSONString(e));
           // throw new BusinessException(ResultEnum.BUSINESS_ERROR);
            return 0;
        }
    }
}
