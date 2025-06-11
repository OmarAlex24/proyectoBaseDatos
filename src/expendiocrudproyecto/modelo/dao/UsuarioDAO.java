package expendiocrudproyecto.modelo.dao;

import expendiocrudproyecto.modelo.pojo.TipoUsuario;
import expendiocrudproyecto.modelo.pojo.Usuario;
import expendiocrudproyecto.utilidades.HashUtil;
import java.sql.*;

public class UsuarioDAO {
    private Connection conn;

    public UsuarioDAO(Connection conn) {
        this.conn = conn;
    }

    public boolean insertarUsuario(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO usuario (username, contrasenia, idTipoUsuario, idEmpleado) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, usuario.getUsername());
            stmt.setString(2, HashUtil.hashPassword(usuario.getContrasenia()));
            stmt.setInt(3, usuario.getTipoUsuario().getId());
            stmt.setString(4, String.valueOf(usuario.getIdEmpleado()));
            return stmt.executeUpdate() > 0;
        }
    }

    public Usuario autenticar(String nombre, String contrasenia) throws SQLException {
        String sql = "SELECT * FROM usuario WHERE username = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
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
        }
    }
}