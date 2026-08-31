import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;
import modelo.Cliente;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ArrayList<Cliente> listaClientes = new ArrayList<>();
        int opcion = 0;

        do {
            System.out.println("BIENVENIDO AL SISTEMA DE VETERINARIA");
            System.out.println("===================================");
            System.out.println("Elige una opción:");
            System.out.println("1 - Registrar un cliente");
            System.out.println("2 - Buscar un cliente");
            System.out.println("3 - Salir");
            System.out.print("\nIngresa una opción: ");

            // Try catch para controlar errores
            try {
                opcion = scanner.nextInt();
                scanner.nextLine(); // Limpiar el buffer

                switch (opcion) {
                    case 1:
                        System.out.println("\n--- REGISTRO DE CLIENTE ---");
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
                        System.out.println("Saliendo del sistema...");
                        break;

                    default:
                        System.out.println("Opción inválida. Elige un número entre 1 y 3.\n");
                }

            } catch (InputMismatchException e) {
                System.out.println("\n[ERROR] Debes ingresar un número válido, no letras.\n");
                scanner.nextLine(); // Limpiar la entrada errónea del scanner para que no entre en bucle
                opcion = 0; // Reiniciamos la opción para que el ciclo continúe
            } catch (Exception e) {
                System.out.println("\n[ERROR INESPERADO]: " + e.getMessage() + "\n");
            }

        } while (opcion != 3);

        scanner.close();
    }
}
