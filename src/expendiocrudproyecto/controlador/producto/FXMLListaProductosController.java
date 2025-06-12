package expendiocrudproyecto.controlador.producto;

import expendiocrudproyecto.App;
import expendiocrudproyecto.controlador.FXMLPrincipalController;
import expendiocrudproyecto.modelo.dao.BebidaDAO;
import expendiocrudproyecto.modelo.dao.UsuarioDAO;
import expendiocrudproyecto.modelo.pojo.Bebida;
import expendiocrudproyecto.modelo.pojo.TipoUsuario;
import expendiocrudproyecto.modelo.pojo.Usuario;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import expendiocrudproyecto.utilidades.Alertas;
import expendiocrudproyecto.utilidades.UtilPantallas;
import javafx.beans.property.SimpleFloatProperty;
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
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.HBox;
import javafx.scene.control.ButtonBar;

public class FXMLListaProductosController implements Initializable, FXMLPrincipalController.ControladorConUsuario {

  @FXML
  private TextField tfBusqueda;
  @FXML
  private Button btnBuscar;
  @FXML
  private TableView<Bebida> tvProductos;
  @FXML
  private TableColumn<Bebida, String> tcNombre;
  @FXML
  private TableColumn<Bebida, Float> tcPrecio;
  @FXML
  private TableColumn<Bebida, Integer> tcStock;
  @FXML
  private TableColumn<Bebida, Integer> tcStockMinimo;
  @FXML
  private Button btnAgregar;
  @FXML
  private Button btnEditar;
  @FXML
  private Button btnEliminar;
  @FXML
  private Button btnRegresar;

  private Usuario usuarioSesion;
  private UsuarioDAO usuarioDAO ;
  private ObservableList<Bebida> productos;
  private BebidaDAO bebidaDAO;
  private FXMLPrincipalController principalController;
  private TableColumn<Bebida, Void> tcAcciones;

  @Override
  public void initialize(URL url, ResourceBundle rb) {
    configurarTabla();
    configurarColumnaAcciones();
    configurarBotones();

    bebidaDAO = new BebidaDAO();
    productos = FXCollections.observableArrayList();
    usuarioDAO = new UsuarioDAO();
  }

  @Override
  public void inicializar(Usuario usuario, FXMLPrincipalController principalController) {
    this.usuarioSesion = usuario;
    this.principalController = principalController;
    cargarProductos();
    configurarPermisos();
  }

  private void configurarPermisos() {
    if (usuarioSesion.getTipoUsuario() == TipoUsuario.EMPLEADO) {
      // Ocultar y deshabilitar botones para empleados
      ocultarBoton(btnEditar);
      ocultarBoton(btnAgregar);
      ocultarBoton(btnEliminar);
    }
    // El administrador tiene acceso completo por defecto
  }
  private void ocultarBoton(Button boton) {
    boton.setVisible(false);
    boton.setDisable(true);
  }

  private void configurarTabla() {
    tcNombre.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNombre()));
    tcPrecio.setCellValueFactory(cellData -> new SimpleFloatProperty(cellData.getValue().getPrecio()).asObject());
    tcStock.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getStock()).asObject());
    tcStockMinimo
        .setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getStockMinimo()).asObject());
  }

  private void configurarColumnaAcciones() {
    tcAcciones = new TableColumn<>("Acciones");
    tvProductos.getColumns().add(tcAcciones);

    tcAcciones.setCellFactory(col -> new TableCell<Bebida, Void>() {
      private final Button btnAutorizar = new Button("Cambiar stock");

      {
        btnAutorizar.setOnAction(e -> {
          Bebida bebida = getTableView().getItems().get(getIndex());
          solicitarAutorizacion(bebida);
        });
      }

      @Override
      protected void updateItem(Void item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
          setGraphic(null);
        } else {
          setGraphic(btnAutorizar);
        }
      }
    });
  }

  private void solicitarAutorizacion(Bebida bebida) {
    Dialog<String> dialog = new Dialog<>();
    dialog.setTitle("Autorización de administrador");
    dialog.setHeaderText("Actualizar stock para: " + bebida.getNombre());

    ButtonType okButtonType = new ButtonType("Aceptar", ButtonBar.ButtonData.OK_DONE);
    dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

    PasswordField pf = new PasswordField();
    pf.setPromptText("Contraseña");
    dialog.getDialogPane().setContent(pf);

    dialog.setResultConverter(dialogButton -> {
      if (dialogButton == okButtonType) {
        return pf.getText();
      }
      return null;
    });

    dialog.showAndWait().ifPresent(pass -> {
      if (usuarioDAO.autorizarStock(pass)) {
        pedirNuevoStock(bebida);
      } else {
        Alertas.crearAlerta(Alert.AlertType.ERROR, "Contraseña incorrecta", "La contraseña ingresada es incorrecta.");
      }
    });
  }

  private void pedirNuevoStock(Bebida bebida) {
    TextInputDialog dialog = new TextInputDialog(String.valueOf(bebida.getStock()));
    dialog.setTitle("Actualizar Stock");
    dialog.setHeaderText("Nuevo stock para: " + bebida.getNombre());
    dialog.setContentText("Ingrese el nuevo valor de stock:");

    dialog.showAndWait().ifPresent(valor -> {
      try {
        int nuevoStock = Integer.parseInt(valor);
        if (nuevoStock < 0) {
          throw new NumberFormatException();
        }
        boolean exito = bebidaDAO.actualizarStock(bebida.getId(), nuevoStock);
        if (exito) {
          bebida.setStock(nuevoStock);
          tvProductos.refresh();
          Alertas.crearAlerta(Alert.AlertType.INFORMATION, "Stock actualizado",
              "El stock se ha actualizado correctamente.");
        } else {
          Alertas.crearAlerta(Alert.AlertType.ERROR, "Error al actualizar",
              "No se pudo actualizar el stock del producto.");
        }
      } catch (NumberFormatException | SQLException ex) {
        Alertas.crearAlerta(Alert.AlertType.ERROR, "Valor inválido", "Debe ingresar un número entero válido.");
      }
    });
  }

  private void configurarBotones() {
    // btnBuscar.setOnAction(this::buscarProductos);
    btnAgregar.setOnAction(this::agregarProducto);
    btnEditar.setOnAction(this::editarProducto);
    btnEliminar.setOnAction(this::eliminarProducto);
    btnRegresar.setOnAction(this::regresar);
  }

  private void cargarProductos() {
    try {
      List<Bebida> listaBebidas = bebidaDAO.leerTodo();
      productos.clear();
      productos.addAll(listaBebidas);
      tvProductos.setItems(productos);
    } catch (SQLException ex) {
      Alertas.crearAlerta(Alert.AlertType.ERROR, "Error al cargar productos",
          "Error al cargar la lista de productos: " + ex.getMessage());
    }
  }

  private void buscarProductos(ActionEvent event) {
    String criterioBusqueda = tfBusqueda.getText().trim();

    if (criterioBusqueda.isEmpty()) {
      cargarProductos();
    } else {
      try {
        List<Bebida> resultadosBusqueda = bebidaDAO.buscarPorNombre(criterioBusqueda);
        productos.clear();
        productos.addAll(resultadosBusqueda);
        tvProductos.setItems(productos);
      } catch (SQLException ex) {
        Alertas.crearAlerta(Alert.AlertType.ERROR, "Error al buscar productos",
            "Error al buscar productos: " + ex.getMessage());
      }
    }
  }

  private void agregarProducto(ActionEvent event) {
    try {
      FXMLLoader loader = new FXMLLoader(App.class.getResource("vista/producto/FXMLFormularioProducto.fxml"));
      Parent vista = loader.load();

      FXMLFormularioProductoController controller = loader.getController();
      controller.inicializar(usuarioSesion, principalController);
      controller.inicializarNuevoProducto();

      Stage stage = new Stage();
      stage.setScene(new Scene(vista));
      stage.setTitle("Nuevo Producto");
      stage.initModality(Modality.APPLICATION_MODAL);
      stage.showAndWait();

      cargarProductos();
    } catch (IOException ex) {
      Alertas.crearAlerta(Alert.AlertType.ERROR, "Error al abrir el formulario",
          "Error al abrir el formulario de nuevo producto: " + ex.getMessage());
    }
  }

  private void editarProducto(ActionEvent event) {
    Bebida bebidaSeleccionada = tvProductos.getSelectionModel().getSelectedItem();

    if (bebidaSeleccionada != null) {
      try {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("vista/producto/FXMLFormularioProducto.fxml"));
        Parent vista = loader.load();

        FXMLFormularioProductoController controller = loader.getController();
        controller.inicializar(usuarioSesion, principalController);
        controller.inicializarEdicionProducto(bebidaSeleccionada);

        Stage stage = new Stage();
        stage.setScene(new Scene(vista));
        stage.setTitle("Editar Producto");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();

        cargarProductos();
      } catch (IOException ex) {
        Alertas.crearAlerta(Alert.AlertType.ERROR, "Error al abrir el formulario",
            "Error al abrir el formulario de edición: " + ex.getMessage());
      }
    } else {
      Alertas.crearAlerta(Alert.AlertType.WARNING, "Selección de producto",
          "Debe seleccionar un producto para editar.");
    }
  }

  private void eliminarProducto(ActionEvent event) {
    Bebida bebidaSeleccionada = tvProductos.getSelectionModel().getSelectedItem();

    if (bebidaSeleccionada != null) {
      Optional<ButtonType> respuesta = mostrarConfirmacion("¿Está seguro que desea eliminar el producto '" +
          bebidaSeleccionada.getNombre() + "'?");

      if (respuesta.isPresent() && respuesta.get() == ButtonType.OK) {
        try {
          boolean eliminacionExitosa = bebidaDAO.eliminar(bebidaSeleccionada.getId());

          if (eliminacionExitosa) {
            Alertas.crearAlerta(Alert.AlertType.INFORMATION, "Producto eliminado",
                "El producto '" + bebidaSeleccionada.getNombre() + "' ha sido eliminado.");
            cargarProductos();
          } else {
            Alertas.crearAlerta(Alert.AlertType.ERROR, "Error al eliminar producto",
                "No se pudo eliminar el producto '" + bebidaSeleccionada.getNombre() + "'.");
          }
        } catch (SQLException ex) {
          Alertas.crearAlerta(Alert.AlertType.ERROR, "Error al eliminar producto",
              "Error al eliminar el producto: " + ex.getMessage());
        }
      }
    } else {
      Alertas.crearAlerta(Alert.AlertType.WARNING, "Selección de producto",
          "Debe seleccionar un producto para eliminar.");
    }
  }

  private void regresar(ActionEvent event) {
    principalController.restaurarVistaPrincipal();
  }

  private Optional<ButtonType> mostrarConfirmacion(String mensaje) {
    Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
    alerta.setTitle("Confirmación");
    alerta.setHeaderText(null);
    alerta.setContentText(mensaje);
    return alerta.showAndWait();
  }
}