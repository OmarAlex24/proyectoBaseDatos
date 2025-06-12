package expendiocrudproyecto.controlador;

import expendiocrudproyecto.App;
import expendiocrudproyecto.modelo.pojo.TipoUsuario;
import expendiocrudproyecto.modelo.pojo.Usuario;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import expendiocrudproyecto.utilidades.SesionUsuario;
import expendiocrudproyecto.utilidades.UtilPantallas;
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
import javafx.scene.layout.BorderPane;

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
  private Button btnReportes;
  @FXML
  private Button btnCerrarSesion;
  @FXML
  private VBox menuLateral;

  private Usuario usuarioSesion;

  @Override
  public void initialize(URL url, ResourceBundle rb) {
    // Configurar eventos de botones de forma homogénea
    btnVentas.setOnAction(this::irVentas);
    btnPromociones.setOnAction(this::irPromociones);
    btnClientes.setOnAction(this::irClientes);
    btnPedidosCliente.setOnAction(this::irPedidosCliente);
    btnPedidosMercancia.setOnAction(this::irPedidosMercancia);
    btnProveedores.setOnAction(this::irProveedores);
    btnProductos.setOnAction(this::irProductos);
    btnReportes.setOnAction(this::irReportes);
    btnCerrarSesion.setOnAction(this::cerrarSesion);
  }

  /**
   * Inicializa el usuario en sesión y configura la interfaz según sus permisos
   */
  public void inicializarUsuario(Usuario usuario) {
    this.usuarioSesion = usuario;

    if (usuario != null) {
      lblBienvenida.setText("Bienvenido(a), " + usuario.getUsername());
      lblTipoUsuario.setText("Rol: " + usuario.getTipoUsuario().getNombre());
      configurarPermisos();
    }
  }

  /**
   * Configura los permisos de acceso según el tipo de usuario
   */
  private void configurarPermisos() {
    if (usuarioSesion.getTipoUsuario() == TipoUsuario.EMPLEADO) {
      // Ocultar y deshabilitar botones para empleados
      ocultarBoton(btnPromociones);
      ocultarBoton(btnPedidosMercancia);
      ocultarBoton(btnProveedores);
      ocultarBoton(btnReportes); // Los empleados no tienen acceso a reportes
      ocultarBoton(btnReportes);
    }
    // El administrador tiene acceso completo por defecto
  }

  /**
   * Método auxiliar para ocultar y deshabilitar botones
   */
  private void ocultarBoton(Button boton) {
    boton.setVisible(false);
    boton.setDisable(true);
  }

  @FXML
  private void irVentas(ActionEvent event) {
    cargarInterfaz("venta/FXMLListaVenta.fxml", "Gestión de Ventas");
  }

  @FXML
  private void irPromociones(ActionEvent event) {
    cargarInterfaz("promocion/FXMLListaPromociones.fxml", "Gestión de Promociones");
  }

  @FXML
  private void irClientes(ActionEvent event) {
    cargarInterfaz("cliente/FXMLListaClientes.fxml", "Gestión de Clientes");
  }

  @FXML
  private void irPedidosCliente(ActionEvent event) {
    cargarInterfaz("pedidocliente/FXMLListaPedidosCliente.fxml", "Gestión de Pedidos de Clientes");
  }

  @FXML
  private void irPedidosMercancia(ActionEvent event) {
    cargarInterfaz("pedidomercancia/FXMLListaPedidosMercancia.fxml", "Gestión de Pedidos de Mercancía");
  }

  @FXML
  private void irProveedores(ActionEvent event) {
    cargarInterfaz("proveedor/FXMLListaProveedores.fxml", "Gestión de Proveedores");
  }

  @FXML
  private void irProductos(ActionEvent event) {
    cargarInterfaz("producto/FXMLListaProductos.fxml", "Gestión de Productos");
  }

  @FXML
  private void irReportes(ActionEvent event) {
    cargarInterfaz("reporte/FXMLReportes.fxml", "Reporte de Productos");
  }

  @FXML
  private void cerrarSesion(ActionEvent event) {
    try {
      SesionUsuario.getInstancia().cerrarSesion();
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/expendiocrudproyecto/vista/FXMLInicioSesion.fxml"));
      Parent vista = loader.load();

      Scene escenaLogin = new Scene(vista);
      Stage escenarioBase = (Stage) btnCerrarSesion.getScene().getWindow();

      escenarioBase.setScene(escenaLogin);
      escenarioBase.setTitle("Inicio de sesión");
      escenarioBase.show();

    } catch (IOException ex) {
      Alertas.crearAlerta(Alert.AlertType.ERROR, "Error al cerrar sesión",
              "Error al cargar la pantalla de inicio de sesión: " + ex.getMessage());
    }
  }

  // Método para cargar interfaces en el panel central
  private void cargarInterfaz(String rutaFXML, String titulo) {
    try {
      FXMLLoader loader = new FXMLLoader(App.class.getResource("vista/" + rutaFXML));
      Parent vista = loader.load();

      // Inicializar controlador si implementa la interfaz ControladorConUsuario
      Object controlador = loader.getController();
      if (controlador instanceof ControladorConUsuario) {
        ((ControladorConUsuario) controlador).inicializar(usuarioSesion, this);
      }

      // Cargar la vista en el panel central
      BorderPane panePadre = (BorderPane) menuLateral.getScene().getRoot();
      panePadre.setCenter(vista);

    } catch (IOException ex) {
      Alertas.crearAlerta(Alert.AlertType.ERROR, "Error al cargar la interfaz",
              "Error al cargar " + titulo + ": " + ex.getMessage());
      System.err.println("Error cargando " + rutaFXML + ": " + ex.getMessage());
      ex.printStackTrace();
    }
  }

  /**
   * Restaura la vista principal en el panel central
   */
  public void restaurarVistaPrincipal() {
    try {
      FXMLLoader loader = new FXMLLoader(App.class.getResource("vista/FXMLPrincipal.fxml"));
      Parent vista = loader.load();
      BorderPane panePadre = (BorderPane) menuLateral.getScene().getRoot();
      panePadre.setCenter(((BorderPane) vista).getCenter());
    } catch (IOException e) {
      System.err.println("Error al restaurar vista principal: " + e.getMessage());
      e.printStackTrace();
    }
  }

  public interface ControladorConUsuario {
    void inicializar(Usuario usuario, FXMLPrincipalController principalController);
  }
}