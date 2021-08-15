package us.dontcareabout.QtdClan.client.view;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.sencha.gxt.chart.client.chart.Chart;
import com.sencha.gxt.chart.client.chart.Legend;
import com.sencha.gxt.chart.client.chart.axis.NumericAxis;
import com.sencha.gxt.chart.client.chart.axis.TimeAxis;
import com.sencha.gxt.chart.client.chart.series.BarSeries;
import com.sencha.gxt.chart.client.chart.series.LineSeries;
import com.sencha.gxt.chart.client.chart.series.Series;
import com.sencha.gxt.chart.client.chart.series.Series.LabelPosition;
import com.sencha.gxt.chart.client.chart.series.SeriesLabelConfig;
import com.sencha.gxt.chart.client.draw.RGB;
import com.sencha.gxt.chart.client.draw.sprite.TextSprite;
import com.sencha.gxt.chart.client.draw.sprite.TextSprite.TextAnchor;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.core.client.util.DateWrapper;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

import us.dontcareabout.QtdClan.client.common.DamageAnalyser;
import us.dontcareabout.QtdClan.client.data.DamageReadyEvent;
import us.dontcareabout.QtdClan.client.data.DamageReadyEvent.DamageReadyHandler;
import us.dontcareabout.QtdClan.client.data.DataCenter;
import us.dontcareabout.QtdClan.client.util.Format;
import us.dontcareabout.QtdClan.client.view.TrendChart.Data;

public class TrendChart extends Chart<Data> {
	private static final Properties properties = GWT.create(Properties.class);

	private ListStore<Data> store = new ListStore<>(new ModelKeyProvider<Data>() {
		@Override
		public String getKey(Data item) {
			return item.getDate().toString();
		}
	});
	private TimeAxis<Data> timeAxis = new TimeAxis<>();
	private NumericAxis<Data> lAxis = new NumericAxis<>();
	private NumericAxis<Data> rAxis = new NumericAxis<>();

	public TrendChart() {
		DataCenter.addDamageReady(new DamageReadyHandler() {
			@Override
			public void onDamageReady(DamageReadyEvent event) {
				refresh(DataCenter.damageAnalyser);
			}
		});

		timeAxis.setField(properties.date());
		timeAxis.setLabelProvider(new LabelProvider<Date>() {
			@Override
			public String getLabel(Date item) {
				return Format.shortDate(item);
			}
		});
		timeAxis.setDisplayGrid(true);

		rAxis.setPosition(Position.RIGHT);
		rAxis.addField(properties.number());
		rAxis.setMinimum(1);
		rAxis.setMaximum(40);	//這裡就不考慮一天有 41 個人打這種神奇事蹟了
		rAxis.setHidden(true);	//只打算設定上限、沒打算顯示

		lAxis.setPosition(Position.LEFT);
		lAxis.addField(properties.order());
		lAxis.setDisplayGrid(true);
		lAxis.setMinimum(1);
		lAxis.setMaximum(41);	//第一天排名沒超過 40 過
		lAxis.setSteps(10);
		lAxis.setLabelProvider(new LabelProvider<Number>() {
			@Override
			public String getLabel(Number item) {
				return String.valueOf(lAxis.getMaximum() + 1 - item.intValue());
			}
		});

		LineSeries<Data> line = new LineSeries<Data>();
		line.setYAxisPosition(Position.LEFT);
		line.setYField(properties.order());
		line.setStroke(new RGB("#ffa000"));
		line.setLegendTitle("戰隊排名");
		line.setStrokeWidth(3);

		Legend<Data> legend = new Legend<>();

		setStore(store);
		addAxis(timeAxis);
		addAxis(lAxis);
		addAxis(rAxis);
		addSeries(genBarSeries());
		addSeries(line);
		setLegend(legend);
		setDefaultInsets(10);
	}

	public void refresh(DamageAnalyser analyser) {
		List<Data> list = new ArrayList<>();
		DateWrapper date = new DateWrapper(analyser.startDate);

		for (int i = 0; i < analyser.days; i++) {
			Data data = new Data();
			data.setDate(date.addDays(i).asDate());

			int order = DataCenter.findTrend(data.getDate()).getOrder();
			//排名不可能是 0，那代表當天沒有紀錄..... Orz
			if (order != 0) {
				data.setOrder(lAxis.getMaximum() - order + 1);
			} else {
				data.setOrder(Double.NaN);
			}
			data.setNumber(analyser.attendance[i]);
			list.add(data);
		}

		timeAxis.setStartDate(analyser.startDate);
		timeAxis.setEndDate(analyser.endDate);

		store.replaceAll(list);
		redrawChart();
	}

	@Override
	protected void onLoad() {
		super.onLoad();
		refresh(DataCenter.damageAnalyser);
	}

	private Series<Data> genBarSeries() {
		TextSprite spriteConfig = new TextSprite();
		spriteConfig.setY(-2);
		spriteConfig.setTextAnchor(TextAnchor.MIDDLE);

		SeriesLabelConfig<Data> labelConfig = new SeriesLabelConfig<Data>();
		labelConfig.setLabelPosition(LabelPosition.OUTSIDE);
		labelConfig.setSpriteConfig(spriteConfig);

		BarSeries<Data> result = new BarSeries<>();
		result.setYAxisPosition(Position.RIGHT);
		result.addYField(properties.number());
		result.addColor(new RGB("#1976d2"));
		result.setColumn(true);
		result.setGroupGutter(5);
		result.setLegendTitle("參戰人數");
		result.setLabelConfig(labelConfig);
		return result;
	}

	class Data {
		Date date;
		Double order;
		int number;

		public Date getDate() {
			return date;
		}
		public void setDate(Date date) {
			this.date = date;
		}
		public Double getOrder() {
			return order;
		}
		public void setOrder(Double order) {
			this.order = order;
		}
		public int getNumber() {
			return number;
		}
		public void setNumber(int number) {
			this.number = number;
		}
	}

	interface Properties extends PropertyAccess<Data> {
		ValueProvider<Data, Date> date();
		ValueProvider<Data, Double> order();
		ValueProvider<Data, Integer> number();
	}
}
