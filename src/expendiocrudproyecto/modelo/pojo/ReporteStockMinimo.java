package expendiocrudproyecto.modelo.pojo;

import java.math.BigDecimal;

// Clase creada específicamente para el reporte de productos con stock mínimo
public class ReporteStockMinimo {
    private int idProducto;
    private String nombreProducto;
    private String categoria;
    private int stockActual;
    private int stockMinimo;
    private int diferencia;
    private BigDecimal precioUnitario;
    private String estado; // "Crítico", "Bajo", "Agotado"

    public ReporteStockMinimo() {}

    public ReporteStockMinimo(int idProducto, String nombreProducto, String categoria, int stockActual, int stockMinimo, int diferencia, BigDecimal precioUnitario, String estado) {
        this.idProducto = idProducto;
        this.nombreProducto = nombreProducto;
        this.categoria = categoria;
        this.stockActual = stockActual;
        this.stockMinimo = stockMinimo;
        this.diferencia = diferencia;
        this.precioUnitario = precioUnitario;
        this.estado = estado;
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

    public int getStockActual() {
        return stockActual;
    }

    public void setStockActual(int stockActual) {
        this.stockActual = stockActual;
    }

    public int getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(int stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public int getDiferencia() {
        return diferencia;
    }

    public void setDiferencia(int diferencia) {
        this.diferencia = diferencia;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "ReporteStockMinimo{" +
                "idProducto=" + idProducto +
                ", nombreProducto='" + nombreProducto + '\'' +
                ", categoria='" + categoria + '\'' +
                ", stockActual=" + stockActual +
                ", stockMinimo=" + stockMinimo +
                ", diferencia=" + diferencia +
                ", precioUnitario=" + precioUnitario +
                ", estado='" + estado + '\'' +
                '}';
    }
}