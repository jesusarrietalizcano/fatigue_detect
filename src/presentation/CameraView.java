package presentation;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class CameraView extends JFrame {

    private JLabel imageLabel;

    public CameraView() {
        setTitle("Detector de fatiga");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // centrar la ventana en pantalla

        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(JLabel.CENTER);

        // Agregar el label dentro de un panel que ocupa todo el espacio
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(imageLabel, BorderLayout.CENTER);
        add(panel);

        setVisible(true);
    }

    public void updateImage(BufferedImage image) {
        if (image != null) {
            // Escalar la imagen al tamaño actual de la ventana
            int w = getWidth();
            int h = getHeight();
            Image scaled = image.getScaledInstance(w, h, Image.SCALE_FAST);
            imageLabel.setIcon(new ImageIcon(scaled));
            imageLabel.repaint();
        }
    }
}