package us.dontcareabout.QtdClan.client.component;

import com.sencha.gxt.chart.client.draw.Color;
import com.sencha.gxt.chart.client.draw.RGB;
import com.sencha.gxt.core.client.util.DateWrapper;

import us.dontcareabout.QtdClan.client.common.DamageAnalyser;
import us.dontcareabout.QtdClan.client.data.DataCenter;
import us.dontcareabout.QtdClan.client.vo.Trend;
import us.dontcareabout.gxt.client.draw.LayerContainer;
import us.dontcareabout.gxt.client.draw.LayerSprite;
import us.dontcareabout.gxt.client.draw.component.TextButton;
import us.dontcareabout.gxt.client.draw.layout.VerticalLayoutLayer;

public class DailyBoard extends LayerContainer {
	private static final Color YELLOW = new RGB("#ffb300");
	
	private VerticalLayoutLayer root = new VerticalLayoutLayer();
	private LayerSprite bg = new LayerSprite();

	private InfoLayer numberInfo = new InfoLayer("參戰人數");
	private InfoLayer orderInfo = new InfoLayer("戰隊排名");

	public DailyBoard() {
		bg.setBgColor(RGB.WHITE);
		addLayer(bg);

		root.setMargins(10);
		root.setGap(20);
		root.addChild(numberInfo, 0.5);
		root.addChild(orderInfo, 0.5);
		addLayer(root);
	}

	public void refresh(DamageAnalyser analyser, int dayIndex) {
		DateWrapper start = new DateWrapper(analyser.startDate);
		Trend trend = DataCenter.findTrend(start.addDays(dayIndex).asDate());
		orderInfo.setValue(trend.getOrder());
		numberInfo.setValue(analyser.attendance[dayIndex]);

		if (dayIndex == 0) {
			orderInfo.setDiff(null);
			numberInfo.setDiff(null);
		} else {
			orderInfo.setDiff(
				DataCenter.findTrend(start.addDays(dayIndex - 1).asDate()).getOrder()
				- trend.getOrder()
			);
			numberInfo.setDiff(analyser.attendance[dayIndex] - analyser.attendance[dayIndex - 1]);
		}

		redrawSurface();
	}

	@Override
	protected void adjustMember(int width, int height) {
		bg.resize(width, height);
		root.resize(width, height);
	}

	private class InfoLayer extends LayerSprite {
		private TextButton titleTB = new TextButton();
		private TextButton valueTB = new TextButton();
		private TextButton diffTB = new TextButton();

		InfoLayer(String name) {
			titleTB.setText(name);
			add(titleTB);
			add(valueTB);
			add(diffTB);
		}

		@Override
		protected void adjustMember() {
			int titleH = 80;
			titleTB.setLX(0);
			titleTB.setLY(0);
			titleTB.resize(getWidth(), titleH);

			double h = getHeight() - titleH;
			valueTB.setLX(0);
			valueTB.setLY(titleH);
			valueTB.resize(getWidth() * 0.7, h);

			diffTB.setLX(getWidth() * 0.7);
			diffTB.setLY(titleH + h * 0.5);
			diffTB.resize(getWidth() * 0.3, h * 0.4);
		}

		void setValue(int value) {
			valueTB.setText("" + value);
		}

		void setDiff(Integer diff) {
			if (diff == null || diff == 0) {
				diffTB.setText("--");
				diffTB.setTextColor(YELLOW);
			} else if (diff > 0) {
				diffTB.setText("↑" + diff);
				diffTB.setTextColor(RGB.RED);
			} else {
				diffTB.setText("↓" + (diff * -1));
				diffTB.setTextColor(RGB.GREEN);
			}
		}
	}
}
