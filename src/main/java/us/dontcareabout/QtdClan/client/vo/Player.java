package us.dontcareabout.QtdClan.client.vo;

import static us.dontcareabout.QtdClan.client.vo.LevelMantissa.ZERO;

import java.util.Date;
import java.util.List;

import com.sencha.gxt.core.client.util.DateWrapper;

import us.dontcareabout.QtdClan.client.data.Damage;

public class Player {
	public final String name;
	public final LevelMantissa[] dayDamage;
	public final LevelMantissa[] diffDamage;
	public final int attendance;

	@Deprecated
	private final List<Damage> rawDamage;

	public Player(String name, List<Damage> list, Date startDate, int days) {
		this.name = name;
		this.rawDamage = list;
		this.dayDamage = new LevelMantissa[days];
		this.diffDamage = new LevelMantissa[days];

		int count = 0;

		for (int i = 0; i < diffDamage.length; i++) {
			Date date = new DateWrapper(startDate).addDays(i).asDate();
			dayDamage[i] = getDamage(date);
			diffDamage[i] = getDiffDamage(date);
			if (!diffDamage[i].equals(LevelMantissa.ZERO)) { count++; }
		}

		attendance = count;
	}

	public LevelMantissa getDamage(Date date) {
		int index = find(rawDamage, date);

		if (index == -1) { return ZERO; }

		return new LevelMantissa(rawDamage.get(index));
	}

	public LevelMantissa getDiffDamage(Date date) {
		int index = find(rawDamage, date);

		if (index == -1) { return ZERO; }
		if (index == 0) { return new LevelMantissa(rawDamage.get(0)); }

		return LevelMantissa.minus(
			new LevelMantissa(rawDamage.get(index)),
			new LevelMantissa(rawDamage.get(index - 1))
		);
	}

	/**
	 * @return 如果 list 是空的、或是 list 內的時間都比傳入時間大，回傳 -1。
	 * 	如果有指定日期，回傳指定日期的 index 值、否則回傳 list.size() - 1。
	 */
	//其實實際數據不會有這問題，只要第 x 天有紀錄，x + n 天都會有紀錄
	//反而是測試資料才會炸這種問題 wwwww
	private static int find(List<Damage> list, Date date) {
		if (list == null || list.isEmpty()) { return -1; }

		int i = 0;

		for (; i < list.size(); i++) {
			if (date.equals(list.get(i).getDate())) {
				return i;
			}
		}

		i--;
		return date.after(list.get(i).getDate()) ? i : - 1;
	}
}
