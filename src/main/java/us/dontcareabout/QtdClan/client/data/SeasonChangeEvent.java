package us.dontcareabout.QtdClan.client.data;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import us.dontcareabout.QtdClan.client.data.SeasonChangeEvent.SeasonChangeHandler;

public class SeasonChangeEvent extends GwtEvent<SeasonChangeHandler> {
	public static final Type<SeasonChangeHandler> TYPE = new Type<SeasonChangeHandler>();

	public final int data;

	public SeasonChangeEvent(int selectSeason) {
		this.data = selectSeason;
	}

	@Override
	public Type<SeasonChangeHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SeasonChangeHandler handler) {
		handler.onSelectSeasonChange(this);
	}

	public interface SeasonChangeHandler extends EventHandler{
		public void onSelectSeasonChange(SeasonChangeEvent event);
	}
}
