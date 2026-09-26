package modelo;

import java.util.ArrayList;

public class Cita {
    public static final String ESTADO_PENDIENTE = "Pendiente";
    public static final String ESTADO_CONFIRMADA = "Confirmada";
    public static final String ESTADO_CANCELADA = "Cancelada";
    public static final String ESTADO_COMPLETADA = "Completada";

    private static int contadorId = 1;

    private int idCita;
    private int idMascota;        // Relación con la mascota atendida
    private String fechaHora;
    private String veterinario;   // Veterinario asignado (por ahora, solo el nombre)
    private String tipo;          // Consulta, Control, Cirugía, Vacuna
    private String estado;        // Pendiente, Confirmada, Cancelada, Completada

    public Cita(int idMascota, String fechaHora, String veterinario, String tipo) {
        if (fechaHora == null || fechaHora.trim().isEmpty()) {
            throw new IllegalArgumentException("La fecha y hora de la cita no puede estar vacía.");
        }
        if (veterinario == null || veterinario.trim().isEmpty()) {
            throw new IllegalArgumentException("Debe indicarse un veterinario para la cita.");
        }

        this.idCita = contadorId++;
        this.idMascota = idMascota;
        this.fechaHora = fechaHora;
        this.veterinario = veterinario;
        this.tipo = tipo;
        this.estado = ESTADO_PENDIENTE; // Toda cita nueva nace pendiente
    }

    /**
     * Constructor privado usado exclusivamente para reconstruir una cita ya existente
     * (con estado ya definido) a partir de los datos leídos de un archivo.
     */
    private Cita(int idCita, int idMascota, String fechaHora, String veterinario, String tipo, String estado) {
        this.idCita = idCita;
        this.idMascota = idMascota;
        this.fechaHora = fechaHora;
        this.veterinario = veterinario;
        this.tipo = tipo;
        this.estado = estado;
    }

    /**
     * Reconstruye una Cita a partir de datos leídos de un archivo. Lanza IllegalArgumentException
     * si algún dato es inválido, para que la capa de persistencia descarte la línea sin detener
     * la carga del resto. Uso exclusivo de la capa de persistencia (persistencia.CitaRepositorio).
     */
    public static Cita reconstruirDesdeArchivo(int idCita, int idMascota, String fechaHora, String veterinario,
            String tipo, String estado) {
        if (idCita <= 0) {
            throw new IllegalArgumentException("El ID de la cita debe ser un número mayor que 0.");
        }
        if (fechaHora == null || fechaHora.trim().isEmpty()) {
            throw new IllegalArgumentException("Fecha y hora de cita inválida en el archivo.");
        }
        if (veterinario == null || veterinario.trim().isEmpty()) {
            throw new IllegalArgumentException("Veterinario de cita inválido en el archivo.");
        }
        if (!esEstadoValido(estado)) {
            throw new IllegalArgumentException("Estado de cita inválido en el archivo: " + estado);
        }

        Cita c = new Cita(idCita, idMascota, fechaHora, veterinario, tipo, estado);
        if (idCita >= contadorId) {
            contadorId = idCita + 1;
        }
        return c;
    }

    public static boolean esEstadoValido(String estado) {
        return ESTADO_PENDIENTE.equals(estado) || ESTADO_CONFIRMADA.equals(estado)
                || ESTADO_CANCELADA.equals(estado) || ESTADO_COMPLETADA.equals(estado);
    }

    // --- Transiciones de estado ---

    public void confirmar() {
        if (!estado.equals(ESTADO_PENDIENTE)) {
            throw new IllegalStateException("Solo una cita pendiente puede confirmarse (estado actual: " + estado + ").");
        }
        estado = ESTADO_CONFIRMADA;
    }

    public void cancelar() {
        if (estado.equals(ESTADO_COMPLETADA)) {
            throw new IllegalStateException("Una cita completada no puede cancelarse.");
        }
        estado = ESTADO_CANCELADA;
    }

    public void completar() {
        if (estado.equals(ESTADO_CANCELADA) || estado.equals(ESTADO_COMPLETADA)) {
            throw new IllegalStateException("Solo una cita pendiente o confirmada puede completarse (estado actual: " + estado + ").");
        }
        estado = ESTADO_COMPLETADA;
    }

    // --- Búsqueda ---

    public static Cita buscarPorId(ArrayList<Cita> listaCitas, int idCita) {
        for (Cita c : listaCitas) {
            if (c.getIdCita() == idCita) {
                return c;
            }
        }
        return null;
    }

    // --- Getters ---

    public int getIdCita() {
        return idCita;
    }

    public int getIdMascota() {
        return idMascota;
    }

    public String getFechaHora() {
        return fechaHora;
    }

    public String getVeterinario() {
        return veterinario;
    }

    public String getTipo() {
        return tipo;
    }

    public String getEstado() {
        return estado;
    }

    public void mostrarDatos() {
        System.out.println("\n--- DATOS DE LA CITA ---");
        System.out.println("ID Cita: " + idCita);
        System.out.println("ID Mascota: " + idMascota);
        System.out.println("Fecha y Hora: " + fechaHora);
        System.out.println("Veterinario: " + veterinario);
        System.out.println("Tipo: " + tipo);
        System.out.println("Estado: " + estado);
        System.out.println("------------------------\n");
    }


     //Sobrecarga de mostrarDatos: si resumen es false, muestra el detalle si es true, muestra una sola

    public void mostrarDatos(boolean resumen) {
        if (!resumen) {
            mostrarDatos();
            return;
        }
        System.out.println("Cita #" + idCita + " | Mascota ID: " + idMascota
                + " | " + fechaHora + " | " + tipo + " | Estado: " + estado);
    }
}
