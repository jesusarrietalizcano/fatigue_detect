package presentation;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import domain.EyeState;


public class CameraView extends JFrame {

    // En esta sección se definen los colores reutilizables en toda la interfaz,
    // para mantener un diseño consistente y facilitar futuros cambios de tema.
    private static final Color BG_DARK      = new Color(20, 30, 50);
    private static final Color BG_HEADER    = new Color(28, 52, 87);
    private static final Color BG_LIGHT     = new Color(245, 245, 245);
    private static final Color COLOR_GREEN  = new Color(80, 180, 80);
    private static final Color COLOR_YELLOW = new Color(210, 160, 50);
    private static final Color COLOR_RED    = new Color(200, 60, 60);
    private static final Color COLOR_BLUE   = new Color(50, 120, 200);
    private static final Color TEXT_MUTED   = new Color(120, 120, 120);
    private static final Color TEXT_DARK    = new Color(50, 50, 50);

    // En esta sección se declaran los componentes que se actualizan en tiempo real
    // cada vez que llega un nuevo frame desde el controlador de detección.
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

    // Variable que controla si la vista está en pausa
    private boolean paused = false;


    // En esta parte se configura la ventana principal (tamaño, título, cierre)
    // y se ensamblan los tres paneles principales: encabezado, cámara y panel inferior.
    public CameraView() {
        setTitle("Detector de fatiga del conductor");
        setSize(1100, 720);
        setMinimumSize(new Dimension(900, 620));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel raíz con layout en tres zonas: norte, centro y sur
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_LIGHT);
        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildCamera(), BorderLayout.CENTER);
        root.add(buildBottom(), BorderLayout.SOUTH);

        setContentPane(root);
        setVisible(true);
    }


    // En esta parte se construye la barra superior azul oscuro
    // que muestra el título de la aplicación.
    private JPanel buildHeader() {
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 12));
        header.setBackground(BG_HEADER);
        JLabel title = new JLabel("Detector de fatiga del conductor");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 17));
        header.add(title);
        return header;
    }

    // En esta parte se construye el panel central oscuro donde se muestra
    // el video en tiempo real de la cámara, junto con el indicador "En vivo".
    private JPanel buildCamera() {
        JPanel wrapper = new JPanel(new BorderLayout(0, 8));
        wrapper.setBackground(BG_DARK);
        wrapper.setBorder(new EmptyBorder(12, 16, 12, 16));

        // Fila superior con el punto rojo y la etiqueta "En vivo"
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

        // Label donde se renderiza cada frame de la cámara
        cameraLabel = new JLabel("Sin señal de cámara", JLabel.CENTER);
        cameraLabel.setOpaque(true);
        cameraLabel.setBackground(new Color(15, 22, 38));
        cameraLabel.setForeground(new Color(90, 100, 120));
        cameraLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        cameraLabel.setBorder(BorderFactory.createLineBorder(new Color(60, 100, 170), 2));
        cameraLabel.setPreferredSize(new Dimension(640, 420));

        // Panel central que centra el label de la cámara
        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(BG_DARK);
        center.add(cameraLabel);

        wrapper.add(liveRow, BorderLayout.NORTH);
        wrapper.add(center,  BorderLayout.CENTER);
        return wrapper;
    }

    // En esta parte se construye la sección de abajo que contiene tres filas:
    // 1) Tarjetas de métricas (estado, ojos cerrados, ojos detectados)
    // 2) Umbral de alarma y módulos activos
    // 3) Botones de Pausar y Detener sistema
    private JPanel buildBottom() {
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBackground(BG_LIGHT);
        bottom.setBorder(new EmptyBorder(16, 20, 16, 20));

        // Fila 1: las tres tarjetas de estado en tiempo real
        JPanel metricsRow = new JPanel(new GridLayout(1, 3, 20, 0));
        metricsRow.setBackground(BG_LIGHT);
        metricsRow.add(buildStatusCard());
        metricsRow.add(buildClosedTimeCard());
        metricsRow.add(buildEyesDetectedCard());

        // Fila 2: umbral de alarma a la izquierda, módulos activos a la derecha
        JPanel middleRow = new JPanel(new GridLayout(1, 2, 20, 0));
        middleRow.setBackground(BG_LIGHT);
        middleRow.setBorder(new EmptyBorder(14, 0, 14, 0));
        middleRow.add(buildThresholdCard());
        middleRow.add(buildModulesCard());

        // Fila 3: botones de acción
        JPanel buttonsRow = new JPanel(new GridLayout(1, 2, 15, 0));
        buttonsRow.setBackground(BG_LIGHT);

        pauseButton = createButton("Pausar", new Color(220, 220, 220), TEXT_DARK);
        stopButton  = createButton("Detener sistema", new Color(255, 220, 220), COLOR_RED);

        // Al pausar, se alterna el texto del botón y se congela la actualización de la vista
        pauseButton.addActionListener(e -> {
            paused = !paused;
            pauseButton.setText(paused ? "Reanudar" : "Pausar");
        });

        // Al detener, se cierra completamente la aplicación
        stopButton.addActionListener(e -> System.exit(0));

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

    // En esta parte se construye la tarjeta que muestra el estado del conductor
    // (Despierto, Somnoliento o ¡ALARMA!) con un badge de color que va cambiaando
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


    // En esta parte se construye la tarjeta que muestra cuántos segundos
    // lleva el conductor con los ojos cerrados, en formato "X.X seg".
    private JPanel buildClosedTimeCard() {
        JPanel card = card();
        card.add(muted("Ojos cerrados"));
        closedTimeValueLabel = new JLabel("0.0 seg");
        closedTimeValueLabel.setFont(new Font("Arial", Font.BOLD, 22));
        closedTimeValueLabel.setForeground(TEXT_DARK);
        card.add(closedTimeValueLabel);
        return card;
    }

    // En esta parte se construye la tarjeta que indica con un punto de color
    // y un texto "true/false" si la cámara está detectando los ojos del conductor.
    private JPanel buildEyesDetectedCard() {
        JPanel card = card();
        card.add(muted("Ojos detectados"));
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        row.setBackground(Color.WHITE);
        eyesDetectedDot = new JLabel("●");
        eyesDetectedDot.setForeground(COLOR_GREEN);
        eyesDetectedDot.setFont(new Font("Arial", Font.BOLD, 14));
        eyesDetectedValue = new JLabel("true");
        eyesDetectedValue.setFont(new Font("Arial", Font.BOLD, 13));
        eyesDetectedValue.setForeground(TEXT_DARK);
        row.add(eyesDetectedDot);
        row.add(eyesDetectedValue);
        card.add(row);
        return card;
    }

    // En esta parte se construye la tarjeta con la barra de progreso que muestra
    // cuánto tiempo llevan cerrados los ojos respecto al umbral de 3 segundos.
    // La barra cambia de color según el nivel de alerta.
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

    // En esta parte se construye la tarjeta que lista los tres módulos del sistema
    // (Detección visual, Lógica somnolencia, Alarma sonora) con un punto de color
    // que indica si cada módulo está operativo o no.
    private JPanel buildModulesCard() {
        JPanel card = card();
        card.add(muted("Módulos activos"));

        moduleVisualDot     = new JLabel("●");
        moduleSomnolenceDot = new JLabel("●");
        moduleAlarmDot      = new JLabel("●");

        moduleVisualDot.setForeground(COLOR_GREEN);
        moduleSomnolenceDot.setForeground(COLOR_GREEN);
        moduleAlarmDot.setForeground(COLOR_YELLOW);

        card.add(moduleRow("Detección visual",   moduleVisualDot));
        card.add(moduleRow("Lógica somnolencia", moduleSomnolenceDot));
        card.add(moduleRow("Alarma sonora",       moduleAlarmDot));
        return card;
    }

    // En esta sección se agrupan los métodos auxiliares que crean componentes
    // reutilizables: tarjetas con borde, etiquetas de texto secundario,
    // filas de módulo y botones estilizados.

    // Crea un panel blanco con borde gris claro y padding, usado como tarjeta base
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

    // Crea una etiqueta de texto secundario (gris, fuente pequeña) para los títulos de tarjeta
    private JLabel muted(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Arial", Font.PLAIN, 11));
        l.setForeground(TEXT_MUTED);
        return l;
    }

    // Crea una fila con el nombre del módulo a la izquierda y su punto de estado a la derecha
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

    // Crea un botón estilizado con color de fondo, texto y cursor de mano
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

    // apartado de tiempo real
    // En esta parte se recibe cada frame procesado desde el DetectionController.
    // Se actualiza la imagen de la cámara, el tiempo de ojos cerrados,
    // la barra de progreso y los colores del estado según el resultado de la lógica.
    public void updateImage(BufferedImage image, EyeState estado, long tiempoMs) {
        // Si está en pausa, no se actualiza ningún componente
        if (paused) return;

        SwingUtilities.invokeLater(() -> {

            // Actualizar la imagen de la cámara escalada al tamaño del panel
            if (image != null) {
                int w = cameraLabel.getWidth();
                int h = cameraLabel.getHeight();
                if (w > 0 && h > 0) {
                    Image scaled = image.getScaledInstance(w, h, Image.SCALE_FAST);
                    cameraLabel.setIcon(new ImageIcon(scaled));
                    cameraLabel.setText("");
                }
            }

            // Actualizar el contador de tiempo con ojos cerrados
            double seg = tiempoMs / 1000.0;
            closedTimeValueLabel.setText(String.format("%.1f seg", seg));

            // Actualizar la barra de progreso del umbral (máximo 3000 ms)
            int clamped = (int) Math.min(tiempoMs, 3000);
            alarmBar.setValue(clamped);
            alarmBarLabel.setText(String.format("%.1f seg / 3.0 seg", seg));

            // Actualizar colores y textos según el estado  detectado
            switch (estado) {
                case DESPIERTO -> {
                    // Estado normal: verde, ojos abiertos detectados
                    statusValueLabel.setText("Despierto");
                    statusValueLabel.setBackground(new Color(210, 235, 210));
                    statusValueLabel.setForeground(new Color(40, 110, 40));
                    alarmBar.setForeground(COLOR_BLUE);
                    eyesDetectedDot.setForeground(COLOR_GREEN);
                    eyesDetectedValue.setForeground(COLOR_GREEN);
                    eyesDetectedValue.setText("DETECTADOS");
                }
                case SOMNOLIENTO -> {
                    // Estado de alerta: amarillo, ojos cerrados por más de 1.5 seg
                    statusValueLabel.setText("Somnoliento");
                    statusValueLabel.setBackground(new Color(255, 240, 180));
                    statusValueLabel.setForeground(new Color(160, 100, 10));
                    alarmBar.setForeground(COLOR_YELLOW);
                    eyesDetectedDot.setForeground(COLOR_RED);
                    eyesDetectedValue.setForeground(COLOR_RED);
                    eyesDetectedValue.setText("NO DETECTADOS");
                }
                case ALARMA_ACTIVADA -> {
                    // Estado crítico: rojo, ojos cerrados por más de 3 seg
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

    // Retorna si la vista está actualmente en pausa (usado por el controlador)
    public boolean isPaused() {
        return paused;
    }
}