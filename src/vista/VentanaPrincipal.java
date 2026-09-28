package vista;

import javax.swing.*;
import java.awt.*;

/**
 * Ventana principal del sistema SpeedFast.
 * Organiza la gestión de Repartidores, Pedidos y Entregas en pestañas.
 */
public class VentanaPrincipal extends JFrame {

    private final PanelRepartidores panelRepartidores = new PanelRepartidores();
    private final PanelPedidos panelPedidos = new PanelPedidos();
    private final PanelEntregas panelEntregas = new PanelEntregas();

    public VentanaPrincipal() {
        setTitle("SpeedFast - Gestión de pedidos y entregas");
        setSize(1000, 620);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLayout(new BorderLayout());

        // ----- Encabezado -----
        JLabel lblTitulo = new JLabel("SpeedFast", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 24));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));

        // ----- Pestañas -----
        JTabbedPane pestanas = new JTabbedPane();
        pestanas.addTab("Repartidores", panelRepartidores);
        pestanas.addTab("Pedidos", panelPedidos);
        pestanas.addTab("Entregas", panelEntregas);

        // Al cambiar de pestaña se refrescan tablas y combos con los datos actuales de la BD
        pestanas.addChangeListener(e -> {
            Component actual = pestanas.getSelectedComponent();
            if (actual == panelEntregas) {
                panelEntregas.recargarCombos();
                panelEntregas.cargarTabla();
            } else if (actual == panelPedidos) {
                panelPedidos.cargarTabla();
            } else if (actual == panelRepartidores) {
                panelRepartidores.cargarTabla();
            }
        });

        // ----- Pie -----
        JLabel lblPie = new JLabel("Desarrollo Orientado a Objetos II - Semana 8", SwingConstants.CENTER);
        lblPie.setFont(new Font("SansSerif", Font.ITALIC, 11));
        lblPie.setBorder(BorderFactory.createEmptyBorder(5, 10, 8, 10));

        add(lblTitulo, BorderLayout.NORTH);
        add(pestanas, BorderLayout.CENTER);
        add(lblPie, BorderLayout.SOUTH);

        // Confirmación al cerrar
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                if (Mensajes.confirmar(VentanaPrincipal.this, "¿Deseas salir del sistema?")) {
                    System.exit(0);
                }
            }
        });

        setVisible(true);
    }
}