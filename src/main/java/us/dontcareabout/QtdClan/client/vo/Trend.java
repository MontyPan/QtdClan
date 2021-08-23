package us.dontcareabout.QtdClan.client.vo;

import java.util.Date;

import us.dontcareabout.gwt.client.google.sheet.Row;

public final class Trend extends Row {
	protected Trend() {}

	public Date getDate() {
		return dateField("date");
	}

	public int getBoss() {
		return intField("boss");
	}

	public int getOrder() {
		return intField("order");
	}
}
