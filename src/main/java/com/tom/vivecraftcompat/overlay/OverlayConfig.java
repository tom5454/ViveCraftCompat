package com.tom.vivecraftcompat.overlay;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

import com.tom.cpl.config.ConfigEntry;
import com.tom.cpl.config.ConfigEntry.ConfigEntryList;
import com.tom.cpl.config.ModConfigFile.ConfigEntryTemp;
import com.tom.vivecraftcompat.Client;
import com.tom.vivecraftcompat.ConfigKeys;
import com.tom.vivecraftcompat.overlay.OverlayManager.Layer;

public class OverlayConfig {

	public static void loadOverlays() {
		ConfigEntry ovs = Client.config.getEntry(ConfigKeys.OVERLAY_ELEMENT_LIST);
		List<Layer> loadedLayers = new ArrayList<>();
		ovs.keySet().forEach(key -> {
			ConfigEntry ce = ovs.getEntry(key);
			String name = ce.getString(ConfigKeys.OVERLAY_NAME, "Overlay");
			ConfigEntryList oel = ce.getEntryList(ConfigKeys.OVERLAY_ELEMENT_LIST);

			HudOverlayScreen ov = new HudOverlayScreen(key);
			ov.setName(name);
			ov.enable = OverlayEnable.byName(ce.getString(ConfigKeys.OVERLAY_ENABLE, ""));

			for (int i = 0; i < oel.size(); i++) {
				String o = String.valueOf(oel.get(i));
				ResourceLocation id = ResourceLocation.tryParse(o);
				if (id != null) ov.overlays.add(id);
			}

			Layer layer = new Layer(ov);
			ov.layer = layer;
			layer.setScale(ce.getFloat(ConfigKeys.OVERLAY_SCALE, 1));
			layer.setLockDirect(OverlayLock.byName(ce.getString(ConfigKeys.OVERLAY_LOCK, "")));

			ConfigEntry pos = ce.getEntry(ConfigKeys.OVERLAY_POS);
			layer.setPos(new Vector3f(
					pos.getFloat("x", 0),
					pos.getFloat("y", 0),
					pos.getFloat("z", 0)
			));

			ConfigEntry rot = ce.getEntry(ConfigKeys.OVERLAY_ROTATION);
			// Convert Legacy Matrix4f to Quaternion if needed,
			// does not overwrite config until save is called
			if (rot.hasEntry("m00")) {
				Matrix4f mat = new Matrix4f();
				mat.m00(rot.getFloat("m00", 1));
				mat.m01(rot.getFloat("m01", 0));
				mat.m02(rot.getFloat("m02", 0));
				mat.m10(rot.getFloat("m10", 0));
				mat.m11(rot.getFloat("m11", 1));
				mat.m12(rot.getFloat("m12", 0));
				mat.m20(rot.getFloat("m20", 0));
				mat.m21(rot.getFloat("m21", 0));
				mat.m22(rot.getFloat("m22", 1));

				Quaternionf quaternion = new Quaternionf();
				mat.getUnnormalizedRotation(quaternion);
				rot.setFloat("x", quaternion.x);
				rot.setFloat("y", quaternion.y);
				rot.setFloat("z", quaternion.z);
				rot.setFloat("w", quaternion.w);
			}
			float x = rot.getFloat("x", 0);
			float y = rot.getFloat("y", 0);
			float z = rot.getFloat("z", 0);
			float w = rot.getFloat("w", 1);  // Default w to 1 for identity quaternion

			Quaternionf quaternion = new Quaternionf(x, y, z, w);

			// Convert the quaternion to a matrix, since the overlay system uses matrices for rotation
			Matrix4f mat = new Matrix4f().rotate(quaternion);

			layer.setRotation(mat);

			loadedLayers.add(layer);
		});

		Minecraft.getInstance().execute(() -> loadedLayers.forEach(OverlayManager::addLayer));
	}


	public static void saveOverlays() {
		ConfigEntryTemp config = Client.config.createTemp();
		ConfigEntry ovs = config.getEntry(ConfigKeys.OVERLAY_ELEMENT_LIST);
		ovs.clear();
		OverlayManager.forEachLayer(l -> {
			if (l.getScreen() instanceof HudOverlayScreen h) {
				ConfigEntry ce = ovs.getEntry(h.getId());
				ce.setString(ConfigKeys.OVERLAY_NAME, h.getName());
				ce.setString(ConfigKeys.OVERLAY_ENABLE, h.enable.name().toLowerCase(Locale.ROOT));

				ConfigEntryList oel = ce.getEntryList(ConfigKeys.OVERLAY_ELEMENT_LIST);
				h.overlays.forEach(e -> oel.add(e.toString()));

				ce.setString(ConfigKeys.OVERLAY_LOCK, l.getLock().name().toLowerCase(Locale.ROOT));
				ce.setFloat(ConfigKeys.OVERLAY_SCALE, l.getScale());
				ConfigEntry pos = ce.getEntry(ConfigKeys.OVERLAY_POS);
				Vector3f p = l.getPosRaw();
				pos.setFloat("x", p.x);
				pos.setFloat("y", p.y);
				pos.setFloat("z", p.z);

				// Rotation handling using Quaternion (float4)
				ConfigEntry rot = ce.getEntry(ConfigKeys.OVERLAY_ROTATION);
				Matrix4f rotationMatrix = l.getRotationRaw();
				Quaternionf quaternion = new Quaternionf();
				rotationMatrix.getUnnormalizedRotation(quaternion);

				rot.setFloat("x", quaternion.x);
				rot.setFloat("y", quaternion.y);
				rot.setFloat("z", quaternion.z);
				rot.setFloat("w", quaternion.w);
			}
		});
		config.saveConfig();
	}

}
