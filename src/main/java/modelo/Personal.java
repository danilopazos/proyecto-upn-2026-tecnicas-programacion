package modelo;

import java.util.ArrayList;

public class Personal {

    // Roles permitidos(valores fijos)
    public static final String ROL_VETERINARIO = "Veterinario";
    public static final String ROL_ASISTENTE = "Asistente veterinario";
    public static final String ROL_RECEPCIONISTA = "Recepcionista";

    // Variable estática para llevar la cuenta global de empleados creados
    private static int contadorId = 1;

    private int idEmpleado;      // Autogenerado: siempre numérico, nunca se ingresa por teclado
    private String nombre;
    private String horarioAtencion;
    private String rol;          // Veterinario, Asistente veterinario, Recepcionista

    public Personal(String nombre, String horarioAtencion, String rol) {
        if (!esNombreValido(nombre)) {
            throw new IllegalArgumentException("El nombre no puede estar vacío.");
        }
        if (!esRolValido(rol)) {
            throw new IllegalArgumentException("El rol debe ser Veterinario, Asistente veterinario o Recepcionista.");
        }

        this.idEmpleado = contadorId++;
        this.nombre = nombre;
        this.horarioAtencion = horarioAtencion;
        this.rol = rol;
    }
    // --- Validaciones ---

    /**
     * Un nombre es válido si no es null y no está vacío (ni compuesto solo de espacios).
     */
    public static boolean esNombreValido(String nombre) {
        return nombre != null && !nombre.trim().isEmpty();
    }

    /**
     * Un rol es válido si coincide (sin distinguir mayúsculas/minúsculas) con uno de los roles permitidos.
     */
    public static boolean esRolValido(String rol) {
        return rol != null && (rol.equalsIgnoreCase(ROL_VETERINARIO)
                || rol.equalsIgnoreCase(ROL_ASISTENTE)
                || rol.equalsIgnoreCase(ROL_RECEPCIONISTA));
    }

    /**
     * Verifica que un texto ingresado (ej. el ID de un empleado) contenga solo dígitos numéricos.
     */
    public static boolean esIdNumerico(String texto) {
        return texto != null && texto.matches("\\d+");
    }

    // --- Búsqueda ---
    public static Personal buscarPorId(ArrayList<Personal> listaPersonal, int idEmpleado) {
        for (Personal p : listaPersonal) {
            if (p.getIdEmpleado() == idEmpleado) {
                return p;
            }
        }
        return null;
    }

    /**
     * Devuelve la lista de empleados que tienen el rol indicado (ej. para listar solo veterinarios).
     */
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
        System.out.println("Empleado #" + idEmpleado + " | " + nombre + " | Rol: " + rol);
    }

}
