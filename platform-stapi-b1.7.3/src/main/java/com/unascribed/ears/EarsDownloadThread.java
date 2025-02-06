package com.unascribed.ears;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.imageio.ImageIO;

import com.unascribed.ears.legacy.LegacyHelper;
import com.unascribed.ears.api.features.AlfalfaData;
import com.unascribed.ears.common.EarsCommon;
import com.unascribed.ears.common.EarsFeaturesParser;
import com.unascribed.ears.common.debug.EarsLog;
import com.unascribed.ears.common.legacy.AWTEarsImage;

import net.minecraft.client.texture.ImageProcessor;

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
					String newUrl = LegacyHelper.getSkinUrl(username);
					if (newUrl == null) return;
					url = new URL(newUrl);
				}

				imageConnection = (HttpURLConnection)url.openConnection();
				imageConnection.setDoInput(true);
				imageConnection.setDoOutput(false);
				imageConnection.connect();
				if (imageConnection.getResponseCode() / 100 != 4) {
					BufferedImage rawImage = ImageIO.read(imageConnection.getInputStream());
					AlfalfaData alfalfa = EarsCommon.preprocessSkin(new AWTEarsImage(rawImage));
					if (imageProcessor == null) {
						EarsDownloadThread.this.image = rawImage;
					} else {
						EarsDownloadThread.this.image = imageProcessor.process(rawImage);
					}

					EarsLog.debug(EarsLog.Tag.PLATFORM_INJECT, "Process player skin");
					EarsMod.earsSkinFeatures.put(string, EarsFeaturesParser.detect(new AWTEarsImage(EarsDownloadThread.this.image), alfalfa,
							data -> new AWTEarsImage(ImageIO.read(new ByteArrayInputStream(data)))));

					return;
				}
			} catch (Exception e) {
				e.printStackTrace();
				return;
			} finally {
				if (imageConnection != null) {
					imageConnection.disconnect();
				}
			}
		})).start();
	}
}
