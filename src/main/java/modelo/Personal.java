package modelo;

import java.util.ArrayList;
import util.Validaciones;

public class Personal {

    // Roles permitidos (valores fijos)
    public static final String ROL_VETERINARIO = "Veterinario";
    public static final String ROL_ASISTENTE = "Asistente veterinario";
    public static final String ROL_RECEPCIONISTA = "Recepcionista";

    // Turnos de atención (valores fijos)
    public static final String TURNO_MANANA = "Mañana (8:00 am - 1:00 pm)";
    public static final String TURNO_TARDE = "Tarde (1:00 pm - 6:00 pm)";
    public static final String TURNO_NOCHE = "Noche (6:00 pm - 11:00 pm)";

    // Variable estática para llevar la cuenta global de empleados creados
    private static int contadorId = 1;

    private int idEmpleado;      // Autogenerado: siempre numérico, nunca se ingresa por teclado
    private String dni;
    private String nombre;
    private String horarioAtencion;
    private String rol;          // Veterinario, Asistente veterinario, Recepcionista

    public Personal(String dni, String nombre, String horarioAtencion, String rol) {
        validarDatos(dni, nombre);
        if (!esRolValido(rol)) {
            throw new IllegalArgumentException("El rol debe ser Veterinario, Asistente veterinario o Recepcionista.");
        }

        this.idEmpleado = contadorId++;
        this.dni = dni;
        this.nombre = nombre.trim();
        this.horarioAtencion = horarioAtencion;
        this.rol = rol;
    }

    // Reconstruye empleado ya existente
    private Personal(int idEmpleado, String dni, String nombre, String horarioAtencion, String rol) {
        validarDatos(dni, nombre);
        this.idEmpleado = idEmpleado;
        this.dni = dni;
        this.nombre = nombre.trim();
        this.horarioAtencion = horarioAtencion;
        this.rol = rol;
    }

    // Valida y reconstruye desde archivo
    public static Personal reconstruirDesdeArchivo(int idEmpleado, String dni, String nombre,
            String horarioAtencion, String rol) {
        if (idEmpleado <= 0) {
            throw new IllegalArgumentException("El ID del empleado debe ser un número mayor que 0.");
        }
        if (!esRolValido(rol)) {
            throw new IllegalArgumentException("Rol de empleado inválido en el archivo: " + rol);
        }

        Personal p = new Personal(idEmpleado, dni, nombre, horarioAtencion, rol);
        if (idEmpleado >= contadorId) {
            contadorId = idEmpleado + 1;
        }
        return p;
    }

    // Reutiliza las reglas centralizadas en util.Validaciones
    private static void validarDatos(String dni, String nombre) {
        if (!Validaciones.esDniValido(dni)) {
            throw new IllegalArgumentException("El DNI debe tener exactamente 8 dígitos numéricos.");
        }
        if (!Validaciones.esNombreValido(nombre)) {
            throw new IllegalArgumentException("El nombre debe contener letras y puede incluir espacios, guiones o apóstrofos.");
        }
    }

    public static boolean esRolValido(String rol) {
        return rol != null && (rol.equalsIgnoreCase(ROL_VETERINARIO)
                || rol.equalsIgnoreCase(ROL_ASISTENTE)
                || rol.equalsIgnoreCase(ROL_RECEPCIONISTA));
    }

    // --- Búsqueda ---

    public static Personal buscarPorDni(ArrayList<Personal> listaPersonal, String dni) {
        for (Personal p : listaPersonal) {
            if (p.getDni().equals(dni)) {
                return p;
            }
        }
        return null;
    }

    /** Devuelve los empleados que tienen el rol indicado (ej. para listar solo veterinarios). */
    public static ArrayList<Personal> buscarPorRol(ArrayList<Personal> listaPersonal, String rol) {
        ArrayList<Personal> resultado = new ArrayList<>();
        for (Personal p : listaPersonal) {
            if (p.getRol().equalsIgnoreCase(rol)) {
                resultado.add(p);
            }
        }
        return resultado;
    }

    // --- Getters ---
    public int getIdEmpleado() {
        return idEmpleado;
    }

    public String getDni() {
        return dni;
    }

    public String getNombre() {
        return nombre;
    }

    public String getHorarioAtencion() {
        return horarioAtencion;
    }

    public String getRol() {
        return rol;
    }

    public void mostrarDatos() {
        System.out.println("\n--- DATOS DEL EMPLEADO ---");
        System.out.println("ID Empleado: " + idEmpleado);
        System.out.println("DNI: " + dni);
        System.out.println("Nombre: " + nombre);
        System.out.println("Horario de Atención: " + horarioAtencion);
        System.out.println("Rol: " + rol);
        System.out.println("---------------------------\n");
    }

    public void mostrarDatos(boolean resumen) {
        if (!resumen) {
            mostrarDatos();
            return;
        }
        System.out.println("Empleado #" + idEmpleado + " | DNI: " + dni + " | " + nombre
                + " | Rol: " + rol + " | Turno: " + horarioAtencion);
    }
}
