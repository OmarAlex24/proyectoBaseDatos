package expendiocrudproyecto.modelo.pojo;

import java.util.Date;

public class Promocion {
    private int idPromocion;
    private String nombre;
    private String descripcion;
    private int descuento;
    private Date fechaInicio;
    private Date fechaFin;
    private String terminosCondiciones;
    private int idProducto;
    private boolean acumulable;
    private int idCliente;


    public Promocion() {
    }

    public Promocion(String nombre,int idPromocion, String descripcion, int descuento, Date fechaInicio, Date fechaFin, String terminosCondiciones, int idProducto, boolean acumulable) {
        this.idPromocion = idPromocion;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.descuento = descuento;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.terminosCondiciones = terminosCondiciones;
        this.idProducto = idProducto;
        this.acumulable = acumulable;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getIdPromocion() {
        return idPromocion;
    }

    public void setIdPromocion(int idPromocion) {
        this.idPromocion = idPromocion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getDescuento() {
        return descuento;
    }

    public void setDescuento(int descuento) {
        this.descuento = descuento;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Date getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getTerminosCondiciones() {
        return terminosCondiciones;
    }

    public void setTerminosCondiciones(String terminosCondiciones) {
        this.terminosCondiciones = terminosCondiciones;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public boolean isAcumulable() {
        return acumulable;
    }

    public void setAcumulable(boolean acumulable) {
        this.acumulable = acumulable;
    }

}
