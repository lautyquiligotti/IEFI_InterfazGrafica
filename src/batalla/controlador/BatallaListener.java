package batalla.controlador;

import batalla.modelo.Personaje;
import java.util.List;

public interface BatallaListener {
    void onTurno(int turno, Personaje actual, Personaje enemigo);
    void onAccion(String texto);
    void onEstado(Personaje heroe, Personaje villano);
    void onFin(String resumen,
               Personaje heroe,
               Personaje villano,
               int turnos,
               List<String> eventosEspeciales,
               List<String> historial);
}
