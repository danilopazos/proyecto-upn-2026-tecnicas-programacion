package servicio;

import modelo.Cliente;
import modelo.Mascota;
import persistencia.RepositorioCSV;
import java.io.IOException;
import java.util.ArrayList;

public class ServicioVeterinaria {
    private final RepositorioCSV repositorio;
    private final ArrayList<Cliente> clientes;
    private final ArrayList<Mascota> mascotas;

    public ServicioVeterinaria(RepositorioCSV repositorio) throws IOException {
        this.repositorio = repositorio;
        clientes = repositorio.cargarClientes();
        mascotas = repositorio.cargarMascotas(clientes);
    }

    public ArrayList<Cliente> getClientes() { return new ArrayList<>(clientes); }
    public ArrayList<Mascota> getMascotas() { return new ArrayList<>(mascotas); }

    public Cliente buscarClientePorId(int id) {
        for (Cliente c : clientes) if (c.getIdCliente() == id) return c;
        return null;
    }

    public void registrarCliente(Cliente cliente) throws IOException {
        if (Cliente.existeDni(clientes, cliente.getDni()) || buscarClientePorId(cliente.getIdCliente()) != null) {
            throw new IllegalArgumentException("El cliente ya existe.");
        }
        ArrayList<Cliente> nuevos = getClientes();
        nuevos.add(cliente);
        repositorio.guardarClientes(nuevos);
        clientes.add(cliente);
    }

    public void registrarMascota(Mascota mascota) throws IOException {
        if (buscarClientePorId(mascota.getIdCliente()) == null) throw new IllegalArgumentException("No existe el cliente propietario.");
        if (Mascota.buscarPorId(mascotas, mascota.getIdMascota()) != null) throw new IllegalArgumentException("El ID de mascota ya existe.");
        if (mascota.getEspecie().trim().isEmpty()) throw new IllegalArgumentException("La especie no puede estar vacía.");
        if (!Double.isFinite(mascota.getPesoActual()) || mascota.getPesoActual() <= 0) throw new IllegalArgumentException("Peso inválido.");
        ArrayList<Mascota> nuevas = getMascotas();
        nuevas.add(mascota);
        repositorio.guardarMascotas(nuevas);
        mascotas.add(mascota);
    }

    public void registrarPeso(Mascota mascota, double peso, String fecha) throws IOException {
        if (!Double.isFinite(peso) || peso <= 0) throw new IllegalArgumentException("Peso inválido.");
        mascota.registrarControlPeso(peso, fecha);
        try {
            repositorio.guardarMascotas(mascotas);
        } catch (IOException e) {
            mascota.getHistorialPeso().remove(mascota.getHistorialPeso().size() - 1);
            mascota.getFechasHistorialPeso().remove(mascota.getFechasHistorialPeso().size() - 1);
            throw e;
        }
    }
}
