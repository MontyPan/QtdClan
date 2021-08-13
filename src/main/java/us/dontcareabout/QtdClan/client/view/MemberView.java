package us.dontcareabout.QtdClan.client.view;

import static us.dontcareabout.QtdClan.client.data.DataCenter.damageAnalyser;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;

import us.dontcareabout.QtdClan.client.component.MemberChart;
import us.dontcareabout.QtdClan.client.component.MemberGrid;
import us.dontcareabout.QtdClan.client.component.MemberGrid.Data;
import us.dontcareabout.QtdClan.client.data.DamageReadyEvent;
import us.dontcareabout.QtdClan.client.data.DamageReadyEvent.DamageReadyHandler;
import us.dontcareabout.QtdClan.client.data.DataCenter;
import us.dontcareabout.gxt.client.component.GFComposite;

public class MemberView extends GFComposite {
	private static MemberViewUiBinder uiBinder = GWT.create(MemberViewUiBinder.class);

	@UiField MemberGrid grid;
	@UiField MemberChart chart;

	public MemberView() {
		initWidget(uiBinder.createAndBindUi(this));

		grid.addSelectionHandler(new SelectionHandler<Data>() {
			@Override
			public void onSelection(SelectionEvent<Data> event) {
				chart.refresh(damageAnalyser, event.getSelectedItem().getName());
			}
		});
	}

	@Override
	protected void enrollWhenVisible() {
		enrollHR(
			DataCenter.addDamageReady(new DamageReadyHandler() {
				@Override
				public void onDamageReady(DamageReadyEvent event) {
					grid.refresh(damageAnalyser);
				}
			})
		);
	}

	interface MemberViewUiBinder extends UiBinder<Widget, MemberView> {}
}
