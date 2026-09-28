package dao;

import modelo.Entrega;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO de la tabla entregas.
 * Implementa el CRUD completo y listados filtrados por pedido o repartidor.
 */
public class EntregaDAO {

    private static final String SELECT_BASE =
            "SELECT e.id, e.id_pedido, e.id_repartidor, e.fecha, e.hora, "
                    + "p.direccion, r.nombre "
                    + "FROM entregas e "
                    + "JOIN pedidos p ON e.id_pedido = p.id "
                    + "JOIN repartidores r ON e.id_repartidor = r.id";

    /**
     * Inserta una entrega asociando un pedido y un repartidor.
     */
    public boolean create(Entrega entrega) throws SQLException {
        String sql = "INSERT INTO entregas (id_pedido, id_repartidor, fecha, hora) VALUES (?, ?, ?, ?)";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, entrega.getIdPedido());
            ps.setInt(2, entrega.getIdRepartidor());
            ps.setDate(3, entrega.getFecha());
            ps.setTime(4, entrega.getHora());
            int filas = ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    entrega.setId(rs.getInt(1));
                }
            }
            return filas > 0;
        }
    }

    /**
     * Retorna todas las entregas.
     */
    public List<Entrega> readAll() throws SQLException {
        return readByFiltro(null, null);
    }

    /**
     * Retorna las entregas filtradas por pedido y/o repartidor.
     * Si un filtro es null, no se aplica.
     */
    public List<Entrega> readByFiltro(Integer idPedido, Integer idRepartidor) throws SQLException {
        StringBuilder sql = new StringBuilder(SELECT_BASE).append(" WHERE 1=1");
        if (idPedido != null) {
            sql.append(" AND e.id_pedido = ?");
        }
        if (idRepartidor != null) {
            sql.append(" AND e.id_repartidor = ?");
        }
        sql.append(" ORDER BY e.fecha, e.hora");

        List<Entrega> lista = new ArrayList<>();

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {

            int indice = 1;
            if (idPedido != null) {
                ps.setInt(indice++, idPedido);
            }
            if (idRepartidor != null) {
                ps.setInt(indice, idRepartidor);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Entrega entrega = new Entrega(
                            rs.getInt("id"),
                            rs.getInt("id_pedido"),
                            rs.getInt("id_repartidor"),
                            rs.getDate("fecha"),
                            rs.getTime("hora"));
                    entrega.setDireccionPedido(rs.getString("direccion"));
                    entrega.setNombreRepartidor(rs.getString("nombre"));
                    lista.add(entrega);
                }
            }
        }
        return lista;
    }

    /**
     * Actualiza pedido, repartidor, fecha y hora de una entrega existente.
     */
    public boolean update(Entrega entrega) throws SQLException {
        String sql = "UPDATE entregas SET id_pedido = ?, id_repartidor = ?, fecha = ?, hora = ? WHERE id = ?";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, entrega.getIdPedido());
            ps.setInt(2, entrega.getIdRepartidor());
            ps.setDate(3, entrega.getFecha());
            ps.setTime(4, entrega.getHora());
            ps.setInt(5, entrega.getId());
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Elimina una entrega por su id.
     */
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM entregas WHERE id = ?";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }
}