package expendiocrudproyecto.modelo.pojo;

import java.util.Date;

public class PedidoCliente {
  private Integer idPedido;
  private String folio;
  private Date fecha;
  private Integer idCliente;
  private String nombreCliente;
  private Double total;
  private String estado;

  public PedidoCliente() {
  }

  public PedidoCliente(Integer idPedido, String folio, Date fecha, Integer idCliente, String nombreCliente,
      Double total, String estado) {
    this.idPedido = idPedido;
    this.folio = folio;
    this.fecha = fecha;
    this.idCliente = idCliente;
    this.nombreCliente = nombreCliente;
    this.total = total;
    this.estado = estado;
  }

  public Integer getIdPedido() {
    return idPedido;
  }

  public void setIdPedido(Integer idPedido) {
    this.idPedido = idPedido;
  }

  public String getFolio() {
    return folio;
  }

  public void setFolio(String folio) {
    this.folio = folio;
  }

  public Date getFecha() {
    return fecha;
  }

  public void setFecha(Date fecha) {
    this.fecha = fecha;
  }

  public Integer getIdCliente() {
    return idCliente;
  }

  public void setIdCliente(Integer idCliente) {
    this.idCliente = idCliente;
  }

  public String getNombreCliente() {
    return nombreCliente;
  }

  public void setNombreCliente(String nombreCliente) {
    this.nombreCliente = nombreCliente;
  }

  public Double getTotal() {
    return total;
  }

  public void setTotal(Double total) {
    this.total = total;
  }

  public String getEstado() {
    return estado;
  }

  public void setEstado(String estado) {
    this.estado = estado;
  }
}