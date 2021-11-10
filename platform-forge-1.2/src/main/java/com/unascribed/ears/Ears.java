package com.unascribed.ears;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

import com.unascribed.ears.common.legacy.mcauthlib.data.GameProfile;
import com.unascribed.ears.common.legacy.mcauthlib.data.GameProfile.TextureModel;
import com.unascribed.ears.common.legacy.mcauthlib.data.GameProfile.TextureType;
import com.unascribed.ears.common.legacy.mcauthlib.service.ProfileService;
import com.unascribed.ears.common.legacy.mcauthlib.service.ProfileService.ProfileLookupCallback;
import com.unascribed.ears.common.util.EarsStorage;
import com.unascribed.ears.common.legacy.mcauthlib.service.SessionService;
import com.unascribed.ears.common.EarsCommon;
import com.unascribed.ears.common.EarsCommon.StripAlphaMethod;
import com.unascribed.ears.common.debug.EarsLog;
import com.unascribed.ears.common.legacy.AWTEarsImage;
import com.unascribed.ears.common.EarsFeatures;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ReflectionHelper;
import forge.MinecraftForge;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ImageBufferDownload;
import net.minecraft.src.ModelBiped;
import net.minecraft.src.ModelRenderer;
import net.minecraft.src.RenderLiving;
import net.minecraft.src.RenderPlayer;

public class Ears {
	
	public static final Map<String, EarsFeatures> earsSkinFeatures = new WeakHashMap<>();
	
	public static boolean forceBipedTextureHeight = false;
	
	private static final SessionService sessionService = new SessionService();
	private static final ProfileService profileService = new ProfileService();
	private static LayerEars layer;
	
	public static final Set<String> slimUsers = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
	public static ModelRenderer slimLeftArm;
	public static ModelRenderer slimRightArm;
	public static ModelRenderer fatLeftArm;
	public static ModelRenderer fatRightArm;
	
	public static void init() {
		if (EarsLog.DEBUG) {
			EarsLog.debugva("Platform", "Initialized - Minecraft 1.2.5 / Forge {}; Side={}", MinecraftForge.getVersionString().replace("Minecraft Forge ", ""), FMLCommonHandler.instance().getSide());
		}
		layer = new LayerEars();
	}
	
	public static void modelPreconstruct(ModelBiped model) {
		EarsLog.debug("Platform:Inject", "modelPreconstruct({})", model);
		if (forceBipedTextureHeight) {
			model.textureHeight = 64;
		}
	}
	
	public static void amendPlayerRenderer(RenderPlayer rp) {
		EarsLog.debug("Platform", "Hacking 64x64 skin support into player model");
		forceBipedTextureHeight = true;
		ModelBiped model = new ModelBiped(0, 0);
		forceBipedTextureHeight = false;
		
		ReflectionHelper.setPrivateValue(RenderLiving.class, rp, 0, model);
		setModelBipedMain(rp, model);
		
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
		
		// non-head secondary layers
		ModelTransBox.addBoxTo(model.bipedLeftArm, 48, 48, -1, -2, -2, 4, 12, 4, 0.25f);
		ModelTransBox.addBoxTo(model.bipedBody, 16, 32, -4, 0, -2, 8, 12, 4, 0.25f);
		ModelTransBox.addBoxTo(model.bipedRightArm, 40, 32, -3, -2, -2, 4, 12, 4, 0.25f);
		ModelTransBox.addBoxTo(model.bipedLeftLeg, 0, 48, -2, 0, -2, 4, 12, 4, 0.25f);
		ModelTransBox.addBoxTo(model.bipedRightLeg, 0, 32, -2, 0, -2, 4, 12, 4, 0.25f);
		
		fatLeftArm = model.bipedLeftArm;
		fatRightArm = model.bipedRightArm;
		
		// slim arms
		slimLeftArm = new ModelRenderer(model, 32, 48);
		slimLeftArm.addBox(-1, -2, -2, 3, 12, 4, 0);
		slimLeftArm.setRotationPoint(5, 2.5f, 0);
		ModelTransBox.addBoxTo(slimLeftArm, 48, 48, -1, -2, -2, 3, 12, 4, 0.25f);
		
		slimRightArm = new ModelRenderer(model, 40, 16);
		slimRightArm.addBox(-2, -2, -2, 3, 12, 4, 0);
		slimRightArm.setRotationPoint(-5, 2.5f, 0);
		ModelTransBox.addBoxTo(slimRightArm, 40, 32, -2, -2, -2, 3, 12, 4, 0.25f);
	}
	
	public static void renderSpecials(RenderPlayer render, EntityPlayer player, float f) {
		EarsLog.debug("Platform", "renderSpecials player={}, partialTicks={}", player, f);
		layer.doRenderLayer(render, player,
				player.field_705_Q + (player.field_704_R - player.field_705_Q) * f,
				f);
	}
	
	public static BufferedImage interceptParseUserSkin(final ImageBufferDownload subject, BufferedImage image) {
		EarsLog.debug("Platform:Inject", "parseUserSkin({}, {})", subject, image);
		if (image == null) {
			EarsLog.debug("Platform:Inject", "parseUserSkin(...): Image is null");
			return null;
		} else {
			setImageWidth(subject, 64);
			setImageHeight(subject, 64);
			BufferedImage newImg = new BufferedImage(64, 64, 2);
			Graphics g = newImg.getGraphics();
			g.drawImage(image, 0, 0, null);

			if (image.getHeight() == 32) {
				EarsLog.debug("Platform:Inject", "parseUserSkin(...): Upgrading legacy skin");
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
			EarsCommon.carefullyStripAlpha(new StripAlphaMethod() {
				@Override
				public void stripAlpha(int _x1, int _y1, int _x2, int _y2) {
					setAreaOpaque(subject, _x1, _y1, _x2, _y2);
				}
			}, true);
			setAreaTransparent(subject, 32, 0, 64, 32);
			setAreaTransparent(subject, 0, 32, 16, 48);
			setAreaTransparent(subject, 16, 32, 40, 48);
			setAreaTransparent(subject, 40, 32, 56, 48);
			setAreaTransparent(subject, 0, 48, 16, 64);
			setAreaTransparent(subject, 48, 48, 64, 64);
			return newImg;
		}
	}
	
	public static void checkSkin(Object tdi, BufferedImage img) {
		EarsLog.debug("Platform:Inject", "Process player skin");
		earsSkinFeatures.put(getLocation(tdi), EarsFeatures.detect(new AWTEarsImage(img), EarsStorage.get(img, EarsStorage.Key.ALFALFA)));
	}
	
	public static String amendSkinUrl(String url) {
		EarsLog.debug("Platform:Inject", "Amend skin URL {}", url);
		if (url.startsWith("http://s3.amazonaws.com/MinecraftSkins/") && url.endsWith(".png")) {
			final String username = url.substring(39, url.length()-4);
			// this is called in the download thread, so it's ok to block
			final String[] newUrl = {null};
			profileService.findProfilesByName(new String[] {username}, new ProfileLookupCallback() {
				
				@Override
				public void onProfileLookupSucceeded(GameProfile profile) {
					try {
						sessionService.fillProfileProperties(profile);
						if (profile.getTexture(TextureType.SKIN).getModel() == TextureModel.SLIM) {
							slimUsers.add(username);
						} else {
							slimUsers.remove(username);
						}
						newUrl[0] = profile.getTexture(TextureType.SKIN, false).getURL();
						EarsLog.debug("Platform:Inject", "Profile lookup successful, slim={} url={}", profile.getTexture(TextureType.SKIN).getModel() == TextureModel.SLIM, newUrl[0]);
					} catch (Throwable t) {
						t.printStackTrace();
						System.err.println("[Ears] Profile lookup failed");
					}
				}
				
				@Override
				public void onProfileLookupFailed(GameProfile profile, Exception e) {
					e.printStackTrace();
					System.err.println("[Ears] Profile lookup failed");
				}
			}, false);
			return newUrl[0];
		}
		return url;
	}
	
	public static void beforeRender(RenderPlayer rp) {
		beforeRender(rp, FMLClientHandler.instance().getClient().h);
	}
	
	public static void beforeRender(RenderPlayer rp, EntityPlayer player) {
		EarsLog.debug("Platform:Inject", "Before render rp={} player={}", rp, player);
		boolean slim = slimUsers.contains(player.username);
		ModelBiped modelBipedMain = getModelBipedMain(rp);
		if (slim) {
			modelBipedMain.bipedLeftArm = slimLeftArm;
			modelBipedMain.bipedRightArm = slimRightArm;
		} else {
			modelBipedMain.bipedLeftArm = fatLeftArm;
			modelBipedMain.bipedRightArm = fatRightArm;
		}
	}
	
	public static void renderFirstPersonArm(RenderPlayer rp) {
		layer.renderRightArm(rp, FMLClientHandler.instance().getClient().h);
	}
	
	private static final Method setAreaOpaque;
	private static final Method setAreaTransparent;
	private static final Field imageHeight;
	private static final Field imageWidth;
	private static final Field imageData;
	private static final Field location;
	private static final Field modelBipedMain;
	static {
		try {
			setAreaOpaque = ImageBufferDownload.class.getDeclaredMethod("b", int.class, int.class, int.class, int.class);
			setAreaOpaque.setAccessible(true);
			
			setAreaTransparent = ImageBufferDownload.class.getDeclaredMethod("a", int.class, int.class, int.class, int.class);
			setAreaTransparent.setAccessible(true);
			
			imageHeight = ImageBufferDownload.class.getDeclaredField("c");
			imageHeight.setAccessible(true);
			
			imageWidth = ImageBufferDownload.class.getDeclaredField("b");
			imageWidth.setAccessible(true);
			
			imageData = ImageBufferDownload.class.getDeclaredField("a");
			imageData.setAccessible(true);
			
			location = Class.forName("ahw").getDeclaredField("a");
			location.setAccessible(true);
			
			modelBipedMain = RenderPlayer.class.getDeclaredField("c");
			modelBipedMain.setAccessible(true);
		} catch (Throwable t) {
			throw new Error(t);
		}
	}
	
	private static int getImageWidth(ImageBufferDownload subject) {
		try {
			return (int)imageWidth.get(subject);
		} catch (Throwable e) {
			if (e instanceof RuntimeException) throw (RuntimeException)e;
			throw new RuntimeException(e);
		}
	}
	private static int getImageHeight(ImageBufferDownload subject) {
		try {
			return (int)imageHeight.get(subject);
		} catch (Throwable e) {
			if (e instanceof RuntimeException) throw (RuntimeException)e;
			throw new RuntimeException(e);
		}
	}
	private static String getLocation(Object subject) {
		try {
			return (String)location.get(subject);
		} catch (Throwable e) {
			if (e instanceof RuntimeException) throw (RuntimeException)e;
			throw new RuntimeException(e);
		}
	}
	private static void setImageWidth(ImageBufferDownload subject, int i) {
		try {
			imageWidth.set(subject, i);
		} catch (Throwable e) {
			if (e instanceof RuntimeException) throw (RuntimeException)e;
			throw new RuntimeException(e);
		}
	}
	private static void setImageHeight(ImageBufferDownload subject, int i) {
		try {
			imageHeight.set(subject, i);
		} catch (Throwable e) {
			if (e instanceof RuntimeException) throw (RuntimeException)e;
			throw new RuntimeException(e);
		}
	}
	private static void setImageData(ImageBufferDownload subject, int[] data) {
		try {
			imageData.set(subject, data);
		} catch (Throwable e) {
			if (e instanceof RuntimeException) throw (RuntimeException)e;
			throw new RuntimeException(e);
		}
	}
	private static void setModelBipedMain(RenderPlayer subject, ModelBiped model) {
		try {
			modelBipedMain.set(subject, model);
		} catch (Throwable e) {
			if (e instanceof RuntimeException) throw (RuntimeException)e;
			throw new RuntimeException(e);
		}
	}
	public static ModelBiped getModelBipedMain(RenderPlayer subject) {
		try {
			return (ModelBiped)modelBipedMain.get(subject);
		} catch (Throwable e) {
			if (e instanceof RuntimeException) throw (RuntimeException)e;
			throw new RuntimeException(e);
		}
	}
	private static void setAreaOpaque(ImageBufferDownload subject, int x1, int y1, int x2, int y2) {
		try {
			EarsLog.debug("Platform:Inject", "stripAlpha({}, {}, {}, {}, {})", subject, x1, y1, x2, y2);
			setAreaOpaque.invoke(subject, x1, y1, x2, y2);
		} catch (Throwable e) {
			if (e instanceof RuntimeException) throw (RuntimeException)e;
			throw new RuntimeException(e);
		}
	}
	private static void setAreaTransparent(ImageBufferDownload subject, int x1, int y1, int x2, int y2) {
		try {
			setAreaTransparent.invoke(subject, x1, y1, x2, y2);
		} catch (Throwable e) {
			if (e instanceof RuntimeException) throw (RuntimeException)e;
			throw new RuntimeException(e);
		}
	}
	
}
