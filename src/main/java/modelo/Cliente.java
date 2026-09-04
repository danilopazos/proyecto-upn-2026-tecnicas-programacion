package modelo;

import java.util.ArrayList;

public class Cliente {
    // Variable estática para llevar la cuenta global de clientes creados
    private static int contadorId = 1;

    private int idCliente; // Cambiado a int para que sea numérico
    private String nombreCompleto;
    private String telefono;
    private String email;
    private String direccion;
    private String dni;
    private String fechaRegistro;

    // Constructor (ya no recibe el ID por parámetro, se autoasigna)
    public Cliente(String nombreCompleto, String telefono, String email, String direccion, String dni, String fechaRegistro) {
        if (!esNombreValido(nombreCompleto)) {
            throw new IllegalArgumentException("El nombre no puede estar vacío.");
        }
        if (!esDniValido(dni)) {
            throw new IllegalArgumentException("El DNI debe tener exactamente 8 dígitos numéricos.");
        }

        this.idCliente = contadorId++; // Asigna el número actual y luego suma 1 para el siguiente
        this.nombreCompleto = nombreCompleto;
        this.telefono = telefono;
        this.email = email;
        this.direccion = direccion;
        this.dni = dni;
        this.fechaRegistro = fechaRegistro;
    }

    // --- Validaciones ---

    /**
     * Un nombre es válido si no es null y no está vacío (ni compuesto solo de espacios).
     */
    public static boolean esNombreValido(String nombre) {
        return nombre != null && !nombre.trim().isEmpty();
    }

    /**
     * Un DNI es válido si tiene exactamente 8 caracteres y todos son dígitos numéricos.
     */
    public static boolean esDniValido(String dni) {
        return dni != null && dni.matches("\\d{8}");
    }

    /**
     * Verifica si ya existe un cliente registrado con el DNI indicado.
     */
    public static boolean existeDni(ArrayList<Cliente> listaClientes, String dni) {
        for (Cliente c : listaClientes) {
            if (c.getDni().equalsIgnoreCase(dni)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Verifica si existe un cliente registrado con el ID indicado.
     */
    public static boolean existeId(ArrayList<Cliente> listaClientes, int idCliente) {
        for (Cliente c : listaClientes) {
            if (c.getIdCliente() == idCliente) {
                return true;
            }
        }
        return false;
    }

    // --- Getters ---

    public int getIdCliente() {
        return idCliente;
    }

    public String getDni() {
        return dni;
    }

    public void mostrarDatos() {
        System.out.println("\n--- DATOS DEL CLIENTE ---");
        System.out.println("ID Cliente: " + idCliente);
        System.out.println("Nombre: " + nombreCompleto);
        System.out.println("Teléfono: " + telefono);
        System.out.println("Email: " + email);
        System.out.println("Dirección: " + direccion);
        System.out.println("DNI: " + dni);
        System.out.println("Fecha de Registro: " + fechaRegistro);
        System.out.println("-------------------------\n");
    }
}
