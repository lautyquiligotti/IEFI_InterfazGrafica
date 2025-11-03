package Batalla.Modelo;

import batalla.vista.VistaConsola;
import Batalla.Modelo.Personaje;

public abstract class AtaqueSupremo {
    protected String nombre;
    protected Personaje lanzador;
    protected boolean usado = false; // para controlar si ya se ejecutó

    public AtaqueSupremo(String nombre, Personaje lanzador) {
        this.nombre = nombre;
        this.lanzador = lanzador;
    }

    public abstract void ejecutar(Personaje objetivo);

    // Marcar como usado
    protected void registrarUso(int danio) {
        lanzador.registrarSupremoUsado();
        usado = true; // importante: marcarlo al usar
        VistaConsola.mostrarEventoEspecial(
            lanzador.getNombre() + " activo \"" + nombre + "\" --> " + danio + " de danio"
        );
    }

    // Getter para que Villano pueda consultar
    public boolean yaUsado() {
        return usado;
    }
}
