package com.unascribed.ears;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.ByteArrayInputStream;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.WeakHashMap;

import javax.imageio.ImageIO;

import com.unascribed.ears.common.EarsCommon;
import com.unascribed.ears.common.EarsFeaturesParser;
import com.unascribed.ears.api.features.EarsFeatures;
import com.unascribed.ears.common.debug.EarsLog;
import com.unascribed.ears.common.legacy.AWTEarsImage;
import com.unascribed.ears.common.util.EarsStorage;
import com.unascribed.ears.legacy.LegacyHelper;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.ImageBufferDownload;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.common.MinecraftForge;

@Mod(modid="ears", name="Ears", version="@VERSION@", useMetadata=true)
public class Ears {
	
	public static final Map<ITextureObject, EarsFeatures> earsSkinFeatures = new WeakHashMap<>();
	
	private static LayerEars layer;
	
	public static ModelRenderer slimLeftArm;
	public static ModelRenderer slimRightArm;
	public static ModelRenderer fatLeftArm;
	public static ModelRenderer fatRightArm;
	
	public Ears() {
		if (EarsLog.DEBUG) {
			EarsLog.debugva(EarsLog.Tag.PLATFORM, "Initialized - {} / Forge {}; Side={}",
					Loader.instance().getMCVersionString(), ForgeVersion.getVersion(), FMLCommonHandler.instance().getSide());
		}
	}
	
	@EventHandler
	public void onPreInit(FMLPreInitializationEvent e) {
		MinecraftForge.EVENT_BUS.register(this);
		layer = new LayerEars();
	}
	
	public static void amendPlayerRenderer(RenderPlayer rp) {
		EarsLog.debug(EarsLog.Tag.PLATFORM, "Hacking 64x64 skin support into player model");
		
		ModelBiped model = new ModelBiped(0, 0, 64, 64);
		ReflectionHelper.setPrivateValue(RendererLivingEntity.class, rp, model, "field_77045_g", "mainModel");
		rp.modelBipedMain = model;
		
		model.textureHeight = 32;
		model.bipedCloak = new ModelRenderer(model, 0, 0);
		model.bipedCloak.addBox(-5, 0, -1, 10, 16, 1, 0);
		model.textureHeight = 64;
		
		// translucent head layer
		model.bipedHeadwear.cubeList.remove(0);
		ModelTransBox.addBoxTo(model.bipedHeadwear, 32, 0, -4, -8, -4, 8, 8, 8, 0.5f);
		
		// non-flipped left arm/leg
		model.bipedLeftArm = new ModelRenderer(model, 32, 48);
		model.bipedLeftArm.addBox(-1, -2, -2, 4, 12, 4, 0);
		model.bipedLeftArm.setRotationPoint(5, 2, 0);
		
		model.bipedLeftLeg = new ModelRenderer(model, 16, 48);
		model.bipedLeftLeg.addBox(-2, 0, -2, 4, 12, 4, 0);
		model.bipedLeftLeg.setRotationPoint(1.9f, 12, 0);
		
		fatLeftArm = model.bipedLeftArm;
		fatRightArm = model.bipedRightArm;
		
		// slim arms
		slimLeftArm = new ModelRenderer(model, 32, 48);
        slimLeftArm.addBox(-1, -2, -2, 3, 12, 4, 0);
        slimLeftArm.setRotationPoint(5, 2.5f, 0);
        
        slimRightArm = new ModelRenderer(model, 40, 16);
        slimRightArm.addBox(-2, -2, -2, 3, 12, 4, 0);
        slimRightArm.setRotationPoint(-5, 2.5f, 0);
	}
	
	@SubscribeEvent
	public void onRenderPlayerPre(RenderPlayerEvent.Pre e) {
		beforeRender(e.renderer, e.entityPlayer);
	}
	
	@SubscribeEvent
	public void onRenderPlayerPost(RenderPlayerEvent.Specials.Post e) {
		EarsLog.debug(EarsLog.Tag.PLATFORM_RENDERER, "RenderPlayerEvent.Specials.Post player={}, renderer={}, partialTicks={}", e.entityPlayer, e.renderer, e.partialRenderTick);
		layer.doRenderLayer(e.renderer, (AbstractClientPlayer)e.entityPlayer,
				e.entityPlayer.prevLimbSwingAmount + (e.entityPlayer.limbSwingAmount - e.entityPlayer.prevLimbSwingAmount) * e.partialRenderTick,
				e.partialRenderTick);
	}
	
	public static BufferedImage interceptParseUserSkin(ImageBufferDownload subject, BufferedImage image) {
		EarsLog.debug(EarsLog.Tag.PLATFORM_INJECT, "parseUserSkin({}, {})", subject, image);
		if (image == null) {
			EarsLog.debug(EarsLog.Tag.PLATFORM_INJECT, "parseUserSkin(...): Image is null");
			return null;
		} else {
			setImageWidth(subject, 64);
			setImageHeight(subject, 64);
			BufferedImage newImg = new BufferedImage(64, 64, 2);
			Graphics g = newImg.getGraphics();
			g.drawImage(image, 0, 0, null);

			if (image.getHeight() == 32) {
				EarsLog.debug(EarsLog.Tag.PLATFORM_INJECT, "parseUserSkin(...): Upgrading legacy skin");
				g.drawImage(newImg, 24, 48, 20, 52, 4, 16, 8, 20, null);
				g.drawImage(newImg, 28, 48, 24, 52, 8, 16, 12, 20, null);
				g.drawImage(newImg, 20, 52, 16, 64, 8, 20, 12, 32, null);
				g.drawImage(newImg, 24, 52, 20, 64, 4, 20, 8, 32, null);
				g.drawImage(newImg, 28, 52, 24, 64, 0, 20, 4, 32, null);
				g.drawImage(newImg, 32, 52, 28, 64, 12, 20, 16, 32, null);
				g.drawImage(newImg, 40, 48, 36, 52, 44, 16, 48, 20, null);
				g.drawImage(newImg, 44, 48, 40, 52, 48, 16, 52, 20, null);
				g.drawImage(newImg, 36, 52, 32, 64, 48, 20, 52, 32, null);
				g.drawImage(newImg, 40, 52, 36, 64, 44, 20, 48, 32, null);
				g.drawImage(newImg, 44, 52, 40, 64, 40, 20, 44, 32, null);
				g.drawImage(newImg, 48, 52, 44, 64, 52, 20, 56, 32, null);
			}

			g.dispose();
			
			EarsStorage.put(newImg, EarsStorage.Key.ALFALFA, EarsCommon.preprocessSkin(new AWTEarsImage(newImg)));
			
			setImageData(subject, ((DataBufferInt) newImg.getRaster().getDataBuffer()).getData());
			EarsCommon.carefullyStripAlpha((_x1, _y1, _x2, _y2) -> setAreaOpaque(subject, _x1, _y1, _x2, _y2), true);
			setAreaTransparent(subject, 32, 0, 64, 32);
			setAreaTransparent(subject, 0, 32, 16, 48);
			setAreaTransparent(subject, 16, 32, 40, 48);
			setAreaTransparent(subject, 40, 32, 56, 48);
			setAreaTransparent(subject, 0, 48, 16, 64);
			setAreaTransparent(subject, 48, 48, 64, 64);
			return newImg;
		}
	}
	
	public static void checkSkin(ThreadDownloadImageData tdid, BufferedImage img) {
		if (img == null) return;
		EarsLog.debug(EarsLog.Tag.PLATFORM_INJECT, "Process player skin");
		earsSkinFeatures.put(tdid, EarsFeaturesParser.detect(new AWTEarsImage(img), EarsStorage.get(img, EarsStorage.Key.ALFALFA),
				data -> new AWTEarsImage(ImageIO.read(new ByteArrayInputStream(data)))));
	}
	
	public static void beforeRender(RenderPlayer rp, EntityPlayer player) {
		LegacyHelper.ensureLookedUpAsynchronously(player.getGameProfile().getId(), player.getGameProfile().getName());
		boolean slim = LegacyHelper.isSlimArms(player.getGameProfile().getId());
		if (slim) {
			rp.modelBipedMain.bipedLeftArm = slimLeftArm;
			rp.modelBipedMain.bipedRightArm = slimRightArm;
		} else {
			rp.modelBipedMain.bipedLeftArm = fatLeftArm;
			rp.modelBipedMain.bipedRightArm = fatRightArm;
		}
	}
	
	public static void renderFirstPersonArm(RenderPlayer rp, EntityPlayer player) {
		layer.renderRightArm(rp, (AbstractClientPlayer)player);
	}
	
	private static final MethodHandle setAreaOpaque;
	private static final MethodHandle setAreaTransparent;
	private static final MethodHandle gImageHeight;
	private static final MethodHandle sImageHeight;
	private static final MethodHandle gImageWidth;
	private static final MethodHandle sImageWidth;
	private static final MethodHandle sImageData;
	static {
		try {
			Method saom = ReflectionHelper.findMethod(ImageBufferDownload.class, null, new String[] {"func_78433_b", "setAreaOpaque"}, int.class, int.class, int.class, int.class);
			saom.setAccessible(true);
			setAreaOpaque = MethodHandles.lookup().unreflect(saom);
			
			Method satm = ReflectionHelper.findMethod(ImageBufferDownload.class, null, new String[] {"func_78434_a", "setAreaTransparent"}, int.class, int.class, int.class, int.class);
			satm.setAccessible(true);
			setAreaTransparent = MethodHandles.lookup().unreflect(satm);
			
			Field ihf = ReflectionHelper.findField(ImageBufferDownload.class, "field_78437_c", "imageHeight");
			ihf.setAccessible(true);
			gImageHeight = MethodHandles.lookup().unreflectGetter(ihf);
			sImageHeight = MethodHandles.lookup().unreflectSetter(ihf);
			
			Field iwf = ReflectionHelper.findField(ImageBufferDownload.class, "field_78436_b", "imageWidth");
			iwf.setAccessible(true);
			gImageWidth = MethodHandles.lookup().unreflectGetter(iwf);
			sImageWidth = MethodHandles.lookup().unreflectSetter(iwf);
			
			Field idf = ReflectionHelper.findField(ImageBufferDownload.class, "field_78438_a", "imageData");
			idf.setAccessible(true);
			sImageData = MethodHandles.lookup().unreflectSetter(idf);
		} catch (Throwable t) {
			throw new Error(t);
		}
	}
	
	private static int getImageWidth(ImageBufferDownload subject) {
		try {
			return (int)gImageWidth.invokeExact(subject);
		} catch (Throwable e) {
			if (e instanceof RuntimeException) throw (RuntimeException)e;
			throw new RuntimeException(e);
		}
	}
	private static int getImageHeight(ImageBufferDownload subject) {
		try {
			return (int)gImageHeight.invokeExact(subject);
		} catch (Throwable e) {
			if (e instanceof RuntimeException) throw (RuntimeException)e;
			throw new RuntimeException(e);
		}
	}
	private static void setImageWidth(ImageBufferDownload subject, int i) {
		try {
			sImageWidth.invokeExact(subject, i);
		} catch (Throwable e) {
			if (e instanceof RuntimeException) throw (RuntimeException)e;
			throw new RuntimeException(e);
		}
	}
	private static void setImageHeight(ImageBufferDownload subject, int i) {
		try {
			sImageHeight.invokeExact(subject, i);
		} catch (Throwable e) {
			if (e instanceof RuntimeException) throw (RuntimeException)e;
			throw new RuntimeException(e);
		}
	}
	private static void setImageData(ImageBufferDownload subject, int[] data) {
		try {
			sImageData.invokeExact(subject, data);
		} catch (Throwable e) {
			if (e instanceof RuntimeException) throw (RuntimeException)e;
			throw new RuntimeException(e);
		}
	}
	private static void setAreaOpaque(ImageBufferDownload subject, int x1, int y1, int x2, int y2) {
		try {
			EarsLog.debug(EarsLog.Tag.PLATFORM_INJECT, "stripAlpha({}, {}, {}, {}, {})", subject, x1, y1, x2, y2);
			setAreaOpaque.invokeExact(subject, x1, y1, x2, y2);
		} catch (Throwable e) {
			if (e instanceof RuntimeException) throw (RuntimeException)e;
			throw new RuntimeException(e);
		}
	}
	private static void setAreaTransparent(ImageBufferDownload subject, int x1, int y1, int x2, int y2) {
		try {
			setAreaTransparent.invokeExact(subject, x1, y1, x2, y2);
		} catch (Throwable e) {
			if (e instanceof RuntimeException) throw (RuntimeException)e;
			throw new RuntimeException(e);
		}
	}
	
}
