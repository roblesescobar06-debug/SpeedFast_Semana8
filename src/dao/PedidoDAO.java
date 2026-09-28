package dao;

import modelo.Pedido;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO de la tabla pedidos.
 * Implementa el CRUD completo y un listado con filtros por estado y tipo.
 */
public class PedidoDAO {

    /**
     * Inserta un pedido y le asigna el id generado por MySQL.
     */
    public boolean create(Pedido pedido) throws SQLException {
        String sql = "INSERT INTO pedidos (direccion, tipo, estado) VALUES (?, ?, ?)";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, pedido.getDireccionEntrega());
            ps.setString(2, pedido.getTipo());
            ps.setString(3, pedido.getEstado());
            int filas = ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    pedido.setIdPedido(rs.getInt(1));
                }
            }
            return filas > 0;
        }
    }

    /**
     * Retorna todos los pedidos.
     */
    public List<Pedido> readAll() throws SQLException {
        return readByFiltro(null, null);
    }

    /**
     * Retorna los pedidos filtrados por estado y/o tipo.
     * Si un filtro es null, no se aplica.
     */
    public List<Pedido> readByFiltro(String estado, String tipo) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT id, direccion, tipo, estado FROM pedidos WHERE 1=1");
        if (estado != null) {
            sql.append(" AND estado = ?");
        }
        if (tipo != null) {
            sql.append(" AND tipo = ?");
        }
        sql.append(" ORDER BY id");

        List<Pedido> lista = new ArrayList<>();

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {

            int indice = 1;
            if (estado != null) {
                ps.setString(indice++, estado);
            }
            if (tipo != null) {
                ps.setString(indice, tipo);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        }
        return lista;
    }

    /**
     * Actualiza dirección, tipo y estado de un pedido existente.
     */
    public boolean update(Pedido pedido) throws SQLException {
        String sql = "UPDATE pedidos SET direccion = ?, tipo = ?, estado = ? WHERE id = ?";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, pedido.getDireccionEntrega());
            ps.setString(2, pedido.getTipo());
            ps.setString(3, pedido.getEstado());
            ps.setInt(4, pedido.getIdPedido());
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Elimina un pedido por su id.
     * Si tiene entregas asociadas, MySQL lanza una excepción de integridad.
     */
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM pedidos WHERE id = ?";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Convierte la fila actual del ResultSet en la subclase de Pedido correcta.
     */
    private Pedido mapear(ResultSet rs) throws SQLException {
        Pedido pedido = Pedido.crear(rs.getString("tipo"), rs.getInt("id"), rs.getString("direccion"));
        pedido.setEstado(rs.getString("estado"));
        return pedido;
    }
}