import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;

public class PanelInicio extends JPanel {

    private final MainFrame mainFrame;

    public PanelInicio(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        setBackground(new Color(246, 244, 255));
        construirUI();
    }

    private void construirUI() {
        JPanel centro = new JPanel();
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));
        centro.setBackground(new Color(246, 244, 255));
        centro.setBorder(BorderFactory.createEmptyBorder(32, 32, 32, 32));

        JLabel logoLabel = new JLabel();
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        java.net.URL recursoLogo = getClass().getResource("/Imagenes/logo.png");
        if (recursoLogo != null) {
            ImageIcon logo = new ImageIcon(recursoLogo);
            Image logoEscalado = logo.getImage().getScaledInstance(300, 110, Image.SCALE_SMOOTH);
            logoLabel.setIcon(new ImageIcon(logoEscalado));
        } else {
            logoLabel.setText("Mente en Calma");
            logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        }

        JLabel titulo = new JLabel("Mente en Calma");
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 42));
        titulo.setForeground(new Color(70, 84, 130));

        JLabel slogan = new JLabel("Pequeños pasos, grandes cambios");
        slogan.setAlignmentX(Component.CENTER_ALIGNMENT);
        slogan.setFont(new Font("SansSerif", Font.PLAIN, 22));
        slogan.setForeground(new Color(112, 120, 150));

        centro.add(Box.createVerticalGlue());
        centro.add(logoLabel);
        centro.add(Box.createVerticalStrut(18));
        centro.add(titulo);
        centro.add(Box.createVerticalStrut(10));
        centro.add(slogan);
        centro.add(Box.createVerticalStrut(28));
        centro.add(crearPanelBotones());
        centro.add(Box.createVerticalGlue());

        add(centro, BorderLayout.CENTER);
    }

    private JPanel crearPanelBotones() {
        JPanel panelBotones = new JPanel(new GridLayout(1, 3, 14, 14));
        panelBotones.setBackground(new Color(246, 244, 255));

        JButton btnIniciar = crearBoton("Iniciar", FontAwesomeSolid.PLAY);
        btnIniciar.addActionListener(e -> solicitarNivelYJugar());

        JButton btnFrases = crearBoton("Frases de Vida", FontAwesomeSolid.HEART);
        btnFrases.addActionListener(e -> mainFrame.cambiarPantalla(MainFrame.PANTALLA_FRASES));

        JButton btnSalir = crearBoton("Salir", FontAwesomeSolid.TIMES);
        btnSalir.addActionListener(e -> System.exit(0));

        panelBotones.add(btnIniciar);
        panelBotones.add(btnFrases);
        panelBotones.add(btnSalir);
        return panelBotones;
    }

    private JButton crearBoton(String texto, FontAwesomeSolid icono) {
        JButton boton = new JButton(texto);
        boton.setIcon(crearIcono(icono));
        boton.setFont(new Font("SansSerif", Font.BOLD, 16));
        boton.setFocusPainted(false);
        boton.setBackground(new Color(228, 232, 255));
        boton.setForeground(new Color(64, 72, 116));
        boton.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        return boton;
    }


    private Icon crearIcono(FontAwesomeSolid icono) {
        return FontIcon.of(icono, 16, new Color(64, 72, 116));
    }

    private void solicitarNivelYJugar() {
        String[] opciones = {"2x2", "3x3", "4x4", "5x5", "6x6", "7x7"};
        String seleccion = (String) JOptionPane.showInputDialog(
                this,
                "Selecciona el nivel :)",
                "Niveles",
                JOptionPane.QUESTION_MESSAGE,
                null,
                opciones,
                opciones[0]
        );

        if (seleccion == null) {
            return;
        }

        int nivel = Integer.parseInt(seleccion.substring(0, 1));
        mainFrame.iniciarJuegoConNivel(nivel);
    }
}
