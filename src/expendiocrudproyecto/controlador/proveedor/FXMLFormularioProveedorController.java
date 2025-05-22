package expendiocrudproyecto.controlador.proveedor;

import expendiocrudproyecto.controlador.FXMLPrincipalController;
import expendiocrudproyecto.modelo.ConexionBD;
import expendiocrudproyecto.modelo.pojo.Proveedor;
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

public class FXMLFormularioProveedorController implements Initializable, FXMLPrincipalController.ControladorConUsuario {

    @FXML
    private Label lblTitulo;
    @FXML
    private TextField tfRazonSocial;
    @FXML
    private TextField tfTelefono;
    @FXML
    private TextField tfDireccion;
    @FXML
    private TextField tfCorreo;
    @FXML
    private Button btnGuardar;
    @FXML
    private Button btnCancelar;
    
    private Usuario usuarioSesion;
    private Proveedor proveedorEdicion;
    private boolean esEdicion;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        btnGuardar.setOnAction(this::guardarProveedor);
        btnCancelar.setOnAction(this::cancelar);
    }
    
    @Override
    public void inicializarUsuario(Usuario usuario) {
        this.usuarioSesion = usuario;
    }
    
    public void inicializarNuevoProveedor() {
        this.esEdicion = false;
        this.proveedorEdicion = new Proveedor();
        lblTitulo.setText("Nuevo Proveedor");
    }
    
    public void inicializarEdicionProveedor(Proveedor proveedor) {
        this.esEdicion = true;
        this.proveedorEdicion = proveedor;
        lblTitulo.setText("Editar Proveedor");
        
        // Cargar datos del proveedor en el formulario
        tfRazonSocial.setText(proveedor.getRazonSocial());
        tfTelefono.setText(proveedor.getTelefono());
        tfDireccion.setText(proveedor.getDireccion());
        tfCorreo.setText(proveedor.getCorreo());
    }
    
    private void guardarProveedor(ActionEvent event) {
        if (validarFormulario()) {
            try {
                // Obtener datos del formulario
                proveedorEdicion.setRazonSocial(tfRazonSocial.getText().trim());
                proveedorEdicion.setTelefono(tfTelefono.getText().trim());
                proveedorEdicion.setDireccion(tfDireccion.getText().trim());
                proveedorEdicion.setCorreo(tfCorreo.getText().trim());
                
                Connection conexion = ConexionBD.abrirConexion();
                
                if (conexion != null) {
                    boolean operacionExitosa;
                    
                    if (esEdicion) {
                        // Actualizar proveedor existente
                        String consulta = "UPDATE proveedor SET razonSocial = ?, telefono = ?, direccion = ?, correo = ? WHERE idProveedor = ?";
                        
                        PreparedStatement statement = conexion.prepareStatement(consulta);
                        statement.setString(1, proveedorEdicion.getRazonSocial());
                        statement.setString(2, proveedorEdicion.getTelefono());
                        statement.setString(3, proveedorEdicion.getDireccion());
                        statement.setString(4, proveedorEdicion.getCorreo());
                        statement.setInt(5, proveedorEdicion.getIdProveedor());
                        
                        int filasActualizadas = statement.executeUpdate();
                        operacionExitosa = filasActualizadas > 0;
                        
                        statement.close();
                        
                        if (operacionExitosa) {
                            Alertas.crearAlerta(Alert.AlertType.INFORMATION, "Proveedor actualizado",
                                    "El proveedor ha sido actualizado con éxito.");
                            cerrarVentana();
                        } else {
                            Alertas.crearAlerta(Alert.AlertType.ERROR, "Error al actualizar proveedor",
                                    "No se pudo actualizar el proveedor");
                        }
                    } else {
                        // Insertar nuevo proveedor
                        String consulta = "INSERT INTO proveedor (razonSocial, telefono, direccion, correo) VALUES (?, ?, ?, ?)";
                        
                        PreparedStatement statement = conexion.prepareStatement(consulta, Statement.RETURN_GENERATED_KEYS);
                        statement.setString(1, proveedorEdicion.getRazonSocial());
                        statement.setString(2, proveedorEdicion.getTelefono());
                        statement.setString(3, proveedorEdicion.getDireccion());
                        statement.setString(4, proveedorEdicion.getCorreo());
                        
                        int filasInsertadas = statement.executeUpdate();
                        
                        if (filasInsertadas > 0) {
                            ResultSet generatedKeys = statement.getGeneratedKeys();
                            if (generatedKeys.next()) {
                                proveedorEdicion.setIdProveedor(generatedKeys.getInt(1));
                                Alertas.crearAlerta(Alert.AlertType.INFORMATION, "Proveedor guardado",
                                        "El proveedor ha sido guardado con éxito.");
                                cerrarVentana();
                            }
                        } else {
                            Alertas.crearAlerta(Alert.AlertType.ERROR, "Error al guardar proveedor",
                                    "No se pudo guardar el proveedor");
                        }
                        
                        statement.close();
                    }
                    
                    conexion.close();
                } else {
                    Alertas.crearAlerta(Alert.AlertType.ERROR, "Error de conexión",
                            "No se pudo conectar a la base de datos");
                }
                
            } catch (SQLException ex) {
                Alertas.crearAlerta(Alert.AlertType.ERROR, "Error de base de datos",
                        "Error al guardar el proveedor: " + ex.getMessage());
            }
        }
    }
    
    private boolean validarFormulario() {
        String razonSocial = tfRazonSocial.getText().trim();
        String telefono = tfTelefono.getText().trim();
        
        if (razonSocial.isEmpty()) {
            Alertas.crearAlerta(Alert.AlertType.WARNING, "Campo requerido", "La razón social no puede estar vacía");
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