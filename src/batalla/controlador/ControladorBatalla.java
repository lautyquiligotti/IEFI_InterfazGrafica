package batalla.controlador;

import batalla.modelo.*;
import batalla.vista.VistaConsola;
import java.util.*;

public class ControladorBatalla {

    // ÚNICO listener válido
    private BatallaListener listener;
    public void setListener(BatallaListener listener) { this.listener = listener; }

    private Heroe heroe;
    private Villano villano;
    private final List<String> eventosEspeciales = new ArrayList<>();
    private final List<String> historial = new ArrayList<>();

    public ControladorBatalla() {}

    public void iniciarBatalla(String apodoHeroe, String apodoVillano) {
        Random rnd = new Random();

        heroe = new Heroe(apodoHeroe, 130 + rnd.nextInt(31), 24 + rnd.nextInt(9), 8 + rnd.nextInt(6), 30 + rnd.nextInt(71));
        villano = new Villano(apodoVillano, 130 + rnd.nextInt(31), 24 + rnd.nextInt(9), 8 + rnd.nextInt(6), 30 + rnd.nextInt(71));

        int turno = 1;
        Personaje actual = heroe;
        Personaje enemigo = villano;

        // Notificar estado inicial
        if (listener != null) listener.onEstado(heroe, villano);

        while (heroe.estaVivo() && villano.estaVivo()) {
            if (listener != null) listener.onTurno(turno, actual, enemigo);
            System.out.println("----- Turno " + turno + " - " + actual.getNombre() + " -----");

            actual.aplicarEstadosAlInicioDelTurno();
            if (!actual.estaVivo()) break;

            String antesArma = (actual.getArmaActual() != null) ? actual.getArmaActual().getNombre() : "-";
            int vidaAntesEnemigo = enemigo.getVida();

            actual.decidirAccion(enemigo);

            String despuesArma = (actual.getArmaActual() != null) ? actual.getArmaActual().getNombre() : "-";
            if (!antesArma.equals(despuesArma)) {
                String ev = actual.getNombre() + " invocó " + despuesArma;
                eventosEspeciales.add(ev);
                System.out.println("[ACCION] " + ev);
                if (listener != null) listener.onAccion(ev);
            } else {
                if (enemigo.getVida() != vidaAntesEnemigo) {
                    String ev = actual.getNombre() + " dañó a " + enemigo.getNombre()
                            + " (" + vidaAntesEnemigo + " -> " + enemigo.getVida() + ")";
                    System.out.println("[ACCION] " + ev);
                    if (listener != null) listener.onAccion(ev);
                } else {
                    String ev = actual.getNombre() + " no hizo daño visible este turno.";
                    System.out.println("[ACCION] " + ev);
                    if (listener != null) listener.onAccion(ev);
                }
            }

            System.out.println(heroe);
            System.out.println(villano);
            if (listener != null) listener.onEstado(heroe, villano);

            Personaje temp = actual;
            actual = enemigo;
            enemigo = temp;
            turno++;
        }

        String ganador = heroe.estaVivo() ? heroe.getNombre() : villano.getNombre();
        String resumen = "Heroe: " + heroe.getNombre()
                + " | Villano: " + villano.getNombre()
                + " | Ganador: " + ganador
                + " | Turnos: " + turno;

        historial.add(resumen);

        if (listener != null)
            listener.onFin(resumen, heroe, villano, turno, eventosEspeciales, historial);

        String reporte = Reportes.generar(heroe, villano, eventosEspeciales, historial, turno);
        VistaConsola.mostrarReporte(reporte);
    }
}
