package modelo;

import java.util.ArrayList;

public class Mascota {
    // Variable estática para llevar la cuenta global de mascotas creadas
    private static int contadorId = 1;

    private int idMascota;      // Autogenerado: siempre numérico, nunca se ingresa por teclado
    private int idCliente;      // Relación con el dueño (Cliente)
    private String nombre;
    private String especie;     // Perro, gato, conejo, etc.
    private String raza;
    private String fechaNacimiento;
    private String sexo;
    private ArrayList<Double> historialPeso; // Peso histórico: se agrega un valor nuevo en cada control
    private boolean esterilizado;

    public Mascota(int idCliente, String nombre, String especie, String raza,
                    String fechaNacimiento, String sexo, double pesoInicial, boolean esterilizado) {
        if (!esNombreValido(nombre)) {
            throw new IllegalArgumentException("El nombre de la mascota no puede estar vacío.");
        }

        this.idMascota = contadorId++;
        this.idCliente = idCliente;
        this.nombre = nombre;
        this.especie = especie;
        this.raza = raza;
        this.fechaNacimiento = fechaNacimiento;
        this.sexo = sexo;
        this.historialPeso = new ArrayList<>();
        this.historialPeso.add(pesoInicial);
        this.esterilizado = esterilizado;
    }

    // --- Validaciones ---

    /**
     * Un nombre es válido si no es null y no está vacío (ni compuesto solo de espacios).
     */
    public static boolean esNombreValido(String nombre) {
        return nombre != null && !nombre.trim().isEmpty();
    }

    /**
     * Verifica que un texto ingresado (ej. el ID de un cliente) contenga solo dígitos numéricos.
     */
    public static boolean esIdNumerico(String texto) {
        return texto != null && texto.matches("\\d+");
    }

    // --- Peso histórico ---

    /**
     * Registra un nuevo peso (ej. luego de un control veterinario) sin perder el historial anterior.
     */
    public void registrarControlPeso(double nuevoPeso) {
        historialPeso.add(nuevoPeso);
    }

    public double getPesoActual() {
        return historialPeso.get(historialPeso.size() - 1);
    }

    public ArrayList<Double> getHistorialPeso() {
        return historialPeso;
    }

    // --- Búsqueda ---

    public static Mascota buscarPorId(ArrayList<Mascota> listaMascotas, int idMascota) {
        for (Mascota m : listaMascotas) {
            if (m.getIdMascota() == idMascota) {
                return m;
            }
        }
        return null;
    }

    // --- Getters ---

    public int getIdMascota() {
        return idMascota;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void mostrarDatos() {
        System.out.println("\n--- DATOS DE LA MASCOTA ---");
        System.out.println("ID Mascota: " + idMascota);
        System.out.println("ID Cliente (dueño): " + idCliente);
        System.out.println("Nombre: " + nombre);
        System.out.println("Especie: " + especie);
        System.out.println("Raza: " + raza);
        System.out.println("Fecha de Nacimiento: " + fechaNacimiento);
        System.out.println("Sexo: " + sexo);
        System.out.println("Peso actual: " + getPesoActual() + " kg");
        System.out.println("Historial de peso: " + historialPeso);
        System.out.println("Esterilizado: " + (esterilizado ? "Sí" : "No"));
        System.out.println("---------------------------\n");
    }
}
