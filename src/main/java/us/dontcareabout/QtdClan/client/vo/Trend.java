package us.dontcareabout.QtdClan.client.vo;

import java.util.Date;

import us.dontcareabout.gwt.client.google.SheetEntry;

public final class Trend extends SheetEntry {
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
