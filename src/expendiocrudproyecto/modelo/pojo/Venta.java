package expendiocrudproyecto.modelo.pojo;

import java.util.ArrayList;
import java.util.Date;

public class Venta {
  private int idVenta;
  private int idCliente;
  private String folioFactura;
  private Date fechaVenta;
  private Double totalVenta;
  private Double subtotal;
  private Double descuento;
  private ArrayList<DetalleVenta> detalleVenta;

  public Venta() {
  }

  public Venta(int idVenta, int idCliente, String folioFactura, Date fechaVenta, Double totalVenta, Double subtotal,
      Double descuento, ArrayList<DetalleVenta> detalleVenta) {
    this.idVenta = idVenta;
    this.idCliente = idCliente;
    this.folioFactura = folioFactura;
    this.fechaVenta = fechaVenta;
    this.totalVenta = totalVenta;
    this.subtotal = subtotal;
    this.descuento = descuento;
    this.detalleVenta = detalleVenta;
  }

  public Double getTotalVenta() {
    return totalVenta;
  }

  public void setTotalVenta(Double totalVenta) {
    this.totalVenta = totalVenta;
  }

  public Double getSubtotal() {
    return subtotal;
  }

  public void setSubtotal(Double subtotal) {
    this.subtotal = subtotal;
  }

  public Double getDescuento() {
    return descuento;
  }

  public void setDescuento(Double descuento) {
    this.descuento = descuento;
  }

  public int getIdVenta() {
    return idVenta;
  }

  public void setIdVenta(int idVenta) {
    this.idVenta = idVenta;
  }

  public int getIdCliente() {
    return idCliente;
  }

  public void setIdCliente(int idCliente) {
    this.idCliente = idCliente;
  }

  public String getFolioFactura() {
    return folioFactura;
  }

  public void setFolioFactura(String folioFactura) {
    this.folioFactura = folioFactura;
  }

  public Date getFechaVenta() {
    return fechaVenta;
  }

  public void setFechaVenta(Date fechaVenta) {
    this.fechaVenta = fechaVenta;
  }

  public ArrayList<DetalleVenta> getDetalleVenta() {
    return detalleVenta;
  }

  public void setDetalleVenta(ArrayList<DetalleVenta> detalleVenta) {
    this.detalleVenta = detalleVenta;
  }
}
