
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class formulario extends javax.swing.JFrame {

    private JLabel[] etiquetasCartas;
    private int[] valoresCartas;
    private boolean[] cartasEncontradas;
    private int indicePrimeraCartaSeleccionada = -1;
    private int indiceSegundaCartaSeleccionada = -1;
    private boolean bloqueoClicks = false;

    private int nivelActual = 1;
    private int filasActuales;
    private int columnasActuales;

    private static final int VALOR_CARTA_EVIL = -1;

    // Cartas regulares disponibles para formar parejas
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

    public formulario() {
        initComponents();
        cargar_nivel(1);
    }

    private void cargar_nivel(int nivel) {
        nivelActual = nivel;
        if (nivel == 1) {
            inicializar_juego_parejas(2, 2);
        } else {
            inicializar_juego_parejas(4, 4);
        }
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
        getContentPane().setLayout(new GridLayout(filas, columnas, 10, 10));

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

        if (nivelActual == 1) {
            // Nivel 1: 2 parejas aleatorias
            List<Integer> imagenesDisponibles = new ArrayList<>();
            for (int i = 0; i < rutasImagenesBase.length; i++) {
                imagenesDisponibles.add(i);
            }
            Collections.shuffle(imagenesDisponibles);
            for (int i = 0; i < 2; i++) {
                int valor = imagenesDisponibles.get(i);
                valores.add(valor);
                valores.add(valor);
            }
        } else {
            // Nivel 2: 7 parejas + 2 cartas evil
            for (int i = 0; i < rutasImagenesBase.length; i++) {
                valores.add(i);
                valores.add(i);
            }
            valores.add(VALOR_CARTA_EVIL);
            valores.add(VALOR_CARTA_EVIL);
        }

        Collections.shuffle(valores);
        for (int i = 0; i < valores.size(); i++) {
            valoresCartas[i] = valores.get(i);
            if (valoresCartas[i] == VALOR_CARTA_EVIL) {
                cartasEncontradas[i] = true;
            }
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

    // Hace que todas las etiquetas de cartas tengan el mismo tamaño visual
    public void ajustar_tamano_etiquetas() {
        if (etiquetasCartas == null) {
            return;
        }

        int base = (nivelActual == 1) ? 180 : 120;
        java.awt.Dimension tamano = new java.awt.Dimension(base, base);

        for (JLabel etiqueta : etiquetasCartas) {
            etiqueta.setPreferredSize(tamano);
            etiqueta.setMinimumSize(tamano);
            etiqueta.setMaximumSize(tamano);
        }

        pack();
        setLocationRelativeTo(null);
    }

    // Configura colores y aspecto general del formulario para que se vea más profesional
    public void configurar_apariencia_juego() {
        getContentPane().setBackground(new Color(30, 30, 60)); // fondo oscuro elegante
    }

    private void manejar_clic_en_carta(int indiceCarta) {
        if (bloqueoClicks || cartasEncontradas[indiceCarta]) {
            return;
        }

        mostrar_carta(indiceCarta);

        if (valoresCartas[indiceCarta] == VALOR_CARTA_EVIL) {
            JOptionPane.showMessageDialog(this, "¡Susto! Has perdido");
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
            int ancho = etiquetasCartas[indiceCarta].getWidth();
            int alto = etiquetasCartas[indiceCarta].getHeight();
            ImageIcon iconoEscalado = escalar_imagen(recurso, ancho, alto);
            etiquetasCartas[indiceCarta].setIcon(iconoEscalado);
            etiquetasCartas[indiceCarta].setText("");
        } else {
            etiquetasCartas[indiceCarta].setIcon(null);
            etiquetasCartas[indiceCarta].setText("X");
        }

        etiquetasCartas[indiceCarta].setBackground(Color.WHITE);
    }

    private void ocultar_carta(int indiceCarta) {
        if (!cartasEncontradas[indiceCarta]) {
            configurar_carta_oculta(etiquetasCartas[indiceCarta]);
        }
    }

    private void verificar_pareja() {
        if (valoresCartas[indicePrimeraCartaSeleccionada] == valoresCartas[indiceSegundaCartaSeleccionada]) {
            cartasEncontradas[indicePrimeraCartaSeleccionada] = true;
            cartasEncontradas[indiceSegundaCartaSeleccionada] = true;
            etiquetasCartas[indicePrimeraCartaSeleccionada].setBackground(Color.YELLOW);
            etiquetasCartas[indiceSegundaCartaSeleccionada].setBackground(Color.YELLOW);

            indicePrimeraCartaSeleccionada = -1;
            indiceSegundaCartaSeleccionada = -1;

            if (todas_las_parejas_encontradas()) {
                if (nivelActual == 1) {
                    JOptionPane.showMessageDialog(this, "¡Felicidades! Completaste el Nivel 1");
                    cargar_nivel(2);
                } else {
                    JOptionPane.showMessageDialog(this, "¡Excelente! Completaste todos los niveles");
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

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        label1 = new javax.swing.JLabel();
        label2 = new javax.swing.JLabel();
        label3 = new javax.swing.JLabel();
        label4 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        label1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        label1.addAncestorListener(new javax.swing.event.AncestorListener() {
            public void ancestorAdded(javax.swing.event.AncestorEvent evt) {
                label1AncestorAdded(evt);
            }
            public void ancestorMoved(javax.swing.event.AncestorEvent evt) {
            }
            public void ancestorRemoved(javax.swing.event.AncestorEvent evt) {
            }
        });

        label3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                label3MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(47, 47, 47)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(label4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(label2, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(478, 478, 478))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(45, 45, 45)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label1, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(label3, javax.swing.GroupLayout.DEFAULT_SIZE, 134, Short.MAX_VALUE)
                    .addComponent(label4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(298, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void label1AncestorAdded(javax.swing.event.AncestorEvent evt) {//GEN-FIRST:event_label1AncestorAdded
    }//GEN-LAST:event_label1AncestorAdded

    private void label3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label3MouseClicked
    }//GEN-LAST:event_label3MouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(formulario.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(formulario.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(formulario.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(formulario.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new formulario().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel label1;
    private javax.swing.JLabel label2;
    private javax.swing.JLabel label3;
    private javax.swing.JLabel label4;
    // End of variables declaration//GEN-END:variables
}
