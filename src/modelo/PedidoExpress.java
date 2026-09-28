package modelo;

/**
 * Pedido de tipo Express.
 *
 * Hereda de la clase abstracta Pedido e implementa el método abstracto
 * calcularTiempoEntrega() con su propia fórmula:
 *   tiempo = 10 min base; si la distancia es mayor a 5 km, se suman 5 min extra.
 */
public class PedidoExpress extends Pedido {

    /**
     * Constructor. Llama al constructor de la clase abstracta con super().
     *
     * @param idPedido         identificador único del pedido
     * @param direccionEntrega dirección de entrega
     * @param distanciaKm      distancia de reparto en kilómetros
     */
    public PedidoExpress(int idPedido, String direccionEntrega, double distanciaKm) {
        super(idPedido, direccionEntrega, distanciaKm);
    }

    /**
     * Implementación del método abstracto para pedidos Express.
     * Fórmula: 10 minutos base. Si la distancia supera los 5 km,
     * se agregan 5 minutos extra.
     *
     * @return tiempo estimado de entrega en minutos
     */
    @Override
    public int calcularTiempoEntrega() {
        int tiempo = 10;
        if (distanciaKm > 5) {
            tiempo = tiempo + 5;
        }
        return tiempo;
    }

    /**
     * SOBRESCRITURA del método asignarRepartidor().
     * Regla de negocio: los pedidos express se asignan al repartidor más cercano disponible.
     */
    @Override
    public void asignarRepartidor() {
        this.repartidor = "Repartidor express prioritario";
        System.out.println("Pedido express asignado automáticamente a: " + this.repartidor);
    }

    /**
     * Tipo del pedido tal como se guarda en la base de datos.
     */
    @Override
    public String getTipo() {
        return "EXPRESS";
    }
}