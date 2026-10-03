import java.util.ArrayList;
import java.util.Scanner;
import modelo.Cita;
import modelo.Cliente;
import modelo.Consulta;
import modelo.Facturacion;
import modelo.Mascota;
import modelo.Personal;
import persistencia.ArchivoDatosException;
import persistencia.RepositorioArchivos;
import util.EntradaDatos;
import util.Validaciones;

public class Main {

    // Rutas de los archivos de datos
    private static final String RUTA_CLIENTES = "data/clientes.csv";
    private static final String RUTA_MASCOTAS = "data/mascotas.csv";
    private static final String RUTA_CITAS = "data/citas.csv";
    private static final String RUTA_CONSULTAS = "data/consultas.csv";
    private static final String RUTA_PERSONAL = "data/personal.csv";
    private static final String RUTA_FACTURAS = "data/facturas.csv";

    // Listas del sistema
    private static ArrayList<Cliente> listaClientes = new ArrayList<>();
    private static ArrayList<Mascota> listaMascotas = new ArrayList<>();
    private static ArrayList<Cita> listaCitas = new ArrayList<>();
    private static ArrayList<Consulta> listaConsultas = new ArrayList<>();
    private static ArrayList<Personal> listaPersonal = new ArrayList<>();
    private static ArrayList<Facturacion> listaFacturas = new ArrayList<>();

    // Lectura de datos con control de errores (clase util.EntradaDatos)
    private static EntradaDatos entrada;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        entrada = new EntradaDatos(scanner);
        cargarTodo();

        int opcion;
        do {
            mostrarMenu();
            opcion = entrada.leerOpcionMenu("\nIngresa una opcion: ");
            try {
                switch (opcion) {
                    case 1:
                        registrarClienteYMascota();
                        break;
                    case 2:
                        buscarClienteEHistorial();
                        break;
                    case 3:
                        registrarCita();
                        break;
                    case 4:
                        gestionarCita();
                        break;
                    case 5:
                        registrarPersonal();
                        break;
                    case 6:
                        buscarPersonal();
                        break;
                    case 7:
                        registrarFactura();
                        break;
                    case 8:
                        verFacturasCliente();
                        break;
                    case 9:
                        System.out.println("\n--- GUARDAR DATOS EN ARCHIVO ---");
                        guardarTodo();
                        break;
                    case 0:
                        System.out.println("Saliendo del sistema...");
                        break;
                    default:
                        System.out.println("[ERROR] Opción inválida. Elige un número entre 0 y 9.");
                }
            } catch (Exception e) {
                System.out.println("\n[ERROR INESPERADO]: " + e.getMessage());
            }
            if (opcion != 0) {
                entrada.pausar();
            }
        } while (opcion != 0);

        scanner.close();
    }

    private static void mostrarMenu() {
        System.out.println("\nBIENVENIDO AL SISTEMA DE VETERINARIA");
        System.out.println("=========================================");
        System.out.println("Elige una opcion:");
        System.out.println("1 - Registrar Cliente y/o Mascota");
        System.out.println("2 - Buscar Cliente y ver Historial Clinico");
        System.out.println("3 - Registrar una cita");
        System.out.println("4 - Gestionar una cita (completar/cancelar)");
        System.out.println("5 - Registrar personal");
        System.out.println("6 - Buscar personal");
        System.out.println("7 - Registrar una factura");
        System.out.println("8 - Ver facturas de un cliente");
        System.out.println("9 - Guardar datos registrados");
        System.out.println("0 - Salir");
    }

    // =====================================================================
    // 1 - Registrar cliente y/o mascota
    // =====================================================================
    private static void registrarClienteYMascota() {
        System.out.println("\n--- REGISTRO DE CLIENTE Y/O MASCOTA ---");
        String dni = entrada.leerDni("DNI del cliente (obligatoriamente 8 dígitos numéricos): ");
        Cliente cliente = Cliente.buscarPorDni(listaClientes, dni);

        if (cliente == null) {
            System.out.println("Procediendo a registrar cliente nuevo...");
            String nombre = entrada.leerNombre("Nombre completo: ");
            String telefono = entrada.leerTelefono("Celular (9 dígitos): ");
            String email = entrada.leerEmail("Email (Ej: nombre@dominio.com): ");
            String direccion = entrada.leerTextoObligatorio("Dirección: ", "La dirección");
            String fecha = entrada.leerFecha("Fecha de registro (dd/MM/aaaa, Ej: 30/08/2026): ");

            cliente = new Cliente(nombre, telefono, email, direccion, dni, fecha);
            listaClientes.add(cliente);
            System.out.println("¡Cliente registrado con éxito! (ID asignado: " + cliente.getIdCliente() + ")\n");
        } else {
            System.out.println("\n[Cliente encontrado en el sistema]");
            cliente.mostrarDatos(true);
        }

        System.out.println("\n--- Registrando mascota para el cliente ---");
        String nombreMascota = entrada.leerNombre("Nombre de la mascota: ");
        String especie = entrada.leerTextoObligatorio("Especie (perro, gato, conejo, etc.): ", "La especie");
        String raza = entrada.leerTextoObligatorio("Raza: ", "La raza");
        String fechaNacimiento = entrada.leerFecha("Fecha de nacimiento (dd/MM/aaaa, Ej: 15/05/2024): ");
        String sexo = entrada.leerValidado("Sexo (M/H): ",
                t -> t.equalsIgnoreCase("M") || t.equalsIgnoreCase("H"), "Escribe M (macho) o H (hembra).")
                .toUpperCase();
        double peso = entrada.leerDecimalPositivo("Peso (kg): ", "El peso", "4.5");
        boolean esterilizado = entrada.leerSiNo("¿Esterilizado? (S/N): ");

        Mascota nuevaMascota = new Mascota(cliente.getIdCliente(), nombreMascota, especie, raza,
                fechaNacimiento, sexo, peso, esterilizado);
        listaMascotas.add(nuevaMascota);
        System.out.println("¡Mascota registrada con éxito! (ID asignado: " + nuevaMascota.getIdMascota() + ")");
    }

    // =====================================================================
    // 2 - Buscar cliente e historial clínico
    // =====================================================================
    private static void buscarClienteEHistorial() {
        System.out.println("\n--- BUSCAR CLIENTE E HISTORIAL CLÍNICO ---");
        Cliente cliente = pedirClientePorDni("Ingresa el DNI del cliente a buscar (8 dígitos): ");
        if (cliente == null) {
            return;
        }
        System.out.println("\n[Datos del Cliente]");
        cliente.mostrarDatos(true);

        ArrayList<Mascota> mascotas = mostrarMascotasDe(cliente);
        if (mascotas.isEmpty()) {
            return;
        }

        String inputId = entrada.leerTexto("\nIngresa el ID de la mascota para ver su historial clínico (o 0 para salir): ");
        if (inputId.equals("0") || !Validaciones.esIdNumerico(inputId)) {
            return;
        }
        int idMascota = Integer.parseInt(inputId);
        System.out.println("\n--- HISTORIAL CLÍNICO ---");
        boolean tieneConsultas = false;
        for (Consulta c : listaConsultas) {
            if (c.getIdMascota() == idMascota) {
                c.mostrarDatos();
                tieneConsultas = true;
            }
        }
        if (!tieneConsultas) {
            System.out.println("Esta mascota no tiene consultas registradas en su historial.");
        }
    }

    // =====================================================================
    // 3 - Registrar cita (dueño por DNI, veterinario por DNI, fecha validada)
    // =====================================================================
    private static void registrarCita() {
        System.out.println("\n--- REGISTRAR CITA ---");
        Cliente cliente = pedirClientePorDni("DNI del dueño de la mascota: ");
        if (cliente == null) {
            return;
        }
        ArrayList<Mascota> mascotas = mostrarMascotasDe(cliente);
        if (mascotas.isEmpty()) {
            return;
        }
        int idMascota = entrada.leerIdDeLista("\nID de la mascota: ", idsDeMascotas(mascotas),
                "Elige el ID de una de las mascotas mostradas.");

        String fecha = entrada.leerFecha("Fecha de la cita (dd/MM/aaaa, Ej: 15/09/2026): ");
        String hora = entrada.leerHora("Hora de la cita (Ej: 10:00 am): ");

        ArrayList<Personal> veterinarios = Personal.buscarPorRol(listaPersonal, Personal.ROL_VETERINARIO);
        if (veterinarios.isEmpty()) {
            System.out.println("[ERROR] No hay veterinarios registrados. Registra uno en la opción 5.");
            return;
        }
        System.out.println("\n--- Veterinarios disponibles ---");
        for (Personal p : veterinarios) {
            p.mostrarDatos(true);
        }
        Personal veterinario;
        do {
            String dniVeterinario = entrada.leerDni("DNI del veterinario asignado: ");
            veterinario = Personal.buscarPorDni(veterinarios, dniVeterinario);
            if (veterinario == null) {
                System.out.println("[ERROR] No existe un veterinario con ese DNI.");
            }
        } while (veterinario == null);

        String[] tipos = {"Consulta", "Control", "Cirugía", "Vacuna"};
        String tipo = tipos[entrada.leerOpcion("Tipo de cita:", tipos) - 1];

        Cita nuevaCita = new Cita(idMascota, fecha + " " + hora, veterinario.getNombre(), tipo);
        listaCitas.add(nuevaCita);
        System.out.println("¡Cita registrada con éxito! (ID asignado: " + nuevaCita.getIdCita()
                + ", veterinario: " + veterinario.getNombre() + ", estado: " + nuevaCita.getEstado() + ")");
    }

    // =====================================================================
    // 4 - Gestionar cita: se busca por DNI del dueño; opciones Completar / Cancelar
    // =====================================================================
    private static void gestionarCita() {
        System.out.println("\n--- GESTIONAR CITA ---");
        Cliente cliente = pedirClientePorDni("DNI del dueño de la mascota: ");
        if (cliente == null) {
            return;
        }
        ArrayList<Mascota> mascotas = Mascota.buscarPorCliente(listaMascotas, cliente.getIdCliente());
        ArrayList<Cita> citas = Cita.buscarPorMascotas(listaCitas, mascotas);
        if (citas.isEmpty()) {
            System.out.println("Este cliente no tiene citas registradas para sus mascotas.");
            return;
        }

        System.out.println("\n--- Citas de las mascotas de " + cliente.getNombre() + " ---");
        ArrayList<Integer> idsCitas = new ArrayList<>();
        for (Cita c : citas) {
            Mascota m = Mascota.buscarPorId(listaMascotas, c.getIdMascota());
            System.out.println("Cita #" + c.getIdCita() + " | Mascota: " + (m != null ? m.getNombre() : "-")
                    + " | " + c.getFechaHora() + " | " + c.getTipo() + " | Vet: " + c.getVeterinario()
                    + " | Estado: " + c.getEstado());
            idsCitas.add(c.getIdCita());
        }

        int idCita = entrada.leerIdDeLista("\nID de la cita: ", idsCitas, "Elige el ID de una de las citas mostradas.");
        Cita cita = Cita.buscarPorId(listaCitas, idCita);
        cita.mostrarDatos();

        if (cita.estaCerrada()) {
            System.out.println("[AVISO] Esta cita ya está " + cita.getEstado().toLowerCase() + " y no puede modificarse.");
            return;
        }

        int accion = entrada.leerOpcion("¿Qué deseas hacer con esta cita?",
                "Completar (registra la consulta / historial clínico)",
                "Cancelar");
        if (accion == 1) {
            completarCita(cita);
        } else {
            cita.cancelar();
            System.out.println("Cita cancelada.");
        }
    }

    private static void completarCita(Cita cita) {
        System.out.println("\n--- REGISTRO DE CONSULTA (HISTORIAL CLÍNICO) ---");
        String motivo = entrada.leerTextoObligatorio("Motivo de la consulta: ", "El motivo");
        String diagnostico = entrada.leerTextoObligatorio("Diagnóstico: ", "El diagnóstico");
        String tratamiento = entrada.leerTextoObligatorio("Tratamiento indicado: ", "El tratamiento");
        double peso = entrada.leerDecimalPositivo("Peso (kg): ", "El peso", "4.5");
        double temperatura = entrada.leerDecimalPositivo("Temperatura (°C): ", "La temperatura", "38.5");
        String observaciones = entrada.leerTexto("Observaciones: ");
        String proximaCita = entrada.leerValidado("Próxima cita sugerida (Ej: 15/10/2026, o 'NO'): ",
                t -> t.equalsIgnoreCase("NO") || Validaciones.esFechaValida(t),
                "Ingresa una fecha real en formato dd/MM/aaaa o escribe NO.");

        Consulta nuevaConsulta = new Consulta(cita.getIdMascota(), cita.getIdCita(), cita.getFechaHora(),
                motivo, cita.getVeterinario(), diagnostico, tratamiento, peso, temperatura,
                observaciones, proximaCita.toUpperCase().equals("NO") ? "NO" : proximaCita);
        listaConsultas.add(nuevaConsulta);

        Mascota mascota = Mascota.buscarPorId(listaMascotas, cita.getIdMascota());
        if (mascota != null) {
            mascota.registrarControlPeso(peso, cita.getFechaHora());
        }
        cita.completar();
        System.out.println("¡Cita completada y consulta registrada! (ID consulta: " + nuevaConsulta.getIdConsulta() + ")");
    }

    // =====================================================================
    // 5 - Registrar personal (con DNI y turno de atención)
    // =====================================================================
    private static void registrarPersonal() {
        System.out.println("\n--- REGISTRO DE PERSONAL ---");
        String dni = entrada.leerValidado("DNI del empleado (8 dígitos): ",
                t -> Validaciones.esDniValido(t) && Personal.buscarPorDni(listaPersonal, t) == null,
                "El DNI debe tener 8 dígitos y no puede pertenecer a otro empleado registrado.");
        String nombre = entrada.leerNombre("Nombre completo: ");

        String[] turnos = {Personal.TURNO_MANANA, Personal.TURNO_TARDE, Personal.TURNO_NOCHE};
        String horario = turnos[entrada.leerOpcion("Turno de atención:", turnos) - 1];

        String[] roles = {Personal.ROL_VETERINARIO, Personal.ROL_ASISTENTE, Personal.ROL_RECEPCIONISTA};
        String rol = roles[entrada.leerOpcion("Rol:", roles) - 1];

        Personal nuevoPersonal = new Personal(dni, nombre, horario, rol);
        listaPersonal.add(nuevoPersonal);
        System.out.println("¡Empleado registrado con éxito! (ID asignado: " + nuevoPersonal.getIdEmpleado() + ")");
    }

    // =====================================================================
    // 6 - Buscar personal por DNI
    // =====================================================================
    private static void buscarPersonal() {
        System.out.println("\n--- BUSCAR PERSONAL ---");
        String dni = entrada.leerDni("Ingresa el DNI del empleado a buscar: ");
        Personal personal = Personal.buscarPorDni(listaPersonal, dni);
        if (personal != null) {
            personal.mostrarDatos();
        } else {
            System.out.println("No se encontró ningún empleado con ese DNI.");
        }
    }

    // =====================================================================
    // 7 - Registrar factura: se busca por DNI del dueño y se eligen consultas pendientes
    // =====================================================================
    private static void registrarFactura() {
        System.out.println("\n--- REGISTRAR FACTURA ---");
        Cliente cliente = pedirClientePorDni("DNI del dueño de la mascota: ");
        if (cliente == null) {
            return;
        }
        ArrayList<Consulta> consultas = consultasDe(cliente);
        if (consultas.isEmpty()) {
            System.out.println("Este cliente no tiene consultas registradas para facturar.");
            return;
        }

        System.out.println("\n--- Consultas de las mascotas de " + cliente.getNombre() + " ---");
        ArrayList<Integer> pendientes = new ArrayList<>();
        for (Consulta c : consultas) {
            Facturacion factura = Facturacion.buscarPorConsulta(listaFacturas, c.getIdConsulta());
            String estado = (factura != null) ? "Facturada (Factura #" + factura.getIdFactura() + ")" : "PENDIENTE DE FACTURAR";
            System.out.println("Consulta #" + c.getIdConsulta() + " | Mascota: " + nombreMascota(c.getIdMascota())
                    + " | " + c.getFechaHora() + " | " + c.getDiagnostico() + " | " + estado);
            if (factura == null) {
                pendientes.add(c.getIdConsulta());
            }
        }
        if (pendientes.isEmpty()) {
            System.out.println("\nTodas las consultas de este cliente ya fueron facturadas.");
            return;
        }

        int idConsulta = entrada.leerIdDeLista("\nID de la consulta a facturar: ", pendientes,
                "Elige el ID de una consulta PENDIENTE DE FACTURAR.");
        String fecha = entrada.leerFecha("Fecha de la factura (dd/MM/aaaa, Ej: 15/09/2026): ");
        double monto = entrada.leerDecimalPositivo("Monto (S/): ", "El monto", "80.00");

        String[] metodos = {Facturacion.PAGO_EFECTIVO, Facturacion.PAGO_TARJETA,
            Facturacion.PAGO_YAPE_PLIN, Facturacion.PAGO_TRANSFERENCIA};
        String metodoPago = metodos[entrada.leerOpcion("Método de pago:", metodos) - 1];

        Facturacion nuevaFactura = new Facturacion(cliente.getIdCliente(), idConsulta, fecha, monto, metodoPago);
        listaFacturas.add(nuevaFactura);
        System.out.println("¡Factura registrada con éxito! (ID asignado: " + nuevaFactura.getIdFactura() + ")");
    }

    // =====================================================================
    // 8 - Ver facturas de un cliente (por DNI) y sus consultas pendientes de facturar
    // =====================================================================
    private static void verFacturasCliente() {
        System.out.println("\n--- FACTURAS DE UN CLIENTE ---");
        Cliente cliente = pedirClientePorDni("DNI del cliente: ");
        if (cliente == null) {
            return;
        }
        cliente.mostrarDatos(true);

        ArrayList<Facturacion> facturas = Facturacion.buscarPorCliente(listaFacturas, cliente.getIdCliente());
        System.out.println("\n--- Facturas emitidas ---");
        if (facturas.isEmpty()) {
            System.out.println("Este cliente no tiene facturas registradas.");
        } else {
            for (Facturacion f : facturas) {
                f.mostrarDatos(true);
            }
        }

        System.out.println("\n--- Consultas pendientes de facturar ---");
        boolean hayPendientes = false;
        for (Consulta c : consultasDe(cliente)) {
            if (Facturacion.buscarPorConsulta(listaFacturas, c.getIdConsulta()) == null) {
                System.out.println("Consulta #" + c.getIdConsulta() + " | Mascota: " + nombreMascota(c.getIdMascota())
                        + " | " + c.getFechaHora() + " | " + c.getDiagnostico());
                hayPendientes = true;
            }
        }
        if (!hayPendientes) {
            System.out.println("No tiene consultas pendientes de facturar.");
        }
    }

    // =====================================================================
    // Métodos de apoyo reutilizados por varias opciones
    // =====================================================================

    /** Pide un DNI y devuelve el cliente, o null (con mensaje) si no está registrado. */
    private static Cliente pedirClientePorDni(String mensaje) {
        String dni = entrada.leerDni(mensaje);
        Cliente cliente = Cliente.buscarPorDni(listaClientes, dni);
        if (cliente == null) {
            System.out.println("\nNo existe ningún cliente registrado con ese DNI.");
        }
        return cliente;
    }

    /** Muestra las mascotas del cliente y las devuelve (lista vacía si no tiene). */
    private static ArrayList<Mascota> mostrarMascotasDe(Cliente cliente) {
        ArrayList<Mascota> mascotas = Mascota.buscarPorCliente(listaMascotas, cliente.getIdCliente());
        if (mascotas.isEmpty()) {
            System.out.println("\nEste cliente no tiene mascotas registradas.");
        } else {
            System.out.println("\n--- Mascotas registradas de " + cliente.getNombre() + " ---");
            for (Mascota m : mascotas) {
                m.mostrarDatos(true);
            }
        }
        return mascotas;
    }

    private static ArrayList<Integer> idsDeMascotas(ArrayList<Mascota> mascotas) {
        ArrayList<Integer> ids = new ArrayList<>();
        for (Mascota m : mascotas) {
            ids.add(m.getIdMascota());
        }
        return ids;
    }

    /** Todas las consultas de las mascotas de un cliente. */
    private static ArrayList<Consulta> consultasDe(Cliente cliente) {
        ArrayList<Integer> idsMascotas = idsDeMascotas(Mascota.buscarPorCliente(listaMascotas, cliente.getIdCliente()));
        ArrayList<Consulta> resultado = new ArrayList<>();
        for (Consulta c : listaConsultas) {
            if (idsMascotas.contains(c.getIdMascota())) {
                resultado.add(c);
            }
        }
        return resultado;
    }

    private static String nombreMascota(int idMascota) {
        Mascota m = Mascota.buscarPorId(listaMascotas, idMascota);
        return m != null ? m.getNombre() : "-";
    }

    // =====================================================================
    // Persistencia
    // =====================================================================

    private static void cargarTodo() {
        System.out.println("Cargando datos guardados...");
        try {
            listaClientes = RepositorioArchivos.cargarClientes(RUTA_CLIENTES);
            listaMascotas = RepositorioArchivos.cargarMascotas(RUTA_MASCOTAS);
            listaPersonal = RepositorioArchivos.cargarPersonal(RUTA_PERSONAL);
            listaCitas = RepositorioArchivos.cargarCitas(RUTA_CITAS);
            listaConsultas = RepositorioArchivos.cargarConsultas(RUTA_CONSULTAS);
            listaFacturas = RepositorioArchivos.cargarFacturas(RUTA_FACTURAS);
            System.out.println("Carga finalizada.");
        } catch (ArchivoDatosException e) {
            System.out.println("[ERROR AL CARGAR DATOS] " + e.getMessage());
            System.out.println("El sistema continuará con las listas vacías; los datos guardados no se perdieron,\n"
                    + "revisa el mensaje de error anterior antes de volver a guardar.");
        }
    }

    // Guarda las 6 listas en CSV
    private static void guardarTodo() {
        int guardadosOk = 0;
        guardadosOk += intentarGuardar("clientes", () -> RepositorioArchivos.guardarClientes(listaClientes, RUTA_CLIENTES));
        guardadosOk += intentarGuardar("mascotas", () -> RepositorioArchivos.guardarMascotas(listaMascotas, RUTA_MASCOTAS));
        guardadosOk += intentarGuardar("personal", () -> RepositorioArchivos.guardarPersonal(listaPersonal, RUTA_PERSONAL));
        guardadosOk += intentarGuardar("citas", () -> RepositorioArchivos.guardarCitas(listaCitas, RUTA_CITAS));
        guardadosOk += intentarGuardar("consultas", () -> RepositorioArchivos.guardarConsultas(listaConsultas, RUTA_CONSULTAS));
        guardadosOk += intentarGuardar("facturas", () -> RepositorioArchivos.guardarFacturas(listaFacturas, RUTA_FACTURAS));

        if (guardadosOk == 6) {
            System.out.println("¡Todos los datos se guardaron correctamente en la carpeta 'data'!");
        } else {
            System.out.println(guardadosOk + " de 6 archivos se guardaron correctamente. Revisa los errores anteriores.");
        }
    }

    // Acción de guardado reutilizable
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
}
