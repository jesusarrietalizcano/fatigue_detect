package presentation;

import domain.EyeState;
import java.awt.image.BufferedImage;

public interface DetectorView {

    // Método que toda vista debe implementar para recibir
    // la imagen, el estado y el tiempo desde el controlador
    void updateImage(BufferedImage image, EyeState estado, long tiempoMs);

    // Método que toda vista debe implementar para indicar
    // si está pausada o no
    boolean isPaused();
}