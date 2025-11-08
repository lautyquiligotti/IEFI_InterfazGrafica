/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package batalla.modelo;

import batalla.modelo.Personaje;

/**
 *
 * @author Mar
 */
 public class HozVenenosa extends Arma {
    public HozVenenosa() { super("Hoz Venenosa", 10); }
    @Override
    public void usarEfectoEspecial(Personaje objetivo) {
        objetivo.aplicarVeneno(5, 3);
    }
}
