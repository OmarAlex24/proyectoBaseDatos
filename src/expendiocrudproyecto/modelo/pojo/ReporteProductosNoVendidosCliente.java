package expendiocrudproyecto.modelo.pojo;

import java.math.BigDecimal;

// Clase creada específicamente para el reporte de productos que no se le ha vendido a un cliente
public class ReporteProductosNoVendidosCliente {
    private int idCliente;
    private String nombreCliente;
    private String apellidoCliente;
    private int idProducto;
    private String nombreProducto;
    private String categoria;
    private BigDecimal precioUnitario;
    private int stockActual;
    private String descripcion;
    private String recomendacion; // "Nuevo para el cliente", "Producto popular", etc.

    public ReporteProductosNoVendidosCliente() {}

    public ReporteProductosNoVendidosCliente(int idCliente, String nombreCliente, String apellidoCliente, int idProducto, String nombreProducto, String categoria, BigDecimal precioUnitario, int stockActual, String descripcion, String recomendacion) {
        this.idCliente = idCliente;
        this.nombreCliente = nombreCliente;
        this.apellidoCliente = apellidoCliente;
        this.idProducto = idProducto;
        this.nombreProducto = nombreProducto;
        this.categoria = categoria;
        this.precioUnitario = precioUnitario;
        this.stockActual = stockActual;
        this.descripcion = descripcion;
        this.recomendacion = recomendacion;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getApellidoCliente() {
        return apellidoCliente;
    }

    public void setApellidoCliente(String apellidoCliente) {
        this.apellidoCliente = apellidoCliente;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public int getStockActual() {
        return stockActual;
    }

    public void setStockActual(int stockActual) {
        this.stockActual = stockActual;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getRecomendacion() {
        return recomendacion;
    }

    public void setRecomendacion(String recomendacion) {
        this.recomendacion = recomendacion;
    }

    // Auxiliar para obtener nombre completo del cliente
    public String getNombreCompletoCliente() {
        return nombreCliente + " " + apellidoCliente;
    }

    @Override
    public String toString() {
        return "ReporteProductosNoVendidosCliente{" +
                "idCliente=" + idCliente +
                ", nombreCliente='" + nombreCliente + '\'' +
                ", apellidoCliente='" + apellidoCliente + '\'' +
                ", idProducto=" + idProducto +
                ", nombreProducto='" + nombreProducto + '\'' +
                ", categoria='" + categoria + '\'' +
                ", precioUnitario=" + precioUnitario +
                ", stockActual=" + stockActual +
                ", descripcion='" + descripcion + '\'' +
                ", recomendacion='" + recomendacion + '\'' +
                '}';
    }
}