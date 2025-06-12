package expendiocrudproyecto.controlador.pedidocliente;

import expendiocrudproyecto.controlador.FXMLPrincipalController;
import expendiocrudproyecto.modelo.pojo.Bebida;
import expendiocrudproyecto.modelo.pojo.Cliente;
import expendiocrudproyecto.modelo.pojo.DetalleVenta;
import expendiocrudproyecto.modelo.pojo.Usuario;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import java.sql.SQLException;
import java.time.LocalDate;
import javafx.collections.FXCollections;
import expendiocrudproyecto.modelo.dao.BebidaDAO;
import expendiocrudproyecto.modelo.dao.ClienteDAO;
import expendiocrudproyecto.modelo.dao.PedidoClienteDAO;
import expendiocrudproyecto.utilidades.Alertas;
import javafx.scene.control.Alert;
import javafx.scene.control.SpinnerValueFactory;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.control.TableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.IntegerStringConverter;

public class FXMLFormularioPedidoClienteController
    implements Initializable, FXMLPrincipalController.ControladorConUsuario {

  @FXML
  private DatePicker dpFechaPedido;
  @FXML
  private ComboBox<Cliente> cbCliente;
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
  private TableColumn<DetalleVenta, Double> tcPrecioUnitario;
  @FXML
  private TableColumn<DetalleVenta, Integer> tcCantidad;
  @FXML
  private TableColumn<DetalleVenta, Double> tcSubtotal;
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
  private ObservableList<DetalleVenta> detallesPedido;
  private ObservableList<Bebida> productos = FXCollections.observableArrayList();
  private ObservableList<Cliente> clientes = FXCollections.observableArrayList();
  private BebidaDAO bebidaDAO = new BebidaDAO();
  private ClienteDAO clienteDAO = new ClienteDAO();
  private PedidoClienteDAO pedidoDAO = new PedidoClienteDAO();

  @Override
  public void initialize(URL url, ResourceBundle rb) {
    dpFechaPedido.setValue(LocalDate.now());

    spCantidad.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 9999, 1));

    detallesPedido = FXCollections.observableArrayList();
    tvDetallePedido.setItems(detallesPedido);
    tvDetallePedido.setEditable(true);

    btnAgregarProducto.setOnAction(this::agregarProducto);
    btnRegistrarPedido.setOnAction(this::registrarPedido);
    btnCancelar.setOnAction(this::cancelar);

    configurarTabla();

    cargarClientes();
    cargarProductos();
  }

  @Override
  public void inicializar(Usuario usuario, FXMLPrincipalController principalController) {
    this.usuarioSesion = usuario;
    this.principalController = principalController;
    lblEmpleado.setText(usuario.getUsername());
  }

  @FXML
  private void agregarProducto(ActionEvent event) {
    Bebida productoSel = cbProducto.getValue();
    if (productoSel == null) {
      Alertas.crearAlerta(Alert.AlertType.WARNING, "Producto no seleccionado", "Por favor, seleccione un producto de la lista.");
      return;
    }

    int cantidadSolicitada = spCantidad.getValue();
    if (cantidadSolicitada <= 0) {
      Alertas.crearAlerta(Alert.AlertType.WARNING, "Cantidad inválida", "La cantidad debe ser mayor a 0.");
      return;
    }

    DetalleVenta detalleExistente = null;
    for (DetalleVenta d : detallesPedido) {
      if (d.getIdProducto() == productoSel.getId()) {
        detalleExistente = d;
        break;
      }
    }

    int cantidadPrevia = (detalleExistente != null) ? detalleExistente.getCantidadUnitaria() : 0;
    int cantidadTotal = cantidadPrevia + cantidadSolicitada;

    if (cantidadTotal > productoSel.getStock()) {
      Alertas.crearAlerta(Alert.AlertType.ERROR, "Stock insuficiente",
              "No hay suficiente stock para '" + productoSel.getNombre() + "'.\n" +
                      "Stock disponible: " + productoSel.getStock() + "\n" +
                      "Usted intenta solicitar: " + cantidadTotal);
      return;
    }

    if (detalleExistente != null) {
      detalleExistente.setCantidadUnitaria(cantidadTotal);
      tvDetallePedido.refresh();
    } else {
      DetalleVenta nuevoDetalle = new DetalleVenta();
      nuevoDetalle.setBebida(productoSel);
      nuevoDetalle.setIdProducto(productoSel.getId());
      nuevoDetalle.setPrecioUnitario(productoSel.getPrecio());
      nuevoDetalle.setCantidadUnitaria(cantidadSolicitada);
      detallesPedido.add(nuevoDetalle);
    }
    actualizarTotal();
  }

  @FXML
  private void registrarPedido(ActionEvent event) {
    Cliente cli = cbCliente.getValue();
    if (cli == null) {
      Alertas.crearAlerta(Alert.AlertType.WARNING, "Cliente", "Seleccione un cliente");
      return;
    }
    if (detallesPedido.isEmpty()) {
      Alertas.crearAlerta(Alert.AlertType.WARNING, "Productos", "Agregue al menos un producto al pedido");
      return;
    }

    try {
      boolean ok = pedidoDAO.registrarPedido(dpFechaPedido.getValue(), cli.getIdCliente(), detallesPedido);
      if (ok) {
        Alertas.crearAlerta(Alert.AlertType.INFORMATION, "Éxito", "Pedido registrado y stock reservado correctamente.");
        principalController.restaurarVistaPrincipal();
      }
    } catch (SQLException ex) {
      Alertas.crearAlerta(Alert.AlertType.ERROR, "Error al registrar el pedido", ex.getMessage());
    }
  }
  private void configurarTabla() {
    tcProducto.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getBebida().getNombre()));
    tcPrecioUnitario.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getPrecioUnitario()));
    tcCantidad.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getCantidadUnitaria()));
    tcSubtotal.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(
        data.getValue().getPrecioUnitario() * data.getValue().getCantidadUnitaria()));

    tcAcciones.setCellFactory(col -> new TableCell<DetalleVenta, Void>() {
      private final Button btn = new Button("Eliminar");

      {
        btn.setOnAction(evt -> {
          DetalleVenta d = getTableView().getItems().get(getIndex());
          detallesPedido.remove(d);
          actualizarTotal();
        });
      }

      @Override
      protected void updateItem(Void item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
          setGraphic(null);
        } else {
          setGraphic(btn);
        }
      }
    });

    tcCantidad.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
    tcCantidad.setOnEditCommit(ev -> {
      DetalleVenta d = ev.getRowValue();
      int nueva = ev.getNewValue();
      if (nueva <= 0) {
        Alertas.crearAlerta(Alert.AlertType.WARNING, "Cantidad inválida", "Debe ser > 0");
        tvDetallePedido.refresh();
        return;
      }
      d.setCantidadUnitaria(nueva);
      actualizarTotal();
    });
  }

  private void actualizarTotal() {
    double total = detallesPedido.stream()
        .mapToDouble(d -> d.getPrecioUnitario() * d.getCantidadUnitaria())
        .sum();
    lblTotal.setText(String.format("$%.2f", total));
  }

  private void cargarClientes() {
    try {
      clientes.setAll(clienteDAO.leerTodo());
      cbCliente.setItems(clientes);
    } catch (SQLException ex) {
      Alertas.crearAlerta(Alert.AlertType.ERROR, "Error cargando clientes", ex.getMessage());
    }
  }

  private void cargarProductos() {
    try {
      productos.setAll(bebidaDAO.leerTodo());
      cbProducto.setItems(productos);
    } catch (SQLException ex) {
      Alertas.crearAlerta(Alert.AlertType.ERROR, "Error cargando productos", ex.getMessage());
    }
  }

  @FXML
  private void cancelar(ActionEvent event) {
    principalController.restaurarVistaPrincipal();
  }
}