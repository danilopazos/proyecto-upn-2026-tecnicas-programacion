package persistencia;

import java.util.ArrayList;
import java.util.List;
import modelo.Personal;

public final class PersonalRepositorio {

    private static final int COLUMNAS = 4;

    private PersonalRepositorio() {
    }

    public static void guardar(ArrayList<Personal> listaPersonal, String ruta) throws ArchivoDatosException {
        List<String> lineas = new ArrayList<>();
        for (Personal p : listaPersonal) {
            lineas.add(ArchivoUtil.construirLinea(
                    String.valueOf(p.getIdEmpleado()),
                    p.getNombre(),
                    p.getHorarioAtencion(),
                    p.getRol()
            ));
        }
        ArchivoUtil.escribirArchivo(ruta, lineas);
    }

    public static ArrayList<Personal> cargar(String ruta) throws ArchivoDatosException {
        List<String> lineas = ArchivoUtil.leerArchivo(ruta);
        ArrayList<Personal> lista = new ArrayList<>();
        int lineasIgnoradas = 0;

        for (int i = 0; i < lineas.size(); i++) {
            String linea = lineas.get(i);
            try {
                String[] c = ArchivoUtil.partirLinea(linea, COLUMNAS);
                if (c.length != COLUMNAS) {
                    throw new IllegalArgumentException("se esperaban " + COLUMNAS + " columnas y se encontraron " + c.length);
                }
                int idEmpleado = Integer.parseInt(c[0].trim());
                Personal p = Personal.reconstruirDesdeArchivo(idEmpleado, c[1], c[2], c[3]);
                lista.add(p);
            } catch (NumberFormatException e) {
                lineasIgnoradas++;
                System.out.println("[AVISO] personal.csv, línea " + (i + 1) + " ignorada: el ID no es un número válido.");
            } catch (IllegalArgumentException e) {
                lineasIgnoradas++;
                System.out.println("[AVISO] personal.csv, línea " + (i + 1) + " ignorada: " + e.getMessage());
            }
        }

        System.out.println("[Personal] " + lista.size() + " registro(s) cargado(s)"
                + (lineasIgnoradas > 0 ? ", " + lineasIgnoradas + " línea(s) ignorada(s) por formato inválido." : "."));
        return lista;
    }
}
