package expendiocrudproyecto.controlador;

import expendiocrudproyecto.App;
import expendiocrudproyecto.modelo.pojo.TipoUsuario;
import expendiocrudproyecto.modelo.pojo.Usuario;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import expendiocrudproyecto.utilidades.Alertas;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class FXMLPrincipalController implements Initializable {

    @FXML
    private Label lblBienvenida;
    @FXML
    private Label lblTipoUsuario;
    @FXML
    private Button btnVentas;
    @FXML
    private Button btnPromociones;
    @FXML
    private Button btnClientes;
    @FXML
    private Button btnPedidosCliente;
    @FXML
    private Button btnPedidosMercancia;
    @FXML
    private Button btnProveedores;
    @FXML
    private Button btnProductos;
    @FXML
    private Button btnCerrarSesion;
    @FXML
    private VBox menuLateral;

    private Usuario usuarioSesion;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Configurar eventos de botones
        btnVentas.setOnAction(this::irVentas);
        btnPromociones.setOnAction(this::irPromociones);
        btnClientes.setOnAction(this::irClientes);
        btnPedidosCliente.setOnAction(this::irPedidosCliente);
        btnPedidosMercancia.setOnAction(this::irPedidosMercancia);
        btnProveedores.setOnAction(this::irProveedores);
        btnProductos.setOnAction(this::irProductos);
        btnCerrarSesion.setOnAction(this::cerrarSesion);
    }

    public void inicializarUsuario(Usuario usuario) {
        this.usuarioSesion = usuario;

        if (usuario != null) {
            lblBienvenida.setText("Bienvenido(a), " + usuario.getUsername());
            lblTipoUsuario.setText("Rol: " + usuario.getTipoUsuario().getNombre());

            configurarPermisos();
        }
    }

    private void configurarPermisos() {
        if (usuarioSesion.getTipoUsuario() == TipoUsuario.EMPLEADO) {
            btnPromociones.setDisable(true);
            btnPromociones.setVisible(false);
            btnPedidosMercancia.setVisible(false);
            btnPedidosMercancia.setDisable(true);
            btnProveedores.setDisable(true);
            btnProveedores.setVisible(false);
        }
        // El administrador tiene acceso a todo por defecto
    }

    private void irVentas(ActionEvent event) {
        cargarInterfaz("venta/FXMLListaVenta.fxml", "Gestión de Ventas");
    }

    private void irPromociones(ActionEvent event) {
        cargarInterfaz("promocion/FXMLListaPromociones.fxml", "Gestión de Promociones");
    }

    private void irClientes(ActionEvent event) {
        cargarInterfaz("cliente/FXMLListaClientes.fxml", "Gestión de Clientes");
    }

    private void irPedidosCliente(ActionEvent event) {
        cargarInterfaz("pedidoCliente/FXMLListaPedidosCliente.fxml", "Gestión de Pedidos de Clientes");
    }

    private void irPedidosMercancia(ActionEvent event) {
        cargarInterfaz("pedidoMercancia/FXMLListaPedidosMercancia.fxml", "Gestión de Pedidos de Mercancía");
    }

    private void irProveedores(ActionEvent event) {
        cargarInterfaz("proveedor/FXMLListaProveedores.fxml", "Gestión de Proveedores");
    }

    private void irProductos(ActionEvent event) {
        cargarInterfaz("producto/FXMLListaProductos.fxml", "Gestión de Productos");
    }

    private void cerrarSesion(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/expendiocrudproyecto/vista/FXMLInicioSesion.fxml"));
            Parent vista = loader.load();

            Scene escenaLogin = new Scene(vista);
            Stage escenarioBase = (Stage) btnCerrarSesion.getScene().getWindow();

            escenarioBase.setScene(escenaLogin);
            escenarioBase.setTitle("Inicio de sesión");
            escenarioBase.show();

        } catch (IOException ex) {
            Alertas.crearAlerta(Alert.AlertType.ERROR, "Error al cerrar sesión", "Error al cargar la pantalla de inicio de sesión: " + ex.getMessage());
        }
    }

    private void cargarInterfaz(String rutaFXML, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("vista/" + rutaFXML));
            Parent vista = loader.load();

            // Pasar el usuario en sesión al controlador si es necesario
            Object controlador = loader.getController();
            if (controlador instanceof ControladorConUsuario) {
                ((ControladorConUsuario) controlador).inicializarUsuario(usuarioSesion);
            }

            Scene nuevaEscena = new Scene(vista);
            Stage escenarioActual = (Stage) menuLateral.getScene().getWindow();

            escenarioActual.setScene(nuevaEscena);
            escenarioActual.setTitle(titulo);
            escenarioActual.initModality(Modality.APPLICATION_MODAL);
            escenarioActual.showAndWait();

        } catch (IOException ex) {
            Alertas.crearAlerta(Alert.AlertType.ERROR, "Error al cargar la interfaz", "Error al cargar la interfaz: " + ex.getMessage());
        }
    }

    // Interfaz para controladores que necesitan el usuario en sesión
    public interface ControladorConUsuario {
        void inicializarUsuario(Usuario usuario);
    }
}