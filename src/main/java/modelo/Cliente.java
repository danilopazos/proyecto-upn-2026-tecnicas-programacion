package modelo;

import java.util.ArrayList;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

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

    public Cliente(String nombreCompleto, String telefono, String email, String direccion, String dni, String fechaRegistro) {
        if (!esNombreValido(nombreCompleto)) {
            throw new IllegalArgumentException("El nombre debe contener letras y puede incluir espacios, guiones o apóstrofos.");
        }
        if (!esDniValido(dni)) {
            throw new IllegalArgumentException("El DNI debe tener exactamente 8 dígitos numéricos.");
        }

        if (!esTelefonoValido(telefono)) {
            throw new IllegalArgumentException("El teléfono debe tener exactamente 9 dígitos numéricos.");
        }
        if (!esEmailValido(email)) {
            throw new IllegalArgumentException("Ingresa un correo válido (Ej: nombre@dominio.com).");
        }
        if (!esDireccionValida(direccion)) {
            throw new IllegalArgumentException("La dirección no puede estar vacía.");
        }
        if (!esFechaRegistroValida(fechaRegistro)) {
            throw new IllegalArgumentException("Ingresa una fecha real en formato dd/MM/aaaa.");
        }

        this.idCliente = contadorId++; // Asigna el número actual y luego suma 1 para el siguiente
        this.nombreCompleto = nombreCompleto;
        this.telefono = telefono;
        this.email = email;
        this.direccion = direccion;
        this.dni = dni;
        this.fechaRegistro = fechaRegistro;
    }

    // Reconstruye cliente ya existente
    private Cliente(int idCliente, String nombreCompleto, String telefono, String email,
            String direccion, String dni, String fechaRegistro) {
        this.idCliente = idCliente;
        this.nombreCompleto = nombreCompleto;
        this.telefono = telefono;
        this.email = email;
        this.direccion = direccion;
        this.dni = dni;
        this.fechaRegistro = fechaRegistro;
    }

    // Valida y reconstruye desde archivo
    public static Cliente reconstruirDesdeArchivo(int idCliente, String nombreCompleto, String telefono,
            String email, String direccion, String dni, String fechaRegistro) {
        if (idCliente <= 0) {
            throw new IllegalArgumentException("El ID del cliente debe ser un número mayor que 0.");
        }
        if (!esNombreValido(nombreCompleto)) {
            throw new IllegalArgumentException("Nombre de cliente inválido en el archivo.");
        }
        if (!esDniValido(dni)) {
            throw new IllegalArgumentException("DNI inválido en el archivo (debe tener 8 dígitos).");
        }
        if (!esTelefonoValido(telefono)) {
            throw new IllegalArgumentException("Teléfono inválido en el archivo (debe tener 9 dígitos).");
        }
        if (!esEmailValido(email)) {
            throw new IllegalArgumentException("Email inválido en el archivo.");
        }
        if (!esDireccionValida(direccion)) {
            throw new IllegalArgumentException("Dirección inválida en el archivo.");
        }
        if (!esFechaRegistroValida(fechaRegistro)) {
            throw new IllegalArgumentException("Fecha de registro inválida en el archivo.");
        }

        Cliente c = new Cliente(idCliente, nombreCompleto, telefono, email, direccion, dni, fechaRegistro);
        if (idCliente >= contadorId) {
            contadorId = idCliente + 1; // Evita IDs duplicados
        }
        return c;
    }

    // --- Validaciones ---
    /**
     * Acepta letras Unicode (incluye tildes y ñ), espacios, guiones y apóstrofos.
     */
    public static boolean esNombreValido(String nombre) {
        return nombre != null && nombre.trim().matches("[\\p{L}\\p{M}]+(?:[ '’-][\\p{L}\\p{M}]+)*");
    }

    /**
     * Un DNI es válido si tiene exactamente 8 dígitos numéricos.
     */
    public static boolean esDniValido(String dni) {
        return dni != null && dni.matches("\\d{8}");
    }

    public static boolean esTelefonoValido(String telefono) {
        return telefono != null && telefono.matches("\\d{9}");
    }

    /** Valida el formato habitual de correo: usuario@dominio.extensión. */
    public static boolean esEmailValido(String email) {
        return email != null
                && email.matches("[A-Za-z0-9_+%'-]+(?:[.][A-Za-z0-9_+%'-]+)*@[A-Za-z0-9]+(?:-[A-Za-z0-9]+)*(?:[.][A-Za-z0-9]+(?:-[A-Za-z0-9]+)*)*[.][A-Za-z]{2,}");
    }

    public static boolean esDireccionValida(String direccion) {
        return direccion != null && !direccion.trim().isEmpty();
    }

    /** Rechaza fechas imposibles, incluso días inválidos en años no bisiestos. */
    public static boolean esFechaRegistroValida(String fecha) {
        if (fecha == null || !fecha.matches("[0-9]{2}/[0-9]{2}/[0-9]{4}")) {
            return false;
        }
        try {
            LocalDate fechaRegistro = LocalDate.parse(fecha,
                    DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT));
            return fechaRegistro.getYear() > 0;
        } catch (DateTimeParseException e) {
            return false;
        }
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

    public String getNombreCompleto() {
        return nombreCompleto;
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

    public String getDni() {
        return dni;
    }

    public String getFechaRegistro() {
        return fechaRegistro;
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

    public void mostrarDatos(boolean resumen) {
        if (!resumen) {
            mostrarDatos();
            return;
        }
        System.out.println("Cliente #" + idCliente + " | " + nombreCompleto
                + " | DNI: " + dni + " | Tel: " + telefono);
    }
}
