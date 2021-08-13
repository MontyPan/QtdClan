package us.dontcareabout.QtdClan.client.ui.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import us.dontcareabout.QtdClan.client.ui.event.ChangeViewEvent.ChangeViewHandler;

public class ChangeViewEvent extends GwtEvent<ChangeViewHandler> {
	public static final Type<ChangeViewHandler> TYPE = new Type<ChangeViewHandler>();

	public final View view;

	public ChangeViewEvent(View view) {
		this.view = view;
	}

	@Override
	public Type<ChangeViewHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ChangeViewHandler handler) {
		handler.onChangeView(this);
	}

	public interface ChangeViewHandler extends EventHandler{
		public void onChangeView(ChangeViewEvent event);
	}

	public enum View {
		MemberAnalysis, TeamTrend, TeamDayByDay
	}
}
