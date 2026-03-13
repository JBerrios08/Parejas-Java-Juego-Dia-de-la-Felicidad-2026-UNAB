import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.util.Random;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

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

        JButton btnAyuda = new JButton("\u260E Línea de Ayuda (131)");
        btnAyuda.setFont(new Font("SansSerif", Font.BOLD, 16));
        btnAyuda.setBackground(new Color(255, 230, 230));
        btnAyuda.addActionListener(e -> JOptionPane.showMessageDialog(
                this,
                "No estás solo/a. Si necesitas apoyo inmediato, llama al 131.",
                "Línea de Ayuda",
                JOptionPane.INFORMATION_MESSAGE
        ));

        JButton btnVolver = new JButton("\u21A9 Volver al inicio");
        btnVolver.setFont(new Font("SansSerif", Font.BOLD, 16));
        btnVolver.addActionListener(e -> mainFrame.cambiarPantalla(MainFrame.PANTALLA_INICIO));

        botones.add(btnNuevaFrase);
        botones.add(btnAyuda);
        botones.add(btnVolver);
        add(botones, BorderLayout.SOUTH);
    }

    private void actualizarFrase() {
        fraseLabel.setText("\u201C" + frases[random.nextInt(frases.length)] + "\u201D");
    }
}
