package de.juhu.util.Interfaces;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import de.juhu.util.References;

public interface Renderable {

	public void init();

	public void render();

	public void close();

	/**
	 * Loads the Texture from the given File (name)
	 * 
	 * @param name
	 * @return
	 */
	public default BufferedImage loadImage(String name) {
		try {
			References.LOGGER.info("Try");
			BufferedImage image = ImageIO.read(new File("assets/textures/" + name));
			return image;
		} catch (IOException e) {
			References.LOGGER.info("Failed");
			try {
				return ImageIO.read(getClass().getClassLoader().getResourceAsStream("assets/textures/" + name));
			} catch (IOException e1) {
				References.LOGGER.info("Failed");
				e1.printStackTrace();
			}
		}
		return null;
	}
}
