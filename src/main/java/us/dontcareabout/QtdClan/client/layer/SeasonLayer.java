package us.dontcareabout.QtdClan.client.layer;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.sencha.gxt.chart.client.draw.RGB;
import com.sencha.gxt.chart.client.draw.sprite.SpriteSelectionEvent;
import com.sencha.gxt.chart.client.draw.sprite.SpriteSelectionEvent.SpriteSelectionHandler;

import us.dontcareabout.QtdClan.client.common.DateUtil;
import us.dontcareabout.QtdClan.client.component.SeasonSelectPanel;
import us.dontcareabout.QtdClan.client.component.gf.PopUtil;
import us.dontcareabout.QtdClan.client.data.DataCenter;
import us.dontcareabout.QtdClan.client.data.SeasonChangeEvent;
import us.dontcareabout.QtdClan.client.data.SeasonChangeEvent.SeasonChangeHandler;
import us.dontcareabout.gxt.client.draw.Cursor;
import us.dontcareabout.gxt.client.draw.LTextSprite;
import us.dontcareabout.gxt.client.draw.LayerContainer;
import us.dontcareabout.gxt.client.draw.LayerSprite;

public class SeasonLayer extends LayerContainer {
	public static final DateTimeFormat dateFormat = DateTimeFormat.getFormat("yyyy/MM/dd");

	private static final SeasonSelectPanel seasonSelect = new SeasonSelectPanel();

	private NameLayer nameL = new NameLayer();

	public SeasonLayer() {
		addLayer(nameL);
		nameL.addSpriteSelectionHandler(new SpriteSelectionHandler() {
			@Override
			public void onSpriteSelect(SpriteSelectionEvent event) {
				PopUtil.showDialog(seasonSelect);
			}
		});

		DataCenter.addSeasonChange(new SeasonChangeHandler() {
			@Override
			public void onSelectSeasonChange(SeasonChangeEvent event) {
				nameL.setSession(event.data);
			}
		});
	}

	@Override
	protected void adjustMember(int width, int height) {
		nameL.setLX(5);
		nameL.setLY(5);
		nameL.resize(180, height - 10);
	}

	class NameLayer extends LayerSprite {
		LTextSprite nameTS = new LTextSprite();
		LTextSprite dateTS = new LTextSprite();

		NameLayer() {
			nameTS.setFontSize(30);
			nameTS.setFill(RGB.WHITE);
			dateTS.setFontSize(16);
			dateTS.setFill(RGB.WHITE);
			add(nameTS);
			add(dateTS);

			setBgColor(RGB.DARKGRAY);
			setBgRadius(5);
			setMemberCursor(Cursor.POINTER);
		}

		@Override
		protected void adjustMember() {
			nameTS.setLX(10);
			nameTS.setLY(8);
			dateTS.setLX(10);
			dateTS.setLY(50);
		}

		void setSession(int i) {
			nameTS.setText("第 N + " + i + " 季");
			dateTS.setText(
				dateFormat.format(DateUtil.sessionStart(i)) + "～" +
				dateFormat.format(DateUtil.sessionEnd(i))
			);
			redraw();
		}
	}
}
