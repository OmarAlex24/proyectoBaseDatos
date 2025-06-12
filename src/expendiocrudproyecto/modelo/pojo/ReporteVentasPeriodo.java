package expendiocrudproyecto.modelo.pojo;

import java.math.BigDecimal;
import java.time.LocalDate;

// Clase creada específicamente para el reporte de ventas semanales, mensuales, anuales
public class ReporteVentasPeriodo {
    private LocalDate fecha;
    private int totalPedidos;
    private BigDecimal totalVentas;
    private String periodo; // "Semanal", "Mensual", "Anual"

    public ReporteVentasPeriodo() {}

    public ReporteVentasPeriodo(LocalDate fecha, int totalPedidos, BigDecimal totalVentas, String periodo) {
        this.fecha = fecha;
        this.totalPedidos = totalPedidos;
        this.totalVentas = totalVentas;
        this.periodo = periodo;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public int getTotalPedidos() {
        return totalPedidos;
    }

    public void setTotalPedidos(int totalPedidos) {
        this.totalPedidos = totalPedidos;
    }

    public BigDecimal getTotalVentas() {
        return totalVentas;
    }

    public void setTotalVentas(BigDecimal totalVentas) {
        this.totalVentas = totalVentas;
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    @Override
    public String toString() {
        return "ReporteVentasPeriodo{" +
                "fecha=" + fecha +
                ", totalPedidos=" + totalPedidos +
                ", totalVentas=" + totalVentas +
                ", periodo='" + periodo + '\'' +
                '}';
    }
}