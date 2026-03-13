import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Font;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Random;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.event.HyperlinkEvent;

public class PanelFrases extends JPanel {

    private final String[] frases = {
        "Respira profundo: hoy también cuenta.",
        "Cada intento te acerca a tu mejor versión.",
        "Tus emociones importan, abrázalas con calma.",
        "Un día difícil no define toda tu historia.",
        "Tu bienestar es prioridad, paso a paso."
    };

    private final JLabel fraseLabel;
    private final Random random;
    private int indiceFraseActual = -1;

    public PanelFrases(MainFrame mainFrame) {
        random = new Random();
        setLayout(new BorderLayout(16, 16));
        setBackground(new Color(240, 250, 247));
        setBorder(BorderFactory.createEmptyBorder(50, 40, 50, 40));

        JLabel titulo = new JLabel("Espacio Zen", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 38));
        titulo.setForeground(new Color(48, 103, 100));
        add(titulo, BorderLayout.NORTH);

        fraseLabel = new JLabel("", SwingConstants.CENTER);
        fraseLabel.setFont(new Font("SansSerif", Font.PLAIN, 28));
        fraseLabel.setForeground(new Color(55, 82, 91));
        actualizarFrase();
        add(fraseLabel, BorderLayout.CENTER);

        JPanel botones = new JPanel();
        botones.setBackground(new Color(240, 250, 247));

        JButton btnNuevaFrase = new JButton("\uF005 Nueva frase");
        btnNuevaFrase.setFont(new Font("SansSerif", Font.BOLD, 16));
        btnNuevaFrase.addActionListener(e -> actualizarFrase());

        JButton btnAyuda = new JButton("\u260E Contactos de apoyo emocional");
        btnAyuda.setFont(new Font("SansSerif", Font.BOLD, 16));
        btnAyuda.setBackground(new Color(255, 230, 230));
        btnAyuda.addActionListener(e -> mostrarContactosApoyo());

        JButton btnVolver = new JButton("\u21A9 Volver al inicio");
        btnVolver.setFont(new Font("SansSerif", Font.BOLD, 16));
        btnVolver.addActionListener(e -> mainFrame.cambiarPantalla(MainFrame.PANTALLA_INICIO));

        botones.add(btnNuevaFrase);
        botones.add(btnAyuda);
        botones.add(btnVolver);
        add(botones, BorderLayout.SOUTH);
    }

    private void actualizarFrase() {
        if (frases.length == 0) {
            fraseLabel.setText("");
            return;
        }

        int nuevoIndice = random.nextInt(frases.length);
        if (frases.length > 1) {
            while (nuevoIndice == indiceFraseActual) {
                nuevoIndice = random.nextInt(frases.length);
            }
        }

        indiceFraseActual = nuevoIndice;
        fraseLabel.setText("\u201C" + frases[nuevoIndice] + "\u201D");
    }

    private void mostrarContactosApoyo() {
        String contenidoHtml = """
                <html>
                <body style='font-family:SansSerif; font-size:12px; padding:8px;'>
                <h2 style='color:#306764; margin-top:0;'>Contactos de apoyo en El Salvador</h2>
                <ul>
                  <li><b>MINSAL (Te Escucho)</b>: <b>126</b><br/>Línea nacional de salud mental. Brindan apoyo psicológico gratuito las 24 horas.</li>
                  <li><b>Teléfono de la Esperanza</b>: <b>2231-9595</b><br/>ONG especializada en prevención del suicidio y crisis emocionales.</li>
                  <li><b>ISSS (Te Escucho)</b>: <b>191</b> (opción 4)<br/>Específico para derechohabientes del Seguro Social.</li>
                  <li><b>Sistema de Emergencias Médicas</b>: <b>131</b><br/>Para traslados o crisis de salud urgentes.</li>
                  <li><b>PNC (Emergencias)</b>: <b>911</b><br/>Asistencia inmediata en situaciones de riesgo inminente.</li>
                </ul>
                <h3 style='color:#306764;'>Instituciones referentes</h3>
                <ul>
                  <li><b>⚑ UNAB - Sede San Miguel</b><br/>◉ Campus Central.<br/>
                      ☎ <a href='tel:+50326658700'>2665-8700</a> &nbsp;|&nbsp; ↪ <a href='https://www.unab.edu.sv/'>Sitio web UNAB</a></li>
                  <li><b>⚑ UNAB - Sede San Salvador</b><br/>◉ Atención académica y bienestar estudiantil.<br/>
                      ☎ <a href='tel:+50326658701'>2665-8701</a> &nbsp;|&nbsp; ↪ <a href='https://www.unab.edu.sv/'>Sitio web UNAB</a></li>
                  <li><b>⚑ UNAB - Sede Chalatenango</b><br/>◉ Atención a estudiantes en la zona norte.<br/>
                      ☎ <a href='tel:+50326658702'>2665-8702</a> &nbsp;|&nbsp; ↪ <a href='https://www.unab.edu.sv/'>Sitio web UNAB</a></li>
                  <li><b>⚑ UNAB - Sede Sonsonate</b><br/>◉ Atención académica y orientación estudiantil.<br/>
                      ☎ <a href='tel:+50326658703'>2665-8703</a> &nbsp;|&nbsp; ↪ <a href='https://www.unab.edu.sv/'>Sitio web UNAB</a></li>
                </ul>
                <p><i>Si hay riesgo inmediato para tu vida o la de alguien más, llama al 911.</i></p>
                </body>
                </html>
                """;

        JEditorPane panelInfo = new JEditorPane("text/html", contenidoHtml);
        panelInfo.setEditable(false);
        panelInfo.setOpaque(false);
        panelInfo.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
        panelInfo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        panelInfo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panelInfo.addHyperlinkListener(evento -> {
            if (evento.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                abrirEnlace(evento.getURL().toString());
            }
        });

        JScrollPane scrollPane = new JScrollPane(panelInfo);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setPreferredSize(new java.awt.Dimension(620, 430));

        JOptionPane.showMessageDialog(
                this,
                scrollPane,
                "Red de apoyo emocional",
                JOptionPane.INFORMATION_MESSAGE
        );
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
