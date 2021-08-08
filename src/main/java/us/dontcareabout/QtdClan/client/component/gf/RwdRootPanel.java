package us.dontcareabout.QtdClan.client.component.gf;

import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.Component;
import com.sencha.gxt.widget.core.client.container.Viewport;

/**
 * 提供「以寬度辨識 device 種類」 ，並提供 {@link RootPanel} + {@link Viewport} 的能力。
 * 另外還提供：
 * <ul>
 * 	<li>mask（{@link #block(String)}）與 unmask（{@link #unblock()}）</li>
 * <ul>
 */
//目前沒有打算提供「device 改變時炸 event」的功能
//主要原因是在 GXT 的機制下，onResize() 本身就已經能處理 87%（也許 100%）的需求
public class RwdRootPanel extends Viewport {
	/**
	 * 每種 device 的寬度下限。例如 320 就是 {@link #DEVICE_MOBILE_S} 的最小值。
	 * <p>
	 * reference：Chrome DevTool
	 */
	public static final int[] WIDTH_DEMARCATION = {320, 375, 425, 768, 1024, 1440, 2560};
	public static final int DEVICE_MIN = 0;
	public static final int DEVICE_MOBILE_S = 1;
	public static final int DEVICE_MOBILE_M = 2;
	public static final int DEVICE_MOBILE_L = 3;
	public static final int DEVICE_TABLET = 4;
	public static final int DEVICE_LATOP = 5;
	public static final int DEVICE_LATOP_L = 6;
	public static final int DEVICE_4K = 7;

	private static RwdRootPanel instance;
	private static int deviceType = 0;

	/**
	 * 簡化 <code>RootPanel.get().add(new Viewport())</code>。
	 * 可重複呼叫，第二次後會將原先 {@link Viewport} 上的 widget 移除後再加到 {@link Viewport} 中。
	 */
	public static void setComponent(Component c) {
		ensureInstance();
		Widget child = instance.getWidget();

		if (child == c) { return; }
		if (child != null) { instance.remove(child); }

		instance.add(c);

		//終究還是要下 forceLayout()
		//不然切換 component 之後，新的 component 沒有觸發點去作 layout
		instance.forceLayout();
	}

	/**
	 * 值域：{@value #DEVICE_MIN}（{@link #DEVICE_MIN}）～ {@value #DEVICE_4K}（{@link #DEVICE_4K}）。
	 * 會用數字而不是 enum，是因為在後續使用上比較方便，
	 * 例如「如果 device type 是 mobile」就可以直接寫成 <code>if (getDeviceType() <= DEVICE_MOBILE_L)</code>。
	 */
	public static int getDeviceType() {
		return deviceType;
	}

	public static int getWidth() {
		ensureInstance();
		return instance.getOffsetWidth();
	}

	public static int getHeight() {
		ensureInstance();
		return instance.getOffsetHeight();
	}

	/**
	 * 等同 {@link Viewport#mask(String)}。
	 */
	public static void block(String string) {
		ensureInstance();
		instance.mask(string);
	}

	/**
	 * 等同 {@link Viewport#unmask()}。
	 */
	public static void unblock() {
		ensureInstance();
		instance.unmask();
	}

	private static void ensureInstance() {
		if (instance == null) {
			instance = new RwdRootPanel();
			RootPanel.get().add(instance);
		}
	}

	//////////////////////////////////////////////////////////////////

	@Override
	protected void doLayout() {
		int width = getOffsetWidth();

		deviceType = DEVICE_4K;

		for (int i = 0; i < WIDTH_DEMARCATION.length; i++) {
			if (width < WIDTH_DEMARCATION[i]) {
				deviceType = i;
				break;
			}
		}

		super.doLayout();
	}
}
