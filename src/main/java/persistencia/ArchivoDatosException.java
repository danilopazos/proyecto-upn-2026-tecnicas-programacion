package persistencia;

// Error propio de lectura/escritura de archivos
public class ArchivoDatosException extends Exception {

    public ArchivoDatosException(String mensaje) {
        super(mensaje);
    }

    public ArchivoDatosException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
