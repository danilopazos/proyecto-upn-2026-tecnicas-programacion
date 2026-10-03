package persistencia;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

// Conexión JDBC a MySQL.
// Los datos de conexión (url, usuario y clave) NO van escritos en el código: se leen del archivo
// 'conexion.properties' (carpeta raíz del proyecto, ignorado por git) o de las variables de entorno
// DB_URL, DB_USER y DB_PASSWORD. Plantilla: 'conexion.properties.example'.
public final class ConexionBD {

    private static final String ARCHIVO_CONFIG = "conexion.properties";

    private ConexionBD() {
        // No se instancia
    }

    private static String dato(Properties config, String claveProp, String variableEntorno) {
        String entorno = System.getenv(variableEntorno);
        if (entorno != null && !entorno.isEmpty()) {
            return entorno;
        }
        return config.getProperty(claveProp);
    }

    // Abre una conexión nueva. Quien la use debe cerrarla (try-with-resources).
    public static Connection obtenerConexion() throws SQLException {
        Properties config = new Properties();
        try (FileInputStream in = new FileInputStream(ARCHIVO_CONFIG)) {
            config.load(in);
        } catch (IOException e) {
            // Sin archivo: se intenta solo con variables de entorno
        }

        String url = dato(config, "url", "DB_URL");
        String usuario = dato(config, "usuario", "DB_USER");
        String clave = dato(config, "clave", "DB_PASSWORD");
        if (url == null || usuario == null) {
            throw new SQLException("Falta la configuración de conexión: crea el archivo '" + ARCHIVO_CONFIG
                    + "' en la carpeta del proyecto (usa 'conexion.properties.example' como plantilla).");
        }
        return DriverManager.getConnection(url, usuario, clave == null ? "" : clave);
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
