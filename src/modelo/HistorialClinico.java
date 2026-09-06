package modelo;

public class HistorialClinico {

    private static int contadorId = 1;

    private int idConsulta;
    private int idMascota;
    private String fechaHora;
    private String motivoConsulta;
    private String veterinario;
    private String diagnostico;
    private String tratamiento;
    private double peso;
    private double temperatura;
    private String observaciones;
    private String proximaCita;

    public HistorialClinico(int idMascota, String fechaHora, String motivoConsulta,
                            String veterinario, String diagnostico, String tratamiento,
                            double peso, double temperatura, String observaciones,
                            String proximaCita) {

        this.idConsulta = contadorId++;
        this.idMascota = idMascota;
        this.fechaHora = fechaHora;
        this.motivoConsulta = motivoConsulta;
        this.veterinario = veterinario;
        this.diagnostico = diagnostico;
        this.tratamiento = tratamiento;
        this.peso = peso;
        this.temperatura = temperatura;
        this.observaciones = observaciones;
        this.proximaCita = proximaCita;
    }

    public int getIdMascota() {
        return idMascota;
    }

    public void mostrarDatos() {
        System.out.println("\n--- HISTORIAL CLÍNICO ---");
        System.out.println("ID Consulta: " + idConsulta);
        System.out.println("ID Mascota: " + idMascota);
        System.out.println("Fecha y hora: " + fechaHora);
        System.out.println("Motivo de consulta: " + motivoConsulta);
        System.out.println("Veterinario: " + veterinario);
        System.out.println("Diagnóstico: " + diagnostico);
        System.out.println("Tratamiento: " + tratamiento);
        System.out.println("Peso: " + peso + " kg");
        System.out.println("Temperatura: " + temperatura + " °C");
        System.out.println("Observaciones: " + observaciones);
        System.out.println("Próxima cita: " + proximaCita);
        System.out.println("-------------------------\n");
    }
}