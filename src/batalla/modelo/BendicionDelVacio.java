/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Batalla.Modelo;

import batalla.modelo.Bendicion;
import Batalla.Modelo.HozVenenosa;
import Batalla.Modelo.HozOxidada;
import Batalla.Modelo.HozMortifera;

/**
 *
 * @author Mar
 */
 class BendicionDelVacio implements Bendicion {
    @Override
    public Arma decidirArma(int p) {
        if (p >= 80) return new HozMortifera();
        if (p >= 40) return new HozVenenosa();
        return new HozOxidada();
    }
    @Override public String getNombre() { return "Bendicion del Vacio"; }
}
