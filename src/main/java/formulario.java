
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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

    // Cada valor de carta apunta a una de estas imágenes dentro de src/main/resources/Imagenes
    // Asegúrate de que los archivos existan como f1.png, f2.png, etc.
    private String[] rutasImagenesBase = {
        "/Imagenes/sonrisa-clasica.png",
        "/Imagenes/riendo-fuerte.png"
    };

    public formulario() {
        initComponents();
       // configurar_apariencia_juego();
        inicializar_juego_parejas();
    getContentPane().setLayout(new GridLayout(2, 2, 10, 10));
    }

    private void inicializar_juego_parejas() {
        etiquetasCartas = new JLabel[]{label1, label2, label3, label4};

        ajustar_tamano_etiquetas();

        int numeroCartas = etiquetasCartas.length;
        int numeroParejas = numeroCartas / 2;

        valoresCartas = new int[numeroCartas];
        cartasEncontradas = new boolean[numeroCartas];

        int indice = 0;
        for (int valor = 1; valor <= numeroParejas; valor++) {
            valoresCartas[indice++] = valor;
            valoresCartas[indice++] = valor;
        }
        barajar_valores();
        for (int i = 0; i < numeroCartas; i++) {
            final int indiceCarta = i;
            configurar_carta_oculta(etiquetasCartas[i]);
            
            etiquetasCartas[i].addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    manejar_clic_en_carta(indiceCarta);
                }
            });
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

    Image imagenEscalada = iconoOriginal.getImage()
            .getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);

    return new ImageIcon(imagenEscalada);
}
    
    // Hace que todas las etiquetas de cartas tengan el mismo tamaño visual
    public void ajustar_tamano_etiquetas() {
        if (etiquetasCartas == null) {
            return;
        }
        java.awt.Dimension tamano = new java.awt.Dimension(200, 200);
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

    private void barajar_valores() {
        java.util.Random aleatorio = new java.util.Random();
        for (int i = valoresCartas.length - 1; i > 0; i--) {
            int j = aleatorio.nextInt(i + 1);
            int temp = valoresCartas[i];
            valoresCartas[i] = valoresCartas[j];
            valoresCartas[j] = temp;
        }
    }

    private void manejar_clic_en_carta(int indiceCarta) {
        if (bloqueoClicks) {
            return;
        }
        if (cartasEncontradas[indiceCarta]) {
            return;
        }

        if (indicePrimeraCartaSeleccionada == -1) {
            indicePrimeraCartaSeleccionada = indiceCarta;
            mostrar_carta(indiceCarta);
        } else if (indiceSegundaCartaSeleccionada == -1 && indiceCarta != indicePrimeraCartaSeleccionada) {
            indiceSegundaCartaSeleccionada = indiceCarta;
            mostrar_carta(indiceCarta);
            verificar_pareja();
        }
    }

private void mostrar_carta(int indiceCarta) {

    int indiceImagen = valoresCartas[indiceCarta] - 1;

    if (indiceImagen >= 0 && indiceImagen < rutasImagenesBase.length) {

        java.net.URL recurso = getClass().getResource(rutasImagenesBase[indiceImagen]);

        if (recurso != null) {

            int ancho = etiquetasCartas[indiceCarta].getWidth();
            int alto = etiquetasCartas[indiceCarta].getHeight();

            ImageIcon iconoEscalado = escalar_imagen(recurso, ancho, alto);

            etiquetasCartas[indiceCarta].setIcon(iconoEscalado);
            etiquetasCartas[indiceCarta].setText("");

        } else {
            etiquetasCartas[indiceCarta].setIcon(null);
            etiquetasCartas[indiceCarta].setText(String.valueOf(valoresCartas[indiceCarta]));
        }

    } else {
        etiquetasCartas[indiceCarta].setIcon(null);
        etiquetasCartas[indiceCarta].setText(String.valueOf(valoresCartas[indiceCarta]));
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
                JOptionPane.showMessageDialog(this, "¡Has encontrado todas las parejas!");
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
        for (boolean encontrada : cartasEncontradas) {
            if (!encontrada) {
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
