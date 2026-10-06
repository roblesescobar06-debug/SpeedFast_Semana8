# SpeedFast – Semana 8: CRUD con JDBC + Swing

Sistema de gestión de repartidores, pedidos y entregas en Java (Swing) conectado a MySQL mediante JDBC.

## Funcionalidades
- **Repartidores:** registrar, editar, eliminar y listar.
- **Pedidos:** registrar, editar, eliminar y listar con filtros por estado y tipo.
- **Entregas:** registrar asociando pedido y repartidor (JComboBox cargados desde la BD), editar, eliminar y listar con filtros por pedido o repartidor.
- Validación de campos obligatorios y formatos antes de cada operación.
- Manejo de excepciones SQL con mensajes claros (JOptionPane).

## Estructura (separación por capas)
- `modelo`: Pedido (abstracta), PedidoComida, PedidoEncomienda, PedidoExpress, Repartidor, Entrega, interfaces y ControladorDeEnvios.
- `dao`: ConexionBD, RepartidorDAO, PedidoDAO, EntregaDAO (create, readAll, update, delete con PreparedStatement y ResultSet).
- `vista`: VentanaPrincipal (JTabbedPane), PanelRepartidores, PanelPedidos, PanelEntregas, Mensajes.
- `main`: Main (punto de entrada).

## Requisitos
- JDK 21 o superior.
- MySQL Server en `localhost:3306`, usuario `root`, contraseña `speedfast2026` (configurable en `dao/ConexionBD.java`).
- Conector MySQL incluido en la carpeta `lib`.

## Cómo ejecutar
1. Ejecutar el script `speedfast_db.sql` en MySQL para crear la base `speedfast_db`.
2. Abrir el proyecto en IntelliJ IDEA.
3. Ejecutar `main/Main.java`.

## Autor
Cristóbal Robles – Desarrollo Orientado a Objetos II, Duoc UC.
