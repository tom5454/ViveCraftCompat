package com.tom.vivecraftcompat.overlay;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.vivecraft.client.VivecraftVRMod;
import org.vivecraft.client_vr.provider.ControllerType;
import org.vivecraft.client_vr.provider.MCVR;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.gui.GuiLayerManager.NamedLayer;

import com.tom.cpl.gui.Frame;
import com.tom.cpl.gui.IGui;
import com.tom.cpl.gui.elements.Button;
import com.tom.cpl.gui.elements.ConfirmPopup;
import com.tom.cpl.gui.elements.DropDownBox;
import com.tom.cpl.gui.elements.InputPopup;
import com.tom.cpl.gui.elements.Label;
import com.tom.cpl.gui.elements.ListPicker;
import com.tom.cpl.gui.elements.Panel;
import com.tom.cpl.gui.elements.ScrollPanel;
import com.tom.cpl.gui.elements.Slider;
import com.tom.cpl.gui.elements.Tooltip;
import com.tom.cpl.gui.util.FlowLayout;
import com.tom.cpl.math.Box;
import com.tom.cpl.util.NamedElement;
import com.tom.cpl.util.NamedElement.NameMapper;
import com.tom.vivecraftcompat.VRMode;
import com.tom.vivecraftcompat.overlay.OverlayManager.Layer;

public class OverlaySettingsGui extends Frame {
	private List<OverlayElement> overlays;
	private DropDownBox<OverlayElement> overlaysBox;
	private NameMapper<ResourceLocation> elementNames;
	private ListPicker<NamedElement<ResourceLocation>> elementsBox;
	private ScrollPanel currentElementsScp;
	private Panel currentElements;
	private FlowLayout currentElementsLayout;
	private DropDownBox<NamedElement<OverlayLock>> overlayLockBox;
	private NameMapper<OverlayLock> overlayLockNames;
	private DropDownBox<NamedElement<OverlayEnable>> overlayEnableBox;
	private NameMapper<OverlayEnable> overlayEnableNames;
	private Button btnAdd, btnDel;
	private Set<ResourceLocation> allElements;
	private Slider sliderScale;
	private Button btnMoveL, btnMoveR;

	public OverlaySettingsGui(IGui gui) {
		super(gui);

		gui.setCloseListener(t -> {
			updateOutlines(null);
			OverlayConfig.saveOverlays();
			t.run();
		});
	}

	@Override
	public void initFrame(int width, int height) {
		allElements = OverlayAccess.getLayers().stream().map(NamedLayer::name).collect(Collectors.toSet());

		overlays = new ArrayList<>();
		overlays.add(new OverlayElement(null));
		OverlayManager.forEachLayer(l -> {
			if(l.getScreen() instanceof HudOverlayScreen h) {
				overlays.add(new OverlayElement(h));
				allElements.removeAll(h.overlays);
			}
		});

		int w = width;
		int h = height - 20;

		Panel p = new Panel(gui);
		p.setBounds(new Box(0, 20, w, h));
		p.setBackgroundColor(gui.getColors().popup_background & 0x80FFFFFF);
		addElement(p);

		Button btnClose = new Button(gui, "X", gui::closeGui);
		btnClose.setBounds(new Box(w - 20, 0, 20, 20));
		p.addElement(btnClose);

		int lw = w / 2 - 10;
		int rw = w / 2 - 50;
		int rx = lw + 10;
		int rx2 = rx + rw + 5;

		overlaysBox = new DropDownBox<>(gui.getFrame(), overlays);
		overlaysBox.setBounds(new Box(rx, 5, rw, 20));
		p.addElement(overlaysBox);

		overlaysBox.setAction(this::updateCurrentElementsList);

		Button btnRename = new Button(gui, "E", () -> {
			if (overlaysBox.getSelected() != null && overlaysBox.getSelected().overlay != null) {
				HudOverlayScreen scr = overlaysBox.getSelected().overlay;
				openPopup(new InputPopup(this, gui.i18nFormat("vivecraftcompat.gui.overlay.editname"), scr::setName, null));
			}
		});
		btnRename.setBounds(new Box(rx2, 5, 20, 20));
		p.addElement(btnRename);

		btnDel = new Button(gui, "-", ConfirmPopup.confirmHandler(this, gui.i18nFormat("vivecraftcompat.gui.overlay.delete"), this::deleteOverlay));
		btnDel.setBounds(new Box(rx2, 30, 20, 20));
		p.addElement(btnDel);

		elementNames = new NameMapper<>(allElements, rl -> gui.i18nFormat(rl.toLanguageKey("overlay")));
		elementsBox = new ListPicker<>(gui.getFrame(), elementNames.asList());
		elementsBox.setBounds(new Box(5, 5, lw, 20));
		p.addElement(elementsBox);

		btnAdd = new Button(gui, gui.i18nFormat("vivecraftcompat.gui.overlay.add"), this::addElementToOverlay);
		btnAdd.setBounds(new Box(5, 30, lw, 20));
		p.addElement(btnAdd);

		elementsBox.setAction(() -> {
			btnAdd.setEnabled(elementsBox.getSelected() != null);
		});

		Panel rp = new Panel(gui);
		rp.setBounds(new Box(0, 0, rw + 5, 100));

		ScrollPanel rscp = new ScrollPanel(gui);
		rscp.setDisplay(rp);
		rscp.setBounds(new Box(lw + 5, 30, rw + 5, h - 30));
		p.addElement(rscp);

		initLayerSettings(rp, rw);

		currentElementsScp = new ScrollPanel(gui);
		currentElements = new Panel(gui);
		currentElements.setBackgroundColor(gui.getColors().button_border);
		currentElementsScp.setDisplay(currentElements);
		currentElements.setBounds(new Box(0, 0, lw, 0));
		currentElementsScp.setBounds(new Box(5, 65, lw, h - 70));

		p.addElement(new Label(gui, gui.i18nFormat("vivecraftcompat.gui.overlay.current")).setBounds(new Box(5, 55, 0, 0)));
		p.addElement(currentElementsScp);

		currentElementsLayout = new FlowLayout(currentElements, 5, 1);

		updateCurrentElementsList();
	}

	private void initLayerSettings(Panel rp, int rw) {
		FlowLayout layout = new FlowLayout(rp, 5, 1);

		rp.addElement(new Label(gui, gui.i18nFormat("vivecraftcompat.gui.overlay.lock")).setBounds(new Box(5, 0, 100, 10)));

		overlayLockNames = new NameMapper<>(OverlayLock.values(), l -> gui.i18nFormat("vivecraftcompat.gui.overlay.lock." + l.name().toLowerCase(Locale.ROOT)));
		overlayLockBox = new DropDownBox<>(gui.getFrame(), overlayLockNames.asList());
		overlayLockBox.setBounds(new Box(5, 0, rw, 20));
		overlayLockNames.setSetter(overlayLockBox::setSelected);
		rp.addElement(overlayLockBox);

		overlayLockBox.setAction(() -> {
			if (overlaysBox.getSelected() != null && overlaysBox.getSelected().overlay != null) {
				overlaysBox.getSelected().overlay.layer.setLock(overlayLockBox.getSelected().getElem());
			}
		});

		Panel moveBtns = new Panel(gui);
		moveBtns.setBounds(new Box(0, 0, rw, 20));

		btnMoveL = new Button(gui, "L", () -> {
			if (overlaysBox.getSelected() != null && overlaysBox.getSelected().overlay != null) {
				overlaysBox.getSelected().overlay.layer.startMovingLayer(1);
			}
		});
		String leftBtn = VRMode.isVR() ? MCVR.get().getOriginName(MCVR.get().getInputAction(VivecraftVRMod.INSTANCE.keyMenuButton).getLastOrigin()) : "?";
		btnMoveL.setTooltip(new Tooltip(this, gui.i18nFormat("tooltip.vivecraftcompat.overlay.moveL", leftBtn)));
		btnMoveL.setBounds(new Box(5, 0, 20, 20));
		moveBtns.addElement(btnMoveL);

		btnMoveR = new Button(gui, "R", () -> {
			if (overlaysBox.getSelected() != null && overlaysBox.getSelected().overlay != null) {
				overlaysBox.getSelected().overlay.layer.startMovingLayer(0);
			}
		});
		btnMoveR.setBounds(new Box(5 + 25, 0, 20, 20));
		String rightBtn = VRMode.isVR() ? MCVR.get().getOriginName(MCVR.get().getInputAction(VivecraftVRMod.INSTANCE.keyMenuButton).getLastOrigin()) : "?";
		btnMoveR.setTooltip(new Tooltip(this, gui.i18nFormat("tooltip.vivecraftcompat.overlay.moveR", rightBtn)));
		moveBtns.addElement(btnMoveR);

		rp.addElement(moveBtns);

		sliderScale = new Slider(gui, formatScale(1));
		sliderScale.setValue(1 / 5f);
		sliderScale.setBounds(new Box(5, 0, rw, 20));
		rp.addElement(sliderScale);
		sliderScale.setAction(() -> {
			sliderScale.setText(formatScale(sliderScale.getValue() * 5));
			if (overlaysBox.getSelected() != null && overlaysBox.getSelected().overlay != null) {
				HudOverlayScreen s = overlaysBox.getSelected().overlay;
				s.layer.setScale(sliderScale.getValue() * 5);
			}
		});

		rp.addElement(new Label(gui, gui.i18nFormat("vivecraftcompat.gui.overlay.enable")).setBounds(new Box(5, 0, 100, 10)));

		overlayEnableNames = new NameMapper<>(OverlayEnable.values(), l -> gui.i18nFormat("vivecraftcompat.gui.overlay.enable." + l.name().toLowerCase(Locale.ROOT)));
		overlayEnableBox = new DropDownBox<>(gui.getFrame(), overlayEnableNames.asList());
		overlayEnableBox.setBounds(new Box(5, 0, rw, 20));
		overlayEnableNames.setSetter(overlayEnableBox::setSelected);
		rp.addElement(overlayEnableBox);

		overlayEnableBox.setAction(() -> {
			if (overlaysBox.getSelected() != null && overlaysBox.getSelected().overlay != null) {
				overlaysBox.getSelected().overlay.enable = overlayEnableBox.getSelected().getElem();
			}
		});

		layout.reflow();
	}

	private String formatScale(float def) {
		return gui.i18nFormat("vivecraftcompat.gui.overlay.scale", def);
	}

	private void addElementToOverlay() {
		if (elementsBox.getSelected() != null) {
			HudOverlayScreen ov;
			if (overlaysBox.getSelected() != null && overlaysBox.getSelected().overlay == null) {
				ov = new HudOverlayScreen(UUID.randomUUID().toString());
				ov.setName(gui.i18nFormat("vivecraftcompat.gui.overlay.name", overlays.size()));
				OverlayElement el = new OverlayElement(ov);
				overlays.add(el);
				overlaysBox.setSelected(el);

				Layer layer = new Layer(ov);
				ov.layer = layer;
				layer.spawnOverlay(ControllerType.RIGHT);
				OverlayManager.addLayer(layer);
				updateOutlines(ov);
			} else {
				ov = overlaysBox.getSelected().overlay;
			}
			ResourceLocation select = elementsBox.getSelected().getElem();
			ov.overlays.add(select);
			allElements.remove(select);
			elementNames.refreshValues();
			elementsBox.setSelected(null);
			updateCurrentElementsList();
		}
	}

	private void deleteOverlay() {
		if (overlaysBox.getSelected() != null && overlaysBox.getSelected().overlay != null) {
			HudOverlayScreen s = overlaysBox.getSelected().overlay;
			allElements.addAll(s.overlays);
			elementNames.refreshValues();

			currentElements.getElements().clear();
			currentElementsScp.setScrollY(0);

			overlaysBox.setSelected(overlays.get(0));
			overlays.removeIf(h -> h.overlay == s);
			s.layer.remove();

			btnDel.setEnabled(false);
		}
	}

	private void updateOutlines(HudOverlayScreen ov) {
		OverlayManager.forEachLayer(l -> {
			if(l.getScreen() instanceof HudOverlayScreen h) {
				h.outline = h == ov;
			}
		});
	}

	private void updateCurrentElementsList() {
		int w = currentElements.getBounds().w;
		currentElements.getElements().clear();
		currentElementsScp.setScrollY(0);
		boolean en = overlaysBox.getSelected() != null && overlaysBox.getSelected().overlay != null;
		btnDel.setEnabled(en);
		sliderScale.setEnabled(en);
		overlayLockBox.setEnabled(en);
		overlayEnableBox.setEnabled(en);
		updateOutlines(null);
		overlayLockNames.setValue(OverlayLock.FLOAT);
		overlayEnableNames.setValue(OverlayEnable.ALWAYS);
		if (en) {
			HudOverlayScreen s = overlaysBox.getSelected().overlay;
			overlayLockNames.setValue(s.layer.getLock());
			overlayEnableNames.setValue(s.enable);
			sliderScale.setValue(s.layer.getScale() / 5f);
			sliderScale.setText(formatScale(s.layer.getScale()));
			s.outline = true;
			s.overlays.forEach(l -> {
				Panel pn = new Panel(gui);

				pn.setBounds(new Box(0, 0, w, 30));

				pn.addElement(new Label(gui, gui.i18nFormat(l.toLanguageKey("overlay"))).setBounds(new Box(5, 5, 0, 0)));

				Button del = new Button(gui, "-", () -> {
					s.overlays.remove(l);
					allElements.add(l);
					elementNames.refreshValues();
					currentElements.getElements().remove(pn);
					currentElementsLayout.reflow();
				});
				del.setBounds(new Box(w - 25, 5, 20, 20));
				pn.addElement(del);

				currentElements.addElement(pn);
			});
			currentElementsLayout.reflow();
		}
	}

	@Override
	public void tick() {
		boolean en = overlaysBox.getSelected() != null && overlaysBox.getSelected().overlay != null && !overlaysBox.getSelected().overlay.layer.isMoving();
		btnMoveL.setEnabled(en);
		btnMoveR.setEnabled(en);
	}

	public class OverlayElement {
		private HudOverlayScreen overlay;

		public OverlayElement(HudOverlayScreen overlay) {
			this.overlay = overlay;
		}

		@Override
		public String toString() {
			return overlay == null ? gui.i18nFormat("vivecraftcompat.gui.overlay.create") : overlay.toString();
		}
	}
}
