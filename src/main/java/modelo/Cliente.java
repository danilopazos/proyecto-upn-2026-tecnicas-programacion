package modelo;

import java.util.ArrayList;
import util.Validaciones;

public class Cliente {
    // Variable estática para llevar la cuenta global de clientes creados
    private static int contadorId = 1;
    private int idCliente;
    private String nombre;
    private String dni;
    private String telefono;
    private String email;
    private String direccion;
    private String fechaRegistro;

    public Cliente(String nombreCompleto, String telefono, String email, String direccion, String dni, String fechaRegistro) {
        validarDatos(nombreCompleto, dni, telefono, email, direccion, fechaRegistro);

        this.idCliente = contadorId++; // Asigna el número actual y luego suma 1 para el siguiente
        this.nombre = nombreCompleto.trim();
        this.dni = dni;
        this.telefono = telefono;
        this.email = email;
        this.direccion = direccion;
        this.fechaRegistro = fechaRegistro;
    }

    // Reconstruye cliente ya existente
    private Cliente(int idCliente, String nombreCompleto, String telefono, String email,
            String direccion, String dni, String fechaRegistro) {
        validarDatos(nombreCompleto, dni, telefono, email, direccion, fechaRegistro);
        this.idCliente = idCliente;
        this.nombre = nombreCompleto.trim();
        this.dni = dni;
        this.telefono = telefono;
        this.email = email;
        this.direccion = direccion;
        this.fechaRegistro = fechaRegistro;
    }

    // Valida y reconstruye desde archivo
    public static Cliente reconstruirDesdeArchivo(int idCliente, String nombreCompleto, String telefono,
            String email, String direccion, String dni, String fechaRegistro) {
        if (idCliente <= 0) {
            throw new IllegalArgumentException("El ID del cliente debe ser un número mayor que 0.");
        }
        Cliente c = new Cliente(idCliente, nombreCompleto, telefono, email, direccion, dni, fechaRegistro);
        if (idCliente >= contadorId) {
            contadorId = idCliente + 1; // Evita IDs duplicados
        }
        return c;
    }

    // Reutiliza las reglas centralizadas en util.Validaciones
    private static void validarDatos(String nombre, String dni, String telefono, String email,
            String direccion, String fechaRegistro) {
        if (!Validaciones.esNombreValido(nombre)) {
            throw new IllegalArgumentException("El nombre debe contener letras y puede incluir espacios, guiones o apóstrofos.");
        }
        if (!Validaciones.esDniValido(dni)) {
            throw new IllegalArgumentException("El DNI debe tener exactamente 8 dígitos numéricos.");
        }
        if (!Validaciones.esTelefonoValido(telefono)) {
            throw new IllegalArgumentException("El teléfono debe tener exactamente 9 dígitos numéricos.");
        }
        if (email != null && !Validaciones.esEmailValido(email)) {
            throw new IllegalArgumentException("Ingresa un correo válido (Ej: nombre@dominio.com).");
        }
        if (!Validaciones.esTextoNoVacio(direccion)) {
            throw new IllegalArgumentException("La dirección no puede estar vacía.");
        }
        if (!Validaciones.esFechaValida(fechaRegistro)) {
            throw new IllegalArgumentException("Ingresa una fecha real en formato dd/MM/aaaa.");
        }
    }

    // --- Búsqueda ---

    public static Cliente buscarPorDni(ArrayList<Cliente> listaClientes, String dni) {
        for (Cliente c : listaClientes) {
            if (c.getDni().equals(dni)) {
                return c;
            }
        }
        return null;
    }

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

    public String getNombreCompleto() {
        return nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDni() {
        return dni;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getEmail() {
        return email;
    }

    public String getDireccion() {
        return direccion;
    }

    public String getFechaRegistro() {
        return fechaRegistro;
    }

    public void mostrarDatos() {
        System.out.println("\n--- DATOS DEL CLIENTE ---");
        System.out.println("ID Cliente: " + idCliente);
        System.out.println("Nombre: " + nombre);
        System.out.println("Teléfono: " + telefono);
        System.out.println("Email: " + (email == null ? "No registrado" : email));
        System.out.println("Dirección: " + direccion);
        System.out.println("DNI: " + dni);
        System.out.println("Fecha de Registro: " + fechaRegistro);
        System.out.println("-------------------------\n");
    }

    public void mostrarDatos(boolean resumen) {
        if (!resumen) {
            mostrarDatos();
            return;
        }
        System.out.println("Cliente #" + idCliente + " | " + nombre
                + " | DNI: " + dni + " | Tel: " + telefono);
    }
}
