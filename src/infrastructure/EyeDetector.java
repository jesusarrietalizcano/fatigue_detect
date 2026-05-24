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

        // Tomar solo entre 20% y 60% de la altura de la cara
        // Evita la frente (cejas) y la nariz/boca
        // Los ojos siempre están en esa franja
        int inicio = (int) (faceRegion.rows() * 0.25);
        int fin    = (int) (faceRegion.rows() * 0.55);
        int altura = fin - inicio;

        Rect zonaOjos = new Rect(0, inicio, faceRegion.cols(), altura);
        Mat zonaBusqueda = new Mat(faceRegion, zonaOjos);

        // Convertir a gris
        Mat gris = new Mat();
        Imgproc.cvtColor(zonaBusqueda, gris, Imgproc.COLOR_BGR2GRAY);

        // Mejorar contraste
        Imgproc.equalizeHist(gris, gris);

        // Suavizar ruido
        Imgproc.GaussianBlur(gris, gris, new Size(5, 5), 0);

        MatOfRect ojos = new MatOfRect();

        // minNeighbors subido a 6 → más estricto, menos falsos positivos
        // tamaño mínimo subido a 25x25
        eyeClassifier.detectMultiScale(gris, ojos, 1.1, 6, 0, new Size(25, 25), new Size());

        Rect[] ojosArray = ojos.toArray();

        // Si detectó más de 2, quedarse con los 2 más grandes
        if (ojosArray.length > 2) {
            java.util.Arrays.sort(ojosArray, (a, b) ->
                    (b.width * b.height) - (a.width * a.height)
            );
            return new Rect[]{ojosArray[0], ojosArray[1]};
        }

        return ojosArray;
    }
}