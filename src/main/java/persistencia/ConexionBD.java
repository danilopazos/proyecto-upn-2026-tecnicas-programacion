package persistencia;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// Conexión JDBC a MySQL
public final class ConexionBD {

    private static final String BASE_DATOS = "veterinaria";
    private static final String URL = "jdbc:mysql://localhost:3306/" + BASE_DATOS
            + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USUARIO = "root";
    private static final String CLAVE = "root"; //

    private ConexionBD() {
        // No se instancia
    }

    // Abre una conexión nueva. Quien la use debe cerrarla (try-with-resources).
    public static Connection obtenerConexion() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, CLAVE);
    }

    // Comprueba que MySQL responde; devuelve true si la conexión fue exitosa.
    public static boolean probarConexion() {
        try (Connection con = obtenerConexion()) {
            System.out.println("[MySQL] Conexión exitosa a la base de datos '" + con.getCatalog() + "'.");
            return true;
        } catch (SQLException e) {
            System.out.println("[MySQL] No se pudo conectar: " + e.getMessage());
            return false;
        }
    }
}
