package vista;

import dao.PedidoDAO;
import modelo.Pedido;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Panel de gestión de pedidos: registrar, editar, eliminar y listar
 * con filtros opcionales por estado y tipo.
 */
public class PanelPedidos extends JPanel {

    private static final String[] TIPOS = {"COMIDA", "ENCOMIENDA", "EXPRESS"};
    private static final String[] ESTADOS = {"PENDIENTE", "EN_REPARTO", "ENTREGADO"};
    private static final String TODOS = "TODOS";

    private final PedidoDAO pedidoDAO = new PedidoDAO();

    private final JTextField txtDireccion = new JTextField(25);
    private final JComboBox<String> cboTipo = new JComboBox<>(TIPOS);
    private final JComboBox<String> cboEstado = new JComboBox<>(ESTADOS);

    private final JComboBox<String> cboFiltroEstado = new JComboBox<>();
    private final JComboBox<String> cboFiltroTipo = new JComboBox<>();

    private final DefaultTableModel modeloTabla;
    private final JTable tabla;

    // Id del pedido seleccionado en la tabla (0 = ninguno)
    private int idSeleccionado = 0;

    public PanelPedidos() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ----- Formulario -----
        JPanel panelFormulario = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelFormulario.setBorder(BorderFactory.createTitledBorder("Datos del pedido"));
        panelFormulario.add(new JLabel("Dirección:"));
        panelFormulario.add(txtDireccion);
        panelFormulario.add(new JLabel("Tipo:"));
        panelFormulario.add(cboTipo);
        panelFormulario.add(new JLabel("Estado:"));
        panelFormulario.add(cboEstado);

        // ----- Botones CRUD -----
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JButton btnRegistrar = new JButton("Registrar");
        JButton btnEditar = new JButton("Editar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnLimpiar = new JButton("Limpiar");
        panelBotones.add(btnRegistrar);
        panelBotones.add(btnEditar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnLimpiar);

        // ----- Filtros -----
        cboFiltroEstado.addItem(TODOS);
        for (String estado : ESTADOS) {
            cboFiltroEstado.addItem(estado);
        }
        cboFiltroTipo.addItem(TODOS);
        for (String tipo : TIPOS) {
            cboFiltroTipo.addItem(tipo);
        }
        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelFiltros.setBorder(BorderFactory.createTitledBorder("Filtros"));
        JButton btnFiltrar = new JButton("Filtrar");
        panelFiltros.add(new JLabel("Estado:"));
        panelFiltros.add(cboFiltroEstado);
        panelFiltros.add(new JLabel("Tipo:"));
        panelFiltros.add(cboFiltroTipo);
        panelFiltros.add(btnFiltrar);

        JPanel panelSuperior = new JPanel(new GridLayout(3, 1));
        panelSuperior.add(panelFormulario);
        panelSuperior.add(panelBotones);
        panelSuperior.add(panelFiltros);

        // ----- Tabla -----
        modeloTabla = new DefaultTableModel(new String[]{"ID", "Dirección", "Tipo", "Estado"}, 0) {
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
        btnFiltrar.addActionListener(e -> cargarTabla());
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cargarSeleccion();
            }
        });

        cargarTabla();
    }

    /**
     * Valida la dirección ingresada. Retorna null si no es válida.
     */
    private String validarDireccion() {
        String direccion = txtDireccion.getText().trim();
        if (direccion.isEmpty()) {
            Mensajes.advertencia(this, "La dirección es obligatoria.");
            return null;
        }
        if (direccion.length() < 5 || direccion.length() > 100) {
            Mensajes.advertencia(this, "La dirección debe tener entre 5 y 100 caracteres.");
            return null;
        }
        if (!direccion.matches(".*\\d.*")) {
            Mensajes.advertencia(this, "La dirección debe incluir la numeración (ej: Los Aromos 6559).");
            return null;
        }
        return direccion;
    }

    /**
     * Construye el pedido (subclase según el tipo) con los datos del formulario.
     */
    private Pedido construirPedido(int id, String direccion) {
        Pedido pedido = Pedido.crear((String) cboTipo.getSelectedItem(), id, direccion);
        pedido.setEstado((String) cboEstado.getSelectedItem());
        return pedido;
    }

    private void registrar() {
        String direccion = validarDireccion();
        if (direccion == null) {
            return;
        }
        try {
            Pedido pedido = construirPedido(0, direccion);
            pedidoDAO.create(pedido);
            Mensajes.exito(this, "Pedido #" + pedido.getIdPedido() + " registrado correctamente.");
            limpiar();
            cargarTabla();
        } catch (SQLException e) {
            Mensajes.errorBD(this, "registrar el pedido", e);
        }
    }

    private void editar() {
        if (idSeleccionado == 0) {
            Mensajes.advertencia(this, "Selecciona un pedido de la tabla para editar.");
            return;
        }
        String direccion = validarDireccion();
        if (direccion == null) {
            return;
        }
        try {
            pedidoDAO.update(construirPedido(idSeleccionado, direccion));
            Mensajes.exito(this, "Pedido actualizado correctamente.");
            limpiar();
            cargarTabla();
        } catch (SQLException e) {
            Mensajes.errorBD(this, "actualizar el pedido", e);
        }
    }

    private void eliminar() {
        if (idSeleccionado == 0) {
            Mensajes.advertencia(this, "Selecciona un pedido de la tabla para eliminar.");
            return;
        }
        if (!Mensajes.confirmar(this, "¿Eliminar el pedido con ID " + idSeleccionado + "?")) {
            return;
        }
        try {
            pedidoDAO.delete(idSeleccionado);
            Mensajes.exito(this, "Pedido eliminado correctamente.");
            limpiar();
            cargarTabla();
        } catch (SQLException e) {
            Mensajes.errorBD(this, "eliminar el pedido", e);
        }
    }

    /**
     * Copia la fila seleccionada de la tabla al formulario.
     */
    private void cargarSeleccion() {
        int fila = tabla.getSelectedRow();
        if (fila >= 0) {
            idSeleccionado = (int) modeloTabla.getValueAt(fila, 0);
            txtDireccion.setText((String) modeloTabla.getValueAt(fila, 1));
            cboTipo.setSelectedItem(modeloTabla.getValueAt(fila, 2));
            cboEstado.setSelectedItem(modeloTabla.getValueAt(fila, 3));
        }
    }

    private void limpiar() {
        idSeleccionado = 0;
        txtDireccion.setText("");
        cboTipo.setSelectedIndex(0);
        cboEstado.setSelectedIndex(0);
        tabla.clearSelection();
    }

    /**
     * Recarga la tabla aplicando los filtros seleccionados.
     */
    public void cargarTabla() {
        String estado = (String) cboFiltroEstado.getSelectedItem();
        String tipo = (String) cboFiltroTipo.getSelectedItem();

        modeloTabla.setRowCount(0);
        try {
            List<Pedido> pedidos = pedidoDAO.readByFiltro(
                    TODOS.equals(estado) ? null : estado,
                    TODOS.equals(tipo) ? null : tipo);
            for (Pedido p : pedidos) {
                modeloTabla.addRow(new Object[]{p.getIdPedido(), p.getDireccionEntrega(), p.getTipo(), p.getEstado()});
            }
        } catch (SQLException e) {
            Mensajes.errorBD(this, "cargar los pedidos", e);
        }
    }
}