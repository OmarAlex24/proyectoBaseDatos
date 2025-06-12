package expendiocrudproyecto.utilidades;

import expendiocrudproyecto.modelo.pojo.Usuario;

public class SesionUsuario {
    private static SesionUsuario instancia;
    private Usuario usuarioLogueado;

    private SesionUsuario() {
    }

    public static SesionUsuario getInstancia() {
        if (instancia == null) {
            instancia = new SesionUsuario();
        }
        return instancia;
    }

    public Usuario getUsuarioLogueado() {
        return usuarioLogueado;
    }

    public void setUsuarioLogueado(Usuario usuarioLogueado) {
        this.usuarioLogueado = usuarioLogueado;
    }

    public void cerrarSesion() {
        this.usuarioLogueado = null;
    }

    public String getRolUsuario() {
        if (usuarioLogueado != null && usuarioLogueado.getTipoUsuario() != null) {
            return usuarioLogueado.getTipoUsuario().getNombre();
        }
        return "login_checker";
    }
}