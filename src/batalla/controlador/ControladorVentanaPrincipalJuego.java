package batalla.controlador;

import batalla.vista.VentanaPrincipalJuego;
import batalla.vista.VentanaResultadosBatalla;
import batalla.modelo.Personaje;
import javax.swing.*;
import java.util.List;

public class ControladorVentanaPrincipalJuego implements BatallaListener {

    private final VentanaPrincipalJuego vista;
    private final ControladorBatalla ctrlBatalla;
    private int turnoActual = 1;
    private int partidaActual = 1;
    private int totalPartidas = 3;

    private final Personaje heroe;
    private final Personaje villano;

    // =======================================================
    // 🔹 Constructor mejorado
    // =======================================================
    public ControladorVentanaPrincipalJuego(
            VentanaPrincipalJuego vista,
            ControladorBatalla ctrlBatalla,
            Personaje heroe,
            Personaje villano,
            int totalPartidas) {

        this.vista = vista;
        this.ctrlBatalla = ctrlBatalla;
        this.heroe = heroe;
        this.villano = villano;
        this.totalPartidas = totalPartidas;

        inicializarEventos();
        inicializarVista();
    }

    // =======================================================
    // 🔹 Inicialización de la vista
    // =======================================================
    private void inicializarVista() {
        vista.getLblPartida().setText("Partida 1/" + totalPartidas);
        vista.getLblTurno().setText("Turno 1");

        // Configurar barras
        vista.getBarraVidaHeroe().setMaximum(heroe.getVida());
        vista.getBarraVidaVillano().setMaximum(villano.getVida());
        vista.getBarraBendicionHeroe().setMaximum(100);
        vista.getBarraBendicionVillano().setMaximum(100);

        // Mostrar datos iniciales
        actualizarEstadoPersonajes(heroe, villano);
        vista.getLogEventos().append("🏁 Comienza la batalla entre "
                + heroe.getNombre() + " y " + villano.getNombre() + "!\n");
    }

    // =======================================================
    // 🔹 Inicialización de eventos de menú
    // =======================================================
    private void inicializarEventos() {
        JMenuItem guardar = new JMenuItem("Guardar partida");
        guardar.addActionListener(e -> guardarPartida());
        vista.getMenuPartida().add(guardar);

        JMenuItem salir = new JMenuItem("Salir");
        salir.addActionListener(e -> System.exit(0));
        vista.getMenuPartida().add(salir);

        JMenuItem verReporte = new JMenuItem("Ver Reporte Final");
        verReporte.addActionListener(e -> new VentanaResultadosBatalla().setVisible(true));
        vista.getMenuVer().add(verReporte);
    }

    // =======================================================
    // 🔹 Métodos de actualización de interfaz
    // =======================================================
    public void actualizarTurno(int turno) {
        this.turnoActual = turno;
        vista.getLblTurno().setText("Turno " + turnoActual);
    }

    public void actualizarPartida(int num, int total) {
        this.partidaActual = num;
        this.totalPartidas = total;
        vista.getLblPartida().setText("Partida " + num + "/" + total);
    }

    public void actualizarEstadoPersonajes(Personaje heroe, Personaje villano) {
        // Héroe
        vista.getLblNombreHeroe().setText("Nombre: " + heroe.getNombre());
        vista.getLblApodoHeroe().setText("Clase: " + heroe.getClass().getSimpleName());
        vista.getBarraVidaHeroe().setValue(heroe.getVida());
        vista.getBarraBendicionHeroe().setValue(heroe.getBendicion());
        vista.getLblArmaHeroe().setText("Arma: " +
                (heroe.getArmaActual() != null ? heroe.getArmaActual().getNombre() : "-"));
        vista.getLblEstadoHeroe().setText("Estado: " + (heroe.estaVivo() ? "Activo" : "Derrotado"));

        // Villano
        vista.getLblNombreVillano().setText("Nombre: " + villano.getNombre());
        vista.getLblApodoVillano().setText("Clase: " + villano.getClass().getSimpleName());
        vista.getBarraVidaVillano().setValue(villano.getVida());
        vista.getBarraBendicionVillano().setValue(villano.getBendicion());
        vista.getLblArmaVillano().setText("Arma: " +
                (villano.getArmaActual() != null ? villano.getArmaActual().getNombre() : "-"));
        vista.getLblEstadoVillano().setText("Estado: " + (villano.estaVivo() ? "Activo" : "Derrotado"));
    }

    public void agregarEvento(String texto) {
        vista.getLogEventos().append("→ " + texto + "\n");
        vista.getLogEventos().setCaretPosition(vista.getLogEventos().getDocument().getLength());
    }

    public void mostrarGanador(String ganador) {
        JOptionPane.showMessageDialog(vista, "🏆 Ganador: " + ganador);
        new VentanaResultadosBatalla().setVisible(true);
        vista.dispose();
    }

    private void guardarPartida() {
        JOptionPane.showMessageDialog(vista, "Funcionalidad de guardado aún no implementada 🗃️");
    }

    // =======================================================
    // 🔹 Implementación del BatallaListener
    // =======================================================
    @Override
    public void onTurno(int turno, Personaje actual, Personaje enemigo) {
        SwingUtilities.invokeLater(() -> {
            actualizarTurno(turno);
            agregarEvento("🔁 Turno " + turno + ": " + actual.getNombre() + " actúa.");
        });
    }

    @Override
    public void onAccion(String texto) {
        SwingUtilities.invokeLater(() -> agregarEvento(texto));
    }

    @Override
    public void onEstado(Personaje heroe, Personaje villano) {
        SwingUtilities.invokeLater(() -> actualizarEstadoPersonajes(heroe, villano));
    }

    @Override
    public void onFin(String resumen,
                      Personaje heroe,
                      Personaje villano,
                      int turnos,
                      List<String> eventosEspeciales,
                      List<String> historial) {
        SwingUtilities.invokeLater(() -> {
            agregarEvento("🏁 " + resumen);
            mostrarGanador(heroe.estaVivo() ? heroe.getNombre() : villano.getNombre());
        });
    }
}
