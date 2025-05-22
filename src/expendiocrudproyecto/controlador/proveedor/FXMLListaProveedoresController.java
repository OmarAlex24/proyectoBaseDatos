package expendiocrudproyecto.controlador.proveedor;

import expendiocrudproyecto.controlador.FXMLPrincipalController;
import expendiocrudproyecto.modelo.dao.ProveedorDAO;
import expendiocrudproyecto.modelo.pojo.Proveedor;
import expendiocrudproyecto.modelo.pojo.Usuario;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import expendiocrudproyecto.utilidades.UtilPantallas;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class FXMLListaProveedoresController implements Initializable, FXMLPrincipalController.ControladorConUsuario {

    @FXML
    private TextField tfBusqueda;
    @FXML
    private Button btnBuscar;
    @FXML
    private TableView<Proveedor> tvProveedores;
    @FXML
    private TableColumn<Proveedor, Integer> tcId;
    @FXML
    private TableColumn<Proveedor, String> tcRazonSocial;
    @FXML
    private TableColumn<Proveedor, String> tcTelefono;
    @FXML
    private TableColumn<Proveedor, String> tcDireccion;
    @FXML
    private TableColumn<Proveedor, String> tcCorreo;
    @FXML
    private Button btnAgregar;
    @FXML
    private Button btnEditar;
    @FXML
    private Button btnEliminar;
    @FXML
    private Button btnRegresar;

    private Usuario usuarioSesion;
    private ObservableList<Proveedor> proveedores;
    private ProveedorDAO proveedorDAO;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
        configurarBotones();

        proveedorDAO = new ProveedorDAO();
        proveedores = FXCollections.observableArrayList();
    }

    @Override
    public void inicializarUsuario(Usuario usuario) {
        this.usuarioSesion = usuario;
        cargarProveedores();
    }

    private void configurarTabla() {
        tcId.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getIdProveedor()).asObject());
        tcRazonSocial.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRazonSocial()));
        tcTelefono.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTelefono()));
        tcDireccion.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDireccion()));
        tcCorreo.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCorreo()));
    }

    private void configurarBotones() {
        btnBuscar.setOnAction(this::buscarProveedores);
        btnAgregar.setOnAction(this::agregarProveedor);
        btnEditar.setOnAction(this::editarProveedor);
        btnEliminar.setOnAction(this::eliminarProveedor);
        btnRegresar.setOnAction(this::regresar);
    }

    private void cargarProveedores() {
        try {
            List<Proveedor> listaProveedores = proveedorDAO.leerTodo();
            proveedores.clear();
            proveedores.addAll(listaProveedores);
            tvProveedores.setItems(proveedores);
        } catch (SQLException ex) {
            mostrarAlerta("Error al cargar los proveedores: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void buscarProveedores(ActionEvent event) {
        String criterioBusqueda = tfBusqueda.getText().trim();

        if (criterioBusqueda.isEmpty()) {
            cargarProveedores();
        } else {
            try {
                List<Proveedor> resultadosBusqueda = proveedorDAO.buscarPorNombre(criterioBusqueda);
                proveedores.clear();
                proveedores.addAll(resultadosBusqueda);
                tvProveedores.setItems(proveedores);
            } catch (SQLException ex) {
                mostrarAlerta("Error al buscar proveedores: " + ex.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    private void agregarProveedor(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/expendiocrudproyecto/vista/FXMLFormularioProveedor.fxml"));
            Parent vista = loader.load();

            FXMLFormularioProveedorController controller = loader.getController();
            controller.inicializarUsuario(usuarioSesion);
            controller.inicializarNuevoProveedor();

            Stage stage = new Stage();
            stage.setScene(new Scene(vista));
            stage.setTitle("Nuevo Proveedor");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            cargarProveedores();
        } catch (IOException ex) {
            mostrarAlerta("Error al abrir el formulario: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void editarProveedor(ActionEvent event) {
        Proveedor proveedorSeleccionado = tvProveedores.getSelectionModel().getSelectedItem();

        if (proveedorSeleccionado != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/expendiocrudproyecto/vista/proveedor/FXMLFormularioProveedor.fxml"));
                Parent vista = loader.load();

                FXMLFormularioProveedorController controller = loader.getController();
                controller.inicializarUsuario(usuarioSesion);
                controller.inicializarEdicionProveedor(proveedorSeleccionado);

                Stage stage = new Stage();
                stage.setScene(new Scene(vista));
                stage.setTitle("Editar Proveedor");
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.showAndWait();

                cargarProveedores();
            } catch (IOException ex) {
                mostrarAlerta("Error al abrir el formulario: " + ex.getMessage(), Alert.AlertType.ERROR);
            }
        } else {
            mostrarAlerta("Debe seleccionar un proveedor para editar", Alert.AlertType.WARNING);
        }
    }

    private void eliminarProveedor(ActionEvent event) {
        Proveedor proveedorSeleccionado = tvProveedores.getSelectionModel().getSelectedItem();

        if (proveedorSeleccionado != null) {
            Optional<ButtonType> respuesta = mostrarConfirmacion("¿Está seguro que desea eliminar el proveedor '" +
                    proveedorSeleccionado.getRazonSocial() + "'?");

            if (respuesta.isPresent() && respuesta.get() == ButtonType.OK) {
                try {
                    boolean eliminacionExitosa = proveedorDAO.eliminar(proveedorSeleccionado.getIdProveedor());

                    if (eliminacionExitosa) {
                        mostrarAlerta("Proveedor eliminado correctamente", Alert.AlertType.INFORMATION);
                        cargarProveedores();
                    } else {
                        mostrarAlerta("No se pudo eliminar el proveedor", Alert.AlertType.ERROR);
                    }
                } catch (SQLException ex) {
                    mostrarAlerta("Error al eliminar el proveedor: " + ex.getMessage(), Alert.AlertType.ERROR);
                }
            }
        } else {
            mostrarAlerta("Debe seleccionar un proveedor para eliminar", Alert.AlertType.WARNING);
        }
    }

    private void regresar(ActionEvent event) {
        UtilPantallas.regresarPrincipal(event, usuarioSesion, btnRegresar);
    }

    private void mostrarAlerta(String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle("Sistema de Gestión de Bebidas");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    private Optional<ButtonType> mostrarConfirmacion(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle("Confirmación");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        return alerta.showAndWait();
    }
}