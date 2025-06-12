package expendiocrudproyecto.modelo.dao;

import expendiocrudproyecto.modelo.ConexionBD;
import expendiocrudproyecto.modelo.pojo.Cliente;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO implements CrudDAO<Cliente> {

    @Override
    public List<Cliente> leerTodo() throws SQLException {
        List<Cliente> clientes = new ArrayList<>();
        Connection conexion = ConexionBD.getInstancia().abrirConexion();

        if (conexion != null) {
            String consulta = "SELECT idCliente, nombre, telefono, correo, razonSocial FROM cliente";
            PreparedStatement statement = conexion.prepareStatement(consulta);
            ResultSet resultado = statement.executeQuery();

            while (resultado.next()) {
                clientes.add(convertirCliente(resultado));
            }

            resultado.close();
            statement.close();
            conexion.close();
        } else {
            throw new SQLException("No se pudo conectar a la base de datos");
        }

        return clientes;
    }

    @Override
    public Cliente leerPorId(Integer id) throws SQLException {
        Cliente cliente = null;
        Connection conexion = ConexionBD.getInstancia().abrirConexion();

        if (conexion != null) {
            String consulta = "SELECT idCliente, nombre, telefono, correo, razonSocial FROM cliente WHERE idCliente = ?";
            PreparedStatement statement = conexion.prepareStatement(consulta);
            statement.setInt(1, id);
            ResultSet resultado = statement.executeQuery();

            if (resultado.next()) {
                cliente = convertirCliente(resultado);
            }

            resultado.close();
            statement.close();
            conexion.close();
        } else {
            throw new SQLException("No se pudo conectar a la base de datos");
        }

        return cliente;
    }

    @Override
    public Cliente insertar(Cliente cliente) throws SQLException {
        Connection conexion = ConexionBD.getInstancia().abrirConexion();

        if (conexion != null) {
            String consulta = "INSERT INTO cliente (nombre, telefono, correo, razonSocial) VALUES (?, ?, ?, ?)";

            PreparedStatement statement = conexion.prepareStatement(consulta, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, cliente.getNombre());
            statement.setString(2, cliente.getTelefono());
            statement.setString(3, cliente.getCorreo());
            statement.setString(4, cliente.getRazonSocial());

            int filasInsertadas = statement.executeUpdate();

            if (filasInsertadas > 0) {
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    cliente.setIdCliente(generatedKeys.getInt(1));
                }
                generatedKeys.close();
            }

            statement.close();
            conexion.close();
        } else {
            throw new SQLException("No se pudo conectar a la base de datos");
        }

        return cliente;
    }

    @Override
    public boolean actualizar(Cliente cliente) throws SQLException {
        Connection conexion = ConexionBD.getInstancia().abrirConexion();
        boolean operacionExitosa = false;

        if (conexion != null) {
            String consulta = "UPDATE cliente SET nombre = ?, telefono = ?, correo = ?, razonSocial = ? WHERE idCliente = ?";

            PreparedStatement statement = conexion.prepareStatement(consulta);
            statement.setString(1, cliente.getNombre());
            statement.setString(2, cliente.getTelefono());
            statement.setString(3, cliente.getCorreo());
            statement.setString(4, cliente.getRazonSocial());
            statement.setInt(5, cliente.getIdCliente());

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
    public boolean eliminar(Integer idCliente) throws SQLException {
        Connection conexion = ConexionBD.getInstancia().abrirConexion();
        boolean operacionExitosa = false;

        if (conexion != null) {
            String consulta = "DELETE FROM cliente WHERE idCliente =  ?";
            PreparedStatement statement = conexion.prepareStatement(consulta);
            statement.setInt(1, idCliente);

            int filasEliminadas = statement.executeUpdate();
            operacionExitosa = filasEliminadas > 0;

            statement.close();
            conexion.close();
        } else {
            throw new SQLException("No se pudo conectar a la base de datos");
        }

        return operacionExitosa;
    }

    private Cliente convertirCliente(ResultSet resultado) throws SQLException {
        Cliente cliente = new Cliente();
        cliente.setIdCliente(resultado.getInt("idCliente"));
        cliente.setNombre(resultado.getString("nombre"));
        cliente.setTelefono(resultado.getString("telefono"));
        cliente.setCorreo(resultado.getString("correo"));
        cliente.setRazonSocial(resultado.getString("razonSocial"));
        return cliente;
    }

    public List<Cliente> buscarPorNombreOApellido(String criterio) throws SQLException {
        List<Cliente> clientes = new ArrayList<>();
        Connection conexion = ConexionBD.getInstancia().abrirConexion();

        if (conexion != null) {
            String consulta = "SELECT idCliente, nombre, telefono, correo, razonSocial FROM cliente WHERE nombre LIKE ?";
            PreparedStatement statement = conexion.prepareStatement(consulta);
            String likeCriterio = "%" + criterio + "%";
            statement.setString(1, likeCriterio);
            ResultSet resultado = statement.executeQuery();

            while (resultado.next()) {
                clientes.add(convertirCliente(resultado));
            }

            resultado.close();
            statement.close();
            conexion.close();
        } else {
            throw new SQLException("No se pudo conectar a la base de datos");
        }

        return clientes;
    }
}