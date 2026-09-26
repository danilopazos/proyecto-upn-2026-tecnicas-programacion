package persistencia;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

// Funciones de apoyo para leer/escribir CSV
public final class ArchivoUtil {

    public static final String SEPARADOR = "\\|";       // Para split() (regex)
    public static final String SEPARADOR_ESCRITURA = "|"; // Para join() al escribir

    private static final String MARCADOR_SEPARADOR = "&#124;";
    private static final String MARCADOR_SALTO_LINEA = "&#10;";

    private ArchivoUtil() {
        // No se instancia
    }

    // Escapa "|" y saltos de línea
    public static String escaparCampo(String valor) {
        if (valor == null) {
            return "";
        }
        return valor.replace("|", MARCADOR_SEPARADOR)
                .replace("\r\n", MARCADOR_SALTO_LINEA)
                .replace("\n", MARCADOR_SALTO_LINEA);
    }

    // Revierte el escape anterior
    public static String desescaparCampo(String valor) {
        if (valor == null) {
            return "";
        }
        return valor.replace(MARCADOR_SEPARADOR, "|").replace(MARCADOR_SALTO_LINEA, "\n");
    }

    // Une columnas en una línea
    public static String construirLinea(String... campos) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < campos.length; i++) {
            if (i > 0) {
                sb.append(SEPARADOR_ESCRITURA);
            }
            sb.append(escaparCampo(campos[i]));
        }
        return sb.toString();
    }

    // Escribe todas las líneas al archivo
    public static void escribirArchivo(String ruta, List<String> lineas) throws ArchivoDatosException {
        Path path = Paths.get(ruta);
        try {
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }
            try (BufferedWriter escritor = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
                for (String linea : lineas) {
                    escritor.write(linea);
                    escritor.newLine();
                }
            }
        } catch (IOException e) {
            throw new ArchivoDatosException(
                    "No se pudo escribir el archivo \"" + ruta + "\". Verifica permisos, espacio en disco "
                            + "o que el archivo no esté abierto en otro programa. Detalle: " + e.getMessage(), e);
        }
    }

    // Lee las líneas del archivo
    public static List<String> leerArchivo(String ruta) throws ArchivoDatosException {
        Path path = Paths.get(ruta);
        List<String> lineas = new ArrayList<>();
        if (!Files.exists(path)) {
            return lineas; // Aún no existe: normal
        }
        try (BufferedReader lector = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String linea;
            while ((linea = lector.readLine()) != null) {
                if (!linea.trim().isEmpty()) {
                    lineas.add(linea);
                }
            }
        } catch (IOException e) {
            throw new ArchivoDatosException(
                    "No se pudo leer el archivo \"" + ruta + "\". Verifica que exista y que tengas permisos "
                            + "de lectura. Detalle: " + e.getMessage(), e);
        }
        return lineas;
    }

    // Separa la línea en columnas
    public static String[] partirLinea(String linea, int columnasEsperadas) {
        String[] partes = linea.split(SEPARADOR, -1);
        String[] resultado = new String[partes.length];
        for (int i = 0; i < partes.length; i++) {
            resultado[i] = desescaparCampo(partes[i]);
        }
        return resultado;
    }
}
