/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package expendiocrudproyecto.modelo.dao;

import expendiocrudproyecto.modelo.ConexionBD;
import expendiocrudproyecto.modelo.pojo.Bebida;
import expendiocrudproyecto.modelo.pojo.Promocion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author OmarAlex
 */
public class BebidaDAO implements CrudDAO<Bebida> {

  @Override
  public List<Bebida> leerTodo() throws SQLException {

    List<Bebida> bebidas = new ArrayList<>();
    Connection conexionBD = ConexionBD.getInstancia().abrirConexion();

    if (conexionBD != null) {
      String consultaSQL = "Select * from producto";

      PreparedStatement statement = conexionBD.prepareStatement(consultaSQL);
      ResultSet rs = statement.executeQuery();

      while (rs.next()) {
        bebidas.add(convertirBebida(rs));
      }

      statement.close();
      conexionBD.close();
    } else {
      throw new SQLException("Error al conectar con la base de datos");
    }

    return bebidas;
  }

  @Override
  public Bebida leerPorId(Integer id) throws SQLException {

    Bebida bebida = new Bebida();
    Connection conexionBD = ConexionBD.getInstancia().abrirConexion();

    if (conexionBD != null) {
      String consultaSQL = "Select * from producto where idProducto = ?";

      PreparedStatement statement = conexionBD.prepareStatement(consultaSQL);
      statement.setInt(1, id);

      ResultSet rs = statement.executeQuery();

      while (rs.next()) {
        bebida = convertirBebida(rs);
      }
      statement.close();
      conexionBD.close();
    } else {
      throw new SQLException("Error al conectar con la base de datos");
    }

    return bebida;
  }

  public List<Bebida> buscarPorNombre(String nombre) throws SQLException {
    List<Bebida> listaPromociones = new ArrayList<>();
    Connection conexion = ConexionBD.getInstancia().abrirConexion();

    if (conexion != null) {
      String consulta = "SELECT idProducto, nombre, precio, stock, stockMinimo FROM producto " +
          "WHERE nombre LIKE ?";
      PreparedStatement statement = conexion.prepareStatement(consulta);
      statement.setString(1, "%" + nombre + "%");

      ResultSet resultado = statement.executeQuery();

      while (resultado.next()) {
        listaPromociones.add(convertirBebida(resultado));
      }

      resultado.close();
      statement.close();
      conexion.close();
    }

    return listaPromociones;
  }

  @Override
  public Bebida insertar(Bebida bebida) throws SQLException {

    Connection conexionBD = ConexionBD.getInstancia().abrirConexion();

    if (conexionBD != null) {
      String consultaSQL = "insert into producto (nombre, precio, stock, stockMinimo) values (?, ?, ?, ?)";

      PreparedStatement statement = conexionBD.prepareStatement(consultaSQL, Statement.RETURN_GENERATED_KEYS);

      statement.setString(1, bebida.getNombre());
      statement.setFloat(2, bebida.getPrecio());
      statement.setInt(3, bebida.getStock());
      statement.setInt(4, bebida.getStockMinimo());

      int filasInsertadas = statement.executeUpdate();

      if (filasInsertadas > 0) {
        ResultSet generatedKeys = statement.getGeneratedKeys();
        if (generatedKeys.next()) {
          bebida.setId(generatedKeys.getInt(1));
        }
      }

      statement.close();
      conexionBD.close();
    } else {
      throw new SQLException("Error al conectar con la base de datos");
    }

    return bebida;
  }

  @Override
  public boolean actualizar(Bebida bebida) throws SQLException {

    Connection conexionBD = ConexionBD.getInstancia().abrirConexion();

    if (conexionBD != null) {
      String consultaSQL = "update producto set nombre = ?, precio = ?, stockMinimo = ? where idProducto = ?";

      PreparedStatement statement = conexionBD.prepareStatement(consultaSQL);
      statement.setString(1, bebida.getNombre());
      statement.setFloat(2, bebida.getPrecio());
      statement.setInt(3, bebida.getStockMinimo());
      statement.setInt(4, bebida.getId());

      int filasActualizadas = statement.executeUpdate();

      statement.close();
      conexionBD.close();

      return filasActualizadas > 0;
    } else {
      throw new SQLException("Error al conectar con la base de datos");
    }
  }

  @Override
  public boolean eliminar(Integer bebidaId) throws SQLException {
    Connection conexionBD = ConexionBD.getInstancia().abrirConexion();

    if (conexionBD != null) {
      String consultaSQL = "delete from producto where idProducto = ?";

      PreparedStatement statement = conexionBD.prepareStatement(consultaSQL);
      statement.setInt(1, bebidaId);

      int filasEliminadas = statement.executeUpdate();

      statement.close();
      conexionBD.close();

      return filasEliminadas > 0;
    } else {
      throw new SQLException("Error al conectar con la base de datos");
    }
  }

  public List<Bebida> leerBajoStock() throws SQLException {
    List<Bebida> bebidasBajoStock = new ArrayList<>();
    Connection conexionBD = ConexionBD.getInstancia().abrirConexion();

    if (conexionBD != null) {
      // En la base de datos debe existir la vista "vista_productos_bajo_stock"
      String consultaSQL = "SELECT idProducto, nombre, precio, stock, stockMinimo FROM vista_productos_bajo_stock";

      PreparedStatement statement = conexionBD.prepareStatement(consultaSQL);
      ResultSet rs = statement.executeQuery();

      while (rs.next()) {
        bebidasBajoStock.add(convertirBebida(rs));
      }

      statement.close();
      conexionBD.close();
    } else {
      throw new SQLException("Error al conectar con la base de datos");
    }

    return bebidasBajoStock;
  }

  public boolean actualizarStock(int idProducto, int nuevoStock) throws SQLException {
    Connection conexion = ConexionBD.getInstancia().abrirConexion();
    if (conexion == null) {
      throw new SQLException("Error al conectar con la base de datos");
    }
    String sql = "UPDATE producto SET stock = ? WHERE idProducto = ?";
    boolean exito;
    try (PreparedStatement ps = conexion.prepareStatement(sql)) {
      ps.setInt(1, nuevoStock);
      ps.setInt(2, idProducto);
      exito = ps.executeUpdate() > 0;
    } finally {
      conexion.close();
    }
    return exito;
  }

  private static Bebida convertirBebida(ResultSet rs) throws SQLException {
    Bebida bebidaActual = new Bebida();

    bebidaActual.setId(rs.getInt("idProducto"));
    bebidaActual.setNombre(rs.getString("nombre"));
    bebidaActual.setPrecio(rs.getFloat("precio"));
    bebidaActual.setStock(rs.getInt("stock"));
    bebidaActual.setStockMinimo(rs.getInt("stockMinimo"));

    return bebidaActual;
  }
}
