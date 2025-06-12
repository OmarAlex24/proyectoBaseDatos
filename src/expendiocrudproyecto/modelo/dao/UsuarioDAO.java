package expendiocrudproyecto.modelo.dao;

import expendiocrudproyecto.modelo.ConexionBD;
import expendiocrudproyecto.modelo.pojo.TipoUsuario;
import expendiocrudproyecto.modelo.pojo.Usuario;
import expendiocrudproyecto.utilidades.HashUtil;
import java.sql.*;

public class UsuarioDAO {


    public UsuarioDAO() {

    }

    public boolean insertarUsuario(Usuario usuario) throws SQLException {
        if (usuario == null || usuario.getUsername() == null || usuario.getContrasenia() == null ||
            usuario.getTipoUsuario() == null || usuario.getIdEmpleado() <= 0) {
            throw new IllegalArgumentException("Los datos del usuario son invalidos.");
        }
        try {
            Connection conexionBD = ConexionBD.getInstancia().abrirConexion();

            String sql = "INSERT INTO usuario (username, contrasenia, idTipoUsuario, idEmpleado) VALUES (?, ?, ?, ?)";

            PreparedStatement stmt = conexionBD.prepareStatement(sql);

            stmt.setString(1, usuario.getUsername());
            stmt.setString(2, HashUtil.hashPassword(usuario.getContrasenia()));
            stmt.setInt(3, usuario.getTipoUsuario().getId());
            stmt.setString(4, String.valueOf(usuario.getIdEmpleado()));
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Usuario autenticar(String nombre, String contrasenia) throws SQLException {
        try {
            Connection conexionBD = ConexionBD.getInstancia().abrirConexion();

            String sql = "SELECT * FROM usuario WHERE username = ?";

            PreparedStatement stmt = conexionBD.prepareStatement(sql);
            stmt.setString(1, nombre);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String contraseniaHash = rs.getString("contrasenia");
                if (HashUtil.hashPassword(contrasenia).equals(contraseniaHash)) {
                    Usuario usuario = new Usuario();
                    usuario.setIdUsuario(rs.getInt("idUsuario"));
                    usuario.setUsername(rs.getString("username"));
                    usuario.setContrasenia(rs.getString("contrasenia"));
                    usuario.setTipoUsuario(TipoUsuario.fromId(rs.getInt("idTipoUsuario")));
                    usuario.setIdEmpleado(rs.getInt("idEmpleado"));
                    return usuario;
                }
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean autorizarStock(String contrasenia){

        try {
                Connection conexionBD = ConexionBD.getInstancia().abrirConexion();

                String sql = "SELECT * FROM usuario WHERE contrasenia = ? AND idTipoUsuario = 1";

                PreparedStatement stmt = conexionBD.prepareStatement(sql);

                String contraseniaHash =  HashUtil.hashPassword(contrasenia);

                System.out.println("Contrasenia hasheada: " + contraseniaHash);

                stmt.setString(1, contraseniaHash);

                ResultSet rs = stmt.executeQuery();

                return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}