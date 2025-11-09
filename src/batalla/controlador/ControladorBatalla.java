package batalla.controlador;

import batalla.modelo.*;
import batalla.vista.VistaConsola;
import java.util.*;

public class ControladorBatalla {

    // ÚNICO listener válido
    private BatallaListener listener;
    public void setListener(BatallaListener l){ this.listener = l; }

    private Heroe heroe;
    private Villano villano;
    private final List<String> eventosEspeciales = new ArrayList<>();
    private final List<String> historial = new ArrayList<>();

    // ---------------------------
    //  INICIO DE BATALLA
    // ---------------------------

    // Compatibilidad con la versión vieja (modo consola)
    public void iniciarBatalla(String apodoHeroe, String apodoVillano) {
        iniciarBatalla(apodoHeroe, apodoVillano, null);
    }

    // Versión que recibe la configuración del formulario (A2)
    public void iniciarBatalla(String apodoHeroe, String apodoVillano,
                               ControladorConfigurarcionDeBatalla.ConfigPartida cfg) {

        Random rnd = new Random();

        // Usa los valores del formulario si existen; si no, random
        int vidaH   = (cfg != null) ? cfg.vida      : 130 + rnd.nextInt(31);
        int fuerzaH = (cfg != null) ? cfg.fuerza    : 24  + rnd.nextInt(9);
        int defH    = (cfg != null) ? cfg.defensa   : 8   + rnd.nextInt(6);
        int bendH   = (cfg != null) ? cfg.bendicion : 30  + rnd.nextInt(71);

        int vidaV   = (cfg != null) ? cfg.vida      : 130 + rnd.nextInt(31);
        int fuerzaV = (cfg != null) ? cfg.fuerza    : 24  + rnd.nextInt(9);
        int defV    = (cfg != null) ? cfg.defensa   : 8   + rnd.nextInt(6);
        int bendV   = (cfg != null) ? cfg.bendicion : 30  + rnd.nextInt(71);

        // Crea los personajes
        heroe   = new Heroe(apodoHeroe,    vidaH, fuerzaH, defH, bendH);
        villano = new Villano(apodoVillano,vidaV, fuerzaV, defV, bendV);

        int turno = 1;
        Personaje actual = heroe;
        Personaje enemigo = villano;
        
        // Limpiar listas de batallas anteriores
        eventosEspeciales.clear();

        // Estado inicial
        if (listener != null) listener.onEstado(heroe, villano);
        System.out.println("=== INICIO DE LA BATALLA ===");
        System.out.println("Héroe: " + heroe.getNombre() + "  VS  Villano: " + villano.getNombre());
        System.out.println("-----------------------------");

        // Bucle principal (muestra toda la pelea)
        while (heroe.estaVivo() && villano.estaVivo()) {
            System.out.println("\n----- Turno " + turno + " (" + actual.getNombre() + ") -----");

            if (listener != null) listener.onTurno(turno, actual, enemigo);

            actual.aplicarEstadosAlInicioDelTurno();
            if (!actual.estaVivo()) break;

            String armaAntes = (actual.getArmaActual()!=null)? actual.getArmaActual().getNombre() : "-";
            int vidaAntesEnemigo = enemigo.getVida();

            actual.decidirAccion(enemigo);

            // Chequeamos si la acción mató al enemigo
            if (!enemigo.estaVivo()) {
                if (listener != null) listener.onEstado(heroe, villano); // Actualiza la vida a 0
                
                // Generamos el último evento de daño
                String ev = actual.getNombre() + " dañó a " + enemigo.getNombre()
                          + " (" + vidaAntesEnemigo + " -> " + enemigo.getVida() + ")";
                eventosEspeciales.add(ev);
                System.out.println("[ACCION] " + ev);
                if (listener != null) listener.onAccion(ev);
                
                break; // Termina el bucle
            }

            String armaDesp = (actual.getArmaActual()!=null)? actual.getArmaActual().getNombre() : "-";

            if (!armaAntes.equals(armaDesp)) {
                String ev = actual.getNombre() + " invocó " + armaDesp;
                eventosEspeciales.add(ev);
                System.out.println("[ACCION] " + ev);
                if (listener != null) listener.onAccion(ev);
            } else if (enemigo.getVida() != vidaAntesEnemigo) {
                String ev = actual.getNombre() + " dañó a " + enemigo.getNombre()
                          + " (" + vidaAntesEnemigo + " -> " + enemigo.getVida() + ")";
                eventosEspeciales.add(ev);
                System.out.println("[ACCION] " + ev);
                if (listener != null) listener.onAccion(ev);
            } else {
                String ev = actual.getNombre() + " no hizo daño visible este turno.";
                eventosEspeciales.add(ev);
                System.out.println("[ACCION] " + ev);
                if (listener != null) listener.onAccion(ev);
            }

            System.out.println(heroe);
            System.out.println(villano);
            if (listener != null) listener.onEstado(heroe, villano);

            // Cambia el turno
            Personaje t = actual;
            actual = enemigo;
            enemigo = t;
            turno++;
            
            // ==========================================================
            // ===          AQUÍ ESTÁ LA PAUSA DE 1 SEGUNDO           ===
            // ==========================================================
            try {
                // Pausa el juego por 1 segundo (1000 milisegundos)
                Thread.sleep(1000); 
            } catch (InterruptedException ex) {
                // No hacer nada, solo continuar
            }
            // ==========================================================
            
        }

        // Fin de batalla
        String ganador = heroe.estaVivo()? heroe.getNombre() : villano.getNombre();
        
        // Corregimos el conteo de turnos
        int turnosTotales = turno;
        if (heroe.estaVivo() || villano.estaVivo()) { // Si no terminó por veneno
             turnosTotales = turno - 1;
        }
        if (turnosTotales == 0) turnosTotales = 1; // Mínimo 1 turno
        
        String resumen = "Heroe: " + heroe.getNombre() + 
                         " | Villano: " + villano.getNombre() +
                         " | Ganador: " + ganador +
                         " | Turnos: " + turnosTotales;

        historial.add(resumen);
        System.out.println("\n=== FIN DE LA BATALLA ===");
        System.out.println(resumen);

        if (listener != null)
            listener.onFin(resumen, heroe, villano, turnosTotales, eventosEspeciales, historial);

        // Genera reporte y muestra por consola
        String reporte = Reportes.generar(heroe, villano, eventosEspeciales, historial, turnosTotales);
        VistaConsola.mostrarReporte(reporte);
    }
}