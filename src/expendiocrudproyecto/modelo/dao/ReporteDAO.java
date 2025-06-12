package expendiocrudproyecto.modelo.dao;

import expendiocrudproyecto.modelo.ConexionBD;
import expendiocrudproyecto.modelo.pojo.*;
import expendiocrudproyecto.utilidades.Alertas;
import javafx.scene.control.Alert;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ReporteDAO {

  // ==================== VENTAS POR PERÍODO ====================

  /**
   * Obtiene reporte de ventas semanales
   */
  public List<ReporteVentasPeriodo> obtenerVentasSemanales(String fechaInicio, String fechaFin) {
    List<ReporteVentasPeriodo> reportes = new ArrayList<>();
    String sql = "SELECT " +
        "YEAR(v.fechaVenta) as anio, " +
        "WEEK(v.fechaVenta) as semana, " +
        "COUNT(v.idVenta) as totalVentas, " +
        "SUM(dv.total_pagado) as totalIngresos, " +
        "SUM(dv.cantidadUnitaria) as totalProductosVendidos, " +
        "AVG(dv.total_pagado) as promedioVentaDiaria " +
        "FROM venta v " +
        "INNER JOIN detalle_venta dv ON v.idVenta = dv.Venta_idVenta " +
        "WHERE v.fechaVenta BETWEEN ? AND ? " +
        "GROUP BY YEAR(v.fechaVenta), WEEK(v.fechaVenta) " +
        "ORDER BY anio DESC, semana DESC";

    try (Connection conexion = ConexionBD.getInstancia().abrirConexion();
        PreparedStatement stmt = conexion.prepareStatement(sql)) {

      stmt.setString(1, fechaInicio);
      stmt.setString(2, fechaFin);

      try (ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
          ReporteVentasPeriodo reporte = new ReporteVentasPeriodo();
          reporte.setPeriodo("Semana " + rs.getInt("semana") + " - " + rs.getInt("anio"));
          reporte.setTotalVentas(BigDecimal.valueOf(rs.getDouble("totalIngresos")));
          reporte.setTotalPedidos(rs.getInt("totalProductosVendidos"));

          reportes.add(reporte);
        }
      }

    } catch (SQLException e) {
      System.err.println("Error al obtener ventas semanales: " + e.getMessage());
      e.printStackTrace();
    }

    return reportes;
  }

  /**
   * Obtiene reporte de ventas mensuales
   */
  public List<ReporteVentasPeriodo> obtenerVentasMensuales() {
    List<ReporteVentasPeriodo> reportes = new ArrayList<>();
    // Consulta SQL corregida y más estándar
    String sql = "SELECT " +
        "    YEAR(v.fechaVenta) AS anio, " +
        "    MONTH(v.fechaVenta) AS mes, " +
        "    COUNT(DISTINCT v.idVenta) AS totalTransacciones, " + // Corregido: Contar ventas únicas
        "    SUM(dv.total_pagado) AS totalIngresos, " +
        "    SUM(dv.cantidadUnitaria) AS totalProductosVendidos " +
        "FROM venta v " +
        "INNER JOIN detalle_venta dv ON v.idVenta = dv.Venta_idVenta " +
        "GROUP BY YEAR(v.fechaVenta), MONTH(v.fechaVenta) " + // Agrupamos por año y número de mes
        "ORDER BY anio DESC, mes DESC";

    try (Connection conexion = ConexionBD.getInstancia().abrirConexion();
        PreparedStatement stmt = conexion.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery()) {

      // Locale para obtener el nombre del mes en Español
      Locale localeSpanish = new Locale("es", "ES");

      while (rs.next()) {
        ReporteVentasPeriodo reporte = new ReporteVentasPeriodo();

        int anio = rs.getInt("anio");
        int numeroMes = rs.getInt("mes");

        // --- Formateo del período en Java (más seguro) ---
        Month mesEnum = Month.of(numeroMes);
        // Obtener el nombre del mes en español (ej. "Enero", "Febrero")
        String nombreMes = mesEnum.getDisplayName(TextStyle.FULL, localeSpanish);
        // Capitalizar la primera letra
        nombreMes = nombreMes.substring(0, 1).toUpperCase() + nombreMes.substring(1);

        reporte.setPeriodo(nombreMes + " " + anio);
        // --- Fin del formateo ---

        reporte.setTotalVentas(BigDecimal.valueOf(rs.getDouble("totalIngresos")));
        reporte.setTotalPedidos(rs.getInt("totalProductosVendidos"));
        // Si tu clase ReporteVentasPeriodo tiene un campo para el número de
        // transacciones,
        // lo asignarías aquí con: rs.getInt("totalTransacciones")

        reportes.add(reporte);
      }

    } catch (SQLException e) {
      System.err.println("Error al obtener ventas mensuales: " + e.getMessage());
      e.printStackTrace();
    }

    return reportes;
  }

  /**
   * Obtiene reporte de ventas anuales
   */
  public List<ReporteVentasPeriodo> obtenerVentasAnuales(String fechaInicio, String fechaFin) {
    List<ReporteVentasPeriodo> reportes = new ArrayList<>();
    String sql = "SELECT " +
        "YEAR(v.fechaVenta) as anio, " +
        "COUNT(v.idVenta) as totalVentas, " +
        "SUM(dv.total_pagado) as totalIngresos, " +
        "SUM(dv.cantidadUnitaria) as totalProductosVendidos, " +
        "AVG(dv.total_pagado) as promedioVentaDiaria " +
        "FROM venta v " +
        "INNER JOIN detalle_venta dv ON v.idVenta = dv.Venta_idVenta " +
        "WHERE v.fechaVenta BETWEEN ? AND ? " +
        "GROUP BY YEAR(v.fechaVenta) " +
        "ORDER BY anio DESC";

    try (Connection conexion = ConexionBD.getInstancia().abrirConexion();
        PreparedStatement stmt = conexion.prepareStatement(sql)) {

      stmt.setString(1, fechaInicio);
      stmt.setString(2, fechaFin);

      try (ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
          ReporteVentasPeriodo reporte = new ReporteVentasPeriodo();
          reporte.setPeriodo("Año " + rs.getInt("anio"));
          reporte.setTotalVentas(BigDecimal.valueOf(rs.getDouble("totalIngresos")));
          reporte.setTotalPedidos(rs.getInt("totalProductosVendidos"));

          reportes.add(reporte);
        }
      }

    } catch (SQLException e) {
      System.err.println("Error al obtener ventas anuales: " + e.getMessage());
      e.printStackTrace();
    }

    return reportes;
  }

  // ==================== VENTAS POR PRODUCTO ====================

  /**
   * Obtiene reporte de ventas por producto
   */
  public List<ReporteVentasProducto> obtenerVentasPorProducto() {
    List<ReporteVentasProducto> reportes = new ArrayList<>();
    String sql = "SELECT " +
        "p.idProducto, " +
        "p.nombre as nombreProducto, " +
        "p.precio as precioUnitario, " +
        "SUM(dv.cantidadUnitaria) as cantidadVendida, " +
        "SUM(dv.total_pagado) as totalIngresos, " +
        "COUNT(DISTINCT v.idVenta) as numeroVentas, " +
        "AVG(dv.cantidadUnitaria) as promedioUnidadesPorVenta " +
        "FROM producto p " +
        "INNER JOIN detalle_venta dv ON p.idProducto = dv.Producto_idProducto " +
        "INNER JOIN venta v ON dv.Venta_idVenta = v.idVenta " +
        "GROUP BY p.idProducto, p.nombre, p.precio " +
        "ORDER BY totalIngresos DESC";

    try (Connection conexion = ConexionBD.getInstancia().abrirConexion();
        PreparedStatement stmt = conexion.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery()) {

      while (rs.next()) {
        ReporteVentasProducto reporte = new ReporteVentasProducto();
        reporte.setIdProducto(rs.getInt("idProducto"));
        reporte.setNombreProducto(rs.getString("nombreProducto"));
        reporte.setPrecioUnitario(BigDecimal.valueOf(rs.getDouble("precioUnitario")));
        reporte.setCantidadVendida(rs.getInt("cantidadVendida"));
        reporte.setTotalVentas(BigDecimal.valueOf(rs.getDouble("totalIngresos")));
        // reporte.setCantidadVendida(rs.getInt("numeroVentas"));

        reportes.add(reporte);
      }

    } catch (SQLException e) {
      System.err.println("Error al obtener ventas por producto: " + e.getMessage());
      e.printStackTrace();
    }

    return reportes;
  }

  // ==================== STOCK MÍNIMO ====================

  /**
   * Obtiene productos con stock mínimo o por debajo del mínimo
   */
  public List<ReporteStockMinimo> obtenerProductosStockMinimo() {
    List<ReporteStockMinimo> reportes = new ArrayList<>();
    String sql = "SELECT " +
        "p.idProducto, " +
        "p.nombre as nombreProducto, " +
        "p.stock as stockActual, " +
        "p.stockMinimo, " +
        "p.precio as precioUnitario, " +
        "CASE " +
        "    WHEN p.stock = 0 THEN 'SIN STOCK' " +
        "    WHEN p.stock < p.stockMinimo THEN 'CRÍTICO' " +
        "    WHEN p.stock = p.stockMinimo THEN 'MÍNIMO' " +
        "    ELSE 'NORMAL' " +
        "END as estadoStock, " +
        "(p.stockMinimo - p.stock) as cantidadFaltante " +
        "FROM producto p " +
        "WHERE p.stock <= p.stockMinimo " +
        "ORDER BY " +
        "CASE " +
        "    WHEN p.stock = 0 THEN 1 " +
        "    WHEN p.stock < p.stockMinimo THEN 2 " +
        "    ELSE 3 " +
        "END, " +
        "p.stock ASC";

    try (Connection conexion = ConexionBD.getInstancia().abrirConexion();
        PreparedStatement stmt = conexion.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery()) {

      while (rs.next()) {
        ReporteStockMinimo reporte = new ReporteStockMinimo();
        reporte.setIdProducto(rs.getInt("idProducto"));
        reporte.setNombreProducto(rs.getString("nombreProducto"));
        reporte.setStockActual(rs.getInt("stockActual"));
        reporte.setStockMinimo(rs.getInt("stockMinimo"));
        reporte.setPrecioUnitario(BigDecimal.valueOf(rs.getDouble("precioUnitario")));

        reportes.add(reporte);
      }

    } catch (SQLException e) {
      System.err.println("Error al obtener productos con stock mínimo: " + e.getMessage());
      e.printStackTrace();
    }

    return reportes;
  }

  // ==================== PRODUCTOS MÁS VENDIDOS ====================

  /**
   * Obtiene los productos más vendidos
   */
  public List<ReporteProductosMasVendidos> obtenerProductosMasVendidos(int limite) {
    List<ReporteProductosMasVendidos> reportes = new ArrayList<>();
    String sql = "SELECT " +
        "p.idProducto, " +
        "p.nombre as nombreProducto, " +
        "p.precio as precioUnitario, " +
        "SUM(dv.cantidadUnitaria) as totalVendido, " +
        "SUM(dv.total_pagado) as totalIngresos, " +
        "COUNT(DISTINCT v.idVenta) as numeroVentas, " +
        "AVG(dv.cantidadUnitaria) as promedioUnidadesPorVenta, " +
        "RANK() OVER (ORDER BY SUM(dv.cantidadUnitaria) DESC) as ranking " +
        "FROM producto p " +
        "INNER JOIN detalle_venta dv ON p.idProducto = dv.Producto_idProducto " +
        "INNER JOIN venta v ON dv.Venta_idVenta = v.idVenta " +
        "GROUP BY p.idProducto, p.nombre, p.precio " +
        "ORDER BY totalVendido DESC " +
        "LIMIT ?";

    try (Connection conexion = ConexionBD.getInstancia().abrirConexion();
        PreparedStatement stmt = conexion.prepareStatement(sql)) {

      stmt.setInt(1, limite);

      try (ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
          ReporteProductosMasVendidos reporte = new ReporteProductosMasVendidos();
          reporte.setIdProducto(rs.getInt("idProducto"));
          reporte.setNombreProducto(rs.getString("nombreProducto"));
          reporte.setPrecioUnitario(BigDecimal.valueOf(rs.getDouble("precioUnitario")));
          reporte.setTotalVentas(BigDecimal.valueOf(rs.getInt("numeroVentas")));

          reportes.add(reporte);
        }
      }

    } catch (SQLException e) {
      System.err.println("Error al obtener productos más vendidos: " + e.getMessage());
      e.printStackTrace();
    }

    return reportes;
  }

  // ==================== PRODUCTOS MENOS VENDIDOS ====================

  /**
   * Obtiene los productos menos vendidos
   */
  public List<ReporteProductosMenosVendidos> obtenerProductosMenosVendidos(int limite) {
    List<ReporteProductosMenosVendidos> reportes = new ArrayList<>();
    String sql = "SELECT " +
        "p.idProducto, " +
        "p.nombre as nombreProducto, " +
        "p.precio as precioUnitario, " +
        "p.stock as stockActual, " +
        "COALESCE(SUM(dv.cantidadUnitaria), 0) as totalVendido, " +
        "COALESCE(SUM(dv.total_pagado), 0) as totalIngresos, " +
        "COALESCE(COUNT(DISTINCT v.idVenta), 0) as numeroVentas, " +
        "CASE " +
        "    WHEN SUM(dv.cantidadUnitaria) IS NULL THEN 'NUNCA VENDIDO' " +
        "    WHEN SUM(dv.cantidadUnitaria) < 5 THEN 'POCO VENDIDO' " +
        "    ELSE 'VENDIDO' " +
        "END as estadoVenta " +
        "FROM producto p " +
        "LEFT JOIN detalle_venta dv ON p.idProducto = dv.Producto_idProducto " +
        "LEFT JOIN venta v ON dv.Venta_idVenta = v.idVenta " +
        "GROUP BY p.idProducto, p.nombre, p.precio, p.stock " +
        "ORDER BY totalVendido ASC, p.nombre ASC " +
        "LIMIT ?";

    try (Connection conexion = ConexionBD.getInstancia().abrirConexion();
        PreparedStatement stmt = conexion.prepareStatement(sql)) {

      stmt.setInt(1, limite);

      try (ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
          ReporteProductosMenosVendidos reporte = new ReporteProductosMenosVendidos();
          reporte.setIdProducto(rs.getInt("idProducto"));
          reporte.setNombreProducto(rs.getString("nombreProducto"));
          reporte.setPrecioUnitario(BigDecimal.valueOf(rs.getDouble("precioUnitario")));
          reporte.setStockActual(rs.getInt("stockActual"));
          reporte.setTotalVentas(BigDecimal.valueOf(rs.getInt("numeroVentas")));
          reporte.setObservacion(rs.getString("estadoVenta"));

          reportes.add(reporte);
        }
      }

    } catch (SQLException e) {
      System.err.println("Error al obtener productos menos vendidos: " + e.getMessage());
      e.printStackTrace();
    }

    return reportes;
  }

  // ==================== PRODUCTOS NO VENDIDOS A CLIENTE ====================

  /**
   * Obtiene productos que no han sido vendidos a un cliente específico
   */
  public List<ReporteProductosNoVendidosCliente> obtenerProductosNoVendidosACliente(int idCliente) {
    List<ReporteProductosNoVendidosCliente> reportes = new ArrayList<>();
    String sql = "SELECT " +
        "p.idProducto, " +
        "p.nombre as nombreProducto, " +
        "p.precio as precioUnitario, " +
        "p.stock as stockDisponible, " +
        "COALESCE(total_vendido.totalVendidoGeneral, 0) as totalVendidoGeneral, " +
        "CASE " +
        "    WHEN p.stock > 0 THEN 'DISPONIBLE' " +
        "    ELSE 'SIN STOCK' " +
        "END as disponibilidad, " +
        "'RECOMENDADO PARA CLIENTE' as recomendacion " +
        "FROM producto p " +
        "LEFT JOIN ( " +
        "    SELECT " +
        "        dv.Producto_idProducto, " +
        "        SUM(dv.cantidadUnitaria) as totalVendidoGeneral " +
        "    FROM detalle_venta dv " +
        "    GROUP BY dv.Producto_idProducto " +
        ") total_vendido ON p.idProducto = total_vendido.Producto_idProducto " +
        "WHERE p.idProducto NOT IN ( " +
        "    SELECT DISTINCT dv.Producto_idProducto " +
        "    FROM venta v " +
        "    INNER JOIN detalle_venta dv ON v.idVenta = dv.Venta_idVenta " +
        "    WHERE v.Cliente_idCliente = ? " +
        ") " +
        "ORDER BY totalVendidoGeneral DESC, p.nombre ASC";

    try (Connection conexion = ConexionBD.getInstancia().abrirConexion();
        PreparedStatement stmt = conexion.prepareStatement(sql)) {

      stmt.setInt(1, idCliente);

      try (ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
          ReporteProductosNoVendidosCliente reporte = new ReporteProductosNoVendidosCliente();
          reporte.setIdProducto(rs.getInt("idProducto"));
          reporte.setNombreProducto(rs.getString("nombreProducto"));
          reporte.setPrecioUnitario(BigDecimal.valueOf(rs.getDouble("precioUnitario")));
          reporte.setStockActual(rs.getInt("stockDisponible"));
          reporte.setRecomendacion(rs.getString("recomendacion"));

          reportes.add(reporte);
        }
      }

    } catch (SQLException e) {
      System.err.println("Error al obtener productos no vendidos a cliente: " + e.getMessage());
      e.printStackTrace();
    }

    return reportes;
  }

  // ==================== PRODUCTO MÁS VENDIDO A CLIENTE ====================

  /**
   * Obtiene el producto más vendido a un cliente específico
   */
  public List<ReporteProductoMasVendidoCliente> obtenerProductoMasVendidoACliente(int idCliente) {
    List<ReporteProductoMasVendidoCliente> reportes = new ArrayList<>();

    String sql = "SELECT " +
        "  idCliente, nombreCliente, idProducto, nombreProducto, " +
        "  precioUnitario, cantidadComprada " +
        "FROM vista_producto_mascomprado_cliente " +
        "WHERE idCliente = ? " +
        "ORDER BY cantidadComprada DESC, totalGastadoProducto DESC " +
        "LIMIT 5";

    try (Connection conexion = ConexionBD.getInstancia().abrirConexion();
        PreparedStatement stmt = conexion.prepareStatement(sql)) {

      stmt.setInt(1, idCliente);

      try (ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
          ReporteProductoMasVendidoCliente reporte = new ReporteProductoMasVendidoCliente();
          reporte.setIdCliente(rs.getInt("idCliente"));
          reporte.setNombreCliente(rs.getString("nombreCliente"));
          reporte.setIdProducto(rs.getInt("idProducto"));
          reporte.setNombreProducto(rs.getString("nombreProducto"));
          reporte.setPrecioUnitario(BigDecimal.valueOf(rs.getDouble("precioUnitario")));
          reporte.setCantidadComprada(rs.getInt("cantidadComprada"));

          reportes.add(reporte);
        }
      }

    } catch (SQLException e) {
      System.err.println("Error al obtener producto más vendido a cliente: " + e.getMessage());
      e.printStackTrace();
    }

    return reportes;
  }

  public Integer obtenerIdProductoMasCompradoPorCliente(int idCliente) {
    Integer idProducto = null;
    ConexionBD conexionBD = ConexionBD.getInstancia();
    // La consulta ordena por la cantidad total comprada para encontrar el producto
    // TOP 1.
    String consulta = "SELECT idProducto FROM vista_producto_mascomprado_cliente WHERE idCliente = ? ORDER BY cantidadComprada DESC LIMIT 1";

    try (Connection conexion = conexionBD.abrirConexion();
        PreparedStatement ps = conexion.prepareStatement(consulta)) {

      ps.setInt(1, idCliente);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          idProducto = rs.getInt("idProducto");
        }
      }
    } catch (SQLException ex) {
      Alertas.crearAlerta(Alert.AlertType.ERROR, "Error al consultar producto más comprado por cliente.", consulta);
    }
    // La conexión se cierra automáticamente por el try-with-resources

    return idProducto;
  }
}