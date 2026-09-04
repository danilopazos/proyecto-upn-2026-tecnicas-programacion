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
}
