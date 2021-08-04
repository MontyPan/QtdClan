package us.dontcareabout.QtdClan.client.component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.sencha.gxt.chart.client.chart.Chart;
import com.sencha.gxt.chart.client.chart.Legend;
import com.sencha.gxt.chart.client.chart.axis.NumericAxis;
import com.sencha.gxt.chart.client.chart.axis.TimeAxis;
import com.sencha.gxt.chart.client.chart.series.BarSeries;
import com.sencha.gxt.chart.client.chart.series.LineSeries;
import com.sencha.gxt.chart.client.chart.series.Series;
import com.sencha.gxt.chart.client.chart.series.Series.LabelPosition;
import com.sencha.gxt.chart.client.chart.series.SeriesLabelConfig;
import com.sencha.gxt.chart.client.chart.series.SeriesLabelProvider;
import com.sencha.gxt.chart.client.draw.RGB;
import com.sencha.gxt.chart.client.draw.sprite.TextSprite;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.core.client.util.DateWrapper;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

import us.dontcareabout.QtdClan.client.common.DamageAnalyser;
import us.dontcareabout.QtdClan.client.component.MemberChart.Data;
import us.dontcareabout.QtdClan.client.vo.LevelMantissa;
import us.dontcareabout.QtdClan.client.vo.Player;

public class MemberChart extends Chart<Data> {
	public static final int CLAN_SIZE = 40;

	private static final Properties properties = GWT.create(Properties.class);
	private static final DateTimeFormat dateFormat = DateTimeFormat.getFormat("MM/dd");
	private static NumberFormat numFormat = NumberFormat.getFormat("##.#%");

	private TimeAxis<Data> timeAxis = new TimeAxis<>();
	private ListStore<Data> store = new ListStore<>(new ModelKeyProvider<Data>() {
		@Override
		public String getKey(Data item) {
			return item.getDate().toString();
		}
	});

	public MemberChart() {
		timeAxis.setField(properties.date());
		timeAxis.setLabelProvider(new LabelProvider<Date>() {
			@Override
			public String getLabel(Date item) {
				return dateFormat.format(item);
			}
		});
		timeAxis.setDisplayGrid(true);

		NumericAxis<Data> axisLeft = new NumericAxis<Data>();
		axisLeft.setPosition(Position.LEFT);
		axisLeft.addField(properties.order());
		axisLeft.setDisplayGrid(true);
		axisLeft.setMaximum(CLAN_SIZE);
		axisLeft.setMinimum(1);
		axisLeft.setInterval(5);
		axisLeft.setLabelProvider(new LabelProvider<Number>() {
			@Override
			public String getLabel(Number item) {
				return String.valueOf(CLAN_SIZE + 1 - item.intValue());
			}
		});

		final LineSeries<Data> line = new LineSeries<Data>();
		line.setYAxisPosition(Position.LEFT);
		line.setYField(properties.order());
		line.setStroke(new RGB("#ffa000"));
		line.setLegendTitle("排名");
		line.setStrokeWidth(3);

		Legend<Data> legend = new Legend<>();

		setStore(store);
		addAxis(axisLeft);
		addAxis(timeAxis);
		addSeries(genBarSeries());
		addSeries(line);
		setLegend(legend);
		setDefaultInsets(10);
	}

	public void refresh(DamageAnalyser analyser, String player) {
		List<Data> list = new ArrayList<>();

		timeAxis.setStartDate(analyser.startDate);
		timeAxis.setEndDate(analyser.endDate);

		DateWrapper date = new DateWrapper(analyser.startDate);
		Player p = analyser.get(player);

		for (int i = 0; i < analyser.days; i++) {
			Data data = new Data();
			data.setDate(date.addDays(i).asDate());
			data.setTotalRatio(LevelMantissa.divide(p.dayDamage[i], analyser.daySum[i]));
			data.setDayRatio(LevelMantissa.divide(p.diffDamage[i], analyser.diffSum[i]));
			//如果該天完全沒有紀錄，就會遇到 diffSum 是 0 的狀態，所以擋一下 Orz
			data.setDayRatio(Double.isNaN(data.getDayRatio()) ? 0 : data.getDayRatio());
			data.setOrder(CLAN_SIZE - analyser.findOrder(player, i));
			list.add(data);
		}

		store.replaceAll(list);
		redrawChart();
	}

	private Series<Data> genBarSeries() {
		TextSprite spriteConfig = new TextSprite();
		spriteConfig.setY(-2);

		SeriesLabelConfig<Data> labelConfig = new SeriesLabelConfig<Data>();
		labelConfig.setLabelPosition(LabelPosition.OUTSIDE);
		labelConfig.setLabelProvider(new SeriesLabelProvider<Data>() {
			@Override
			public String getLabel(Data item, ValueProvider<? super Data, ? extends Number> vp) {
				return numFormat.format(vp.getValue(item));
			}
		});
		labelConfig.setSpriteConfig(spriteConfig);

		BarSeries<Data> result = new BarSeries<>();
		result.addYField(properties.totalRatio());
		result.addColor(new RGB("#1976d2"));
		result.addYField(properties.dayRatio());
		result.addColor(new RGB("#4fc3f7"));
		result.setColumn(true);
		result.setGroupGutter(5);
		result.setLegendTitles(Arrays.asList("總佔比", "日佔比"));
		result.setLabelConfig(labelConfig);
		return result;
	}

	public class Data {
		Date date;
		double totalRatio;
		double dayRatio;
		int order;

		public Date getDate() {
			return date;
		}
		public void setDate(Date date) {
			this.date = date;
		}
		public double getTotalRatio() {
			return totalRatio;
		}
		public void setTotalRatio(double totalRatio) {
			this.totalRatio = totalRatio;
		}
		public double getDayRatio() {
			return dayRatio;
		}
		public void setDayRatio(double dayRatio) {
			this.dayRatio = dayRatio;
		}
		public int getOrder() {
			return order;
		}
		public void setOrder(int order) {
			this.order = order;
		}
	}

	interface Properties extends PropertyAccess<Data> {
		ValueProvider<Data, Date> date();
		ValueProvider<Data, Double> totalRatio();
		ValueProvider<Data, Double> dayRatio();
		ValueProvider<Data, Integer> order();
	}
}
