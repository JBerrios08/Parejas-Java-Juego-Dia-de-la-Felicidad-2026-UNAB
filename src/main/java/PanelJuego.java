import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;

public class PanelJuego extends JPanel {

    private static final int VALOR_CARTA_EVIL = -1;
    private static final String AUDIO_EVIL = "/Audios/evil.wav";
    private static final String AUDIO_PAREJA_CORRECTA = "/Audios/pareja-correcta.wav";
    private static final String AUDIO_NIVEL_FINALIZADO = "/Audios/nivel-finalizado.wav";

    private final MainFrame mainFrame;
    private final JPanel tableroPanel;
    private final JLabel estadoLabel;

    private JLabel[] etiquetasCartas;
    private int[] valoresCartas;
    private boolean[] cartasEncontradas;
    private int indicePrimeraCartaSeleccionada = -1;
    private int indiceSegundaCartaSeleccionada = -1;
    private boolean bloqueoClicks = false;
    private int nivelActual = 2;
    private int filasActuales;
    private int columnasActuales;

    private final String[] rutasImagenesBase = {
        "/Imagenes/arcoiris.png",
        "/Imagenes/celebracion.png",
        "/Imagenes/corazon.png",
        "/Imagenes/fiesta.png",
        "/Imagenes/riendo-fuerte.png",
        "/Imagenes/risa-grande.png",
        "/Imagenes/sonrisa-clasica.png"
    };

    private final String rutaCartaEvil = "/Imagenes/evil.png";
    private final Map<String, Clip> cacheSonidos = new HashMap<>();

    public PanelJuego(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(12, 12));
        setBackground(new Color(236, 243, 252));
        setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        estadoLabel = new JLabel("Selecciona un nivel para comenzar", SwingConstants.CENTER);
        estadoLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        estadoLabel.setForeground(new Color(58, 78, 116));
        add(estadoLabel, BorderLayout.NORTH);

        tableroPanel = new JPanel();
        tableroPanel.setOpaque(false);
        add(tableroPanel, BorderLayout.CENTER);

        JButton volverBtn = new JButton("Volver al inicio");
        volverBtn.setIcon(crearIcono(FontAwesomeSolid.ARROW_LEFT));
        volverBtn.setFont(new Font("SansSerif", Font.BOLD, 15));
        volverBtn.addActionListener(e -> mainFrame.cambiarPantalla(MainFrame.PANTALLA_INICIO));
        add(volverBtn, BorderLayout.SOUTH);
    }


    private Icon crearIcono(FontAwesomeSolid icono) {
        return FontIcon.of(icono, 15, new Color(58, 78, 116));
    }

    public void cargarNivel(int nivel) {
        this.nivelActual = nivel;
        inicializarJuegoParejas(nivel, nivel);
    }

    private void inicializarJuegoParejas(int filas, int columnas) {
        filasActuales = filas;
        columnasActuales = columnas;
        indicePrimeraCartaSeleccionada = -1;
        indiceSegundaCartaSeleccionada = -1;
        bloqueoClicks = false;

        int numeroCartas = filas * columnas;
        etiquetasCartas = new JLabel[numeroCartas];
        valoresCartas = new int[numeroCartas];
        cartasEncontradas = new boolean[numeroCartas];

        tableroPanel.removeAll();
        tableroPanel.setLayout(new GridLayout(filas, columnas, 8, 8));
        estadoLabel.setText("Nivel actual: " + nivelActual + "x" + nivelActual);

        for (int i = 0; i < numeroCartas; i++) {
            JLabel etiqueta = new JLabel("?", SwingConstants.CENTER);
            etiqueta.setOpaque(true);
            etiqueta.setBackground(new Color(221, 226, 241));
            etiqueta.setForeground(new Color(58, 78, 116));
            etiqueta.setFont(new Font("SansSerif", Font.BOLD, 22));
            etiqueta.setBorder(BorderFactory.createLineBorder(new Color(166, 177, 210), 1));

            final int indiceCarta = i;
            etiqueta.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    manejarClicEnCarta(indiceCarta);
                }
            });
            etiquetasCartas[i] = etiqueta;
            tableroPanel.add(etiqueta);
        }

        prepararValoresDelNivel();
        ajustarTamanoEtiquetas();

        revalidate();
        repaint();
    }

    private void prepararValoresDelNivel() {
        List<Integer> valores = new ArrayList<>();
        int numeroCartas = filasActuales * columnasActuales;
        int parejasNecesarias = numeroCartas / 2;

        for (int i = 0; i < parejasNecesarias; i++) {
            int valorImagen = i % rutasImagenesBase.length;
            valores.add(valorImagen);
            valores.add(valorImagen);
        }

        if (numeroCartas % 2 != 0) {
            valores.add(VALOR_CARTA_EVIL);
        }

        Collections.shuffle(valores);
        for (int i = 0; i < valores.size(); i++) {
            valoresCartas[i] = valores.get(i);
            cartasEncontradas[i] = false;
        }
    }

    private void ajustarTamanoEtiquetas() {
        int mayorDimension = Math.max(filasActuales, columnasActuales);
        int base = Math.max(70, 460 / mayorDimension);

        for (JLabel etiqueta : etiquetasCartas) {
            etiqueta.setPreferredSize(new java.awt.Dimension(base, base));
        }
    }

    private void manejarClicEnCarta(int indiceCarta) {
        if (bloqueoClicks || cartasEncontradas[indiceCarta]) {
            return;
        }

        mostrarCarta(indiceCarta);

        if (valoresCartas[indiceCarta] == VALOR_CARTA_EVIL) {
            bloqueoClicks = true;
            JOptionPane.showMessageDialog(this, "Los obstáculos son parte del camino, ¡inténtalo de nuevo!");
            cargarNivel(nivelActual);
            return;
        }

        if (indicePrimeraCartaSeleccionada == -1) {
            indicePrimeraCartaSeleccionada = indiceCarta;
        } else if (indiceSegundaCartaSeleccionada == -1 && indiceCarta != indicePrimeraCartaSeleccionada) {
            indiceSegundaCartaSeleccionada = indiceCarta;
            verificarPareja();
        }
    }

    private void mostrarCarta(int indiceCarta) {
        String rutaImagen = (valoresCartas[indiceCarta] == VALOR_CARTA_EVIL)
                ? rutaCartaEvil
                : rutasImagenesBase[valoresCartas[indiceCarta]];

        java.net.URL recurso = getClass().getResource(rutaImagen);
        if (recurso != null) {
            int ancho = Math.max(50, etiquetasCartas[indiceCarta].getWidth());
            int alto = Math.max(50, etiquetasCartas[indiceCarta].getHeight());
            ImageIcon iconoEscalado = escalarImagen(recurso, ancho, alto);
            etiquetasCartas[indiceCarta].setIcon(iconoEscalado);
            etiquetasCartas[indiceCarta].setText("");
        } else {
            etiquetasCartas[indiceCarta].setIcon(null);
            etiquetasCartas[indiceCarta].setText("X");
        }

        etiquetasCartas[indiceCarta].setBackground(new Color(255, 255, 255));

        if (valoresCartas[indiceCarta] == VALOR_CARTA_EVIL) {
            reproducirSonido(AUDIO_EVIL);
        }
    }

    private ImageIcon escalarImagen(java.net.URL recurso, int ancho, int alto) {
        ImageIcon iconoOriginal = new ImageIcon(recurso);
        Image imagenEscalada = iconoOriginal.getImage().getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
        return new ImageIcon(imagenEscalada);
    }

    private void ocultarCarta(int indiceCarta) {
        if (!cartasEncontradas[indiceCarta]) {
            etiquetasCartas[indiceCarta].setIcon(null);
            etiquetasCartas[indiceCarta].setText("?");
            etiquetasCartas[indiceCarta].setBackground(new Color(221, 226, 241));
        }
    }

    private void verificarPareja() {
        if (valoresCartas[indicePrimeraCartaSeleccionada] == valoresCartas[indiceSegundaCartaSeleccionada]) {
            reproducirSonido(AUDIO_PAREJA_CORRECTA);
            cartasEncontradas[indicePrimeraCartaSeleccionada] = true;
            cartasEncontradas[indiceSegundaCartaSeleccionada] = true;
            etiquetasCartas[indicePrimeraCartaSeleccionada].setBackground(new Color(218, 255, 211));
            etiquetasCartas[indiceSegundaCartaSeleccionada].setBackground(new Color(218, 255, 211));

            indicePrimeraCartaSeleccionada = -1;
            indiceSegundaCartaSeleccionada = -1;

            if (todasLasParejasEncontradas()) {
                reproducirSonido(AUDIO_NIVEL_FINALIZADO);
                JOptionPane.showMessageDialog(this, "¡Excelente! Completaste el nivel " + nivelActual + "x" + nivelActual);
                mainFrame.cambiarPantalla(MainFrame.PANTALLA_INICIO);
            }
        } else {
            bloqueoClicks = true;
            javax.swing.Timer temporizador = new javax.swing.Timer(800, e -> {
                ocultarCarta(indicePrimeraCartaSeleccionada);
                ocultarCarta(indiceSegundaCartaSeleccionada);
                indicePrimeraCartaSeleccionada = -1;
                indiceSegundaCartaSeleccionada = -1;
                bloqueoClicks = false;
            });
            temporizador.setRepeats(false);
            temporizador.start();
        }
    }

    private boolean todasLasParejasEncontradas() {
        for (int i = 0; i < cartasEncontradas.length; i++) {
            if (valoresCartas[i] != VALOR_CARTA_EVIL && !cartasEncontradas[i]) {
                return false;
            }
        }
        return true;
    }

    private void reproducirSonido(String rutaAudio) {
        try {
            Clip clip = cacheSonidos.get(rutaAudio);
            if (clip == null) {
                java.net.URL recurso = getClass().getResource(rutaAudio);
                if (recurso == null) {
                    return;
                }

                try (AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new BufferedInputStream(recurso.openStream()))) {
                    clip = AudioSystem.getClip();
                    AudioFormat formato = audioInputStream.getFormat();
                    boolean requiereConversion = formato.getSampleSizeInBits() > 16 || formato.getEncoding() != AudioFormat.Encoding.PCM_SIGNED;

                    if (requiereConversion) {
                        AudioFormat formatoCompatible = new AudioFormat(
                                AudioFormat.Encoding.PCM_SIGNED,
                                formato.getSampleRate(),
                                16,
                                formato.getChannels(),
                                formato.getChannels() * 2,
                                formato.getSampleRate(),
                                false
                        );

                        try (AudioInputStream audioConvertido = AudioSystem.getAudioInputStream(formatoCompatible, audioInputStream)) {
                            clip.open(audioConvertido);
                        }
                    } else {
                        clip.open(audioInputStream);
                    }
                    cacheSonidos.put(rutaAudio, clip);
                }
            }

            clip.stop();
            clip.setFramePosition(0);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException ex) {
            java.util.logging.Logger.getLogger(PanelJuego.class.getName()).log(
                    java.util.logging.Level.WARNING,
                    "No se pudo reproducir el audio: " + rutaAudio,
                    ex
            );
        }
    }
}
