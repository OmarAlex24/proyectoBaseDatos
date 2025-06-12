package expendiocrudproyecto.modelo.pojo;

import java.math.BigDecimal;

// Clase creada específicamente para el reporte de productos menos vendidos
public class ReporteProductosMenosVendidos {
    private int posicion;
    private int idProducto;
    private String nombreProducto;
    private String categoria;
    private int cantidadVendida;
    private BigDecimal totalVentas;
    private BigDecimal precioUnitario;
    private int stockActual;
    private String observacion; // "Sin ventas", "Ventas bajas", etc.

    public ReporteProductosMenosVendidos() {}

    public ReporteProductosMenosVendidos(int posicion, int idProducto, String nombreProducto, String categoria, int cantidadVendida, BigDecimal totalVentas, BigDecimal precioUnitario, int stockActual, String observacion) {
        this.posicion = posicion;
        this.idProducto = idProducto;
        this.nombreProducto = nombreProducto;
        this.categoria = categoria;
        this.cantidadVendida = cantidadVendida;
        this.totalVentas = totalVentas;
        this.precioUnitario = precioUnitario;
        this.stockActual = stockActual;
        this.observacion = observacion;
    }

    public int getPosicion() {
        return posicion;
    }

    public void setPosicion(int posicion) {
        this.posicion = posicion;
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

    public int getCantidadVendida() {
        return cantidadVendida;
    }

    public void setCantidadVendida(int cantidadVendida) {
        this.cantidadVendida = cantidadVendida;
    }

    public BigDecimal getTotalVentas() {
        return totalVentas;
    }

    public void setTotalVentas(BigDecimal totalVentas) {
        this.totalVentas = totalVentas;
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

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    @Override
    public String toString() {
        return "ReporteProductosMenosVendidos{" +
                "posicion=" + posicion +
                ", idProducto=" + idProducto +
                ", nombreProducto='" + nombreProducto + '\'' +
                ", categoria='" + categoria + '\'' +
                ", cantidadVendida=" + cantidadVendida +
                ", totalVentas=" + totalVentas +
                ", precioUnitario=" + precioUnitario +
                ", stockActual=" + stockActual +
                ", observacion='" + observacion + '\'' +
                '}';
    }
}