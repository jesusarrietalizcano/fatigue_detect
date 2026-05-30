package infrastructure.alarm;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;

public class AlarmService {

    private Clip clip;
    private boolean playing = false;

    public void activar() {
        // Si ya está sonando no la reinicia
        if (playing) return;

        try {
            // Cargar el archivo .wav desde resources/sounds/
            URL soundUrl = getClass().getClassLoader().getResource("sounds/alarma.wav");

            if (soundUrl == null) {
                System.out.println("No se encontró el archivo alarma.wav");
                return;
            }

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundUrl);
            clip = AudioSystem.getClip();
            clip.open(audioStream);

            // Reproducir en bucle infinito
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();
            playing = true;

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.out.println("Error al reproducir alarma: " + e.getMessage());
        }
    }

    public void detener() {
        if (clip != null && playing) {
            clip.stop();
            clip.close();
            playing = false;
        }
    }

    public boolean isPlaying() {
        return playing;
    }
}