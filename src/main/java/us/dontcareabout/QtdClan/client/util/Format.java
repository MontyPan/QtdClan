package us.dontcareabout.QtdClan.client.util;

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;

public class Format {
	private static final DateTimeFormat shortDate = DateTimeFormat.getFormat("MM/dd");
	private static final NumberFormat xx_x = NumberFormat.getFormat("##.#");

	public static String shortDate(Date d) {
		return shortDate.format(d);
	}

	public static String xx_x(Number n) {
		return xx_x.format(n);
	}
}
