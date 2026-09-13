package modelo;

import java.util.ArrayList;

public class Facturacion {

    // Métodos de pago permitidos (valores fijos)
    public static final String PAGO_EFECTIVO = "Efectivo";
    public static final String PAGO_TARJETA = "Tarjeta";
    public static final String PAGO_YAPE_PLIN = "Yape/Plin";
    public static final String PAGO_TRANSFERENCIA = "Transferencia";

    // Variable estática para llevar la cuenta global de facturas creadas
    private static int contadorId = 1;

    private int idFactura;      // Autogenerado: siempre numérico, nunca se ingresa por teclado
    private int idCliente;      // Relación con el cliente facturado
    private int idConsulta;     // Relación con la consulta que originó el cobro
    private String fecha;
    private double monto;
    private String metodoPago;  // Efectivo, Tarjeta, Yape/Plin, Transferencia

    public Facturacion(int idCliente, int idConsulta, String fecha, double monto, String metodoPago) {
        if (fecha == null || fecha.trim().isEmpty()) {
            throw new IllegalArgumentException("La fecha de la factura no puede estar vacía.");
        }
        if (!esMontoValido(monto)) {
            throw new IllegalArgumentException("El monto debe ser un número mayor que 0.");
        }
        if (!esMetodoPagoValido(metodoPago)) {
            throw new IllegalArgumentException("El método de pago debe ser Efectivo, Tarjeta, Yape/Plin o Transferencia.");
        }

        this.idFactura = contadorId++;
        this.idCliente = idCliente;
        this.idConsulta = idConsulta;
        this.fecha = fecha;
        this.monto = monto;
        this.metodoPago = metodoPago;
    }

    // --- Validaciones ---
    /**
     * Un monto es válido si es un número mayor que 0.
     */
    public static boolean esMontoValido(double monto) {
        return monto > 0;
    }

    /**
     * Un método de pago es válido si coincide (sin distinguir
     * mayúsculas/minúsculas) con uno de los métodos permitidos.
     */
    public static boolean esMetodoPagoValido(String metodoPago) {
        return metodoPago != null && (metodoPago.equalsIgnoreCase(PAGO_EFECTIVO)
                || metodoPago.equalsIgnoreCase(PAGO_TARJETA)
                || metodoPago.equalsIgnoreCase(PAGO_YAPE_PLIN)
                || metodoPago.equalsIgnoreCase(PAGO_TRANSFERENCIA));
    }

    /**
     * Verifica que un texto ingresado (ej. el ID de una factura) contenga solo
     * dígitos numéricos.
     */
    public static boolean esIdNumerico(String texto) {
        return texto != null && texto.matches("\\d+");
    }

    // --- Búsqueda ---
    public static Facturacion buscarPorId(ArrayList<Facturacion> listaFacturas, int idFactura) {
        for (Facturacion f : listaFacturas) {
            if (f.getIdFactura() == idFactura) {
                return f;
            }
        }
        return null;
    }

    /**
     * Devuelve la lista de facturas asociadas a un cliente (ej. para ver su
     * historial de pagos).
     */
    public static ArrayList<Facturacion> buscarPorCliente(ArrayList<Facturacion> listaFacturas, int idCliente) {
        ArrayList<Facturacion> resultado = new ArrayList<>();
        for (Facturacion f : listaFacturas) {
            if (f.getIdCliente() == idCliente) {
                resultado.add(f);
            }
        }
        return resultado;
    }

    // --- Getters ---
    public int getIdFactura() {
        return idFactura;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public int getIdConsulta() {
        return idConsulta;
    }

    public String getFecha() {
        return fecha;
    }

    public double getMonto() {
        return monto;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void mostrarDatos() {
        System.out.println("\n--- DATOS DE LA FACTURA ---");
        System.out.println("ID Factura: " + idFactura);
        System.out.println("ID Cliente: " + idCliente);
        System.out.println("ID Consulta: " + idConsulta);
        System.out.println("Fecha: " + fecha);
        System.out.println("Monto: S/ " + monto);
        System.out.println("Método de pago: " + metodoPago);
        System.out.println("---------------------------\n");
    }

    // Sobrecarga de mostrarDatos: si resumen es false, muestra el detalle; si es true, muestra una sola línea
    public void mostrarDatos(boolean resumen) {
        if (!resumen) {
            mostrarDatos();
            return;
        }
        System.out.println("Factura #" + idFactura + " | Cliente ID: " + idCliente
                + " | Consulta ID: " + idConsulta + " | S/ " + monto + " | " + metodoPago);
    }
}
