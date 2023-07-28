package com.tom.vivecraftcompat.overlay;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import org.vivecraft.ClientDataHolder;
import org.vivecraft.VRTextureTarget;
import org.vivecraft.api.VRData;
import org.vivecraft.extensions.GameRendererExtension;
import org.vivecraft.gameplay.screenhandlers.GuiHandler;
import org.vivecraft.gameplay.screenhandlers.KeyboardHandler;
import org.vivecraft.provider.ControllerType;
import org.vivecraft.provider.MCVR;
import org.vivecraft.utils.Utils;
import org.vivecraft.utils.math.Matrix4f;
import org.vivecraft.utils.math.Quaternion;
import org.vivecraft.utils.math.Vector3;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import com.tom.vivecraftcompat.access.MC;
import com.tom.vivecraftcompat.events.VRBindingsEvent;
import com.tom.vivecraftcompat.events.VRUpdateControllersEvent;
import com.tom.vivecraftcompat.overlay.OverlayLock.LockedPosition;

public class OverlayManager {
	public static Minecraft minecraft = Minecraft.getInstance();
	public static ClientDataHolder dh = ClientDataHolder.getInstance();
	private static List<Layer> screens = new ArrayList<>();
	private static boolean overlayRendering;

	public static void reinitBuffers() {
		screens.forEach(Layer::reinit);
	}

	public static void drawLayers(float partial) {
		if(screens.isEmpty())return;
		MC mc = (MC) minecraft;
		RenderTarget bak = minecraft.getMainRenderTarget();
		overlayRendering = true;
		for (Layer layer : screens) {
			mc.mc$setMainRenderTarget(layer.getFramebuffer());
			layer.framebuffer.clear(Minecraft.ON_OSX);
			layer.framebuffer.bindWrite(true);

			((GameRendererExtension) minecraft.gameRenderer).drawScreen(partial, layer.screen, new PoseStack());
		}
		overlayRendering = false;
		mc.mc$setMainRenderTarget(bak);
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void overlayPre(RenderGuiOverlayEvent.Pre event) {
		if(overlayRendering)return;
		ResourceLocation rl = event.getOverlay().id();
		if(isOverlayDetached(rl)) {
			event.setCanceled(true);
		}
	}

	public static boolean isOverlayDetached(ResourceLocation rl) {
		return screens.stream().anyMatch(h -> h.screen instanceof HudOverlayScreen s && s.overlays.contains(rl));
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
			framebuffer = new VRTextureTarget("HudScreen", minecraft.getWindow().getScreenWidth(), minecraft.getWindow().getScreenHeight(), true, false, -1, false, true, false);
			screen.init(minecraft, minecraft.getWindow().getGuiScaledWidth(), minecraft.getWindow().getGuiScaledHeight());
		}

		public RenderTarget getFramebuffer() {
			if(framebuffer == null)reinit();
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
			ClientDataHolder dataholder = ClientDataHolder.getInstance();
			startController = controller;
			startControllerPose = dataholder.vrPlayer.vrdata_room_pre.getController(controller);
			startDragPosX = (float) pos.x;
			startDragPosY = (float) pos.y;
			startDragPosZ = (float) pos.z;
			startDragRot = new Quaternion(rotation);
			moved = false;
		}

		public void updateMovingLayer() {
			ClientDataHolder dataholder = ClientDataHolder.getInstance();

			if (startControllerPose != null) {
				VRData.VRDevicePose vrdata$vrdevicepose = dataholder.vrPlayer.vrdata_room_pre.getController(startController);
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
	}

	public static void forEachLayer(Consumer<Layer> renderLayer) {
		if(screens.isEmpty())return;
		new ArrayList<>(screens).forEach(renderLayer);
	}

	public static void renderLayers(Consumer<Layer> renderLayer) {
		forEachLayer(l -> {
			float gs = GuiHandler.guiScale;
			GuiHandler.guiScale = gs * l.scale;
			renderLayer.accept(l);
			GuiHandler.guiScale = gs;
		});
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
			if (layer.startControllerPose != null && MCVR.get().keyMenuButton.consumeClick()) {
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
}
