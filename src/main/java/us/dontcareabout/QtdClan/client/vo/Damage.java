package us.dontcareabout.QtdClan.client.vo;

import java.util.Date;
import java.util.List;

import us.dontcareabout.gwt.client.google.sheet.Row;
import us.dontcareabout.gwt.client.google.sheet.Validator;

public final class Damage extends Row {
	public static final Validator<Damage> validator = new Validator<Damage>() {
		@Override
		public List<Throwable> validate(Damage entry) {
			if (entry.getDate() == null) { return Validator.DUMMY_FAIL; }
			return null;
		}
	};

	protected Damage() {}

	public Date getDate() {
		return dateField("Date");
	}

	public String getPlayer() {
		return stringField("Player");
	}

	public int getLevel() {
		return intField("Level");
	}

	public double getMantissa() {
		return doubleField("Mantissa");
	}
}
