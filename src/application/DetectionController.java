package application;

import infrastructure.CameraService;
import infrastructure.utils.ImageUtils;
import presentation.CameraView;
import org.opencv.core.Mat;

public class DetectionController {

    private CameraService cameraService;
    private CameraView view;

    public DetectionController(CameraService cameraService, CameraView view) {
        this.cameraService = cameraService;
        this.view = view;
    }

    public void start() {

        if (!cameraService.isOpened()) {
            System.out.println("Error con la cámara");
            return;
        }

        while (true) {
            Mat frame = cameraService.getFrame();

            if (!frame.empty()) {
                var image = ImageUtils.matToBufferedImage(frame);
                view.updateImage(image);
            }
        }
    }
}