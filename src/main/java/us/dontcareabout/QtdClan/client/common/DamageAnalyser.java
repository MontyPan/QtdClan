package us.dontcareabout.QtdClan.client.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.google.gwt.user.datepicker.client.CalendarUtil;

import us.dontcareabout.QtdClan.client.data.Damage;
import us.dontcareabout.QtdClan.client.vo.LevelMantissa;
import us.dontcareabout.QtdClan.client.vo.Player;

public class DamageAnalyser {
	private static final Comparator<Damage> compareDate = new Comparator<Damage>() {
		@Override
		public int compare(Damage o1, Damage o2) {
			return o1.getDate().compareTo(o2.getDate());
		}
	};

	public final int session;
	public final List<Damage> list;

	public final Date startDate;
	public final Date endDate;
	public final int days;
	public final Set<String> players;
	public final HashMap<String, Player> playerData = new HashMap<>();

	public final LevelMantissa sum;
	public final LevelMantissa[] daySum;
	public final LevelMantissa[] diffSum;

	public DamageAnalyser(int session, List<Damage> list) {
		this.session = session;
		this.list = list;

		Date start = new Date();
		Date end = new Date(0);
		HashMap<String, List<Damage>> byPlayer = new HashMap<>();

		for (Damage d : list) {
			ensure(byPlayer, d.getPlayer()).add(d);

			if (start.after(d.getDate())) { start = d.getDate(); }
			if (end.before(d.getDate())) { end = d.getDate(); }
		}

		players = byPlayer.keySet();
		startDate = start;
		endDate = end;
		days = CalendarUtil.getDaysBetween(start, end) + 1;

		//保險起見，按照日期排序一下
		for (String player : players) {
			Collections.sort(byPlayer.get(player), compareDate);
			playerData.put(
				player,
				new Player(player, byPlayer.get(player), startDate, days)
			);
		}

		this.daySum = new LevelMantissa[days];
		this.diffSum = new LevelMantissa[days];

		for (int i = 0; i < days; i++) {
			daySum[i] = LevelMantissa.ZERO;
			diffSum[i] = LevelMantissa.ZERO;

			for (String player : players) {
				Player p = get(player);
				daySum[i] = LevelMantissa.plus(daySum[i], p.dayDamage[i]);
				diffSum[i] = LevelMantissa.plus(diffSum[i], p.diffDamage[i]);
			}
		}

		this.sum = daySum[days - 1];
	}

	@Deprecated
	public LevelMantissa getDamage(String player, Date date) {
		return get(player).getDamage(date);
	}

	@Deprecated
	public LevelMantissa getDiffDamage(String player, Date date) {
		return get(player).getDiffDamage(date);
	}

	public int getAttendance(String player) {
		return get(player).attendance;
	}

	public Player get(String player) {
		return playerData.get(player);
	}

	/** @return 該玩家當天的排名，0 為第一名 */
	public int findOrder(String player, int dayOffset) {
		LevelMantissa damage = get(player).dayDamage[dayOffset];

		if (LevelMantissa.ZERO.equals(damage)) { return players.size() - 1; }

		int result = 0;

		for (String p : players) {
			if (p.equals(player)) { continue; }

			if (get(p).dayDamage[dayOffset].compareTo(damage) > 0) {
				result++;
			}
		}

		return result;
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
