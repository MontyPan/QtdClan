package us.dontcareabout.QtdClan.client.component;

import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.form.FieldLabel;

import us.dontcareabout.QtdClan.client.common.DateUtil;
import us.dontcareabout.QtdClan.client.component.gf.PopUtil;
import us.dontcareabout.QtdClan.client.component.gf.PopUtil.IsDialogWidget;
import us.dontcareabout.QtdClan.client.data.DataCenter;
import us.dontcareabout.QtdClan.client.data.SeasonChangeEvent;
import us.dontcareabout.QtdClan.client.data.SeasonChangeEvent.SeasonChangeHandler;

public class SeasonSelectPanel extends FramedPanel implements IsDialogWidget {
	private TextButton submitBtn = new TextButton("確定");
	private SeasonComboBox seasonCB = new SeasonComboBox();

	public SeasonSelectPanel() {
		setHeaderVisible(false);

		FieldLabel fl = new FieldLabel();
		fl.setText("選擇賽季");
		fl.setLabelWidth(65);
		fl.setWidget(seasonCB);

		submitBtn.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				PopUtil.closeDialog();
				DataCenter.seasonChange(seasonCB.getValue());
			}
		});
		DataCenter.addSeasonChange(new SeasonChangeHandler() {
			@Override
			public void onSelectSeasonChange(SeasonChangeEvent event) {
				seasonCB.setValue(event.data);
			}
		});

		add(fl);
		addButton(submitBtn);
	}

	@Override
	public int dialogWidth() { return 200; }

	@Override
	public int dialogHeight() { return 120; }

	class SeasonComboBox extends ComboBox<Integer> {
		SeasonComboBox() {
			super(
				new ListStore<>(new ModelKeyProvider<Integer>() {
					@Override
					public String getKey(Integer item) {
						return item.toString();
					}
				}),
				new LabelProvider<Integer>() {
					@Override
					public String getLabel(Integer item) {
						return "第 N + " + item + " 季";
					}
				}
			);

			for (int i = DateUtil.nowSeason(); i >= 1; i--) {
				getStore().add(i);
			}

			setTriggerAction(TriggerAction.ALL);
		}
	}
}
