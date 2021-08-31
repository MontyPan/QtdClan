package us.dontcareabout.QtdClan.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.Slider;

import us.dontcareabout.QtdClan.client.component.DailyBoard;
import us.dontcareabout.QtdClan.client.component.DayRatioPie;
import us.dontcareabout.QtdClan.client.data.DamageReadyEvent;
import us.dontcareabout.QtdClan.client.data.DamageReadyEvent.DamageReadyHandler;
import us.dontcareabout.QtdClan.client.data.DataCenter;

public class DailyView extends Composite {
	private static DailyViewUiBinder uiBinder = GWT.create(DailyViewUiBinder.class);

	@UiField DayRatioPie dayPie;
	@UiField Slider slider;
	@UiField DailyBoard board;

	public DailyView() {
		initWidget(uiBinder.createAndBindUi(this));
		DataCenter.addDamageReady(new DamageReadyHandler() {
			@Override
			public void onDamageReady(DamageReadyEvent event) {
				refresh();
			}
		});
	}

	@Override
	protected void onLoad() {
		super.onLoad();
		refresh();
	}

	@UiHandler("slider")
	void sliderChange(ValueChangeEvent<Integer> vce) {
		dayPie.refresh(DataCenter.damageAnalyser, vce.getValue() - 1);
		board.refresh(DataCenter.damageAnalyser, vce.getValue() - 1);
	}

	private void refresh() {
		slider.setMaxValue(DataCenter.damageAnalyser.days);
		//如果 slider 原本就是 1，那再次呼叫 refresh() 時不會觸發 event
		//所以只好先亂跳一個數字再跳回去 wwwwww
		slider.setValue(slider.getMaxValue());
		slider.setValue(1, true);
	}

	interface DailyViewUiBinder extends UiBinder<Widget, DailyView> {}
}
