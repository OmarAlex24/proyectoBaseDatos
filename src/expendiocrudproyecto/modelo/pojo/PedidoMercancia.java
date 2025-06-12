package expendiocrudproyecto.modelo.pojo;

import java.util.Date;

public class PedidoMercancia {
  private Integer idPedido;
  private String folio;
  private Date fecha;
  private Integer idProveedor;
  private String nombreProveedor;
  private Double total;
  private String estado;

  public PedidoMercancia() {
  }

  public PedidoMercancia(Integer idPedido, String folio, Date fecha, Integer idProveedor, String nombreProveedor,
      Double total, String estado) {
    this.idPedido = idPedido;
    this.folio = folio;
    this.fecha = fecha;
    this.idProveedor = idProveedor;
    this.nombreProveedor = nombreProveedor;
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

  public Integer getIdProveedor() {
    return idProveedor;
  }

  public void setIdProveedor(Integer idProveedor) {
    this.idProveedor = idProveedor;
  }

  public String getNombreProveedor() {
    return nombreProveedor;
  }

  public void setNombreProveedor(String nombreProveedor) {
    this.nombreProveedor = nombreProveedor;
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