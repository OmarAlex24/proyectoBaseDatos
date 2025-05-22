package expendiocrudproyecto.controlador.promocion;

import expendiocrudproyecto.controlador.FXMLPrincipalController;
import expendiocrudproyecto.modelo.ConexionBD;
import expendiocrudproyecto.modelo.pojo.Promocion;
import expendiocrudproyecto.modelo.pojo.Usuario;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import expendiocrudproyecto.utilidades.UtilPantallas;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
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
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class FXMLListaPromocionesController implements Initializable, FXMLPrincipalController.ControladorConUsuario {

    @FXML
    private TextField tfBusqueda;
    @FXML
    private Button btnBuscar;
    @FXML
    private TableView<Promocion> tvPromociones;
    @FXML
    private TableColumn<Promocion, Integer> tcId;
    @FXML
    private TableColumn<Promocion, String> tcNombre;
    @FXML
    private TableColumn<Promocion, String> tcDescripcion;
    @FXML
    private TableColumn<Promocion, Float> tcDescuento;
    @FXML
    private TableColumn<Promocion, Date> tcFechaInicio;
    @FXML
    private TableColumn<Promocion, Date> tcFechaFin;
    @FXML
    private Button btnAgregar;
    @FXML
    private Button btnEditar;
    @FXML
    private Button btnEliminar;
    @FXML
    private Button btnRegresar;

    private Usuario usuarioSesion;
    private ObservableList<Promocion> promociones;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
        configurarBotones();

        promociones = FXCollections.observableArrayList();
    }

    @Override
    public void inicializarUsuario(Usuario usuario) {
        this.usuarioSesion = usuario;
        cargarPromociones();
    }

    private void configurarTabla() {
        tcId.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getIdPromocion()).asObject());
        tcNombre.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNombre()));
        tcDescripcion.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescripcion()));
        tcDescuento.setCellValueFactory(cellData -> new SimpleFloatProperty(cellData.getValue().getDescuento()).asObject());

        // Configurar formato de fecha para las columnas de fecha
        tcFechaInicio.setCellValueFactory(cellData -> new SimpleObjectProperty(cellData.getValue().getFechaInicio()));
        tcFechaInicio.setCellFactory(column -> new TableCell<Promocion, Date>() {
            private final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

            @Override
            protected void updateItem(Date item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(format.format(item));
                }
            }
        });

        tcFechaFin.setCellValueFactory(cellData -> new SimpleObjectProperty(cellData.getValue().getFechaFin()));
        tcFechaFin.setCellFactory(column -> new TableCell<Promocion, Date>() {
            private final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

            @Override
            protected void updateItem(Date item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(format.format(item));
                }
            }
        });
    }

    private void configurarBotones() {
        btnBuscar.setOnAction(this::buscarPromociones);
        btnAgregar.setOnAction(this::agregarPromocion);
        btnEditar.setOnAction(this::editarPromocion);
        btnEliminar.setOnAction(this::eliminarPromocion);
        btnRegresar.setOnAction(this::regresar);
    }

    private void cargarPromociones() {
        try {
            List<Promocion> listaPromociones = obtenerTodasLasPromociones();
            promociones.clear();
            promociones.addAll(listaPromociones);
            tvPromociones.setItems(promociones);
        } catch (SQLException ex) {
            mostrarAlerta("Error al cargar las promociones: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private List<Promocion> obtenerTodasLasPromociones() throws SQLException {
        List<Promocion> listaPromociones = new ArrayList<>();
        Connection conexion = ConexionBD.abrirConexion();

        if (conexion != null) {
            String consulta = "SELECT idPromocion, nombre, descripcion, descuento, fechaInicio, fechaFin, terminosCondiciones, Producto_idProducto, acumulable FROM promocion";
            PreparedStatement statement = conexion.prepareStatement(consulta);
            ResultSet resultado = statement.executeQuery();

            while (resultado.next()) {
                Promocion promocion = new Promocion();
                promocion.setIdPromocion(resultado.getInt("idPromocion"));
                promocion.setNombre(resultado.getString("nombre"));
                promocion.setDescripcion(resultado.getString("descripcion"));
                promocion.setDescuento(resultado.getInt("descuento"));
                promocion.setFechaInicio(resultado.getDate("fechaInicio"));
                promocion.setFechaFin(resultado.getDate("fechaFin"));
                promocion.setTerminosCondiciones(resultado.getString("terminosCondiciones"));
                promocion.setIdProducto(resultado.getInt("Producto_idProducto"));
                promocion.setAcumulable(resultado.getBoolean("acumulable"));

                listaPromociones.add(promocion);
            }

            resultado.close();
            statement.close();
            conexion.close();
        }

        return listaPromociones;
    }

    private void buscarPromociones(ActionEvent event) {
        String criterioBusqueda = tfBusqueda.getText().trim();

        if (criterioBusqueda.isEmpty()) {
            cargarPromociones();
        } else {
            try {
                List<Promocion> resultadosBusqueda = buscarPromocionesPorNombre(criterioBusqueda);
                promociones.clear();
                promociones.addAll(resultadosBusqueda);
                tvPromociones.setItems(promociones);
            } catch (SQLException ex) {
                mostrarAlerta("Error al buscar promociones: " + ex.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    private List<Promocion> buscarPromocionesPorNombre(String nombre) throws SQLException {
        List<Promocion> listaPromociones = new ArrayList<>();
        Connection conexion = ConexionBD.abrirConexion();

        if (conexion != null) {
            String consulta = "SELECT idPromocion, nombre, descripcion, descuento, fechaInicio, fechaFin, terminosCondiciones, Producto_idProducto, acumulable FROM promocion " +
                    "WHERE nombre LIKE ?";
            PreparedStatement statement = conexion.prepareStatement(consulta);
            statement.setString(1, "%" + nombre + "%");

            ResultSet resultado = statement.executeQuery();

            while (resultado.next()) {
                Promocion promocion = new Promocion();
                promocion.setIdPromocion(resultado.getInt("idPromocion"));
                promocion.setNombre(resultado.getString("nombre"));
                promocion.setDescripcion(resultado.getString("descripcion"));
                promocion.setDescuento(resultado.getInt("descuento"));
                promocion.setFechaInicio(resultado.getDate("fechaInicio"));
                promocion.setFechaFin(resultado.getDate("fechaFin"));
                promocion.setTerminosCondiciones(resultado.getString("terminosCondiciones"));
                promocion.setIdProducto(resultado.getInt("Producto_idProducto"));
                promocion.setAcumulable(resultado.getBoolean("acumulable"));

                listaPromociones.add(promocion);
            }

            resultado.close();
            statement.close();
            conexion.close();
        }

        return listaPromociones;
    }

    private void agregarPromocion(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/expendiocrudproyecto/vista/promocion/FXMLFormularioPromocion.fxml"));
            Parent vista = loader.load();

            FXMLFormularioPromocionController controller = loader.getController();
            controller.inicializarUsuario(usuarioSesion);
            controller.inicializarNuevaPromocion();

            Stage stage = new Stage();
            stage.setScene(new Scene(vista));
            stage.setTitle("Nueva Promoción");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            cargarPromociones();
        } catch (IOException ex) {
            mostrarAlerta("Error al abrir el formulario: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void editarPromocion(ActionEvent event) {
        Promocion promocionSeleccionada = tvPromociones.getSelectionModel().getSelectedItem();

        if (promocionSeleccionada != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/expendiocrudproyecto/vista/promocion/FXMLFormularioPromocion.fxml"));
                Parent vista = loader.load();

                FXMLFormularioPromocionController controller = loader.getController();
                controller.inicializarUsuario(usuarioSesion);
                controller.inicializarEdicionPromocion(promocionSeleccionada);

                Stage stage = new Stage();
                stage.setScene(new Scene(vista));
                stage.setTitle("Editar Promoción");
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.showAndWait();

                cargarPromociones();
            } catch (IOException ex) {
                mostrarAlerta("Error al abrir el formulario: " + ex.getMessage(), Alert.AlertType.ERROR);
            }
        } else {
            mostrarAlerta("Debe seleccionar una promoción para editar", Alert.AlertType.WARNING);
        }
    }

    private void eliminarPromocion(ActionEvent event) {
        Promocion promocionSeleccionada = tvPromociones.getSelectionModel().getSelectedItem();

        if (promocionSeleccionada != null) {
            Optional<ButtonType> respuesta = mostrarConfirmacion("¿Está seguro que desea eliminar la promoción '" +
                    promocionSeleccionada.getNombre() + "'?");

            if (respuesta.isPresent() && respuesta.get() == ButtonType.OK) {
                try {
                    boolean eliminacionExitosa = eliminarPromocionBD(promocionSeleccionada.getIdPromocion());

                    if (eliminacionExitosa) {
                        mostrarAlerta("Promoción eliminada correctamente", Alert.AlertType.INFORMATION);
                        cargarPromociones();
                    } else {
                        mostrarAlerta("No se pudo eliminar la promoción", Alert.AlertType.ERROR);
                    }
                } catch (SQLException ex) {
                    mostrarAlerta("Error al eliminar la promoción: " + ex.getMessage(), Alert.AlertType.ERROR);
                }
            }
        } else {
            mostrarAlerta("Debe seleccionar una promoción para eliminar", Alert.AlertType.WARNING);
        }
    }

    private boolean eliminarPromocionBD(int idPromocion) throws SQLException {
        Connection conexion = ConexionBD.abrirConexion();
        boolean eliminacionExitosa = false;

        if (conexion != null) {
            String consulta = "DELETE FROM promocion WHERE idPromocion = ?";
            PreparedStatement statement = conexion.prepareStatement(consulta);
            statement.setInt(1, idPromocion);

            int filasAfectadas = statement.executeUpdate();
            eliminacionExitosa = filasAfectadas > 0;

            statement.close();
            conexion.close();
        }

        return eliminacionExitosa;
    }

    private void regresar(ActionEvent event) {
        UtilPantallas.regresarPrincipal(event, usuarioSesion, btnRegresar);
    }

    private void mostrarAlerta(String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle("Sistema de Gestión de Bebidas");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    private Optional<ButtonType> mostrarConfirmacion(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle("Confirmación");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        return alerta.showAndWait();
    }
}