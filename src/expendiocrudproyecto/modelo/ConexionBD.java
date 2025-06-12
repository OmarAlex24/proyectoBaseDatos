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
        System.out.println("Abriendo conexiÃ³n a la base de datos...");
        cerrarConexion();
        System.out.println("ConexiÃ³n cerrada, abriendo una nueva conexiÃ³n...");
        try (InputStream input = ConexionBD.class.getClassLoader().getResourceAsStream("config.properties")) {
            System.out.println("Intentando cargar el archivo de configuraciÃ³n...");

            if (input == null) {
                throw new SQLException("No se encontro el archivo database.properties.");
            }
            System.out.println("Archivo de configuraciÃ³n encontrado, cargando propiedades...");
            Properties prop = new Properties();
            prop.load(input);
            System.out.println("Archivo de configuraciÃ³n cargado correctamente.");

            String url = prop.getProperty("db.url");

            String rol = SesionUsuario.getInstancia().getRolUsuario();

            System.out.println(rol);

            if (rol == null || rol.isEmpty()) {
                throw new SQLException("No se puede abrir una conexiÃ³n principal sin un rol de usuario valido.");
            }

            String userKey = "db.user." + rol;
            String passKey = "db.password." + rol;

            System.out.println("Clave de usuario: " + userKey);
            System.out.println("Clave de contraseÃ±a: " + passKey);

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