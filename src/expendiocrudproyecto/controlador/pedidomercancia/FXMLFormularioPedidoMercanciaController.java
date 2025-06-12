package expendiocrudproyecto.controlador.pedidomercancia;

import expendiocrudproyecto.controlador.FXMLPrincipalController;
import expendiocrudproyecto.modelo.pojo.Bebida;
import expendiocrudproyecto.modelo.pojo.DetalleVenta;
import expendiocrudproyecto.modelo.pojo.Proveedor;
import expendiocrudproyecto.modelo.pojo.Usuario;
import expendiocrudproyecto.modelo.dao.BebidaDAO;
import expendiocrudproyecto.modelo.dao.ProveedorDAO;
import expendiocrudproyecto.modelo.dao.PedidoMercanciaDAO;
import expendiocrudproyecto.utilidades.Alertas;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert;
import javafx.scene.control.SpinnerValueFactory;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.control.TableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.IntegerStringConverter;
import java.io.IOException;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

public class FXMLFormularioPedidoMercanciaController
    implements Initializable, FXMLPrincipalController.ControladorConUsuario {

  @FXML
  private DatePicker dpFechaPedido;
  @FXML
  private ComboBox<Proveedor> cbProveedor;
  @FXML
  private Label lblEmpleado;
  @FXML
  private ComboBox<Bebida> cbProducto;
  @FXML
  private Spinner<Integer> spCantidad;
  @FXML
  private Button btnAgregarProducto;
  @FXML
  private TableView<DetalleVenta> tvDetallePedido;
  @FXML
  private TableColumn<DetalleVenta, String> tcProducto;
  @FXML
  private TableColumn<DetalleVenta, Number> tcPrecioUnitario;
  @FXML
  private TableColumn<DetalleVenta, Integer> tcCantidad;
  @FXML
  private TableColumn<DetalleVenta, Number> tcSubtotal;
  @FXML
  private TableColumn<DetalleVenta, Void> tcAcciones;
  @FXML
  private Label lblTotal;
  @FXML
  private Button btnRegistrarPedido;
  @FXML
  private Button btnCancelar;

  private Usuario usuarioSesion;
  private FXMLPrincipalController principalController;
  private ObservableList<DetalleVenta> detallesPedido = FXCollections.observableArrayList();
  private ProveedorDAO proveedorDAO = new ProveedorDAO();
  private BebidaDAO bebidaDAO = new BebidaDAO();
  private PedidoMercanciaDAO pedidoDAO = new PedidoMercanciaDAO();
  private ObservableList<DetalleVenta> itemsTabla = FXCollections.observableArrayList();

  @Override
  public void initialize(URL url, ResourceBundle rb) {
    // Establecer la fecha actual
    dpFechaPedido.setValue(LocalDate.now());

    // Configurar spinner de cantidad (mín 1, máx 9999, valor inicial 1)
    spCantidad.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 9999, 1));

    // Ocultar controles de selección antigua
    cbProducto.setVisible(false);
    cbProducto.setManaged(false);
    spCantidad.setVisible(false);
    spCantidad.setManaged(false);

    // Asignar acción al nuevo botón para abrir la ventana de selección
    btnAgregarProducto.setOnAction(this::agregarProducto);

    // Acciones para registrar y cancelar
    btnRegistrarPedido.setOnAction(this::registrarPedido);
    btnCancelar.setOnAction(this::cancelar);

    // Cargar proveedores al iniciar
    cargarProveedores();

    // Listener para cuando se seleccione un proveedor
    cbProveedor.setOnAction(event -> cargarProductosBajoStock());

    // Configurar columnas de la tabla
    configurarTabla();

    tvDetallePedido.setEditable(true);
  }

  @Override
  public void inicializar(Usuario usuario, FXMLPrincipalController principalController) {
    this.usuarioSesion = usuario;
    this.principalController = principalController;

    lblEmpleado.setText(usuario.getUsername());
  }

  private void cargarProveedores() {
    try {
      ObservableList<Proveedor> lista = FXCollections.observableArrayList(proveedorDAO.leerTodo());
      cbProveedor.setItems(lista);
    } catch (SQLException ex) {
      Alertas.crearAlerta(Alert.AlertType.ERROR, "Error al cargar proveedores", ex.getMessage());
    }
  }

  private void cargarProductosBajoStock() {
    try {
      itemsTabla.clear();
      detallesPedido.clear();
      actualizarTotal();

      for (Bebida beb : bebidaDAO.leerBajoStock()) {
        DetalleVenta dv = new DetalleVenta();
        dv.setBebida(beb);
        dv.setIdProducto(beb.getId());
        dv.setPrecioUnitario(beb.getPrecio());
        dv.setCantidadUnitaria(1);
        itemsTabla.add(dv);
      }
      tvDetallePedido.setItems(itemsTabla);
    } catch (SQLException ex) {
      Alertas.crearAlerta(Alert.AlertType.ERROR, "Error al cargar productos", ex.getMessage());
    }
  }

  @FXML
  private void agregarProducto(ActionEvent event) {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource(
          "/expendiocrudproyecto/vista/pedidomercancia/FXMLSeleccionarProducto.fxml"));
      Parent vista = loader.load();

      FXMLSeleccionarProductoController controller = loader.getController();
      controller.inicializar(this, usuarioSesion);

      Stage stage = new Stage();
      stage.setScene(new Scene(vista));
      stage.setTitle("Seleccionar Producto");
      stage.initModality(Modality.APPLICATION_MODAL);
      stage.showAndWait();
    } catch (IOException ex) {
      Alertas.crearAlerta(Alert.AlertType.ERROR, "Error",
          "No se pudo abrir la ventana de selección: " + ex.getMessage());
    }
  }

  @FXML
  private void registrarPedido(ActionEvent event) {
    Proveedor proveedor = cbProveedor.getValue();
    if (proveedor == null) {
      Alertas.crearAlerta(Alert.AlertType.WARNING, "Proveedor", "Seleccione un proveedor");
      return;
    }

    if (detallesPedido.isEmpty()) {
      Alertas.crearAlerta(Alert.AlertType.WARNING, "Productos", "Agregue al menos un producto al pedido");
      return;
    }

    try {
      boolean ok = pedidoDAO.registrarPedido(dpFechaPedido.getValue(), proveedor.getIdProveedor(), detallesPedido);
      if (ok) {
        Alertas.crearAlerta(Alert.AlertType.INFORMATION, "Éxito", "Pedido registrado correctamente");
        principalController.restaurarVistaPrincipal();
      } else {
        Alertas.crearAlerta(Alert.AlertType.ERROR, "Error", "No se pudo registrar el pedido");
      }
    } catch (SQLException ex) {
      Alertas.crearAlerta(Alert.AlertType.ERROR, "Error SQL", ex.getMessage());
    }
  }

  @FXML
  private void cancelar(ActionEvent event) {
    principalController.restaurarVistaPrincipal();
  }

  private void configurarTabla() {
    tcProducto.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getBebida().getNombre()));
    tcPrecioUnitario.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getPrecioUnitario()));
    tcCantidad.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getCantidadUnitaria()));
    tcSubtotal.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(
        data.getValue().getPrecioUnitario() * data.getValue().getCantidadUnitaria()));

    tcAcciones.setCellFactory(col -> new TableCell<DetalleVenta, Void>() {
      private final Button btn = new Button();

      {
        btn.setOnAction(evt -> {
          DetalleVenta detalle = getTableView().getItems().get(getIndex());
          if (!detallesPedido.contains(detalle)) {
            detallesPedido.add(detalle);
            btn.setText("Eliminar");
          } else {
            detallesPedido.remove(detalle);
            btn.setText("Agregar al pedido");
          }
          actualizarTotal();
        });
      }

      @Override
      protected void updateItem(Void item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
          setGraphic(null);
        } else {
          DetalleVenta detalle = getTableView().getItems().get(getIndex());
          btn.setText(detallesPedido.contains(detalle) ? "Eliminar" : "Agregar al pedido");
          setGraphic(btn);
        }
      }
    });

    // Hacer la columna cantidad editable
    tcCantidad.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));

    tcCantidad.setOnEditCommit(event -> {
      DetalleVenta detalle = event.getRowValue();
      int nuevaCantidad = event.getNewValue();
      if (nuevaCantidad <= 0) {
        Alertas.crearAlerta(Alert.AlertType.WARNING, "Cantidad inválida", "La cantidad debe ser mayor a 0");
        tvDetallePedido.refresh();
        return;
      }
      detalle.setCantidadUnitaria(nuevaCantidad);
      actualizarTotal();
    });
  }

  private void actualizarTotal() {
    double total = detallesPedido.stream()
        .mapToDouble(d -> d.getPrecioUnitario() * d.getCantidadUnitaria())
        .sum();
    lblTotal.setText(String.format("$%.2f", total));
  }

  // Método llamado desde la ventana de selección
  public void agregarProductoDesdeVentana(Bebida bebida) {
    // Verificar si ya existe
    for (DetalleVenta d : detallesPedido) {
      if (d.getIdProducto() == bebida.getId()) {
        d.setCantidadUnitaria(d.getCantidadUnitaria() + 1);
        tvDetallePedido.refresh();
        actualizarTotal();
        return;
      }
    }

    DetalleVenta dv = new DetalleVenta();
    dv.setBebida(bebida);
    dv.setIdProducto(bebida.getId());
    dv.setPrecioUnitario(bebida.getPrecio());
    dv.setCantidadUnitaria(1);

    detallesPedido.add(dv);
    itemsTabla.add(dv);
    tvDetallePedido.refresh();
    actualizarTotal();
  }
}