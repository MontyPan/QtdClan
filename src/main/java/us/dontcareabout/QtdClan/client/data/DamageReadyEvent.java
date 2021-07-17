package us.dontcareabout.QtdClan.client.data;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import us.dontcareabout.QtdClan.client.data.DamageReadyEvent.DamageReadyHandler;

public class DamageReadyEvent extends GwtEvent<DamageReadyHandler> {
	public static final Type<DamageReadyHandler> TYPE = new Type<DamageReadyHandler>();

	@Override
	public Type<DamageReadyHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(DamageReadyHandler handler) {
		handler.onDamageReady(this);
	}

	public interface DamageReadyHandler extends EventHandler{
		public void onDamageReady(DamageReadyEvent event);
	}
}
