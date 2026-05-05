import application.DetectionController;
import infrastructure.CameraService;
import presentation.CameraView;
import org.opencv.core.Core;

public class Main {
    public static void main(String[] args) {

        System.load(System.getProperty("user.dir") + "\\libs\\opencv_java4120.dll");


        CameraService cameraService = new CameraService();
        CameraView view = new CameraView();

        DetectionController controller = new DetectionController(cameraService, view);
        controller.start();
    }
}

