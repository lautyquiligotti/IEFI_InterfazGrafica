package Batalla.Modelo;

import Batalla.Modelo.EspadaCelestial;
import Batalla.Modelo.EspadaSagrada;
import Batalla.Modelo.EspadaSimple;

class BendicionCelestial implements Bendicion {
    @Override
    public Arma decidirArma(int p) {
        if (p >= 80) return new EspadaCelestial();
        if (p >= 40) return new EspadaSagrada();
        return new EspadaSimple();
    }
    @Override public String getNombre() { return "Bendicion Celestial"; }
}
