package us.dontcareabout.QtdClan.client.data;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import us.dontcareabout.QtdClan.client.data.TrendReadyEvent.TrendReadyHandler;

public class TrendReadyEvent extends GwtEvent<TrendReadyHandler> {
	public static final Type<TrendReadyHandler> TYPE = new Type<TrendReadyHandler>();

	@Override
	public Type<TrendReadyHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(TrendReadyHandler handler) {
		handler.onTrendReady(this);
	}

	public interface TrendReadyHandler extends EventHandler{
		public void onTrendReady(TrendReadyEvent event);
	}
}
