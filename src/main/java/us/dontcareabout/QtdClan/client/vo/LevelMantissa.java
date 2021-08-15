package us.dontcareabout.QtdClan.client.vo;

import java.util.Objects;

import com.google.common.base.Preconditions;

public class LevelMantissa implements Comparable<LevelMantissa> {
	public static LevelMantissa ZERO = new LevelMantissa(0, 0);

	public final int level;
	public final double mantissa;

	public LevelMantissa(int level, double mantissa) {
		Preconditions.checkArgument(level >= 0);

		if (mantissa >= 1000) {
			this.level = level + 1;
			this.mantissa = toInt(mantissa) / 1000.0;
		} else if (mantissa < 1 && level > 1) {	//level = 0 再降沒意義
			//不打算考慮 level 降兩階的狀況... [眼神死]
			this.level = level - 1;
			this.mantissa = toInt(mantissa * 1000000) / 1000.0;
		} else {
			this.level = level;
			this.mantissa = mantissa;
		}
	}

	public LevelMantissa(Damage damage) {
		this(damage.getLevel(), damage.getMantissa());
	}

	public double value() {
		return Math.pow(1000, level - 1) * mantissa;
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

	@Override
	public int compareTo(LevelMantissa o) {
		if (level != o.level) { return level - o.level; }
		return Double.valueOf(mantissa).compareTo(Double.valueOf(o.mantissa));
	}

	/**
	 * @return a / b
	 */
	public static double divide(LevelMantissa a, LevelMantissa b) {
		if (ZERO.equals(b)) { return Double.NaN; }

		return a.value() / b.value();
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
			return new LevelMantissa(b.level, a.mantissa * 1000 - b.mantissa);
		}

		//level 差距超過 2，b 等同於雜訊，所以直接回傳 a
		return a;
	}

	/**
	 * @return a + b
	 */
	public static LevelMantissa plus(LevelMantissa a, LevelMantissa b) {
		LevelMantissa big = a;
		LevelMantissa small = b;

		if (a.compareTo(b) < 0) { big = b; small = a; }

		if (big.level == small.level) {
			return new LevelMantissa(big.level, big.mantissa + small.mantissa);
		}

		if (big.level == small.level + 1) {
			return new LevelMantissa(big.level, big.mantissa + small.mantissa / 1000.0);
		}

		//level 差距超過 2，small 等同於雜訊，所以直接回傳 big
		return big;
	}

	private static int toInt(double d) {
		return (int) d;
	}
}
