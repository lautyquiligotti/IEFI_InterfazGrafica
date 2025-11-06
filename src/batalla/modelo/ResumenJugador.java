/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package batalla.modelo;

public class ResumenJugador {
    private final String nombre;
    private final String apodo;    // podés repetir el nombre si no usás apodo
    private final String tipo;     // "Heroe" o "Villano"
    private final int vidaFinal;
    private final int victorias;
    private final int supremosUsados;
    private final int armasInvocadas;

    public ResumenJugador(String nombre, String apodo, String tipo, int vidaFinal,
                          int victorias, int supremosUsados, int armasInvocadas) {
        this.nombre = nombre;
        this.apodo = apodo;
        this.tipo = tipo;
        this.vidaFinal = vidaFinal;
        this.victorias = victorias;
        this.supremosUsados = supremosUsados;
        this.armasInvocadas = armasInvocadas;
    }
    public String getNombre() { return nombre; }
    public String getApodo() { return apodo; }
    public String getTipo() { return tipo; }
    public int getVidaFinal() { return vidaFinal; }
    public int getVictorias() { return victorias; }
    public int getSupremosUsados() { return supremosUsados; }
    public int getArmasInvocadas() { return armasInvocadas; }
}

