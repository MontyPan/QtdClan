package us.dontcareabout.QtdClan.client.common;

import java.util.Date;

import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.sencha.gxt.core.client.util.DateWrapper;

public class DateUtil {
	public static final DateWrapper start = new DateWrapper(2021, 2, 17);

	/**
	 * @return 指定的日期是第幾季。（起算日期為 {@link #start}）
	 */
	public static int session(Date d) {
		DateWrapper date = new DateWrapper(d);
		return (int) Math.floor(
			CalendarUtil.getDaysBetween(start.asDate(), date.asDate()) / 14.0
		) + 1;
	}

	/**
	 * @return 目前是第幾季。
	 * @see #session(Date)
	 */
	public static int nowSession() {
		return session(new Date());
	}

	public static Date sessionStart(int i) {
		return start.addDays((i - 1) * 14).asDate();
	}

	public static Date sessionEnd(int i) {
		return start.addDays(i * 14 - 3).asDate();
	}
}
