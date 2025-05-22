package expendiocrudproyecto.utilidades;

import expendiocrudproyecto.App;
import expendiocrudproyecto.controlador.FXMLPrincipalController;
import expendiocrudproyecto.controlador.proveedor.FXMLFormularioProveedorController;
import expendiocrudproyecto.modelo.pojo.Usuario;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Control;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class UtilPantallas {
    public static void regresarPrincipal(ActionEvent event, Usuario usuarioSesion, Control componente ) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/expendiocrudproyecto/vista/FXMLPrincipal.fxml"));
            Parent vista = loader.load();

            FXMLPrincipalController controlador = loader.getController();
            controlador.inicializarUsuario(usuarioSesion);

            Scene escenaPrincipal = new Scene(vista);
            Stage escenarioBase = (Stage) componente.getScene().getWindow();
            escenarioBase.setScene(escenaPrincipal);
            escenarioBase.setTitle("Sistema de Gestión de Bebidas");
            escenarioBase.show();

        } catch (IOException ex) {
            Alertas.crearAlerta(Alert.AlertType.ERROR, "Error al regresar","Error al cargar la pantalla principal");
        }
    }


}
