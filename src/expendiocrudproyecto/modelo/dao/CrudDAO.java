/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package expendiocrudproyecto.modelo.dao;

import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author OmarAlex
 */
public interface CrudDAO<T> {
    
    List<T> leerTodo() throws SQLException;

    T leerPorId(Integer id) throws SQLException;

    T insertar(T t) throws SQLException;

    boolean actualizar(T t) throws SQLException;

    boolean eliminar(Integer t) throws SQLException;
    
    
    
}
