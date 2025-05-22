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
import java.util.Date;
import java.util.ResourceBundle;

import expendiocrudproyecto.utilidades.Alertas;
import expendiocrudproyecto.utilidades.UtilPantallas;
import javafx.beans.property.SimpleDoubleProperty;
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
    private TableColumn<Venta, Double> tcSubtotal;
    @FXML
    private TableColumn<Venta, Double> tcDescuento;
    @FXML
    private TableColumn<Venta, Double> tcTotal;
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
            String nombreCliente = String.valueOf(cellData.getValue().getIdCliente());
            return new SimpleStringProperty(nombreCliente != null ? nombreCliente : "Sin cliente");
        });

        tcSubtotal.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(cellData.getValue().getSubtotal()).asObject());

        tcDescuento.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(cellData.getValue().getDescuento()).asObject());

        tcTotal.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(cellData.getValue().getTotalVenta()).asObject());

        // Configurar formato para columnas de moneda
        tcSubtotal.setCellFactory(tc -> new TableCell<Venta, Double>() {
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

        tcDescuento.setCellFactory(tc -> new TableCell<Venta, Double>() {
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

        tcTotal.setCellFactory(tc -> new TableCell<Venta, Double>() {
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
                    mostrarDetallesVenta(venta);
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
                consulta.append("SELECT v.idVenta, v.fechaVenta, v.Cliente_idCliente, v.folioFactura, ");
                consulta.append("c.nombre AS nombreCliente, ");
                consulta.append("SUM(dv.total_pagado) AS total ");
                consulta.append("FROM venta v ");
                consulta.append("LEFT JOIN cliente c ON v.Cliente_idCliente = c.idCliente ");
                consulta.append("LEFT JOIN detalle_venta dv ON v.idVenta = dv.Venta_idVenta ");
                consulta.append("WHERE 1=1 ");

                if (fechaInicio != null) {
                    consulta.append("AND DATE(v.fechaVenta) >= ? ");
                }

                if (fechaFin != null) {
                    consulta.append("AND DATE(v.fechaVenta) <= ? ");
                }

                if (!busqueda.isEmpty()) {
                    consulta.append("AND (v.idVenta LIKE ? OR c.nombre LIKE ? OR v.folioFactura LIKE ?) ");
                }

                consulta.append("GROUP BY v.idVenta, v.fechaVenta, v.Cliente_idCliente, v.folioFactura, c.nombre ");
                consulta.append("ORDER BY v.fechaVenta DESC, v.idVenta DESC");

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
                    statement.setString(paramIndex++, "%" + busqueda + "%");
                }

                ResultSet resultado = statement.executeQuery();

                ventas.clear();
                double montoTotal = 0.0;

                while (resultado.next()) {
                    Venta venta = new Venta();
                    venta.setIdVenta(resultado.getInt("idVenta"));
                    venta.setFechaVenta(resultado.getTimestamp("fechaVenta"));
                    venta.setIdCliente(resultado.getInt("Cliente_idCliente"));
                    venta.setFolioFactura(resultado.getString("folioFactura"));

                    // Calcular subtotal, descuento y total
                    double total = resultado.getDouble("total");
                    venta.setTotalVenta(total);
                    venta.setSubtotal(total); // Por ahora, asumimos que no hay descuento
                    venta.setDescuento(0.0);

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
            Alertas.crearAlerta(Alert.AlertType.ERROR, "Error",
                    "Error al cargar las ventas: " + ex.getMessage());
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
            Alertas.crearAlerta(Alert.AlertType.ERROR, "Error",
                    "Error al abrir la ventana de registro de venta: " + ex.getMessage());
        }
    }

    private void mostrarDetallesVenta(Venta venta) {
        try {
            // Mostrar detalles de la venta en una alerta por ahora
            StringBuilder detalles = new StringBuilder();
            detalles.append("Detalles de la Venta #").append(venta.getIdVenta()).append("\n\n");
            detalles.append("Fecha: ").append(venta.getFechaVenta()).append("\n");
            detalles.append("Folio: ").append(venta.getFolioFactura()).append("\n");
            detalles.append("Cliente ID: ").append(venta.getIdCliente()).append("\n");
            detalles.append("Total: $").append(String.format("%.2f", venta.getTotalVenta()));

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Detalles de Venta");
            alert.setHeaderText("Venta #" + venta.getIdVenta());
            alert.setContentText(detalles.toString());
            alert.showAndWait();

        } catch (Exception ex) {
            Alertas.crearAlerta(Alert.AlertType.ERROR, "Error",
                    "Error al mostrar detalles de la venta: " + ex.getMessage());
        }
    }

    private void imprimirVenta(Venta venta) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Imprimir Venta");
        alerta.setHeaderText(null);
        alerta.setContentText("Se ha enviado a imprimir la venta #" + venta.getIdVenta());
        alerta.showAndWait();
    }

    private void regresar(ActionEvent event) {
        UtilPantallas.regresarPrincipal(event, usuarioSesion, btnRegresar);
    }
}