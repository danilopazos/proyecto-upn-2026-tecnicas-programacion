package persistencia;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import modelo.Facturacion;

public final class FacturacionRepositorio {

    private static final int COLUMNAS = 6;

    private FacturacionRepositorio() {
    }

    public static void guardar(ArrayList<Facturacion> listaFacturas, String ruta) throws ArchivoDatosException {
        List<String> lineas = new ArrayList<>();
        for (Facturacion f : listaFacturas) {
            lineas.add(ArchivoUtil.construirLinea(
                    String.valueOf(f.getIdFactura()),
                    String.valueOf(f.getIdCliente()),
                    String.valueOf(f.getIdConsulta()),
                    f.getFecha(),
                    String.valueOf(f.getMonto()),
                    f.getMetodoPago()
            ));
        }
        ArchivoUtil.escribirArchivo(ruta, lineas);
    }

    public static ArrayList<Facturacion> cargar(String ruta) throws ArchivoDatosException {
        List<String> lineas = ArchivoUtil.leerArchivo(ruta);
        ArrayList<Facturacion> lista = new ArrayList<>();
        int lineasIgnoradas = 0;

        for (int i = 0; i < lineas.size(); i++) {
            String linea = lineas.get(i);
            try {
                String[] c = ArchivoUtil.partirLinea(linea, COLUMNAS);
                if (c.length != COLUMNAS) {
                    throw new IllegalArgumentException("se esperaban " + COLUMNAS + " columnas y se encontraron " + c.length);
                }
                int idFactura = Integer.parseInt(c[0].trim());
                int idCliente = Integer.parseInt(c[1].trim());
                int idConsulta = Integer.parseInt(c[2].trim());
                double monto = Double.parseDouble(c[4].trim().replace(",", ".").toLowerCase(Locale.ROOT));

                Facturacion factura = Facturacion.reconstruirDesdeArchivo(idFactura, idCliente, idConsulta, c[3], monto, c[5]);
                lista.add(factura);
            } catch (NumberFormatException e) {
                lineasIgnoradas++;
                System.out.println("[AVISO] facturas.csv, línea " + (i + 1) + " ignorada: un ID o el monto no es un número válido.");
            } catch (IllegalArgumentException e) {
                lineasIgnoradas++;
                System.out.println("[AVISO] facturas.csv, línea " + (i + 1) + " ignorada: " + e.getMessage());
            }
        }

        System.out.println("[Facturas] " + lista.size() + " registro(s) cargado(s)"
                + (lineasIgnoradas > 0 ? ", " + lineasIgnoradas + " línea(s) ignorada(s) por formato inválido." : "."));
        return lista;
    }
}
