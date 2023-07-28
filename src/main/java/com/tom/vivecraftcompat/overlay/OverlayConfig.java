package com.tom.vivecraftcompat.overlay;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.vivecraft.utils.math.Matrix4f;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

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

			for (int i = 0;i<oel.size();i++) {
				String o = String.valueOf(oel.get(i));
				ResourceLocation id = ResourceLocation.tryParse(o);
				if(id != null)ov.overlays.add(id);
			}

			Layer layer = new Layer(ov);
			ov.layer = layer;
			layer.setScale(ce.getFloat(ConfigKeys.OVERLAY_SCALE, 1));
			layer.setLock(OverlayLock.byName(ce.getString(ConfigKeys.OVERLAY_LOCK, "")));
			ConfigEntry pos = ce.getEntry(ConfigKeys.OVERLAY_POS);
			layer.setPos(new Vec3(
					pos.getFloat("x", 0),
					pos.getFloat("y", 0),
					pos.getFloat("z", 0)
					));
			ConfigEntry rot = ce.getEntry(ConfigKeys.OVERLAY_ROTATION);
			Matrix4f mat = new Matrix4f();
			for(int x = 0;x<4;x++) for(int y = 0;y<4;y++) {
				mat.M[x][y] = rot.getFloat("m" + x + "" + y, mat.M[x][y]);
			}
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

				ConfigEntryList oel = ce.getEntryList(ConfigKeys.OVERLAY_ELEMENT_LIST);
				h.overlays.forEach(e -> oel.add(e.toString()));

				ce.setString(ConfigKeys.OVERLAY_LOCK, l.getLock().name().toLowerCase(Locale.ROOT));
				ce.setFloat(ConfigKeys.OVERLAY_SCALE, l.getScale());
				ConfigEntry pos = ce.getEntry(ConfigKeys.OVERLAY_POS);
				Vec3 p = l.getPosRaw();
				pos.setFloat("x", (float) p.x);
				pos.setFloat("y", (float) p.y);
				pos.setFloat("z", (float) p.z);
				ConfigEntry rot = ce.getEntry(ConfigKeys.OVERLAY_ROTATION);
				Matrix4f r = l.getRotationRaw();
				for(int x = 0;x<4;x++) for(int y = 0;y<4;y++) {
					rot.setFloat("m" + x + "" + y, r.M[x][y]);
				}
			}
		});
		config.saveConfig();
	}
}
