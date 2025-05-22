package expendiocrudproyecto.utilidades;

import javafx.scene.control.Alert;

public class Alertas {
    public static void crearAlerta(Alert.AlertType tipoAlerta, String titulo, String contenido) {
        Alert alerta = new Alert(tipoAlerta);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(contenido);
        alerta.showAndWait();
    }
}
