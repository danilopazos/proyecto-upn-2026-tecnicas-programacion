package modelo;

public class Consulta {
    private static int contadorId = 1;

    private int idConsulta;
    private int idMascota;
    private int idCita;                  // Cita que originó esta consulta
    private String fechaHora;
    private String motivoConsulta;
    private String veterinario;
    private String diagnostico;
    private String tratamientoIndicado;
    private double peso;                 // Peso al momento de la consulta
    private double temperatura;
    private String observaciones;
    private String proximaCitaSugerida;

    public Consulta(int idMascota, int idCita, String fechaHora, String motivoConsulta, String veterinario,
                     String diagnostico, String tratamientoIndicado, double peso, double temperatura,
                     String observaciones, String proximaCitaSugerida) {
        if (diagnostico == null || diagnostico.trim().isEmpty()) {
            throw new IllegalArgumentException("El diagnóstico no puede estar vacío.");
        }

        this.idConsulta = contadorId++;
        this.idMascota = idMascota;
        this.idCita = idCita;
        this.fechaHora = fechaHora;
        this.motivoConsulta = motivoConsulta;
        this.veterinario = veterinario;
        this.diagnostico = diagnostico;
        this.tratamientoIndicado = tratamientoIndicado;
        this.peso = peso;
        this.temperatura = temperatura;
        this.observaciones = observaciones;
        this.proximaCitaSugerida = proximaCitaSugerida;
    }

    // --- Getters ---

    public int getIdConsulta() {
        return idConsulta;
    }

    public int getIdMascota() {
        return idMascota;
    }

    public double getPeso() {
        return peso;
    }

    public void mostrarDatos() {
        System.out.println("\n--- CONSULTA / HISTORIAL CLÍNICO ---");
        System.out.println("ID Consulta: " + idConsulta);
        System.out.println("ID Mascota: " + idMascota);
        System.out.println("ID Cita: " + idCita);
        System.out.println("Fecha y Hora: " + fechaHora);
        System.out.println("Motivo de consulta: " + motivoConsulta);
        System.out.println("Veterinario a cargo: " + veterinario);
        System.out.println("Diagnóstico: " + diagnostico);
        System.out.println("Tratamiento indicado: " + tratamientoIndicado);
        System.out.println("Peso: " + peso + " kg");
        System.out.println("Temperatura: " + temperatura + " °C");
        System.out.println("Observaciones: " + observaciones);
        System.out.println("Próxima cita sugerida: " + proximaCitaSugerida);
        System.out.println("------------------------------------\n");
    }
}
