/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package batalla.controlador;

import batalla.modelo.RegistroBatalla;
import batalla.modelo.ResumenJugador;
import java.util.*;

public class ServicioEstadisticas {

    public static class MaximoGolpe {
        public final String jugador; public final int monto;
        public MaximoGolpe(String jugador, int monto) { this.jugador = jugador; this.monto = monto; }
    }
    public static class BatallaMasLarga {
        public final int turnos; public final String ganador;
        public BatallaMasLarga(int turnos, String ganador) { this.turnos = turnos; this.ganador = ganador; }
    }

   
    public MaximoGolpe mayorDanioEnEventos(List<String> eventosEspeciales) {
        int mejor = -1; String quien = "N/D";
        if (eventosEspeciales != null) {
            for (String ev : eventosEspeciales) {
                int i1 = ev.lastIndexOf("-->");
                int i2 = ev.lastIndexOf("de danio");
                if (i1 >= 0 && i2 > i1) {
                    try {
                        int dmg = Integer.parseInt(ev.substring(i1 + 3, i2).trim());
                        if (dmg > mejor) {
                            mejor = dmg;
                            int a = ev.indexOf(" activo");
                            quien = (a > 0) ? ev.substring(0, a).trim() : quien;
                        }
                    } catch (Exception ignore) {}
                }
            }
        }
        return (mejor < 0) ? new MaximoGolpe("N/D", 0) : new MaximoGolpe(quien, mejor);
    }

    public BatallaMasLarga batallaMasLarga(List<RegistroBatalla> batallas) {
        if (batallas == null || batallas.isEmpty()) return new BatallaMasLarga(0, "N/D");
        RegistroBatalla b = batallas.stream().max(Comparator.comparingInt(RegistroBatalla::getTurnos)).orElse(null);
        return (b == null) ? new BatallaMasLarga(0, "N/D") : new BatallaMasLarga(b.getTurnos(), b.getGanador());
    }

    public Map<String,Integer> totalArmasInvocadas(List<ResumenJugador> jugadores) {
        Map<String,Integer> m = new LinkedHashMap<>();
        for (ResumenJugador r : jugadores) m.put(r.getApodo(), r.getArmasInvocadas());
        return m;
    }
    public Map<String,Integer> totalSupremos(List<ResumenJugador> jugadores) {
        Map<String,Integer> m = new LinkedHashMap<>();
        for (ResumenJugador r : jugadores) m.put(r.getApodo(), r.getSupremosUsados());
        return m;
    }

    public Map<String, Double> porcentajeVictoriasPorTipo(List<ResumenJugador> jugadores) {
        Map<String,Integer> victorias = new HashMap<>(), cantidad = new HashMap<>();
        for (ResumenJugador r : jugadores) {
            cantidad.merge(r.getTipo(), 1, Integer::sum);
            victorias.merge(r.getTipo(), r.getVictorias(), Integer::sum);
        }
        Map<String, Double> pct = new LinkedHashMap<>();
        for (String tipo : cantidad.keySet()) {
            int c = cantidad.get(tipo), v = victorias.getOrDefault(tipo, 0);
            pct.put(tipo, c == 0 ? 0.0 : (v * 100.0 / c));
        }
        return pct;
    }
}

