package batalla.modelo;

import batalla.modelo.EspadaCelestial;
import batalla.modelo.EspadaSagrada;
import batalla.modelo.EspadaSimple;

class BendicionCelestial implements Bendicion {
    @Override
    public Arma decidirArma(int p) {
        if (p >= 80) return new EspadaCelestial();
        if (p >= 40) return new EspadaSagrada();
        return new EspadaSimple();
    }
    @Override public String getNombre() { return "Bendicion Celestial"; }
}
