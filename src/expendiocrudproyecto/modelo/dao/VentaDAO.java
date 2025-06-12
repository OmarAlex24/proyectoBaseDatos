package expendiocrudproyecto.modelo.dao;

import com.google.gson.Gson;
import expendiocrudproyecto.modelo.ConexionBD;
import expendiocrudproyecto.modelo.pojo.Cliente;
import expendiocrudproyecto.modelo.pojo.DetalleVenta;
import expendiocrudproyecto.modelo.pojo.Venta;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class VentaDAO implements CrudDAO<Venta> {

  public static class DetalleVentaJSON {
    private int idProducto;
    private int cantidad;
    private Integer idPromocion; // Puede ser null

    public DetalleVentaJSON(int idProducto, int cantidad) {
      this.idProducto = idProducto;
      this.cantidad = cantidad;
    }

    // Getters y setters
    public int getIdProducto() {
      return idProducto;
    }

    public void setIdProducto(int idProducto) {
      this.idProducto = idProducto;
    }

    public int getCantidad() {
      return cantidad;
    }

    public void setCantidad(int cantidad) {
      this.cantidad = cantidad;
    }

    public Integer getIdPromocion() {
      return idPromocion;
    }

    public void setIdPromocion(Integer idPromocion) {
      this.idPromocion = idPromocion;
    }
  }

  public static class ResultadoVenta {
    private boolean exito;
    private Integer idVenta;
    private String mensaje;

    public ResultadoVenta(boolean exito, Integer idVenta, String mensaje) {
      this.exito = exito;
      this.idVenta = idVenta;
      this.mensaje = mensaje;
    }

    // Getters
    public boolean isExito() {
      return exito;
    }

    public Integer getIdVenta() {
      return idVenta;
    }

    public String getMensaje() {
      return mensaje;
    }
  }

  @Override
  public List<Venta> leerTodo() throws SQLException {
    List<Venta> ventas = new ArrayList<>();
    Connection conexion = ConexionBD.getInstancia().abrirConexion();

    if (conexion != null) {
      String consulta = "SELECT idVenta, fechaVenta, Cliente_idCliente, folioFactura FROM venta";
      PreparedStatement statement = conexion.prepareStatement(consulta);
      ResultSet resultado = statement.executeQuery();

      while (resultado.next()) {
        ventas.add(convertirVenta(resultado));
      }

      resultado.close();
      statement.close();
      conexion.close();
    } else {
      throw new SQLException("No se pudo conectar a la base de datos");
    }

    return ventas;
  }

  @Override
  public Venta leerPorId(Integer id) throws SQLException {
    Venta venta = null;
    Connection conexion = ConexionBD.getInstancia().abrirConexion();

    if (conexion != null) {
      String consulta = "SELECT idVenta, fechaVenta, Cliente_idCliente, folioFactura FROM venta WHERE idVenta = ?";
      PreparedStatement statement = conexion.prepareStatement(consulta);
      statement.setInt(1, id);
      ResultSet resultado = statement.executeQuery();

      if (resultado.next()) {
        venta = convertirVenta(resultado);
      }

      resultado.close();
      statement.close();
      conexion.close();
    } else {
      throw new SQLException("No se pudo conectar a la base de datos");
    }

    return venta;
  }

  // Deprecated: Este método no se usa en la aplicación actual
  @Override
  public Venta insertar(Venta venta) throws SQLException {
    Connection conexion = ConexionBD.getInstancia().abrirConexion();

    if (conexion != null) {
      try {
        // Iniciar transacción
        conexion.setAutoCommit(false);

        // 1. Insertar la venta
        int idVenta = insertarVenta(conexion, venta);
        venta.setIdVenta(idVenta);

        // 2. Insertar los detalles de la venta
        if (venta != null) {
          insertarDetallesVenta(conexion, (List<DetalleVenta>) venta, idVenta);
        }

        // Confirmar transacción
        conexion.commit();

      } catch (SQLException ex) {
        // Revertir en caso de error
        conexion.rollback();
        throw ex;
      } finally {
        conexion.setAutoCommit(true);
        conexion.close();
      }
    } else {
      throw new SQLException("No se pudo conectar a la base de datos");
    }

    return venta;
  }

  public static ResultadoVenta registrarVentaCompleta(int idCliente, Timestamp fechaVenta,
      String folioFactura, List<DetalleVentaJSON> detalles) {
    Connection conexion = null;
    CallableStatement stmt = null;

    try {
      conexion = ConexionBD.getInstancia().abrirConexion();

      // Convertir la lista de detalles a JSON
      Gson gson = new Gson();
      String jsonDetalles = gson.toJson(detalles);

      // Preparar la llamada al stored procedure
      String sql = "{CALL sp_registrar_venta_completa(?, ?, ?, ?, ?, ?)}";
      stmt = conexion.prepareCall(sql);

      // Parámetros de entrada
      stmt.setInt(1, idCliente);
      stmt.setTimestamp(2, fechaVenta);
      stmt.setString(3, folioFactura);
      stmt.setString(4, jsonDetalles);

      // Parámetros de salida
      stmt.registerOutParameter(5, Types.INTEGER); // p_idVenta
      stmt.registerOutParameter(6, Types.VARCHAR); // p_mensaje

      // Ejecutar el procedimiento
      stmt.execute();

      // Obtener resultados
      Integer idVenta = stmt.getInt(5);
      String mensaje = stmt.getString(6);

      // Si idVenta es null (0), la operación falló
      boolean exito = (idVenta != null && idVenta > 0);

      return new ResultadoVenta(exito, exito ? idVenta : null, mensaje);

    } catch (SQLException e) {
      return new ResultadoVenta(false, null, "Error de conexión: " + e.getMessage());
    } finally {
      try {
        if (stmt != null)
          stmt.close();
        if (conexion != null)
          conexion.close();
      } catch (SQLException e) {
        System.err.println("Error al cerrar conexión: " + e.getMessage());
      }
    }
  }

  public static ResultadoVenta registrarVenta(Venta venta, List<DetalleVenta> detallesVenta) {
    return registrarVenta(venta, detallesVenta, null);
  }

  // Nueva sobrecarga que recibe la promoción aplicada. Si idPromocion es null o 0
  // se enviará como null al SP.
  public static ResultadoVenta registrarVenta(Venta venta, List<DetalleVenta> detallesVenta, Integer idPromocion) {
    // Convertir DetalleVenta a DetalleVentaJSON
    List<DetalleVentaJSON> detallesJSON = new ArrayList<>();

    for (DetalleVenta detalle : detallesVenta) {
      DetalleVentaJSON json = new DetalleVentaJSON(
          detalle.getIdProducto(),
          detalle.getCantidadUnitaria());

      // Asignar promoción si corresponde
      if (idPromocion != null && idPromocion > 0) {
        json.setIdPromocion(idPromocion);
      }

      detallesJSON.add(json);
    }

    return registrarVentaCompleta(
        venta.getIdCliente(),
        new Timestamp(venta.getFechaVenta().getTime()),
        venta.getFolioFactura(),
        detallesJSON);
  }

  @Override
  public boolean actualizar(Venta venta) throws SQLException {
    // Normalmente no se actualizan ventas una vez registradas
    return false;
  }

  @Override
  public boolean eliminar(Integer idVenta) throws SQLException {
    // Normalmente no se eliminan ventas una vez registradas
    return false;
  }

  public List<Cliente> obtenerClientes() throws SQLException {
    List<Cliente> clientes = new ArrayList<>();
    Connection conexion = ConexionBD.getInstancia().abrirConexion();

    if (conexion != null) {
      String consulta = "SELECT idCliente, nombre, telefono, correo, razonSocial FROM cliente";
      PreparedStatement statement = conexion.prepareStatement(consulta);
      ResultSet resultado = statement.executeQuery();

      while (resultado.next()) {
        Cliente cliente = new Cliente();
        cliente.setIdCliente(resultado.getInt("idCliente"));
        cliente.setNombre(resultado.getString("nombre"));
        cliente.setTelefono(resultado.getString("telefono"));
        cliente.setCorreo(resultado.getString("correo"));
        cliente.setRazonSocial(resultado.getString("razonSocial"));
        clientes.add(cliente);
      }

      resultado.close();
      statement.close();
      conexion.close();
    } else {
      throw new SQLException("No se pudo conectar a la base de datos");
    }

    return clientes;
  }

  private int insertarVenta(Connection conexion, Venta venta) throws SQLException {
    String consulta = "INSERT INTO venta (fechaVenta, Cliente_idCliente, folioFactura) VALUES (?, ?, ?)";

    PreparedStatement statement = conexion.prepareStatement(consulta, Statement.RETURN_GENERATED_KEYS);

    // Obtener cliente seleccionado
    Integer idCliente = venta.getIdCliente() != 0 && venta.getIdCliente() > 0 ? venta.getIdCliente() : null;

    statement.setDate(1, (Date) venta.getFechaVenta());

    if (idCliente != null) {
      statement.setInt(2, idCliente);
    } else {
      statement.setNull(2, java.sql.Types.INTEGER);
    }

    statement.setString(3, venta.getFolioFactura());

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

  private void insertarDetallesVenta(Connection conexion, List<DetalleVenta> detalles, int idVenta)
      throws SQLException {
    String consulta = "INSERT INTO detalle_venta (Producto_idProducto, Venta_idVenta, total_pagado, cantidadUnitaria) "
        +
        "VALUES (?, ?, ?, ?)";

    PreparedStatement statement = conexion.prepareStatement(consulta);

    for (DetalleVenta detalle : detalles) {
      statement.setInt(1, detalle.getIdProducto());
      statement.setInt(2, idVenta);
      statement.setDouble(3, detalle.getTotal_pagado());
      statement.setInt(4, detalle.getCantidadUnitaria());

      statement.executeUpdate();
    }

    statement.close();
  }

  private void actualizarStockProductos(Connection conexion, List<DetalleVenta> detalles) throws SQLException {
    String consulta = "UPDATE producto SET stock = stock - ? WHERE idProducto = ?";

    PreparedStatement statement = conexion.prepareStatement(consulta);

    for (DetalleVenta detalle : detalles) {
      statement.setInt(1, detalle.getCantidadUnitaria());
      statement.setInt(2, detalle.getIdProducto());

      statement.executeUpdate();
    }

    statement.close();
  }

  private Venta convertirVenta(ResultSet resultado) throws SQLException {
    Venta venta = new Venta();
    venta.setIdVenta(resultado.getInt("idVenta"));
    venta.setFechaVenta(resultado.getDate("fechaVenta"));

    Integer idCliente = resultado.getInt("Cliente_idCliente");
    if (!resultado.wasNull()) {
      venta.setIdCliente(idCliente);
    }

    venta.setFolioFactura(resultado.getString("folioFactura"));
    return venta;
  }

  public String generarFolioFactura(LocalDate fecha) {
    // Generar folio de factura (formato simple: VENTA-YYYYMMDD-XXXX)
    return "VENTA-" + fecha.toString().replace("-", "") + "-" +
        String.format("%04d", (int) (Math.random() * 10000));
  }
}