package com.unascribed.ears;

import java.awt.image.BufferedImage;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.imageio.ImageIO;

import com.unascribed.ears.common.legacy.mcauthlib.data.GameProfile;
import com.unascribed.ears.common.legacy.mcauthlib.service.ProfileService;
import com.unascribed.ears.common.EarsFeatures;
import com.unascribed.ears.common.debug.EarsLog;
import com.unascribed.ears.common.legacy.AWTEarsImage;

import net.minecraft.client.ImageProcessor;

public class EarsDownloadThread {
	public BufferedImage image;

	public EarsDownloadThread(final String string, final ImageProcessor imageProcessor, BufferedImage image) {
		this.image = image;

		(new Thread(() -> {
			HttpURLConnection imageConnection = null;

			try {
				URL url = new URL(string);
				if (string.startsWith("http://s3.amazonaws.com/MinecraftSkins/") && string.endsWith(".png")) {
					String username = string.substring(39, string.length() - 4);
					// this is called in the download thread, so it's ok to block
					final String[] newUrl = {null};
					EarsMod.profileService.findProfilesByName(new String[]{username}, new ProfileService.ProfileLookupCallback() {
						@Override
						public void onProfileLookupSucceeded(GameProfile profile) {
							try {
								EarsMod.sessionService.fillProfileProperties(profile);
								if (profile.getTexture(GameProfile.TextureType.SKIN).getModel() == GameProfile.TextureModel.SLIM) {
									EarsMod.slimUsers.add(username);
								} else {
									EarsMod.slimUsers.remove(username);
								}
								newUrl[0] = profile.getTexture(GameProfile.TextureType.SKIN, false).getURL();
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
					url = new URL(newUrl[0]);
				}

				imageConnection = (HttpURLConnection)url.openConnection();
				imageConnection.setDoInput(true);
				imageConnection.setDoOutput(false);
				imageConnection.connect();
				if (imageConnection.getResponseCode() / 100 != 4) {
					if (imageProcessor == null) {
						EarsDownloadThread.this.image = ImageIO.read(imageConnection.getInputStream());
					} else {
						EarsDownloadThread.this.image = imageProcessor.process(ImageIO.read(imageConnection.getInputStream()));
					}

					EarsLog.debug("Platform:Inject", "Process player skin");
					EarsMod.earsSkinFeatures.put(string, EarsFeatures.detect(new AWTEarsImage(EarsDownloadThread.this.image)));

					return;
				}
			} catch (Exception e) {
				e.printStackTrace();
				return;
			} finally {
				imageConnection.disconnect();
			}
		})).start();
	}
}
