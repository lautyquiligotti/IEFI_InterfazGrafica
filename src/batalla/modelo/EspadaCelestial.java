package batalla.modelo;

import batalla.modelo.Personaje;

public class EspadaCelestial extends Arma {
    public EspadaCelestial() { super("Espada Celestial", 20); }
    @Override
    public void usarEfectoEspecial(Personaje objetivo) {
        if (portador != null) {
            portador.curar(15);
            portador.aplicarBuffDefensa(5, 3);
        }
    }
}
