import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

public class formulario extends javax.swing.JFrame {

    private JLabel[] etiquetasCartas;
    private int[] valoresCartas;
    private boolean[] cartasEncontradas;
    private int indicePrimeraCartaSeleccionada = -1;
    private int indiceSegundaCartaSeleccionada = -1;
    private boolean bloqueoClicks = false;

    private int nivelActual = 2;
    private int filasActuales;
    private int columnasActuales;

    private static final int VALOR_CARTA_EVIL = -1;

    private static final String AUDIO_EVIL = "/Audios/evil.wav";
    private static final String AUDIO_PAREJA_CORRECTA = "/Audios/pareja-correcta.wav";
    private static final String AUDIO_NIVEL_FINALIZADO = "/Audios/nivel-finalizado.wav";

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

    public formulario() {
        initComponents();
        configurarMenu();
        configurar_apariencia_juego();
        seleccionarNivelInicial();
    }

    private void configurarMenu() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menuNiveles = new JMenu("Niveles");

        for (int nivel = 2; nivel <= 7; nivel++) {
            final int nivelSeleccionado = nivel;
            JMenuItem itemNivel = new JMenuItem(nivel + "x" + nivel);
            itemNivel.addActionListener(e -> cargar_nivel(nivelSeleccionado));
            menuNiveles.add(itemNivel);
        }

        JMenu menuJuego = new JMenu("Juego");
        JMenuItem itemSalir = new JMenuItem("Salir");
        itemSalir.addActionListener(e -> salirDelJuego());
        menuJuego.add(itemSalir);

        menuBar.add(menuNiveles);
        menuBar.add(menuJuego);
        setJMenuBar(menuBar);
    }

    private void seleccionarNivelInicial() {
        Integer nivel = mostrarDialogoSeleccionNivel();
        if (nivel == null) {
            salirDelJuego();
            return;
        }
        cargar_nivel(nivel);
    }

    private Integer mostrarDialogoSeleccionNivel() {
        String[] opciones = {"2x2", "3x3", "4x4", "5x5", "6x6", "7x7", "Salir"};
        int respuesta = JOptionPane.showOptionDialog(
                this,
                "Eligir un nivel para jugar:",
                "Feliz día de la Felicidad",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                opciones,
                opciones[0]
        );

        if (respuesta < 0 || respuesta == 6) {
            return null;
        }

        return respuesta + 2;
    }

    private void salirDelJuego() {
        dispose();
        System.exit(0);
    }

    private void cargar_nivel(int nivel) {
        nivelActual = nivel;
        inicializar_juego_parejas(nivel, nivel);
    }

    private void inicializar_juego_parejas(int filas, int columnas) {
        filasActuales = filas;
        columnasActuales = columnas;
        indicePrimeraCartaSeleccionada = -1;
        indiceSegundaCartaSeleccionada = -1;
        bloqueoClicks = false;

        int numeroCartas = filas * columnas;
        etiquetasCartas = new JLabel[numeroCartas];
        valoresCartas = new int[numeroCartas];
        cartasEncontradas = new boolean[numeroCartas];

        getContentPane().removeAll();
        getContentPane().setLayout(new GridLayout(filas, columnas, 8, 8));

        for (int i = 0; i < numeroCartas; i++) {
            JLabel etiqueta = new JLabel();
            etiqueta.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
            final int indiceCarta = i;
            etiqueta.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    manejar_clic_en_carta(indiceCarta);
                }
            });
            etiquetasCartas[i] = etiqueta;
            getContentPane().add(etiqueta);
        }

        preparar_valores_del_nivel();
        ajustar_tamano_etiquetas();
        for (JLabel etiqueta : etiquetasCartas) {
            configurar_carta_oculta(etiqueta);
        }

        revalidate();
        repaint();
    }

    private void preparar_valores_del_nivel() {
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

    private void configurar_carta_oculta(JLabel etiqueta) {
        etiqueta.setOpaque(true);
        etiqueta.setBackground(Color.LIGHT_GRAY);
        etiqueta.setForeground(Color.BLACK);
        etiqueta.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        etiqueta.setIcon(null);
        etiqueta.setText("?");
    }

    private ImageIcon escalar_imagen(java.net.URL recurso, int ancho, int alto) {
        ImageIcon iconoOriginal = new ImageIcon(recurso);
        Image imagenEscalada = iconoOriginal.getImage().getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
        return new ImageIcon(imagenEscalada);
    }

    public void ajustar_tamano_etiquetas() {
        if (etiquetasCartas == null) {
            return;
        }

        int mayorDimension = Math.max(filasActuales, columnasActuales);
        int base = Math.max(70, 420 / mayorDimension);
        java.awt.Dimension tamano = new java.awt.Dimension(base, base);

        for (JLabel etiqueta : etiquetasCartas) {
            etiqueta.setPreferredSize(tamano);
            etiqueta.setMinimumSize(tamano);
            etiqueta.setMaximumSize(tamano);
        }

        pack();
        setLocationRelativeTo(null);
    }

    public void configurar_apariencia_juego() {
        getContentPane().setBackground(new Color(30, 30, 60));
    }

    private void manejar_clic_en_carta(int indiceCarta) {
        if (bloqueoClicks || cartasEncontradas[indiceCarta]) {
            return;
        }

        mostrar_carta(indiceCarta);

        if (valoresCartas[indiceCarta] == VALOR_CARTA_EVIL) {
            bloqueoClicks = true;
            JOptionPane.showMessageDialog(this, "Encontraste la carita mala, intenta otra vez!");
            cargar_nivel(nivelActual);
            return;
        }

        if (indicePrimeraCartaSeleccionada == -1) {
            indicePrimeraCartaSeleccionada = indiceCarta;
        } else if (indiceSegundaCartaSeleccionada == -1 && indiceCarta != indicePrimeraCartaSeleccionada) {
            indiceSegundaCartaSeleccionada = indiceCarta;
            verificar_pareja();
        }
    }

    private void mostrar_carta(int indiceCarta) {
        String rutaImagen = (valoresCartas[indiceCarta] == VALOR_CARTA_EVIL)
                ? rutaCartaEvil
                : rutasImagenesBase[valoresCartas[indiceCarta]];

        java.net.URL recurso = getClass().getResource(rutaImagen);
        if (recurso != null) {
            int ancho = Math.max(50, etiquetasCartas[indiceCarta].getWidth());
            int alto = Math.max(50, etiquetasCartas[indiceCarta].getHeight());
            ImageIcon iconoEscalado = escalar_imagen(recurso, ancho, alto);
            etiquetasCartas[indiceCarta].setIcon(iconoEscalado);
            etiquetasCartas[indiceCarta].setText("");
        } else {
            etiquetasCartas[indiceCarta].setIcon(null);
            etiquetasCartas[indiceCarta].setText("X");
        }

        etiquetasCartas[indiceCarta].setBackground(Color.WHITE);

        if (valoresCartas[indiceCarta] == VALOR_CARTA_EVIL) {
            reproducirSonido(AUDIO_EVIL);
        }
    }

    private void ocultar_carta(int indiceCarta) {
        if (!cartasEncontradas[indiceCarta]) {
            configurar_carta_oculta(etiquetasCartas[indiceCarta]);
        }
    }

    private void verificar_pareja() {
        if (valoresCartas[indicePrimeraCartaSeleccionada] == valoresCartas[indiceSegundaCartaSeleccionada]) {
            reproducirSonido(AUDIO_PAREJA_CORRECTA);
            cartasEncontradas[indicePrimeraCartaSeleccionada] = true;
            cartasEncontradas[indiceSegundaCartaSeleccionada] = true;
            etiquetasCartas[indicePrimeraCartaSeleccionada].setBackground(Color.YELLOW);
            etiquetasCartas[indiceSegundaCartaSeleccionada].setBackground(Color.YELLOW);

            indicePrimeraCartaSeleccionada = -1;
            indiceSegundaCartaSeleccionada = -1;

            if (todas_las_parejas_encontradas()) {
                reproducirSonido(AUDIO_NIVEL_FINALIZADO);
                JOptionPane.showMessageDialog(this, "Felicidades! Completaste el nivel " + nivelActual + "x" + nivelActual);
                Integer siguienteNivel = mostrarDialogoSeleccionNivel();
                if (siguienteNivel == null) {
                    salirDelJuego();
                } else {
                    cargar_nivel(siguienteNivel);
                }
            }
        } else {
            bloqueoClicks = true;
            javax.swing.Timer temporizador = new javax.swing.Timer(800, e -> {
                ocultar_carta(indicePrimeraCartaSeleccionada);
                ocultar_carta(indiceSegundaCartaSeleccionada);
                indicePrimeraCartaSeleccionada = -1;
                indiceSegundaCartaSeleccionada = -1;
                bloqueoClicks = false;
            });
            temporizador.setRepeats(false);
            temporizador.start();
        }
    }

    private boolean todas_las_parejas_encontradas() {
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
            java.util.logging.Logger.getLogger(formulario.class.getName()).log(java.util.logging.Level.WARNING, "No se pudo reproducir el audio: " + rutaAudio, ex);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(formulario.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(() -> new formulario().setVisible(true));
    }
}
