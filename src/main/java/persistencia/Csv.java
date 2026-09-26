package persistencia;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** CSV con comas, comillas dobles escapadas y campos de varias líneas. */
public final class Csv {
    private Csv() { }

    public static String fila(List<String> campos) {
        List<String> escapados = new ArrayList<>();
        for (String campo : campos) {
            escapados.add("\"" + campo.replace("\"", "\"\"") + "\"");
        }
        return String.join(",", escapados) + "\n";
    }

    public static List<List<String>> leer(String texto) throws IOException {
        List<List<String>> filas = new ArrayList<>();
        List<String> fila = new ArrayList<>();
        StringBuilder campo = new StringBuilder();
        boolean entreComillas = false;
        boolean cerrado = false;
        if (texto.startsWith("\uFEFF")) texto = texto.substring(1);
        for (int i = 0; i < texto.length(); i++) {
            char c = texto.charAt(i);
            if (entreComillas) {
                if (c == '"') {
                    if (i + 1 < texto.length() && texto.charAt(i + 1) == '"') {
                        campo.append('"');
                        i++;
                    } else {
                        entreComillas = false;
                        cerrado = true;
                    }
                } else campo.append(c);
            } else if (c == ',' || c == '\n' || c == '\r') {
                fila.add(campo.toString());
                campo.setLength(0);
                cerrado = false;
                if (c != ',') {
                    filas.add(fila);
                    fila = new ArrayList<>();
                    if (c == '\r' && i + 1 < texto.length() && texto.charAt(i + 1) == '\n') i++;
                }
            } else if (c == '"' && campo.length() == 0 && !cerrado) {
                entreComillas = true;
            } else {
                if (cerrado || c == '"') throw new IOException("Comillas incorrectas en CSV.");
                campo.append(c);
            }
        }
        if (entreComillas) throw new IOException("Campo CSV con comillas sin cerrar.");
        if (cerrado || campo.length() > 0 || !fila.isEmpty()) {
            fila.add(campo.toString());
            filas.add(fila);
        }
        return filas;
    }
}
