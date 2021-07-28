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
	public final Date lastDate;
	public final List<Damage> list;
	public final HashMap<Date, List<Damage>> byDate = new HashMap<>();
	public final HashMap<String, List<Damage>> byPlayer = new HashMap<>();

	public DamageAnalyser(int session, List<Damage> list) {
		this.session = session;
		this.list = list;

		Date last = new Date(0);

		for (Damage d : list) {
			ensure(byDate, d.getDate()).add(d);
			ensure(byPlayer, d.getPlayer()).add(d);

			if (last.before(d.getDate())) { last = d.getDate(); }
		}

		//保險起見，按照日期排序一下
		for (String player : byPlayer.keySet()) {
			Collections.sort(byPlayer.get(player), compareDate);
		}

		lastDate = last;
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

	private static int find(List<Damage> list, Date date) {
		if (list == null || list.isEmpty()) { return -1; }

		for (int i = 0; i < list.size(); i++) {
			if (date.equals(list.get(i).getDate())) {
				return i;
			}
		}

		return -1;
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
