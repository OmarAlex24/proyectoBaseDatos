package expendiocrudproyecto.utilidades;

import expendiocrudproyecto.App;
import expendiocrudproyecto.controlador.FXMLPrincipalController;
import expendiocrudproyecto.modelo.pojo.Usuario;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;

public class UtilPantallas {

    // Abre una nueva ventana con el archivo FXML especificado
    public static void abrirPantalla(String rutaFXML, String titulo, Button btnReportes) throws IOException {
        FXMLLoader loader = new FXMLLoader(UtilPantallas.class.getResource(rutaFXML));
        Parent root = loader.load();

        Stage stage = new Stage();
        stage.setTitle(titulo);
        stage.setScene(new Scene(root));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(true);
        stage.show();
    }

    // Abre una nueva ventana con tamaño específico
    public static void abrirPantallaConTamaño(String rutaFXML, String titulo, double ancho, double alto) throws IOException {
        FXMLLoader loader = new FXMLLoader(UtilPantallas.class.getResource(rutaFXML));
        Parent root = loader.load();

        Stage stage = new Stage();
        stage.setTitle(titulo);
        stage.setScene(new Scene(root, ancho, alto));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(true);
        stage.show();
    }

    // Abre una ventana no modal
    public static void abrirPantallaNoModal(String rutaFXML, String titulo) throws IOException {
        FXMLLoader loader = new FXMLLoader(UtilPantallas.class.getResource(rutaFXML));
        Parent root = loader.load();

        Stage stage = new Stage();
        stage.setTitle(titulo);
        stage.setScene(new Scene(root));
        stage.setResizable(true);
        stage.show();
    }

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
