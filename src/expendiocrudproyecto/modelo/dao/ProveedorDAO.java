package expendiocrudproyecto.modelo.dao;


import expendiocrudproyecto.modelo.pojo.Proveedor;
import java.util.List;

public class ProveedorDAO implements CrudDAO<Proveedor>{
    @Override
    public List<Proveedor> leerTodo() {
        return null;
    }

    @Override
    public Proveedor leerPorId(Integer id) {
        return null;
    }

    @Override
    public Proveedor insertar(Proveedor proveedor) {
        return null;
    }

    @Override
    public boolean actualizar(Proveedor proveedor) {
        return false;
    }

    @Override
    public boolean eliminar(Integer proveedor) {
        return false;
    }
}
