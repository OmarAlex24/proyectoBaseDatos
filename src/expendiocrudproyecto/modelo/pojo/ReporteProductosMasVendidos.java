package expendiocrudproyecto.modelo.pojo;

import java.math.BigDecimal;

// Clase creada específicamente para el reporte de productos más vendidos
public class ReporteProductosMasVendidos {
    private int posicion;
    private int idProducto;
    private String nombreProducto;
    private String categoria;
    private int cantidadVendida;
    private BigDecimal totalVentas;
    private BigDecimal precioUnitario;
    private double porcentajeVentas;

    public ReporteProductosMasVendidos() {}

    public ReporteProductosMasVendidos(int posicion, int idProducto, String nombreProducto, String categoria, int cantidadVendida, BigDecimal totalVentas, BigDecimal precioUnitario, double porcentajeVentas) {
        this.posicion = posicion;
        this.idProducto = idProducto;
        this.nombreProducto = nombreProducto;
        this.categoria = categoria;
        this.cantidadVendida = cantidadVendida;
        this.totalVentas = totalVentas;
        this.precioUnitario = precioUnitario;
        this.porcentajeVentas = porcentajeVentas;
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

    public double getPorcentajeVentas() {
        return porcentajeVentas;
    }

    public void setPorcentajeVentas(double porcentajeVentas) {
        this.porcentajeVentas = porcentajeVentas;
    }

    @Override
    public String toString() {
        return "ReporteProductosMasVendidos{" +
                "posicion=" + posicion +
                ", idProducto=" + idProducto +
                ", nombreProducto='" + nombreProducto + '\'' +
                ", categoria='" + categoria + '\'' +
                ", cantidadVendida=" + cantidadVendida +
                ", totalVentas=" + totalVentas +
                ", precioUnitario=" + precioUnitario +
                ", porcentajeVentas=" + porcentajeVentas +
                '}';
    }
}