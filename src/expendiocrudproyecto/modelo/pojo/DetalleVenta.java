package expendiocrudproyecto.modelo.pojo;

import expendiocrudproyecto.modelo.dao.BebidaDAO;

import java.sql.SQLException;

public class DetalleVenta {
    private int idVenta;
    private int idProducto;
    private Bebida bebida;
    private double total_pagado;
    private int cantidadUnitaria;
    private double precioUnitario;



    public DetalleVenta() {}

    public DetalleVenta(int idVenta, int idProducto, double total_pagado, int cantidadUnitaria) throws SQLException {
        BebidaDAO bebidaDAO = new BebidaDAO();

        this.idVenta = idVenta;
        this.idProducto = idProducto;
        this.total_pagado = total_pagado;
        this.cantidadUnitaria = cantidadUnitaria;
        this.precioUnitario = total_pagado / cantidadUnitaria;

        this.bebida = bebidaDAO.leerPorId(idProducto);
    }

    public Bebida getBebida() {
        return bebida;
    }

    public void setBebida(Bebida bebida) {
        this.bebida = bebida;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public int getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(int idVenta) {
        this.idVenta = idVenta;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public double getTotal_pagado() {
        return total_pagado;
    }

    public void setTotal_pagado(double total_pagado) {
        this.total_pagado = total_pagado;
    }

    public int getCantidadUnitaria() {
        return cantidadUnitaria;
    }

    public void setCantidadUnitaria(int cantidadUnitaria) {
        this.cantidadUnitaria = cantidadUnitaria;
    }
}
