import application.DetectionController;
import infrastructure.CameraService;
import infrastructure.FaceDetector;
import infrastructure.EyeDetector;
import presentation.CameraView;
import domain.DrowsinessLogic;


public class Main {
    public static void main(String[] args) {

        // Cargar la librería de OpenCV
        System.load(System.getProperty("user.dir") + "\\libs\\opencv_java4120.dll");

        // Ruta absoluta donde están los archivos XML
        String cascadesPath;
        try {
            cascadesPath = new java.io.File(
                    Main.class.getClassLoader().getResource("cascades/").toURI()
            ).getAbsolutePath() + "\\";
        } catch (Exception e) {
            throw new RuntimeException("No se pudo obtener la ruta de cascades", e);
        }


// Crear cada componente
        CameraService cameraService = new CameraService();
        CameraView view = new CameraView();
        FaceDetector faceDetector = new FaceDetector(cascadesPath + "haarcascade_frontalface_default.xml");
        EyeDetector eyeDetector = new EyeDetector(cascadesPath + "haarcascade_eye_tree_eyeglasses.xml");
        DrowsinessLogic drowsinessLogic = new DrowsinessLogic();

// Iniciar el controlador con todos los componentes
        DetectionController controller = new DetectionController(cameraService, view, faceDetector, eyeDetector, drowsinessLogic);
        controller.start();
    }
}


