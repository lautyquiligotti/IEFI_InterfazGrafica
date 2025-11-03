/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Batalla.Modelo;

import Batalla.Modelo.Personaje;

/**
 *
 * @author Mar
 */
 class HozVenenosa extends Arma {
    public HozVenenosa() { super("Hoz Venenosa", 10); }
    @Override
    public void usarEfectoEspecial(Personaje objetivo) {
        objetivo.aplicarVeneno(5, 3);
    }
}
