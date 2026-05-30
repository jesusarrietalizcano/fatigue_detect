package application;

import domain.DrowsinessLogic;
import domain.EyeState;
import infrastructure.CameraService;
import infrastructure.FaceDetector;
import infrastructure.EyeDetector;
import infrastructure.utils.ImageUtils;
import presentation.DetectorView;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import infrastructure.alarm.AlarmService;

public class DetectionController {
    private CameraService cameraService;
    private DetectorView view;
    private FaceDetector faceDetector;
    private EyeDetector eyeDetector;
    private DrowsinessLogic drowsinessLogic;
    private volatile boolean running = true;
    private volatile boolean paused = false;
    private int framesSinOjos = 0;
    private static final int UMBRAL_FRAMES = 3;
    private AlarmService alarmService;

    public DetectionController(CameraService cameraService, DetectorView view,
                               FaceDetector faceDetector, EyeDetector eyeDetector,
                               DrowsinessLogic drowsinessLogic, AlarmService alarmService) {
        this.cameraService   = cameraService;
        this.view            = view;
        this.faceDetector    = faceDetector;
        this.eyeDetector     = eyeDetector;
        this.drowsinessLogic = drowsinessLogic;
        this.alarmService    = alarmService;
    }

    public void start() {
        if (!cameraService.isOpened()) {
            System.out.println("Error con la cámara");
            return;
        }

        while (running) {
            if (paused) {
                try { Thread.sleep(100); } catch (InterruptedException ignored) {}
                continue;
            }
            Mat frame = cameraService.getFrame();

            if (!frame.empty()) {
                Core.flip(frame, frame, 1);

                boolean ojosDetectados = procesarFrame(frame);

                EyeState estado = drowsinessLogic.evaluate(ojosDetectados);


                if (drowsinessLogic.shouldActivateAlarm()) {
                    alarmService.activar();
                } else {
                    alarmService.detener();
                }

                var image = ImageUtils.matToBufferedImage(frame);
                view.updateImage(image, estado, drowsinessLogic.getClosedDurationMs());
            }
        }
    }

    private boolean procesarFrame(Mat frame) {
        Rect[] rostros = faceDetector.detect(frame);

        // Sin rostro → resetear todo inmediatamente
        if (rostros.length == 0) {
            framesSinOjos = 0;
            drowsinessLogic.evaluateSinRostro();
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

        // Hay rostro y ojos detectados → despierto
        if (ojosDetectados) {
            framesSinOjos = 0;
            return true;
        }

        // Hay rostro pero sin ojos → contar frames
        // No esperar umbral, pasar false inmediatamente
        // El umbral solo servía para parpadeos pero causaba el bug
        framesSinOjos++;
        return false;
    }
    public void detener() {
        running = false;
        alarmService.detener();
    }
    public void setPaused(boolean paused) {
        this.paused = paused;
    }
}