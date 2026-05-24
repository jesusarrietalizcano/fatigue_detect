package infrastructure;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

public class FaceDetector {

    private CascadeClassifier faceClassifier;

    public FaceDetector(String cascadePath) {
        faceClassifier = new CascadeClassifier(cascadePath);
    }

    public Rect[] detect(Mat frame) {
        Mat gris = new Mat();
        Imgproc.cvtColor(frame, gris, Imgproc.COLOR_BGR2GRAY);

        Imgproc.equalizeHist(gris, gris);

        MatOfRect rostros = new MatOfRect();

        // Solo detecta caras de mínimo 100x100 píxeles
        // Evita que objetos del fondo se confundan con caras
        faceClassifier.detectMultiScale(gris, rostros, 1.1, 5, 0, new Size(100, 100), new Size());

        return rostros.toArray();
    }
}