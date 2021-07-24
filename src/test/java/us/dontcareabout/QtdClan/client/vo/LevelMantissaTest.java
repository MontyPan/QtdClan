package us.dontcareabout.QtdClan.client.vo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class LevelMantissaTest {
	@Test
	void testConstructor() {
		assertEquals(new LevelMantissa(5, 999.999), new LevelMantissa(5, 999.999));
		assertEquals(new LevelMantissa(4, 1000), new LevelMantissa(5, 1));
		assertEquals(new LevelMantissa(5, 1234.567), new LevelMantissa(6, 1.234));
		assertEquals(new LevelMantissa(5, 0.123456), new LevelMantissa(4, 123.456));
	}

	@Test
	void testMinus() {
		LevelMantissa v1 = new LevelMantissa(4, 900.5);

		assertEquals(	//同 L 不同 M，diff 足夠
			LevelMantissa.minus(v1, new LevelMantissa(4, 1.3)),
			new LevelMantissa(4, 899.2)
		);
		assertEquals(	//同 L 不同 M，diff 不足 L
			LevelMantissa.minus(v1, new LevelMantissa(4, 900.409)),
			new LevelMantissa(3, 91)
		);
		assertEquals(	//L 差 1，diff 不足 L
			LevelMantissa.minus(new LevelMantissa(5, 1.1), v1),
			new LevelMantissa(4, 199.5)
		);
		assertEquals(	//L 差 1，diff 足夠
			LevelMantissa.minus(new LevelMantissa(5, 2), v1),
			new LevelMantissa(5, 1.099)
		);
		assertEquals(	//L 差 2
			LevelMantissa.minus(new LevelMantissa(6, 2), v1),
			new LevelMantissa(6, 2)
		);
		assertEquals(	//完全一樣
			LevelMantissa.minus(v1, v1),
			LevelMantissa.ZERO
		);
	}

	@Test
	void testPlus() {
		LevelMantissa v = new LevelMantissa(4, 500.5);

		assertEquals(	//同 L，sum 沒有超過
			LevelMantissa.plus(v, new LevelMantissa(4, 499.499)),
			new LevelMantissa(4, 999.999)
		);
		assertEquals(	//同 L，sum 超過
			LevelMantissa.plus(v, new LevelMantissa(4, 499.5)),
			new LevelMantissa(5, 1)
		);
		assertEquals(	//同 L，sum 超過
			LevelMantissa.plus(v, new LevelMantissa(4, 511.5)),
			new LevelMantissa(5, 1.012)
		);
		assertEquals(	//L 差 1，sum 沒有超過
			LevelMantissa.plus(v, new LevelMantissa(3, 400)),
			new LevelMantissa(4, 500.9)
		);
		assertEquals(	//L 差 1，sum 沒有超過，交換律
			LevelMantissa.plus(new LevelMantissa(3, 400), v),
			new LevelMantissa(4, 500.9)
		);
		assertEquals(	//L 差 1，sum 有超過
			LevelMantissa.plus(new LevelMantissa(4, 999.999), new LevelMantissa(3, 999)),
			new LevelMantissa(5, 1)
		);
		assertEquals(	//L 差 2
			LevelMantissa.plus(v, new LevelMantissa(2, 400)),
			v
		);
		assertEquals(	//L 差 2，交換律
			LevelMantissa.plus(new LevelMantissa(2, 400), v),
			v
		);
	}
}
