package Batalla.Modelo;

import Batalla.Modelo.Personaje;

class EspadaSagrada extends Arma {
    public EspadaSagrada() { super("Espada Sagrada", 12); }
    @Override
    public void usarEfectoEspecial(Personaje objetivo) {
        if (portador != null) portador.curar(10);
    }
}
