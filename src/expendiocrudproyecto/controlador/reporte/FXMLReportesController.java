package expendiocrudproyecto.controlador.reporte;

import expendiocrudproyecto.modelo.dao.BebidaDAO;
import expendiocrudproyecto.modelo.dao.ClienteDAO;
import expendiocrudproyecto.modelo.dao.ReporteDAO;
import expendiocrudproyecto.modelo.pojo.*;
import expendiocrudproyecto.utilidades.GeneradorReportesPDF;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class FXMLReportesController implements Initializable {

  private ReporteDAO reporteDAO;

  // CONTROLES DE VENTAS POR PERÍODO
  @FXML private DatePicker dpFechaInicioVentas;
  @FXML private DatePicker dpFechaFinVentas;
  @FXML private ComboBox<String> cbTipoPeriodo;
  @FXML private TableView<ReporteVentasPeriodo> tblVentasPeriodo;
  @FXML private TableColumn<ReporteVentasPeriodo, LocalDate> colFechaVenta;
  @FXML private TableColumn<ReporteVentasPeriodo, Integer> colTotalPedidos;
  @FXML private TableColumn<ReporteVentasPeriodo, BigDecimal> colTotalVentas;
  @FXML private TableColumn<ReporteVentasPeriodo, String> colPeriodo;
  @FXML private Button btnGenerarVentasPeriodo;
  @FXML private Button btnExportarVentasPeriodo;

  // CONTROLES DE VENTAS POR PRODUCTO
  @FXML private DatePicker dpFechaInicioProducto;
  @FXML private DatePicker dpFechaFinProducto;
  @FXML private ComboBox<String> cbVentasProducto;
  @FXML private TableView<ReporteVentasProducto> tblVentasProducto;
  @FXML private TableColumn<ReporteVentasProducto, String> colNombreProducto;
  @FXML private TableColumn<ReporteVentasProducto, String> colCategoriaProducto;
  @FXML private TableColumn<ReporteVentasProducto, Integer> colCantidadVendida;
  @FXML private TableColumn<ReporteVentasProducto, BigDecimal> colTotalVentasProducto;
  @FXML private TableColumn<ReporteVentasProducto, BigDecimal> colPrecioUnitario;
  @FXML private Button btnGenerarVentasProducto;
  @FXML private Button btnExportarVentasProducto;

  // CONTROLES DE STOCK MÍNIMO
  @FXML private TableView<ReporteStockMinimo> tblStockMinimo;
  @FXML private TableColumn<ReporteStockMinimo, String> colNombreStock;
  @FXML private TableColumn<ReporteStockMinimo, String> colCategoriaStock;
  @FXML private TableColumn<ReporteStockMinimo, Integer> colStockActual;
  @FXML private TableColumn<ReporteStockMinimo, Integer> colStockMinimo;
  @FXML private TableColumn<ReporteStockMinimo, Integer> colDiferencia;
  @FXML private TableColumn<ReporteStockMinimo, String> colEstado;
  @FXML private Button btnGenerarStockMinimo;
  @FXML private Button btnExportarStockMinimo;

  // CONTROLES DE PRODUCTOS MÁS VENDIDOS
  @FXML private DatePicker dpFechaInicioMasVendidos;
  @FXML private DatePicker dpFechaFinMasVendidos;
  @FXML private Spinner<Integer> spnLimiteMasVendidos;
  @FXML private TableView<ReporteProductosMasVendidos> tblProductosMasVendidos;
  @FXML private TableColumn<ReporteProductosMasVendidos, Integer> colPosicionMas;
  @FXML private TableColumn<ReporteProductosMasVendidos, String> colNombreMasVendido;
  @FXML private TableColumn<ReporteProductosMasVendidos, Integer> colCantidadMasVendido;
  @FXML private TableColumn<ReporteProductosMasVendidos, BigDecimal> colTotalMasVendido;
  @FXML private TableColumn<ReporteProductosMasVendidos, Double> colPorcentajeMasVendido;
  @FXML private Button btnGenerarMasVendidos;
  @FXML private Button btnExportarMasVendidos;

  // CONTROLES DE PRODUCTOS MENOS VENDIDOS
  @FXML private DatePicker dpFechaInicioMenosVendidos;
  @FXML private DatePicker dpFechaFinMenosVendidos;
  @FXML private Spinner<Integer> spnLimiteMenosVendidos;
  @FXML private TableView<ReporteProductosMenosVendidos> tblProductosMenosVendidos;
  @FXML private TableColumn<ReporteProductosMenosVendidos, Integer> colPosicionMenos;
  @FXML private TableColumn<ReporteProductosMenosVendidos, String> colNombreMenosVendido;
  @FXML private TableColumn<ReporteProductosMenosVendidos, BigDecimal> colCantidadMenosVendido;
  @FXML private TableColumn<ReporteProductosMenosVendidos, String> colObservacion;
  @FXML private Button btnGenerarMenosVendidos;
  @FXML private Button btnExportarMenosVendidos;

  // CONTROLES DE PRODUCTOS NO VENDIDOS A CLIENTE
  @FXML private ComboBox<String> cbClientesNoVendidos;
  @FXML private TableView<ReporteProductosNoVendidosCliente> tblProductosNoVendidos;
  @FXML private TableColumn<ReporteProductosNoVendidosCliente, String> colProductoNoVendido;
  @FXML private TableColumn<ReporteProductosNoVendidosCliente, String> colCategoriaNoVendido;
  @FXML private TableColumn<ReporteProductosNoVendidosCliente, BigDecimal> colPrecioNoVendido;
  @FXML private TableColumn<ReporteProductosNoVendidosCliente, String> colRecomendacion;
  @FXML private Button btnGenerarProductosNoVendidos;
  @FXML private Button btnExportarNoVendidos;

  // CONTROLES DE PRODUCTO MÁS VENDIDO POR CLIENTE
  @FXML private ComboBox<String> cbClientesPromocion;
  @FXML private TableView<ReporteProductoMasVendidoCliente> tblProductoMasVendidoCliente;
  @FXML private TableColumn<ReporteProductoMasVendidoCliente, String> colClientePromocion;
  @FXML private TableColumn<ReporteProductoMasVendidoCliente, String> colProductoFavorito;
  @FXML private TableColumn<ReporteProductoMasVendidoCliente, Integer> colCantidadComprada;
  @FXML private TableColumn<ReporteProductoMasVendidoCliente, String> colTipoPromocion;
  @FXML private TableColumn<ReporteProductoMasVendidoCliente, String> colObservacionesPromocion;
  @FXML private Button btnGenerarPromocionCliente;
  @FXML private Button btnExportarPromocionCliente;

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    reporteDAO = new ReporteDAO();
    configurarComponentes();
    configurarTablas();
    configurarEventos();
  }

  private void configurarComponentes() {
    cbTipoPeriodo.setItems(FXCollections.observableArrayList("Semanal", "Mensual", "Anual"));
    cbTipoPeriodo.setValue("Mensual");

    spnLimiteMasVendidos.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(5, 50, 10));
    spnLimiteMenosVendidos.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(5, 50, 10));

    LocalDate fechaActual = LocalDate.now();
    LocalDate fechaInicio = fechaActual.minusMonths(1);

    dpFechaInicioVentas.setValue(fechaInicio);
    dpFechaFinVentas.setValue(fechaActual);
    dpFechaInicioProducto.setValue(fechaInicio);
    dpFechaFinProducto.setValue(fechaActual);
    dpFechaInicioMasVendidos.setValue(fechaInicio);
    dpFechaFinMasVendidos.setValue(fechaActual);
    dpFechaInicioMenosVendidos.setValue(fechaInicio);
    dpFechaFinMenosVendidos.setValue(fechaActual);

    cargarClientesEnComboBox();
    cargarProductosEnComboBox();
  }

  private void cargarClientesEnComboBox() {
    try {
      ClienteDAO clienteDAO = new ClienteDAO();
      List<Cliente> clientes = clienteDAO.leerTodo();
      ObservableList<String> clientesComboBox = FXCollections.observableArrayList();
      for (Cliente cliente : clientes) {
        String itemComboBox = cliente.getIdCliente() + " - " + cliente.getNombre();
        if (cliente.getRazonSocial() != null && !cliente.getRazonSocial().trim().isEmpty()) {
          itemComboBox += " (" + cliente.getRazonSocial() + ")";
        }
        clientesComboBox.add(itemComboBox);
      }
      cbClientesNoVendidos.setItems(clientesComboBox);
      cbClientesPromocion.setItems(clientesComboBox);
      if (!clientesComboBox.isEmpty()) {
        cbClientesNoVendidos.setValue(clientesComboBox.get(0));
        cbClientesPromocion.setValue(clientesComboBox.get(0));
      }
    } catch (SQLException e) {
      mostrarAlerta("Error", "No se pudieron cargar los clientes: " + e.getMessage());
    }
  }

  private void cargarProductosEnComboBox() {
    try {
      BebidaDAO bebidaDAO = new BebidaDAO();
      List<Bebida> bebidas = bebidaDAO.leerTodo();
      ObservableList<String> productosComboBox = FXCollections.observableArrayList();
      for (Bebida bebida : bebidas) {
        String itemComboBox = bebida.getId() + " - " + bebida.getNombre();
        productosComboBox.add(itemComboBox);
      }
      cbVentasProducto.setItems(productosComboBox);
      if (!productosComboBox.isEmpty()) {
        cbVentasProducto.setValue(productosComboBox.get(0));
      }
    } catch (SQLException e) {
      mostrarAlerta("Error", "No se pudieron cargar los productos: " + e.getMessage());
    }
  }

  private void configurarTablas() {
    colPeriodo.setCellValueFactory(new PropertyValueFactory<>("periodo"));
    colTotalPedidos.setCellValueFactory(new PropertyValueFactory<>("totalPedidos"));
    colTotalVentas.setCellValueFactory(new PropertyValueFactory<>("totalVentas"));

    colNombreProducto.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
    colCantidadVendida.setCellValueFactory(new PropertyValueFactory<>("cantidadVendida"));
    colTotalVentasProducto.setCellValueFactory(new PropertyValueFactory<>("totalVentas"));
    colPrecioUnitario.setCellValueFactory(new PropertyValueFactory<>("precioUnitario"));

    colNombreStock.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
    colStockActual.setCellValueFactory(new PropertyValueFactory<>("stockActual"));
    colStockMinimo.setCellValueFactory(new PropertyValueFactory<>("stockMinimo"));

    colPosicionMas.setCellValueFactory(new PropertyValueFactory<>("idProducto"));
    colNombreMasVendido.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
    colTotalMasVendido.setCellValueFactory(new PropertyValueFactory<>("totalVentas"));

    colPosicionMenos.setCellValueFactory(new PropertyValueFactory<>("idProducto"));
    colNombreMenosVendido.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
    colCantidadMenosVendido.setCellValueFactory(new PropertyValueFactory<>("totalVentas"));
    colObservacion.setCellValueFactory(new PropertyValueFactory<>("observacion"));

    colProductoNoVendido.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
    colPrecioNoVendido.setCellValueFactory(new PropertyValueFactory<>("precioUnitario"));
    colRecomendacion.setCellValueFactory(new PropertyValueFactory<>("recomendacion"));

    colClientePromocion.setCellValueFactory(new PropertyValueFactory<>("nombreCliente"));
    colProductoFavorito.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
    colCantidadComprada.setCellValueFactory(new PropertyValueFactory<>("cantidadComprada"));
  }

  private void configurarEventos() {
    // --- Eventos para los botones de Generar ---
    btnGenerarVentasPeriodo.setOnAction(e -> generarReporteVentasPeriodo());
    btnGenerarVentasProducto.setOnAction(e -> generarReporteVentasProducto());
    btnGenerarStockMinimo.setOnAction(e -> generarReporteStockMinimo());
    btnGenerarMasVendidos.setOnAction(e -> generarReporteProductosMasVendidos());
    btnGenerarMenosVendidos.setOnAction(e -> generarReporteProductosMenosVendidos());
    btnGenerarProductosNoVendidos.setOnAction(e -> generarReporteProductosNoVendidos());
    btnGenerarPromocionCliente.setOnAction(e -> generarReportePromocionCliente());

    // --- Eventos para los botones de Exportar (Ahora se manejan con FXML) ---
    // Ya no es necesario configurar los setOnAction aquí
  }

  // --- MÉTODOS DE EXPORTACIÓN (CORREGIDOS CON @FXML) ---

  @FXML
  private void exportarReporteVentasPeriodo() {
    if (tblVentasPeriodo.getItems().isEmpty()) {
      mostrarAlerta("Información", "No hay datos para exportar.");
      return;
    }
    try {
      String ruta = GeneradorReportesPDF.generarReporteVentasPeriodo(
              tblVentasPeriodo.getItems(),
              dpFechaInicioVentas.getValue(),
              dpFechaFinVentas.getValue());
      GeneradorReportesPDF.abrirArchivoPDF(ruta);
    } catch (Exception e) {
      mostrarAlerta("Error", "No se pudo exportar el reporte: " + e.getMessage());
    }
  }

  @FXML
  private void exportarReporteVentasProducto() {
    if (tblVentasProducto.getItems().isEmpty()) {
      mostrarAlerta("Información", "No hay datos para exportar.");
      return;
    }
    try {
      String ruta = GeneradorReportesPDF.generarReporteVentasProducto(tblVentasProducto.getItems());
      GeneradorReportesPDF.abrirArchivoPDF(ruta);
    } catch (Exception e) {
      mostrarAlerta("Error", "No se pudo exportar el reporte: " + e.getMessage());
    }
  }

  @FXML
  private void exportarReporteStockMinimo() {
    if (tblStockMinimo.getItems().isEmpty()) {
      mostrarAlerta("Información", "No hay datos para exportar.");
      return;
    }
    try {
      String ruta = GeneradorReportesPDF.generarReporteStockMinimo(tblStockMinimo.getItems());
      GeneradorReportesPDF.abrirArchivoPDF(ruta);
    } catch (Exception e) {
      mostrarAlerta("Error", "No se pudo exportar el reporte: " + e.getMessage());
    }
  }

  @FXML
  private void exportarReporteMasVendidos() {
    if (tblProductosMasVendidos.getItems().isEmpty()) {
      mostrarAlerta("Información", "No hay datos para exportar.");
      return;
    }
    try {
      String ruta = GeneradorReportesPDF.generarReporteProductosMasVendidos(tblProductosMasVendidos.getItems());
      GeneradorReportesPDF.abrirArchivoPDF(ruta);
    } catch (Exception e) {
      mostrarAlerta("Error", "No se pudo exportar el reporte: " + e.getMessage());
    }
  }

  @FXML
  private void exportarReporteMenosVendidos() {
    if (tblProductosMenosVendidos.getItems().isEmpty()) {
      mostrarAlerta("Información", "No hay datos para exportar.");
      return;
    }
    try {
      String ruta = GeneradorReportesPDF.generarReporteProductosMenosVendidos(tblProductosMenosVendidos.getItems());
      GeneradorReportesPDF.abrirArchivoPDF(ruta);
    } catch (Exception e) {
      mostrarAlerta("Error", "No se pudo exportar el reporte: " + e.getMessage());
    }
  }

  @FXML
  private void exportarReporteNoVendidos() {
    if (tblProductosNoVendidos.getItems().isEmpty()) {
      mostrarAlerta("Información", "No hay datos para exportar.");
      return;
    }
    try {
      String ruta = GeneradorReportesPDF.generarReporteProductosNoVendidos(tblProductosNoVendidos.getItems());
      GeneradorReportesPDF.abrirArchivoPDF(ruta);
    } catch (Exception e) {
      mostrarAlerta("Error", "No se pudo exportar el reporte: " + e.getMessage());
    }
  }

  @FXML
  private void exportarReportePromocionCliente() {
    if (tblProductoMasVendidoCliente.getItems().isEmpty()) {
      mostrarAlerta("Información", "No hay datos para exportar.");
      return;
    }
    try {
      String ruta = GeneradorReportesPDF.generarReportePromocionCliente(tblProductoMasVendidoCliente.getItems());
      GeneradorReportesPDF.abrirArchivoPDF(ruta);
    } catch (Exception e) {
      mostrarAlerta("Error", "No se pudo exportar el reporte: " + e.getMessage());
    }
  }

  // --- MÉTODOS DE GENERACIÓN DE REPORTES (Sin cambios) ---

  @FXML
  private void generarReporteVentasPeriodo() {
    LocalDate fechaInicioLD = dpFechaInicioVentas.getValue();
    LocalDate fechaFinLD = dpFechaFinVentas.getValue();
    if (fechaInicioLD == null || fechaFinLD == null) {
      mostrarAlerta("Error", "Debe seleccionar una fecha de inicio y una fecha de fin.");
      return;
    }
    if (fechaInicioLD.isAfter(fechaFinLD)) {
      mostrarAlerta("Error", "La fecha de inicio no puede ser posterior a la fecha de fin.");
      return;
    }
    String fechaInicio = fechaInicioLD.toString();
    String fechaFin = fechaFinLD.toString();
    try {
      String tipoPeriodo = cbTipoPeriodo.getValue();
      List<ReporteVentasPeriodo> reportes;
      switch (tipoPeriodo) {
        case "Semanal": reportes = reporteDAO.obtenerVentasSemanales(fechaInicio, fechaFin); break;
        case "Mensual": reportes = reporteDAO.obtenerVentasMensuales(); break;
        case "Anual": reportes = reporteDAO.obtenerVentasAnuales(fechaInicio, fechaFin); break;
        default: reportes = reporteDAO.obtenerVentasMensuales();
      }
      ObservableList<ReporteVentasPeriodo> data = FXCollections.observableArrayList(reportes);
      tblVentasPeriodo.setItems(data);
      if (reportes.isEmpty()) {
        mostrarAlerta("Información", "No se encontraron registros en el período seleccionado.");
      } else {
        mostrarAlerta("Éxito", "Se cargaron " + reportes.size() + " registros de ventas " + tipoPeriodo.toLowerCase());
      }
    } catch (Exception e) {
      mostrarAlerta("Error", "Error al generar reporte: " + e.getMessage());
    }
  }

  @FXML
  private void generarReporteVentasProducto() {
    try {
      List<ReporteVentasProducto> reportes = reporteDAO.obtenerVentasPorProducto();
      ObservableList<ReporteVentasProducto> data = FXCollections.observableArrayList(reportes);
      tblVentasProducto.setItems(data);
      mostrarAlerta("Éxito", "Se cargaron " + reportes.size() + " productos con ventas");
    } catch (Exception e) {
      mostrarAlerta("Error", "Error al generar reporte: " + e.getMessage());
    }
  }

  @FXML
  private void generarReporteStockMinimo() {
    try {
      List<ReporteStockMinimo> reportes = reporteDAO.obtenerProductosStockMinimo();
      ObservableList<ReporteStockMinimo> data = FXCollections.observableArrayList(reportes);
      tblStockMinimo.setItems(data);
      mostrarAlerta("Éxito", "Se encontraron " + reportes.size() + " productos con stock mínimo o crítico");
    } catch (Exception e) {
      mostrarAlerta("Error", "Error al generar reporte: " + e.getMessage());
    }
  }

  @FXML
  private void generarReporteProductosMasVendidos() {
    try {
      int limite = spnLimiteMasVendidos.getValue();
      List<ReporteProductosMasVendidos> reportes = reporteDAO.obtenerProductosMasVendidos(limite);
      ObservableList<ReporteProductosMasVendidos> data = FXCollections.observableArrayList(reportes);
      tblProductosMasVendidos.setItems(data);
      mostrarAlerta("Éxito", "Top " + limite + " productos más vendidos cargados");
    } catch (Exception e) {
      mostrarAlerta("Error", "Error al generar reporte: " + e.getMessage());
    }
  }

  @FXML
  private void generarReporteProductosMenosVendidos() {
    try {
      int limite = spnLimiteMenosVendidos.getValue();
      List<ReporteProductosMenosVendidos> reportes = reporteDAO.obtenerProductosMenosVendidos(limite);
      ObservableList<ReporteProductosMenosVendidos> data = FXCollections.observableArrayList(reportes);
      tblProductosMenosVendidos.setItems(data);
      mostrarAlerta("Éxito", "Top " + limite + " productos menos vendidos cargados");
    } catch (Exception e) {
      mostrarAlerta("Error", "Error al generar reporte: " + e.getMessage());
    }
  }

  @FXML
  private void generarReporteProductosNoVendidos() {
    try {
      String clienteSeleccionado = cbClientesNoVendidos.getValue();
      if (clienteSeleccionado == null) {
        mostrarAlerta("Error", "Debe seleccionar un cliente");
        return;
      }
      int idCliente = Integer.parseInt(clienteSeleccionado.split(" - ")[0]);
      List<ReporteProductosNoVendidosCliente> reportes = reporteDAO.obtenerProductosNoVendidosACliente(idCliente);
      ObservableList<ReporteProductosNoVendidosCliente> data = FXCollections.observableArrayList(reportes);
      tblProductosNoVendidos.setItems(data);
      mostrarAlerta("Éxito", "Se encontraron " + reportes.size() + " productos no vendidos al cliente");
    } catch (Exception e) {
      mostrarAlerta("Error", "Error al generar reporte: " + e.getMessage());
    }
  }

  @FXML
  private void generarReportePromocionCliente() {
    try {
      String clienteSeleccionado = cbClientesPromocion.getValue();
      if (clienteSeleccionado == null) {
        mostrarAlerta("Error", "Debe seleccionar un cliente");
        return;
      }
      int idCliente = Integer.parseInt(clienteSeleccionado.split(" - ")[0]);
      List<ReporteProductoMasVendidoCliente> reportes = reporteDAO.obtenerProductoMasVendidoACliente(idCliente);
      ObservableList<ReporteProductoMasVendidoCliente> data = FXCollections.observableArrayList(reportes);
      tblProductoMasVendidoCliente.setItems(data);
      mostrarAlerta("Éxito", "Productos más comprados por el cliente cargados");
    } catch (Exception e) {
      mostrarAlerta("Error", "Error al generar reporte: " + e.getMessage());
    }
  }

  private void mostrarAlerta(String titulo, String mensaje) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle(titulo);
    alert.setHeaderText(null);
    alert.setContentText(mensaje);
    alert.showAndWait();
  }
}