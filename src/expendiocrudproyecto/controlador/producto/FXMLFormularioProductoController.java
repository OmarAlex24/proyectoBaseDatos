package expendiocrudproyecto.controlador.producto;

import expendiocrudproyecto.controlador.FXMLPrincipalController;
import expendiocrudproyecto.modelo.ConexionBD;
import expendiocrudproyecto.modelo.dao.BebidaDAO;
import expendiocrudproyecto.modelo.pojo.Bebida;
import expendiocrudproyecto.modelo.pojo.Usuario;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import expendiocrudproyecto.utilidades.Alertas;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class FXMLFormularioProductoController implements Initializable, FXMLPrincipalController.ControladorConUsuario {

    @FXML
    private Label lblTitulo;
    @FXML
    private TextField tfNombre;
    @FXML
    private TextField tfPrecio;
    @FXML
    private Spinner<Integer> spStock;
    @FXML
    private Spinner<Integer> spStockMinimo;
    @FXML
    private Button btnGuardar;
    @FXML
    private Button btnCancelar;
    
    private Usuario usuarioSesion;
    private Bebida bebidaEdicion;
    private boolean esEdicion;
    private BebidaDAO bebidaDAO;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarSpinners();
        
        bebidaDAO = new BebidaDAO();
        
        btnGuardar.setOnAction(this::guardarProducto);
        btnCancelar.setOnAction(this::cancelar);
    }
    
    @Override
    public void inicializarUsuario(Usuario usuario) {
        this.usuarioSesion = usuario;
    }
    
    public void inicializarNuevoProducto() {
        this.esEdicion = false;
        this.bebidaEdicion = new Bebida();
        lblTitulo.setText("Nuevo Producto");
    }
    
    public void inicializarEdicionProducto(Bebida bebida) {
        this.esEdicion = true;
        this.bebidaEdicion = bebida;
        lblTitulo.setText("Editar Producto");
        
        // Cargar datos del producto en el formulario
        tfNombre.setText(bebida.getNombre());
        tfPrecio.setText(bebida.getPrecio().toString());
        spStock.getValueFactory().setValue(bebida.getStock());
        spStockMinimo.getValueFactory().setValue(bebida.getStockMinimo());
    }
    
    private void configurarSpinners() {
        SpinnerValueFactory<Integer> stockValueFactory = 
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10000, 0);
        spStock.setValueFactory(stockValueFactory);
        
        SpinnerValueFactory<Integer> stockMinimoValueFactory = 
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 1000, 0);
        spStockMinimo.setValueFactory(stockMinimoValueFactory);
    }
    
    private void guardarProducto(ActionEvent event) {
        if (validarFormulario()) {
            try {
                // Obtener datos del formulario
                bebidaEdicion.setNombre(tfNombre.getText().trim());
                bebidaEdicion.setPrecio(Float.parseFloat(tfPrecio.getText().trim()));
                bebidaEdicion.setStock(spStock.getValue());
                bebidaEdicion.setStockMinimo(spStockMinimo.getValue());
                
                boolean operacionExitosa;
                
                if (esEdicion) {
                    // Actualizar producto existente
                    operacionExitosa = bebidaDAO.actualizar(bebidaEdicion);
                    if (operacionExitosa) {
                        Alertas.crearAlerta(Alert.AlertType.INFORMATION, "Producto actualizado", "Producto actualizado correctamente");
                        cerrarVentana();
                    } else {
                        Alertas.crearAlerta(Alert.AlertType.ERROR, "Error al actualizar", "No se pudo actualizar el producto");
                    }
                } else {
                    // Insertar nuevo producto
                    Bebida bebidaInsertada = bebidaDAO.insertar(bebidaEdicion);
                    if (bebidaInsertada != null && bebidaInsertada.getId() != null) {
                        Alertas.crearAlerta(Alert.AlertType.INFORMATION, "Producto guardado", "Producto guardado correctamente");
                        cerrarVentana();
                    } else {
                        Alertas.crearAlerta(Alert.AlertType.ERROR, "Error al guardar", "No se pudo guardar el producto");
                    }
                }
                
            } catch (SQLException ex) {
                Alertas.crearAlerta(Alert.AlertType.ERROR, "Error de base de datos", "Error al guardar el producto: " + ex.getMessage());
            } catch (NumberFormatException ex) {
                Alertas.crearAlerta(Alert.AlertType.ERROR, "Error de formato", "El precio debe ser un número válido");
            }
        }
    }
    
    private boolean validarFormulario() {
        String nombre = tfNombre.getText().trim();
        String precio = tfPrecio.getText().trim();
        
        if (nombre.isEmpty()) {
            Alertas.crearAlerta(Alert.AlertType.WARNING, "Error de validación", "El nombre no puede estar vacío");
            return false;
        }
        
        if (precio.isEmpty()) {
            Alertas.crearAlerta(Alert.AlertType.WARNING, "Error de validación", "El precio no puede estar vacío");
            return false;
        }
        
        try {
            float precioFloat = Float.parseFloat(precio);
            if (precioFloat <= 0) {
                Alertas.crearAlerta(Alert.AlertType.WARNING, "Error de validación", "El precio debe ser mayor que cero");
                return false;
            }
        } catch (NumberFormatException e) {
            Alertas.crearAlerta(Alert.AlertType.WARNING, "Error de validación", "El precio debe ser un número válido");
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