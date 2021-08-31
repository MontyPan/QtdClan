package us.dontcareabout.QtdClan.client.component;

import java.util.Arrays;
import java.util.List;

import com.sencha.gxt.chart.client.draw.Color;
import com.sencha.gxt.chart.client.draw.RGB;
import com.sencha.gxt.chart.client.draw.sprite.SpriteSelectionEvent;
import com.sencha.gxt.chart.client.draw.sprite.SpriteSelectionEvent.SpriteSelectionHandler;
import com.sencha.gxt.core.client.util.Margins;

import us.dontcareabout.QtdClan.client.ui.UiCenter;
import us.dontcareabout.QtdClan.client.ui.event.ChangeViewEvent;
import us.dontcareabout.QtdClan.client.ui.event.ChangeViewEvent.ChangeViewHandler;
import us.dontcareabout.QtdClan.client.ui.event.ChangeViewEvent.View;
import us.dontcareabout.gxt.client.draw.LRectangleSprite;
import us.dontcareabout.gxt.client.draw.LayerContainer;
import us.dontcareabout.gxt.client.draw.component.TextButton;
import us.dontcareabout.gxt.client.draw.layout.HorizontalLayoutLayer;

public class ToolbarLayer extends LayerContainer {
	private final List<ToolItem> list;

	private HorizontalLayoutLayer layout = new HorizontalLayoutLayer();

	public ToolbarLayer() {

		list = Arrays.asList(
			new ToolItem("賽季排名趨勢", View.TeamTrend),
			new ToolItem("逐日分析報告", View.TeamDayByDay),
			new ToolItem("個人戰績圖表", View.MemberAnalysis)
		);

		for (ToolItem ti : list) {
			layout.addChild(ti, 1.0 / list.size());
		}

		layout.setMargins(10);
		layout.setGap(10);
		addLayer(layout);

		UiCenter.addChangeView(new ChangeViewHandler() {
			@Override
			public void onChangeView(ChangeViewEvent event) {
				for (ToolItem ti : list) {
					if (ti.view.equals(event.view)) {
						select(ti);
						return;
					}
				}
			}
		});
	}

	@Override
	protected void adjustMember(int width, int height) {
		layout.resize(width, height);
	}

	private void select(ToolItem ti) {
		for (ToolItem item : list) {
			item.setSelect(ti.equals(item));
		}
		redrawSurface();
	}

	private static final Margins margins = new Margins(5, 10, 10, 10);
	private static final Color BLACK = new RGB("#424242");

	class ToolItem extends TextButton {
		static final int radius = 5;

		final View view;
		final LRectangleSprite underLine = new LRectangleSprite();

		ToolItem(String text, View view) {
			super(text);
			this.view = view;

			setBgColor(RGB.DARKGRAY);
			setTextColor(RGB.WHITE);
			setBgRadius(radius * 2);
			setMargins(margins);
			addSpriteSelectionHandler(new Handler(this));

			underLine.setFill(BLACK);
			underLine.setRadius(radius);
			add(underLine);
		}

		void setSelect(boolean isSelect) {
			underLine.setHidden(!isSelect);
		}

		boolean isSelect() {
			return !underLine.isHidden();
		}

		@Override
		protected void adjustMember() {
			super.adjustMember();
			underLine.setLX(radius);
			underLine.setLY(getHeight() - radius * 1.7);
			underLine.setWidth(getWidth() - radius * 2);
			underLine.setHeight(radius * 1.5);
		}
	}

	class Handler implements SpriteSelectionHandler {
		final ToolItem source;

		Handler(ToolItem ti) {
			source = ti;
		}

		@Override
		public final void onSpriteSelect(SpriteSelectionEvent event) {
			if (source.isSelect()) { return; }

			select(source);
			UiCenter.changeView(source.view);
		}
	}
}
