package ec.edu.espol.modelo;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BaseDatos {
    private HashMap<Integer, Anfitrion> anfitriones;
    private HashMap<Integer, Huesped> huespedes;
    private static BaseDatos database;       // Singleton

    /* Getters */
    public HashMap<Integer, Anfitrion> getAnfitriones() {
        return anfitriones;
    }
    public HashMap<Integer, Huesped> getHuespedes() {
        return huespedes;
    }

    private BaseDatos() {
        anfitriones = new HashMap<Integer, Anfitrion>();
        huespedes = new HashMap<Integer, Huesped>();
    }

    public static BaseDatos getDataBase() {
        if (database == null) {
            database = new BaseDatos();
        }
        return database;
    }

    public void agregarAnfitrion(Anfitrion anfitrion) {
        anfitriones.put(anfitrion.getID(), anfitrion);
    }

    public void agregarHuesped(Huesped huesped) {
        huespedes.put(huesped.getID(), huesped);
    }

    public void inicializarDatosDemo() {
        if (!anfitriones.isEmpty()) {
            return;
        }

        Anfitrion ana = new Anfitrion("Ana Torres", 1);
        Anfitrion mario = new Anfitrion("Mario Cedeño", 2);
        Anfitrion sofia = new Anfitrion("Sofia Andrade", 3);

        agregarAnfitrion(ana);
        agregarAnfitrion(mario);
        agregarAnfitrion(sofia);

        ana.agregarPropiedad(crearPropiedad(
                "Salinas",
                ana,
                List.of(Servicio.WiFi, Servicio.Piscina, Servicio.Estacionamiento),
                crearUnidad("Departamento", 85),
                crearUnidad("Habitacion", 42)
        ));
        ana.agregarPropiedad(crearPropiedad(
                "Guayaquil",
                ana,
                List.of(Servicio.WiFi, Servicio.PetFriendly),
                crearUnidad("Casa", 120),
                crearUnidad("Habitacion", 35)
        ));
        mario.agregarPropiedad(crearPropiedad(
                "Cuenca",
                mario,
                List.of(Servicio.WiFi, Servicio.Estacionamiento),
                crearUnidad("Departamento", 70),
                crearUnidad("Casa", 150)
        ));
        sofia.agregarPropiedad(crearPropiedad(
                "Quito",
                sofia,
                List.of(Servicio.PetFriendly, Servicio.WiFi),
                crearUnidad("Habitacion", 48),
                crearUnidad("Departamento", 95)
        ));
        sofia.agregarPropiedad(crearPropiedad(
                "Manta",
                sofia,
                List.of(Servicio.Piscina, Servicio.Estacionamiento),
                crearUnidad("Casa", 180),
                crearUnidad("Departamento", 110)
        ));
    }

    private Propiedad crearPropiedad(String ubicacion, Anfitrion propietario, List<Servicio> servicios, Unidad... unidades) {
        Propiedad propiedad = new Propiedad(ubicacion, new ArrayList<>(), propietario, new ArrayList<>());
        propiedad.setServicios(new ArrayList<>(servicios));
        for (Unidad unidad : unidades) {
            unidad.setPropiedad(propiedad);
            propiedad.agregarUnidad(unidad);
        }
        return propiedad;
    }

    private Unidad crearUnidad(String tipo, double precio) {
        Unidad unidad;
        if ("Casa".equals(tipo)) {
            unidad = new Casa();
        } else if ("Departamento".equals(tipo)) {
            unidad = new DepartamentoCompleto();
        } else {
            unidad = new HabitacionPrivada();
        }
        unidad.setPrecio(precio);
        unidad.setEstadoAlojamiento(EstadoAlojamiento.DISPONIBLE);
        return unidad;
    }

    public void mostrarAnfitriones() {
        for (Anfitrion a : anfitriones.values()) {
            System.out.println(a);
        }
    }

    public void mostrarHuespedes() {
        for (Huesped h : huespedes.values()) {
            System.out.println(h);
        }
    }

    /* Buscar anfitrión por ID */
    public Anfitrion buscarAnfitrion(int id) {
        return anfitriones.get(id);
    }

    /* Buscar huésped por ID */
    public Huesped buscarHuesped(int id) {
        return huespedes.get(id);
    }

    /* Eliminar anfitrión por ID */
    public void eliminarAnfitrion(int id) {
        anfitriones.remove(id);
    }

    /* Eliminar huésped por ID */
    public void eliminarHuesped(int id) {
        huespedes.remove(id);
    }
    public boolean hasAnfitriones(){
        return !anfitriones.isEmpty();
    }
    public boolean hasHuespedes(){
        return !huespedes.isEmpty();
    }
    public void getUbicaciones() {
        java.util.HashSet<String> ubicaciones = new java.util.HashSet<>();
        for (Anfitrion anfitrion : anfitriones.values()) {
            for (Propiedad propiedad : anfitrion.getPropiedades()) {
                ubicaciones.add(propiedad.getUbicacion());
            }
        }
        System.out.println("Ubicaciones registradas:");
        for (String ubicacion : ubicaciones) {
            System.out.println("- " + ubicacion);
        }
    }
    public ArrayList<Propiedad> buscarPropiedadesPorUbicacion(String ubicacion) {
        ArrayList<Propiedad> resultado = new ArrayList<>();
        for (Anfitrion anfitrion : anfitriones.values()) {
            for (Propiedad propiedad : anfitrion.getPropiedades()) {
                if (propiedad.getUbicacion().equalsIgnoreCase(ubicacion)) {
                    resultado.add(propiedad);
                }
            }
        }
        return resultado;
    }

}

