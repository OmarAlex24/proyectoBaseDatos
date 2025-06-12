package expendiocrudproyecto.modelo.dao;

import expendiocrudproyecto.modelo.ConexionBD;
import expendiocrudproyecto.modelo.pojo.Promocion;
import expendiocrudproyecto.modelo.pojo.Proveedor;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ProveedorDAO implements CrudDAO<Proveedor> {

    @Override
    public List<Proveedor> leerTodo() throws SQLException {
        List<Proveedor> proveedores = new ArrayList<>();
        Connection conexion = ConexionBD.getInstancia().abrirConexion();

        if (conexion != null) {
            String consulta = "SELECT idProveedor, razonSocial, telefono, direccion, correo FROM proveedor";
            PreparedStatement statement = conexion.prepareStatement(consulta);
            ResultSet resultado = statement.executeQuery();

            while (resultado.next()) {
                proveedores.add(convertirProveedor(resultado));
            }

            resultado.close();
            statement.close();
            conexion.close();
        } else {
            throw new SQLException("No se pudo conectar a la base de datos");
        }

        return proveedores;
    }

    @Override
    public Proveedor leerPorId(Integer id) throws SQLException {
        Proveedor proveedor = null;
        Connection conexion = ConexionBD.getInstancia().abrirConexion();

        if (conexion != null) {
            String consulta = "SELECT idProveedor, razonSocial, telefono, direccion, correo FROM proveedor WHERE idProveedor = ?";
            PreparedStatement statement = conexion.prepareStatement(consulta);
            statement.setInt(1, id);
            ResultSet resultado = statement.executeQuery();

            if (resultado.next()) {
                proveedor = convertirProveedor(resultado);
            }

            resultado.close();
            statement.close();
            conexion.close();
        } else {
            throw new SQLException("No se pudo conectar a la base de datos");
        }

        return proveedor;
    }

    @Override
    public Proveedor insertar(Proveedor proveedor) throws SQLException {
        Connection conexion = ConexionBD.getInstancia().abrirConexion();

        if (conexion != null) {
            String consulta = "INSERT INTO proveedor (razonSocial, telefono, direccion, correo) VALUES (?, ?, ?, ?)";

            PreparedStatement statement = conexion.prepareStatement(consulta, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, proveedor.getRazonSocial());
            statement.setString(2, proveedor.getTelefono());
            statement.setString(3, proveedor.getDireccion());
            statement.setString(4, proveedor.getCorreo());

            int filasInsertadas = statement.executeUpdate();

            if (filasInsertadas > 0) {
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    proveedor.setIdProveedor(generatedKeys.getInt(1));
                }
                generatedKeys.close();
            }

            statement.close();
            conexion.close();
        } else {
            throw new SQLException("No se pudo conectar a la base de datos");
        }

        return proveedor;
    }

    public List<Proveedor> buscarPorNombre(String criterio) throws SQLException {
        List<Proveedor> listaProveedores = new ArrayList<>();
        Connection conexion = ConexionBD.getInstancia().abrirConexion();

        if (conexion != null) {
            String consulta = "SELECT idProveedor, razonSocial, telefono, direccion, correo FROM proveedor " +
                    "WHERE razonSocial LIKE ? OR telefono LIKE ?";
            PreparedStatement statement = conexion.prepareStatement(consulta);
            statement.setString(1, "%" + criterio + "%");
            statement.setString(2, "%" + criterio + "%");

            ResultSet resultado = statement.executeQuery();

            while (resultado.next()) {
                Proveedor proveedor = new Proveedor();
                proveedor.setIdProveedor(resultado.getInt("idProveedor"));
                proveedor.setRazonSocial(resultado.getString("razonSocial"));
                proveedor.setTelefono(resultado.getString("telefono"));
                proveedor.setDireccion(resultado.getString("direccion"));
                proveedor.setCorreo(resultado.getString("correo"));

                listaProveedores.add(proveedor);
            }

            resultado.close();
            statement.close();
            conexion.close();
        }

        return listaProveedores;
    }

    @Override
    public boolean actualizar(Proveedor proveedor) throws SQLException {
        Connection conexion = ConexionBD.getInstancia().abrirConexion();
        boolean operacionExitosa = false;

        if (conexion != null) {
            String consulta = "UPDATE proveedor SET razonSocial = ?, telefono = ?, direccion = ?, correo = ? WHERE idProveedor = ?";

            PreparedStatement statement = conexion.prepareStatement(consulta);
            statement.setString(1, proveedor.getRazonSocial());
            statement.setString(2, proveedor.getTelefono());
            statement.setString(3, proveedor.getDireccion());
            statement.setString(4, proveedor.getCorreo());
            statement.setInt(5, proveedor.getIdProveedor());

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
    public boolean eliminar(Integer idProveedor) throws SQLException {
        Connection conexion = ConexionBD.getInstancia().abrirConexion();
        boolean operacionExitosa = false;

        if (conexion != null) {
            String consulta = "DELETE FROM proveedor WHERE idProveedor = ?";
            PreparedStatement statement = conexion.prepareStatement(consulta);
            statement.setInt(1, idProveedor);

            int filasEliminadas = statement.executeUpdate();
            operacionExitosa = filasEliminadas > 0;

            statement.close();
            conexion.close();
        } else {
            throw new SQLException("No se pudo conectar a la base de datos");
        }

        return operacionExitosa;
    }

    private Proveedor convertirProveedor(ResultSet resultado) throws SQLException {
        Proveedor proveedor = new Proveedor();
        proveedor.setIdProveedor(resultado.getInt("idProveedor"));
        proveedor.setRazonSocial(resultado.getString("razonSocial"));
        proveedor.setTelefono(resultado.getString("telefono"));
        proveedor.setDireccion(resultado.getString("direccion"));
        proveedor.setCorreo(resultado.getString("correo"));
        return proveedor;
    }
}