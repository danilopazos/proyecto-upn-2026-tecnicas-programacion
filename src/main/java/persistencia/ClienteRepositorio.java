package persistencia;

import java.util.ArrayList;
import java.util.List;
import modelo.Cliente;

public final class ClienteRepositorio {

    private static final int COLUMNAS = 7;

    private ClienteRepositorio() {
    }

    public static void guardar(ArrayList<Cliente> listaClientes, String ruta) throws ArchivoDatosException {
        List<String> lineas = new ArrayList<>();
        for (Cliente c : listaClientes) {
            lineas.add(ArchivoUtil.construirLinea(
                    String.valueOf(c.getIdCliente()),
                    c.getNombreCompleto(),
                    c.getTelefono(),
                    c.getEmail(),
                    c.getDireccion(),
                    c.getDni(),
                    c.getFechaRegistro()
            ));
        }
        ArchivoUtil.escribirArchivo(ruta, lineas);
    }

    public static ArrayList<Cliente> cargar(String ruta) throws ArchivoDatosException {
        List<String> lineas = ArchivoUtil.leerArchivo(ruta);
        ArrayList<Cliente> lista = new ArrayList<>();
        int lineasIgnoradas = 0;

        for (int i = 0; i < lineas.size(); i++) {
            String linea = lineas.get(i);
            try {
                String[] c = ArchivoUtil.partirLinea(linea, COLUMNAS);
                if (c.length != COLUMNAS) {
                    throw new IllegalArgumentException("se esperaban " + COLUMNAS + " columnas y se encontraron " + c.length);
                }
                int idCliente = Integer.parseInt(c[0].trim());
                Cliente cliente = Cliente.reconstruirDesdeArchivo(idCliente, c[1], c[2], c[3], c[4], c[5], c[6]);
                lista.add(cliente);
            } catch (NumberFormatException e) {
                lineasIgnoradas++;
                System.out.println("[AVISO] clientes.csv, línea " + (i + 1) + " ignorada: el ID no es un número válido.");
            } catch (IllegalArgumentException e) {
                lineasIgnoradas++;
                System.out.println("[AVISO] clientes.csv, línea " + (i + 1) + " ignorada: " + e.getMessage());
            }
        }

        System.out.println("[Clientes] " + lista.size() + " registro(s) cargado(s)"
                + (lineasIgnoradas > 0 ? ", " + lineasIgnoradas + " línea(s) ignorada(s) por formato inválido." : "."));
        return lista;
    }
}
