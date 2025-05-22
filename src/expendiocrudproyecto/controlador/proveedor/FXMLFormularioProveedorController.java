package expendiocrudproyecto.controlador.proveedor;

import expendiocrudproyecto.controlador.FXMLPrincipalController;
import expendiocrudproyecto.modelo.dao.ProveedorDAO;
import expendiocrudproyecto.modelo.pojo.Proveedor;
import expendiocrudproyecto.modelo.pojo.Usuario;
import java.net.URL;
import java.sql.SQLException;
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
    private ProveedorDAO proveedorDAO;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        proveedorDAO = new ProveedorDAO();
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

                if (esEdicion) {
                    // Actualizar proveedor existente
                    boolean operacionExitosa = proveedorDAO.actualizar(proveedorEdicion);

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
                    Proveedor proveedorInsertado = proveedorDAO.insertar(proveedorEdicion);

                    if (proveedorInsertado != null && proveedorInsertado.getIdProveedor() != null) {
                        Alertas.crearAlerta(Alert.AlertType.INFORMATION, "Proveedor guardado",
                                "El proveedor ha sido guardado con éxito.");
                        cerrarVentana();
                    } else {
                        Alertas.crearAlerta(Alert.AlertType.ERROR, "Error al guardar proveedor",
                                "No se pudo guardar el proveedor");
                    }
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