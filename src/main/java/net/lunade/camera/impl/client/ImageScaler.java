package net.lunade.camera.impl.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Arrays;

@Environment(EnvType.CLIENT)
public class ImageScaler {

    @Contract("_, _ -> new")
    public static @NotNull InputStream createScaledImage(File imageFile, int resolution) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(imageFile);
        Image image = bufferedImage.getScaledInstance(resolution, resolution, Image.SCALE_FAST);

        BufferedImage scaledImage;

        if (image instanceof BufferedImage buffered) {
            scaledImage = buffered;
        } else {
            scaledImage = new BufferedImage(resolution, resolution, BufferedImage.TYPE_INT_ARGB);
            Graphics2D scaledGraphics = scaledImage.createGraphics();
            scaledGraphics.drawImage(image, 0, 0, null);
            scaledGraphics.dispose();
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(scaledImage, Arrays.stream(imageFile.getName().split("\\.")).toList().getLast(), outputStream);
        return new ByteArrayInputStream(outputStream.toByteArray());
    }
}
