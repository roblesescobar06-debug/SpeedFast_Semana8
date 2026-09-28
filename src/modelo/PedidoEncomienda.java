package modelo;

/**
 * Pedido de tipo Encomienda.
 *
 * Hereda de la clase abstracta Pedido e implementa el método abstracto
 * calcularTiempoEntrega() con su propia fórmula:
 *   tiempo = 20 min base + 1.5 min por cada kilómetro (ajustado a entero).
 */
public class PedidoEncomienda extends Pedido {

    /**
     * Constructor. Llama al constructor de la clase abstracta con super().
     *
     * @param idPedido         identificador único del pedido
     * @param direccionEntrega dirección de entrega
     * @param distanciaKm      distancia de reparto en kilómetros
     */
    public PedidoEncomienda(int idPedido, String direccionEntrega, double distanciaKm) {
        super(idPedido, direccionEntrega, distanciaKm);
    }

    /**
     * Implementación del método abstracto para pedidos de Encomienda.
     * Fórmula: 20 minutos base + 1.5 minutos por cada kilómetro.
     * El resultado se ajusta a entero mediante casting (int).
     *
     * @return tiempo estimado de entrega en minutos
     */
    @Override
    public int calcularTiempoEntrega() {
        return (int) (20 + 1.5 * distanciaKm);
    }

    /**
     * SOBRESCRITURA del método asignarRepartidor().
     * Regla de negocio: las encomiendas se asignan a repartidores en camioneta.
     */
    @Override
    public void asignarRepartidor() {
        this.repartidor = "Repartidor en camioneta (Encomienda)";
        System.out.println("Encomienda asignada automáticamente a: " + this.repartidor);
    }

    /**
     * Tipo del pedido tal como se guarda en la base de datos.
     */
    @Override
    public String getTipo() {
        return "ENCOMIENDA";
    }
}