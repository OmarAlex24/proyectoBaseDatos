package expendiocrudproyecto.controlador.pedidomercancia;

import expendiocrudproyecto.modelo.dao.BebidaDAO;
import expendiocrudproyecto.modelo.pojo.Bebida;
import expendiocrudproyecto.modelo.pojo.Usuario;
import expendiocrudproyecto.utilidades.Alertas;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import javafx.beans.property.ReadOnlyObjectWrapper;

public class FXMLSeleccionarProductoController implements Initializable {

  @FXML
  private TableView<Bebida> tvProductos;
  @FXML
  private TableColumn<Bebida, String> tcNombre;
  @FXML
  private TableColumn<Bebida, Number> tcPrecio;
  @FXML
  private TableColumn<Bebida, Number> tcStock;
  @FXML
  private Button btnAgregar;
  @FXML
  private Button btnCancelar;

  private Usuario usuarioSesion;
  private FXMLFormularioPedidoMercanciaController mainController;
  private BebidaDAO bebidaDAO = new BebidaDAO();
  private ObservableList<Bebida> productos = FXCollections.observableArrayList();

  @Override
  public void initialize(URL url, ResourceBundle rb) {
    configurarTabla();
  }

  public void inicializar(FXMLFormularioPedidoMercanciaController mainController, Usuario usuarioSesion) {
    this.mainController = mainController;
    this.usuarioSesion = usuarioSesion;
    cargarProductos();
  }

  private void configurarTabla() {
    tcNombre.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getNombre()));
    tcPrecio.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getPrecio()));
    tcStock.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getStock()));
  }

  private void cargarProductos() {
    try {
      productos.clear();
      productos.addAll(bebidaDAO.leerTodo());
      tvProductos.setItems(productos);
    } catch (SQLException ex) {
      Alertas.crearAlerta(Alert.AlertType.ERROR, "Error al cargar productos", ex.getMessage());
    }
  }

  @FXML
  private void agregar(ActionEvent event) {
    Bebida seleccionada = tvProductos.getSelectionModel().getSelectedItem();
    if (seleccionada != null) {
      mainController.agregarProductoDesdeVentana(seleccionada);
      cerrarVentana();
    } else {
      Alertas.crearAlerta(Alert.AlertType.WARNING, "Selección", "Seleccione un producto para agregar");
    }
  }

  @FXML
  private void cancelar(ActionEvent event) {
    cerrarVentana();
  }

  private void cerrarVentana() {
    Stage stage = (Stage) tvProductos.getScene().getWindow();
    stage.close();
  }
}