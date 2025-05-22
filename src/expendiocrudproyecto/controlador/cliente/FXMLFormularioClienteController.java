package expendiocrudproyecto.controlador.cliente;

import expendiocrudproyecto.controlador.FXMLPrincipalController;
import expendiocrudproyecto.modelo.ConexionBD;
import expendiocrudproyecto.modelo.pojo.Cliente;
import expendiocrudproyecto.modelo.pojo.Usuario;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

import expendiocrudproyecto.utilidades.Alertas;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class FXMLFormularioClienteController implements Initializable, FXMLPrincipalController.ControladorConUsuario {

    @FXML
    private Label lblTitulo;
    @FXML
    private TextField tfNombre;
    @FXML
    private TextField tfTelefono;
    @FXML
    private TextField tfCorreo;
    @FXML
    private TextField tfRazonSocial;
    @FXML
    private Button btnGuardar;
    @FXML
    private Button btnCancelar;

    private Usuario usuarioSesion;
    private Cliente clienteEdicion;
    private boolean esEdicion;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        btnGuardar.setOnAction(this::guardarCliente);
        btnCancelar.setOnAction(this::cancelar);
    }

    @Override
    public void inicializarUsuario(Usuario usuario) {
        this.usuarioSesion = usuario;
    }

    public void inicializarNuevoCliente() {
        this.esEdicion = false;
        this.clienteEdicion = new Cliente();
        lblTitulo.setText("Nuevo Cliente");
    }

    public void inicializarEdicionCliente(Cliente cliente) {
        this.esEdicion = true;
        this.clienteEdicion = cliente;
        lblTitulo.setText("Editar Cliente");

        // Cargar datos del cliente en el formulario
        tfNombre.setText(cliente.getNombre());
        tfTelefono.setText(cliente.getTelefono());
        tfCorreo.setText(cliente.getCorreo());
        tfRazonSocial.setText(cliente.getRazonSocial());
    }

    private void guardarCliente(ActionEvent event) {
        if (validarFormulario()) {
            try {
                // Obtener datos del formulario
                clienteEdicion.setNombre(tfNombre.getText().trim());
                clienteEdicion.setTelefono(tfTelefono.getText().trim());
                clienteEdicion.setCorreo(tfCorreo.getText().trim());
                clienteEdicion.setRazonSocial(tfRazonSocial.getText().trim());

                Connection conexion = ConexionBD.abrirConexion();

                if (conexion != null) {
                    boolean operacionExitosa;

                    if (esEdicion) {
                        // Actualizar cliente existente
                        String consulta = "UPDATE cliente SET nombre = ?, telefono = ?, correo = ?, razonSocial = ? WHERE idCliente = ?";

                        PreparedStatement statement = conexion.prepareStatement(consulta);
                        statement.setString(1, clienteEdicion.getNombre());
                        statement.setString(2, clienteEdicion.getTelefono());
                        statement.setString(3, clienteEdicion.getCorreo());
                        statement.setString(4, clienteEdicion.getRazonSocial());
                        statement.setInt(5, clienteEdicion.getIdCliente());

                        int filasActualizadas = statement.executeUpdate();
                        operacionExitosa = filasActualizadas > 0;

                        statement.close();

                        if (operacionExitosa) {
                            Alertas.crearAlerta(Alert.AlertType.INFORMATION, "Cliente actualizado",
                                    "El cliente ha sido actualizado correctamente.");
                            cerrarVentana();
                        } else {
                            Alertas.crearAlerta(Alert.AlertType.ERROR, "Error al actualizar cliente",
                                    "No se pudo actualizar el cliente, por favor intentalo mas tarde");
                        }
                    } else {
                        // Insertar nuevo cliente
                        String consulta = "INSERT INTO cliente (nombre, telefono, correo, razonSocial) VALUES (?, ?, ?, ?)";

                        PreparedStatement statement = conexion.prepareStatement(consulta, Statement.RETURN_GENERATED_KEYS);
                        statement.setString(1, clienteEdicion.getNombre());
                        statement.setString(2, clienteEdicion.getTelefono());
                        statement.setString(3, clienteEdicion.getCorreo());
                        statement.setString(4, clienteEdicion.getRazonSocial());

                        int filasInsertadas = statement.executeUpdate();

                        if (filasInsertadas > 0) {
                            ResultSet generatedKeys = statement.getGeneratedKeys();
                            if (generatedKeys.next()) {
                                clienteEdicion.setIdCliente(generatedKeys.getInt(1));
                                Alertas.crearAlerta(Alert.AlertType.INFORMATION, "Cliente guardado",
                                        "El cliente ha sido guardado correctamente.");
                                cerrarVentana();
                            }
                        } else {
                            Alertas.crearAlerta(Alert.AlertType.ERROR, "Error al guardar cliente",
                                    "No se pudo guardar el cliente, por favor intentalo mas tarde");
                        }

                        statement.close();
                    }

                    conexion.close();
                } else {
                    Alertas.crearAlerta(Alert.AlertType.ERROR, "Error de conexión",
                            "No se pudo conectar a la base de datos. Por favor, verifica tu conexión.");
                }

            } catch (SQLException ex) {
                Alertas.crearAlerta(Alert.AlertType.ERROR, "Error de base de datos",
                        "Error al guardar el cliente, por favor intentalo mas tarde" );
            }
        }
    }

    private boolean validarFormulario() {
        String nombre = tfNombre.getText().trim();
        String telefono = tfTelefono.getText().trim();

        if (nombre.isEmpty()) {
            Alertas.crearAlerta(Alert.AlertType.WARNING, "Campo requerido", "El nombre no puede estar vacío");
            return false;
        }

        if (telefono.isEmpty()) {
            Alertas.crearAlerta(Alert.AlertType.WARNING, "Campo requerido", "El teléfono no puede estar vacío");
            return false;
        }

        return true;
    }

    private void cancelar(ActionEvent event) {
        cerrarVentana();
    }

    private void cerrarVentana() {
        Stage escenario = (Stage) btnCancelar.getScene().getWindow();
        escenario.close();
    }

}