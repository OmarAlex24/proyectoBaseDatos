package expendiocrudproyecto.controlador.reporte;

import expendiocrudproyecto.modelo.dao.BebidaDAO;
import expendiocrudproyecto.modelo.dao.ClienteDAO;
import expendiocrudproyecto.modelo.dao.ReporteDAO;
import expendiocrudproyecto.modelo.pojo.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

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

    // CONTROLES DE STOCK MÍNIMO
    @FXML private TableView<ReporteStockMinimo> tblStockMinimo;
    @FXML private TableColumn<ReporteStockMinimo, String> colNombreStock;
    @FXML private TableColumn<ReporteStockMinimo, String> colCategoriaStock;
    @FXML private TableColumn<ReporteStockMinimo, Integer> colStockActual;
    @FXML private TableColumn<ReporteStockMinimo, Integer> colStockMinimo;
    @FXML private TableColumn<ReporteStockMinimo, Integer> colDiferencia;
    @FXML private TableColumn<ReporteStockMinimo, String> colEstado;
    @FXML private Button btnGenerarStockMinimo;

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

    // CONTROLES DE PRODUCTOS NO VENDIDOS A CLIENTE
    @FXML private ComboBox<String> cbClientesNoVendidos;
    @FXML private TableView<ReporteProductosNoVendidosCliente> tblProductosNoVendidos;
    @FXML private TableColumn<ReporteProductosNoVendidosCliente, String> colProductoNoVendido;
    @FXML private TableColumn<ReporteProductosNoVendidosCliente, String> colCategoriaNoVendido;
    @FXML private TableColumn<ReporteProductosNoVendidosCliente, BigDecimal> colPrecioNoVendido;
    @FXML private TableColumn<ReporteProductosNoVendidosCliente, String> colRecomendacion;
    @FXML private Button btnGenerarProductosNoVendidos;

    // CONTROLES DE PRODUCTO MÁS VENDIDO POR CLIENTE
    @FXML private ComboBox<String> cbClientesPromocion;
    @FXML private TableView<ReporteProductoMasVendidoCliente> tblProductoMasVendidoCliente;
    @FXML private TableColumn<ReporteProductoMasVendidoCliente, String> colClientePromocion;
    @FXML private TableColumn<ReporteProductoMasVendidoCliente, String> colProductoFavorito;
    @FXML private TableColumn<ReporteProductoMasVendidoCliente, Integer> colCantidadComprada;
    @FXML private TableColumn<ReporteProductoMasVendidoCliente, String> colTipoPromocion;
    @FXML private TableColumn<ReporteProductoMasVendidoCliente, String> colObservacionesPromocion;
    @FXML private Button btnGenerarPromocionCliente;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        reporteDAO = new ReporteDAO();
        configurarComponentes();
        configurarTablas();
        configurarEventos();
    }

    private void configurarComponentes() {
        // Configurar ComboBox de tipo de período
        cbTipoPeriodo.setItems(FXCollections.observableArrayList("Semanal", "Mensual", "Anual"));
        cbTipoPeriodo.setValue("Mensual");

        // Configurar Spinners
        spnLimiteMasVendidos.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(5, 50, 10));
        spnLimiteMenosVendidos.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(5, 50, 10));

        // Configurar fechas por defecto
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

        // Cargar clientes en los ComboBox
        cargarClientesEnComboBox();
        cargarProductosEnComboBox();
    }

    // Método separado para cargar clientes en los ComboBox
    private void cargarClientesEnComboBox() {
        try {
            // Crear instancia del ClienteDAO
            ClienteDAO clienteDAO = new ClienteDAO();

            // Obtener todos los clientes
            List<Cliente> clientes = clienteDAO.leerTodo();

            // Crear lista observable para los ComboBox
            ObservableList<String> clientesComboBox = FXCollections.observableArrayList();

            // Convertir clientes a formato "ID - Nombre (Razón Social)"
            for (Cliente cliente : clientes) {
                String itemComboBox = cliente.getIdCliente() + " - " + cliente.getNombre();

                // Si tiene razón social, agregarla
                if (cliente.getRazonSocial() != null && !cliente.getRazonSocial().trim().isEmpty()) {
                    itemComboBox += " (" + cliente.getRazonSocial() + ")";
                }

                clientesComboBox.add(itemComboBox);
            }

            // Asignar la lista a ambos ComboBox
            cbClientesNoVendidos.setItems(clientesComboBox);
            cbClientesPromocion.setItems(clientesComboBox);

            // Opcional: Seleccionar el primer cliente por defecto
            if (!clientesComboBox.isEmpty()) {
                cbClientesNoVendidos.setValue(clientesComboBox.get(0));
                cbClientesPromocion.setValue(clientesComboBox.get(0));
            }

        } catch (SQLException e) {
            System.err.println("Error al cargar clientes: " + e.getMessage());
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

                if (bebida.getNombre() != null && !bebida.getNombre().trim().isEmpty()) {
                    itemComboBox += " (" + bebida.getNombre() + ")";
                }

                productosComboBox.add(itemComboBox);
            }

            // Asignar la lista a ambos ComboBox
            cbVentasProducto.setItems(productosComboBox);

            // Opcional: Seleccionar el primer cliente por defecto
            if (!productosComboBox.isEmpty()) {
                cbClientesNoVendidos.setValue(productosComboBox.get(0));
            }

        } catch (SQLException e) {
            System.err.println("Error al cargar clientes: " + e.getMessage());
            mostrarAlerta("Error", "No se pudieron cargar los clientes: " + e.getMessage());
        }
    }


    private void configurarTablas() {
        // ==================== TABLA VENTAS POR PERÍODO ====================
        // CORREGIDO: Usar campos exactos de tu ReporteVentasPeriodo
        colPeriodo.setCellValueFactory(new PropertyValueFactory<>("periodo"));
        colTotalPedidos.setCellValueFactory(new PropertyValueFactory<>("totalPedidos"));
        colTotalVentas.setCellValueFactory(new PropertyValueFactory<>("totalVentas"));

        // ==================== TABLA VENTAS POR PRODUCTO ====================
        // CORREGIDO: Usar campos exactos de tu ReporteVentasProducto
        colNombreProducto.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
        colCantidadVendida.setCellValueFactory(new PropertyValueFactory<>("cantidadVendida"));
        colTotalVentasProducto.setCellValueFactory(new PropertyValueFactory<>("totalVentas"));
        colPrecioUnitario.setCellValueFactory(new PropertyValueFactory<>("precioUnitario"));

        // ==================== TABLA STOCK MÍNIMO ====================
        // CORREGIDO: Usar campos exactos de tu ReporteStockMinimo
        colNombreStock.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
        colStockActual.setCellValueFactory(new PropertyValueFactory<>("stockActual"));
        colStockMinimo.setCellValueFactory(new PropertyValueFactory<>("stockMinimo"));
        // Nota: Tu DAO no setea estadoStock ni cantidadFaltante, así que estas columnas estarán vacías

        // ==================== TABLA PRODUCTOS MÁS VENDIDOS ====================
        // CORREGIDO: Usar campos exactos de tu ReporteProductosMasVendidos
        colPosicionMas.setCellValueFactory(new PropertyValueFactory<>("idProducto")); // Tu DAO no setea ranking
        colNombreMasVendido.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
        colTotalMasVendido.setCellValueFactory(new PropertyValueFactory<>("totalVentas"));
        // Nota: Tu DAO no setea totalVendido, usa totalVentas

        // ==================== TABLA PRODUCTOS MENOS VENDIDOS ====================
        // CORREGIDO: Usar campos exactos de tu ReporteProductosMenosVendidos
        colPosicionMenos.setCellValueFactory(new PropertyValueFactory<>("idProducto"));
        colNombreMenosVendido.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
        colCantidadMenosVendido.setCellValueFactory(new PropertyValueFactory<>("totalVentas"));
        colObservacion.setCellValueFactory(new PropertyValueFactory<>("observacion"));

        // ==================== TABLA PRODUCTOS NO VENDIDOS A CLIENTE ====================
        // CORREGIDO: Usar campos exactos de tu ReporteProductosNoVendidosCliente
        colProductoNoVendido.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
        colPrecioNoVendido.setCellValueFactory(new PropertyValueFactory<>("precioUnitario"));
        colRecomendacion.setCellValueFactory(new PropertyValueFactory<>("recomendacion"));

        // ==================== TABLA PRODUCTO MÁS VENDIDO POR CLIENTE ====================
        // CORREGIDO: Usar campos exactos de tu ReporteProductoMasVendidoCliente
        colClientePromocion.setCellValueFactory(new PropertyValueFactory<>("nombreCliente"));
        colProductoFavorito.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
        colCantidadComprada.setCellValueFactory(new PropertyValueFactory<>("cantidadComprada"));
        // Nota: Tu DAO no setea recomendacion para estas columnas, estarán vacías
        colTipoPromocion.setCellValueFactory(new PropertyValueFactory<>("precioUnitario")); // Mostrar precio como alternativa
        colObservacionesPromocion.setCellValueFactory(new PropertyValueFactory<>("idCliente")); // Mostrar ID como alternativa
    }

    private void configurarEventos() {
        btnGenerarVentasPeriodo.setOnAction(e -> generarReporteVentasPeriodo());
        btnGenerarVentasProducto.setOnAction(e -> generarReporteVentasProducto());
        btnGenerarStockMinimo.setOnAction(e -> generarReporteStockMinimo());
        btnGenerarMasVendidos.setOnAction(e -> generarReporteProductosMasVendidos());
        btnGenerarMenosVendidos.setOnAction(e -> generarReporteProductosMenosVendidos());
        btnGenerarProductosNoVendidos.setOnAction(e -> generarReporteProductosNoVendidos());
        btnGenerarPromocionCliente.setOnAction(e -> generarReportePromocionCliente());
    }

    // ==================== MÉTODOS DE GENERACIÓN DE REPORTES ====================

    @FXML
    private void generarReporteVentasPeriodo() {
        try {
            String tipoPeriodo = cbTipoPeriodo.getValue();
            List<ReporteVentasPeriodo> reportes;

            switch (tipoPeriodo) {
                case "Semanal":
                    reportes = reporteDAO.obtenerVentasSemanales();
                    break;
                case "Mensual":
                    reportes = reporteDAO.obtenerVentasMensuales();
                    break;
                case "Anual":
                    reportes = reporteDAO.obtenerVentasAnuales();
                    break;
                default:
                    reportes = reporteDAO.obtenerVentasMensuales();
            }

            ObservableList<ReporteVentasPeriodo> data = FXCollections.observableArrayList(reportes);
            tblVentasPeriodo.setItems(data);

            mostrarAlerta("Éxito", "Se cargaron " + reportes.size() + " registros de ventas " + tipoPeriodo.toLowerCase());

        } catch (Exception e) {
            mostrarAlerta("Error", "Error al generar reporte: " + e.getMessage());
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
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

            // Extraer ID del cliente del ComboBox (formato: "ID - Nombre")
            int idCliente = Integer.parseInt(clienteSeleccionado.split(" - ")[0]);

            List<ReporteProductosNoVendidosCliente> reportes = reporteDAO.obtenerProductosNoVendidosACliente(idCliente);
            ObservableList<ReporteProductosNoVendidosCliente> data = FXCollections.observableArrayList(reportes);
            tblProductosNoVendidos.setItems(data);

            mostrarAlerta("Éxito", "Se encontraron " + reportes.size() + " productos no vendidos al cliente");

        } catch (Exception e) {
            mostrarAlerta("Error", "Error al generar reporte: " + e.getMessage());
            e.printStackTrace();
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

            // Extraer ID del cliente del ComboBox (formato: "ID - Nombre")
            int idCliente = Integer.parseInt(clienteSeleccionado.split(" - ")[0]);

            List<ReporteProductoMasVendidoCliente> reportes = reporteDAO.obtenerProductoMasVendidoACliente(idCliente);
            ObservableList<ReporteProductoMasVendidoCliente> data = FXCollections.observableArrayList(reportes);
            tblProductoMasVendidoCliente.setItems(data);

            mostrarAlerta("Éxito", "Productos más comprados por el cliente cargados");

        } catch (Exception e) {
            mostrarAlerta("Error", "Error al generar reporte: " + e.getMessage());
            e.printStackTrace();
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