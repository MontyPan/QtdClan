package us.dontcareabout.QtdClan.client.vo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class LevelMantissaTest {
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
}
