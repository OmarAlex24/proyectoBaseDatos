package expendiocrudproyecto.modelo.dao;

import expendiocrudproyecto.modelo.ConexionBD;
import expendiocrudproyecto.modelo.pojo.Cliente;
import expendiocrudproyecto.modelo.pojo.DetalleVenta;
import expendiocrudproyecto.modelo.pojo.Venta;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class VentaDAO implements CrudDAO<Venta> {

    @Override
    public List<Venta> leerTodo() throws SQLException {
        List<Venta> ventas = new ArrayList<>();
        Connection conexion = ConexionBD.abrirConexion();

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
        Connection conexion = ConexionBD.abrirConexion();

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

    @Override
    public Venta insertar(Venta venta) throws SQLException {
        Connection conexion = ConexionBD.abrirConexion();

        if (conexion != null) {
            try {
                // Iniciar transacción
                conexion.setAutoCommit(false);

                // 1. Insertar la venta
                int idVenta = insertarVenta(conexion, venta);
                venta.setIdVenta(idVenta);

                // 2. Insertar los detalles de la venta
                if (venta != null ) {
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
        Connection conexion = ConexionBD.abrirConexion();

        if (conexion != null) {
            String consulta = "SELECT idCliente, nombre, telefono, correo, razonSocial FROM cliente";
            PreparedStatement statement = conexion.prepareStatement(consulta);
            ResultSet resultado = statement.executeQuery();

            // Agregar opción "Sin cliente"
            Cliente sinCliente = new Cliente();
            sinCliente.setIdCliente(0);
            sinCliente.setNombre("Sin cliente");
            clientes.add(sinCliente);

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
        Integer idCliente = venta.getIdCliente() != 0 && venta.getIdCliente() > 0 ?
                venta.getIdCliente() : null;

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

    private void insertarDetallesVenta(Connection conexion, List<DetalleVenta> detalles, int idVenta) throws SQLException {
        String consulta = "INSERT INTO detalle_venta (Producto_idProducto, Venta_idVenta, total_pagado, cantidadUnitaria) " +
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
                String.format("%04d", (int)(Math.random() * 10000));
    }
}