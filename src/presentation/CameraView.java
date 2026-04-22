package presentation;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class CameraView extends JFrame {

    private JLabel imageLabel;

    public CameraView() {
        setTitle("Detector de Fatiga");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        imageLabel = new JLabel();
        add(imageLabel);

        setVisible(true);
    }

    public void updateImage(BufferedImage image) {
        if (image != null) {
            imageLabel.setIcon(new ImageIcon(image));
        }
    }
}