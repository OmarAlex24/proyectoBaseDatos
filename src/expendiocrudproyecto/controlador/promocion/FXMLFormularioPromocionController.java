package expendiocrudproyecto.controlador.promocion;

import expendiocrudproyecto.controlador.FXMLPrincipalController;
import expendiocrudproyecto.modelo.ConexionBD;
import expendiocrudproyecto.modelo.pojo.Promocion;
import expendiocrudproyecto.modelo.pojo.Usuario;
import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ResourceBundle;

import expendiocrudproyecto.utilidades.Alertas;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class FXMLFormularioPromocionController implements Initializable, FXMLPrincipalController.ControladorConUsuario {

    @FXML
    private Label lblTitulo;
    @FXML
    private TextField tfNombre;
    @FXML
    private TextArea taDescripcion;
    @FXML
    private TextField tfDescuento;
    @FXML
    private DatePicker dpFechaInicio;
    @FXML
    private DatePicker dpFechaFin;
    @FXML
    private Button btnGuardar;
    @FXML
    private Button btnCancelar;
    
    private Usuario usuarioSesion;
    private Promocion promocionEdicion;
    private boolean esEdicion;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        btnGuardar.setOnAction(this::guardarPromocion);
        btnCancelar.setOnAction(this::cancelar);
        
        // Establecer fecha actual como predeterminada
        dpFechaInicio.setValue(LocalDate.now());
        dpFechaFin.setValue(LocalDate.now().plusDays(7));
    }
    
    @Override
    public void inicializarUsuario(Usuario usuario) {
        this.usuarioSesion = usuario;
    }
    
    public void inicializarNuevaPromocion() {
        this.esEdicion = false;
        this.promocionEdicion = new Promocion();
        lblTitulo.setText("Nueva Promoción");
    }
    
    public void inicializarEdicionPromocion(Promocion promocion) {
        this.esEdicion = true;
        this.promocionEdicion = promocion;
        lblTitulo.setText("Editar Promoción");
        
        // Cargar datos de la promoción en el formulario
        taDescripcion.setText(promocion.getDescripcion());
        tfDescuento.setText(String.valueOf(promocion.getDescuento()));
        
        // Convertir java.util.Date a LocalDate para DatePicker
        if (promocion.getFechaInicio() != null) {
            dpFechaInicio.setValue(new java.sql.Date(promocion.getFechaInicio().getTime()).toLocalDate());
        }
        
        if (promocion.getFechaFin() != null) {
            dpFechaFin.setValue(new java.sql.Date(promocion.getFechaFin().getTime()).toLocalDate());
        }
    }
    
    private void guardarPromocion(ActionEvent event) {
        if (validarFormulario()) {
            try {
                // Obtener datos del formulario
                promocionEdicion.setDescripcion(taDescripcion.getText().trim());
                promocionEdicion.setDescuento(Integer.parseInt(tfDescuento.getText().trim()));
                promocionEdicion.setFechaInicio(Date.valueOf(dpFechaInicio.getValue()));
                promocionEdicion.setFechaFin(Date.valueOf(dpFechaFin.getValue()));
                
                Connection conexion = ConexionBD.abrirConexion();
                
                if (conexion != null) {
                    boolean operacionExitosa;
                    
                    if (esEdicion) {
                        // Actualizar promoción existente
                        String consulta = "UPDATE promocion SET descripcion = ?, descuento = ?, fechaInicio = ?, fechaFin = ?, terminosCondiciones = ?, Producto_idProducto = ?, acumulable = ? WHERE idPromocion = ?";
                        
                        PreparedStatement statement = conexion.prepareStatement(consulta);
                        statement.setString(1, promocionEdicion.getDescripcion());
                        statement.setFloat(2, promocionEdicion.getDescuento());
                        statement.setDate(3, new java.sql.Date(promocionEdicion.getFechaInicio().getTime()));
                        statement.setDate(4, new java.sql.Date(promocionEdicion.getFechaFin().getTime()));
                        statement.setString(5, promocionEdicion.getTerminosCondiciones());
                        statement.setInt(6, promocionEdicion.getIdProducto());
                        statement.setBoolean(7, promocionEdicion.isAcumulable());
                        statement.setInt(8, promocionEdicion.getIdPromocion());

                        
                        int filasActualizadas = statement.executeUpdate();
                        operacionExitosa = filasActualizadas > 0;
                        
                        statement.close();
                        
                        if (operacionExitosa) {
                            Alertas.crearAlerta(Alert.AlertType.INFORMATION, "Promoción actualizada", "Promoción actualizada correctamente");
                            cerrarVentana();
                        } else {
                            Alertas.crearAlerta(Alert.AlertType.ERROR, "Error al actualizar promoción", "No se pudo actualizar la promoción");
                        }
                    } else {
                        // Insertar nueva promoción
                        String consulta = "INSERT INTO promocion (descripcion, descuento, fechaInicio, fechaFin, terminosCondiciones, Producto_idProducto, acumulable) VALUES (?, ?, ?, ?, ?, ?, ?)";
                        
                        PreparedStatement statement = conexion.prepareStatement(consulta, Statement.RETURN_GENERATED_KEYS);
                        statement.setString(1, promocionEdicion.getDescripcion());
                        statement.setInt(2, promocionEdicion.getDescuento());
                        statement.setDate(3, new java.sql.Date(promocionEdicion.getFechaInicio().getTime()));
                        statement.setDate(4, new java.sql.Date(promocionEdicion.getFechaFin().getTime()));
                        statement.setString(5, promocionEdicion.getTerminosCondiciones());
                        statement.setInt(6, promocionEdicion.getIdProducto());
                        statement.setBoolean(7, promocionEdicion.isAcumulable());
                        
                        int filasInsertadas = statement.executeUpdate();
                        
                        if (filasInsertadas > 0) {
                            ResultSet generatedKeys = statement.getGeneratedKeys();
                            if (generatedKeys.next()) {
                                promocionEdicion.setIdPromocion(generatedKeys.getInt(1));
                                Alertas.crearAlerta(Alert.AlertType.INFORMATION, "Promoción guardada", "Promoción guardada correctamente");
                                cerrarVentana();
                            }
                        } else {
                            Alertas.crearAlerta(Alert.AlertType.ERROR, "Error al guardar promoción", "No se pudo guardar la promoción");
                        }
                        
                        statement.close();
                    }
                    
                    conexion.close();
                } else {
                    Alertas.crearAlerta(Alert.AlertType.ERROR, "Error de conexión", "No se pudo conectar a la base de datos, verifica tu conexión");
                }
                
            } catch (SQLException ex) {
                Alertas.crearAlerta(Alert.AlertType.ERROR, "Error de base de datos", "Error al guardar la promoción, intentalo mas tarde");
            } catch (NumberFormatException ex) {
                Alertas.crearAlerta(Alert.AlertType.ERROR, "Error de formato", "El descuento debe ser un número válido");
            }
        }
    }
    
    private boolean validarFormulario() {
        String nombre = tfNombre.getText().trim();
        String descuento = tfDescuento.getText().trim();
        LocalDate fechaInicio = dpFechaInicio.getValue();
        LocalDate fechaFin = dpFechaFin.getValue();
        
        if (nombre.isEmpty()) {
            Alertas.crearAlerta(Alert.AlertType.WARNING, "Error de validación", "El nombre no puede estar vacío");
            return false;
        }
        
        if (descuento.isEmpty()) {
            Alertas.crearAlerta(Alert.AlertType.WARNING, "Error de validación", "El descuento no puede estar vacío");
            return false;
        }
        
        try {
            int descuentoInt = Integer.parseInt(descuento);
            if (descuentoInt <= 0 || descuentoInt > 100) {
                Alertas.crearAlerta(Alert.AlertType.WARNING, "Error de validación", "El descuento debe ser un número entre 1 y 100");
                return false;
            }
        } catch (NumberFormatException e) {
            Alertas.crearAlerta(Alert.AlertType.WARNING, "Error de validación", "El descuento debe ser un número válido");
            return false;
        }
        
        if (fechaInicio == null) {
            Alertas.crearAlerta(Alert.AlertType.WARNING, "Error de validación", "La fecha de inicio no puede estar vacía");
            return false;
        }
        
        if (fechaFin == null) {
            Alertas.crearAlerta(Alert.AlertType.WARNING, "Error de validación", "La fecha de fin no puede estar vacía");
            return false;
        }
        
        if (fechaFin.isBefore(fechaInicio)) {
            Alertas.crearAlerta(Alert.AlertType.WARNING, "Error de validación", "La fecha de fin no puede ser anterior a la fecha de inicio");
            return false;
        }
        
        return true;
    }
    
    private void cancelar(ActionEvent event) {
        cerrarVentana();
    }
    
    private void cerrarVentana() {
        Stage escenario = (Stage) btnCancelar.getScene().getWindow();
        escenario.close();
    }
}