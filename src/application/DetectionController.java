package application;

import infrastructure.CameraService;
import infrastructure.FaceDetector;
import infrastructure.EyeDetector;
import infrastructure.utils.ImageUtils;
import presentation.CameraView;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

public class DetectionController {
    private CameraService cameraService;
    private CameraView view;
    private FaceDetector faceDetector;
    private EyeDetector eyeDetector;

    // Contador de frames consecutivos sin ojos detectados
    private int framesSinOjos = 0;
    private static final int UMBRAL_FRAMES = 3;

    public DetectionController(CameraService cameraService, CameraView view, FaceDetector faceDetector, EyeDetector eyeDetector) {
        this.cameraService = cameraService;
        this.view = view;
        this.faceDetector = faceDetector;
        this.eyeDetector = eyeDetector;
    }
    public void start(){
        if (!cameraService.isOpened()){
            System.out.println("Error con la cámara");
            return;
        }
        while (true) {
            Mat frame = cameraService.getFrame();

            System.out.println("Frame vacío: " + frame.empty());
            System.out.println("Frame size: " + frame.size());

            if (!frame.empty()) {
                Core.flip(frame, frame, 1);

                boolean ojosDetectados = procesarFrame(frame);
                System.out.println("ojosDetectados = " + ojosDetectados);

                var image = ImageUtils.matToBufferedImage(frame);
                System.out.println("Image null: " + (image == null)); // ← clave

                view.updateImage(image);
            }
        }
    }

    private boolean procesarFrame(Mat frame){
        Rect[] rostros = faceDetector.detect(frame);
        boolean ojosDetectados = false;

        for (Rect rostro : rostros){
             Imgproc.rectangle(frame, rostro,new Scalar(255, 0, 0),2);

             Mat regionRostro = new Mat(frame, rostro);

             Rect[] ojos = eyeDetector.detect(regionRostro);
              for (Rect ojo : ojos){
                  Rect ojoEnFrame = new Rect(
                          rostro.x + ojo.x,
                          rostro.y + ojo.y,
                          ojo.width,
                          ojo.height
                  );
                  Imgproc.rectangle(frame, ojoEnFrame, new Scalar(0, 255, 0), 2);

                  ojosDetectados = true;
              }
        }
        // Si detectó ojos, resetear contador
        if (ojosDetectados) {
            framesSinOjos = 0;
            return true;
        }

// Si no detectó ojos, incrementar contador
        framesSinOjos++;

// Solo reportar false si llevan varios frames cerrados
// Un parpadeo normal no supera el umbral
        return framesSinOjos >= UMBRAL_FRAMES;
    }
}