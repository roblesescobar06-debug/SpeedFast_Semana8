package modelo;

/**
 * Clase abstracta que representa un pedido genérico del sistema SpeedFast.
 *
 * Define los atributos comunes a todos los pedidos y dos métodos:
 *  - mostrarResumen(): método implementado (con cuerpo) que muestra los datos básicos.
 *  - calcularTiempoEntrega(): método ABSTRACTO, que cada subclase implementa con su propia fórmula.
 *
 * Al ser abstracta, esta clase no se puede instanciar directamente:
 * solo sirve como plantilla base para las subclases.
 */
public abstract class Pedido {

    // Atributos comunes a todos los pedidos
    protected int idPedido;
    protected String direccionEntrega;
    protected double distanciaKm;
    protected String repartidor;
    protected String estado = "PENDIENTE";

    /**
     * Constructor de la clase abstracta.
     *
     * @param idPedido         identificador único del pedido
     * @param direccionEntrega dirección de entrega
     * @param distanciaKm      distancia de reparto en kilómetros
     */
    public Pedido(int idPedido, String direccionEntrega, double distanciaKm) {
        this.idPedido = idPedido;
        this.direccionEntrega = direccionEntrega;
        this.distanciaKm = distanciaKm;
    }

    /**
     * Método implementado (con cuerpo): imprime los datos básicos del pedido.
     * Todas las subclases lo heredan tal cual.
     */
    public void mostrarResumen() {
        System.out.println("----------------------------------------------------");
        System.out.println("Pedido #" + idPedido);
        System.out.println("Dirección de entrega: " + direccionEntrega);
        System.out.println("Distancia: " + distanciaKm + " km");
    }

    /**
     * Método ABSTRACTO: cada subclase implementa su propia fórmula
     * de cálculo del tiempo de entrega.
     *
     * @return el tiempo estimado de entrega en minutos
     */
    public abstract int calcularTiempoEntrega();

    /**
     * Método ABSTRACTO: cada subclase indica su tipo tal como se guarda
     * en la base de datos (COMIDA, ENCOMIENDA o EXPRESS).
     *
     * @return tipo del pedido
     */
    public abstract String getTipo();

    /**
     * Método de fábrica: crea la subclase correcta según el tipo
     * leído desde la base de datos o elegido en la interfaz.
     *
     * @param tipo      COMIDA, ENCOMIENDA o EXPRESS
     * @param id        identificador del pedido
     * @param direccion dirección de entrega
     * @return instancia de PedidoComida, PedidoEncomienda o PedidoExpress
     */
    public static Pedido crear(String tipo, int id, String direccion) {
        switch (tipo) {
            case "COMIDA":
                return new PedidoComida(id, direccion, 0);
            case "ENCOMIENDA":
                return new PedidoEncomienda(id, direccion, 0);
            case "EXPRESS":
                return new PedidoExpress(id, direccion, 0);
            default:
                throw new IllegalArgumentException("Tipo de pedido no válido: " + tipo);
        }
    }

    /**
     * Método que se SOBRESCRIBE en cada subclase con su lógica de asignación.
     * Asignación AUTOMÁTICA del repartidor.
     */
    public void asignarRepartidor() {
        this.repartidor = "Repartidor genérico";
    }

    /**
     * Método SOBRECARGADO: mismo nombre, distintos parámetros.
     * Asignación MANUAL del repartidor por nombre.
     */
    public void asignarRepartidor(String nombre) {
        this.repartidor = nombre;
        System.out.println("Repartidor asignado manualmente: " + nombre);
    }

    // ------------------- Getters y Setters -------------------

    public int getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
    }

    public String getDireccionEntrega() {
        return direccionEntrega;
    }

    public void setDireccionEntrega(String direccionEntrega) {
        this.direccionEntrega = direccionEntrega;
    }

    public double getDistanciaKm() {
        return distanciaKm;
    }

    public void setDistanciaKm(double distanciaKm) {
        this.distanciaKm = distanciaKm;
    }

    public String getRepartidor() {
        return repartidor;
    }

    public void setRepartidor(String repartidor) {
        this.repartidor = repartidor;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    /**
     * Texto legible para los JComboBox: "id - dirección".
     */
    @Override
    public String toString() {
        return idPedido + " - " + direccionEntrega;
    }
}