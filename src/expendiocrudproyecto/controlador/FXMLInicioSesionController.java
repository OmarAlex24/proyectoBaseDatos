package expendiocrudproyecto.controlador;

import expendiocrudproyecto.App;
import expendiocrudproyecto.modelo.ConexionBD;
import expendiocrudproyecto.modelo.dao.UsuarioDAO;
import expendiocrudproyecto.modelo.pojo.TipoUsuario;
import expendiocrudproyecto.modelo.pojo.Usuario;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ResourceBundle;

import expendiocrudproyecto.utilidades.Alertas;
import expendiocrudproyecto.utilidades.SesionUsuario;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class FXMLInicioSesionController implements Initializable {

    @FXML
    private TextField txtUsuario;
    @FXML
    private PasswordField txtContrasenia;
    @FXML
    private Button btnLogin;
    @FXML
    private Label lblError;

    private Usuario usuarioSesion;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        btnLogin.setOnAction(this::iniciarSesion);
    }

    private void iniciarSesion(ActionEvent event) {
        String usuario = txtUsuario.getText();
        String contrasenia = txtContrasenia.getText();

        if (validarCampos(usuario, contrasenia)) {
            verificarCredenciales(usuario, contrasenia);
        }
    }

    private boolean validarCampos(String usuario, String contrasenia) {
        boolean sonValidos = true;

        if (usuario.isEmpty()) {
            mostrarMensajeError("El campo de usuario no puede estar vacío");
            sonValidos = false;
        } else if (contrasenia.isEmpty()) {
            mostrarMensajeError("El campo de contraseña no puede estar vacío");
            sonValidos = false;
        }

        return sonValidos;
    }

    private void verificarCredenciales(String usuario, String contrasenia) {
        try {
            Connection conexion = ConexionBD.getInstancia().abrirConexion();

            if (conexion != null) {
                try {
                    UsuarioDAO usuarioDAO = new UsuarioDAO();
                    Usuario usuarioAutenticado = usuarioDAO.autenticar(usuario, contrasenia);

                    if (usuarioAutenticado != null) {
                        this.usuarioSesion = usuarioAutenticado;
                        SesionUsuario.getInstancia().setUsuarioLogueado(usuarioSesion);
                        irPantallaPrincipal();
                    } else {
                        mostrarMensajeError("Usuario o contraseña incorrectos");
                    }

                } catch (SQLException ex) {
                    mostrarMensajeError("Error al conectar con la base de datos: " + ex.getMessage());
                    System.out.println("Error al conectar con la base de datos: " + ex.getMessage());
                } finally {
                    try {
                        conexion.close();
                    } catch (SQLException ex) {
                        System.out.println("Error al cerrar la conexión: " + ex.getMessage());
                    }
                }
            } else {
                mostrarMensajeError("No se pudo conectar con la base de datos");
            }
        } catch (SQLException e){
            mostrarMensajeError("No se pudo conectar con la base de datos");
        }

    }

    private void mostrarMensajeError(String mensaje) {
        lblError.setText(mensaje);
        lblError.setVisible(true);
    }

    private void irPantallaPrincipal() {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("vista/FXMLPrincipal.fxml"));
            Parent vista = loader.load();

            FXMLPrincipalController controlador = loader.getController();
            controlador.inicializarUsuario(usuarioSesion);

            Scene escenaPrincipal = new Scene(vista);
            Stage escenarioBase = (Stage) btnLogin.getScene().getWindow();
            escenarioBase.setScene(escenaPrincipal);
            escenarioBase.setTitle("Sistema de Gestión de Bebidas");
            escenarioBase.showAndWait();

        } catch (IOException ex) {
            Alertas.crearAlerta(Alert.AlertType.ERROR, "Error al cargar la pantalla principal",
                    "No se pudo cargar la pantalla principal. Por favor, inténtelo de nuevo más tarde.");
            System.out.println(ex.getMessage());
        }
    }
}