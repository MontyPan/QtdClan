package us.dontcareabout.QtdClan.client.vo;

import java.util.Objects;

import us.dontcareabout.QtdClan.client.data.Damage;

public class LevelMantissa {
	public static LevelMantissa ZERO = new LevelMantissa(0, 0);

	public final int level;
	public final double mantissa;

	public LevelMantissa(int level, double mantissa) {
		this.level = level;
		this.mantissa = mantissa;
	}

	public LevelMantissa(Damage damage) {
		this(damage.getLevel(), damage.getMantissa());
	}

	@Override
	public String toString() {
		return "[L = " + level + ", M = " + mantissa + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(level, mantissa);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) { return true; }
		if (!(obj instanceof LevelMantissa)) { return false; }
		LevelMantissa other = (LevelMantissa)obj;
		return level == other.level && Double.doubleToLongBits(mantissa) == Double.doubleToLongBits(other.mantissa);
	}

	/**
	 * @return a - b
	 */
	public static LevelMantissa minus(LevelMantissa a, LevelMantissa b) {
		if (a.equals(b)) { return ZERO; }

		if (a.level == b.level) {
			return new LevelMantissa(a.level, a.mantissa - b.mantissa);
		}

		if (a.level == b.level + 1) {
			double diff = a.mantissa * 1000 - b.mantissa;

			if (diff < 1000) {
				return new LevelMantissa(b.level, ((int)(diff * 1000)) / 1000.0 );
			} else {
				return new LevelMantissa(a.level, ((int)diff) / 1000.0);
			}
		}

		//level 差距超過 2，b 等同於雜訊，所以直接回傳 a
		return a;
	}
}
