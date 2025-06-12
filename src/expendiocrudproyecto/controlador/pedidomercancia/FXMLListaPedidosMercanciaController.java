package expendiocrudproyecto.controlador.pedidomercancia;

import expendiocrudproyecto.controlador.FXMLPrincipalController;
import expendiocrudproyecto.modelo.dao.PedidoMercanciaDAO;
import expendiocrudproyecto.modelo.pojo.PedidoMercancia;
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

public class FXMLListaPedidosMercanciaController
    implements Initializable, FXMLPrincipalController.ControladorConUsuario {

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
  private TableView<PedidoMercancia> tvPedidosMercancia;
  @FXML
  private TableColumn<PedidoMercancia, String> tcFolio;
  @FXML
  private TableColumn<PedidoMercancia, Date> tcFecha;
  @FXML
  private TableColumn<PedidoMercancia, String> tcProveedor;
  @FXML
  private TableColumn<PedidoMercancia, Double> tcTotal;
  @FXML
  private TableColumn<PedidoMercancia, String> tcEstado;
  @FXML
  private TableColumn<PedidoMercancia, Void> tcAcciones;
  @FXML
  private Button btnRegresar;

  private Usuario usuarioSesion;
  private ObservableList<PedidoMercancia> pedidos;
  private PedidoMercanciaDAO pedidoMercanciaDAO;
  private FXMLPrincipalController principalController;

  @Override
  public void initialize(URL url, ResourceBundle rb) {
    pedidoMercanciaDAO = new PedidoMercanciaDAO();
    pedidos = FXCollections.observableArrayList();
    configurarTabla();

    // Valores por defecto: desde un mes antes hasta un mes después
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
    tcProveedor.setCellValueFactory(new PropertyValueFactory<>("nombreProveedor"));
    tcTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
    tcEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

    // Fecha con formato dd/MM/yyyy
    tcFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
    tcFecha.setCellFactory(column -> new TableCell<PedidoMercancia, Date>() {
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

    // Columna Acciones
    tcAcciones.setCellFactory(col -> new TableCell<PedidoMercancia, Void>() {
      private final Button btnConf = new Button("Confirmar");
      private final Button btnCanc = new Button("Cancelar");
      private final HBox hbox = new HBox(5, btnConf, btnCanc);

      {
        btnConf.setOnAction(e -> {
          PedidoMercancia pedido = getTableView().getItems().get(getIndex());
          btnConf.setDisable(true);
          btnCanc.setDisable(true);
          actualizarEstadoPedido(pedido, 3); // 3 = Recibido
        });

        btnCanc.setOnAction(e -> {
          PedidoMercancia pedido = getTableView().getItems().get(getIndex());
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
          PedidoMercancia pedido = getTableView().getItems().get(getIndex());
          String estado = pedido.getEstado() == null ? "" : pedido.getEstado().toLowerCase();
          if (estado.equals("solicitado") || estado.equals("pendiente")) {
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
      pedidos.setAll(pedidoMercanciaDAO.leerTodo());
      aplicarFiltros();
    } catch (SQLException e) {
      // Manejar excepcion
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
          getClass().getResource("/expendiocrudproyecto/vista/pedidomercancia/FXMLFormularioPedidoMercancia.fxml"));
      Parent vista = loader.load();

      FXMLFormularioPedidoMercanciaController controller = loader.getController();
      controller.inicializar(usuarioSesion, principalController);

      Stage stage = new Stage();
      stage.setScene(new Scene(vista));
      stage.setTitle("Nuevo Pedido de Mercancía");
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

  /**
   * Aplica los filtros de texto y rango de fechas a la lista completa y actualiza
   * la tabla.
   */
  private void aplicarFiltros() {
    String criterio = tfBusqueda.getText() == null ? "" : tfBusqueda.getText().trim().toLowerCase();
    LocalDate inicio = dpFechaInicio.getValue();
    LocalDate fin = dpFechaFin.getValue();

    // Convertir a stream, filtrar y actualizar la vista
    ObservableList<PedidoMercancia> filtrados = FXCollections.observableArrayList(
        pedidos.stream()
            .filter(p -> {
              // Filtrado por texto en folio o proveedor
              boolean coincideTexto = criterio.isEmpty() ||
                  (p.getFolio() != null && p.getFolio().toLowerCase().contains(criterio)) ||
                  (p.getNombreProveedor() != null && p.getNombreProveedor().toLowerCase().contains(criterio));

              // Filtrado por rango de fechas
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

    tvPedidosMercancia.setItems(filtrados);
  }

  private void actualizarEstadoPedido(PedidoMercancia pedido, int idEstatus) {
    try {
      boolean exito = pedidoMercanciaDAO.cambiarEstatus(pedido.getIdPedido(), idEstatus);
      if (exito) {
        cargarPedidos();
      }
    } catch (SQLException ex) {
      ex.printStackTrace();
    }
  }
}