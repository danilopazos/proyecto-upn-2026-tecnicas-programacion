package persistencia;

 // Excepción propia (checked) para toda condición de error relacionada con el manejo de archivos 
public class ArchivoDatosException extends Exception {

    public ArchivoDatosException(String mensaje) {
        super(mensaje);
    }

    public ArchivoDatosException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
