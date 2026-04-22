package infrastructure.utils;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import javax.imageio.ImageIO;

public class ImageUtils {

    public static BufferedImage matToBufferedImage(Mat mat) {
        try {
            var matOfByte = new org.opencv.core.MatOfByte();
            Imgcodecs.imencode(".jpg", mat, matOfByte);
            return ImageIO.read(new ByteArrayInputStream(matOfByte.toArray()));
        } catch (Exception e) {
            return null;
        }
    }
}