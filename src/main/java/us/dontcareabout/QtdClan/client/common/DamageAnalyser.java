package us.dontcareabout.QtdClan.client.common;

import static us.dontcareabout.QtdClan.client.vo.LevelMantissa.ZERO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import us.dontcareabout.QtdClan.client.data.Damage;
import us.dontcareabout.QtdClan.client.vo.LevelMantissa;

public class DamageAnalyser {
	private static final Comparator<Damage> compareDate = new Comparator<Damage>() {
		@Override
		public int compare(Damage o1, Damage o2) {
			return o1.getDate().compareTo(o2.getDate());
		}
	};

	public final int session;
	public final List<Damage> list;

	public final HashMap<String, List<Damage>> byPlayer = new HashMap<>();
	public final Date lastDate;
	public final LevelMantissa sum;

	public DamageAnalyser(int session, List<Damage> list) {
		this.session = session;
		this.list = list;

		Date last = new Date(0);

		for (Damage d : list) {
			ensure(byPlayer, d.getPlayer()).add(d);

			if (last.before(d.getDate())) { last = d.getDate(); }
		}

		//保險起見，按照日期排序一下
		for (String player : byPlayer.keySet()) {
			Collections.sort(byPlayer.get(player), compareDate);
		}

		lastDate = last;
		LevelMantissa sum = LevelMantissa.ZERO;

		for (String player : byPlayer.keySet()) {
			sum = LevelMantissa.plus(sum, getDamage(player, last));
		}

		this.sum = sum;
	}

	public LevelMantissa getDamage(String player, Date date) {
		List<Damage> data = byPlayer.get(player);
		int index = find(data, date);

		if (index == -1) { return ZERO; }

		return new LevelMantissa(data.get(index));
	}

	public LevelMantissa getDiffDamage(String player, Date date) {
		List<Damage> data = byPlayer.get(player);
		int index = find(data, date);

		if (index == -1) { return ZERO; }
		if (index == 0) { return new LevelMantissa(data.get(0)); }

		return LevelMantissa.minus(
			new LevelMantissa(data.get(index)),
			new LevelMantissa(data.get(index - 1))
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

	//Refactory GF
	private static <T, V> List<V> ensure(HashMap<T, List<V>> map, T key) {
		List<V> list = map.get(key);

		if (list == null) {
			list = new ArrayList<>();
			map.put(key, list);
		}

		return list;
	}
}
