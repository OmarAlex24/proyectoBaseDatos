package expendiocrudproyecto.controlador.cliente;

import expendiocrudproyecto.controlador.FXMLPrincipalController;
import expendiocrudproyecto.modelo.dao.ClienteDAO;
import expendiocrudproyecto.modelo.pojo.Cliente;
import expendiocrudproyecto.modelo.pojo.Usuario;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import expendiocrudproyecto.utilidades.UtilPantallas;
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

public class FXMLListaClientesController implements Initializable, FXMLPrincipalController.ControladorConUsuario {

  @FXML
  private TextField tfBusqueda;
  @FXML
  private Button btnBuscar;
  @FXML
  private TableView<Cliente> tvClientes;
  @FXML
  private TableColumn<Cliente, String> tcNombre;
  @FXML
  private TableColumn<Cliente, String> tcTelefono;
  @FXML
  private TableColumn<Cliente, String> tcRazonSocial;
  @FXML
  private TableColumn<Cliente, String> tcCorreo;
  @FXML
  private Button btnAgregar;
  @FXML
  private Button btnEditar;
  @FXML
  private Button btnEliminar;
  @FXML
  private Button btnRegresar;

  private Usuario usuarioSesion;
  private ObservableList<Cliente> clientes;
  private ClienteDAO clienteDAO;
  private FXMLPrincipalController principalController;

  @Override
  public void initialize(URL url, ResourceBundle rb) {
    configurarTabla();
    configurarBotones();

    clienteDAO = new ClienteDAO();
    clientes = FXCollections.observableArrayList();
  }

  @Override
  public void inicializar(Usuario usuario, FXMLPrincipalController principalController) {
    this.usuarioSesion = usuario;
    this.principalController = principalController;
    cargarClientes();
  }

  private void configurarTabla() {
    tcNombre.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNombre()));
    tcTelefono.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTelefono()));
    tcCorreo.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCorreo()));
    tcRazonSocial.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRazonSocial()));
  }

  private void configurarBotones() {
    btnBuscar.setOnAction(this::buscarClientes);
    btnAgregar.setOnAction(this::agregarCliente);
    btnEditar.setOnAction(this::editarCliente);
    btnEliminar.setOnAction(this::eliminarCliente);
    btnRegresar.setOnAction(this::regresar);
  }

  private void cargarClientes() {
    try {
      List<Cliente> listaClientes = clienteDAO.leerTodo();
      clientes.clear();
      clientes.addAll(listaClientes);
      tvClientes.setItems(clientes);
    } catch (SQLException ex) {
      mostrarAlerta("Error al cargar los clientes: " + ex.getMessage(), Alert.AlertType.ERROR);
    }
  }

  private void buscarClientes(ActionEvent event) {
    String criterioBusqueda = tfBusqueda.getText().trim();

    if (criterioBusqueda.isEmpty()) {
      cargarClientes();
    } else {
      try {
        List<Cliente> resultadosBusqueda = clienteDAO.buscarPorNombreOApellido(criterioBusqueda);
        clientes.clear();
        clientes.addAll(resultadosBusqueda);
        tvClientes.setItems(clientes);
      } catch (SQLException ex) {
        mostrarAlerta("Error al buscar clientes: " + ex.getMessage(), Alert.AlertType.ERROR);
      }
    }
  }

  private void agregarCliente(ActionEvent event) {
    try {
      FXMLLoader loader = new FXMLLoader(
          getClass().getResource("/expendiocrudproyecto/vista/cliente/FXMLFormularioCliente.fxml"));
      Parent vista = loader.load();

      FXMLFormularioClienteController controller = loader.getController();
      controller.inicializar(usuarioSesion, principalController);
      controller.inicializarNuevoCliente();

      Stage stage = new Stage();
      stage.setScene(new Scene(vista));
      stage.setTitle("Nuevo Cliente");
      stage.initModality(Modality.APPLICATION_MODAL);
      stage.showAndWait();

      cargarClientes();
    } catch (IOException ex) {
      mostrarAlerta("Error al abrir el formulario: " + ex.getMessage(), Alert.AlertType.ERROR);
    }
  }

  private void editarCliente(ActionEvent event) {
    Cliente clienteSeleccionado = tvClientes.getSelectionModel().getSelectedItem();

    if (clienteSeleccionado != null) {
      try {
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/expendiocrudproyecto/vista/cliente/FXMLFormularioCliente.fxml"));
        Parent vista = loader.load();

        FXMLFormularioClienteController controller = loader.getController();
        controller.inicializar(usuarioSesion, principalController);
        controller.inicializarEdicionCliente(clienteSeleccionado);

        Stage stage = new Stage();
        stage.setScene(new Scene(vista));
        stage.setTitle("Editar Cliente");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();

        cargarClientes();
      } catch (IOException ex) {
        mostrarAlerta("Error al abrir el formulario: " + ex.getMessage(), Alert.AlertType.ERROR);
      }
    } else {
      mostrarAlerta("Debe seleccionar un cliente para editar", Alert.AlertType.WARNING);
    }
  }

  private void eliminarCliente(ActionEvent event) {
    Cliente clienteSeleccionado = tvClientes.getSelectionModel().getSelectedItem();

    if (clienteSeleccionado != null) {
      Optional<ButtonType> respuesta = mostrarConfirmacion("¿Está seguro que desea eliminar el cliente '" +
          clienteSeleccionado.getNombre() + "'?");

      if (respuesta.isPresent() && respuesta.get() == ButtonType.OK) {
        try {
          boolean eliminacionExitosa = clienteDAO.eliminar(clienteSeleccionado.getIdCliente());

          if (eliminacionExitosa) {
            mostrarAlerta("Cliente eliminado correctamente", Alert.AlertType.INFORMATION);
            cargarClientes();
          } else {
            mostrarAlerta("No se pudo eliminar el cliente", Alert.AlertType.ERROR);
          }
        } catch (SQLException ex) {
          mostrarAlerta("Error al eliminar el cliente: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
      }
    } else {
      mostrarAlerta("Debe seleccionar un cliente para eliminar", Alert.AlertType.WARNING);
    }
  }

  private void regresar(ActionEvent event) {
    principalController.restaurarVistaPrincipal();
  }

  private void mostrarAlerta(String mensaje, Alert.AlertType tipo) {
    Alert alerta = new Alert(tipo);
    alerta.setTitle("Sistema de Gestión");
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