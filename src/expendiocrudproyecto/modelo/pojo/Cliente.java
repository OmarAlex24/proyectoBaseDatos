package expendiocrudproyecto.modelo.pojo;

public class Cliente {
    private Integer idCliente;
    private String nombre;
    private String telefono;
    private String correo;
    private String razonSocial;

    public Cliente() {
    }

    public Cliente(String nombre, String telefono, String correo, String razonSocial) {
        this.nombre = nombre;
        this.telefono = telefono;
        this.correo = correo;
        this.razonSocial = razonSocial;
    }

    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }
}
