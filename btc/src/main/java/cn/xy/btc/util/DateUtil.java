package cn.xy.btc.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * 	@author xuyao
 * */
public class DateUtil {
	
	public enum DateUnit{
		minute,hour,day,month,year
	}
	
	public static final String LONG_PATTERN = "yyyy-MM-dd HH:mm:ss";
	public static final String STR_DATE_PATTERN = "yyyy-MM-dd";
	public static final String STR_YM_PATTERN = "yyyyMM";

	
	public static String getNowformatLongPattern() {
		SimpleDateFormat dateformat=new SimpleDateFormat(LONG_PATTERN);
		return dateformat.format(new Date());
	}
	
	public static String formatLongPattern(Date date) {
		return format(date, LONG_PATTERN);
	}

	public static String format(Date date, String pattern) {
		if (date == null)
			return null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(pattern);
			sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
			return sdf.format(date);
		} catch (Exception e) {
			return null;
		}
	}
	
	public static String formatNYR(Date date) {
		if (date == null)
			return null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(STR_DATE_PATTERN);
			sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
			return sdf.format(date);
		} catch (Exception e) {
			return null;
		}
	}
	
	
	public static Date getTodayDay() {
		return new Date();
	}
	

	/**
	 * 返回date用int表示的日期(yyyyMMdd)
	 * @param date
	 * @return
	 */
	public static Integer getDay2Int(Date date) {
		if (date == null)
			return null;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int day = 0;
		day += calendar.get(Calendar.YEAR) * 10000;
		day += (calendar.get(Calendar.MONTH) + 1) * 100;
		day += calendar.get(Calendar.DAY_OF_MONTH);
		return day;
	}
	
	/**
	 * 返回date用int表示的时间(HHmmss)
	 * @param date
	 * @return
	 */
	public static Integer getTime2Int(Date date) {
		if (date == null)
			return null;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int day = 0;
		day += calendar.get(Calendar.HOUR_OF_DAY) * 10000;
		day += calendar.get(Calendar.MINUTE) * 100;
		day += calendar.get(Calendar.SECOND);
		return day;
	}

	/**
	 * 根据int返回具体的日期
	 * @param date
	 * @return
	 */
	public static Date getDateFromNumber(long date) {
		int _date = (int) date;
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, _date / 10000);
		calendar.set(Calendar.MONTH, _date % 10000 / 100 - 1);
		calendar.set(Calendar.DAY_OF_MONTH, _date % 100);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		return calendar.getTime();
	}

	/**
	 * 设置某天的最开始时间
	 * @param date
	 * @return
	 */
	public static Date getFirestTimeOfDay(Date date) {
		if (date == null)
			return null;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}

	/**
	 * 设置某天的最晚时间
	 * @param date
	 * @return
	 */
	public static Date getEndTimeOfDay(Date date) {
		if (date == null)
			return null;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		return calendar.getTime();
	}

	/**
	 * 比较date是否介于from和to之间(包含from和to)
	 * @param date
	 * @param from
	 * @param to
	 * @return
	 */
	public static boolean isBetween(Date date, Date from, Date to) {
		if (date == null || (from == null && to == null))
			return false;

		if (from == null)
			return date.compareTo(to) <= 0;
		if (to == null)
			return date.compareTo(from) >= 0;

		return date.compareTo(from) >= 0 && date.compareTo(to) <= 0;
	}

	public static Date getMonthBegin(Date date) {
		if (date == null)
			return null;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		return calendar.getTime();
	}

	public static Date getYearBegin(Date date) {
		if (date == null)
			return null;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.MONTH, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		return calendar.getTime();
	}

	public static Date getWeekBegin(Date date) {
		if (date == null)
			return null;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		return calendar.getTime();
	}

	/** 得到一天是一年中的第几周的方法 */
	public static int getWeekOfYear(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;
		int week = cal.get(Calendar.WEEK_OF_YEAR);
		if (month == 12 && week == 1) {
			year++;
		}
		return year * 100 + week;
	}

	/** 得到一天是一年中的第几个月份的方法 */
	public static int getMonthOfYear(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;
		return year * 100 + month;
	}
	
	/** 
	 * 	得到当前周的第一天的日期返回int
	 * */
	public static int getBeginDayOfWeek(){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		int day = 0;
		day += calendar.get(Calendar.YEAR) * 10000;
		day += (calendar.get(Calendar.MONTH) + 1) * 100;
		day += calendar.get(Calendar.DAY_OF_MONTH);
		return day;
	}
	
	
	/** 
	 * 	得到当前周的 最后一天的日期返回int
	 * */
	public static int getEndDayOfWeek(){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
		int day = 0;
		day += calendar.get(Calendar.YEAR) * 10000;
		day += (calendar.get(Calendar.MONTH) + 1) * 100;
		day += calendar.get(Calendar.DAY_OF_MONTH);
		return day;
	}
	
	/** 
	 * 	得到当前月的第一天的日期返回int
	 * */
	public static int getBeginDayOfMonth(){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.set(Calendar.DAY_OF_MONTH,1);
		int day = 0;
		day += calendar.get(Calendar.YEAR) * 10000;
		day += (calendar.get(Calendar.MONTH) + 1) * 100;
		day += calendar.get(Calendar.DAY_OF_MONTH);
		return day;
	}
	
	
	/** 
	 * 	得到当前月的 最后一天的日期返回int
	 * */
	public static int getEndDayOfMonth(){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.set(Calendar.DAY_OF_MONTH,Calendar.DATE);
		int day = 0;
		day += calendar.get(Calendar.YEAR) * 10000;
		day += (calendar.get(Calendar.MONTH) + 1) * 100;
		day += calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		return day;
	}
	
	public static Date calcuExpire(int sec){
		Calendar calendar=Calendar.getInstance();		
		calendar.add(Calendar.SECOND, sec);
		return calendar.getTime();
	}	
	
	public static int remainingNumberOfSecondsByCurrentMinute(){
		Calendar calendar = Calendar.getInstance();
		long dateCur = calendar.getTimeInMillis();
		calendar.set(Calendar.SECOND, 59);
		long dateEnd = calendar.getTimeInMillis();
		return (int)((dateEnd - dateCur)/1000);
	}
	
	public static int remainingNumberOfSecondsByCurrentDay(){
		Calendar calendar = Calendar.getInstance();
		long dateCur = calendar.getTimeInMillis();
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		long dateEnd = calendar.getTimeInMillis();
		return (int)((dateEnd - dateCur)/1000);
	}
	
	//验证码超时
	private static Date getTimeOutValidCode(){
		Calendar calendar = Calendar.getInstance();
		
		calendar.set(Calendar.MINUTE, Calendar.getInstance().get(Calendar.MINUTE)+10);
		
		return calendar.getTime();
	}
	
	public static void main(String[] args) {
		System.out.println(remainingNumberOfSecondsByCurrentMinute());
		System.out.println(remainingNumberOfSecondsByCurrentDay());
	}
}
