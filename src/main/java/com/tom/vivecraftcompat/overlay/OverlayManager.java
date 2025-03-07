package com.tom.vivecraftcompat.overlay;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.vivecraft.client.VivecraftVRMod;
import org.vivecraft.client_vr.ClientDataHolderVR;
import org.vivecraft.client_vr.VRData;
import org.vivecraft.client_vr.VRTextureTarget;
import org.vivecraft.client_vr.gameplay.screenhandlers.GuiHandler;
import org.vivecraft.client_vr.gameplay.screenhandlers.KeyboardHandler;
import org.vivecraft.client_vr.provider.ControllerType;
import org.vivecraft.client_vr.provider.MCVR;
import org.vivecraft.client_vr.provider.openvr_lwjgl.VRInputAction.KeyListener;
import org.vivecraft.client_vr.render.helpers.RenderHelper;

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

	public static void resizeBuffers() {
		screens.forEach(Layer::resize);
	}

	public static void drawLayers(DeltaTracker.Timer partial) {
		if(!VRMode.isVR() || screens.isEmpty())return;
		MC mc = (MC) minecraft;
		RenderTarget bak = minecraft.getMainRenderTarget();
		overlayRendering = true;
		for (Layer layer : screens) {
			layer.initialize();
			if (layer.framebuffer == null)
				continue;//??
			mc.mc$setMainRenderTarget(layer.getFramebuffer());
			layer.framebuffer.clear(Minecraft.ON_OSX);
			layer.framebuffer.bindWrite(true);
			GuiGraphics guiGraphics = new GuiGraphics(minecraft, minecraft.renderBuffers().bufferSource());
			RenderHelper.drawScreen(guiGraphics, partial, layer.screen, true);
			guiGraphics.flush();
		}
		overlayRendering = false;
		mc.mc$setMainRenderTarget(bak);
		bak.bindWrite(true);
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
		registerListener(GuiHandler.KEY_LEFT_CLICK, 0);
		registerListener(GuiHandler.KEY_RIGHT_CLICK, 1);
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
		private Vector3f pos = new Vector3f(0.0F, 0.0F, 0.0F);
		private Matrix4f rotation = new Matrix4f();
		private OverlayLock lock = OverlayLock.FLOAT;
		private float scale = 1f;

		private int startController;
		private VRData.VRDevicePose startControllerPose;
		private float startDragPosX;
		private float startDragPosY;
		private float startDragPosZ;
		private Matrix4f startDragRot;
		private boolean moved;
		private OverlayLock preMoveLock;

		public Layer(Screen screen) {
			this.screen = screen;
		}

		public Layer(Function<Layer, Screen> screen) {
			this.screen = screen.apply(this);
		}

		public void resize() {
			if(VRMode.isVR()) {
				if(framebuffer == null) {
					framebuffer = new VRTextureTarget("VCC Overlay: " + screen.getClass().getName(), GuiHandler.GUI_WIDTH, GuiHandler.GUI_HEIGHT, true, -1, true, false, false);
				} else {
					framebuffer.resize(GuiHandler.GUI_WIDTH, GuiHandler.GUI_HEIGHT, Minecraft.ON_OSX);
				}
				int l2 = minecraft.getWindow().getGuiScaledWidth();
				int j3 = minecraft.getWindow().getGuiScaledHeight();
				screen.init(minecraft, l2, j3);
			}
		}

		public RenderTarget getFramebuffer() {
			return framebuffer;
		}

		public Vector3f getPos() {
			if(startControllerPose != null)return pos;
			LockedPosition pose = lock.getLocked();
			if(pose == null)
				return pos;
			Vec3 pp = pose.getPosition();
			var r = pose.getMatrix().transform(new Vector4f(pos, 1f)).add(new Vector4f((float) pp.x, (float) pp.y, (float) pp.z, 0f));
			return new Vector3f(r.x / r.w, r.y / r.w, r.z / r.w);
		}

		public Matrix4f getRotation() {
			if(startControllerPose != null)return rotation;
			LockedPosition pose = lock.getLocked();
			if(pose == null)
				return rotation;
			return pose.getMatrix().mul(rotation, new Matrix4f());
		}

		public void spawnOverlay(ControllerType controller) {
			VRData.VRDevicePose vrdata$vrdevicepose = dh.vrPlayer.vrdata_room_pre.hmd;
			float f = 2.0F;
			int i = 0;

			if (controller == ControllerType.LEFT)
				i = 1;

			vrdata$vrdevicepose = dh.vrPlayer.vrdata_room_pre.getController(i);
			f = 1.2F;

			Vector3f vec3 = vrdata$vrdevicepose.getPositionF();
			Vector3f vec31 = new Vector3f(0.0F, 0.0F, (-f));
			Vector3f vec32 = vrdata$vrdevicepose.getCustomVector(vec31);
			pos = new Vector3f(vec32.x / 2.0F + vec3.x, vec32.y / 2.0F + vec3.y, vec32.z / 2.0F + vec3.z);
			Vector3f vector3 = new Vector3f();
			vector3.x = (pos.x - vec3.x);
			vector3.y = (pos.y - vec3.y);
			vector3.z = (pos.z - vec3.z);
			float f1 = (float)Math.asin(vector3.y / vector3.length());
			float f2 = (float)((float)Math.PI + Math.atan2(vector3.x, vector3.z));
			rotation = new Matrix4f().rotationY(f2);
			Matrix4f matrix4f = new Matrix4f().rotationX(f1);
			rotation.mul(matrix4f);
		}

		public void remove() {
			if(framebuffer != null) {
				RenderTarget f = framebuffer;
				framebuffer = null;
				RenderSystem.recordRenderCall(() -> f.destroyBuffers());
			}
			screens.remove(this);
		}

		public void setPos(Vector3f pos) {
			this.pos = pos;
		}

		public void setPos(Vector4f pos) {
			this.pos = new Vector3f(pos.x, pos.y, pos.z);
		}

		public void setRotation(Matrix4f rotation) {
			this.rotation = rotation;
		}

		public void startMovingLayer(int controller) {
			preMoveLock = lock;
			setLock(OverlayLock.FLOAT);
			startController = controller;
			startControllerPose = dh.vrPlayer.vrdata_room_pre.getController(controller);
			startDragPosX = pos.x;
			startDragPosY = pos.y;
			startDragPosZ = pos.z;
			startDragRot = new Matrix4f(rotation);
			moved = false;
		}

		public void updateMovingLayer() {
			if (startControllerPose != null) {
				VRData.VRDevicePose vrdata$vrdevicepose = dh.vrPlayer.vrdata_room_pre.getController(startController);
				Vec3 vec3 = startControllerPose.getPosition();
				Vec3 vec31 = vrdata$vrdevicepose.getPosition().subtract(vec3);

				Matrix4f matrix4f = vrdata$vrdevicepose.getMatrix().mul(startControllerPose.getMatrix().invert(new Matrix4f()), new Matrix4f());
				Vector3f vector3 = new Vector3f(startDragPosX - (float)vec3.x, startDragPosY - (float)vec3.y, startDragPosZ - (float)vec3.z);
				Vector4f vector31 = matrix4f.transform(new Vector4f(vector3, 1));
				float px = startDragPosX + (float)vec31.x + (vector31.x - vector3.x);
				float py = startDragPosY + (float)vec31.y + (vector31.y - vector3.y);
				float pz = startDragPosZ + (float)vec31.z + (vector31.z - vector3.z);
				setPos(new Vector3f(px, py, pz));
				setRotation(matrix4f.mul(startDragRot));
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
				Matrix4f mat = pose.getMatrix().invert(new Matrix4f()).mul(rotation);
				setPos(pose.getMatrix().invert(new Matrix4f()).transform(new Vector4f(new Vector3f(pos).sub(pose.getPositionF()), 1)));
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

		public Vector3f getPosRaw() {
			return pos;
		}

		public Matrix4f getRotationRaw() {
			return rotation;
		}

		public void setLockDirect(OverlayLock lock) {
			this.lock = lock;
		}

		public void initialize() {
			if (framebuffer == null)resize();
		}

		public boolean ready() {
			return framebuffer != null;
		}

		public void addPos(Vector3f d) {
			pos.x += d.x * .025f;
			pos.y += d.y * .025f;
			pos.z += d.z * .025f;
		}

		public void addRotation(Vector3f d) {
			Matrix4f rotationMatrix = getRotationRaw();
			Quaternionf quaternion = new Quaternionf();
			rotationMatrix.getUnnormalizedRotation(quaternion);
			quaternion.rotateAxis((float) Math.toRadians(10), d);
			Matrix4f mat = new Matrix4f().rotate(quaternion);
			setRotation(mat);
		}
	}

	public static void forEachLayer(Consumer<Layer> renderLayer) {
		if(screens.isEmpty())return;
		new ArrayList<>(screens).forEach(renderLayer);
	}

	public static void renderLayers(Consumer<Layer> renderLayer) {
		if(!VRMode.isVR())return;
		forEachLayer(l -> {
			if (!l.ready())return;//skip initializing layers
			float gs = GuiHandler.GUI_SCALE;
			GuiHandler.GUI_SCALE = gs * l.scale;
			renderLayer.accept(l);
			GuiHandler.GUI_SCALE = gs;
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
		if(KeyboardHandler.SHOWING)return;
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
