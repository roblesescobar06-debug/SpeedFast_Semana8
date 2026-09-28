package modelo;

import java.sql.Date;
import java.sql.Time;

/**
 * Representa la entrega de un pedido realizada por un repartidor
 * (tabla entregas).
 */
public class Entrega {

    private int id;
    private int idPedido;
    private int idRepartidor;
    private Date fecha;
    private Time hora;

    // Datos solo para mostrar en la tabla (se obtienen con JOIN)
    private String direccionPedido;
    private String nombreRepartidor;

    public Entrega(int idPedido, int idRepartidor, Date fecha, Time hora) {
        this.idPedido = idPedido;
        this.idRepartidor = idRepartidor;
        this.fecha = fecha;
        this.hora = hora;
    }

    public Entrega(int id, int idPedido, int idRepartidor, Date fecha, Time hora) {
        this(idPedido, idRepartidor, fecha, hora);
        this.id = id;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdPedido() { return idPedido; }
    public void setIdPedido(int idPedido) { this.idPedido = idPedido; }

    public int getIdRepartidor() { return idRepartidor; }
    public void setIdRepartidor(int idRepartidor) { this.idRepartidor = idRepartidor; }

    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }

    public Time getHora() { return hora; }
    public void setHora(Time hora) { this.hora = hora; }

    public String getDireccionPedido() { return direccionPedido; }
    public void setDireccionPedido(String direccionPedido) { this.direccionPedido = direccionPedido; }

    public String getNombreRepartidor() { return nombreRepartidor; }
    public void setNombreRepartidor(String nombreRepartidor) { this.nombreRepartidor = nombreRepartidor; }
}