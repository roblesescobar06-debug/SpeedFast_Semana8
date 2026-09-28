package vista;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

/**
 * Utilidad para mostrar mensajes al usuario con JOptionPane.
 * Centraliza la traducción de errores SQL a mensajes comprensibles.
 */
public final class Mensajes {

    private Mensajes() {
    }

    public static void exito(Component padre, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, "Operación exitosa", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void advertencia(Component padre, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, "Validación", JOptionPane.WARNING_MESSAGE);
    }

    public static boolean confirmar(Component padre, String mensaje) {
        int opcion = JOptionPane.showConfirmDialog(padre, mensaje, "Confirmar", JOptionPane.YES_NO_OPTION);
        return opcion == JOptionPane.YES_OPTION;
    }

    /**
     * Muestra un error de base de datos con un mensaje claro según su causa.
     *
     * @param padre  componente sobre el que se muestra el diálogo
     * @param accion descripción de lo que se intentaba hacer
     * @param e      excepción capturada
     */
    public static void errorBD(Component padre, String accion, SQLException e) {
        String mensajeOriginal = (e.getMessage() == null) ? "" : e.getMessage();
        String detalle;
        if (e instanceof SQLIntegrityConstraintViolationException) {
            detalle = "El registro está asociado a una o más entregas.\n"
                    + "Elimina primero esas entregas e inténtalo de nuevo.";
        } else if ("08S01".equals(e.getSQLState()) || mensajeOriginal.contains("Communications link failure")) {
            detalle = "No hay conexión con MySQL. Verifica que el servidor esté encendido.";
        } else {
            detalle = mensajeOriginal;
        }
        System.err.println("[ERROR BD] " + accion + ": " + mensajeOriginal);
        JOptionPane.showMessageDialog(padre,
                "No se pudo " + accion + ".\n\n" + detalle,
                "Error de base de datos",
                JOptionPane.ERROR_MESSAGE);
    }
}