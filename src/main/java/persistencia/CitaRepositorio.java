package persistencia;

import java.util.ArrayList;
import java.util.List;
import modelo.Cita;

public final class CitaRepositorio {

    private static final int COLUMNAS = 6;

    private CitaRepositorio() {
    }

    public static void guardar(ArrayList<Cita> listaCitas, String ruta) throws ArchivoDatosException {
        List<String> lineas = new ArrayList<>();
        for (Cita cita : listaCitas) {
            lineas.add(ArchivoUtil.construirLinea(
                    String.valueOf(cita.getIdCita()),
                    String.valueOf(cita.getIdMascota()),
                    cita.getFechaHora(),
                    cita.getVeterinario(),
                    cita.getTipo(),
                    cita.getEstado()
            ));
        }
        ArchivoUtil.escribirArchivo(ruta, lineas);
    }

    public static ArrayList<Cita> cargar(String ruta) throws ArchivoDatosException {
        List<String> lineas = ArchivoUtil.leerArchivo(ruta);
        ArrayList<Cita> lista = new ArrayList<>();
        int lineasIgnoradas = 0;

        for (int i = 0; i < lineas.size(); i++) {
            String linea = lineas.get(i);
            try {
                String[] c = ArchivoUtil.partirLinea(linea, COLUMNAS);
                if (c.length != COLUMNAS) {
                    throw new IllegalArgumentException("se esperaban " + COLUMNAS + " columnas y se encontraron " + c.length);
                }
                int idCita = Integer.parseInt(c[0].trim());
                int idMascota = Integer.parseInt(c[1].trim());
                Cita cita = Cita.reconstruirDesdeArchivo(idCita, idMascota, c[2], c[3], c[4], c[5]);
                lista.add(cita);
            } catch (NumberFormatException e) {
                lineasIgnoradas++;
                System.out.println("[AVISO] citas.csv, línea " + (i + 1) + " ignorada: un ID no es un número válido.");
            } catch (IllegalArgumentException e) {
                lineasIgnoradas++;
                System.out.println("[AVISO] citas.csv, línea " + (i + 1) + " ignorada: " + e.getMessage());
            }
        }

        System.out.println("[Citas] " + lista.size() + " registro(s) cargado(s)"
                + (lineasIgnoradas > 0 ? ", " + lineasIgnoradas + " línea(s) ignorada(s) por formato inválido." : "."));
        return lista;
    }
}
