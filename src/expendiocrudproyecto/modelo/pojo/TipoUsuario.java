package expendiocrudproyecto.modelo.pojo;

public enum TipoUsuario {
    ADMINISTRADOR(1, "Administrador"),
    EMPLEADO(2, "Empleado");

    private final int id;
    private final String nombre;

    TipoUsuario(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public static TipoUsuario fromId(int id) {
        for (TipoUsuario tipo : TipoUsuario.values()) {
            if (tipo.getId() == id) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("No se encontró un tipo de usuario con el ID: " + id);
    }
}
