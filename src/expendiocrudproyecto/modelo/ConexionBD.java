package expendiocrudproyecto.modelo;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import expendiocrudproyecto.utilidades.SesionUsuario;

public class ConexionBD {
    private Connection conexion;
    private static ConexionBD instancia;

    private ConexionBD() {
    }

    public static ConexionBD getInstancia() {
        if (instancia == null) {
            instancia = new ConexionBD();
        }
        return instancia;
    }

    public Connection abrirConexion() throws SQLException {
        cerrarConexion();
        try (InputStream input = ConexionBD.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new SQLException("No se encontro el archivo database.properties.");
            }
            Properties prop = new Properties();
            prop.load(input);

            String url = prop.getProperty("db.url");

            String rol = SesionUsuario.getInstancia().getRolUsuario();

            if (rol == null || rol.isEmpty()) {
                throw new SQLException("No se puede abrir una conexiÃ³n principal sin un rol de usuario valido.");
            }

            String userKey = "db.user." + rol;
            String passKey = "db.password." + rol;

            String usuarioDB = prop.getProperty(userKey);
            String passwordDB = prop.getProperty(passKey);

            if (usuarioDB == null || passwordDB == null) {
                throw new SQLException("No se encontraron las credenciales para el rol: " + rol);
            }

            Class.forName("com.mysql.cj.jdbc.Driver");
            conexion = DriverManager.getConnection(url, usuarioDB, passwordDB);
        } catch (ClassNotFoundException | IOException e) {
            System.out.println("Error en la conexion a la BD: " + e.getMessage());
            throw new SQLException("No se pudo establecer la conexiÃ³n con la base de datos.");
        }
        return conexion;
    }

    public void cerrarConexion() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
            }
        } catch (SQLException e) {
            System.out.println("Error al cerrar la conexiÃ³n: " + e.getMessage());
        }
    }

}