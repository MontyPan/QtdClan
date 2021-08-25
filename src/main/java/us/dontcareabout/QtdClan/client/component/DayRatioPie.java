package us.dontcareabout.QtdClan.client.component;

import java.util.HashMap;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import com.sencha.gxt.chart.client.chart.Chart;
import com.sencha.gxt.chart.client.chart.Legend;
import com.sencha.gxt.chart.client.chart.series.PieSeries;
import com.sencha.gxt.chart.client.chart.series.Series.LabelPosition;
import com.sencha.gxt.chart.client.chart.series.SeriesLabelConfig;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.data.shared.StringLabelProvider;

import us.dontcareabout.QtdClan.client.common.DamageAnalyser;
import us.dontcareabout.QtdClan.client.component.DayRatioPie.Data;
import us.dontcareabout.QtdClan.client.vo.LevelMantissa;
import us.dontcareabout.gxt.client.util.ColorUtil;

public class DayRatioPie extends Chart<Data>{
	private static final Properties properties = GWT.create(Properties.class);
	private ListStore<Data> store = new ListStore<>(new ModelKeyProvider<Data>() {
		@Override
		public String getKey(Data item) {
			return item.getName();
		}
	});
	private PieSeries<Data> series = new PieSeries<Data>();

	public DayRatioPie() {
		SeriesLabelConfig<Data> labelConfig = new SeriesLabelConfig<Data>();
		labelConfig.setLabelPosition(LabelPosition.START);
		labelConfig.setValueProvider(properties.name(), new StringLabelProvider<String>());
		labelConfig.setLabelContrast(true);

		series.setAngleField(properties.ratio());
		series.setLabelConfig(labelConfig);
		series.setHighlighting(true);
		series.setLegendLabelProvider(new LabelProvider<Data>() {
			@Override
			public String getLabel(Data item) {
				return item.getName();
			}
		});

		Legend<Data> legend = new Legend<Data>();
		legend.setPosition(Position.RIGHT);
		legend.setItemHighlighting(true);
		legend.setItemHiding(true);
		legend.getBorderConfig().setStrokeWidth(0);

		setStore(store);
		setLegend(legend);
		addSeries(series);
	}

	public void refresh(DamageAnalyser analyser, int dayIndex) {
		series.clear();

		LevelMantissa diffDamage = analyser.diffSum[dayIndex];
		HashMap<String, Double> ratioMap = new HashMap<>();

		for (String p : analyser.players) {
			LevelMantissa playerDiff = analyser.get(p).diffDamage[dayIndex];
			double ratio = LevelMantissa.divide(playerDiff, diffDamage);

			//佔比太低（包含沒打的）就跳過
			if (ratio < 0.01) { continue; }

			ratioMap.put(p, ratio);
		}

		List<Data> result = Lists.newArrayList();
		int i = 0;

		for (String p : ratioMap.keySet()) {
			Data d = new Data();
			d.setName(p);
			d.setRatio(ratioMap.get(p));
			result.add(d);
			series.addColor(ColorUtil.differential(i++));
		}

		store.replaceAll(result);
		redrawChart();
	}

	public class Data {
		String name;
		Double ratio;
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public Double getRatio() {
			return ratio;
		}
		public void setRatio(Double ratio) {
			this.ratio = ratio;
		}
	}

	interface Properties extends PropertyAccess<Data> {
		ValueProvider<Data, String> name();
		ValueProvider<Data, Double> ratio();
	}
}
