package infrastructure;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

public class CameraService {

    private VideoCapture camera;

    public CameraService() {
        camera = new VideoCapture(0);
    }

    public boolean isOpened() {
        return camera.isOpened();
    }

    public Mat getFrame() {
        Mat frame = new Mat();
        camera.read(frame);
        return frame;
    }
}