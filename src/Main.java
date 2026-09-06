import java.util.ArrayList;
import java.util.Scanner;
import modelo.Cliente;
import modelo.HistorialClinico;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        ArrayList<Cliente> listaClientes = new ArrayList<>();
        ArrayList<HistorialClinico> listaHistoriales = new ArrayList<>();

        int opcion = 0;

        do {
            System.out.println("===================================");
            System.out.println("BIENVENIDO AL SISTEMA DE VETERINARIA");
            System.out.println("===================================");
            System.out.println("Elige una opción:");
            System.out.println("1 - Registrar un cliente");
            System.out.println("2 - Buscar un cliente");
            System.out.println("3 - Registrar consulta clínica");
            System.out.println("4 - Consultar historial clínico");
            System.out.println("5 - Salir");
            System.out.print("\nIngresa una opción: ");
            
            opcion = scanner.nextInt();
            scanner.nextLine(); // Limpiar el buffer

            switch (opcion) {
                case 1:
                    System.out.println("\n--- REGISTRO DE CLIENTE ---");
                    // Ya no pedimos el ID por consola
                    System.out.print("Nombre completo: ");
                    String nombre = scanner.nextLine();
                    System.out.print("Teléfono: ");
                    String telefono = scanner.nextLine();
                    System.out.print("Email: ");
                    String email = scanner.nextLine();
                    System.out.print("Dirección: ");
                    String direccion = scanner.nextLine();
                    System.out.print("DNI: ");
                    String dni = scanner.nextLine();
                    System.out.print("Fecha de registro (Ej: 30/08/2026): ");
                    String fecha = scanner.nextLine();

                    // Creamos el objeto sin pasarle ID; la clase se encarga de ponerle 1, 2, 3...
                    Cliente nuevoCliente = new Cliente(nombre, telefono, email, direccion, dni, fecha);
                    listaClientes.add(nuevoCliente);
                    
                    System.out.println("¡Cliente registrado con éxito!\n");
                    break;

                case 2:
                    System.out.println("\n--- BUSCAR CLIENTE ---");
                    System.out.print("Ingresa el DNI a buscar: ");
                    String dniBuscar = scanner.nextLine();
                    
                    boolean encontrado = false;
                    for (Cliente c : listaClientes) {
                        if (c.getDni().equalsIgnoreCase(dniBuscar)) {
                            c.mostrarDatos();
                            encontrado = true;
                            break;
                        }
                    }

                    if (!encontrado) {
                        System.out.println("No se encontró ningún cliente con ese DNI.\n");
                    }
                    break;
                
                case 3:
                    System.out.println("\n--- REGISTRO DE CONSULTA CLÍNICA ---");

                    System.out.print("ID de mascota: ");
                    int idMascota = scanner.nextInt();
                    scanner.nextLine();

                    System.out.print("Fecha y hora: ");
                    String fechaHora = scanner.nextLine();

                    System.out.print("Motivo de consulta: ");
                    String motivoConsulta = scanner.nextLine();

                    System.out.print("Veterinario a cargo: ");
                    String veterinario = scanner.nextLine();

                    System.out.print("Diagnóstico: ");
                    String diagnostico = scanner.nextLine();

                    System.out.print("Tratamiento indicado: ");
                    String tratamiento = scanner.nextLine();

                    System.out.print("Peso al momento de la consulta (kg): ");
                    double peso = scanner.nextDouble();
                    scanner.nextLine();

                    System.out.print("Temperatura (°C): ");
                    double temperatura = scanner.nextDouble();
                    scanner.nextLine();

                    System.out.print("Observaciones: ");
                    String observaciones = scanner.nextLine();

                    System.out.print("Próxima cita sugerida: ");
                    String proximaCita = scanner.nextLine();

                    HistorialClinico nuevaConsulta = new HistorialClinico(
                        idMascota,
                        fechaHora,
                        motivoConsulta,
                        veterinario,
                        diagnostico,
                        tratamiento,
                        peso,
                        temperatura,
                        observaciones,
                        proximaCita
                    );

                    listaHistoriales.add(nuevaConsulta);

                    System.out.println("¡Consulta registrada con éxito!\n");
                    break; 
                
                case 4:
                    System.out.println("\n--- CONSULTAR HISTORIAL CLÍNICO ---");

                    System.out.print("Ingresa el ID de mascota: ");
                    int idMascotaBuscar = scanner.nextInt();
                    scanner.nextLine();

                    boolean consultaEncontrada = false;

                    for (HistorialClinico h : listaHistoriales) {
                        if (h.getIdMascota() == idMascotaBuscar) {
                            h.mostrarDatos();
                            consultaEncontrada = true;
                        }
                    }

                    if (!consultaEncontrada) {
                        System.out.println("No se encontraron consultas para esa mascota.\n");
                    }

                    break;

                case 5:
                    System.out.println("Saliendo del sistema...");
                    break;

                default:
                    System.out.println("Opción inválida. Intenta de nuevo.\n");
            }

        } while (opcion != 5);

        scanner.close();
    }
}
