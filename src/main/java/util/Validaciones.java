package util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;


 // Reglas de validación centralizadas. Todas las clases del sistema
public final class Validaciones {

    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

    private Validaciones() {
        // Clase de utilidades: no se instancia
    }

    /** DNI: exactamente 8 dígitos numéricos. */
    public static boolean esDniValido(String dni) {
        return dni != null && dni.matches("\\d{8}");
    }

    /** Celular: exactamente 9 dígitos numéricos. */
    public static boolean esTelefonoValido(String telefono) {
        return telefono != null && telefono.matches("\\d{9}");
    }

    /** Correo con el formato habitual usuario@dominio.extensión. */
    public static boolean esEmailValido(String email) {
        return email != null
                && email.matches("[A-Za-z0-9_+%'-]+(?:[.][A-Za-z0-9_+%'-]+)*@[A-Za-z0-9]+(?:-[A-Za-z0-9]+)*(?:[.][A-Za-z0-9]+(?:-[A-Za-z0-9]+)*)*[.][A-Za-z]{2,}");
    }

    /** Nombre: letras (incluye tildes y ñ), espacios, guiones y apóstrofos. Sin números. */
    public static boolean esNombreValido(String nombre) {
        return nombre != null && nombre.trim().matches("[\\p{L}\\p{M}]+(?:[ '’-][\\p{L}\\p{M}]+)*");
    }

    /** Texto obligatorio: no nulo y no compuesto solo de espacios. */
    public static boolean esTextoNoVacio(String texto) {
        return texto != null && !texto.trim().isEmpty();
    }

    /** Fecha real en formato dd/MM/aaaa (rechaza 31/02, 29/02 en años no bisiestos, etc.). */
    public static boolean esFechaValida(String fecha) {
        if (fecha == null || !fecha.matches("[0-9]{2}/[0-9]{2}/[0-9]{4}")) {
            return false;
        }
        try {
            return LocalDate.parse(fecha, FORMATO_FECHA).getYear() > 0;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /** Fecha actual del sistema en formato dd/MM/aaaa. */
    public static String fechaActual() {
        return LocalDate.now().format(FORMATO_FECHA);
    }

    /** Fecha real dd/MM/aaaa que no es anterior al día de hoy. */
    public static boolean esFechaNoPasada(String fecha) {
        return esFechaValida(fecha) && !LocalDate.parse(fecha, FORMATO_FECHA).isBefore(LocalDate.now());
    }

    /** Hora en formato de 12 horas: hh:mm am / hh:mm pm (Ej: 10:00 am, 3:30 pm). */
    public static boolean esHoraValida(String hora) {
        return hora != null && hora.trim().matches("(?i)(0?[1-9]|1[0-2]):[0-5][0-9] ?[ap]\\.? ?m\\.?");
    }

    /** Deja la hora con un formato uniforme: "10:00 am". */
    public static String normalizarHora(String hora) {
        String limpia = hora.trim().toLowerCase().replace(".", "").replace(" ", "");
        String sufijo = limpia.substring(limpia.length() - 2);
        String numeros = limpia.substring(0, limpia.length() - 2);
        if (numeros.length() == 4) {
            numeros = "0" + numeros;
        }
        return numeros + " " + sufijo;
    }

    /** Un ID ingresado por teclado solo puede contener dígitos. */
    public static boolean esIdNumerico(String texto) {
        return texto != null && texto.trim().matches("\\d+");
    }

    /** Montos, pesos y temperaturas deben ser mayores que cero. */
    public static boolean esNumeroPositivo(double numero) {
        return numero > 0;
    }
}
