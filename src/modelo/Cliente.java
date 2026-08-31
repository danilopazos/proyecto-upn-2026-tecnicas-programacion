package modelo;

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
        this.idCliente = contadorId++; // Asigna el número actual y luego suma 1 para el siguiente
        this.nombreCompleto = nombreCompleto;
        this.telefono = telefono;
        this.email = email;
        this.direccion = direccion;
        this.dni = dni;
        this.fechaRegistro = fechaRegistro;
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
