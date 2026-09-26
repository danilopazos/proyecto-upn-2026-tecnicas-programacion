package persistencia;

import modelo.Cliente;
import modelo.Mascota;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/** Almacena clientes y mascotas en UTF-8. Cada escritura reemplaza un archivo completo. */
public class RepositorioCSV {
    private static final List<String> CLIENTES = Arrays.asList("idCliente", "nombreCompleto", "telefono", "email", "direccion", "dni", "fechaRegistro");
    private static final List<String> MASCOTAS = Arrays.asList("idMascota", "idCliente", "nombre", "especie", "raza", "fechaNacimiento", "sexo", "esterilizado", "historialPeso", "fechasHistorialPeso");
    private final Path directorio;

    public RepositorioCSV(Path directorio) { this.directorio = directorio; }

    private List<List<String>> leer(String archivo, List<String> cabecera) throws IOException {
        Path ruta = directorio.resolve(archivo);
        String contenido;
        try {
            contenido = Files.readString(ruta, StandardCharsets.UTF_8);
        } catch (NoSuchFileException e) {
            return new ArrayList<>();
        }
        List<List<String>> filas = Csv.leer(contenido);
        if (filas.isEmpty() || !filas.remove(0).equals(cabecera)) {
            throw new IOException(archivo + ": cabecera ausente o incorrecta.");
        }
        for (int i = 0; i < filas.size(); i++) {
            if (filas.get(i).size() != cabecera.size()) {
                throw new IOException(archivo + ": número de columnas incorrecto en registro " + (i + 1));
            }
        }
        return filas;
    }

    public ArrayList<Cliente> cargarClientes() throws IOException {
        ArrayList<Cliente> resultado = new ArrayList<>();
        Set<Integer> ids = new HashSet<>();
        Set<String> dnis = new HashSet<>();
        int registro = 0;
        try {
            for (List<String> f : leer("clientes.csv", CLIENTES)) {
                registro++;
                int id = Integer.parseInt(f.get(0));
                if (!ids.add(id) || !dnis.add(f.get(5))) throw new IllegalArgumentException("ID o DNI duplicado.");
                resultado.add(new Cliente(id, f.get(1), f.get(2), f.get(3), f.get(4), f.get(5), f.get(6)));
            }
        } catch (IllegalArgumentException e) {
            throw new IOException("clientes.csv, registro " + registro + ": " + e.getMessage(), e);
        }
        return resultado;
    }

    public ArrayList<Mascota> cargarMascotas(ArrayList<Cliente> clientes) throws IOException {
        ArrayList<Mascota> resultado = new ArrayList<>();
        Set<Integer> ids = new HashSet<>();
        int registro = 0;
        try {
            for (List<String> f : leer("mascotas.csv", MASCOTAS)) {
                registro++;
                int id = Integer.parseInt(f.get(0));
                int propietario = Integer.parseInt(f.get(1));
                if (!ids.add(id)) throw new IllegalArgumentException("ID de mascota duplicado.");
                if (!Cliente.existeId(clientes, propietario)) throw new IllegalArgumentException("El propietario no existe.");
                if (f.get(3).trim().isEmpty()) throw new IllegalArgumentException("Especie vacía.");
                if (!f.get(7).equals("true") && !f.get(7).equals("false")) throw new IllegalArgumentException("Esterilizado debe ser true o false.");
                List<List<String>> pesos = Csv.leer(f.get(8));
                List<List<String>> fechas = Csv.leer(f.get(9));
                if (pesos.size() != 1 || fechas.size() != 1 || pesos.get(0).size() != fechas.get(0).size()) {
                    throw new IllegalArgumentException("Historial de peso incorrecto.");
                }
                ArrayList<Double> historial = new ArrayList<>();
                for (String valor : pesos.get(0)) {
                    double peso = Double.parseDouble(valor);
                    if (!Double.isFinite(peso) || peso <= 0) throw new IllegalArgumentException("Peso inválido.");
                    historial.add(peso);
                }
                Mascota m = new Mascota(id, propietario, f.get(2), f.get(3), f.get(4), f.get(5), f.get(6), historial.get(0), Boolean.parseBoolean(f.get(7)));
                m.getHistorialPeso().clear();
                m.getHistorialPeso().addAll(historial);
                m.getFechasHistorialPeso().clear();
                m.getFechasHistorialPeso().addAll(fechas.get(0));
                resultado.add(m);
            }
        } catch (IllegalArgumentException e) {
            throw new IOException("mascotas.csv, registro " + registro + ": " + e.getMessage(), e);
        } catch (IOException e) {
            throw new IOException("mascotas.csv, registro " + registro + ": " + e.getMessage(), e);
        }
        return resultado;
    }

    public void guardarClientes(List<Cliente> clientes) throws IOException {
        StringBuilder contenido = new StringBuilder(Csv.fila(CLIENTES));
        for (Cliente c : clientes) {
            contenido.append(Csv.fila(Arrays.asList(String.valueOf(c.getIdCliente()), c.getNombreCompleto(), c.getTelefono(), c.getEmail(), c.getDireccion(), c.getDni(), c.getFechaRegistro())));
        }
        escribir("clientes.csv", contenido.toString());
    }

    public void guardarMascotas(List<Mascota> mascotas) throws IOException {
        StringBuilder contenido = new StringBuilder(Csv.fila(MASCOTAS));
        for (Mascota m : mascotas) {
            List<String> pesos = new ArrayList<>();
            for (Double peso : m.getHistorialPeso()) pesos.add(peso.toString());
            contenido.append(Csv.fila(Arrays.asList(String.valueOf(m.getIdMascota()), String.valueOf(m.getIdCliente()), m.getNombre(), m.getEspecie(), m.getRaza(), m.getFechaNacimiento(), m.getSexo(), String.valueOf(m.getEsterilizado()), Csv.fila(pesos).stripTrailing(), Csv.fila(m.getFechasHistorialPeso()).stripTrailing())));
        }
        escribir("mascotas.csv", contenido.toString());
    }

    private void escribir(String archivo, String contenido) throws IOException {
        Files.createDirectories(directorio);
        Path temporal = Files.createTempFile(directorio, archivo, ".tmp");
        try {
            Files.writeString(temporal, contenido, StandardCharsets.UTF_8);
            // Si el sistema no admite reemplazo atómico, se informa el error y se conserva el original.
            Files.move(temporal, directorio.resolve(archivo), StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } finally {
            Files.deleteIfExists(temporal);
        }
    }
}
