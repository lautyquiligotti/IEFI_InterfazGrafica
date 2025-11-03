package batalla.controlador;

import batalla.modelo.*;
import batalla.vista.VistaConsola;
import java.util.*;

public class ControladorBatalla {

    private Heroe heroe;
    private Villano villano;
    private final List<String> eventosEspeciales = new ArrayList<>();
    private final List<String> historial = new ArrayList<>();

    public ControladorBatalla() {
    }

    public void iniciarBatalla(String apodoHeroe, String apodoVillano) {
        Random rnd = new Random();

        heroe = new Heroe(apodoHeroe, 130 + rnd.nextInt(31), 24 + rnd.nextInt(9), 8 + rnd.nextInt(6), 30 + rnd.nextInt(71));
        villano = new Villano(apodoVillano, 130 + rnd.nextInt(31), 24 + rnd.nextInt(9), 8 + rnd.nextInt(6), 30 + rnd.nextInt(71));

        int turno = 1;
        Personaje actual = heroe;
        Personaje enemigo = villano;

        while (heroe.estaVivo() && villano.estaVivo()) {
            System.out.println("----- Turno " + turno + " - " + actual.getNombre() + " -----");
            actual.aplicarEstadosAlInicioDelTurno();

            if (!actual.estaVivo()) {
                break;
            }

            // Guardar estado previo para informar qué hace
            String antesArma = (actual.getArmaActual() != null) ? actual.getArmaActual().getNombre() : "-";
            int vidaAntesEnemigo = enemigo.getVida();

            // ejecutar acción (invocar o atacar o supremo)
            actual.decidirAccion(enemigo);

            // Detectar si invocó un arma nueva (compara el arma actual)
            String despuesArma = (actual.getArmaActual() != null) ? actual.getArmaActual().getNombre() : "-";
            if (!antesArma.equals(despuesArma)) {
                String ev = actual.getNombre() + " invocó " + despuesArma;
                eventosEspeciales.add(ev);
                System.out.println("[ACCION] " + ev);
            } else {
                // si la vida del enemigo cambió => atacó o supremo
                if (enemigo.getVida() != vidaAntesEnemigo) {
                    System.out.println("[ACCION] " + actual.getNombre() + " dañó a " + enemigo.getNombre()
                            + " (" + vidaAntesEnemigo + " -> " + enemigo.getVida() + ")");
                } else {
                    System.out.println("[ACCION] " + actual.getNombre() + " no hizo daño visible este turno.");
                }
            }

            // imprimir estado resumido al final del turno (opcional)
            System.out.println(heroe);
            System.out.println(villano);

            // cambio de turno
            Personaje temp = actual;
            actual = enemigo;
            enemigo = temp;
            turno++;
        }

        // Determinar ganador y resumen
        String ganador = heroe.estaVivo() ? heroe.getNombre() : villano.getNombre();
        String resumen = "Heroe: " + heroe.getNombre()
                + " | Villano: " + villano.getNombre()
                + " | Ganador: " + ganador
                + " | Turnos: " + turno;

        historial.add(resumen);

        String reporte = Reportes.generar(heroe, villano, eventosEspeciales, historial, turno);
        VistaConsola.mostrarReporte(reporte);
    }
}
