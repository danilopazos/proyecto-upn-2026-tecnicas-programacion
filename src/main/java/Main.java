import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;
import modelo.Cita;
import modelo.Cliente;
import modelo.Consulta;
import modelo.Mascota;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ArrayList<Cliente> listaClientes = new ArrayList<>();
        ArrayList<Mascota> listaMascotas = new ArrayList<>();
        ArrayList<Cita> listaCitas = new ArrayList<>();
        ArrayList<Consulta> listaConsultas = new ArrayList<>();
        int opcion = 0;

        do {
            System.out.println("BIENVENIDO AL SISTEMA DE VETERINARIA");
            System.out.println("===================================");
            System.out.println("Elige una opción:");
            System.out.println("1 - Registrar un cliente");
            System.out.println("2 - Buscar un cliente");
            System.out.println("3 - Registrar una mascota");
            System.out.println("4 - Buscar una mascota");
            System.out.println("5 - Registrar una cita");
            System.out.println("6 - Gestionar una cita (confirmar/cancelar/completar)");
            System.out.println("7 - Ver historial clínico de una mascota");
            System.out.println("8 - Salir");
            System.out.print("\nIngresa una opción: ");

            // Try catch para controlar errores
            try {
                opcion = scanner.nextInt();
                scanner.nextLine(); // Limpiar el buffer

                switch (opcion) {
                    case 1: {
                        System.out.println("\n--- REGISTRO DE CLIENTE ---");

                        String nombre;
                        do {
                            System.out.print("Nombre completo: ");
                            nombre = scanner.nextLine();
                            if (!Cliente.esNombreValido(nombre)) {
                                System.out.println("[ERROR] El nombre no puede estar vacío.");
                            }
                        } while (!Cliente.esNombreValido(nombre));

                        System.out.print("Teléfono: ");
                        String telefono = scanner.nextLine();
                        System.out.print("Email: ");
                        String email = scanner.nextLine();
                        System.out.print("Dirección: ");
                        String direccion = scanner.nextLine();

                        String dni;
                        do {
                            System.out.print("DNI (8 dígitos numéricos): ");
                            dni = scanner.nextLine();
                            if (!Cliente.esDniValido(dni)) {
                                System.out.println("[ERROR] El DNI debe tener exactamente 8 dígitos numéricos.");
                            } else if (Cliente.existeDni(listaClientes, dni)) {
                                System.out.println("[ERROR] Ya figura un cliente registrado con el DNI " + dni + ".");
                            }
                        } while (!Cliente.esDniValido(dni) || Cliente.existeDni(listaClientes, dni));

                        System.out.print("Fecha de registro (Ej: 30/08/2026): ");
                        String fecha = scanner.nextLine();

                        Cliente nuevoCliente = new Cliente(nombre, telefono, email, direccion, dni, fecha);
                        listaClientes.add(nuevoCliente);

                        System.out.println("¡Cliente registrado con éxito! (ID asignado: " + nuevoCliente.getIdCliente() + ")\n");
                        break;
                    }

                    case 2: {
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
                    }

                    case 3: {
                        System.out.println("\n--- REGISTRO DE MASCOTA ---");

                        if (listaClientes.isEmpty()) {
                            System.out.println("[ERROR] No hay clientes registrados. Registra un cliente antes de registrar una mascota.\n");
                            break;
                        }

                        int idClienteMascota = -1;
                        boolean idClienteValido = false;
                        do {
                            System.out.print("ID del cliente (dueño): ");
                            String idClienteTexto = scanner.nextLine();
                            if (!Mascota.esIdNumerico(idClienteTexto)) {
                                System.out.println("[ERROR] El ID del cliente debe contener solo números.");
                                continue;
                            }
                            idClienteMascota = Integer.parseInt(idClienteTexto);
                            idClienteValido = Cliente.existeId(listaClientes, idClienteMascota);
                            if (!idClienteValido) {
                                System.out.println("[ERROR] No existe ningún cliente con ese ID.");
                            }
                        } while (!idClienteValido);

                        String nombreMascota;
                        do {
                            System.out.print("Nombre de la mascota: ");
                            nombreMascota = scanner.nextLine();
                            if (!Mascota.esNombreValido(nombreMascota)) {
                                System.out.println("[ERROR] El nombre no puede estar vacío.");
                            }
                        } while (!Mascota.esNombreValido(nombreMascota));

                        System.out.print("Especie (perro, gato, conejo, etc.): ");
                        String especie = scanner.nextLine();
                        System.out.print("Raza: ");
                        String raza = scanner.nextLine();
                        System.out.print("Fecha de nacimiento (Ej: 30/08/2026): ");
                        String fechaNacimiento = scanner.nextLine();
                        System.out.print("Sexo (M/H): ");
                        String sexo = scanner.nextLine();

                        double peso = -1;
                        boolean pesoValido = false;
                        do {
                            System.out.print("Peso (kg): ");
                            String pesoTexto = scanner.nextLine();
                            try {
                                peso = Double.parseDouble(pesoTexto);
                                pesoValido = peso > 0;
                                if (!pesoValido) {
                                    System.out.println("[ERROR] El peso debe ser un número mayor que 0.");
                                }
                            } catch (NumberFormatException e) {
                                System.out.println("[ERROR] Ingresa un peso válido (ej: 4.5).");
                            }
                        } while (!pesoValido);

                        System.out.print("¿Esterilizado? (S/N): ");
                        String esterilizadoTexto = scanner.nextLine();
                        boolean esterilizado = esterilizadoTexto.equalsIgnoreCase("S");

                        Mascota nuevaMascota = new Mascota(idClienteMascota, nombreMascota, especie, raza,
                                fechaNacimiento, sexo, peso, esterilizado);
                        listaMascotas.add(nuevaMascota);

                        System.out.println("¡Mascota registrada con éxito! (ID asignado: " + nuevaMascota.getIdMascota() + ")\n");
                        break;
                    }

                    case 4: {
                        System.out.println("\n--- BUSCAR MASCOTA ---");
                        System.out.print("Ingresa el ID de la mascota a buscar: ");
                        String idMascotaTexto = scanner.nextLine();

                        if (!Mascota.esIdNumerico(idMascotaTexto)) {
                            System.out.println("[ERROR] El ID debe contener solo números.\n");
                            break;
                        }

                        Mascota mascotaBuscada = Mascota.buscarPorId(listaMascotas, Integer.parseInt(idMascotaTexto));
                        if (mascotaBuscada != null) {
                            mascotaBuscada.mostrarDatos();
                        } else {
                            System.out.println("No se encontró ninguna mascota con ese ID.\n");
                        }
                        break;
                    }

                    case 5: {
                        System.out.println("\n--- REGISTRAR CITA ---");

                        if (listaMascotas.isEmpty()) {
                            System.out.println("[ERROR] No hay mascotas registradas. Registra una mascota antes de agendar una cita.\n");
                            break;
                        }

                        int idMascotaCita = -1;
                        boolean idMascotaValida = false;
                        do {
                            System.out.print("ID de la mascota: ");
                            String idMascotaTexto = scanner.nextLine();
                            if (!Mascota.esIdNumerico(idMascotaTexto)) {
                                System.out.println("[ERROR] El ID de la mascota debe contener solo números.");
                                continue;
                            }
                            idMascotaCita = Integer.parseInt(idMascotaTexto);
                            idMascotaValida = Mascota.buscarPorId(listaMascotas, idMascotaCita) != null;
                            if (!idMascotaValida) {
                                System.out.println("[ERROR] No existe ninguna mascota con ese ID.");
                            }
                        } while (!idMascotaValida);

                        System.out.print("Fecha y hora (Ej: 15/09/2026 10:00 am): ");
                        String fechaHoraCita = scanner.nextLine();

                        String veterinarioCita;
                        do {
                            System.out.print("Veterinario asignado: ");
                            veterinarioCita = scanner.nextLine();
                            if (veterinarioCita == null || veterinarioCita.trim().isEmpty()) {
                                System.out.println("[ERROR] Debes indicar un veterinario.");
                            }
                        } while (veterinarioCita.trim().isEmpty());

                        String tipoCita = null;
                        while (tipoCita == null) {
                            System.out.println("Tipo de cita: 1-Consulta  2-Control  3-Cirugía  4-Vacuna");
                            System.out.print("Elige una opción: ");
                            String tipoOpcion = scanner.nextLine();
                            switch (tipoOpcion) {
                                case "1": tipoCita = "Consulta"; break;
                                case "2": tipoCita = "Control"; break;
                                case "3": tipoCita = "Cirugía"; break;
                                case "4": tipoCita = "Vacuna"; break;
                                default: System.out.println("[ERROR] Opción inválida.");
                            }
                        }

                        Cita nuevaCita = new Cita(idMascotaCita, fechaHoraCita, veterinarioCita, tipoCita);
                        listaCitas.add(nuevaCita);

                        System.out.println("¡Cita registrada con éxito! (ID asignado: " + nuevaCita.getIdCita()
                                + ", estado: " + nuevaCita.getEstado() + ")\n");
                        break;
                    }

                    case 6: {
                        System.out.println("\n--- GESTIONAR CITA ---");

                        if (listaCitas.isEmpty()) {
                            System.out.println("[ERROR] No hay citas registradas.\n");
                            break;
                        }

                        Cita citaGestion = null;
                        do {
                            System.out.print("ID de la cita: ");
                            String idCitaTexto = scanner.nextLine();
                            if (!Mascota.esIdNumerico(idCitaTexto)) {
                                System.out.println("[ERROR] El ID debe contener solo números.");
                                continue;
                            }
                            citaGestion = Cita.buscarPorId(listaCitas, Integer.parseInt(idCitaTexto));
                            if (citaGestion == null) {
                                System.out.println("[ERROR] No existe ninguna cita con ese ID.");
                            }
                        } while (citaGestion == null);

                        citaGestion.mostrarDatos();

                        System.out.println("¿Qué deseas hacer con esta cita?");
                        System.out.println("1 - Confirmar");
                        System.out.println("2 - Cancelar");
                        System.out.println("3 - Completar (registra la consulta / historial clínico)");
                        System.out.print("Elige una opción: ");
                        String accionCita = scanner.nextLine();

                        try {
                            switch (accionCita) {
                                case "1":
                                    citaGestion.confirmar();
                                    System.out.println("Cita confirmada.\n");
                                    break;

                                case "2":
                                    citaGestion.cancelar();
                                    System.out.println("Cita cancelada.\n");
                                    break;

                                case "3": {
                                    if (citaGestion.getEstado().equals(Cita.ESTADO_CANCELADA)
                                            || citaGestion.getEstado().equals(Cita.ESTADO_COMPLETADA)) {
                                        System.out.println("[ERROR] Solo puedes completar una cita pendiente o confirmada. Estado actual: "
                                                + citaGestion.getEstado() + "\n");
                                        break;
                                    }

                                    System.out.println("\n--- REGISTRO DE CONSULTA (HISTORIAL CLÍNICO) ---");
                                    System.out.print("Motivo de la consulta: ");
                                    String motivo = scanner.nextLine();

                                    String diagnostico;
                                    do {
                                        System.out.print("Diagnóstico: ");
                                        diagnostico = scanner.nextLine();
                                        if (diagnostico.trim().isEmpty()) {
                                            System.out.println("[ERROR] El diagnóstico no puede estar vacío.");
                                        }
                                    } while (diagnostico.trim().isEmpty());

                                    System.out.print("Tratamiento indicado: ");
                                    String tratamiento = scanner.nextLine();

                                    double pesoConsulta = -1;
                                    boolean pesoConsultaValido = false;
                                    do {
                                        System.out.print("Peso (kg): ");
                                        String pesoTexto = scanner.nextLine();
                                        try {
                                            pesoConsulta = Double.parseDouble(pesoTexto);
                                            pesoConsultaValido = pesoConsulta > 0;
                                            if (!pesoConsultaValido) {
                                                System.out.println("[ERROR] El peso debe ser un número mayor que 0.");
                                            }
                                        } catch (NumberFormatException e) {
                                            System.out.println("[ERROR] Ingresa un peso válido (ej: 4.5).");
                                        }
                                    } while (!pesoConsultaValido);

                                    double temperatura = -1;
                                    boolean temperaturaValida = false;
                                    do {
                                        System.out.print("Temperatura (°C): ");
                                        String tempTexto = scanner.nextLine();
                                        try {
                                            temperatura = Double.parseDouble(tempTexto);
                                            temperaturaValida = temperatura > 0;
                                            if (!temperaturaValida) {
                                                System.out.println("[ERROR] La temperatura debe ser un número mayor que 0.");
                                            }
                                        } catch (NumberFormatException e) {
                                            System.out.println("[ERROR] Ingresa una temperatura válida (ej: 38.5).");
                                        }
                                    } while (!temperaturaValida);

                                    System.out.print("Observaciones: ");
                                    String observaciones = scanner.nextLine();
                                    System.out.print("Próxima cita sugerida (Ej: 15/10/2026, o 'Ninguna'): ");
                                    String proximaCita = scanner.nextLine();

                                    Consulta nuevaConsulta = new Consulta(
                                            citaGestion.getIdMascota(),
                                            citaGestion.getIdCita(),
                                            citaGestion.getFechaHora(),
                                            motivo,
                                            citaGestion.getVeterinario(),
                                            diagnostico,
                                            tratamiento,
                                            pesoConsulta,
                                            temperatura,
                                            observaciones,
                                            proximaCita);
                                    listaConsultas.add(nuevaConsulta);

                                    // El peso registrado en la consulta pasa a formar parte
                                    // del historial de peso de la mascota.
                                    Mascota mascotaAtendida = Mascota.buscarPorId(listaMascotas, citaGestion.getIdMascota());
                                    if (mascotaAtendida != null) {
                                        mascotaAtendida.registrarControlPeso(pesoConsulta);
                                    }

                                    citaGestion.completar();

                                    System.out.println("¡Cita completada y consulta registrada! (ID consulta: "
                                            + nuevaConsulta.getIdConsulta() + ")\n");
                                    break;
                                }

                                default:
                                    System.out.println("[ERROR] Opción inválida.\n");
                            }
                        } catch (IllegalStateException e) {
                            System.out.println("[ERROR] " + e.getMessage() + "\n");
                        }
                        break;
                    }

                    case 7: {
                        System.out.println("\n--- HISTORIAL CLÍNICO DE UNA MASCOTA ---");
                        System.out.print("Ingresa el ID de la mascota: ");
                        String idMascotaHistTexto = scanner.nextLine();

                        if (!Mascota.esIdNumerico(idMascotaHistTexto)) {
                            System.out.println("[ERROR] El ID debe contener solo números.\n");
                            break;
                        }

                        int idMascotaHist = Integer.parseInt(idMascotaHistTexto);
                        boolean tieneConsultas = false;
                        for (Consulta c : listaConsultas) {
                            if (c.getIdMascota() == idMascotaHist) {
                                c.mostrarDatos();
                                tieneConsultas = true;
                            }
                        }

                        if (!tieneConsultas) {
                            System.out.println("Esta mascota no tiene consultas registradas en su historial.\n");
                        }
                        break;
                    }

                    case 8:
                        System.out.println("Saliendo del sistema...");
                        break;

                    default:
                        System.out.println("Opción inválida. Elige un número entre 1 y 8.\n");
                }

            } catch (InputMismatchException e) {
                System.out.println("\n[ERROR] Debes ingresar un número válido, no letras.\n");
                scanner.nextLine(); // Limpiar la entrada errónea del scanner para que no entre en bucle
                opcion = 0; // Reiniciamos la opción para que el ciclo continúe
            } catch (Exception e) {
                System.out.println("\n[ERROR INESPERADO]: " + e.getMessage() + "\n");
            }

        } while (opcion != 8);

        scanner.close();
    }
}
