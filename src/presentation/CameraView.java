package presentation;

import application.DetectionController;
import presentation.DetectorView;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import domain.EyeState;


public class CameraView extends JFrame implements DetectorView {

    private static final Color BG_DARK      = new Color(20, 30, 50);
    private static final Color BG_HEADER    = new Color(28, 52, 87);
    private static final Color BG_LIGHT     = new Color(245, 245, 245);
    private static final Color COLOR_GREEN  = new Color(80, 180, 80);
    private static final Color COLOR_YELLOW = new Color(210, 160, 50);
    private static final Color COLOR_RED    = new Color(200, 60, 60);
    private static final Color COLOR_BLUE   = new Color(50, 120, 200);
    private static final Color TEXT_MUTED   = new Color(120, 120, 120);
    private static final Color TEXT_DARK    = new Color(50, 50, 50);

    private JLabel       cameraLabel;
    private JLabel       statusValueLabel;
    private JLabel       closedTimeValueLabel;
    private JLabel       eyesDetectedDot;
    private JLabel       eyesDetectedValue;
    private JProgressBar alarmBar;
    private JLabel       alarmBarLabel;
    private JLabel       alarmTagLabel;
    private JLabel       moduleVisualDot;
    private JLabel       moduleSomnolenceDot;
    private JLabel       moduleAlarmDot;
    private JButton      pauseButton;
    private JButton      stopButton;

    private boolean paused = false;
    private DetectionController controller;

    public CameraView() {
        setTitle("Detector de fatiga del conductor");
        setSize(1100, 720);
        setMinimumSize(new Dimension(900, 620));
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_LIGHT);
        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildCamera(), BorderLayout.CENTER);
        root.add(buildBottom(), BorderLayout.SOUTH);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                cerrarLimpiamente();
            }
        });

        setContentPane(root);
        setVisible(true);
    }

    public void setController(DetectionController controller) {
        this.controller = controller;
    }

    private void cerrarLimpiamente() {
        if (controller != null) {
            controller.detener();
        }
        dispose();
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 12));
        header.setBackground(BG_HEADER);
        JLabel title = new JLabel("Detector de fatiga del conductor");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 17));
        header.add(title);
        return header;
    }

    private JPanel buildCamera() {
        JPanel wrapper = new JPanel(new BorderLayout(0, 8));
        wrapper.setBackground(BG_DARK);
        wrapper.setBorder(new EmptyBorder(12, 16, 12, 16));

        JPanel liveRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        liveRow.setBackground(BG_DARK);
        JLabel dot = new JLabel("●");
        dot.setForeground(COLOR_RED);
        dot.setFont(new Font("Arial", Font.BOLD, 13));
        JLabel liveText = new JLabel("En vivo");
        liveText.setForeground(Color.WHITE);
        liveText.setFont(new Font("Arial", Font.PLAIN, 12));
        liveRow.add(dot);
        liveRow.add(liveText);

        cameraLabel = new JLabel("Sin señal de cámara", JLabel.CENTER);
        cameraLabel.setOpaque(true);
        cameraLabel.setBackground(new Color(15, 22, 38));
        cameraLabel.setForeground(new Color(90, 100, 120));
        cameraLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        cameraLabel.setBorder(BorderFactory.createLineBorder(new Color(60, 100, 170), 2));
        cameraLabel.setPreferredSize(new Dimension(640, 480));

        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(BG_DARK);
        center.add(cameraLabel);

        wrapper.add(liveRow, BorderLayout.NORTH);
        wrapper.add(center,  BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel buildBottom() {
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBackground(BG_LIGHT);
        bottom.setBorder(new EmptyBorder(16, 20, 16, 20));

        JPanel metricsRow = new JPanel(new GridLayout(1, 3, 20, 0));
        metricsRow.setBackground(BG_LIGHT);
        metricsRow.add(buildStatusCard());
        metricsRow.add(buildClosedTimeCard());
        metricsRow.add(buildEyesDetectedCard());

        JPanel middleRow = new JPanel(new GridLayout(1, 2, 20, 0));
        middleRow.setBackground(BG_LIGHT);
        middleRow.setBorder(new EmptyBorder(14, 0, 14, 0));
        middleRow.add(buildThresholdCard());
        middleRow.add(buildModulesCard());

        JPanel buttonsRow = new JPanel(new GridLayout(1, 2, 15, 0));
        buttonsRow.setBackground(BG_LIGHT);

        pauseButton = createButton("Pausar", new Color(220, 220, 220), TEXT_DARK);
        stopButton  = createButton("Detener sistema", new Color(255, 220, 220), COLOR_RED);

        pauseButton.addActionListener(e -> {
            paused = !paused;
            pauseButton.setText(paused ? "Reanudar" : "Pausar");
            if (controller != null) {
                controller.setPaused(paused);
            }
        });

        stopButton.addActionListener(e -> cerrarLimpiamente());

        buttonsRow.add(pauseButton);
        buttonsRow.add(stopButton);

        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(BG_LIGHT);
        content.add(metricsRow, BorderLayout.NORTH);
        content.add(middleRow,  BorderLayout.CENTER);
        content.add(buttonsRow, BorderLayout.SOUTH);

        bottom.add(content, BorderLayout.CENTER);
        return bottom;
    }

    private JPanel buildStatusCard() {
        JPanel card = card();
        card.add(muted("Estado actual"));
        statusValueLabel = new JLabel("Despierto");
        statusValueLabel.setFont(new Font("Arial", Font.BOLD, 13));
        statusValueLabel.setOpaque(true);
        statusValueLabel.setBackground(new Color(210, 235, 210));
        statusValueLabel.setForeground(new Color(40, 110, 40));
        statusValueLabel.setBorder(new EmptyBorder(4, 10, 4, 10));
        JPanel wrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 4));
        wrap.setBackground(Color.WHITE);
        wrap.add(statusValueLabel);
        card.add(wrap);
        return card;
    }

    private JPanel buildClosedTimeCard() {
        JPanel card = card();
        card.add(muted("Ojos cerrados"));
        closedTimeValueLabel = new JLabel("0.0 seg");
        closedTimeValueLabel.setFont(new Font("Arial", Font.BOLD, 22));
        closedTimeValueLabel.setForeground(TEXT_DARK);
        card.add(closedTimeValueLabel);
        return card;
    }

    private JPanel buildEyesDetectedCard() {
        JPanel card = card();
        card.add(muted("Ojos detectados"));
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        row.setBackground(Color.WHITE);
        eyesDetectedDot = new JLabel("●");
        eyesDetectedDot.setForeground(COLOR_GREEN);
        eyesDetectedDot.setFont(new Font("Arial", Font.BOLD, 14));
        eyesDetectedValue = new JLabel("DETECTADOS");
        eyesDetectedValue.setFont(new Font("Arial", Font.BOLD, 13));
        eyesDetectedValue.setForeground(TEXT_DARK);
        row.add(eyesDetectedDot);
        row.add(eyesDetectedValue);
        card.add(row);
        return card;
    }

    private JPanel buildThresholdCard() {
        JPanel card = card();
        card.add(muted("Umbral de alarma"));

        alarmBar = new JProgressBar(0, 3000);
        alarmBar.setValue(0);
        alarmBar.setForeground(COLOR_BLUE);
        alarmBar.setBackground(new Color(220, 220, 220));
        alarmBar.setBorderPainted(false);
        alarmBar.setPreferredSize(new Dimension(200, 8));

        alarmBarLabel = new JLabel("0.0 seg / 3.0 seg");
        alarmBarLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        alarmBarLabel.setForeground(TEXT_MUTED);

        alarmTagLabel = new JLabel("Alerta a los 3 seg");
        alarmTagLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        alarmTagLabel.setOpaque(true);
        alarmTagLabel.setBackground(new Color(255, 235, 180));
        alarmTagLabel.setForeground(new Color(160, 100, 20));
        alarmTagLabel.setBorder(new EmptyBorder(3, 8, 3, 8));

        JPanel tagWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 4));
        tagWrap.setBackground(Color.WHITE);
        tagWrap.add(alarmTagLabel);

        card.add(alarmBar);
        card.add(alarmBarLabel);
        card.add(tagWrap);
        return card;
    }

    private JPanel buildModulesCard() {
        JPanel card = card();
        card.add(muted("Módulos activos"));

        moduleVisualDot     = new JLabel("●");
        moduleSomnolenceDot = new JLabel("●");
        moduleAlarmDot      = new JLabel("●");

        moduleVisualDot.setForeground(COLOR_GREEN);
        moduleSomnolenceDot.setForeground(COLOR_GREEN);
        moduleAlarmDot.setForeground(COLOR_GREEN);

        card.add(moduleRow("Detección visual",   moduleVisualDot));
        card.add(moduleRow("Lógica somnolencia", moduleSomnolenceDot));
        card.add(moduleRow("Alarma sonora",       moduleAlarmDot));
        return card;
    }

    private JPanel card() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(10, 14, 10, 14)
        ));
        return p;
    }

    private JLabel muted(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Arial", Font.PLAIN, 11));
        l.setForeground(TEXT_MUTED);
        return l;
    }

    private JPanel moduleRow(String name, JLabel dot) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(Color.WHITE);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        nameLabel.setForeground(TEXT_DARK);
        dot.setFont(new Font("Arial", Font.BOLD, 14));
        row.add(nameLabel, BorderLayout.WEST);
        row.add(dot,       BorderLayout.EAST);
        return row;
    }

    private JButton createButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.PLAIN, 13));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(0, 38));
        return btn;
    }

    public void updateImage(BufferedImage image, EyeState estado, long tiempoMs) {
        if (paused) return;

        SwingUtilities.invokeLater(() -> {
            if (image != null) {
                int w = cameraLabel.getWidth();
                int h = cameraLabel.getHeight();
                if (w > 0 && h > 0) {
                    double aspectRatio = 640.0 / 480.0;
                    int newWidth  = w;
                    int newHeight = (int)(w / aspectRatio);

                    if (newHeight > h) {
                        newHeight = h;
                        newWidth  = (int)(h * aspectRatio);
                    }

                    // SCALE_FAST reduce carga de CPU manteniendo fluidez
                    Image scaled = image.getScaledInstance(newWidth, newHeight, Image.SCALE_FAST);
                    cameraLabel.setIcon(new ImageIcon(scaled));
                    cameraLabel.setText("");
                }
            }

            double seg = tiempoMs / 1000.0;
            closedTimeValueLabel.setText(String.format("%.1f seg", seg));

            int clamped = (int) Math.min(tiempoMs, 3000);
            alarmBar.setValue(clamped);
            alarmBarLabel.setText(String.format("%.1f seg / 3.0 seg", seg));

            switch (estado) {
                case DESPIERTO -> {
                    statusValueLabel.setText("Despierto");
                    statusValueLabel.setBackground(new Color(210, 235, 210));
                    statusValueLabel.setForeground(new Color(40, 110, 40));
                    alarmBar.setForeground(COLOR_BLUE);
                    eyesDetectedDot.setForeground(COLOR_GREEN);
                    eyesDetectedValue.setForeground(COLOR_GREEN);
                    eyesDetectedValue.setText("DETECTADOS");
                }
                case SOMNOLIENTO -> {
                    statusValueLabel.setText("Somnoliento");
                    statusValueLabel.setBackground(new Color(255, 240, 180));
                    statusValueLabel.setForeground(new Color(160, 100, 10));
                    alarmBar.setForeground(COLOR_YELLOW);
                    eyesDetectedDot.setForeground(COLOR_RED);
                    eyesDetectedValue.setForeground(COLOR_RED);
                    eyesDetectedValue.setText("NO DETECTADOS");
                }
                case ALARMA_ACTIVADA -> {
                    statusValueLabel.setText("¡ALARMA!");
                    statusValueLabel.setBackground(new Color(255, 200, 200));
                    statusValueLabel.setForeground(COLOR_RED);
                    alarmBar.setForeground(COLOR_RED);
                    eyesDetectedDot.setForeground(COLOR_RED);
                    eyesDetectedValue.setForeground(COLOR_RED);
                    eyesDetectedValue.setText("NO DETECTADOS");
                }
            }
        });
    }

    public boolean isPaused() {
        return paused;
    }
}