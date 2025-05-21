package expendiocrudproyecto.modelo.pojo;

import java.util.Date;

public class PedidoCliente {
    private int idPedidoCliente;
    private Date fechaPedido;
    private Cliente cliente;

    public PedidoCliente(int idPedidoCliente, Date fechaPedido, Cliente cliente) {
        this.idPedidoCliente = idPedidoCliente;
        this.fechaPedido = fechaPedido;
        this.cliente = cliente;
    }

    public int getIdPedidoCliente() {
        return idPedidoCliente;
    }

    public void setIdPedidoCliente(int idPedidoCliente) {
        this.idPedidoCliente = idPedidoCliente;
    }

    public Date getFechaPedido() {
        return fechaPedido;
    }

    public void setFechaPedido(Date fechaPedido) {
        this.fechaPedido = fechaPedido;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }
}