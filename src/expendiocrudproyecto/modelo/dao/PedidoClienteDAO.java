package expendiocrudproyecto.modelo.dao;

import expendiocrudproyecto.modelo.pojo.PedidoCliente;
import expendiocrudproyecto.modelo.pojo.DetalleVenta;
import expendiocrudproyecto.modelo.ConexionBD;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.CallableStatement;
import java.sql.Timestamp;
import java.sql.Types;

public class PedidoClienteDAO implements CrudDAO<PedidoCliente> {

  @Override
  public List<PedidoCliente> leerTodo() throws SQLException {
    List<PedidoCliente> pedidos = new ArrayList<>();
    Connection conexion = ConexionBD.getInstancia().abrirConexion();

    if (conexion == null) {
      throw new SQLException("No se pudo conectar a la base de datos");
    }

    String consulta = "SELECT  pc.idPedidoCliente, " +
        "v.folioFactura          AS folio_factura, " +
        "pc.fechaCreacion        AS fecha, " +
        "c.idCliente, " +
        "c.nombre                AS nombreCliente, " +
        "COALESCE( SUM(dpc.precioComprado * dpc.cantidadUnitaria), 0 ) AS total, " +
        "e.estatus " +
        "FROM            pedidocliente pc " +
        "JOIN            cliente  c  ON pc.Cliente_idCliente = c.idCliente " +
        "JOIN            estatus  e  ON pc.idEstatus        = e.idEstatus " +
        "LEFT JOIN       venta    v  ON pc.idVenta          = v.idVenta " +
        "LEFT JOIN       detalle_pedidocliente dpc " +
        "               ON pc.idPedidoCliente = dpc.idPedidoCliente " +
        "GROUP BY pc.idPedidoCliente, v.folioFactura, pc.fechaCreacion, " +
        "         c.idCliente, c.nombre, e.estatus " +
        "ORDER BY pc.fechaCreacion DESC";

    try (PreparedStatement ps = conexion.prepareStatement(consulta); ResultSet rs = ps.executeQuery()) {
      while (rs.next()) {
        pedidos.add(convertirPedido(rs));
      }
    } finally {
      conexion.close();
    }

    return pedidos;
  }

  @Override
  public PedidoCliente leerPorId(Integer id) throws SQLException {
    PedidoCliente pedido = null;
    Connection conexion = ConexionBD.getInstancia().abrirConexion();
    if (conexion == null) {
      throw new SQLException("No se pudo conectar a la base de datos");
    }

    String consulta = "SELECT pc.idPedidoCliente, pc.folio_factura, pc.fecha, " +
        "c.idCliente, c.nombre AS nombreCliente, " +
        "COALESCE(SUM(dpc.total_pagado),0) AS total, e.estatus " +
        "FROM pedidocliente pc " +
        "JOIN cliente c          ON pc.Cliente_idCliente = c.idCliente " +
        "JOIN estatus e          ON pc.idEstatus        = e.idEstatus " +
        "LEFT JOIN detalle_pedidocliente dpc ON pc.idPedidoCliente = dpc.idPedidoCliente " +
        "WHERE pc.idPedidoCliente = ? " +
        "GROUP BY pc.idPedidoCliente, pc.folio_factura, pc.fecha, c.idCliente, c.nombre, e.estatus";

    try (PreparedStatement ps = conexion.prepareStatement(consulta)) {
      ps.setInt(1, id);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          pedido = convertirPedido(rs);
        }
      }
    } finally {
      conexion.close();
    }
    return pedido;
  }

  @Override
  public PedidoCliente insertar(PedidoCliente obj) throws SQLException {
    Connection conexion = ConexionBD.getInstancia().abrirConexion();
    if (conexion == null) {
      throw new SQLException("No se pudo conectar a la base de datos");
    }

    String sql = "INSERT INTO pedidocliente (fecha, total_productos, Cliente_idCliente, idEstatus, folio_factura) " +
        "VALUES (?, ?, ?, ?, ?)";

    try (PreparedStatement ps = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
      ps.setTimestamp(1, new Timestamp(obj.getFecha().getTime()));
      ps.setInt(2, 0); // total_productos se actualizará luego
      ps.setInt(3, obj.getIdCliente());
      ps.setInt(4, 1); // Estatus inicial = 1 (Pendiente)
      ps.setString(5, obj.getFolio());

      ps.executeUpdate();

      try (ResultSet keys = ps.getGeneratedKeys()) {
        if (keys.next()) {
          obj.setIdPedido(keys.getInt(1));
        }
      }
    } finally {
      conexion.close();
    }
    return obj;
  }

  @Override
  public boolean actualizar(PedidoCliente obj) throws SQLException {
    Connection conexion = ConexionBD.getInstancia().abrirConexion();
    if (conexion == null) {
      throw new SQLException("No se pudo conectar a la base de datos");
    }

    String sql = "UPDATE pedidocliente SET folio_factura = ?, Cliente_idCliente = ? WHERE idPedidoCliente = ?";

    boolean exito;
    try (PreparedStatement ps = conexion.prepareStatement(sql)) {
      ps.setString(1, obj.getFolio());
      ps.setInt(2, obj.getIdCliente());
      ps.setInt(3, obj.getIdPedido());
      exito = ps.executeUpdate() > 0;
    } finally {
      conexion.close();
    }

    return exito;
  }

  @Override
  public boolean eliminar(Integer id) throws SQLException {
    Connection conexion = ConexionBD.getInstancia().abrirConexion();
    if (conexion == null) {
      throw new SQLException("No se pudo conectar a la base de datos");
    }

    try {
      conexion.setAutoCommit(false); // Iniciar transacción

      // Paso 1: Cambiar estado a "Cancelado" para que el trigger devuelva stock.
      try (PreparedStatement psCancelar = conexion.prepareStatement(
          "UPDATE pedidocliente SET idEstatus = 2 WHERE idPedidoCliente = ? AND idEstatus != 2")) {
        psCancelar.setInt(1, id);
        psCancelar.executeUpdate();
      }

      // Paso 2: Eliminar detalles
      try (PreparedStatement psDetalles = conexion.prepareStatement(
          "DELETE FROM detalle_pedidocliente WHERE idPedidoCliente = ?")) {
        psDetalles.setInt(1, id);
        psDetalles.executeUpdate();
      }

      // Paso 3: Eliminar pedido principal
      boolean exito;
      try (PreparedStatement psPedido = conexion.prepareStatement(
          "DELETE FROM pedidocliente WHERE idPedidoCliente = ?")) {
        psPedido.setInt(1, id);
        exito = psPedido.executeUpdate() > 0;
      }

      conexion.commit(); // Confirmar la transacción
      return exito;

    } catch (SQLException ex) {
      conexion.rollback(); // Revertir todo si algo falla
      throw ex;
    } finally {
      conexion.setAutoCommit(true);
      conexion.close();
    }
  }

  public List<PedidoCliente> buscar(String criterio) throws SQLException {
    List<PedidoCliente> pedidos = new ArrayList<>();
    Connection conexion = ConexionBD.getInstancia().abrirConexion();
    if (conexion == null) {
      throw new SQLException("No se pudo conectar a la base de datos");
    }

    String sql = "SELECT pc.idPedidoCliente, pc.folio_factura, pc.fecha, " +
        "c.idCliente, c.nombre AS nombreCliente, " +
        "COALESCE(SUM(dpc.total_pagado),0) AS total, e.estatus " +
        "FROM pedidocliente pc " +
        "JOIN cliente c          ON pc.Cliente_idCliente = c.idCliente " +
        "JOIN estatus e          ON pc.idEstatus        = e.idEstatus " +
        "LEFT JOIN detalle_pedidocliente dpc ON pc.idPedidoCliente = dpc.idPedidoCliente " +
        "WHERE pc.folio_factura LIKE ? OR c.nombre LIKE ? " +
        "GROUP BY pc.idPedidoCliente, pc.folio_factura, pc.fecha, c.idCliente, c.nombre, e.estatus " +
        "ORDER BY pc.fecha DESC";

    try (PreparedStatement ps = conexion.prepareStatement(sql)) {
      String like = "%" + criterio + "%";
      ps.setString(1, like);
      ps.setString(2, like);
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          pedidos.add(convertirPedido(rs));
        }
      }
    } finally {
      conexion.close();
    }

    return pedidos;
  }

  // ========================
  // Métodos específicos
  // ========================

  public boolean registrarPedido(java.time.LocalDate fecha, int idCliente, List<DetalleVenta> detalles)
      throws SQLException {
    // Este método ya es correcto, llama al SP que reserva el stock. No se toca.
    Connection conexion = ConexionBD.getInstancia().abrirConexion();
    if (conexion == null) {
      throw new SQLException("No se pudo conectar a la base de datos");
    }
    String jsonDetalles = construirJsonDetalles(detalles);
    String call = "{ call sp_registrar_pedidocliente_completo(?, ?, ?, ?, ?) }";
    try (CallableStatement cs = conexion.prepareCall(call)) {
      cs.setTimestamp(1, Timestamp.valueOf(fecha.atStartOfDay()));
      cs.setInt(2, idCliente);
      cs.setString(3, jsonDetalles);
      cs.registerOutParameter(4, Types.INTEGER); // p_idPedidoCliente
      cs.registerOutParameter(5, Types.VARCHAR); // p_mensaje
      cs.execute();
      int idGenerado = cs.getInt(4);
      String msg = cs.getString(5);

      // Es buena idea manejar el mensaje de error que viene del SP
      if (idGenerado <= 0 && msg != null) {
        throw new SQLException(msg);
      }
      System.out.println("SP pedidoCliente: " + msg);
      return idGenerado > 0;
    } finally {
      conexion.close();
    }
  }

  public boolean confirmarPedido(int idPedido, int idVenta) throws SQLException {
    Connection conexion = ConexionBD.getInstancia().abrirConexion();
    if (conexion == null) {
      throw new SQLException("No se pudo conectar a la base de datos");
    }
    String sql = "UPDATE pedidocliente SET idVenta = ? WHERE idPedidoCliente = ?";
    boolean exito;
    try (PreparedStatement ps = conexion.prepareStatement(sql)) {
      ps.setInt(1, idVenta);
      ps.setInt(2, idPedido);
      exito = ps.executeUpdate() > 0;
    } finally {
      conexion.close();
    }
    return exito;
  }

  public boolean cambiarEstatus(int idPedido, int idEstatus) throws SQLException {
    // Este método es correcto para cancelar pedidos, ya que dispara el trigger. No
    // se toca.
    Connection conexion = ConexionBD.getInstancia().abrirConexion();
    if (conexion == null) {
      throw new SQLException("No se pudo conectar a la base de datos");
    }
    String sql = "UPDATE pedidocliente SET idEstatus = ? WHERE idPedidoCliente = ?";
    boolean exito;
    try (PreparedStatement ps = conexion.prepareStatement(sql)) {
      ps.setInt(1, idEstatus);
      ps.setInt(2, idPedido);
      exito = ps.executeUpdate() > 0;
    } finally {
      conexion.close();
    }
    return exito;
  }
  // ============================
  // Métodos utilitarios privados
  // ============================

  private String construirJsonDetalles(List<DetalleVenta> detalles) {
    StringBuilder sb = new StringBuilder("[");
    for (int i = 0; i < detalles.size(); i++) {
      DetalleVenta d = detalles.get(i);
      sb.append("{")
          .append("\"idProducto\":").append(d.getIdProducto()).append(",")
          .append("\"cantidad_unitaria\":").append(d.getCantidadUnitaria()).append(",")
          .append("\"precioVenta\":").append(d.getPrecioUnitario())
          .append("}");
      if (i < detalles.size() - 1) {
        sb.append(",");
      }
    }
    sb.append("]");
    return sb.toString();
  }

  private PedidoCliente convertirPedido(ResultSet rs) throws SQLException {
    PedidoCliente pedido = new PedidoCliente();
    pedido.setIdPedido(rs.getInt("idPedidoCliente"));
    pedido.setFolio(rs.getString("folio_factura"));
    pedido.setFecha(rs.getTimestamp("fecha"));
    pedido.setIdCliente(rs.getInt("idCliente"));
    pedido.setNombreCliente(rs.getString("nombreCliente"));
    pedido.setTotal(rs.getDouble("total"));
    pedido.setEstado(rs.getString("estatus"));
    return pedido;
  }
}