package application;

import domain.DrowsinessLogic;
import domain.EyeState;
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
    private DrowsinessLogic drowsinessLogic;

    private int framesSinOjos = 0;
    private static final int UMBRAL_FRAMES = 3;

    public DetectionController(CameraService cameraService, CameraView view,
                               FaceDetector faceDetector, EyeDetector eyeDetector,
                               DrowsinessLogic drowsinessLogic) {
        this.cameraService   = cameraService;
        this.view            = view;
        this.faceDetector    = faceDetector;
        this.eyeDetector     = eyeDetector;
        this.drowsinessLogic = drowsinessLogic;
    }

    public void start() {
        if (!cameraService.isOpened()) {
            System.out.println("Error con la cámara");
            return;
        }

        while (true) {
            Mat frame = cameraService.getFrame();

            if (!frame.empty()) {
                Core.flip(frame, frame, 1);

                boolean ojosDetectados = procesarFrame(frame);

                EyeState estado = drowsinessLogic.evaluate(ojosDetectados);
                System.out.println("Estado: " + estado + " | Cerrados: " + drowsinessLogic.getClosedDurationMs() + "ms");

                if (drowsinessLogic.shouldActivateAlarm()) {
                    System.out.println("ALARMA ACTIVADA");

                }

                var image = ImageUtils.matToBufferedImage(frame);
                view.updateImage(image, estado, drowsinessLogic.getClosedDurationMs());
            }
        }
    }

    private boolean procesarFrame(Mat frame) {
        Rect[] rostros = faceDetector.detect(frame);

        if (rostros.length == 0) {
            framesSinOjos = 0;
            return false;
        }

        boolean ojosDetectados = false;

        for (Rect rostro : rostros) {
            Imgproc.rectangle(frame, rostro, new Scalar(255, 0, 0), 2);

            Mat regionRostro = new Mat(frame, rostro);
            Rect[] ojos = eyeDetector.detect(regionRostro);

            for (Rect ojo : ojos) {
                Rect ojoEnFrame = new Rect(
                        rostro.x + ojo.x,
                        rostro.y + ojo.y + (int)(rostro.height * 0.25),
                        ojo.width,
                        ojo.height
                );
                Imgproc.rectangle(frame, ojoEnFrame, new Scalar(0, 255, 0), 2);
            }

            if (ojos.length >= 2) {
                ojosDetectados = true;
            }
        }

        if (ojosDetectados) {
            framesSinOjos = 0;
            return true;
        }

        framesSinOjos++;
        return framesSinOjos >= UMBRAL_FRAMES;
    }
}