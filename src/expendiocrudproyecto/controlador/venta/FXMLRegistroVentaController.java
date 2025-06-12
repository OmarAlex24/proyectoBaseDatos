package expendiocrudproyecto.controlador.venta;

import expendiocrudproyecto.controlador.FXMLPrincipalController;
import expendiocrudproyecto.modelo.dao.BebidaDAO;
import expendiocrudproyecto.modelo.dao.PromocionDAO;
import expendiocrudproyecto.modelo.dao.VentaDAO;
import expendiocrudproyecto.modelo.pojo.Bebida;
import expendiocrudproyecto.modelo.pojo.Cliente;
import expendiocrudproyecto.modelo.pojo.DetalleVenta;
import expendiocrudproyecto.modelo.pojo.Promocion;
import expendiocrudproyecto.modelo.pojo.Usuario;
import expendiocrudproyecto.modelo.pojo.Venta;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import expendiocrudproyecto.utilidades.Alertas;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class FXMLRegistroVentaController implements Initializable, FXMLPrincipalController.ControladorConUsuario {

  @FXML
  private DatePicker dpFechaVenta;
  @FXML
  private ComboBox<Cliente> cbCliente;
  @FXML
  private Label lblEmpleado;
  @FXML
  private ComboBox<Promocion> cbPromocion;
  @FXML
  private ComboBox<Bebida> cbProducto;
  @FXML
  private Spinner<Integer> spCantidad;
  @FXML
  private Button btnAgregarProducto;
  @FXML
  private TableView<DetalleVenta> tvDetalleVenta;
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
  private Label lblSubtotal;
  @FXML
  private Label lblDescuento;
  @FXML
  private Label lblTotal;
  @FXML
  private Button btnRegistrarVenta;
  @FXML
  private Button btnCancelar;

  private Usuario usuarioSesion;
  private FXMLPrincipalController principalController;
  private ObservableList<Cliente> clientes;
  private ObservableList<Promocion> promociones;
  private ObservableList<Bebida> productos;
  private ObservableList<DetalleVenta> detallesVenta;
  private BebidaDAO bebidaDAO;
  private PromocionDAO promocionDAO;
  private VentaDAO ventaDAO;

  private double subtotal = 0.0;
  private double descuento = 0.0;
  private double total = 0.0;

  @Override
  public void initialize(URL url, ResourceBundle rb) {
    configurarFechaActual();
    configurarSpinner();
    configurarTabla();
    configurarBotones();

    bebidaDAO = new BebidaDAO();
    promocionDAO = new PromocionDAO();
    ventaDAO = new VentaDAO();

    clientes = FXCollections.observableArrayList();
    promociones = FXCollections.observableArrayList();
    productos = FXCollections.observableArrayList();
    detallesVenta = FXCollections.observableArrayList();

    tvDetalleVenta.setItems(detallesVenta);

    // Configurar conversores para los ComboBox
    configurarConversorClientes();
    configurarConversorPromociones();
    configurarConversorProductos();

    // Cargar datos iniciales
    cargarClientes();
    cargarPromociones();
    cargarProductos();

    // Listener para actualizar descuento cuando cambia la promoción
    cbPromocion.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
      actualizarDescuento();
    });
  }

  @Override
  public void inicializar(Usuario usuario, FXMLPrincipalController principalController) {
    this.usuarioSesion = usuario;
    this.principalController = principalController;
    lblEmpleado.setText(usuario.getUsername());
  }

  private void configurarFechaActual() {
    dpFechaVenta.setValue(LocalDate.now());
  }

  private void configurarSpinner() {
    SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1);
    spCantidad.setValueFactory(valueFactory);
  }

  private void configurarTabla() {
    tcProducto.setCellValueFactory(cellData -> {
      Bebida bebida = cellData.getValue().getBebida();
      return new SimpleStringProperty(bebida != null ? bebida.getNombre() : "");
    });

    tcPrecioUnitario
        .setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getPrecioUnitario()).asObject());

    tcCantidad.setCellValueFactory(
        cellData -> new SimpleIntegerProperty(cellData.getValue().getCantidadUnitaria()).asObject());

    tcSubtotal
        .setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getTotal_pagado()).asObject());

    // Configurar formato para columnas de moneda
    tcPrecioUnitario.setCellFactory(tc -> new TableCell<DetalleVenta, Double>() {
      @Override
      protected void updateItem(Double item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
          setText(null);
        } else {
          setText(String.format("$%.2f", item));
        }
      }
    });

    tcSubtotal.setCellFactory(tc -> new TableCell<DetalleVenta, Double>() {
      @Override
      protected void updateItem(Double item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
          setText(null);
        } else {
          setText(String.format("$%.2f", item));
        }
      }
    });

    // Configurar columna de acciones (botón eliminar)
    tcAcciones.setCellFactory(param -> new TableCell<DetalleVenta, Void>() {
      private final Button btnEliminar = new Button("Eliminar");

      {
        btnEliminar.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        btnEliminar.setOnAction(event -> {
          DetalleVenta detalle = getTableView().getItems().get(getIndex());
          eliminarDetalle(detalle);
        });
      }

      @Override
      protected void updateItem(Void item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
          setGraphic(null);
        } else {
          setGraphic(btnEliminar);
        }
      }
    });
  }

  private void configurarBotones() {
    btnAgregarProducto.setOnAction(this::agregarProducto);
    btnRegistrarVenta.setOnAction(this::registrarVenta);
    btnCancelar.setOnAction(this::cancelar);
  }

  private void configurarConversorClientes() {
    cbCliente.setConverter(new StringConverter<Cliente>() {
      @Override
      public String toString(Cliente cliente) {
        return cliente != null ? cliente.getNombre() : "";
      }

      @Override
      public Cliente fromString(String string) {
        return null; // No se necesita para este caso
      }
    });
  }

  private void configurarConversorPromociones() {
    cbPromocion.setConverter(new StringConverter<Promocion>() {
      @Override
      public String toString(Promocion promocion) {
        return promocion != null ? promocion.getNombre() + " (" + promocion.getDescuento() + "%)" : "Sin promoción";
      }

      @Override
      public Promocion fromString(String string) {
        return null; // No se necesita para este caso
      }
    });
  }

  private void configurarConversorProductos() {
    cbProducto.setConverter(new StringConverter<Bebida>() {
      @Override
      public String toString(Bebida bebida) {
        return bebida != null ? bebida.getNombre() + " - $" + bebida.getPrecio() : "";
      }

      @Override
      public Bebida fromString(String string) {
        return null; // No se necesita para este caso
      }
    });
  }

  private void cargarClientes() {
    try {
      List<Cliente> listaClientes = ventaDAO.obtenerClientes();
      clientes.clear();
      clientes.addAll(listaClientes);
      cbCliente.setItems(clientes);
      cbCliente.getSelectionModel().select(0); // Seleccionar "Cliente general" por defecto
    } catch (SQLException ex) {
      Alertas.crearAlerta(Alert.AlertType.ERROR, "Error", "Error al cargar los clientes: " + ex.getMessage());
    }
  }

  private void cargarPromociones() {
    try {
      List<Promocion> listaPromociones = promocionDAO.leerPromocionesVigentes();
      promociones.clear();

      // Agregar opción "Sin promoción"
      Promocion sinPromocion = new Promocion();
      sinPromocion.setIdPromocion(0);
      sinPromocion.setNombre("Sin promoción");
      sinPromocion.setDescuento(0);
      promociones.add(sinPromocion);

      promociones.addAll(listaPromociones);
      cbPromocion.setItems(promociones);
      cbPromocion.getSelectionModel().select(0); // Seleccionar "Sin promoción" por defecto
    } catch (SQLException ex) {
      Alertas.crearAlerta(
          Alert.AlertType.ERROR, "Error", "Error al cargar las promociones: " + ex.getMessage());
    }
  }

  private void cargarProductos() {
    try {
      List<Bebida> listaProductos = bebidaDAO.leerTodo();
      productos.clear();

      // Filtrar productos con stock > 0
      for (Bebida bebida : listaProductos) {
        if (bebida.getStock() > 0) {
          productos.add(bebida);
        }
      }

      cbProducto.setItems(productos);
      if (!productos.isEmpty()) {
        cbProducto.getSelectionModel().select(0);
      }
    } catch (SQLException ex) {
      Alertas.crearAlerta(Alert.AlertType.ERROR, "Error", "Error al cargar los productos: " + ex.getMessage());
    }
  }

  private void agregarProducto(ActionEvent event) {
    Bebida bebidaSeleccionada = cbProducto.getSelectionModel().getSelectedItem();

    if (bebidaSeleccionada == null) {
      Alertas.crearAlerta(
          Alert.AlertType.WARNING, "Advertencia", "Seleccione un producto.");
      return;
    }

    int cantidad = spCantidad.getValue();

    if (cantidad <= 0) {
      Alertas.crearAlerta(
          Alert.AlertType.WARNING, "Advertencia", "La cantidad debe ser mayor a 0.");
      return;
    }

    // Verificar si hay suficiente stock
    if (bebidaSeleccionada.getStock() < cantidad) {
      Alertas.crearAlerta(
          Alert.AlertType.WARNING, "Advertencia",
          "No hay suficiente stock. Stock disponible: " + bebidaSeleccionada.getStock());
      return;
    }

    // Verificar si el producto ya está en la lista
    for (DetalleVenta detalle : detallesVenta) {
      if (detalle.getBebida() != null && detalle.getBebida().getId() == bebidaSeleccionada.getId()) {
        // Actualizar cantidad si ya existe
        int nuevaCantidad = detalle.getCantidadUnitaria() + cantidad;

        if (bebidaSeleccionada.getStock() < nuevaCantidad) {
          Alertas.crearAlerta(Alert.AlertType.WARNING, "Advertencia",
              "No hay suficiente stock. Stock disponible: " + bebidaSeleccionada.getStock());
          return;
        }

        detalle.setCantidadUnitaria(nuevaCantidad);
        detalle.setTotal_pagado(detalle.getPrecioUnitario() * nuevaCantidad);
        tvDetalleVenta.refresh();
        actualizarTotales();
        return;
      }
    }

    // Agregar nuevo detalle si no existe
    DetalleVenta nuevoDetalle = new DetalleVenta();
    nuevoDetalle.setBebida(bebidaSeleccionada);
    nuevoDetalle.setIdProducto(bebidaSeleccionada.getId());
    nuevoDetalle.setPrecioUnitario(bebidaSeleccionada.getPrecio());
    nuevoDetalle.setCantidadUnitaria(cantidad);
    nuevoDetalle.setTotal_pagado(bebidaSeleccionada.getPrecio() * cantidad);

    detallesVenta.add(nuevoDetalle);
    actualizarTotales();
  }

  private void eliminarDetalle(DetalleVenta detalle) {
    detallesVenta.remove(detalle);
    actualizarTotales();
  }

  private void actualizarTotales() {
    subtotal = 0.0;

    for (DetalleVenta detalle : detallesVenta) {
      subtotal += detalle.getTotal_pagado();
    }

    actualizarDescuento();
  }

  private void actualizarDescuento() {
    Promocion promocionSeleccionada = cbPromocion.getSelectionModel().getSelectedItem();

    if (promocionSeleccionada != null && promocionSeleccionada.getIdPromocion() > 0) {
      descuento = subtotal * (promocionSeleccionada.getDescuento() / 100.0);
    } else {
      descuento = 0.0;
    }

    total = subtotal - descuento;

    // Actualizar etiquetas
    lblSubtotal.setText(String.format("$%.2f", subtotal));
    lblDescuento.setText(String.format("$%.2f", descuento));
    lblTotal.setText(String.format("$%.2f", total));
  }

  private void registrarVenta(ActionEvent event) {
    try {
      // Crear objeto venta
      Venta nuevaVenta = new Venta();
      nuevaVenta.setIdCliente(cbCliente.getValue().getIdCliente());
      nuevaVenta.setFechaVenta(new Date());
      nuevaVenta.setFolioFactura(ventaDAO.generarFolioFactura(LocalDate.now()));

      // Crear lista de detalles (desde tu tabla o lista en la interfaz)
      List<DetalleVenta> detalles = detallesVenta;

      if (detalles.isEmpty()) {
        Alertas.crearAlerta(Alert.AlertType.WARNING, "Advertencia", "Debe agregar al menos un producto a la venta.");
        return;
      }

      // Obtener promoción seleccionada (puede ser "Sin promoción")
      Integer idPromocion = null;
      Promocion promoSeleccionada = cbPromocion.getValue();
      if (promoSeleccionada != null && promoSeleccionada.getIdPromocion() > 0) {
        idPromocion = promoSeleccionada.getIdPromocion();
      }

      // Llamar al procedimiento con la promoción (si la hay)
      VentaDAO.ResultadoVenta resultado = VentaDAO.registrarVenta(nuevaVenta, detalles, idPromocion);

      if (resultado.isExito()) {
        Alertas.crearAlerta(Alert.AlertType.INFORMATION, "Éxito", resultado.getMensaje());
        limpiarFormulario();
      } else {
        String msg = resultado.getMensaje() == null ? "" : resultado.getMensaje();
        Alertas.crearAlerta(Alert.AlertType.ERROR, "Error", "No se pudo generar la venta\n" + msg);
      }

    } catch (Exception e) {
      Alertas.crearAlerta(Alert.AlertType.ERROR, "Error", "Error inesperado: " + e.getMessage());
    }
  }

  private void limpiarFormulario() {
    dpFechaVenta.setValue(LocalDate.now());
    cbCliente.getSelectionModel().select(0);
    cbPromocion.getSelectionModel().select(0);
    cbProducto.getSelectionModel().select(0);
    spCantidad.getValueFactory().setValue(1);
    detallesVenta.clear();
    actualizarTotales();
  }

  private void cancelar(ActionEvent event) {
    principalController.restaurarVistaPrincipal();
  }
}