/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package expendiocrudproyecto.modelo.pojo;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author OmarAlex
 */
public class Bebida {
    private Integer id;
    private String nombre;
    private Float precio;
    private Integer stock;
    private Integer stockMinimo;

    public Bebida() {
    }

    public Bebida(String nombre, Float precio, Integer stock, Integer stockMinimo) {
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
        this.stockMinimo = stockMinimo;
    }

    public Bebida(Integer id, String nombre, Float precio, Integer stock, Integer stockMinimo) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
        this.stockMinimo = stockMinimo;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Float getPrecio() {
        return precio;
    }

    public void setPrecio(Float precio) {
        this.precio = precio;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Integer getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(Integer stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
