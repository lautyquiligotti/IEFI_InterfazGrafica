/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package batalla.modelo;

public class RegistroBatalla {
    private final int numero;       // 1..N
    private final String heroe;
    private final String villano;
    private final String ganador;   // "Heroe" o "Villano" (o nombre)
    private final int turnos;

    public RegistroBatalla(int numero, String heroe, String villano, String ganador, int turnos) {
        this.numero = numero;
        this.heroe = heroe;
        this.villano = villano;
        this.ganador = ganador;
        this.turnos = turnos;
    }
    public int getNumero() { return numero; }
    public String getHeroe() { return heroe; }
    public String getVillano() { return villano; }
    public String getGanador() { return ganador; }
    public int getTurnos() { return turnos; }
}
