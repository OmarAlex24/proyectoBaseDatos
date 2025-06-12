package expendiocrudproyecto.modelo.pojo;

import java.math.BigDecimal;
import java.time.LocalDate;

// Clase creada específicamente para el reporte de producto más vendido a un cliente para ofrecerle una promoción
public class ReporteProductoMasVendidoCliente {
    private int idCliente;
    private String nombreCliente;
    private String apellidoCliente;
    private String telefono;
    private String email;
    private int idProducto;
    private String nombreProducto;
    private String categoria;
    private int cantidadComprada;
    private BigDecimal totalGastado;
    private BigDecimal precioUnitario;
    private LocalDate ultimaCompra;
    private LocalDate primeraCompra;
    private int frecuenciaCompra; // días entre compras promedio
    private String tipoPromocion; // "Descuento", "2x1", "Producto gratis", etc.
    private String observaciones;

    public ReporteProductoMasVendidoCliente() {}

    public ReporteProductoMasVendidoCliente(int idCliente, String nombreCliente, String apellidoCliente, String telefono, String email, int idProducto, String nombreProducto, String categoria, int cantidadComprada, BigDecimal totalGastado, BigDecimal precioUnitario, LocalDate ultimaCompra, LocalDate primeraCompra, int frecuenciaCompra, String tipoPromocion, String observaciones) {
        this.idCliente = idCliente;
        this.nombreCliente = nombreCliente;
        this.apellidoCliente = apellidoCliente;
        this.telefono = telefono;
        this.email = email;
        this.idProducto = idProducto;
        this.nombreProducto = nombreProducto;
        this.categoria = categoria;
        this.cantidadComprada = cantidadComprada;
        this.totalGastado = totalGastado;
        this.precioUnitario = precioUnitario;
        this.ultimaCompra = ultimaCompra;
        this.primeraCompra = primeraCompra;
        this.frecuenciaCompra = frecuenciaCompra;
        this.tipoPromocion = tipoPromocion;
        this.observaciones = observaciones;
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

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public int getCantidadComprada() {
        return cantidadComprada;
    }

    public void setCantidadComprada(int cantidadComprada) {
        this.cantidadComprada = cantidadComprada;
    }

    public BigDecimal getTotalGastado() {
        return totalGastado;
    }

    public void setTotalGastado(BigDecimal totalGastado) {
        this.totalGastado = totalGastado;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public LocalDate getUltimaCompra() {
        return ultimaCompra;
    }

    public void setUltimaCompra(LocalDate ultimaCompra) {
        this.ultimaCompra = ultimaCompra;
    }

    public LocalDate getPrimeraCompra() {
        return primeraCompra;
    }

    public void setPrimeraCompra(LocalDate primeraCompra) {
        this.primeraCompra = primeraCompra;
    }

    public int getFrecuenciaCompra() {
        return frecuenciaCompra;
    }

    public void setFrecuenciaCompra(int frecuenciaCompra) {
        this.frecuenciaCompra = frecuenciaCompra;
    }

    public String getTipoPromocion() {
        return tipoPromocion;
    }

    public void setTipoPromocion(String tipoPromocion) {
        this.tipoPromocion = tipoPromocion;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    // Auxiliar para obtener nombre completo del cliente
    public String getNombreCompletoCliente() {
        return nombreCliente + " " + apellidoCliente;
    }

    @Override
    public String toString() {
        return "ReporteProductoMasVendidoCliente{" +
                "idCliente=" + idCliente +
                ", nombreCliente='" + nombreCliente + '\'' +
                ", apellidoCliente='" + apellidoCliente + '\'' +
                ", telefono='" + telefono + '\'' +
                ", email='" + email + '\'' +
                ", idProducto=" + idProducto +
                ", nombreProducto='" + nombreProducto + '\'' +
                ", categoria='" + categoria + '\'' +
                ", cantidadComprada=" + cantidadComprada +
                ", totalGastado=" + totalGastado +
                ", precioUnitario=" + precioUnitario +
                ", ultimaCompra=" + ultimaCompra +
                ", primeraCompra=" + primeraCompra +
                ", frecuenciaCompra=" + frecuenciaCompra +
                ", tipoPromocion='" + tipoPromocion + '\'' +
                ", observaciones='" + observaciones + '\'' +
                '}';
    }
}