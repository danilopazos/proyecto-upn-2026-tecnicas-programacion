package persistencia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import modelo.Cita;
import modelo.Cliente;
import modelo.Consulta;
import modelo.Facturacion;
import modelo.Mascota;
import modelo.Personal;
import util.Validaciones;

// Guarda y carga las entidades en MySQL (JDBC con PreparedStatement).
// Las clases del modelo manejan fechas como texto (dd/MM/aaaa); aquí se convierten a los tipos DATE, TIME y DATETIME de MySQL.
// Las columnas con DEFAULT CURRENT_TIMESTAMP (fecha de registro, fecha de factura, fecha de consulta, fecha de peso)
// NO se envían en el INSERT: las completa MySQL con la fecha del sistema.
public final class RepositorioMySQL {

    private static final DateTimeFormatter F_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter F_FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter F_HORA = new DateTimeFormatterBuilder()
            .parseCaseInsensitive().appendPattern("hh:mm a").toFormatter(Locale.ENGLISH);

    private RepositorioMySQL() {
        // No se instancia
    }

    // --- Conversión MySQL -> texto del modelo ---

    // Columna DATE -> "dd/MM/aaaa"
    private static String fechaDeDate(ResultSet rs, String columna) throws SQLException {
        LocalDate f = rs.getObject(columna, LocalDate.class);
        return f != null ? f.format(F_FECHA) : Validaciones.fechaActual();
    }

    // Columna DATETIME -> "dd/MM/aaaa"
    private static String fechaDeDatetime(ResultSet rs, String columna) throws SQLException {
        LocalDateTime f = rs.getObject(columna, LocalDateTime.class);
        return f != null ? f.format(F_FECHA) : Validaciones.fechaActual();
    }

    // Columna DATETIME -> "dd/MM/aaaa HH:mm"
    private static String fechaHoraDeDatetime(ResultSet rs, String columna) throws SQLException {
        LocalDateTime f = rs.getObject(columna, LocalDateTime.class);
        return f != null ? f.format(F_FECHA_HORA) : Validaciones.fechaHoraActual();
    }

    // --- Conversión texto del modelo -> MySQL ---

    private static LocalDate aFecha(String texto) {
        return LocalDate.parse(texto, F_FECHA);
    }

    // =====================================================================
    // Carga inicial (SELECT)
    // =====================================================================

    public static ArrayList<Cliente> cargarClientes() throws SQLException {
        ArrayList<Cliente> lista = new ArrayList<>();
        try (Connection con = ConexionBD.obtenerConexion();
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery("SELECT * FROM clientes ORDER BY id_cliente")) {
            while (rs.next()) {
                try {
                    lista.add(Cliente.reconstruirDesdeArchivo(rs.getInt("id_cliente"), rs.getString("nombre"),
                            rs.getString("telefono"), rs.getString("email"), rs.getString("direccion"),
                            rs.getString("dni"), fechaDeDatetime(rs, "fecha_registro")));
                } catch (IllegalArgumentException e) {
                    System.out.println("[AVISO] clientes, ID " + rs.getInt("id_cliente") + " ignorado: " + e.getMessage());
                }
            }
        }
        System.out.println("[Clientes] " + lista.size() + " registro(s) cargado(s).");
        return lista;
    }

    public static ArrayList<Mascota> cargarMascotas() throws SQLException {
        ArrayList<Mascota> lista = new ArrayList<>();
        try (Connection con = ConexionBD.obtenerConexion()) {
            // Historial de peso agrupado por mascota
            Map<Integer, ArrayList<Double>> pesos = new HashMap<>();
            Map<Integer, ArrayList<String>> fechas = new HashMap<>();
            try (Statement st = con.createStatement();
                    ResultSet rs = st.executeQuery("SELECT * FROM historial_peso ORDER BY id_peso")) {
                while (rs.next()) {
                    int id = rs.getInt("id_mascota");
                    pesos.computeIfAbsent(id, k -> new ArrayList<>()).add(rs.getDouble("peso"));
                    fechas.computeIfAbsent(id, k -> new ArrayList<>()).add(fechaHoraDeDatetime(rs, "fecha"));
                }
            }
            try (Statement st = con.createStatement();
                    ResultSet rs = st.executeQuery("SELECT * FROM mascotas ORDER BY id_mascota")) {
                while (rs.next()) {
                    int id = rs.getInt("id_mascota");
                    try {
                        lista.add(Mascota.reconstruirDesdeArchivo(id, rs.getInt("id_cliente"), rs.getString("nombre"),
                                rs.getString("especie"), rs.getString("raza"), fechaDeDate(rs, "fecha_nacimiento"),
                                rs.getString("sexo"), rs.getBoolean("esterilizado"),
                                pesos.getOrDefault(id, new ArrayList<>()), fechas.getOrDefault(id, new ArrayList<>())));
                    } catch (IllegalArgumentException e) {
                        System.out.println("[AVISO] mascotas, ID " + id + " ignorada: " + e.getMessage());
                    }
                }
            }
        }
        System.out.println("[Mascotas] " + lista.size() + " registro(s) cargado(s).");
        return lista;
    }

    public static ArrayList<Personal> cargarPersonal() throws SQLException {
        ArrayList<Personal> lista = new ArrayList<>();
        try (Connection con = ConexionBD.obtenerConexion();
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery("SELECT * FROM personal ORDER BY id_empleado")) {
            while (rs.next()) {
                try {
                    lista.add(Personal.reconstruirDesdeArchivo(rs.getInt("id_empleado"), rs.getString("dni"),
                            rs.getString("nombre"), rs.getString("horario_atencion"), rs.getString("rol")));
                } catch (IllegalArgumentException e) {
                    System.out.println("[AVISO] personal, ID " + rs.getInt("id_empleado") + " ignorado: " + e.getMessage());
                }
            }
        }
        System.out.println("[Personal] " + lista.size() + " registro(s) cargado(s).");
        return lista;
    }

    public static ArrayList<Cita> cargarCitas() throws SQLException {
        ArrayList<Cita> lista = new ArrayList<>();
        // La cita guarda id_empleado; el modelo trabaja con el nombre del veterinario
        String sql = "SELECT c.id_cita, c.id_mascota, c.fecha, c.hora, c.tipo, c.estado, p.nombre AS veterinario "
                + "FROM citas c JOIN personal p ON p.id_empleado = c.id_empleado ORDER BY c.id_cita";
        try (Connection con = ConexionBD.obtenerConexion();
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                try {
                    LocalTime hora = rs.getObject("hora", LocalTime.class);
                    String fechaHora = fechaDeDate(rs, "fecha") + " " + F_HORA.format(hora).toLowerCase();
                    lista.add(Cita.reconstruirDesdeArchivo(rs.getInt("id_cita"), rs.getInt("id_mascota"),
                            fechaHora, rs.getString("veterinario"), rs.getString("tipo"), rs.getString("estado")));
                } catch (IllegalArgumentException e) {
                    System.out.println("[AVISO] citas, ID " + rs.getInt("id_cita") + " ignorada: " + e.getMessage());
                }
            }
        }
        System.out.println("[Citas] " + lista.size() + " registro(s) cargado(s).");
        return lista;
    }

    public static ArrayList<Consulta> cargarConsultas() throws SQLException {
        ArrayList<Consulta> lista = new ArrayList<>();
        try (Connection con = ConexionBD.obtenerConexion();
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery("SELECT * FROM consultas ORDER BY id_consulta")) {
            while (rs.next()) {
                try {
                    lista.add(Consulta.reconstruirDesdeArchivo(rs.getInt("id_consulta"), rs.getInt("id_mascota"),
                            rs.getInt("id_cita"), fechaHoraDeDatetime(rs, "fecha_hora"), rs.getString("motivo_consulta"),
                            rs.getString("veterinario"), rs.getString("diagnostico"),
                            rs.getString("tratamiento_indicado"), rs.getDouble("peso"), rs.getDouble("temperatura"),
                            rs.getString("observaciones"), rs.getString("proxima_cita_sugerida")));
                } catch (IllegalArgumentException e) {
                    System.out.println("[AVISO] consultas, ID " + rs.getInt("id_consulta") + " ignorada: " + e.getMessage());
                }
            }
        }
        System.out.println("[Consultas] " + lista.size() + " registro(s) cargado(s).");
        return lista;
    }

    public static ArrayList<Facturacion> cargarFacturas() throws SQLException {
        ArrayList<Facturacion> lista = new ArrayList<>();
        try (Connection con = ConexionBD.obtenerConexion();
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery("SELECT * FROM facturas ORDER BY id_factura")) {
            while (rs.next()) {
                try {
                    lista.add(Facturacion.reconstruirDesdeArchivo(rs.getInt("id_factura"), rs.getInt("id_cliente"),
                            rs.getInt("id_consulta"), fechaDeDatetime(rs, "fecha"), rs.getDouble("monto"),
                            rs.getString("metodo_pago")));
                } catch (IllegalArgumentException e) {
                    System.out.println("[AVISO] facturas, ID " + rs.getInt("id_factura") + " ignorada: " + e.getMessage());
                }
            }
        }
        System.out.println("[Facturas] " + lista.size() + " registro(s) cargado(s).");
        return lista;
    }

    // =====================================================================
    // Escritura (INSERT / UPDATE). Devuelven true si se guardó en MySQL.
    // =====================================================================

    // fecha_registro no se envía: MySQL pone la fecha del sistema (DEFAULT CURRENT_TIMESTAMP)
    public static boolean insertarCliente(Cliente c) {
        String sql = "INSERT INTO clientes (id_cliente, nombre, dni, telefono, email, direccion) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = ConexionBD.obtenerConexion(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, c.getIdCliente());
            ps.setString(2, c.getNombreCompleto());
            ps.setString(3, c.getDni());
            ps.setString(4, c.getTelefono());
            ps.setString(5, c.getEmail());
            ps.setString(6, c.getDireccion());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            return error("guardar el cliente", e);
        }
    }

    // Guarda la mascota y su peso inicial en una sola transacción (la fecha del peso la pone MySQL)
    public static boolean insertarMascota(Mascota m) {
        String sqlMascota = "INSERT INTO mascotas (id_mascota, id_cliente, nombre, especie, raza, "
                + "fecha_nacimiento, sexo, esterilizado) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        String sqlPeso = "INSERT INTO historial_peso (id_mascota, peso) VALUES (?, ?)";
        try (Connection con = ConexionBD.obtenerConexion()) {
            con.setAutoCommit(false);
            try (PreparedStatement ps = con.prepareStatement(sqlMascota);
                    PreparedStatement pp = con.prepareStatement(sqlPeso)) {
                ps.setInt(1, m.getIdMascota());
                ps.setInt(2, m.getIdCliente());
                ps.setString(3, m.getNombre());
                ps.setString(4, m.getEspecie());
                ps.setString(5, m.getRaza());
                ps.setObject(6, aFecha(m.getFechaNacimiento()));
                ps.setString(7, m.getSexo());
                ps.setBoolean(8, m.isEsterilizado());
                ps.executeUpdate();

                for (double peso : m.getHistorialPeso()) {
                    pp.setInt(1, m.getIdMascota());
                    pp.setDouble(2, peso);
                    pp.executeUpdate();
                }
                con.commit();
                return true;
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        } catch (SQLException e) {
            return error("guardar la mascota", e);
        }
    }

    public static boolean insertarPersonal(Personal p) {
        String sql = "INSERT INTO personal (id_empleado, dni, nombre, horario_atencion, rol) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = ConexionBD.obtenerConexion(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, p.getIdEmpleado());
            ps.setString(2, p.getDni());
            ps.setString(3, p.getNombre());
            ps.setString(4, p.getHorarioAtencion());
            ps.setString(5, p.getRol());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            return error("guardar el empleado", e);
        }
    }

    // La cita se guarda con fecha (DATE), hora (TIME) y el id del veterinario
    public static boolean insertarCita(Cita c, int idEmpleado) {
        String sql = "INSERT INTO citas (id_cita, id_mascota, fecha, hora, id_empleado, tipo, estado) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = ConexionBD.obtenerConexion(); PreparedStatement ps = con.prepareStatement(sql)) {
            String[] partes = c.getFechaHora().split(" ", 2); // "dd/MM/aaaa" y "hh:mm am"
            ps.setInt(1, c.getIdCita());
            ps.setInt(2, c.getIdMascota());
            ps.setObject(3, aFecha(partes[0]));
            ps.setObject(4, LocalTime.parse(partes[1].trim(), F_HORA));
            ps.setInt(5, idEmpleado);
            ps.setString(6, c.getTipo());
            ps.setString(7, c.getEstado());
            ps.executeUpdate();
            return true;
        } catch (SQLException | RuntimeException e) {
            return error("guardar la cita", e);
        }
    }

    public static boolean actualizarEstadoCita(int idCita, String nuevoEstado) {
        String sql = "UPDATE citas SET estado = ? WHERE id_cita = ?";
        try (Connection con = ConexionBD.obtenerConexion(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nuevoEstado);
            ps.setInt(2, idCita);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return error("actualizar la cita", e);
        }
    }

    // Completar una cita: guarda la consulta, el nuevo peso de la mascota y marca la cita como completada (transacción).
    // fecha_hora de la consulta y fecha del peso las pone MySQL (fecha del sistema).
    public static boolean registrarConsultaYCompletarCita(Consulta c) {
        String sqlConsulta = "INSERT INTO consultas (id_consulta, id_mascota, id_cita, motivo_consulta, "
                + "veterinario, diagnostico, tratamiento_indicado, peso, temperatura, observaciones, "
                + "proxima_cita_sugerida) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String sqlPeso = "INSERT INTO historial_peso (id_mascota, peso) VALUES (?, ?)";
        String sqlCita = "UPDATE citas SET estado = ? WHERE id_cita = ?";
        try (Connection con = ConexionBD.obtenerConexion()) {
            con.setAutoCommit(false);
            try (PreparedStatement ps = con.prepareStatement(sqlConsulta);
                    PreparedStatement pp = con.prepareStatement(sqlPeso);
                    PreparedStatement pc = con.prepareStatement(sqlCita)) {
                ps.setInt(1, c.getIdConsulta());
                ps.setInt(2, c.getIdMascota());
                ps.setInt(3, c.getIdCita());
                ps.setString(4, c.getMotivoConsulta());
                ps.setString(5, c.getVeterinario());
                ps.setString(6, c.getDiagnostico());
                ps.setString(7, c.getTratamientoIndicado());
                ps.setDouble(8, c.getPeso());
                ps.setDouble(9, c.getTemperatura());
                ps.setString(10, c.getObservaciones());
                ps.setString(11, c.getProximaCitaSugerida());
                ps.executeUpdate();

                pp.setInt(1, c.getIdMascota());
                pp.setDouble(2, c.getPeso());
                pp.executeUpdate();

                pc.setString(1, Cita.ESTADO_COMPLETADA);
                pc.setInt(2, c.getIdCita());
                pc.executeUpdate();

                con.commit();
                return true;
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        } catch (SQLException e) {
            return error("guardar la consulta", e);
        }
    }

    // fecha de la factura no se envía: MySQL pone la fecha del sistema
    public static boolean insertarFactura(Facturacion f) {
        String sql = "INSERT INTO facturas (id_factura, id_cliente, id_consulta, monto, metodo_pago) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (Connection con = ConexionBD.obtenerConexion(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, f.getIdFactura());
            ps.setInt(2, f.getIdCliente());
            ps.setInt(3, f.getIdConsulta());
            ps.setDouble(4, f.getMonto());
            ps.setString(5, f.getMetodoPago());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            return error("guardar la factura", e);
        }
    }

    private static boolean error(String accion, Exception e) {
        System.out.println("[ERROR MySQL] No se pudo " + accion + ": " + e.getMessage());
        return false;
    }
}
