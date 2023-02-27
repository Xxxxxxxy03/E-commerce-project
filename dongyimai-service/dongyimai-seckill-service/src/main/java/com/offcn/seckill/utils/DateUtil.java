package com.offcn.seckill.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DateUtil {

    //给定一个Date转换成指定格式的字符串
    public static String date2StrFull(Date date) {
        //创建一个日期格式化工具对象
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(date);
    }

    //把Date转换成年月日时的字符串
    public static String date2Str(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHH");
        return dateFormat.format(date);
    }

    //获取指定Date的   当前日期的 00:00:00的Date对象
    public static Date toDayStartHour(Date date) {
        //获取Calendar对象
        Calendar calendar = Calendar.getInstance();
        //把要调整日期时间对象关联到日历对象
        calendar.setTime(date);
        //拨动表盘
        //拨动时针
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        //拨动分针
        calendar.set(Calendar.MINUTE, 0);
        //拨动秒针
        calendar.set(Calendar.SECOND, 0);
        //拨动毫秒针
        calendar.set(Calendar.MILLISECOND, 0);

        //获取拨动后日期时间对象
        Date start = calendar.getTime();
        return start;
    }

    //给指定日期时间对象，递增N小时
    public static Date addDateHour(Date date, int hour) {
        //获取日历对象
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        //把时针向后拨动n个小时
        calendar.add(Calendar.HOUR, hour);//24小时制
        date = calendar.getTime();
        return date;
    }

    //获取指定数量N个时间段
    public static List<Date> getDates(int hours) {
        //创建集合
        List<Date> dates = new ArrayList<>();


        //把当前日期时间对象，波动到0点0分0秒
        Date date = toDayStartHour(new Date());
        //循环 n次
        for (int i = 0; i < hours; i++) {
            dates.add(addDateHour(date, i * 2));
        }
        return dates;
    }


    //获取当前时间段和往后的4个时间段
    public static List<Date> getDateMenus() {
        // 获取12个时间
        List<Date> dates = getDates(12);
        //获取当前系统时间
        Date now = new Date();
        //    遍历12个时间段，和当前系统时间本地对，看当前系统时间属于那个时间段
        for (Date date : dates) {
            if (date.getTime() <= now.getTime() && now.getTime() < addDateHour(date, 2).getTime()) {
                now = date;
                break;
            }
        }

        //处理要显示时间段
        List<Date> dateMenus = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            dateMenus.add(addDateHour(now, i * 2));
        }
        return dateMenus;
    }

    public static void main(String[] args) {
        // List<Date> dateMenus = DateUtil.getDateMenus();
        // for (Date dateMenu : dateMenus) {
        //     System.out.println(DateUtil.date2StrFull(dateMenu));
        // }
        // Date date = DateUtil.addDateHour(new Date(), 2);
        // System.out.println(date);
        //
        // SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        // try {
        //     Date date1 = simpleDateFormat.parse("2018-08-05");
        //     System.out.println(date1.getTime());
        // } catch (ParseException e) {
        //     e.printStackTrace();
        // }
        // List<Date> dates = getDates(12);
        // for (Date date : dates) {
        //     System.out.println(date2StrFull(date));
        // }
    }
}
