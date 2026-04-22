import application.DetectionController;
import infrastructure.CameraService;
import presentation.CameraView;

public class Main {
    public static void main(String[] args) {

        System.load("C:\\Users\\Lenovo\\Downloads\\opencv\\build\\java\\x64\\opencv_java4120.dll");


        CameraService cameraService = new CameraService();
        CameraView view = new CameraView();

        DetectionController controller = new DetectionController(cameraService, view);
        controller.start();
    }
}

