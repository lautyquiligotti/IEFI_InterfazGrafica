package batalla.controlador;

import batalla.modelo.ModeloTablaRanking;
import batalla.modelo.RegistroBatalla;
import batalla.modelo.ResumenJugador;
import batalla.vista.VentanaResultadosBatalla;
import java.util.List;
import java.util.Map;
import javax.swing.SwingUtilities;

public class ControladorResultadosBatalla {

    private final ControladorVentanaPrincipalJuego ctrlJuego; // Se usa solo para los eventos de "Mayor Daño"
    private final VentanaResultadosBatalla vista;
    private final ServicioEstadisticas servicioEstadisticas = new ServicioEstadisticas();

    public ControladorResultadosBatalla(ControladorVentanaPrincipalJuego ctrlJuego) {
        this.ctrlJuego = ctrlJuego;
        this.vista = new VentanaResultadosBatalla();
    }

    public void mostrarResultados() {
        // Ejecutar en el hilo de la interfaz gráfica
        SwingUtilities.invokeLater(() -> {
            
            // [MODIFICADO] Cargar datos históricos desde los archivos
            List<ResumenJugador> jugadores = ServicioPersistencia.cargarRankingPersonajes();
            List<RegistroBatalla> batallas = ServicioPersistencia.cargarHistorialBatallas();
            
            // [MODIFICADO] Los eventos de "Mayor Daño" solo reflejan la sesión actual
            List<String> eventos = ctrlJuego.getEventosHistoricos();
            
            // 1. Mostrar Estadísticas Detalladas (Punto 4)
            String estadisticas = generarEstadisticas(jugadores, batallas, eventos);
            vista.mostrarEstadisticas(estadisticas);

            // 2. Mostrar Historial (Punto 4)
            List<String> historialList = batallas.stream()
                    .map(r -> String.format("BATALLA #%d - Héroe: %s | Villano: %s | Ganador: %s | Turnos: %d",
                        r.getNumero(), r.getHeroe(), r.getVillano(), r.getGanador(), r.getTurnos()))
                    .toList();
            
            // Revertir para mostrar el más reciente primero
            vista.mostrarHistorial(historialList.reversed());

            // 3. Mostrar Ranking (Tabla) (Punto 4)
            ModeloTablaRanking modeloRanking = new ModeloTablaRanking(jugadores);
            vista.mostrarRanking(modeloRanking);
            
            vista.setVisible(true);
        });
    }

    /** Genera el texto con las estadísticas clave usando ServicioEstadisticas */
    private String generarEstadisticas(List<ResumenJugador> jugadores, List<RegistroBatalla> batallas, List<String> eventos) {
        StringBuilder sb = new StringBuilder();
        
        // [MODIFICADO] Aclarar que "Mayor Daño" es solo de la sesión actual
        ServicioEstadisticas.MaximoGolpe maxGolpe = servicioEstadisticas.mayorDanioEnEventos(eventos);
        sb.append("🔹 Mayor Daño (Sesión Actual):\n") // Aclaración
          .append(String.format("  %s con %d de daño.\n\n", maxGolpe.jugador, maxGolpe.monto));
        
        // Batalla más larga (Cargado de historial)
        ServicioEstadisticas.BatallaMasLarga masLarga = servicioEstadisticas.batallaMasLarga(batallas);
        sb.append("🔹 Batalla más Larga (Histórico):\n")
          .append(String.format("  Ganador: %s en %d turnos.\n\n", masLarga.ganador, masLarga.turnos));
        
        // Total de armas invocadas (Cargado de ranking)
        Map<String,Integer> totalArmas = servicioEstadisticas.totalArmasInvocadas(jugadores);
        sb.append("🔹 Total de Armas Invocadas (Histórico):\n");
        totalArmas.forEach((apodo, count) -> sb.append(String.format("  %s: %d\n", apodo, count)));
        sb.append("\n");
        
        // Ataques supremos ejecutados (Cargado de ranking)
        Map<String,Integer> totalSupremos = servicioEstadisticas.totalSupremos(jugadores);
        sb.append("🔹 Ataques Supremos Ejecutados (Histórico):\n");
        totalSupremos.forEach((apodo, count) -> sb.append(String.format("  %s: %d\n", apodo, count)));
        sb.append("\n");
        
        // Porcentaje de victorias por tipo (Opcional) (Cargado de ranking)
        Map<String, Double> pctVictorias = servicioEstadisticas.porcentajeVictoriasPorTipo(jugadores);
        sb.append("🔹 Porcentaje de Victorias por Tipo (Histórico):\n");
        pctVictorias.forEach((tipo, pct) -> sb.append(String.format("  %s: %.2f%%\n", tipo, pct)));
        sb.append("\n");

        return sb.toString();
    }
}