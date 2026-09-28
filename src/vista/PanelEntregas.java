package vista;

import dao.EntregaDAO;
import dao.PedidoDAO;
import dao.RepartidorDAO;
import modelo.Entrega;
import modelo.Pedido;
import modelo.Repartidor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Panel de gestión de entregas: asocia un Pedido y un Repartidor
 * (seleccionados desde combos cargados desde la BD) con fecha y hora.
 */
public class PanelEntregas extends JPanel {

    private static final String TODOS = "TODOS";
    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm");

    private final EntregaDAO entregaDAO = new EntregaDAO();
    private final PedidoDAO pedidoDAO = new PedidoDAO();
    private final RepartidorDAO repartidorDAO = new RepartidorDAO();

    // Combos del formulario: muestran "id - texto" y guardan el objeto con su id
    private final JComboBox<Pedido> cboPedido = new JComboBox<>();
    private final JComboBox<Repartidor> cboRepartidor = new JComboBox<>();
    private final JTextField txtFecha = new JTextField(10);
    private final JTextField txtHora = new JTextField(5);

    // Combos de filtro: "TODOS" + entidades
    private final JComboBox<Object> cboFiltroPedido = new JComboBox<>();
    private final JComboBox<Object> cboFiltroRepartidor = new JComboBox<>();

    private final DefaultTableModel modeloTabla;
    private final JTable tabla;

    // Id de la entrega seleccionada en la tabla (0 = ninguna)
    private int idSeleccionado = 0;

    public PanelEntregas() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ----- Formulario -----
        JPanel panelFormulario = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelFormulario.setBorder(BorderFactory.createTitledBorder("Datos de la entrega"));
        panelFormulario.add(new JLabel("Pedido:"));
        panelFormulario.add(cboPedido);
        panelFormulario.add(new JLabel("Repartidor:"));
        panelFormulario.add(cboRepartidor);
        panelFormulario.add(new JLabel("Fecha (AAAA-MM-DD):"));
        panelFormulario.add(txtFecha);
        panelFormulario.add(new JLabel("Hora (HH:MM):"));
        panelFormulario.add(txtHora);

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
        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelFiltros.setBorder(BorderFactory.createTitledBorder("Filtros"));
        JButton btnFiltrar = new JButton("Filtrar");
        panelFiltros.add(new JLabel("Por pedido:"));
        panelFiltros.add(cboFiltroPedido);
        panelFiltros.add(new JLabel("Por repartidor:"));
        panelFiltros.add(cboFiltroRepartidor);
        panelFiltros.add(btnFiltrar);

        JPanel panelSuperior = new JPanel(new GridLayout(3, 1));
        panelSuperior.add(panelFormulario);
        panelSuperior.add(panelBotones);
        panelSuperior.add(panelFiltros);

        // ----- Tabla -----
        modeloTabla = new DefaultTableModel(
                new String[]{"ID", "ID Pedido", "Dirección", "ID Repartidor", "Repartidor", "Fecha", "Hora"}, 0) {
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

        recargarCombos();
        limpiar();
        cargarTabla();
    }

    /**
     * Recarga desde la BD los combos de pedidos y repartidores (formulario y filtros).
     * Se llama al abrir la pestaña para reflejar cambios hechos en las otras pestañas.
     */
    public void recargarCombos() {
        try {
            cboPedido.removeAllItems();
            cboFiltroPedido.removeAllItems();
            cboFiltroPedido.addItem(TODOS);
            for (Pedido p : pedidoDAO.readAll()) {
                cboPedido.addItem(p);
                cboFiltroPedido.addItem(p);
            }

            cboRepartidor.removeAllItems();
            cboFiltroRepartidor.removeAllItems();
            cboFiltroRepartidor.addItem(TODOS);
            for (Repartidor r : repartidorDAO.readAll()) {
                cboRepartidor.addItem(r);
                cboFiltroRepartidor.addItem(r);
            }
        } catch (SQLException e) {
            Mensajes.errorBD(this, "cargar pedidos y repartidores", e);
        }
    }

    /**
     * Valida el formulario y construye la entrega. Retorna null si algo no es válido.
     */
    private Entrega construirEntrega(int id) {
        Pedido pedido = (Pedido) cboPedido.getSelectedItem();
        Repartidor repartidor = (Repartidor) cboRepartidor.getSelectedItem();

        if (pedido == null) {
            Mensajes.advertencia(this, "Debes seleccionar un pedido. Si no hay, regístralo en la pestaña Pedidos.");
            return null;
        }
        if (repartidor == null) {
            Mensajes.advertencia(this, "Debes seleccionar un repartidor. Si no hay, regístralo en la pestaña Repartidores.");
            return null;
        }

        String textoFecha = txtFecha.getText().trim();
        String textoHora = txtHora.getText().trim();
        if (textoFecha.isEmpty() || textoHora.isEmpty()) {
            Mensajes.advertencia(this, "La fecha y la hora son obligatorias.");
            return null;
        }

        LocalDate fecha;
        LocalTime hora;
        try {
            fecha = LocalDate.parse(textoFecha);
        } catch (DateTimeParseException e) {
            Mensajes.advertencia(this, "Fecha inválida. Usa el formato AAAA-MM-DD (ej: 2026-10-05).");
            return null;
        }
        try {
            hora = LocalTime.parse(textoHora, FORMATO_HORA);
        } catch (DateTimeParseException e) {
            Mensajes.advertencia(this, "Hora inválida. Usa el formato HH:MM de 24 horas (ej: 15:40).");
            return null;
        }

        return new Entrega(id, pedido.getIdPedido(), repartidor.getId(), Date.valueOf(fecha), Time.valueOf(hora));
    }

    private void registrar() {
        Entrega entrega = construirEntrega(0);
        if (entrega == null) {
            return;
        }
        try {
            entregaDAO.create(entrega);
            Mensajes.exito(this, "Entrega registrada correctamente.");
            limpiar();
            cargarTabla();
        } catch (SQLException e) {
            Mensajes.errorBD(this, "registrar la entrega", e);
        }
    }

    private void editar() {
        if (idSeleccionado == 0) {
            Mensajes.advertencia(this, "Selecciona una entrega de la tabla para editar.");
            return;
        }
        Entrega entrega = construirEntrega(idSeleccionado);
        if (entrega == null) {
            return;
        }
        try {
            entregaDAO.update(entrega);
            Mensajes.exito(this, "Entrega actualizada correctamente.");
            limpiar();
            cargarTabla();
        } catch (SQLException e) {
            Mensajes.errorBD(this, "actualizar la entrega", e);
        }
    }

    private void eliminar() {
        if (idSeleccionado == 0) {
            Mensajes.advertencia(this, "Selecciona una entrega de la tabla para eliminar.");
            return;
        }
        if (!Mensajes.confirmar(this, "¿Eliminar la entrega con ID " + idSeleccionado + "?")) {
            return;
        }
        try {
            entregaDAO.delete(idSeleccionado);
            Mensajes.exito(this, "Entrega eliminada correctamente.");
            limpiar();
            cargarTabla();
        } catch (SQLException e) {
            Mensajes.errorBD(this, "eliminar la entrega", e);
        }
    }

    /**
     * Copia la fila seleccionada al formulario, seleccionando en los combos
     * el pedido y el repartidor por su id.
     */
    private void cargarSeleccion() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            return;
        }
        idSeleccionado = (int) modeloTabla.getValueAt(fila, 0);
        int idPedido = (int) modeloTabla.getValueAt(fila, 1);
        int idRepartidor = (int) modeloTabla.getValueAt(fila, 3);

        for (int i = 0; i < cboPedido.getItemCount(); i++) {
            if (cboPedido.getItemAt(i).getIdPedido() == idPedido) {
                cboPedido.setSelectedIndex(i);
                break;
            }
        }
        for (int i = 0; i < cboRepartidor.getItemCount(); i++) {
            if (cboRepartidor.getItemAt(i).getId() == idRepartidor) {
                cboRepartidor.setSelectedIndex(i);
                break;
            }
        }
        txtFecha.setText(modeloTabla.getValueAt(fila, 5).toString());
        txtHora.setText(modeloTabla.getValueAt(fila, 6).toString().substring(0, 5));
    }

    /**
     * Limpia el formulario y propone la fecha y hora actuales.
     */
    private void limpiar() {
        idSeleccionado = 0;
        if (cboPedido.getItemCount() > 0) {
            cboPedido.setSelectedIndex(0);
        }
        if (cboRepartidor.getItemCount() > 0) {
            cboRepartidor.setSelectedIndex(0);
        }
        txtFecha.setText(LocalDate.now().toString());
        txtHora.setText(LocalTime.now().format(FORMATO_HORA));
        tabla.clearSelection();
    }

    /**
     * Recarga la tabla aplicando los filtros por pedido y repartidor.
     */
    public void cargarTabla() {
        Object filtroPedido = cboFiltroPedido.getSelectedItem();
        Object filtroRepartidor = cboFiltroRepartidor.getSelectedItem();

        Integer idPedido = (filtroPedido instanceof Pedido) ? ((Pedido) filtroPedido).getIdPedido() : null;
        Integer idRepartidor = (filtroRepartidor instanceof Repartidor) ? ((Repartidor) filtroRepartidor).getId() : null;

        modeloTabla.setRowCount(0);
        try {
            for (Entrega e : entregaDAO.readByFiltro(idPedido, idRepartidor)) {
                modeloTabla.addRow(new Object[]{
                        e.getId(), e.getIdPedido(), e.getDireccionPedido(),
                        e.getIdRepartidor(), e.getNombreRepartidor(),
                        e.getFecha(), e.getHora()
                });
            }
        } catch (SQLException e) {
            Mensajes.errorBD(this, "cargar las entregas", e);
        }
    }
}