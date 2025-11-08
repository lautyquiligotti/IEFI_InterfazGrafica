package batalla.modelo;

import batalla.modelo.Personaje;

public class EspadaSagrada extends Arma {
    public EspadaSagrada() { super("Espada Sagrada", 12); }
    @Override
    public void usarEfectoEspecial(Personaje objetivo) {
        if (portador != null) portador.curar(10);
    }
}
