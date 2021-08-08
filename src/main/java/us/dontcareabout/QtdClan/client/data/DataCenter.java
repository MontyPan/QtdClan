package us.dontcareabout.QtdClan.client.data;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.SimpleEventBus;

import us.dontcareabout.QtdClan.client.common.DamageAnalyser;
import us.dontcareabout.QtdClan.client.common.DateUtil;
import us.dontcareabout.QtdClan.client.data.DamageReadyEvent.DamageReadyHandler;
import us.dontcareabout.QtdClan.client.data.SeasonChangeEvent.SeasonChangeHandler;
import us.dontcareabout.gst.client.data.SheetIdDao;
import us.dontcareabout.gwt.client.Console;
import us.dontcareabout.gwt.client.google.Sheet;
import us.dontcareabout.gwt.client.google.SheetHappen;
import us.dontcareabout.gwt.client.google.SheetHappen.Callback;

public class DataCenter {
	private final static SimpleEventBus eventBus = new SimpleEventBus();

	public static DamageAnalyser damageAnalyser;

	public static void wantDamage(int session) {
		//第一個 tab 是 boss 進度，第二個是賽季紀錄
		//第三個才是當前賽季、第 3+N 個是往前 N 個賽季...
		int sessionIndex = 3 + DateUtil.nowSeason() - session;
		SheetHappen.<Damage>get(SheetIdDao.defaultValue(), sessionIndex, new Callback<Damage>() {
			@Override
			public void onSuccess(Sheet<Damage> gs) {
				damageAnalyser = new DamageAnalyser(session, gs.getEntry());
				eventBus.fireEvent(new DamageReadyEvent());
			}

			@Override
			public void onError(Throwable exception) {
				Console.log(exception);
			}
		});
	}

	public static HandlerRegistration addDamageReady(DamageReadyHandler handler) {
		return eventBus.addHandler(DamageReadyEvent.TYPE, handler);
	}

	////////

	public static HandlerRegistration addSeasonChange(SeasonChangeHandler handler) {
		return eventBus.addHandler(SeasonChangeEvent.TYPE, handler);
	}

	public static void seasonChange(int selectSeason) {
		eventBus.fireEvent(new SeasonChangeEvent(selectSeason));
		wantDamage(selectSeason);
	}
}
