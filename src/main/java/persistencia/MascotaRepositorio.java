package persistencia;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import modelo.Mascota;

 //(formato CSV con separador "|"). Columnas:
 //idMascota|idCliente|nombre|especie|raza|fechaNacimiento|sexo|esterilizado|historialPeso

public final class MascotaRepositorio {

    private static final int COLUMNAS = 9;

    private MascotaRepositorio() {
    }

    public static void guardar(ArrayList<Mascota> listaMascotas, String ruta) throws ArchivoDatosException {
        List<String> lineas = new ArrayList<>();
        for (Mascota m : listaMascotas) {
            lineas.add(ArchivoUtil.construirLinea(
                    String.valueOf(m.getIdMascota()),
                    String.valueOf(m.getIdCliente()),
                    m.getNombre(),
                    m.getEspecie(),
                    m.getRaza(),
                    m.getFechaNacimiento(),
                    m.getSexo(),
                    String.valueOf(m.isEsterilizado()),
                    codificarHistorial(m)
            ));
        }
        ArchivoUtil.escribirArchivo(ruta, lineas);
    }

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

    public static ArrayList<Mascota> cargar(String ruta) throws ArchivoDatosException {
        List<String> lineas = ArchivoUtil.leerArchivo(ruta);
        ArrayList<Mascota> lista = new ArrayList<>();
        int lineasIgnoradas = 0;

        for (int i = 0; i < lineas.size(); i++) {
            String linea = lineas.get(i);
            try {
                String[] c = ArchivoUtil.partirLinea(linea, COLUMNAS);
                if (c.length != COLUMNAS) {
                    throw new IllegalArgumentException("se esperaban " + COLUMNAS + " columnas y se encontraron " + c.length);
                }
                int idMascota = Integer.parseInt(c[0].trim());
                int idCliente = Integer.parseInt(c[1].trim());
                boolean esterilizado = Boolean.parseBoolean(c[7].trim());
                ArrayList<Double> historialPeso = new ArrayList<>();
                ArrayList<String> fechasHistorial = new ArrayList<>();
                decodificarHistorial(c[8], historialPeso, fechasHistorial);

                Mascota mascota = Mascota.reconstruirDesdeArchivo(idMascota, idCliente, c[2], c[3], c[4], c[5], c[6],
                        esterilizado, historialPeso, fechasHistorial);
                lista.add(mascota);
            } catch (NumberFormatException e) {
                lineasIgnoradas++;
                System.out.println("[AVISO] mascotas.csv, línea " + (i + 1) + " ignorada: un ID o peso no es un número válido.");
            } catch (IllegalArgumentException e) {
                lineasIgnoradas++;
                System.out.println("[AVISO] mascotas.csv, línea " + (i + 1) + " ignorada: " + e.getMessage());
            }
        }

        System.out.println("[Mascotas] " + lista.size() + " registro(s) cargado(s)"
                + (lineasIgnoradas > 0 ? ", " + lineasIgnoradas + " línea(s) ignorada(s) por formato inválido." : "."));
        return lista;
    }

    private static void decodificarHistorial(String texto, ArrayList<Double> pesos, ArrayList<String> fechas) {
        if (texto == null || texto.trim().isEmpty()) {
            throw new IllegalArgumentException("el historial de peso está vacío.");
        }
        String[] pares = texto.split(";", -1);
        for (String par : pares) {
            int posArroba = par.indexOf('@');
            if (posArroba < 0) {
                throw new IllegalArgumentException("el historial de peso tiene un formato inválido (\"" + par + "\").");
            }
            String pesoTexto = par.substring(0, posArroba);
            String fecha = par.substring(posArroba + 1);
            double peso = Double.parseDouble(pesoTexto.trim().replace(",", ".").toLowerCase(Locale.ROOT));
            pesos.add(peso);
            fechas.add(fecha);
        }
    }
}
