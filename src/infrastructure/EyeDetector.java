package infrastructure;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

public class EyeDetector {

    private CascadeClassifier eyeClassifier;

    public EyeDetector(String cascadePath) {
        eyeClassifier = new CascadeClassifier(cascadePath);
    }

    public Rect[] detect(Mat faceRegion) {
        if (faceRegion.empty() || faceRegion.rows() <= 0 || faceRegion.cols() <= 0) {
            return new Rect[0];
        }

        int inicio = (int) (faceRegion.rows() * 0.25);
        int fin    = (int) (faceRegion.rows() * 0.55);
        int altura = fin - inicio;

        if (altura <= 0) {
            return new Rect[0];
        }

        Rect zonaOjos = new Rect(0, inicio, faceRegion.cols(), altura);

        if (zonaOjos.x + zonaOjos.width  > faceRegion.cols() ||
                zonaOjos.y + zonaOjos.height > faceRegion.rows()) {
            return new Rect[0];
        }

        Mat zonaBusqueda = new Mat(faceRegion, zonaOjos);
        Mat gris = new Mat();

        try {
            Imgproc.cvtColor(zonaBusqueda, gris, Imgproc.COLOR_BGR2GRAY);
            Imgproc.equalizeHist(gris, gris);
            Imgproc.GaussianBlur(gris, gris, new Size(3, 3), 0);

            MatOfRect ojos = new MatOfRect();
            eyeClassifier.detectMultiScale(gris, ojos, 1.1, 8, 0, new Size(30, 30), new Size());

            Rect[] ojosArray = ojos.toArray();

            if (ojosArray.length > 2) {
                java.util.Arrays.sort(ojosArray, (a, b) ->
                        (b.width * b.height) - (a.width * a.height)
                );
                return new Rect[]{ojosArray[0], ojosArray[1]};
            }

            return ojosArray;

        } catch (Exception e) {
            return new Rect[0];
        } finally {
            // Liberar memoria siempre, aunque haya error
            gris.release();
            zonaBusqueda.release();
        }
    }
}