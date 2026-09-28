package modelo;

/**
 * Pedido de tipo Comida.
 *
 * Hereda de la clase abstracta Pedido e implementa el método abstracto
 * calcularTiempoEntrega() con su propia fórmula:
 *   tiempo = 15 min base + 2 min por cada kilómetro.
 */
public class PedidoComida extends Pedido {

    /**
     * Constructor. Llama al constructor de la clase abstracta con super().
     *
     * @param idPedido         identificador único del pedido
     * @param direccionEntrega dirección de entrega
     * @param distanciaKm      distancia de reparto en kilómetros
     */
    public PedidoComida(int idPedido, String direccionEntrega, double distanciaKm) {
        super(idPedido, direccionEntrega, distanciaKm);
    }

    /**
     * Implementación del método abstracto para pedidos de Comida.
     * Fórmula: 15 minutos base + 2 minutos por cada kilómetro de distancia.
     *
     * @return tiempo estimado de entrega en minutos
     */
    @Override
    public int calcularTiempoEntrega() {
        return 15 + (int) (2 * distanciaKm);
    }

    /**
     * SOBRESCRITURA del método asignarRepartidor().
     * Regla de negocio: los pedidos de comida se asignan a repartidores en moto.
     */
    @Override
    public void asignarRepartidor() {
        this.repartidor = "Repartidor en moto (Comida)";
        System.out.println("Pedido de comida asignado automáticamente a: " + this.repartidor);
    }

    /**
     * Tipo del pedido tal como se guarda en la base de datos.
     */
    @Override
    public String getTipo() {
        return "COMIDA";
    }
}