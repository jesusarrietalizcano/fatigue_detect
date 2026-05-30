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
        if (frame.empty() || frame.rows() <= 0 || frame.cols() <= 0) {
            return new Rect[0];
        }

        Mat gris = new Mat();
        try {
            Imgproc.cvtColor(frame, gris, Imgproc.COLOR_BGR2GRAY);
            Imgproc.equalizeHist(gris, gris);

            MatOfRect rostros = new MatOfRect();
            faceClassifier.detectMultiScale(gris, rostros, 1.1, 5, 0, new Size(100, 100), new Size());
            return rostros.toArray();
        } catch (Exception e) {
            return new Rect[0];
        } finally {
            // Liberar memoria siempre, aunque haya error
            gris.release();
        }
    }
}