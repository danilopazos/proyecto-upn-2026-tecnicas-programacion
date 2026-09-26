import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;
import modelo.Cita;
import modelo.Cliente;
import modelo.Consulta;
import modelo.Facturacion;
import modelo.Mascota;
import modelo.Personal;
import persistencia.ArchivoDatosException;
import persistencia.CitaRepositorio;
import persistencia.ClienteRepositorio;
import persistencia.ConsultaRepositorio;
import persistencia.FacturacionRepositorio;
import persistencia.MascotaRepositorio;
import persistencia.PersonalRepositorio;

public class Main {

    // Rutas de los archivos de datos del sistema (carpeta "data" dentro del proyecto).
    private static final String RUTA_CLIENTES = "data/clientes.csv";
    private static final String RUTA_MASCOTAS = "data/mascotas.csv";
    private static final String RUTA_CITAS = "data/citas.csv";
    private static final String RUTA_CONSULTAS = "data/consultas.csv";
    private static final String RUTA_PERSONAL = "data/personal.csv";
    private static final String RUTA_FACTURAS = "data/facturas.csv";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ArrayList<Cliente> listaClientes = new ArrayList<>();
        ArrayList<Mascota> listaMascotas = new ArrayList<>();
        ArrayList<Cita> listaCitas = new ArrayList<>();
        ArrayList<Consulta> listaConsultas = new ArrayList<>();
        ArrayList<Personal> listaPersonal = new ArrayList<>();
        ArrayList<Facturacion> listaFacturas = new ArrayList<>();
        int opcion = -1;

        // --- Carga automática de datos guardados en ejecuciones anteriores ---
        System.out.println("Cargando datos guardados...");
        try {
            listaClientes = ClienteRepositorio.cargar(RUTA_CLIENTES);
            listaMascotas = MascotaRepositorio.cargar(RUTA_MASCOTAS);
            listaPersonal = PersonalRepositorio.cargar(RUTA_PERSONAL);
            listaCitas = CitaRepositorio.cargar(RUTA_CITAS);
            listaConsultas = ConsultaRepositorio.cargar(RUTA_CONSULTAS);
            listaFacturas = FacturacionRepositorio.cargar(RUTA_FACTURAS);
            System.out.println("Carga finalizada.\n");
        } catch (ArchivoDatosException e) {
            System.out.println("[ERROR AL CARGAR DATOS] " + e.getMessage());
            System.out.println("El sistema continuará con las listas vacías; los datos guardados no se perdieron,\n"
                    + "revisa el mensaje de error anterior antes de volver a guardar.\n");
        }

        do {
            System.out.println("BIENVENIDO AL SISTEMA DE VETERINARIA");
            System.out.println("=========================================");
            System.out.println("Elige una opcion:");
            System.out.println("1 - Registrar Cliente y/o Mascota");
            System.out.println("2 - Buscar Cliente y ver Historial Clinico");
            System.out.println("3 - Registrar una cita");
            System.out.println("4 - Gestionar una cita (confirmar/cancelar/completar)");
            System.out.println("5 - Registrar personal");
            System.out.println("6 - Buscar personal");
            System.out.println("7 - Registrar una factura");
            System.out.println("8 - Ver facturas de un cliente");
            System.out.println("9 - Guardar todos los datos en archivo");
            System.out.println("0 - Salir");
            System.out.print("\nIngresa una opcion: ");

            try {
                opcion = scanner.nextInt();
                scanner.nextLine(); // Limpiar el buffer

                switch (opcion) {
                    case 1: {
                        System.out.println("\n--- REGISTRO DE CLIENTE Y/O MASCOTA ---");
                        String dni;
                        do {
                            System.out.print("DNI del cliente (obligatoriamente 8 dígitos numéricos): ");
                            dni = scanner.nextLine().trim();
                            if (!Cliente.esDniValido(dni)) {
                                System.out.println("[ERROR] El DNI debe tener exactamente 8 dígitos numéricos.");
                            }
                        } while (!Cliente.esDniValido(dni));

                        Cliente clienteActual = null;

                        if (!Cliente.existeDni(listaClientes, dni)) {
                            System.out.println("Procediendo a registrar cliente nuevo...");

                            String nombre;
                            do {
                                System.out.print("Nombre completo: ");
                                nombre = scanner.nextLine().trim();
                                if (!Cliente.esNombreValido(nombre)) {
                                    System.out.println("[ERROR] El nombre debe contener letras y puede incluir espacios o apóstrofos.");
                                }
                            } while (!Cliente.esNombreValido(nombre));

                            String telefono;
                            do {
                                System.out.print("Teléfono (9 dígitos): ");
                                telefono = scanner.nextLine().trim();
                                if (!Cliente.esTelefonoValido(telefono)) {
                                    System.out.println("[ERROR] El teléfono debe tener exactamente 9 dígitos numéricos.");
                                }
                            } while (!Cliente.esTelefonoValido(telefono));

                            String email;
                            do {
                                System.out.print("Email (Ej: nombre@dominio.com): ");
                                email = scanner.nextLine().trim();
                                if (!Cliente.esEmailValido(email)) {
                                    System.out.println("[ERROR] Ingresa un correo válido (Ej: nombre@dominio.com).");
                                }
                            } while (!Cliente.esEmailValido(email));

                            String direccion;
                            do {
                                System.out.print("Dirección: ");
                                direccion = scanner.nextLine().trim();
                                if (!Cliente.esDireccionValida(direccion)) {
                                    System.out.println("[ERROR] La dirección no puede estar vacía.");
                                }
                            } while (!Cliente.esDireccionValida(direccion));

                            String fecha;
                            do {
                                System.out.print("Fecha de registro (dd/MM/aaaa, Ej: 30/08/2026): ");
                                fecha = scanner.nextLine().trim();
                                if (!Cliente.esFechaRegistroValida(fecha)) {
                                    System.out.println("[ERROR] Ingresa una fecha real en formato dd/MM/aaaa.");
                                }
                            } while (!Cliente.esFechaRegistroValida(fecha));

                            clienteActual = new Cliente(nombre, telefono, email, direccion, dni, fecha);
                            listaClientes.add(clienteActual);
                            System.out.println("¡Cliente registrado con éxito! (ID asignado: " + clienteActual.getIdCliente() + ")\n");
                        } else {
                            for (Cliente c : listaClientes) {
                                if (c.getDni().equalsIgnoreCase(dni)) {
                                    clienteActual = c;
                                    break;
                                }
                            }
                            System.out.println("\n[Cliente encontrado en el sistema]");
                            clienteActual.mostrarDatos(true);
                        }

                        System.out.println("\n--- Registrando mascota para el cliente ---");
                        String nombreMascota;
                        do {
                            System.out.print("Nombre de la mascota: ");
                            nombreMascota = scanner.nextLine().trim();
                            if (!Mascota.esNombreValido(nombreMascota)) {
                                System.out.println("[ERROR] El nombre no puede estar vacío y no debe contener números.");
                            }
                        } while (!Mascota.esNombreValido(nombreMascota));

                        System.out.print("Especie (perro, gato, conejo, etc.): ");
                        String especie = scanner.nextLine();
                        System.out.print("Raza: ");
                        String raza = scanner.nextLine();

                        String fechaNacimiento;
                        do {
                            System.out.print("Fecha de nacimiento (dd/MM/aaaa, Ej: 15/05/2024): ");
                            fechaNacimiento = scanner.nextLine().trim();
                            if (!Cliente.esFechaRegistroValida(fechaNacimiento)) {
                                System.out.println("[ERROR] Ingresa una fecha real en formato dd/MM/aaaa.");
                            }
                        } while (!Cliente.esFechaRegistroValida(fechaNacimiento));

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

                        Mascota nuevaMascota = new Mascota(clienteActual.getIdCliente(), nombreMascota, especie, raza,
                                fechaNacimiento, sexo, peso, esterilizado);
                        listaMascotas.add(nuevaMascota);

                        System.out.println("¡Mascota registrada con éxito! (ID asignado: " + nuevaMascota.getIdMascota() + ")\n");
                        pausar(scanner);
                        break;
                    }

                    case 2: {
                        System.out.println("\n--- BUSCAR CLIENTE E HISTORIAL CLÍNICO ---");
                        String dniBuscar;
                        do {
                            System.out.print("Ingresa el DNI del cliente a buscar (8 dígitos): ");
                            dniBuscar = scanner.nextLine().trim();
                            if (!Cliente.esDniValido(dniBuscar)) {
                                System.out.println("[ERROR] El DNI debe tener exactamente 8 dígitos numéricos.");
                            }
                        } while (!Cliente.esDniValido(dniBuscar));

                        Cliente clienteBuscado = null;
                        for (Cliente c : listaClientes) {
                            if (c.getDni().equalsIgnoreCase(dniBuscar)) {
                                clienteBuscado = c;
                                break;
                            }
                        }

                        if (clienteBuscado == null) {
                            System.out.println("\nEl cliente no ha sido registrado.\n");
                        } else {
                            System.out.println("\n[Datos del Cliente]");
                            clienteBuscado.mostrarDatos(true);

                            ArrayList<Mascota> mascotasDelCliente = new ArrayList<>();
                            for (Mascota m : listaMascotas) {
                                if (m.getIdCliente() == clienteBuscado.getIdCliente()) {
                                    mascotasDelCliente.add(m);
                                }
                            }

                            if (mascotasDelCliente.isEmpty()) {
                                System.out.println("\nEste cliente no tiene mascotas registradas.");
                            } else {
                                System.out.println("\n--- Mascotas Registradas ---");
                                for (Mascota m : mascotasDelCliente) {
                                    m.mostrarDatos(true);
                                }

                                System.out.print("\nIngresa el ID de la mascota para ver su historial clínico (o 0 para salir): ");
                                String inputId = scanner.nextLine();

                                if (!inputId.equals("0") && Mascota.esIdNumerico(inputId)) {
                                    int idMascotaElegida = Integer.parseInt(inputId);
                                    System.out.println("\n--- HISTORIAL CLÍNICO ---");
                                    boolean tieneConsultas = false;

                                    for (Consulta c : listaConsultas) {
                                        if (c.getIdMascota() == idMascotaElegida) {
                                            c.mostrarDatos();
                                            tieneConsultas = true;
                                        }
                                    }

                                    if (!tieneConsultas) {
                                        System.out.println("Esta mascota no tiene consultas registradas en su historial.\n");
                                    }
                                }
                            }
                        }
                        pausar(scanner);
                        break;
                    }

                    case 3: {
                        System.out.println("\n--- REGISTRAR CITA ---");

                        if (listaMascotas.isEmpty()) {
                            System.out.println("[ERROR] No hay mascotas registradas. Registra una mascota antes de agendar una cita.\n");
                            pausar(scanner);
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
                                case "1":
                                    tipoCita = "Consulta";
                                    break;
                                case "2":
                                    tipoCita = "Control";
                                    break;
                                case "3":
                                    tipoCita = "Cirugía";
                                    break;
                                case "4":
                                    tipoCita = "Vacuna";
                                    break;
                                default:
                                    System.out.println("[ERROR] Opción inválida.");
                            }
                        }

                        Cita nuevaCita = new Cita(idMascotaCita, fechaHoraCita, veterinarioCita, tipoCita);
                        listaCitas.add(nuevaCita);

                        System.out.println("¡Cita registrada con éxito! (ID asignado: " + nuevaCita.getIdCita()
                                + ", estado: " + nuevaCita.getEstado() + ")\n");
                        pausar(scanner);
                        break;
                    }

                    case 4: {
                        System.out.println("\n--- GESTIONAR CITA ---");

                        if (listaCitas.isEmpty()) {
                            System.out.println("[ERROR] No hay citas registradas.\n");
                            pausar(scanner);
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

                                    Mascota mascotaAtendida = Mascota.buscarPorId(listaMascotas, citaGestion.getIdMascota());
                                    if (mascotaAtendida != null) {
                                        mascotaAtendida.registrarControlPeso(pesoConsulta, citaGestion.getFechaHora());
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
                        pausar(scanner);
                        break;
                    }

                    case 5: {
                        System.out.println("\n--- REGISTRO DE PERSONAL ---");

                        String nombrePersonal;
                        do {
                            System.out.print("Nombre completo: ");
                            nombrePersonal = scanner.nextLine();
                            if (!Personal.esNombreValido(nombrePersonal)) {
                                System.out.println("[ERROR] El nombre no puede estar vacío.");
                            }
                        } while (!Personal.esNombreValido(nombrePersonal));

                        System.out.print("Horario de atención (Ej: Lunes a Viernes 8:00 am - 5:00 pm): ");
                        String horarioPersonal = scanner.nextLine();

                        String rolPersonal = null;
                        while (rolPersonal == null) {
                            System.out.println("Rol: 1-Veterinario  2-Asistente veterinario  3-Recepcionista");
                            System.out.print("Elige una opción: ");
                            String rolOpcion = scanner.nextLine();
                            switch (rolOpcion) {
                                case "1":
                                    rolPersonal = Personal.ROL_VETERINARIO;
                                    break;
                                case "2":
                                    rolPersonal = Personal.ROL_ASISTENTE;
                                    break;
                                case "3":
                                    rolPersonal = Personal.ROL_RECEPCIONISTA;
                                    break;
                                default:
                                    System.out.println("[ERROR] Opción inválida.");
                            }
                        }

                        Personal nuevoPersonal = new Personal(nombrePersonal, horarioPersonal, rolPersonal);
                        listaPersonal.add(nuevoPersonal);

                        System.out.println("¡Empleado registrado con éxito! (ID asignado: " + nuevoPersonal.getIdEmpleado() + ")\n");
                        pausar(scanner);
                        break;
                    }

                    case 6: {
                        System.out.println("\n--- BUSCAR PERSONAL ---");
                        System.out.print("Ingresa el ID del empleado a buscar: ");
                        String idPersonalTexto = scanner.nextLine();

                        if (!Personal.esIdNumerico(idPersonalTexto)) {
                            System.out.println("[ERROR] El ID debe contener solo números.\n");
                            pausar(scanner);
                            break;
                        }

                        Personal personalBuscado = Personal.buscarPorId(listaPersonal, Integer.parseInt(idPersonalTexto));
                        if (personalBuscado != null) {
                            personalBuscado.mostrarDatos();
                        } else {
                            System.out.println("No se encontró ningún empleado con ese ID.\n");
                        }
                        pausar(scanner);
                        break;
                    }

                    case 7: {
                        System.out.println("\n--- REGISTRAR FACTURA ---");

                        if (listaConsultas.isEmpty()) {
                            System.out.println("[ERROR] No hay consultas registradas. Registra una consulta antes de emitir una factura.\n");
                            pausar(scanner);
                            break;
                        }

                        int idConsultaFactura = -1;
                        Consulta consultaFacturada = null;
                        do {
                            System.out.print("ID de la consulta a facturar: ");
                            String idConsultaTexto = scanner.nextLine();
                            if (!Facturacion.esIdNumerico(idConsultaTexto)) {
                                System.out.println("[ERROR] El ID de la consulta debe contener solo números.");
                                continue;
                            }
                            idConsultaFactura = Integer.parseInt(idConsultaTexto);
                            for (Consulta c : listaConsultas) {
                                if (c.getIdConsulta() == idConsultaFactura) {
                                    consultaFacturada = c;
                                    break;
                                }
                            }
                            if (consultaFacturada == null) {
                                System.out.println("[ERROR] No existe ninguna consulta con ese ID.");
                            }
                        } while (consultaFacturada == null);

                        int idMascotaFactura = consultaFacturada.getIdMascota();
                        Mascota mascotaFactura = Mascota.buscarPorId(listaMascotas, idMascotaFactura);
                        int idClienteFactura = -1;
                        boolean idClienteEncontrado = false;
                        if (mascotaFactura != null) {
                            idClienteFactura = mascotaFactura.getIdCliente();
                            idClienteEncontrado = Cliente.existeId(listaClientes, idClienteFactura);
                        }

                        if (!idClienteEncontrado) {
                            System.out.println("[ERROR] No se pudo determinar un cliente válido a partir de la consulta seleccionada.\n");
                            pausar(scanner);
                            break;
                        }

                        System.out.print("Fecha de la factura (dd/MM/aaaa, Ej: 15/09/2026): ");
                        String fechaFactura = scanner.nextLine();

                        double montoFactura = -1;
                        boolean montoValido = false;
                        do {
                            System.out.print("Monto (S/): ");
                            String montoTexto = scanner.nextLine();
                            try {
                                montoFactura = Double.parseDouble(montoTexto);
                                montoValido = Facturacion.esMontoValido(montoFactura);
                                if (!montoValido) {
                                    System.out.println("[ERROR] El monto debe ser un número mayor que 0.");
                                }
                            } catch (NumberFormatException e) {
                                System.out.println("[ERROR] Ingresa un monto válido (ej: 80.00).");
                            }
                        } while (!montoValido);

                        String metodoPagoFactura = null;
                        while (metodoPagoFactura == null) {
                            System.out.println("Método de pago: 1-Efectivo  2-Tarjeta  3-Yape/Plin  4-Transferencia");
                            System.out.print("Elige una opción: ");
                            String metodoOpcion = scanner.nextLine();
                            switch (metodoOpcion) {
                                case "1":
                                    metodoPagoFactura = Facturacion.PAGO_EFECTIVO;
                                    break;
                                case "2":
                                    metodoPagoFactura = Facturacion.PAGO_TARJETA;
                                    break;
                                case "3":
                                    metodoPagoFactura = Facturacion.PAGO_YAPE_PLIN;
                                    break;
                                case "4":
                                    metodoPagoFactura = Facturacion.PAGO_TRANSFERENCIA;
                                    break;
                                default:
                                    System.out.println("[ERROR] Opción inválida.");
                            }
                        }

                        Facturacion nuevaFactura = new Facturacion(idClienteFactura, idConsultaFactura, fechaFactura,
                                montoFactura, metodoPagoFactura);
                        listaFacturas.add(nuevaFactura);

                        System.out.println("¡Factura registrada con éxito! (ID asignado: " + nuevaFactura.getIdFactura() + ")\n");
                        pausar(scanner);
                        break;
                    }

                    case 8: {
                        System.out.println("\n--- FACTURAS DE UN CLIENTE ---");

                        if (listaClientes.isEmpty()) {
                            System.out.println("[ERROR] No hay clientes registrados.\n");
                            pausar(scanner);
                            break;
                        }

                        int idClienteBuscar = -1;
                        boolean idClienteValido = false;
                        do {
                            System.out.print("ID del cliente: ");
                            String idClienteTexto = scanner.nextLine();
                            if (!Facturacion.esIdNumerico(idClienteTexto)) {
                                System.out.println("[ERROR] El ID del cliente debe contener solo números.");
                                continue;
                            }
                            idClienteBuscar = Integer.parseInt(idClienteTexto);
                            idClienteValido = Cliente.existeId(listaClientes, idClienteBuscar);
                            if (!idClienteValido) {
                                System.out.println("[ERROR] No existe ningún cliente con ese ID.");
                            }
                        } while (!idClienteValido);

                        ArrayList<Facturacion> facturasCliente = Facturacion.buscarPorCliente(listaFacturas, idClienteBuscar);
                        if (facturasCliente.isEmpty()) {
                            System.out.println("Este cliente no tiene facturas registradas.\n");
                        } else {
                            for (Facturacion f : facturasCliente) {
                                f.mostrarDatos();
                            }
                        }
                        pausar(scanner);
                        break;
                    }

                    case 9: {
                        System.out.println("\n--- GUARDAR DATOS EN ARCHIVO ---");
                        guardarTodo(listaClientes, listaMascotas, listaCitas, listaConsultas, listaPersonal, listaFacturas);
                        pausar(scanner);
                        break;
                    }

                    case 0: {
                        System.out.print("\n¿Deseas guardar los cambios antes de salir? (S/N): ");
                        String respuestaGuardar = scanner.nextLine();
                        if (respuestaGuardar.trim().equalsIgnoreCase("S")) {
                            guardarTodo(listaClientes, listaMascotas, listaCitas, listaConsultas, listaPersonal, listaFacturas);
                        }
                        System.out.println("Saliendo del sistema...");
                        break;
                    }

                    default:
                        System.out.println("Opción inválida. Elige un número entre 0 y 9.\n");
                        pausar(scanner);
                }

            } catch (InputMismatchException e) {
                System.out.println("\n[ERROR] Debes ingresar un número válido, no letras.\n");
                scanner.nextLine();
                opcion = -1;
                pausar(scanner);
            } catch (Exception e) {
                System.out.println("\n[ERROR INESPERADO]: " + e.getMessage() + "\n");
                pausar(scanner);
            }

        } while (opcion != 0);

        scanner.close();
    }

    /**
     * Guarda las seis colecciones del sistema en sus respectivos archivos CSV (carpeta data/).
     * Si alguno falla (permisos, disco lleno, ruta inválida, etc.) se informa el error concreto
     * de ESE archivo y se continúa intentando guardar los demás, en vez de perder todo el avance
     * por un solo archivo con problemas.
     */
    private static void guardarTodo(ArrayList<Cliente> listaClientes, ArrayList<Mascota> listaMascotas,
            ArrayList<Cita> listaCitas, ArrayList<Consulta> listaConsultas, ArrayList<Personal> listaPersonal,
            ArrayList<Facturacion> listaFacturas) {
        int guardadosOk = 0;
        int guardadosError = 0;

        guardadosOk += intentarGuardar("clientes", () -> ClienteRepositorio.guardar(listaClientes, RUTA_CLIENTES));
        guardadosOk += intentarGuardar("mascotas", () -> MascotaRepositorio.guardar(listaMascotas, RUTA_MASCOTAS));
        guardadosOk += intentarGuardar("personal", () -> PersonalRepositorio.guardar(listaPersonal, RUTA_PERSONAL));
        guardadosOk += intentarGuardar("citas", () -> CitaRepositorio.guardar(listaCitas, RUTA_CITAS));
        guardadosOk += intentarGuardar("consultas", () -> ConsultaRepositorio.guardar(listaConsultas, RUTA_CONSULTAS));
        guardadosOk += intentarGuardar("facturas", () -> FacturacionRepositorio.guardar(listaFacturas, RUTA_FACTURAS));

        guardadosError = 6 - guardadosOk;
        if (guardadosError == 0) {
            System.out.println("¡Todos los datos se guardaron correctamente en la carpeta 'data'!\n");
        } else {
            System.out.println(guardadosOk + " de 6 archivos se guardaron correctamente. Revisa los errores anteriores.\n");
        }
    }

    /** Interfaz funcional interna para poder reutilizar el mismo bloque try/catch en guardarTodo(). */
    private interface AccionGuardado {
        void ejecutar() throws ArchivoDatosException;
    }

    private static int intentarGuardar(String nombreArchivo, AccionGuardado accion) {
        try {
            accion.ejecutar();
            return 1;
        } catch (ArchivoDatosException e) {
            System.out.println("[ERROR] No se pudo guardar " + nombreArchivo + ": " + e.getMessage());
            return 0;
        }
    }

    private static void pausar(Scanner scanner) {
        System.out.print("\nPresione ENTER para volver al menú principal...");
        scanner.nextLine();
    }
}
