package util;

import java.util.List;
import java.util.Scanner;
import java.util.function.Predicate;

/**
 * Lectura de datos por consola con control de errores.
 * Cada método repite la pregunta hasta que el usuario ingresa un valor válido,
 * así Main no necesita escribir sus propios bucles do-while ni try-catch.
 */
public class EntradaDatos {

    private final Scanner scanner;

    public EntradaDatos(Scanner scanner) {
        this.scanner = scanner;
    }

    // --- Lectura base ---

    public String leerTexto(String mensaje) {
        System.out.print(mensaje);
        return scanner.nextLine().trim();
    }

    /** Pide un texto hasta que cumpla la regla indicada. */
    public String leerValidado(String mensaje, Predicate<String> regla, String mensajeError) {
        while (true) {
            String valor = leerTexto(mensaje);
            if (regla.test(valor)) {
                return valor;
            }
            System.out.println("[ERROR] " + mensajeError);
        }
    }

    // --- Datos específicos (reutilizan las reglas de Validaciones) ---

    public String leerTextoObligatorio(String mensaje, String campo) {
        return leerValidado(mensaje, Validaciones::esTextoNoVacio, campo + " no puede estar vacío.");
    }

    public String leerDni(String mensaje) {
        return leerValidado(mensaje, Validaciones::esDniValido,
                "El DNI debe tener exactamente 8 dígitos numéricos.");
    }

    public String leerNombre(String mensaje) {
        return leerValidado(mensaje, Validaciones::esNombreValido,
                "El nombre debe contener solo letras y puede incluir espacios o apóstrofos.");
    }

    public String leerTelefono(String mensaje) {
        return leerValidado(mensaje, Validaciones::esTelefonoValido,
                "El celular debe tener 9 dígitos numéricos.");
    }

    public String leerEmail(String mensaje) {
        return leerValidado(mensaje, Validaciones::esEmailValido,
                "Ingresa un correo válido (Ej: nombre@dominio.com).");
    }

    public String leerFecha(String mensaje) {
        return leerValidado(mensaje, Validaciones::esFechaValida,
                "Ingresa una fecha real en formato dd/MM/aaaa.");
    }

    public String leerHora(String mensaje) {
        String hora = leerValidado(mensaje, Validaciones::esHoraValida,
                "Ingresa una hora válida en formato hh:mm am/pm (Ej: 10:00 am).");
        return Validaciones.normalizarHora(hora);
    }

    /** Pide un número decimal mayor que 0 (peso, temperatura, monto...). */
    public double leerDecimalPositivo(String mensaje, String campo, String ejemplo) {
        while (true) {
            String texto = leerTexto(mensaje).replace(",", ".");
            try {
                double numero = Double.parseDouble(texto);
                if (Validaciones.esNumeroPositivo(numero)) {
                    return numero;
                }
                System.out.println("[ERROR] " + campo + " debe ser un número mayor que 0.");
            } catch (NumberFormatException e) {
                System.out.println("[ERROR] Ingresa un valor numérico válido (Ej: " + ejemplo + ").");
            }
        }
    }

    /** Pide un ID que obligatoriamente esté dentro de la lista mostrada al usuario. */
    public int leerIdDeLista(String mensaje, List<Integer> idsPermitidos, String mensajeError) {
        while (true) {
            String texto = leerTexto(mensaje);
            if (!Validaciones.esIdNumerico(texto)) {
                System.out.println("[ERROR] El ID debe contener solo números.");
                continue;
            }
            int id = Integer.parseInt(texto);
            if (idsPermitidos.contains(id)) {
                return id;
            }
            System.out.println("[ERROR] " + mensajeError);
        }
    }

    /** Muestra un menú numerado y devuelve la opción elegida (de 1 a opciones.length). */
    public int leerOpcion(String titulo, String... opciones) {
        while (true) {
            System.out.println(titulo);
            for (int i = 0; i < opciones.length; i++) {
                System.out.println("  " + (i + 1) + " - " + opciones[i]);
            }
            String texto = leerTexto("Elige una opción: ");
            if (Validaciones.esIdNumerico(texto)) {
                int opcion = Integer.parseInt(texto);
                if (opcion >= 1 && opcion <= opciones.length) {
                    return opcion;
                }
            }
            System.out.println("[ERROR] Opción inválida. Elige un número entre 1 y " + opciones.length + ".");
        }
    }

    /** Opción del menú principal: devuelve -1 si el usuario no escribió un número. */
    public int leerOpcionMenu(String mensaje) {
        String texto = leerTexto(mensaje);
        return Validaciones.esIdNumerico(texto) ? Integer.parseInt(texto) : -1;
    }

    public boolean leerSiNo(String mensaje) {
        String respuesta = leerValidado(mensaje, t -> t.equalsIgnoreCase("S") || t.equalsIgnoreCase("N"),
                "Responde S (sí) o N (no).");
        return respuesta.equalsIgnoreCase("S");
    }

    public void pausar() {
        System.out.print("\nPresione ENTER para volver al menú principal...");
        scanner.nextLine();
    }
}
