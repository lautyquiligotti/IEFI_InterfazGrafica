/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package batalla.modelo;

import java.util.List;

public class ResumenJuego {
    private final List<ResumenJugador> jugadores;   // Ranking
    private final List<RegistroBatalla> batallas;   // Historial

    public ResumenJuego(List<ResumenJugador> jugadores, List<RegistroBatalla> batallas) {
        this.jugadores = jugadores;
        this.batallas = batallas;
    }
    public List<ResumenJugador> getJugadores() { return jugadores; }
    public List<RegistroBatalla> getBatallas() { return batallas; }
}

