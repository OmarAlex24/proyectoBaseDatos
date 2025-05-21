package expendiocrudproyecto.modelo.pojo;

public class Usuario {
    private int idUsuario;
    private String username;
    private String contrasenia;
    private TipoUsuario tipoUsuario;
    private int idEmpleado;

    public Usuario() {
    }

    public Usuario(int idUsuario, String username, String contrasenia, TipoUsuario tipoUsuario, int idEmpleado) {
        this.idUsuario = idUsuario;
        this.username = username;
        this.contrasenia = contrasenia;
        this.tipoUsuario = tipoUsuario;
        this.idEmpleado = idEmpleado;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getContrasenia() {
        return contrasenia;
    }

    public void setContrasenia(String contrasenia) {
        this.contrasenia = contrasenia;
    }

    public TipoUsuario getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(TipoUsuario tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    public int getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(int idEmpleado) {
        this.idEmpleado = idEmpleado;
    }
}
