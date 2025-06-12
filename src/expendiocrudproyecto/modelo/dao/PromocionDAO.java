package expendiocrudproyecto.modelo.dao;

import expendiocrudproyecto.modelo.ConexionBD;
import expendiocrudproyecto.modelo.pojo.Promocion;
import expendiocrudproyecto.utilidades.Alertas;
import javafx.scene.control.Alert;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PromocionDAO implements CrudDAO<Promocion> {

    @Override
    public List<Promocion> leerTodo() throws SQLException {
        List<Promocion> promociones = new ArrayList<>();
        Connection conexion = ConexionBD.getInstancia().abrirConexion();

        if (conexion != null) {
            String consulta = "SELECT idPromocion, nombre, descripcion, descuento, fechaInicio, fechaFin, " +
                    "terminosCondiciones, acumulable, Producto_idProducto as idProducto FROM promocion";
            PreparedStatement statement = conexion.prepareStatement(consulta);
            ResultSet resultado = statement.executeQuery();

            while (resultado.next()) {
                promociones.add(convertirPromocion(resultado));
            }

            resultado.close();
            statement.close();
            conexion.close();
        } else {
            throw new SQLException("No se pudo conectar a la base de datos");
        }

        return promociones;
    }

    public List<Promocion> buscarPorNombre(String nombre) throws SQLException {
        List<Promocion> listaPromociones = new ArrayList<>();
        Connection conexion = ConexionBD.getInstancia().abrirConexion();

        if (conexion != null) {
            String consulta = "SELECT idPromocion, nombre, descripcion, descuento, fechaInicio, fechaFin, terminosCondiciones, Producto_idProducto, acumulable FROM promocion " +
                    "WHERE nombre LIKE ?";
            PreparedStatement statement = conexion.prepareStatement(consulta);
            statement.setString(1, "%" + nombre + "%");

            ResultSet resultado = statement.executeQuery();

            while (resultado.next()) {
                listaPromociones.add(convertirPromocion(resultado));
            }

            resultado.close();
            statement.close();
            conexion.close();
        }

        return listaPromociones;
    }

    public List<Promocion> leerPromocionesVigentes() throws SQLException {
        List<Promocion> promociones = new ArrayList<>();
        Connection conexion = ConexionBD.getInstancia().abrirConexion();

        if (conexion != null) {
            String consulta = "SELECT idPromocion, nombre, descripcion, descuento, fechaInicio, fechaFin, " +
                    "terminosCondiciones, acumulable, Producto_idProducto as idProducto " +
                    "FROM promocion " +
                    "WHERE fechaInicio <= CURRENT_DATE AND fechaFin >= CURRENT_DATE";
            PreparedStatement statement = conexion.prepareStatement(consulta);
            ResultSet resultado = statement.executeQuery();

            while (resultado.next()) {
                promociones.add(convertirPromocion(resultado));
            }

            resultado.close();
            statement.close();
            conexion.close();
        } else {
            throw new SQLException("No se pudo conectar a la base de datos");
        }

        return promociones;
    }

    @Override
    public Promocion leerPorId(Integer id) throws SQLException {
        Promocion promocion = null;
        Connection conexion = ConexionBD.getInstancia().abrirConexion();

        if (conexion != null) {
            String consulta = "SELECT idPromocion, nombre, descripcion, descuento, fechaInicio, fechaFin, " +
                    "terminosCondiciones, acumulable, Producto_idProducto as idProducto " +
                    "FROM promocion WHERE idPromocion = ?";
            PreparedStatement statement = conexion.prepareStatement(consulta);
            statement.setInt(1, id);
            ResultSet resultado = statement.executeQuery();

            if (resultado.next()) {
                promocion = convertirPromocion(resultado);
            }

            resultado.close();
            statement.close();
            conexion.close();
        } else {
            throw new SQLException("No se pudo conectar a la base de datos");
        }

        return promocion;
    }

    @Override
    public Promocion insertar(Promocion promocion) throws SQLException {
        Connection conexion = ConexionBD.getInstancia().abrirConexion();

        if (conexion != null) {
            String consulta = "INSERT INTO promocion (descripcion, descuento, fechaInicio, fechaFin, " +
                    "terminosCondiciones, Producto_idProducto, acumulable) VALUES (?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement statement = conexion.prepareStatement(consulta, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, promocion.getDescripcion());
            statement.setInt(2, promocion.getDescuento());
            statement.setDate(3, new java.sql.Date(promocion.getFechaInicio().getTime()));
            statement.setDate(4, new java.sql.Date(promocion.getFechaFin().getTime()));
            statement.setString(5, promocion.getTerminosCondiciones());
            statement.setInt(6, promocion.getIdProducto());
            statement.setBoolean(7, promocion.isAcumulable());

            int filasInsertadas = statement.executeUpdate();

            if (filasInsertadas > 0) {
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    promocion.setIdPromocion(generatedKeys.getInt(1));
                }
                generatedKeys.close();
            }

            statement.close();
            conexion.close();
        } else {
            throw new SQLException("No se pudo conectar a la base de datos");
        }

        return promocion;
    }

    @Override
    public boolean actualizar(Promocion promocion) throws SQLException {
        Connection conexion = ConexionBD.getInstancia().abrirConexion();
        boolean operacionExitosa = false;

        if (conexion != null) {
            String consulta = "UPDATE promocion SET descripcion = ?, descuento = ?, fechaInicio = ?, " +
                    "fechaFin = ?, terminosCondiciones = ?, Producto_idProducto = ?, acumulable = ? " +
                    "WHERE idPromocion = ?";

            PreparedStatement statement = conexion.prepareStatement(consulta);
            statement.setString(1, promocion.getDescripcion());
            statement.setFloat(2, promocion.getDescuento());
            statement.setDate(3, new java.sql.Date(promocion.getFechaInicio().getTime()));
            statement.setDate(4, new java.sql.Date(promocion.getFechaFin().getTime()));
            statement.setString(5, promocion.getTerminosCondiciones());
            statement.setInt(6, promocion.getIdProducto());
            statement.setBoolean(7, promocion.isAcumulable());
            statement.setInt(8, promocion.getIdPromocion());

            int filasActualizadas = statement.executeUpdate();
            operacionExitosa = filasActualizadas > 0;

            statement.close();
            conexion.close();
        } else {
            throw new SQLException("No se pudo conectar a la base de datos");
        }

        return operacionExitosa;
    }

    @Override
    public boolean eliminar(Integer idPromocion) throws SQLException {
        Connection conexion = ConexionBD.getInstancia().abrirConexion();
        boolean operacionExitosa = false;

        if (conexion != null) {
            String consulta = "DELETE FROM promocion WHERE idPromocion = ?";
            PreparedStatement statement = conexion.prepareStatement(consulta);
            statement.setInt(1, idPromocion);

            int filasEliminadas = statement.executeUpdate();
            operacionExitosa = filasEliminadas > 0;

            statement.close();
            conexion.close();
        } else {
            throw new SQLException("No se pudo conectar a la base de datos");
        }

        return operacionExitosa;
    }

    private Promocion convertirPromocion(ResultSet resultado) throws SQLException {
        Promocion promocion = new Promocion();
        promocion.setIdPromocion(resultado.getInt("idPromocion"));
        promocion.setNombre(resultado.getString("nombre"));
        promocion.setDescripcion(resultado.getString("descripcion"));
        promocion.setDescuento(resultado.getInt("descuento"));
        promocion.setFechaInicio(resultado.getDate("fechaInicio"));
        promocion.setFechaFin(resultado.getDate("fechaFin"));
        promocion.setTerminosCondiciones(resultado.getString("terminosCondiciones"));
        promocion.setIdProducto(resultado.getInt("idProducto"));
        promocion.setAcumulable(resultado.getBoolean("acumulable"));
        return promocion;
    }

    public ArrayList<Promocion> buscarPromociones(Date fecha, int idCliente) {
        ArrayList<Promocion> promociones = new ArrayList<>();
        ConexionBD conexionBD = ConexionBD.getInstancia();
        // La consulta busca promociones donde la fecha actual esté dentro del rango de vigencia
        // y que sean generales (Cliente_idCliente IS NULL) o específicas para el cliente dado.
        String consulta = "SELECT * FROM promocion WHERE ? BETWEEN fechaInicio AND fechaFin AND (Cliente_idCliente = ? OR Cliente_idCliente IS NULL)";

        try (Connection conexion = conexionBD.abrirConexion();
             PreparedStatement ps = conexion.prepareStatement(consulta)) {

            ps.setDate(1, fecha);
            ps.setInt(2, idCliente);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Promocion promocion = new Promocion();
                    promocion.setIdPromocion(rs.getInt("idPromocion"));
                    promocion.setNombre(rs.getString("nombre"));
                    promocion.setDescripcion(rs.getString("descripcion"));
                    promocion.setFechaInicio(rs.getDate("fechaInicio"));
                    promocion.setFechaFin(rs.getDate("fechaFin"));
                    promocion.setDescuento(rs.getInt("porcentajeDescuento"));

                    int clienteId = rs.getInt("Cliente_idCliente");
                    if (!rs.wasNull()) {
                        promocion.setIdCliente(clienteId);
                    }
                    promociones.add(promocion);
                }
            }
        } catch (SQLException ex) {
            Alertas.crearAlerta(Alert.AlertType.ERROR, "Error al buscar promociones",
                    "No se pudieron recuperar las promociones: " + ex.getMessage());
        }
        return promociones;
    }
}