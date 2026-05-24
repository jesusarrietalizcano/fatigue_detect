package infrastructure;
import org.opencv.core.Size;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

public class EyeDetector {
    private CascadeClassifier eyeClassifier;

    public EyeDetector (String cascadePath){
        eyeClassifier = new CascadeClassifier(cascadePath);
    }

    public Rect[] detect(Mat faceRegion){

        int mitadAltura = faceRegion.rows() / 2;
        Rect mitadSuperior = new Rect(0, 0, faceRegion.cols(), mitadAltura);
        Mat zonaBusqueda = new Mat(faceRegion, mitadSuperior);

        Mat gris = new Mat();
        Imgproc.cvtColor(zonaBusqueda, gris,Imgproc.COLOR_BGR2GRAY);

        Imgproc.equalizeHist(gris, gris);
        // Suavizar para eliminar ruido antes de detectar
        Imgproc.GaussianBlur(gris, gris, new Size(3, 3), 0);

        MatOfRect ojos = new MatOfRect();

        eyeClassifier.detectMultiScale(gris, ojos, 1.1, 3, 0, new Size(20, 20), new Size());

        Rect[] ojosArray = ojos.toArray();

// Si detectó más de 2, quedarse solo con los 2 más grandes
        if (ojosArray.length > 2) {
            // Ordenar por área de mayor a menor
            java.util.Arrays.sort(ojosArray, (a, b) ->
                    (b.width * b.height) - (a.width * a.height)
            );
            // Devolver solo los 2 primeros (los más grandes)
            return new Rect[]{ojosArray[0], ojosArray[1]};
        }

        return ojosArray;
    }
}
