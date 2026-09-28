# SpeedFast – Semana 7: JDBC + MySQL

Sistema de gestión de pedidos y entregas en Java (Swing) conectado a una base de datos MySQL mediante JDBC.

## Estructura
- `modelo`: Pedido (abstracta), PedidoComida, PedidoEncomienda, PedidoExpress, Repartidor, Entrega, ControladorDeEnvios
- `dao`: ConexionBD, PedidoDAO, RepartidorDAO, EntregaDAO, TestDAO
- `vista`: VentanaPrincipal, VentanaRegistroPedido, VentanaListaPedidos, VentanaRegistroRepartidor, VentanaAsignarRepartidor
- `main`: Main

## Funcionalidades
- Registrar pedidos en la tabla `pedido` (ID generado por MySQL).
- Registrar repartidores en la tabla `repartidor` y mostrarlos en una JTable.
- Listar pedidos desde la base de datos en una JTable.
- Asignar repartidor desde la tabla `repartidor`, registrar la entrega en la tabla `entrega` y actualizar el estado del pedido (Pendiente → En reparto → Entregado) usando un hilo.

## Requisitos
- JDK 21 o superior
- MySQL 8 con la base de datos `speedfast_db` (tablas `repartidor`, `pedido`, `entrega`)
- Conector `mysql-connector-j` agregado como librería del proyecto

## Instalación de la base de datos
Ejecutar el script `speedfast_db.sql` en MySQL Workbench para crear la base de datos, sus tablas y datos de prueba.

## Ejecución
Ejecutar `main.Main`.