package us.dontcareabout.QtdClan.client.component;

import java.util.ArrayList;
import java.util.Arrays;
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
import com.sencha.gxt.chart.client.chart.series.SeriesLabelProvider;
import com.sencha.gxt.chart.client.chart.series.SeriesRenderer;
import com.sencha.gxt.chart.client.draw.RGB;
import com.sencha.gxt.chart.client.draw.sprite.Sprite;
import com.sencha.gxt.chart.client.draw.sprite.TextSprite;
import com.sencha.gxt.chart.client.draw.sprite.TextSprite.TextAnchor;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.core.client.util.DateWrapper;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

import us.dontcareabout.QtdClan.client.common.DamageAnalyser;
import us.dontcareabout.QtdClan.client.component.MemberChart.Data;
import us.dontcareabout.QtdClan.client.util.Format;
import us.dontcareabout.QtdClan.client.vo.LevelMantissa;
import us.dontcareabout.QtdClan.client.vo.Player;

public class MemberChart extends Chart<Data> {
	private static final Properties properties = GWT.create(Properties.class);

	private ListStore<Data> store = new ListStore<>(new ModelKeyProvider<Data>() {
		@Override
		public String getKey(Data item) {
			return item.getDate().toString();
		}
	});
	private TimeAxis<Data> timeAxis = new TimeAxis<>();
	private NumericAxis<Data> axisLeft = new NumericAxis<>();
	private NumericAxis<Data> axisRight = new NumericAxis<>();
	private TextSprite nameTS = new TextSprite();

	public MemberChart() {
		timeAxis.setField(properties.date());
		timeAxis.setLabelProvider(new LabelProvider<Date>() {
			@Override
			public String getLabel(Date item) {
				return Format.shortDate(item);
			}
		});
		timeAxis.setDisplayGrid(true);

		axisRight.setPosition(Position.RIGHT);
		axisRight.addField(properties.dayRatio());
		axisRight.addField(properties.totalRatio());
		axisRight.setHidden(true);	//只打算設定上限、沒打算顯示

		axisLeft.setPosition(Position.LEFT);
		axisLeft.addField(properties.order());
		axisLeft.setDisplayGrid(true);
		axisLeft.setMinimum(1);
		axisLeft.setLabelProvider(new LabelProvider<Number>() {
			@Override
			public String getLabel(Number item) {
				return String.valueOf(axisLeft.getMaximum() + 1 - item.intValue());
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
		addAxis(timeAxis);
		addAxis(axisLeft);
		addAxis(axisRight);
		addSeries(genBarSeries());
		addSeries(line);
		setLegend(legend);
		setDefaultInsets(10);

		//用 GF 的 TextUtil 動態設定 y 會出靈異現象
		//反正玩家名稱有字數上限，所以乾脆寫死 y 跟 font size 就算了 [眼神死]
		nameTS.setY(15);
		nameTS.setFontSize(36);
		nameTS.setFill(RGB.BLACK);
		nameTS.setFillOpacity(0.3);
		nameTS.setZIndex(10000);
		addSprite(nameTS);

		waitPlayer();
	}

	public void refresh(DamageAnalyser analyser, String player) {
		nameTS.setText(player);
		nameTS.redraw();
		//以實驗結果來反推，redrawChart() 不會讓整個 DrawComponent redraw

		List<Data> list = new ArrayList<>();

		DateWrapper date = new DateWrapper(analyser.startDate);
		Player p = analyser.get(player);
		double maxRatio = 0;

		for (int i = 0; i < analyser.days; i++) {
			Data data = new Data();
			data.setDate(date.addDays(i).asDate());
			data.setTotalRatio(LevelMantissa.divide(p.dayDamage[i], analyser.daySum[i]));
			data.setDayRatio(LevelMantissa.divide(p.diffDamage[i], analyser.diffSum[i]));
			//如果該天完全沒有紀錄，就會遇到 diffSum 是 0 的狀態，所以擋一下 Orz
			data.setDayRatio(Double.isNaN(data.getDayRatio()) ? 0 : data.getDayRatio());
			data.setOrder(analyser.players.size() - analyser.findOrder(player, i));
			list.add(data);

			if (data.getTotalRatio() > maxRatio) { maxRatio = data.getTotalRatio(); }
			if (data.getDayRatio() > maxRatio) { maxRatio = data.getDayRatio(); }
		}

		timeAxis.setStartDate(analyser.startDate);
		timeAxis.setEndDate(analyser.endDate);
		axisLeft.setMaximum(analyser.players.size());
		axisRight.setMaximum(maxRatio * 1.05);

		store.replaceAll(list);
		redrawChart();
		unmask();
	}

	public void waitPlayer() {
		mask("請選擇隊員");
	}

	@Override
	public void onResize(int width, int height) {
		nameTS.setX(width - 280);
		super.onResize(width, height);
	}

	private Series<Data> genBarSeries() {
		final String zeroText = "X";
		final RGB zeroColor = new RGB("#bdbdbd");
		final RGB totalColor = new RGB("#1976d2");
		final RGB dayColor = new RGB("#4fc3f7");

		TextSprite spriteConfig = new TextSprite();
		spriteConfig.setY(-2);
		spriteConfig.setTextAnchor(TextAnchor.MIDDLE);

		SeriesLabelConfig<Data> labelConfig = new SeriesLabelConfig<Data>();
		labelConfig.setLabelPosition(LabelPosition.OUTSIDE);
		labelConfig.setLabelProvider(new SeriesLabelProvider<Data>() {
			@Override
			public String getLabel(Data item, ValueProvider<? super Data, ? extends Number> vp) {
				Number value = vp.getValue(item);
				if (value.doubleValue() == 0) { return zeroText; }
				return value.doubleValue() < 0.001 ? "0+" : Format.xx_x(vp.getValue(item).doubleValue() * 100.0);
			}
		});
		labelConfig.setSpriteConfig(spriteConfig);
		labelConfig.setSpriteRenderer(new SeriesRenderer<Data>() {
			@Override
			public void spriteRenderer(Sprite sprite, int index, ListStore<Data> store) {
				TextSprite ts = (TextSprite)sprite;
				ts.setFill(
					zeroText.equals(ts.getText()) ? zeroColor : RGB.BLACK
				);
			}
		});

		BarSeries<Data> result = new BarSeries<>();
		result.setYAxisPosition(Position.RIGHT);
		result.addYField(properties.dayRatio());
		result.addColor(dayColor);
		result.addYField(properties.totalRatio());
		result.addColor(totalColor);
		result.setColumn(true);
		result.setGroupGutter(5);
		result.setLegendTitles(Arrays.asList("日佔比", "總佔比"));
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
