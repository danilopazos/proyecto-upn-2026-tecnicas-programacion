package modelo;

import java.util.ArrayList;

public class Mascota {

    private static int contadorId = 1;

    private int idMascota;
    private int idCliente;
    private String nombre;
    private String especie;
    private String raza;
    private String fechaNacimiento;
    private String sexo;
    private ArrayList<Double> historialPeso;
    private ArrayList<String> fechasHistorialPeso;
    private boolean esterilizado;

    public Mascota(int idCliente, String nombre, String especie, String raza,
            String fechaNacimiento, String sexo, double pesoInicial, boolean esterilizado) {
        if (!esNombreValido(nombre)) {
            throw new IllegalArgumentException("El nombre de la mascota no puede estar vacío y no debe contener números.");
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

    // Reconstruye mascota ya existente
    private Mascota(int idMascota, int idCliente, String nombre, String especie, String raza,
            String fechaNacimiento, String sexo, boolean esterilizado,
            ArrayList<Double> historialPeso, ArrayList<String> fechasHistorialPeso) {
        this.idMascota = idMascota;
        this.idCliente = idCliente;
        this.nombre = nombre;
        this.especie = especie;
        this.raza = raza;
        this.fechaNacimiento = fechaNacimiento;
        this.sexo = sexo;
        this.esterilizado = esterilizado;
        this.historialPeso = historialPeso;
        this.fechasHistorialPeso = fechasHistorialPeso;
    }

    // Valida y reconstruye desde archivo
    public static Mascota reconstruirDesdeArchivo(int idMascota, int idCliente, String nombre, String especie,
            String raza, String fechaNacimiento, String sexo, boolean esterilizado,
            ArrayList<Double> historialPeso, ArrayList<String> fechasHistorialPeso) {
        if (idMascota <= 0) {
            throw new IllegalArgumentException("El ID de la mascota debe ser un número mayor que 0.");
        }
        if (!esNombreValido(nombre)) {
            throw new IllegalArgumentException("Nombre de mascota inválido en el archivo.");
        }
        if (historialPeso == null || historialPeso.isEmpty()) {
            throw new IllegalArgumentException("La mascota debe tener al menos un registro de peso.");
        }
        if (fechasHistorialPeso == null || fechasHistorialPeso.size() != historialPeso.size()) {
            throw new IllegalArgumentException("El historial de peso y sus fechas no coinciden en cantidad.");
        }

        Mascota m = new Mascota(idMascota, idCliente, nombre, especie, raza, fechaNacimiento, sexo,
                esterilizado, historialPeso, fechasHistorialPeso);
        if (idMascota >= contadorId) {
            contadorId = idMascota + 1;
        }
        return m;
    }

    // --- Validaciones ---
    public static boolean esNombreValido(String nombre) {
        return nombre != null && nombre.trim().matches("[\\p{L}\\p{M}]+(?:[ '’-][\\p{L}\\p{M}]+)*");
    }

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

    public String getNombre() {
        return nombre;
    }

    public String getEspecie() {
        return especie;
    }

    public String getRaza() {
        return raza;
    }

    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    public String getSexo() {
        return sexo;
    }

    public boolean isEsterilizado() {
        return esterilizado;
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
