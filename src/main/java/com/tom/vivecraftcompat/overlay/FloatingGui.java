package com.tom.vivecraftcompat.overlay;

import java.util.function.Function;

import org.lwjgl.glfw.GLFW;
import org.vivecraft.client.VivecraftVRMod;
import org.vivecraft.client_vr.extensions.GuiExtension;
import org.vivecraft.client_vr.gameplay.screenhandlers.GuiHandler;
import org.vivecraft.common.utils.math.Matrix4f;

import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;

import com.tom.cpl.gui.Frame;
import com.tom.cpl.gui.IGui;
import com.tom.cpl.gui.KeyboardEvent;
import com.tom.cpl.math.Box;
import com.tom.cpm.client.GuiImpl;
import com.tom.vivecraftcompat.overlay.OverlayManager.Layer;

public class FloatingGui extends GuiImpl implements VRInteractableScreen {
	private Layer layer;
	//private boolean PointedL;
	private boolean PointedR;
	//public float cursorX1;
	//public float cursorY1;
	public float cursorX2;
	public float cursorY2;
	private boolean lastPressedClick, lastPressedRClick;
	private boolean lastPressedShift;
	//private ControllerType activecontroller;
	private boolean shift;
	private Box clickBounds;

	public FloatingGui(Function<IGui, Frame> creator, Layer layer) {
		super(creator, null);
		this.layer = layer;
	}


	@Override
	protected void init() {
		clickBounds = new Box(0, 0, width, height);
		super.init();
	}

	@Override
	public void processGui() {
		//PointedL = false;
		PointedR = false;

		if (!OverlayManager.dh.vrSettings.seated) {
			Vec3 pos = layer.getPos();
			Matrix4f rotation = layer.getRotation();

			//Vec2 vec2 = GuiHandler.getTexCoordsForCursor(pos, rotation, this, GuiHandler.guiScale, OverlayManager.dh.vrPlayer.vrdata_room_pre.getController(1));
			Vec2 vec21 = GuiHandler.getTexCoordsForCursor(pos, rotation, this, GuiHandler.guiScale, OverlayManager.dh.vrPlayer.vrdata_room_pre.getController(0));
			float f = vec21.x;
			float f1 = vec21.y;

			if (!(f < 0.0F) && !(f1 < 0.0F) && !(f > 1.0F) && !(f1 > 1.0F)) {
				float lx = cursorX2;
				float ly = cursorY2;
				if (this.cursorX2 == -1.0F) {
					this.cursorX2 = ((int)(f * minecraft.getWindow().getScreenWidth()));
					this.cursorY2 = ((int)(f1 * minecraft.getWindow().getScreenHeight()));
					double cx = (double)(this.cursorX2 * this.width / this.minecraft.getWindow().getGuiScaledWidth()) * (double)this.minecraft.getWindow().getGuiScaledWidth() / this.minecraft.getWindow().getScreenWidth();
					double cy = (double)(this.cursorY2 * this.height / this.minecraft.getWindow().getGuiScaledHeight()) * (double)this.minecraft.getWindow().getGuiScaledWidth() / this.minecraft.getWindow().getScreenWidth();
					PointedR = clickBounds.isInBounds((int) cx, (int) cy);
				} else {
					float f2 = ((int)(f * minecraft.getWindow().getScreenWidth()));
					float f3 = ((int)(f1 * minecraft.getWindow().getScreenHeight()));
					this.cursorX2 = this.cursorX2 * 0.7F + f2 * 0.3F;
					this.cursorY2 = this.cursorY2 * 0.7F + f3 * 0.3F;
					double cx = (double)(this.cursorX2 * this.width / this.minecraft.getWindow().getGuiScaledWidth()) * (double)this.minecraft.getWindow().getGuiScaledWidth() / this.minecraft.getWindow().getScreenWidth();
					double cy = (double)(this.cursorY2 * this.height / this.minecraft.getWindow().getGuiScaledHeight()) * (double)this.minecraft.getWindow().getGuiScaledWidth() / this.minecraft.getWindow().getScreenWidth();
					PointedR = clickBounds.isInBounds((int) cx, (int) cy);
				}
				if (PointedR && lastPressedClick) {
					if (((int) lx) != ((int) cursorX2) || ((int) ly) != ((int) cursorY2)) {
						double d2 = (double)Math.min(Math.max((int)this.cursorX2, 0), minecraft.getWindow().getScreenWidth()) * (double)minecraft.getWindow().getGuiScaledWidth() / minecraft.getWindow().getScreenWidth();
						double d3 = (double)Math.min(Math.max((int)this.cursorY2, 0), minecraft.getWindow().getScreenWidth()) * (double)minecraft.getWindow().getGuiScaledHeight() / minecraft.getWindow().getScreenHeight();
						mouseDragged(d2, d3, 0, 0, 0);
					}
				}
			} else {
				this.cursorX2 = -1.0F;
				this.cursorY2 = -1.0F;
				PointedR = false;
			}

			/*f = vec2.x;
			f1 = vec2.y;

			if (!(f < 0.0F) && !(f1 < 0.0F) && !(f > 1.0F) && !(f1 > 1.0F)) {
				if (this.cursorX1 == -1.0F) {
					this.cursorX1 = ((int)(f * minecraft.getWindow().getScreenWidth()));
					this.cursorY1 = ((int)(f1 * minecraft.getWindow().getScreenHeight()));
					PointedL = true;
				} else {
					float f4 = ((int)(f * minecraft.getWindow().getScreenWidth()));
					float f5 = ((int)(f1 * minecraft.getWindow().getScreenHeight()));
					this.cursorX1 = this.cursorX1 * 0.7F + f4 * 0.3F;
					this.cursorY1 = this.cursorY1 * 0.7F + f5 * 0.3F;
					PointedL = true;
				}
			} else {
				this.cursorX1 = -1.0F;
				this.cursorY1 = -1.0F;
				PointedL = false;
			}*/
		}
	}

	@Override
	public void processBindings() {
		if (PointedR && minecraft.options.keyShift.consumeClick()) {
			setShift(true);
			lastPressedShift = true;
		}

		if (!minecraft.options.keyShift.isDown() && lastPressedShift) {
			setShift(false);
			lastPressedShift = false;
		}

		//double d0 = (double)Math.min(Math.max((int)this.cursorX1, 0), minecraft.getWindow().getScreenWidth()) * (double)minecraft.getWindow().getGuiScaledWidth() / minecraft.getWindow().getScreenWidth();
		//double d1 = (double)Math.min(Math.max((int)this.cursorY1, 0), minecraft.getWindow().getScreenWidth()) * (double)minecraft.getWindow().getGuiScaledHeight() / minecraft.getWindow().getScreenHeight();
		double d2 = (double)(this.cursorX2 * this.width / this.minecraft.getWindow().getGuiScaledWidth()) * (double)this.minecraft.getWindow().getGuiScaledWidth() / this.minecraft.getWindow().getScreenWidth();
		double d3 = (double)(this.cursorY2 * this.height / this.minecraft.getWindow().getGuiScaledHeight()) * (double)this.minecraft.getWindow().getGuiScaledWidth() / this.minecraft.getWindow().getScreenWidth();

		if (PointedR && GuiHandler.keyScrollUp.consumeClick()) {
			/*double cx, cy;
			if(activecontroller == ControllerType.LEFT) {
				cx = d0;
				cy = d1;
			} else {
				cx = d2;
				cy = d3;
			}*/
			mouseScrolled(d2, d3, 4);
		}

		if (PointedR && GuiHandler.keyScrollDown.consumeClick()) {
			/*double cx, cy;
			if(activecontroller == ControllerType.LEFT) {
				cx = d0;
				cy = d1;
			} else {
				cx = d2;
				cy = d3;
			}*/
			mouseScrolled(d2, d3, -4);
		}

		if(PointedR && VivecraftVRMod.INSTANCE.keyMenuButton.consumeClick()) {
			keyPressed(GLFW.GLFW_KEY_ESCAPE, 0, 0);
		}
	}

	private void setShift(boolean b) {
		shift = b;
	}

	@Override
	public boolean isShiftDown() {
		return shift;
	}

	@Override
	public boolean isUsingController() {
		return PointedR;
	}

	@Override
	public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		double cx, cy;
		/*if(activecontroller == ControllerType.LEFT) {
			cx = (double)(this.cursorX1 * this.width / this.minecraft.getWindow().getGuiScaledWidth()) * (double)this.minecraft.getWindow().getGuiScaledWidth() / this.minecraft.getWindow().getScreenWidth();
			cy = (double)(this.cursorY1 * this.height / this.minecraft.getWindow().getGuiScaledHeight()) * (double)this.minecraft.getWindow().getGuiScaledWidth() / this.minecraft.getWindow().getScreenWidth();
		} else {*/
		cx = (double)(this.cursorX2 * this.width / this.minecraft.getWindow().getGuiScaledWidth()) * (double)this.minecraft.getWindow().getGuiScaledWidth() / this.minecraft.getWindow().getScreenWidth();
		cy = (double)(this.cursorY2 * this.height / this.minecraft.getWindow().getGuiScaledHeight()) * (double)this.minecraft.getWindow().getGuiScaledWidth() / this.minecraft.getWindow().getScreenWidth();
		//}
		int mx = (int) cx;
		int my = (int) cy;
		if(PointedR)
			super.render(matrixStack, mx, my, partialTicks);
		else
			super.render(matrixStack, -1, -1, partialTicks);

		if(PointedR)
			((GuiExtension) this.minecraft.gui).vivecraft$drawMouseMenuQuad(mx, my);
	}

	@Override
	public void renderBackground(PoseStack pPoseStack, int pVOffset) {}

	public void drawTexture(int x, int y, int width, int height, float u1, float v1, float u2, float v2, RenderTarget framebuffer) {
		x += getOffset().x;
		y += getOffset().y;
		framebuffer.bindRead();
		RenderSystem.setShaderTexture(0, framebuffer.getColorTextureId());
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.enableBlend();
		RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
				GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE_MINUS_DST_ALPHA,
				GlStateManager.DestFactor.ONE);
		Tesselator tessellator = Tesselator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuilder();
		bufferbuilder.begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
		float bo = getBlitOffset() + 1;
		com.mojang.math.Matrix4f matrix = matrixStack.last().pose();
		bufferbuilder.vertex(matrix, x, y + height, bo).uv(u1, v2).endVertex();
		bufferbuilder.vertex(matrix, x + width, y + height, bo).uv(u2, v2).endVertex();
		bufferbuilder.vertex(matrix, x + width, y, bo).uv(u2, v1).endVertex();
		bufferbuilder.vertex(matrix, x, y, bo).uv(u1, v1).endVertex();
		BufferUploader.end(bufferbuilder);
		RenderSystem.disableBlend();
	}

	@Override
	public void onClose() {
		layer.remove();
	}

	@Override
	public void displayError(String e) {
		minecraft.player.displayClientMessage(new TextComponent(e), false);
		onClose();
	}

	public void setClickBounds(Box clickBounds) {
		this.clickBounds = clickBounds;
	}

	@Override
	public boolean type(char ch) {
		return charTyped(ch, 0);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		try {
			this.keyModif = modifiers;
			KeyboardEvent evt = new KeyboardEvent(keyCode, scanCode, (char) -1, GLFW.glfwGetKeyName(keyCode, scanCode));
			gui.keyPressed(evt);
			return evt.isConsumed();
		} catch (Throwable e) {
			onGuiException("Error processing key event", e, false);
			return true;
		}
	}


	@Override
	public boolean key(int key) {
		return keyPressed(key, 0, 0);
	}

	@Override
	public void setupCut() {
		if(!noScissorTest) {
			int dw = GuiHandler.guiWidth;
			int dh = GuiHandler.guiHeight;
			float multiplierX = dw / (float)width;
			float multiplierY = dh / (float)height;
			Box box = getContext().cutBox;
			RenderSystem.enableScissor((int) (box.x * multiplierX), dh - (int) ((box.y + box.h) * multiplierY),
					(int) (box.w * multiplierX), (int) (box.h * multiplierY));
		}
	}


	@Override
	public boolean interact(int key, boolean press) {
		double d2 = (double)(this.cursorX2 * this.width / this.minecraft.getWindow().getGuiScaledWidth()) * (double)this.minecraft.getWindow().getGuiScaledWidth() / this.minecraft.getWindow().getScreenWidth();
		double d3 = (double)(this.cursorY2 * this.height / this.minecraft.getWindow().getGuiScaledHeight()) * (double)this.minecraft.getWindow().getGuiScaledWidth() / this.minecraft.getWindow().getScreenWidth();
		if (key == 0) {
			if (press && PointedR) {
				mouseClicked(((int)d2), ((int)d3), 0);
				lastPressedClick = true;
				return true;
			} else if(lastPressedClick) {
				mouseReleased(((int)d2), ((int)d3), 0);
				lastPressedClick = false;
				return true;
			}
		} else if (key == 1) {
			if (press && PointedR) {
				layer.startMovingLayer(0);
				lastPressedRClick = true;
			} else if(lastPressedRClick) {
				lastPressedRClick = false;
				layer.stopMovingLayer();
			}
		}
		return false;
	}

	@Override
	public void tick() {
		if (gui == null || minecraft == null)return;
		try {
			gui.tick();
		} catch (Throwable e) {
			onGuiException("Error in tick gui", e, true);
		}
	}
}