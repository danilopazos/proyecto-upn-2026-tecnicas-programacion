package persistencia;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import modelo.Cita;
import modelo.Cliente;
import modelo.Consulta;
import modelo.Facturacion;
import modelo.Mascota;
import modelo.Personal;

// Guarda y carga las 6 entidades en CSV
public final class RepositorioArchivos {

    private RepositorioArchivos() {
        // No se instancia
    }

    // --- Clientes: guardar y cargar ---

    public static void guardarClientes(ArrayList<Cliente> lista, String ruta) throws ArchivoDatosException {
        List<String> lineas = new ArrayList<>();
        for (Cliente c : lista) {
            lineas.add(ArchivoUtil.construirLinea(String.valueOf(c.getIdCliente()), c.getNombreCompleto(),
                    c.getTelefono(), c.getEmail(), c.getDireccion(), c.getDni(), c.getFechaRegistro()));
        }
        ArchivoUtil.escribirArchivo(ruta, lineas);
    }

    public static ArrayList<Cliente> cargarClientes(String ruta) throws ArchivoDatosException {
        List<String> lineas = ArchivoUtil.leerArchivo(ruta);
        ArrayList<Cliente> lista = new ArrayList<>();
        int ignoradas = 0;
        for (int i = 0; i < lineas.size(); i++) {
            try {
                String[] c = ArchivoUtil.partirLinea(lineas.get(i), 7);
                if (c.length != 7) {
                    throw new IllegalArgumentException("se esperaban 7 columnas y se encontraron " + c.length);
                }
                lista.add(Cliente.reconstruirDesdeArchivo(Integer.parseInt(c[0].trim()), c[1], c[2], c[3], c[4], c[5], c[6]));
            } catch (NumberFormatException e) {
                ignoradas++;
                System.out.println("[AVISO] clientes.csv, línea " + (i + 1) + " ignorada: el ID no es un número válido.");
            } catch (IllegalArgumentException e) {
                ignoradas++;
                System.out.println("[AVISO] clientes.csv, línea " + (i + 1) + " ignorada: " + e.getMessage());
            }
        }
        System.out.println("[Clientes] " + lista.size() + " registro(s) cargado(s)"
                + (ignoradas > 0 ? ", " + ignoradas + " línea(s) ignorada(s) por formato inválido." : "."));
        return lista;
    }

    // --- Mascotas: guardar y cargar ---

    public static void guardarMascotas(ArrayList<Mascota> lista, String ruta) throws ArchivoDatosException {
        List<String> lineas = new ArrayList<>();
        for (Mascota m : lista) {
            lineas.add(ArchivoUtil.construirLinea(String.valueOf(m.getIdMascota()), String.valueOf(m.getIdCliente()),
                    m.getNombre(), m.getEspecie(), m.getRaza(), m.getFechaNacimiento(), m.getSexo(),
                    String.valueOf(m.isEsterilizado()), codificarHistorial(m)));
        }
        ArchivoUtil.escribirArchivo(ruta, lineas);
    }

    // Codifica el historial de peso
    private static String codificarHistorial(Mascota m) {
        StringBuilder sb = new StringBuilder();
        List<Double> pesos = m.getHistorialPeso();
        List<String> fechas = m.getFechasHistorialPeso();
        for (int i = 0; i < pesos.size(); i++) {
            if (i > 0) {
                sb.append(";");
            }
            String fecha = i < fechas.size() ? fechas.get(i) : "Sin fecha registrada";
            sb.append(pesos.get(i)).append("@").append(fecha);
        }
        return sb.toString();
    }

    public static ArrayList<Mascota> cargarMascotas(String ruta) throws ArchivoDatosException {
        List<String> lineas = ArchivoUtil.leerArchivo(ruta);
        ArrayList<Mascota> lista = new ArrayList<>();
        int ignoradas = 0;
        for (int i = 0; i < lineas.size(); i++) {
            try {
                String[] c = ArchivoUtil.partirLinea(lineas.get(i), 9);
                if (c.length != 9) {
                    throw new IllegalArgumentException("se esperaban 9 columnas y se encontraron " + c.length);
                }
                boolean esterilizado = Boolean.parseBoolean(c[7].trim());
                ArrayList<Double> pesos = new ArrayList<>();
                ArrayList<String> fechas = new ArrayList<>();
                decodificarHistorial(c[8], pesos, fechas);
                lista.add(Mascota.reconstruirDesdeArchivo(Integer.parseInt(c[0].trim()), Integer.parseInt(c[1].trim()),
                        c[2], c[3], c[4], c[5], c[6], esterilizado, pesos, fechas));
            } catch (NumberFormatException e) {
                ignoradas++;
                System.out.println("[AVISO] mascotas.csv, línea " + (i + 1) + " ignorada: un ID o peso no es un número válido.");
            } catch (IllegalArgumentException e) {
                ignoradas++;
                System.out.println("[AVISO] mascotas.csv, línea " + (i + 1) + " ignorada: " + e.getMessage());
            }
        }
        System.out.println("[Mascotas] " + lista.size() + " registro(s) cargado(s)"
                + (ignoradas > 0 ? ", " + ignoradas + " línea(s) ignorada(s) por formato inválido." : "."));
        return lista;
    }

    // Decodifica el historial de peso
    private static void decodificarHistorial(String texto, ArrayList<Double> pesos, ArrayList<String> fechas) {
        if (texto == null || texto.trim().isEmpty()) {
            throw new IllegalArgumentException("el historial de peso está vacío.");
        }
        for (String par : texto.split(";", -1)) {
            int pos = par.indexOf('@');
            if (pos < 0) {
                throw new IllegalArgumentException("el historial de peso tiene un formato inválido (\"" + par + "\").");
            }
            pesos.add(Double.parseDouble(par.substring(0, pos).trim().replace(",", ".").toLowerCase(Locale.ROOT)));
            fechas.add(par.substring(pos + 1));
        }
    }

    // --- Personal: guardar y cargar ---

    public static void guardarPersonal(ArrayList<Personal> lista, String ruta) throws ArchivoDatosException {
        List<String> lineas = new ArrayList<>();
        for (Personal p : lista) {
            lineas.add(ArchivoUtil.construirLinea(String.valueOf(p.getIdEmpleado()), p.getNombre(),
                    p.getHorarioAtencion(), p.getRol()));
        }
        ArchivoUtil.escribirArchivo(ruta, lineas);
    }

    public static ArrayList<Personal> cargarPersonal(String ruta) throws ArchivoDatosException {
        List<String> lineas = ArchivoUtil.leerArchivo(ruta);
        ArrayList<Personal> lista = new ArrayList<>();
        int ignoradas = 0;
        for (int i = 0; i < lineas.size(); i++) {
            try {
                String[] c = ArchivoUtil.partirLinea(lineas.get(i), 4);
                if (c.length != 4) {
                    throw new IllegalArgumentException("se esperaban 4 columnas y se encontraron " + c.length);
                }
                lista.add(Personal.reconstruirDesdeArchivo(Integer.parseInt(c[0].trim()), c[1], c[2], c[3]));
            } catch (NumberFormatException e) {
                ignoradas++;
                System.out.println("[AVISO] personal.csv, línea " + (i + 1) + " ignorada: el ID no es un número válido.");
            } catch (IllegalArgumentException e) {
                ignoradas++;
                System.out.println("[AVISO] personal.csv, línea " + (i + 1) + " ignorada: " + e.getMessage());
            }
        }
        System.out.println("[Personal] " + lista.size() + " registro(s) cargado(s)"
                + (ignoradas > 0 ? ", " + ignoradas + " línea(s) ignorada(s) por formato inválido." : "."));
        return lista;
    }

    // --- Citas: guardar y cargar ---

    public static void guardarCitas(ArrayList<Cita> lista, String ruta) throws ArchivoDatosException {
        List<String> lineas = new ArrayList<>();
        for (Cita c : lista) {
            lineas.add(ArchivoUtil.construirLinea(String.valueOf(c.getIdCita()), String.valueOf(c.getIdMascota()),
                    c.getFechaHora(), c.getVeterinario(), c.getTipo(), c.getEstado()));
        }
        ArchivoUtil.escribirArchivo(ruta, lineas);
    }

    public static ArrayList<Cita> cargarCitas(String ruta) throws ArchivoDatosException {
        List<String> lineas = ArchivoUtil.leerArchivo(ruta);
        ArrayList<Cita> lista = new ArrayList<>();
        int ignoradas = 0;
        for (int i = 0; i < lineas.size(); i++) {
            try {
                String[] c = ArchivoUtil.partirLinea(lineas.get(i), 6);
                if (c.length != 6) {
                    throw new IllegalArgumentException("se esperaban 6 columnas y se encontraron " + c.length);
                }
                lista.add(Cita.reconstruirDesdeArchivo(Integer.parseInt(c[0].trim()), Integer.parseInt(c[1].trim()),
                        c[2], c[3], c[4], c[5]));
            } catch (NumberFormatException e) {
                ignoradas++;
                System.out.println("[AVISO] citas.csv, línea " + (i + 1) + " ignorada: un ID no es un número válido.");
            } catch (IllegalArgumentException e) {
                ignoradas++;
                System.out.println("[AVISO] citas.csv, línea " + (i + 1) + " ignorada: " + e.getMessage());
            }
        }
        System.out.println("[Citas] " + lista.size() + " registro(s) cargado(s)"
                + (ignoradas > 0 ? ", " + ignoradas + " línea(s) ignorada(s) por formato inválido." : "."));
        return lista;
    }

    // --- Consultas: guardar y cargar ---

    public static void guardarConsultas(ArrayList<Consulta> lista, String ruta) throws ArchivoDatosException {
        List<String> lineas = new ArrayList<>();
        for (Consulta c : lista) {
            lineas.add(ArchivoUtil.construirLinea(String.valueOf(c.getIdConsulta()), String.valueOf(c.getIdMascota()),
                    String.valueOf(c.getIdCita()), c.getFechaHora(), c.getMotivoConsulta(), c.getVeterinario(),
                    c.getDiagnostico(), c.getTratamientoIndicado(), String.valueOf(c.getPeso()),
                    String.valueOf(c.getTemperatura()), c.getObservaciones(), c.getProximaCitaSugerida()));
        }
        ArchivoUtil.escribirArchivo(ruta, lineas);
    }

    public static ArrayList<Consulta> cargarConsultas(String ruta) throws ArchivoDatosException {
        List<String> lineas = ArchivoUtil.leerArchivo(ruta);
        ArrayList<Consulta> lista = new ArrayList<>();
        int ignoradas = 0;
        for (int i = 0; i < lineas.size(); i++) {
            try {
                String[] c = ArchivoUtil.partirLinea(lineas.get(i), 12);
                if (c.length != 12) {
                    throw new IllegalArgumentException("se esperaban 12 columnas y se encontraron " + c.length);
                }
                double peso = Double.parseDouble(c[8].trim().replace(",", ".").toLowerCase(Locale.ROOT));
                double temp = Double.parseDouble(c[9].trim().replace(",", ".").toLowerCase(Locale.ROOT));
                lista.add(Consulta.reconstruirDesdeArchivo(Integer.parseInt(c[0].trim()), Integer.parseInt(c[1].trim()),
                        Integer.parseInt(c[2].trim()), c[3], c[4], c[5], c[6], c[7], peso, temp, c[10], c[11]));
            } catch (NumberFormatException e) {
                ignoradas++;
                System.out.println("[AVISO] consultas.csv, línea " + (i + 1) + " ignorada: un ID, peso o temperatura no es un número válido.");
            } catch (IllegalArgumentException e) {
                ignoradas++;
                System.out.println("[AVISO] consultas.csv, línea " + (i + 1) + " ignorada: " + e.getMessage());
            }
        }
        System.out.println("[Consultas] " + lista.size() + " registro(s) cargado(s)"
                + (ignoradas > 0 ? ", " + ignoradas + " línea(s) ignorada(s) por formato inválido." : "."));
        return lista;
    }

    // --- Facturas: guardar y cargar ---

    public static void guardarFacturas(ArrayList<Facturacion> lista, String ruta) throws ArchivoDatosException {
        List<String> lineas = new ArrayList<>();
        for (Facturacion f : lista) {
            lineas.add(ArchivoUtil.construirLinea(String.valueOf(f.getIdFactura()), String.valueOf(f.getIdCliente()),
                    String.valueOf(f.getIdConsulta()), f.getFecha(), String.valueOf(f.getMonto()), f.getMetodoPago()));
        }
        ArchivoUtil.escribirArchivo(ruta, lineas);
    }

    public static ArrayList<Facturacion> cargarFacturas(String ruta) throws ArchivoDatosException {
        List<String> lineas = ArchivoUtil.leerArchivo(ruta);
        ArrayList<Facturacion> lista = new ArrayList<>();
        int ignoradas = 0;
        for (int i = 0; i < lineas.size(); i++) {
            try {
                String[] c = ArchivoUtil.partirLinea(lineas.get(i), 6);
                if (c.length != 6) {
                    throw new IllegalArgumentException("se esperaban 6 columnas y se encontraron " + c.length);
                }
                double monto = Double.parseDouble(c[4].trim().replace(",", ".").toLowerCase(Locale.ROOT));
                lista.add(Facturacion.reconstruirDesdeArchivo(Integer.parseInt(c[0].trim()), Integer.parseInt(c[1].trim()),
                        Integer.parseInt(c[2].trim()), c[3], monto, c[5]));
            } catch (NumberFormatException e) {
                ignoradas++;
                System.out.println("[AVISO] facturas.csv, línea " + (i + 1) + " ignorada: un ID o el monto no es un número válido.");
            } catch (IllegalArgumentException e) {
                ignoradas++;
                System.out.println("[AVISO] facturas.csv, línea " + (i + 1) + " ignorada: " + e.getMessage());
            }
        }
        System.out.println("[Facturas] " + lista.size() + " registro(s) cargado(s)"
                + (ignoradas > 0 ? ", " + ignoradas + " línea(s) ignorada(s) por formato inválido." : "."));
        return lista;
    }
}
