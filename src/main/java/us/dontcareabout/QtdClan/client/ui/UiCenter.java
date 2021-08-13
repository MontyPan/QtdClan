package us.dontcareabout.QtdClan.client.ui;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.SimpleEventBus;

import us.dontcareabout.QtdClan.client.ui.event.ChangeViewEvent;
import us.dontcareabout.QtdClan.client.ui.event.ChangeViewEvent.ChangeViewHandler;
import us.dontcareabout.QtdClan.client.ui.event.ChangeViewEvent.View;

public class UiCenter {
	private final static SimpleEventBus eventBus = new SimpleEventBus();

	public static void changeView(View view) {
		eventBus.fireEvent(new ChangeViewEvent(view));
	}

	public static HandlerRegistration addChangeView(ChangeViewHandler handler) {
		return eventBus.addHandler(ChangeViewEvent.TYPE, handler);
	}
}
