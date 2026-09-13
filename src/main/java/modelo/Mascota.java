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
    private ArrayList<String> fechasHistorialPeso; // Fecha asociada a cada peso (paralelo a historialPeso)
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
        this.fechasHistorialPeso = new ArrayList<>();
        this.fechasHistorialPeso.add("Registro inicial");
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
    public void registrarControlPeso(double nuevoPeso) {
        registrarControlPeso(nuevoPeso, null);
    }

    public void registrarControlPeso(double nuevoPeso, String fecha) {
        historialPeso.add(nuevoPeso);
        fechasHistorialPeso.add(
                (fecha == null || fecha.trim().isEmpty()) ? "Sin fecha registrada" : fecha
        );
    }

    public double getPesoActual() {
        return historialPeso.get(historialPeso.size() - 1);
    }

    public ArrayList<Double> getHistorialPeso() {
        return historialPeso;
    }

    public ArrayList<String> getFechasHistorialPeso() {
        return fechasHistorialPeso;
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
        System.out.println("Historial de peso:");
        for (int i = 0; i < historialPeso.size(); i++) {
            String fecha = i < fechasHistorialPeso.size() ? fechasHistorialPeso.get(i) : "Sin fecha registrada";
            System.out.println("  - " + historialPeso.get(i) + " kg (" + fecha + ")");
        }
        System.out.println("Esterilizado: " + (esterilizado ? "Sí" : "No"));
        System.out.println("---------------------------\n");
    }

    public void mostrarDatos(boolean resumen) {
        if (!resumen) {
            mostrarDatos();
            return;
        }
        System.out.println("Mascota #" + idMascota + " | " + nombre + " (" + especie
                + ") | Dueño ID: " + idCliente + " | Peso actual: " + getPesoActual() + " kg");
    }
}
