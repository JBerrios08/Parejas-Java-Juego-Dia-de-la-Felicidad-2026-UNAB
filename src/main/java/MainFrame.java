import java.awt.CardLayout;
import java.awt.Color;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class MainFrame extends JFrame {

    public static final String PANTALLA_INICIO = "inicio";
    public static final String PANTALLA_JUEGO = "juego";
    public static final String PANTALLA_FRASES = "frases";
    public static final String PANTALLA_ACERCA = "acerca";

    private final CardLayout cardLayout;
    private final PanelInicio panelInicio;
    private final PanelJuego panelJuego;
    private final PanelFrases panelFrases;
    private final PanelAcerca panelAcerca;

    public MainFrame() {
        setTitle("Mente en Calma");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(960, 720);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(246, 244, 255));

        cardLayout = new CardLayout();
        setLayout(cardLayout);

        panelInicio = new PanelInicio(this);
        panelJuego = new PanelJuego(this);
        panelFrases = new PanelFrases(this);
        panelAcerca = new PanelAcerca(this);

        add(panelInicio, PANTALLA_INICIO);
        add(panelJuego, PANTALLA_JUEGO);
        add(panelFrases, PANTALLA_FRASES);
        add(panelAcerca, PANTALLA_ACERCA);

        configurarMenu();
        cambiarPantalla(PANTALLA_INICIO);
    }

    private void configurarMenu() {
        JMenuBar menuBar = new JMenuBar();

        JMenu menuNiveles = new JMenu("Niveles");
        for (int nivel = 2; nivel <= 7; nivel++) {
            final int nivelSeleccionado = nivel;
            JMenuItem item = new JMenuItem(nivel + "x" + nivel);
            item.addActionListener(e -> iniciarJuegoConNivel(nivelSeleccionado));
            menuNiveles.add(item);
        }

        JMenu menuNavegacion = new JMenu("Navegación");
        JMenuItem itemInicio = new JMenuItem("Inicio");
        itemInicio.addActionListener(e -> cambiarPantalla(PANTALLA_INICIO));
        JMenuItem itemFrases = new JMenuItem("Frases de Vida");
        itemFrases.addActionListener(e -> cambiarPantalla(PANTALLA_FRASES));
        JMenuItem itemAcerca = new JMenuItem("Acerca de");
        itemAcerca.addActionListener(e -> cambiarPantalla(PANTALLA_ACERCA));
        JMenuItem itemSalir = new JMenuItem("Salir");
        itemSalir.addActionListener(e -> System.exit(0));

        menuNavegacion.add(itemInicio);
        menuNavegacion.add(itemFrases);
        menuNavegacion.add(itemAcerca);
        menuNavegacion.add(itemSalir);

        menuBar.add(menuNiveles);
        menuBar.add(menuNavegacion);
        setJMenuBar(menuBar);
    }

    public void iniciarJuegoConNivel(int nivel) {
        panelJuego.cargarNivel(nivel);
        cambiarPantalla(PANTALLA_JUEGO);
    }

    public void cambiarPantalla(String nombre) {
        cardLayout.show(getContentPane(), nombre);
    }
}
