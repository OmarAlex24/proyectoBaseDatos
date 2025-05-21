/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package expendiocrudproyecto.modelo;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author OmarAlex
 */
public class ConexionBD {

    private static final String IP = "127.0.0.1";
    private static final String PUERTO = "3306";
    private static final String NOMBREBD = "proyectoBebida";
    private static final String USUARIO = "root";
    private static final String PASSWORD = "Monte1324.";
    private static final String DRIVER = "com.mysql.jdbc.Driver";

    public static Connection abrirConexion() {
        Connection conexionBD = null;
        String urlConexion = String.format("jdbc:mysql://%s:%s/%s?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC", IP, PUERTO, NOMBREBD);
        try {
            Class.forName(DRIVER);
            conexionBD = DriverManager.getConnection(urlConexion, USUARIO, PASSWORD);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.err.println("Error Clase no encontrada");
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error en la conexion: " + e.getMessage());
        }
        return conexionBD;
    }

}
