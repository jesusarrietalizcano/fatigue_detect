package infrastructure;

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

        MatOfRect ojos = new MatOfRect();

        eyeClassifier.detectMultiScale(gris, ojos, 1.1,3);

        return ojos.toArray();
    }
}
