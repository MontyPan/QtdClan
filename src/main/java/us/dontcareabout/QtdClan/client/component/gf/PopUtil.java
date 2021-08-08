package us.dontcareabout.QtdClan.client.component.gf;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.Window;

public class PopUtil {

	//本來是走 private final 那一套
	//但實務上會有一些東西要設定，例如 setHeaderVisible() 之類的
	//然後又發現 setHeaderVisible() 會讓 header 有奇妙的高度改變...... (ry
	//所以改成直接開放 access + buildDialog()，caller 要怎麼搞就... 去吧 XD
	public static Window dialog;
	static { buildDialog(); }

	public static void buildDialog() {
		dialog = new Window();
		dialog.setModal(true);
		dialog.setResizable(false);
		dialog.setHeaderVisible(true);
	}

	public static void closeDialog() {
		dialog.hide();
	}

	public static void showDialog(Widget widget, int width, int height) {
		dialog.clear();
		dialog.add(widget);
		dialog.show();
		dialog.setPixelSize(width, height);
		dialog.center();
	}

	public static void showDialog(IsDialogWidget widget) {
		showDialog(widget.asWidget(), widget.dialogWidth(), widget.dialogHeight());
	}

	public interface IsDialogWidget extends IsWidget {
		int dialogWidth();
		int dialogHeight();
	}
}
