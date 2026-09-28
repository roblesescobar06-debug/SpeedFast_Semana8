package vista;

import dao.RepartidorDAO;
import modelo.Repartidor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;

/**
 * Panel de gestión de repartidores: registrar, editar, eliminar y listar.
 */
public class PanelRepartidores extends JPanel {

    private final RepartidorDAO repartidorDAO = new RepartidorDAO();

    private final JTextField txtNombre = new JTextField(20);
    private final DefaultTableModel modeloTabla;
    private final JTable tabla;

    // Id del repartidor seleccionado en la tabla (0 = ninguno)
    private int idSeleccionado = 0;

    public PanelRepartidores() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ----- Formulario -----
        JPanel panelFormulario = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelFormulario.setBorder(BorderFactory.createTitledBorder("Datos del repartidor"));
        panelFormulario.add(new JLabel("Nombre:"));
        panelFormulario.add(txtNombre);

        // ----- Botones -----
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JButton btnRegistrar = new JButton("Registrar");
        JButton btnEditar = new JButton("Editar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnLimpiar = new JButton("Limpiar");
        panelBotones.add(btnRegistrar);
        panelBotones.add(btnEditar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnLimpiar);

        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.add(panelFormulario, BorderLayout.CENTER);
        panelSuperior.add(panelBotones, BorderLayout.SOUTH);

        // ----- Tabla -----
        modeloTabla = new DefaultTableModel(new String[]{"ID", "Nombre"}, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        tabla = new JTable(modeloTabla);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        add(panelSuperior, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        // ----- Eventos -----
        btnRegistrar.addActionListener(e -> registrar());
        btnEditar.addActionListener(e -> editar());
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar.addActionListener(e -> limpiar());
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cargarSeleccion();
            }
        });

        cargarTabla();
    }

    /**
     * Valida el nombre ingresado. Retorna null si no es válido.
     */
    private String validarNombre() {
        String nombre = txtNombre.getText().trim();
        if (nombre.isEmpty()) {
            Mensajes.advertencia(this, "El nombre es obligatorio.");
            return null;
        }
        if (nombre.length() < 3 || nombre.length() > 100) {
            Mensajes.advertencia(this, "El nombre debe tener entre 3 y 100 caracteres.");
            return null;
        }
        if (!nombre.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+")) {
            Mensajes.advertencia(this, "El nombre solo puede contener letras y espacios.");
            return null;
        }
        return nombre;
    }

    private void registrar() {
        String nombre = validarNombre();
        if (nombre == null) {
            return;
        }
        try {
            repartidorDAO.create(new Repartidor(nombre));
            Mensajes.exito(this, "Repartidor registrado correctamente.");
            limpiar();
            cargarTabla();
        } catch (SQLException e) {
            Mensajes.errorBD(this, "registrar el repartidor", e);
        }
    }

    private void editar() {
        if (idSeleccionado == 0) {
            Mensajes.advertencia(this, "Selecciona un repartidor de la tabla para editar.");
            return;
        }
        String nombre = validarNombre();
        if (nombre == null) {
            return;
        }
        try {
            repartidorDAO.update(new Repartidor(idSeleccionado, nombre));
            Mensajes.exito(this, "Repartidor actualizado correctamente.");
            limpiar();
            cargarTabla();
        } catch (SQLException e) {
            Mensajes.errorBD(this, "actualizar el repartidor", e);
        }
    }

    private void eliminar() {
        if (idSeleccionado == 0) {
            Mensajes.advertencia(this, "Selecciona un repartidor de la tabla para eliminar.");
            return;
        }
        if (!Mensajes.confirmar(this, "¿Eliminar el repartidor con ID " + idSeleccionado + "?")) {
            return;
        }
        try {
            repartidorDAO.delete(idSeleccionado);
            Mensajes.exito(this, "Repartidor eliminado correctamente.");
            limpiar();
            cargarTabla();
        } catch (SQLException e) {
            Mensajes.errorBD(this, "eliminar el repartidor", e);
        }
    }

    /**
     * Copia la fila seleccionada de la tabla al formulario.
     */
    private void cargarSeleccion() {
        int fila = tabla.getSelectedRow();
        if (fila >= 0) {
            idSeleccionado = (int) modeloTabla.getValueAt(fila, 0);
            txtNombre.setText((String) modeloTabla.getValueAt(fila, 1));
        }
    }

    private void limpiar() {
        idSeleccionado = 0;
        txtNombre.setText("");
        tabla.clearSelection();
    }

    /**
     * Recarga la tabla con los datos actuales de la base de datos.
     */
    public void cargarTabla() {
        modeloTabla.setRowCount(0);
        try {
            for (Repartidor r : repartidorDAO.readAll()) {
                modeloTabla.addRow(new Object[]{r.getId(), r.getNombre()});
            }
        } catch (SQLException e) {
            Mensajes.errorBD(this, "cargar los repartidores", e);
        }
    }
}