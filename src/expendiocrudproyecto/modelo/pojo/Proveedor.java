package expendiocrudproyecto.modelo.pojo;

public class Proveedor {
    private Integer idProveedor;
    private String telefono;
    private String razonSocial;
    private String direccion;
    private String correo;

    public Proveedor() {
    }

    public Proveedor(String telefono, String razonSocial, String direccion, String correo) {
        this.telefono = telefono;
        this.razonSocial = razonSocial;
        this.direccion = direccion;
        this.correo = correo;
    }

    public Integer getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(Integer idProveedor) {
        this.idProveedor = idProveedor;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }
}
