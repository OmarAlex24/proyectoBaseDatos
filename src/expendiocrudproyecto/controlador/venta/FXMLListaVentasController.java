package expendiocrudproyecto.controlador.venta;

import expendiocrudproyecto.controlador.FXMLPrincipalController;
import expendiocrudproyecto.modelo.ConexionBD;
import expendiocrudproyecto.modelo.pojo.Usuario;
import expendiocrudproyecto.modelo.pojo.Venta;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.ResourceBundle;
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
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class FXMLListaVentasController implements Initializable, FXMLPrincipalController.ControladorConUsuario {

    @FXML
    private TextField tfBusqueda;
    @FXML
    private DatePicker dpFechaInicio;
    @FXML
    private DatePicker dpFechaFin;
    @FXML
    private Button btnBuscar;
    @FXML
    private Button btnNuevaVenta;
    @FXML
    private TableView<Venta> tvVentas;
    @FXML
    private TableColumn<Venta, Integer> tcFolio;
    @FXML
    private TableColumn<Venta, String> tcFecha;
    @FXML
    private TableColumn<Venta, String> tcCliente;
    @FXML
    private TableColumn<Venta, String> tcEmpleado;
    @FXML
    private TableColumn<Venta, Float> tcSubtotal;
    @FXML
    private TableColumn<Venta, Float> tcDescuento;
    @FXML
    private TableColumn<Venta, Float> tcTotal;
    @FXML
    private TableColumn<Venta, Void> tcAcciones;
    @FXML
    private Label lblTotalVentas;
    @FXML
    private Label lblMontoTotal;
    @FXML
    private Button btnRegresar;

    private Usuario usuarioSesion;
    private ObservableList<Venta> ventas;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColumnas();
        configurarBotones();

        ventas = FXCollections.observableArrayList();
        tvVentas.setItems(ventas);

        // Configurar fechas por defecto (último mes)
        dpFechaInicio.setValue(LocalDate.now().minusMonths(1));
        dpFechaFin.setValue(LocalDate.now());
    }

    @Override
    public void inicializarUsuario(Usuario usuario) {
        this.usuarioSesion = usuario;
        cargarVentas();
    }

    private void configurarColumnas() {
        tcFolio.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getIdVenta()).asObject());

        tcFecha.setCellValueFactory(cellData -> {
            Date fecha = cellData.getValue().getFechaVenta();
            return new SimpleStringProperty(fecha != null ? fecha.toString() : "");
        });

        tcCliente.setCellValueFactory(cellData -> {
            String nombreCliente =  String.valueOf(cellData.getValue().getIdCliente());
            return new SimpleStringProperty(nombreCliente != null ? nombreCliente : "Sin cliente");
        });

        tcSubtotal.setCellValueFactory(cellData ->
                new SimpleFloatProperty(cellData.getValue().getSubtotal().floatValue()).asObject());

        tcDescuento.setCellValueFactory(cellData ->
                new SimpleFloatProperty(cellData.getValue().getSubtotal().floatValue()).asObject());

        tcTotal.setCellValueFactory(cellData ->
                new SimpleFloatProperty(cellData.getValue().getTotalVenta().floatValue()).asObject());

        // Configurar formato para columnas de moneda
        tcSubtotal.setCellFactory(tc -> new TableCell<Venta, Float>() {
            @Override
            protected void updateItem(Float item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("$%.2f", item));
                }
            }
        });

        tcDescuento.setCellFactory(tc -> new TableCell<Venta, Float>() {
            @Override
            protected void updateItem(Float item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("$%.2f", item));
                }
            }
        });

        tcTotal.setCellFactory(tc -> new TableCell<Venta, Float>() {
            @Override
            protected void updateItem(Float item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("$%.2f", item));
                }
            }
        });

        // Configurar columna de acciones
        tcAcciones.setCellFactory(param -> new TableCell<Venta, Void>() {
            private final HBox contenedor = new HBox(5);
            private final Button btnDetalles = new Button("Detalles");
            private final Button btnImprimir = new Button("Imprimir");

            {
                btnDetalles.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
                btnImprimir.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");

                btnDetalles.setOnAction(event -> {
                    Venta venta = getTableView().getItems().get(getIndex());
//                    mostrarDetallesVenta(venta);
                });

                btnImprimir.setOnAction(event -> {
                    Venta venta = getTableView().getItems().get(getIndex());
                    imprimirVenta(venta);
                });

                contenedor.getChildren().addAll(btnDetalles, btnImprimir);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(contenedor);
                }
            }
        });
    }

    private void configurarBotones() {
        btnBuscar.setOnAction(this::buscarVentas);
        btnNuevaVenta.setOnAction(this::nuevaVenta);
        btnRegresar.setOnAction(this::regresar);
    }

    private void cargarVentas() {
        try {
            Connection conexion = ConexionBD.abrirConexion();
            if (conexion != null) {
                String busqueda = tfBusqueda.getText().trim();
                LocalDate fechaInicio = dpFechaInicio.getValue();
                LocalDate fechaFin = dpFechaFin.getValue();

                StringBuilder consulta = new StringBuilder();
                consulta.append("SELECT v.idVenta, v.fecha, v.subtotal, v.descuento, v.total, ");
                consulta.append("c.nombre AS nombreCliente, ");
                consulta.append("CONCAT(e.nombre, ' ', e.apellidoPaterno) AS nombreEmpleado, ");
                consulta.append("p.nombre AS nombrePromocion ");
                consulta.append("FROM venta v ");
                consulta.append("LEFT JOIN cliente c ON v.idCliente = c.idCliente ");
                consulta.append("LEFT JOIN usuario e ON v.idEmpleado = e.idUsuario ");
                consulta.append("LEFT JOIN promocion p ON v.idPromocion = p.idPromocion ");
                consulta.append("WHERE 1=1 ");

                if (fechaInicio != null) {
                    consulta.append("AND v.fecha >= ? ");
                }

                if (fechaFin != null) {
                    consulta.append("AND v.fecha <= ? ");
                }

                if (!busqueda.isEmpty()) {
                    consulta.append("AND (v.idVenta LIKE ? OR c.nombre LIKE ?) ");
                }

                consulta.append("ORDER BY v.fecha DESC, v.idVenta DESC");

                PreparedStatement statement = conexion.prepareStatement(consulta.toString());

                int paramIndex = 1;

                if (fechaInicio != null) {
                    statement.setDate(paramIndex++, java.sql.Date.valueOf(fechaInicio));
                }

                if (fechaFin != null) {
                    statement.setDate(paramIndex++, java.sql.Date.valueOf(fechaFin));
                }

                if (!busqueda.isEmpty()) {
                    statement.setString(paramIndex++, "%" + busqueda + "%");
                    statement.setString(paramIndex++, "%" + busqueda + "%");
                }

                ResultSet resultado = statement.executeQuery();

                ventas.clear();
                float montoTotal = 0.0f;

                while (resultado.next()) {
                    Venta venta = new Venta();
                    venta.setIdVenta(resultado.getInt("idVenta"));
                    venta.setFechaVenta(resultado.getDate("fecha"));
                    venta.setSubtotal(resultado.getDouble("subtotal"));
                    venta.setDescuento(resultado.getDouble("descuento"));
                    venta.setTotalVenta(resultado.getDouble("total"));

                    ventas.add(venta);
                    montoTotal += venta.getTotalVenta();
                }

                lblTotalVentas.setText(String.valueOf(ventas.size()));
                lblMontoTotal.setText(String.format("$%.2f", montoTotal));

                resultado.close();
                statement.close();
                conexion.close();
            }
        } catch (SQLException ex) {
            mostrarAlerta("Error al cargar las ventas: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void buscarVentas(ActionEvent event) {
        cargarVentas();
    }

    private void nuevaVenta(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/expendiocrudproyecto/vista/venta/FXMLRegistroVenta.fxml"));
            Parent vista = loader.load();

            FXMLRegistroVentaController controller = loader.getController();
            controller.inicializarUsuario(usuarioSesion);

            Stage stage = new Stage();
            stage.setScene(new Scene(vista));
            stage.setTitle("Nueva Venta");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            // Recargar ventas después de cerrar la ventana
            cargarVentas();

        } catch (IOException ex) {
            mostrarAlerta("Error al abrir la ventana de registro de venta: " + ex.getMessage(),
                    Alert.AlertType.ERROR);
        }
    }

//    private void mostrarDetallesVenta(Venta venta) {
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("/expendiocrudproyecto/vista/venta/FXMLDetalleVenta.fxml"));
//            Parent vista = loader.load();
//
////            FXMLDetalleVentaController controller = loader.getController();
////            controller.inicializarVenta(venta.getIdVenta());
//
//            Stage stage = new Stage();
//            stage.setScene(new Scene(vista));
//            stage.setTitle("Detalle de Venta #" + venta.getIdVenta());
//            stage.initModality(Modality.APPLICATION_MODAL);
//            stage.showAndWait();
//
//        } catch (IOException ex) {
//            mostrarAlerta("Error al abrir la ventana de detalle de venta: " + ex.getMessage(),
//                    Alert.AlertType.ERROR);
//        }
//    }

    private void imprimirVenta(Venta venta) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Imprimir Venta");
        alerta.setHeaderText(null);
        alerta.setContentText("Se ha enviado a imprimir la venta #" + venta.getIdVenta());
        alerta.showAndWait();
    }

    private void regresar(ActionEvent event) {
        Stage escenario = (Stage) btnRegresar.getScene().getWindow();
        escenario.close();
    }

    private void mostrarAlerta(String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle("Sistema de Gestión de Bebidas");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}