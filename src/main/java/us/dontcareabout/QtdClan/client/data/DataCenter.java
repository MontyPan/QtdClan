package us.dontcareabout.QtdClan.client.data;

import java.util.Date;
import java.util.List;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.SimpleEventBus;

import us.dontcareabout.QtdClan.client.common.DamageAnalyser;
import us.dontcareabout.QtdClan.client.common.DateUtil;
import us.dontcareabout.QtdClan.client.data.DamageReadyEvent.DamageReadyHandler;
import us.dontcareabout.QtdClan.client.data.SeasonChangeEvent.SeasonChangeHandler;
import us.dontcareabout.QtdClan.client.data.TrendReadyEvent.TrendReadyHandler;
import us.dontcareabout.QtdClan.client.util.Format;
import us.dontcareabout.QtdClan.client.vo.Damage;
import us.dontcareabout.QtdClan.client.vo.Trend;
import us.dontcareabout.gst.client.data.ApiKey;
import us.dontcareabout.gst.client.data.SheetIdDao;
import us.dontcareabout.gwt.client.Console;
import us.dontcareabout.gwt.client.google.sheet.Sheet;
import us.dontcareabout.gwt.client.google.sheet.SheetDto;
import us.dontcareabout.gwt.client.google.sheet.SheetDto.Callback;

public class DataCenter {
	private final static SimpleEventBus eventBus = new SimpleEventBus();

	public static DamageAnalyser damageAnalyser;

	private static SheetDto<Damage> damageSheet = new SheetDto<Damage>()
		.key(ApiKey.priorityValue()).sheetId(SheetIdDao.priorityValue())
		.validator(Damage.validator);

	public static void wantDamage(int session) {
		String tabName = Format.yyyyMMdd(DateUtil.seasonStart(session));
		damageSheet.tabName(tabName).fetch(new Callback<Damage>() {
			@Override
			public void onSuccess(Sheet<Damage> gs) {
				damageAnalyser = new DamageAnalyser(session, gs.getRows());
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

	public static List<Trend> trend;

	private static SheetDto<Trend> trendSheet = new SheetDto<Trend>()
		.key(ApiKey.priorityValue()).sheetId(SheetIdDao.priorityValue()).tabName("boss");

	public static void wantTrend() {
		trendSheet.fetch(new Callback<Trend>() {
			@Override
			public void onSuccess(Sheet<Trend> gs) {
				trend = gs.getRows();
				eventBus.fireEvent(new TrendReadyEvent());
			}

			@Override
			public void onError(Throwable exception) {
				Console.log(exception);
			}
		});
	}

	public static HandlerRegistration addTrendReady(TrendReadyHandler handler) {
		return eventBus.addHandler(TrendReadyEvent.TYPE, handler);
	}

	public static Trend findTrend(Date date) {
		for (Trend t : trend) {
			if (t.getDate().equals(date)) {
				return t;
			}
		}

		return null;
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
