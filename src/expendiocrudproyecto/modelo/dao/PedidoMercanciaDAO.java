package expendiocrudproyecto.modelo.dao;

import expendiocrudproyecto.modelo.pojo.PedidoMercancia;
import expendiocrudproyecto.modelo.pojo.DetalleVenta;
import expendiocrudproyecto.modelo.ConexionBD;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.CallableStatement;
import java.sql.Types;
import java.sql.Timestamp;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;

public class PedidoMercanciaDAO implements CrudDAO<PedidoMercancia> {

  @Override
  public List<PedidoMercancia> leerTodo() throws SQLException {
    List<PedidoMercancia> pedidos = new ArrayList<>();
    Connection conexion = ConexionBD.getInstancia().abrirConexion();

    if (conexion == null) {
      throw new SQLException("No se pudo conectar a la base de datos");
    }

    String consulta = "SELECT pm.idPedidoMercancia, pm.folio_factura, pm.fecha, " +
        "p.idProveedor, p.razonSocial AS nombreProveedor, " +
        "COALESCE(SUM(dpm.total_pagado),0) AS total, e.estatus " +
        "FROM pedidomercancia pm " +
        "JOIN proveedor p        ON pm.Proveedor_idProveedor = p.idProveedor " +
        "JOIN estatus   e        ON pm.idEstatus            = e.idEstatus " +
        "LEFT JOIN detalle_pedidomercancia dpm ON pm.idPedidoMercancia = dpm.idPedidoMercancia " +
        "GROUP BY pm.idPedidoMercancia, pm.folio_factura, pm.fecha, p.idProveedor, p.razonSocial, e.estatus " +
        "ORDER BY pm.fecha DESC";

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
  public PedidoMercancia leerPorId(Integer id) throws SQLException {
    PedidoMercancia pedido = null;
    Connection conexion = ConexionBD.getInstancia().abrirConexion();
    if (conexion == null) {
      throw new SQLException("No se pudo conectar a la base de datos");
    }

    String consulta = "SELECT pm.idPedidoMercancia, pm.folio_factura, pm.fecha, " +
        "p.idProveedor, p.razonSocial AS nombreProveedor, " +
        "COALESCE(SUM(dpm.total_pagado),0) AS total, e.estatus " +
        "FROM pedidomercancia pm " +
        "JOIN proveedor p        ON pm.Proveedor_idProveedor = p.idProveedor " +
        "JOIN estatus   e        ON pm.idEstatus            = e.idEstatus " +
        "LEFT JOIN detalle_pedidomercancia dpm ON pm.idPedidoMercancia = dpm.idPedidoMercancia " +
        "WHERE pm.idPedidoMercancia = ? " +
        "GROUP BY pm.idPedidoMercancia, pm.folio_factura, pm.fecha, p.idProveedor, p.razonSocial, e.estatus";

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
  public PedidoMercancia insertar(PedidoMercancia obj) throws SQLException {
    Connection conexion = ConexionBD.getInstancia().abrirConexion();
    if (conexion == null) {
      throw new SQLException("No se pudo conectar a la base de datos");
    }

    String sql = "INSERT INTO pedidomercancia (fecha, total_productos, Proveedor_idProveedor, idEstatus, folio_factura) "
        +
        "VALUES (?, ?, ?, ?, ?)";

    try (PreparedStatement ps = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
      ps.setTimestamp(1, new Timestamp(obj.getFecha().getTime()));
      ps.setInt(2, 0); // total_productos se actualizará luego
      ps.setInt(3, obj.getIdProveedor());
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
  public boolean actualizar(PedidoMercancia obj) throws SQLException {
    Connection conexion = ConexionBD.getInstancia().abrirConexion();
    if (conexion == null) {
      throw new SQLException("No se pudo conectar a la base de datos");
    }

    String sql = "UPDATE pedidomercancia SET folio_factura = ?, Proveedor_idProveedor = ? WHERE idPedidoMercancia = ?";

    boolean exito;
    try (PreparedStatement ps = conexion.prepareStatement(sql)) {
      ps.setString(1, obj.getFolio());
      ps.setInt(2, obj.getIdProveedor());
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

    boolean exito;
    try {
      conexion.setAutoCommit(false);

      // 1. Eliminar detalles
      try (PreparedStatement psDetalles = conexion
          .prepareStatement("DELETE FROM detalle_pedidomercancia WHERE idPedidoMercancia = ?")) {
        psDetalles.setInt(1, id);
        psDetalles.executeUpdate();
      }
      // 2. Eliminar pedido principal
      try (PreparedStatement psPedido = conexion
          .prepareStatement("DELETE FROM pedidomercancia WHERE idPedidoMercancia = ?")) {
        psPedido.setInt(1, id);
        exito = psPedido.executeUpdate() > 0;
      }

      conexion.commit();
    } catch (SQLException ex) {
      conexion.rollback();
      throw ex;
    } finally {
      conexion.setAutoCommit(true);
      conexion.close();
    }

    return exito;
  }

  public List<PedidoMercancia> buscar(String criterio) throws SQLException {
    List<PedidoMercancia> pedidos = new ArrayList<>();
    Connection conexion = ConexionBD.getInstancia().abrirConexion();
    if (conexion == null) {
      throw new SQLException("No se pudo conectar a la base de datos");
    }

    String sql = "SELECT pm.idPedidoMercancia, pm.folio_factura, pm.fecha, " +
        "p.idProveedor, p.razonSocial AS nombreProveedor, " +
        "COALESCE(SUM(dpm.total_pagado),0) AS total, e.estatus " +
        "FROM pedidomercancia pm " +
        "JOIN proveedor p        ON pm.Proveedor_idProveedor = p.idProveedor " +
        "JOIN estatus   e        ON pm.idEstatus            = e.idEstatus " +
        "LEFT JOIN detalle_pedidomercancia dpm ON pm.idPedidoMercancia = dpm.idPedidoMercancia " +
        "WHERE pm.folio_factura LIKE ? OR p.razonSocial LIKE ? " +
        "GROUP BY pm.idPedidoMercancia, pm.folio_factura, pm.fecha, p.idProveedor, p.razonSocial, e.estatus " +
        "ORDER BY pm.fecha DESC";

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

  public boolean registrarPedido(java.time.LocalDate fecha, int idProveedor, List<DetalleVenta> detalles)
      throws SQLException {
    Connection conexion = ConexionBD.getInstancia().abrirConexion();

    if (conexion == null) {
      throw new SQLException("No se pudo conectar a la base de datos");
    }

    // Construir el JSON de detalles que espera el procedimiento almacenado
    String jsonDetalles = construirJsonDetalles(detalles);

    String call = "{ call sp_registrar_pedidomercancia_completo(?, ?, ?, ?, ?) }";
    try (CallableStatement cs = conexion.prepareCall(call)) {
      cs.setTimestamp(1, Timestamp.valueOf(fecha.atStartOfDay()));
      cs.setInt(2, idProveedor);
      cs.setString(3, jsonDetalles);

      cs.registerOutParameter(4, Types.INTEGER); // p_idPedidoMercancia
      cs.registerOutParameter(5, Types.VARCHAR); // p_mensaje

      cs.execute();

      int idPedidoGenerado = cs.getInt(4);
      String mensajeBD = cs.getString(5);

      // Puedes registrar o mostrar el mensaje si lo deseas
      System.out.println("Procedimiento almacenado: " + mensajeBD);

      return idPedidoGenerado > 0;
    } finally {
      conexion.close();
    }
  }

  /**
   * Convierte la lista de detalles a un arreglo JSON que cumple con la
   * estructura que espera el procedimiento almacenado.
   */
  private String construirJsonDetalles(List<DetalleVenta> detalles) {
    StringBuilder sb = new StringBuilder("[");
    for (int i = 0; i < detalles.size(); i++) {
      DetalleVenta d = detalles.get(i);
      sb.append("{")
          .append("\"idProducto\":").append(d.getIdProducto()).append(",")
          .append("\"cantidad_unitaria\":").append(d.getCantidadUnitaria()).append(",")
          .append("\"precioComprado\":").append(d.getPrecioUnitario())
          .append("}");
      if (i < detalles.size() - 1) {
        sb.append(",");
      }
    }
    sb.append("]");
    return sb.toString();
  }

  public boolean cambiarEstatus(int idPedido, int idEstatus) throws SQLException {
    Connection conexion = ConexionBD.getInstancia().abrirConexion();
    if (conexion == null) {
      throw new SQLException("No se pudo conectar a la base de datos");
    }
    String sql = "UPDATE pedidomercancia SET idEstatus = ? WHERE idPedidoMercancia = ?";
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
  private PedidoMercancia convertirPedido(ResultSet rs) throws SQLException {
    PedidoMercancia pedido = new PedidoMercancia();
    pedido.setIdPedido(rs.getInt("idPedidoMercancia"));
    pedido.setFolio(rs.getString("folio_factura"));
    pedido.setFecha(rs.getTimestamp("fecha"));
    pedido.setIdProveedor(rs.getInt("idProveedor"));
    pedido.setNombreProveedor(rs.getString("nombreProveedor"));
    pedido.setTotal(rs.getDouble("total"));
    pedido.setEstado(rs.getString("estatus"));
    return pedido;
  }
}