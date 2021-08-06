package us.dontcareabout.QtdClan.client.component;

import java.util.ArrayList;
import java.util.Date;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor.Path;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.data.shared.Store.StoreSortInfo;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;

import us.dontcareabout.QtdClan.client.common.DamageAnalyser;
import us.dontcareabout.QtdClan.client.component.MemberGrid.Data;
import us.dontcareabout.QtdClan.client.util.HtmlTemplate;
import us.dontcareabout.QtdClan.client.vo.LevelMantissa;
import us.dontcareabout.gxt.client.component.Grid2;
import us.dontcareabout.gxt.client.util.ColumnConfigBuilder;

public class MemberGrid extends Grid2<Data> {
	private static final Properties properties = GWT.create(Properties.class);

	private double maxRatio;

	public MemberGrid() {
		init();
		getView().setForceFit(true);
	}

	public void refresh(DamageAnalyser analyser) {
		Date last = analyser.endDate;
		ArrayList<Data> result = new ArrayList<>();
		maxRatio = 0;

		for (String player : analyser.players) {
			Data data = new Data();
			data.setName(player);
			data.setRatio(LevelMantissa.divide(analyser.getDamage(player, last), analyser.sum));
			data.setAttendance(analyser.getAttendance(player));

			if (data.getRatio() > maxRatio) { maxRatio = data.getRatio(); }

			result.add(data);
		}

		getStore().replaceAll(result);
	}

	public HandlerRegistration addSelectionHandler(SelectionHandler<Data> h) {
		return getSelectionModel().addSelectionHandler(h);
	}

	@Override
	protected ListStore<Data> genListStore() {
		ListStore<Data> result = new ListStore<>(properties.key());
		result.addSortInfo(new StoreSortInfo<>(properties.ratio(), SortDir.DESC));
		return result;
	}

	@Override
	protected ColumnModel<Data> genColumnModel() {
		AbstractCell<Double> ratio = new AbstractCell<Double>() {
			@Override
			public void render(Context context, Double value, SafeHtmlBuilder sb) {
				sb.append(
					HtmlTemplate.tplt.ratioCell(
						value / maxRatio * (maxRatio > 0.5 ? 100 : 75)
					)
				);
			}
		};
		ArrayList<ColumnConfig<Data, ?>> list = new ArrayList<>();
		list.add(
			new ColumnConfigBuilder<Data, String>(properties.name())
				.setHeader("名稱").setWidth(80).build()
		);
		list.add(
			new ColumnConfigBuilder<Data, Double>(properties.ratio())
				.setHeader("貢獻度").setWidth(100).setCell(ratio).build()
		);
		list.add(
			new ColumnConfigBuilder<Data, Integer>(properties.attendance())
				.setHeader("參戰天數").setWidth(60).centerStyle().build()
		);
		return new ColumnModel<>(list);
	}

	public class Data {
		String name;
		Double ratio;
		int attendance;

		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public Double getRatio() {
			return ratio;
		}
		public void setRatio(Double ratio) {
			this.ratio = ratio;
		}
		public int getAttendance() {
			return attendance;
		}
		public void setAttendance(int attendance) {
			this.attendance = attendance;
		}
	}

	interface Properties extends PropertyAccess<Data> {
		@Path("name")
		ModelKeyProvider<Data> key();

		ValueProvider<Data, String> name();
		ValueProvider<Data, Double> ratio();
		ValueProvider<Data, Integer> attendance();
	}
}
