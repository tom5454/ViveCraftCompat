package com.tom.vivecraftcompat.overlay;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import org.vivecraft.client.VivecraftVRMod;
import org.vivecraft.client.utils.Utils;
import org.vivecraft.client_vr.ClientDataHolderVR;
import org.vivecraft.client_vr.VRData;
import org.vivecraft.client_vr.VRTextureTarget;
import org.vivecraft.client_vr.gameplay.screenhandlers.GuiHandler;
import org.vivecraft.client_vr.gameplay.screenhandlers.KeyboardHandler;
import org.vivecraft.client_vr.provider.ControllerType;
import org.vivecraft.client_vr.provider.MCVR;
import org.vivecraft.client_vr.provider.openvr_lwjgl.VRInputAction.KeyListener;
import org.vivecraft.client_vr.render.helpers.RenderHelper;
import org.vivecraft.common.utils.math.Matrix4f;
import org.vivecraft.common.utils.math.Quaternion;
import org.vivecraft.common.utils.math.Vector3;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;

import com.tom.vivecraftcompat.VRMode;
import com.tom.vivecraftcompat.access.MC;
import com.tom.vivecraftcompat.events.VRBindingsEvent;
import com.tom.vivecraftcompat.events.VRUpdateControllersEvent;
import com.tom.vivecraftcompat.overlay.OverlayLock.LockedPosition;

public class OverlayManager {
	public static Minecraft minecraft = Minecraft.getInstance();
	public static ClientDataHolderVR dh = ClientDataHolderVR.getInstance();
	private static List<Layer> screens = new ArrayList<>();
	private static boolean overlayRendering;

	public static void reinitBuffers() {
		screens.forEach(Layer::reinit);
	}

	public static void drawLayers(DeltaTracker.Timer partial) {
		if(!VRMode.isVR() || screens.isEmpty())return;
		MC mc = (MC) minecraft;
		RenderTarget bak = minecraft.getMainRenderTarget();
		overlayRendering = true;
		for (Layer layer : screens) {
			mc.mc$setMainRenderTarget(layer.getFramebuffer());
			layer.framebuffer.clear(Minecraft.ON_OSX);
			layer.framebuffer.bindWrite(true);
			GuiGraphics guiGraphics = new GuiGraphics(minecraft, minecraft.renderBuffers().bufferSource());
			RenderHelper.drawScreen(partial, layer.screen, guiGraphics);
			guiGraphics.flush();
		}
		overlayRendering = false;
		mc.mc$setMainRenderTarget(bak);
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void overlayPre(RenderGuiLayerEvent.Pre event) {
		if(overlayRendering || !VRMode.isVR())return;
		ResourceLocation rl = event.getName();
		if(isOverlayDetached(rl)) {
			event.setCanceled(true);
		}
	}

	public static void populateListeners() {
		registerListener(minecraft.options.keyAttack, 0);
		registerListener(minecraft.options.keyUse, 1);
		registerListener(GuiHandler.keyLeftClick, 0);
		registerListener(GuiHandler.keyRightClick, 1);
	}

	private static void registerListener(KeyMapping map, int kb) {
		MCVR.get().getInputAction(map).registerListener(new KeyListener() {

			@Override
			public boolean onUnpressed(ControllerType var1) {
				return interact(kb, false);
			}

			@Override
			public boolean onPressed(ControllerType var1) {
				return interact(kb, true);
			}

			@Override
			public int getPriority() {
				return 0;
			}
		});
	}

	public static boolean isOverlayDetached(ResourceLocation rl) {
		return VRMode.isVR() && screens.stream().anyMatch(h -> h.screen instanceof HudOverlayScreen s && s.isEnabled() && s.overlays.contains(rl));
	}

	public static void addLayer(Layer layer) {
		screens.add(layer);
	}

	public static class Layer {
		private RenderTarget framebuffer = null;
		private Screen screen;
		private Vec3 pos = new Vec3(0.0D, 0.0D, 0.0D);
		private Matrix4f rotation = new Matrix4f();
		private OverlayLock lock = OverlayLock.FLOAT;
		private float scale = 1f;

		private int startController;
		private VRData.VRDevicePose startControllerPose;
		private float startDragPosX;
		private float startDragPosY;
		private float startDragPosZ;
		private Quaternion startDragRot;
		private boolean moved;
		private OverlayLock preMoveLock;

		public Layer(Screen screen) {
			this.screen = screen;
		}

		public Layer(Function<Layer, Screen> screen) {
			this.screen = screen.apply(this);
		}

		public void reinit() {
			if(framebuffer != null)framebuffer.destroyBuffers();
			framebuffer = null;
			if(VRMode.isVR()) {
				framebuffer = new VRTextureTarget("HudScreen", GuiHandler.guiWidth, GuiHandler.guiHeight, true, false, -1, false, true, false);
				int l2 = minecraft.getWindow().getGuiScaledWidth();
				int j3 = minecraft.getWindow().getGuiScaledHeight();
				screen.init(minecraft, l2, j3);
			}
		}

		public RenderTarget getFramebuffer() {
			if(VRMode.isVR() && framebuffer == null)reinit();
			return framebuffer;
		}

		public Vec3 getPos() {
			if(startControllerPose != null)return pos;
			LockedPosition pose = lock.getLocked();
			if(pose == null)
				return pos;
			return pose.getMatrix().transform(new Vector3(pos)).add(new Vector3(pose.getPosition())).toVector3d();
		}

		public Matrix4f getRotation() {
			if(startControllerPose != null)return rotation;
			LockedPosition pose = lock.getLocked();
			if(pose == null)
				return rotation;
			return Matrix4f.multiply(pose.getMatrix(), rotation);
		}

		public void spawnOverlay(ControllerType controller) {
			VRData.VRDevicePose vrdata$vrdevicepose = dh.vrPlayer.vrdata_room_pre.hmd;
			float f = 2.0F;
			int i = 0;

			if (controller == ControllerType.LEFT)
				i = 1;

			vrdata$vrdevicepose = dh.vrPlayer.vrdata_room_pre.getController(i);
			f = 1.2F;

			Vec3 vec3 = vrdata$vrdevicepose.getPosition();
			Vec3 vec31 = new Vec3(0.0D, 0.0D, (-f));
			Vec3 vec32 = vrdata$vrdevicepose.getCustomVector(vec31);
			pos = new Vec3(vec32.x / 2.0D + vec3.x, vec32.y / 2.0D + vec3.y, vec32.z / 2.0D + vec3.z);
			Vector3 vector3 = new Vector3();
			vector3.setX((float)(pos.x - vec3.x));
			vector3.setY((float)(pos.y - vec3.y));
			vector3.setZ((float)(pos.z - vec3.z));
			float f1 = (float)Math.asin(vector3.getY() / vector3.length());
			float f2 = (float)((float)Math.PI + Math.atan2(vector3.getX(), vector3.getZ()));
			rotation = Matrix4f.rotationY(f2);
			Matrix4f matrix4f = Utils.rotationXMatrix(f1);
			rotation = Matrix4f.multiply(rotation, matrix4f);
		}

		public void remove() {
			if(framebuffer != null) {
				RenderTarget f = framebuffer;
				framebuffer = null;
				RenderSystem.recordRenderCall(() -> f.destroyBuffers());
			}
			screens.remove(this);
		}

		public void setPos(Vec3 pos) {
			this.pos = pos;
		}

		public void setRotation(Matrix4f rotation) {
			this.rotation = rotation;
		}

		public void startMovingLayer(int controller) {
			preMoveLock = lock;
			setLock(OverlayLock.FLOAT);
			startController = controller;
			startControllerPose = dh.vrPlayer.vrdata_room_pre.getController(controller);
			startDragPosX = (float) pos.x;
			startDragPosY = (float) pos.y;
			startDragPosZ = (float) pos.z;
			startDragRot = new Quaternion(rotation);
			moved = false;
		}

		public void updateMovingLayer() {
			if (startControllerPose != null) {
				VRData.VRDevicePose vrdata$vrdevicepose = dh.vrPlayer.vrdata_room_pre.getController(startController);
				Vec3 vec3 = startControllerPose.getPosition();
				Vec3 vec31 = vrdata$vrdevicepose.getPosition().subtract(vec3);
				Matrix4f matrix4f = Matrix4f.multiply(vrdata$vrdevicepose.getMatrix(), startControllerPose.getMatrix().inverted());
				Vector3 vector3 = new Vector3(startDragPosX - (float)vec3.x, startDragPosY - (float)vec3.y, startDragPosZ - (float)vec3.z);
				Vector3 vector31 = matrix4f.transform(vector3);
				float px = startDragPosX + (float)vec31.x + (vector31.getX() - vector3.getX());
				float py = startDragPosY + (float)vec31.y + (vector31.getY() - vector3.getY());
				float pz = startDragPosZ + (float)vec31.z + (vector31.getZ() - vector3.getZ());
				setPos(new Vec3(px, py, pz));
				setRotation(Matrix4f.multiply(matrix4f, new Matrix4f(startDragRot)));
				moved = true;
			}
		}

		public void stopMovingLayer() {
			startControllerPose = null;

			if (moved) {
				setLock(preMoveLock);
				moved = false;
			}
		}

		public boolean isMoving() {
			return startControllerPose != null;
		}

		public Screen getScreen() {
			return screen;
		}

		public void setLock(OverlayLock lock) {
			setPos(getPos());
			setRotation(getRotation());
			this.lock = lock;
			LockedPosition pose = lock.getLocked();
			if (pose != null) {
				Matrix4f mat = Matrix4f.multiply(pose.getMatrix().inverted(), rotation);
				setPos(pose.getMatrix().inverted().transform(new Vector3(pos).subtract(new Vector3(pose.getPosition()))).toVector3d());
				setRotation(mat);
			}
		}

		public OverlayLock getLock() {
			return lock;
		}

		public void setScale(float scale) {
			this.scale = scale;
		}

		public float getScale() {
			return scale;
		}

		public Vec3 getPosRaw() {
			return pos;
		}

		public Matrix4f getRotationRaw() {
			return rotation;
		}

		public void setLockDirect(OverlayLock lock) {
			this.lock = lock;
		}
	}

	public static void forEachLayer(Consumer<Layer> renderLayer) {
		if(screens.isEmpty())return;
		new ArrayList<>(screens).forEach(renderLayer);
	}

	public static void renderLayers(Consumer<Layer> renderLayer) {
		if(!VRMode.isVR())return;
		forEachLayer(l -> {
			float gs = GuiHandler.guiScale;
			GuiHandler.guiScale = gs * l.scale;
			renderLayer.accept(l);
			GuiHandler.guiScale = gs;
		});
	}

	@SubscribeEvent
	public static void tick(ClientTickEvent.Post event) {
		if(!VRMode.isVR())return;
		forEachLayer(l -> l.screen.tick());
	}

	@SubscribeEvent
	public static void processGui(VRUpdateControllersEvent event) {
		forEachLayer(s -> {
			if(s.screen instanceof VRInteractableScreen i)
				i.processGui();
			s.updateMovingLayer();
		});
	}

	@SubscribeEvent
	public static void processBindings(VRBindingsEvent event) {
		for (Layer layer : screens) {
			if (layer.startControllerPose != null && VivecraftVRMod.INSTANCE.keyMenuButton.consumeClick()) {
				layer.stopMovingLayer();
			}
		}
		if(KeyboardHandler.Showing)return;
		forEachLayer(s -> {
			if(s.screen instanceof VRInteractableScreen i)
				i.processBindings();
		});
	}

	public static boolean isUsingController() {
		return screens.stream().anyMatch(l -> l.screen instanceof VRInteractableScreen i && i.isUsingController());
	}

	public static Optional<Layer> findLayer(Screen s) {
		return screens.stream().filter(l -> l.screen == s).findFirst();
	}

	public static boolean type(char character) {
		boolean[] found = new boolean[] {false};
		forEachLayer(s -> {
			if(found[0])return;
			if(s.screen instanceof VRInteractableScreen i)
				found[0] = i.type(character);
		});
		return found[0];
	}

	public static boolean key(int key) {
		boolean[] found = new boolean[] {false};
		forEachLayer(s -> {
			if(found[0])return;
			if(s.screen instanceof VRInteractableScreen i)
				found[0] = i.key(key);
		});
		return found[0];
	}

	public static boolean interact(int key, boolean press) {
		boolean[] found = new boolean[] {false};
		forEachLayer(s -> {
			if(found[0])return;
			if(s.screen instanceof VRInteractableScreen i)
				found[0] = i.interact(key, press);
		});
		return found[0];
	}
}
