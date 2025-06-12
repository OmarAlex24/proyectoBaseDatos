package expendiocrudproyecto.controlador.pedidocliente;

import expendiocrudproyecto.controlador.FXMLPrincipalController;
import expendiocrudproyecto.modelo.dao.PedidoClienteDAO;
import expendiocrudproyecto.modelo.pojo.PedidoCliente;
import expendiocrudproyecto.modelo.pojo.Usuario;
import expendiocrudproyecto.utilidades.UtilPantallas;
import java.net.URL;
import java.sql.SQLException;
import java.util.Date;
import java.util.ResourceBundle;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableCell;
import java.text.SimpleDateFormat;
import javafx.scene.layout.HBox;

public class FXMLListaPedidosClienteController implements Initializable, FXMLPrincipalController.ControladorConUsuario {

  @FXML
  private TextField tfBusqueda;
  @FXML
  private DatePicker dpFechaInicio;
  @FXML
  private DatePicker dpFechaFin;
  @FXML
  private Button btnBuscar;
  @FXML
  private Button btnNuevoPedido;
  @FXML
  private TableView<PedidoCliente> tvPedidosCliente;
  @FXML
  private TableColumn<PedidoCliente, String> tcFolio;
  @FXML
  private TableColumn<PedidoCliente, Date> tcFecha;
  @FXML
  private TableColumn<PedidoCliente, String> tcCliente;
  @FXML
  private TableColumn<PedidoCliente, Double> tcTotal;
  @FXML
  private TableColumn<PedidoCliente, String> tcEstado;
  @FXML
  private TableColumn<PedidoCliente, Void> tcAcciones;
  @FXML
  private Button btnRegresar;

  private Usuario usuarioSesion;
  private ObservableList<PedidoCliente> pedidos;
  private PedidoClienteDAO pedidoClienteDAO;
  private FXMLPrincipalController principalController;

  @Override
  public void initialize(URL url, ResourceBundle rb) {
    pedidoClienteDAO = new PedidoClienteDAO();
    pedidos = FXCollections.observableArrayList();
    configurarTabla();

    dpFechaInicio.setValue(LocalDate.now().minusMonths(1));
    dpFechaFin.setValue(LocalDate.now().plusMonths(1));

    btnBuscar.setOnAction(this::buscarPedidos);
    btnNuevoPedido.setOnAction(this::nuevoPedido);
    btnRegresar.setOnAction(this::regresar);
  }

  @Override
  public void inicializar(Usuario usuario, FXMLPrincipalController principalController) {
    this.usuarioSesion = usuario;
    this.principalController = principalController;
    cargarPedidos();
  }

  private void configurarTabla() {
    tcFolio.setCellValueFactory(new PropertyValueFactory<>("folio"));
    tcCliente.setCellValueFactory(new PropertyValueFactory<>("nombreCliente"));
    tcTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
    tcEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

    tcFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
    tcFecha.setCellFactory(column -> new TableCell<PedidoCliente, Date>() {
      private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

      @Override
      protected void updateItem(Date item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
          setText(null);
        } else {
          setText(sdf.format(item));
        }
      }
    });

    // Columna Acciones con botones Confirmar / Cancelar
    tcAcciones.setCellFactory(col -> new TableCell<PedidoCliente, Void>() {
      private final Button btnConf = new Button("Entregado");
      private final Button btnCanc = new Button("Cancelar");
      private final HBox hbox = new HBox(5, btnConf, btnCanc);

      {
        btnConf.setOnAction(e -> {
          PedidoCliente pedido = getTableView().getItems().get(getIndex());
          btnConf.setDisable(true);
          btnCanc.setDisable(true);
          actualizarEstadoPedido(pedido, 3); // 3 = Entregado
        });

        btnCanc.setOnAction(e -> {
          PedidoCliente pedido = getTableView().getItems().get(getIndex());
          btnConf.setDisable(true);
          btnCanc.setDisable(true);
          actualizarEstadoPedido(pedido, 2); // 2 = Cancelado
        });
      }

      @Override
      protected void updateItem(Void item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
          setGraphic(null);
        } else {
          PedidoCliente pedido = getTableView().getItems().get(getIndex());
          String estado = pedido.getEstado() == null ? "" : pedido.getEstado().toLowerCase();
          if (estado.equals("pendiente") || estado.equals("solicitado")) {
            setGraphic(hbox);
            btnConf.setDisable(false);
            btnCanc.setDisable(false);
          } else {
            setGraphic(null);
          }
        }
      }
    });
  }

  private void cargarPedidos() {
    try {
      pedidos.setAll(pedidoClienteDAO.leerTodo());
      aplicarFiltros();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @FXML
  private void buscarPedidos(ActionEvent event) {
    aplicarFiltros();
  }

  @FXML
  private void nuevoPedido(ActionEvent event) {
    try {
      FXMLLoader loader = new FXMLLoader(
          getClass().getResource("/expendiocrudproyecto/vista/pedidocliente/FXMLFormularioPedidoCliente.fxml"));
      Parent vista = loader.load();

      FXMLFormularioPedidoClienteController controller = loader.getController();
      controller.inicializar(usuarioSesion, principalController);

      Stage stage = new Stage();
      stage.setScene(new Scene(vista));
      stage.setTitle("Nuevo Pedido a Cliente");
      stage.initModality(Modality.APPLICATION_MODAL);
      stage.showAndWait();

      cargarPedidos();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @FXML
  private void regresar(ActionEvent event) {
    principalController.restaurarVistaPrincipal();
  }

  private void aplicarFiltros() {
    String criterio = tfBusqueda.getText() == null ? "" : tfBusqueda.getText().trim().toLowerCase();
    LocalDate inicio = dpFechaInicio.getValue().minusMonths(1);
    LocalDate fin = dpFechaFin.getValue().plusMonths(1);

    ObservableList<PedidoCliente> filtrados = FXCollections.observableArrayList(
        pedidos.stream()
            .filter(p -> {
              boolean coincideTexto = criterio.isEmpty() ||
                  (p.getFolio() != null && p.getFolio().toLowerCase().contains(criterio)) ||
                  (p.getNombreCliente() != null && p.getNombreCliente().toLowerCase().contains(criterio));

              if (p.getFecha() == null)
                return false;

              LocalDate fechaPedido = p.getFecha().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
              boolean dentroRango = true;
              if (inicio != null) {
                dentroRango &= !fechaPedido.isBefore(inicio);
              }
              if (fin != null) {
                dentroRango &= !fechaPedido.isAfter(fin);
              }

              return coincideTexto && dentroRango;
            })
            .collect(Collectors.toList()));

    tvPedidosCliente.setItems(filtrados);
  }

  private void actualizarEstadoPedido(PedidoCliente pedido, int idEstatus) {
    try {
      boolean exito = pedidoClienteDAO.cambiarEstatus(pedido.getIdPedido(), idEstatus);
      if (exito) {
        cargarPedidos();
      }
    } catch (SQLException ex) {
      ex.printStackTrace();
    }
  }
}