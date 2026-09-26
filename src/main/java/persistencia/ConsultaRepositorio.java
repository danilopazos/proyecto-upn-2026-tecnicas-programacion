package persistencia;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import modelo.Consulta;

public final class ConsultaRepositorio {

    private static final int COLUMNAS = 12;

    private ConsultaRepositorio() {
    }

    public static void guardar(ArrayList<Consulta> listaConsultas, String ruta) throws ArchivoDatosException {
        List<String> lineas = new ArrayList<>();
        for (Consulta c : listaConsultas) {
            lineas.add(ArchivoUtil.construirLinea(
                    String.valueOf(c.getIdConsulta()),
                    String.valueOf(c.getIdMascota()),
                    String.valueOf(c.getIdCita()),
                    c.getFechaHora(),
                    c.getMotivoConsulta(),
                    c.getVeterinario(),
                    c.getDiagnostico(),
                    c.getTratamientoIndicado(),
                    String.valueOf(c.getPeso()),
                    String.valueOf(c.getTemperatura()),
                    c.getObservaciones(),
                    c.getProximaCitaSugerida()
            ));
        }
        ArchivoUtil.escribirArchivo(ruta, lineas);
    }

    public static ArrayList<Consulta> cargar(String ruta) throws ArchivoDatosException {
        List<String> lineas = ArchivoUtil.leerArchivo(ruta);
        ArrayList<Consulta> lista = new ArrayList<>();
        int lineasIgnoradas = 0;

        for (int i = 0; i < lineas.size(); i++) {
            String linea = lineas.get(i);
            try {
                String[] c = ArchivoUtil.partirLinea(linea, COLUMNAS);
                if (c.length != COLUMNAS) {
                    throw new IllegalArgumentException("se esperaban " + COLUMNAS + " columnas y se encontraron " + c.length);
                }
                int idConsulta = Integer.parseInt(c[0].trim());
                int idMascota = Integer.parseInt(c[1].trim());
                int idCita = Integer.parseInt(c[2].trim());
                double peso = Double.parseDouble(c[8].trim().replace(",", ".").toLowerCase(Locale.ROOT));
                double temperatura = Double.parseDouble(c[9].trim().replace(",", ".").toLowerCase(Locale.ROOT));

                Consulta consulta = Consulta.reconstruirDesdeArchivo(idConsulta, idMascota, idCita, c[3], c[4], c[5],
                        c[6], c[7], peso, temperatura, c[10], c[11]);
                lista.add(consulta);
            } catch (NumberFormatException e) {
                lineasIgnoradas++;
                System.out.println("[AVISO] consultas.csv, línea " + (i + 1) + " ignorada: un ID, peso o temperatura no es un número válido.");
            } catch (IllegalArgumentException e) {
                lineasIgnoradas++;
                System.out.println("[AVISO] consultas.csv, línea " + (i + 1) + " ignorada: " + e.getMessage());
            }
        }

        System.out.println("[Consultas] " + lista.size() + " registro(s) cargado(s)"
                + (lineasIgnoradas > 0 ? ", " + lineasIgnoradas + " línea(s) ignorada(s) por formato inválido." : "."));
        return lista;
    }
}
