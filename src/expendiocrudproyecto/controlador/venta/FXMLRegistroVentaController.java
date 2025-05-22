package expendiocrudproyecto.controlador.venta;

import expendiocrudproyecto.controlador.FXMLPrincipalController;
import expendiocrudproyecto.modelo.ConexionBD;
import expendiocrudproyecto.modelo.dao.BebidaDAO;
import expendiocrudproyecto.modelo.pojo.Bebida;
import expendiocrudproyecto.modelo.pojo.Cliente;
import expendiocrudproyecto.modelo.pojo.DetalleVenta;
import expendiocrudproyecto.modelo.pojo.Promocion;
import expendiocrudproyecto.modelo.pojo.Usuario;
import expendiocrudproyecto.modelo.pojo.Venta;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleFloatProperty;
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
    private TableColumn<DetalleVenta, Float> tcPrecioUnitario;
    @FXML
    private TableColumn<DetalleVenta, Integer> tcCantidad;
    @FXML
    private TableColumn<DetalleVenta, Float> tcSubtotal;
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
    private ObservableList<Cliente> clientes;
    private ObservableList<Promocion> promociones;
    private ObservableList<Bebida> productos;
    private ObservableList<DetalleVenta> detallesVenta;
    private BebidaDAO bebidaDAO;

    private float subtotal = 0.0f;
    private float descuento = 0.0f;
    private float total = 0.0f;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarFechaActual();
        configurarSpinner();
        configurarTabla();
        configurarBotones();

        bebidaDAO = new BebidaDAO();
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
    public void inicializarUsuario(Usuario usuario) {
        this.usuarioSesion = usuario;
        lblEmpleado.setText(usuario.getUsername() + " " + usuario.getIdEmpleado());
    }

    private void configurarFechaActual() {
        dpFechaVenta.setValue(LocalDate.now());
    }

    private void configurarSpinner() {
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1);
        spCantidad.setValueFactory(valueFactory);
    }

    private void configurarTabla() {
        Double precioUnitario = 0.0;

        tcProducto.setCellValueFactory(cellData -> {
            Bebida bebida = null;
            try {
                bebida = bebidaDAO.leerPorId(cellData.getValue().getIdProducto());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return new SimpleStringProperty(bebida != null ? bebida.getNombre() : "");
        });

        tcPrecioUnitario.setCellValueFactory(cellData ->
                new SimpleFloatProperty((float) cellData.getValue().getPrecioUnitario()).asObject());

        tcCantidad.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getCantidadUnitaria()).asObject());

        tcSubtotal.setCellValueFactory(cellData ->
                new SimpleFloatProperty((float) cellData.getValue().getTotal_pagado()).asObject());

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
            Connection conexion = ConexionBD.abrirConexion();
            if (conexion != null) {
                String consulta = "SELECT idCliente, nombre, telefono, correo, razonSocial FROM cliente";
                PreparedStatement statement = conexion.prepareStatement(consulta);
                ResultSet resultado = statement.executeQuery();

                clientes.clear();

                // Agregar opción "Sin cliente"
                Cliente sinCliente = new Cliente();
                sinCliente.setIdCliente(0);
                sinCliente.setNombre("Sin cliente");
                clientes.add(sinCliente);

                while (resultado.next()) {
                    Cliente cliente = new Cliente();
                    cliente.setIdCliente(resultado.getInt("idCliente"));
                    cliente.setNombre(resultado.getString("nombre"));
                    cliente.setTelefono(resultado.getString("telefono"));
                    cliente.setCorreo(resultado.getString("correo"));
                    cliente.setRazonSocial(resultado.getString("razonSocial"));

                    clientes.add(cliente);
                }

                cbCliente.setItems(clientes);
                cbCliente.getSelectionModel().select(0); // Seleccionar "Sin cliente" por defecto

                resultado.close();
                statement.close();
                conexion.close();
            }
        } catch (SQLException ex) {
            mostrarAlerta("Error al cargar los clientes: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void cargarPromociones() {
        try {
            Connection conexion = ConexionBD.abrirConexion();
            if (conexion != null) {
                String consulta = "SELECT idPromocion, nombre, descripcion, descuento, fechaInicio, fechaFin " +
                        "FROM promocion " +
                        "WHERE fechaInicio <= CURRENT_DATE AND fechaFin >= CURRENT_DATE";
                PreparedStatement statement = conexion.prepareStatement(consulta);
                ResultSet resultado = statement.executeQuery();

                promociones.clear();

                // Agregar opción "Sin promoción"
                Promocion sinPromocion = new Promocion();
                sinPromocion.setIdPromocion(0);
                sinPromocion.setNombre("Sin promoción");
                sinPromocion.setDescuento(0);
                promociones.add(sinPromocion);

                while (resultado.next()) {
                    Promocion promocion = new Promocion();
                    promocion.setIdPromocion(resultado.getInt("idPromocion"));
                    promocion.setNombre(resultado.getString("nombre"));
                    promocion.setDescripcion(resultado.getString("descripcion"));
                    promocion.setDescuento(resultado.getInt("descuento"));
                    promocion.setFechaInicio(resultado.getDate("fechaInicio"));
                    promocion.setFechaFin(resultado.getDate("fechaFin"));
                    promocion.setAcumulable(resultado.getBoolean("acumulable"));
                    promocion.setTerminosCondiciones(resultado.getString("terminosCondiciones"));
                    promocion.setIdProducto(resultado.getInt("idProducto"));

                    promociones.add(promocion);
                }

                cbPromocion.setItems(promociones);
                cbPromocion.getSelectionModel().select(0); // Seleccionar "Sin promoción" por defecto

                resultado.close();
                statement.close();
                conexion.close();
            }
        } catch (SQLException ex) {
            mostrarAlerta("Error al cargar las promociones: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void cargarProductos() {
        try {
            List<Bebida> listaBebidas = bebidaDAO.leerTodo();
            productos.clear();
            productos.addAll(listaBebidas);
            cbProducto.setItems(productos);

            if (!productos.isEmpty()) {
                cbProducto.getSelectionModel().select(0);
            }
        } catch (SQLException ex) {
            mostrarAlerta("Error al cargar los productos: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void agregarProducto(ActionEvent event) {
        Bebida bebidaSeleccionada = cbProducto.getSelectionModel().getSelectedItem();

        if (bebidaSeleccionada == null) {
            mostrarAlerta("Debe seleccionar un producto", Alert.AlertType.WARNING);
            return;
        }

        int cantidad = spCantidad.getValue();

        if (cantidad <= 0) {
            mostrarAlerta("La cantidad debe ser mayor que cero", Alert.AlertType.WARNING);
            return;
        }

        // Verificar si hay suficiente stock
        if (bebidaSeleccionada.getStock() < cantidad) {
            mostrarAlerta("No hay suficiente stock. Stock disponible: " + bebidaSeleccionada.getStock(),
                    Alert.AlertType.WARNING);
            return;
        }

        // Verificar si el producto ya está en la lista
        for (DetalleVenta detalle : detallesVenta) {
            if (detalle.getIdProducto() == bebidaSeleccionada.getId()) {
                // Actualizar cantidad si ya existe
                int nuevaCantidad = detalle.getCantidadUnitaria() + cantidad;

                if (bebidaSeleccionada.getStock() < nuevaCantidad) {
                    mostrarAlerta("No hay suficiente stock para agregar esa cantidad. Stock disponible: " +
                            bebidaSeleccionada.getStock(), Alert.AlertType.WARNING);
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
        subtotal = 0.0f;

        for (DetalleVenta detalle : detallesVenta) {
            subtotal += detalle.getTotal_pagado();
        }

        actualizarDescuento();
    }

    private void actualizarDescuento() {
        Promocion promocionSeleccionada = cbPromocion.getSelectionModel().getSelectedItem();

        if (promocionSeleccionada != null && promocionSeleccionada.getIdPromocion() > 0) {
            descuento = subtotal * (promocionSeleccionada.getDescuento() / 100.0f);
        } else {
            descuento = 0.0f;
        }

        total = subtotal - descuento;

        // Actualizar etiquetas
        lblSubtotal.setText(String.format("$%.2f", subtotal));
        lblDescuento.setText(String.format("$%.2f", descuento));
        lblTotal.setText(String.format("$%.2f", total));
    }

    private void registrarVenta(ActionEvent event) {
        if (detallesVenta.isEmpty()) {
            mostrarAlerta("Debe agregar al menos un producto a la venta", Alert.AlertType.WARNING);
            return;
        }

        try {
            Connection conexion = ConexionBD.abrirConexion();

            if (conexion != null) {
                conexion.setAutoCommit(false);

                try {
                    // 1. Insertar la venta
                    int idVenta = insertarVenta(conexion);

                    // 2. Insertar los detalles de la venta
                    insertarDetallesVenta(conexion, idVenta);

                    // 3. Actualizar el stock de los productos
                    actualizarStockProductos(conexion);

                    conexion.commit();
                    mostrarAlerta("Venta registrada correctamente", Alert.AlertType.INFORMATION);
                    cerrarVentana();

                } catch (SQLException ex) {
                    conexion.rollback();
                    mostrarAlerta("Error al registrar la venta: " + ex.getMessage(), Alert.AlertType.ERROR);
                } finally {
                    conexion.setAutoCommit(true);
                    conexion.close();
                }
            }
        } catch (SQLException ex) {
            mostrarAlerta("Error en la conexión a la base de datos: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private int insertarVenta(Connection conexion) throws SQLException {
        String consulta = "INSERT INTO venta (fecha, subtotal, descuento, total, idCliente, idEmpleado, idPromocion) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement statement = conexion.prepareStatement(consulta, Statement.RETURN_GENERATED_KEYS);

        // Obtener cliente seleccionado
        Cliente clienteSeleccionado = cbCliente.getSelectionModel().getSelectedItem();
        int idCliente = clienteSeleccionado != null && clienteSeleccionado.getIdCliente() > 0 ?
                clienteSeleccionado.getIdCliente() : null;

        // Obtener promoción seleccionada
        Promocion promocionSeleccionada = cbPromocion.getSelectionModel().getSelectedItem();
        int idPromocion = promocionSeleccionada != null && promocionSeleccionada.getIdPromocion() > 0 ?
                promocionSeleccionada.getIdPromocion() : null;

        statement.setDate(1, java.sql.Date.valueOf(dpFechaVenta.getValue()));
        statement.setFloat(2, subtotal);
        statement.setFloat(3, descuento);
        statement.setFloat(4, total);

        if (idCliente != 0) {
            statement.setInt(5, idCliente);
        } else {
            statement.setNull(5, java.sql.Types.INTEGER);
        }

        statement.setInt(6, usuarioSesion.getIdUsuario());

        if (idPromocion != 0) {
            statement.setInt(7, idPromocion);
        } else {
            statement.setNull(7, java.sql.Types.INTEGER);
        }

        statement.executeUpdate();

        ResultSet generatedKeys = statement.getGeneratedKeys();
        int idVenta = 0;

        if (generatedKeys.next()) {
            idVenta = generatedKeys.getInt(1);
        }

        generatedKeys.close();
        statement.close();

        return idVenta;
    }

    private void insertarDetallesVenta(Connection conexion, int idVenta) throws SQLException {
        String consulta = "INSERT INTO detalleVenta (idVenta, idBebida, cantidad, precioUnitario, subtotal) " +
                "VALUES (?, ?, ?, ?, ?)";

        PreparedStatement statement = conexion.prepareStatement(consulta);

        for (DetalleVenta detalle : detallesVenta) {
            statement.setInt(1, idVenta);
            statement.setInt(2, detalle.getBebida().getId());
            statement.setInt(3, detalle.getCantidadUnitaria());
            statement.setDouble(4, detalle.getPrecioUnitario());
            statement.setDouble(5, detalle.getTotal_pagado());

            statement.executeUpdate();
        }

        statement.close();
    }

    private void actualizarStockProductos(Connection conexion) throws SQLException {
        String consulta = "UPDATE bebida SET stock = stock - ? WHERE idBebida = ?";

        PreparedStatement statement = conexion.prepareStatement(consulta);

        for (DetalleVenta detalle : detallesVenta) {
            statement.setInt(1, detalle.getCantidadUnitaria());
            statement.setInt(2, detalle.getBebida().getId());

            statement.executeUpdate();
        }

        statement.close();
    }

    private void cancelar(ActionEvent event) {
        cerrarVentana();
    }

    private void cerrarVentana() {
        Stage escenario = (Stage) btnCancelar.getScene().getWindow();
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