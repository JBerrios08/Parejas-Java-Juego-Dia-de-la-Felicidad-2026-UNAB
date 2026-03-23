import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.Image;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
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

public class PanelAcerca extends JPanel {

    public PanelAcerca(MainFrame mainFrame) {
        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(250, 247, 255));
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JLabel titulo = new JLabel("Acerca de Jaime", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 36));
        titulo.setForeground(new Color(73, 64, 118));
        add(titulo, BorderLayout.NORTH);

        JPanel contenido = new JPanel();
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.X_AXIS));
        contenido.setOpaque(false);

        JLabel fotoLabel = new JLabel();
        fotoLabel.setAlignmentY(Component.TOP_ALIGNMENT);
        fotoLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(206, 195, 235), 2),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));

        java.net.URL recursoFoto = getClass().getResource("/Imagenes/JaimeBerrios.jpg");
        if (recursoFoto != null) {
            ImageIcon foto = new ImageIcon(recursoFoto);
            Image fotoEscalada = foto.getImage().getScaledInstance(260, 320, Image.SCALE_SMOOTH);
            fotoLabel.setIcon(new ImageIcon(fotoEscalada));
        } else {
            fotoLabel.setText("Foto no disponible");
            fotoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        }

        JPanel textoPanel = new JPanel();
        textoPanel.setLayout(new BoxLayout(textoPanel, BoxLayout.Y_AXIS));
        textoPanel.setOpaque(false);
        textoPanel.setAlignmentY(Component.TOP_ALIGNMENT);

        textoPanel.add(crearLinea("Nombre", "Jaime Fernando Berrios Ortiz"));
        textoPanel.add(crearLinea("Ubicación", "San Miguel, El Salvador"));
        textoPanel.add(crearLinea("Perfil", "Estudiante de Ingeniería en Sistemas y Computación con perfil multidisciplinario en desarrollo web, marketing digital e innovación."));
        textoPanel.add(crearLinea("Educación", "Universidad Dr. Andrés Bello (UNAB), Facultad de Tecnología e Innovación."));
        textoPanel.add(crearLinea("Nivel actual", "Tercer año de Ingeniería en Sistemas y Computación."));
        textoPanel.add(crearLinea("Emprendimiento", "Fundador de JaiFer Corp. (Branding, desarrollo web, publicidad y estructuras metálicas)."));
        textoPanel.add(crearLinea("Roles", "Trafficker Digital (Meta Ads y Google Ads), Desarrollador de Web Apps y APIs, Fundador."));
        textoPanel.add(crearLinea("Proyectos", "Torogoz Académico, Coffee Run 2026 y proyectos de automatización e impresión 3D."));
        textoPanel.add(crearLinea("Stack técnico", "HTML5, CSS3, JavaScript, PHP, React, Bootstrap, TailwindCSS, MySQL, Git, GitHub, Docker, Arduino."));
        textoPanel.add(crearLinea("Intereses", "Marvel (Loki), Batman, viajes en el tiempo, física aplicada, packaging creativo y cultura salvadoreña."));

        JLabel linkedin = crearEnlace("LinkedIn: linkedin.com/in/jberrios08", "https://www.linkedin.com/in/jberrios08");
        JLabel github = crearEnlace("GitHub: github.com/JBerrios08", "https://github.com/JBerrios08");
        textoPanel.add(linkedin);
        textoPanel.add(Box.createVerticalStrut(4));
        textoPanel.add(github);

        contenido.add(fotoLabel);
        contenido.add(Box.createHorizontalStrut(20));
        contenido.add(textoPanel);

        add(contenido, BorderLayout.CENTER);

        JButton btnVolver = new JButton("Volver al inicio");
        btnVolver.setIcon(crearIcono(FontAwesomeSolid.ARROW_LEFT));
        btnVolver.setFont(new Font("SansSerif", Font.BOLD, 16));
        btnVolver.setBackground(new Color(228, 232, 255));
        btnVolver.setForeground(new Color(64, 72, 116));
        btnVolver.setFocusPainted(false);
        btnVolver.addActionListener(e -> mainFrame.cambiarPantalla(MainFrame.PANTALLA_INICIO));

        JPanel pie = new JPanel();
        pie.setOpaque(false);
        pie.add(btnVolver);
        add(pie, BorderLayout.SOUTH);
    }

    private JLabel crearLinea(String titulo, String texto) {
        JLabel label = new JLabel("<html><b>" + titulo + ":</b> " + texto + "</html>");
        label.setFont(new Font("SansSerif", Font.PLAIN, 15));
        label.setForeground(new Color(52, 48, 74));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        return label;
    }

    private JLabel crearEnlace(String texto, String url) {
        JLabel link = new JLabel("<html><a href=''>" + texto + "</a></html>");
        link.setCursor(new Cursor(Cursor.HAND_CURSOR));
        link.setFont(new Font("SansSerif", Font.BOLD, 15));
        link.setAlignmentX(Component.LEFT_ALIGNMENT);
        link.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                abrirEnlace(url);
            }
        });
        return link;
    }

    private Icon crearIcono(FontAwesomeSolid icono) {
        return FontIcon.of(icono, 16, new Color(64, 72, 116));
    }

    private void abrirEnlace(String url) {
        if (!Desktop.isDesktopSupported()) {
            return;
        }
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (IOException | URISyntaxException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "No fue posible abrir el enlace: " + url,
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }
}
