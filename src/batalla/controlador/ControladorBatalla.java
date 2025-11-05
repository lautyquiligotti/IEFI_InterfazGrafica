package batalla.controlador;

import batalla.modelo.*;
import batalla.vista.VistaConsola;
import java.util.*;

// Controlador principal con lógica original + ampliación para Actividad 2
public class ControladorBatalla {

    // ==============================================================
    // === LISTENER Y CAMPOS BASE ===
    // ==============================================================
    private BatallaListener listener;
    public void setListener(BatallaListener listener) { this.listener = listener; }

    private Heroe heroe;
    private Villano villano;
    private final List<String> eventosEspeciales = new ArrayList<>();
    private final List<String> historial = new ArrayList<>();


    // ==============================================================
    // === [ACTIVIDAD 2] REGISTRO DE JUGADORES (desde vista Wiring) ===
    // ==============================================================

    public enum TipoPersonaje { HEROE, VILLANO }

    public static class RegistroJugador {
        private final String nombre;
        private final String apodo;
        private final TipoPersonaje tipo;

        public RegistroJugador(String nombre, String apodo, TipoPersonaje tipo) {
            this.nombre = nombre;
            this.apodo = apodo;
            this.tipo = tipo;
        }
        public String getNombre() { return nombre; }
        public String getApodo() { return apodo; }
        public TipoPersonaje getTipo() { return tipo; }
        @Override public String toString() { return tipo + " | " + nombre + " (" + apodo + ")"; }
    }

    private final List<RegistroJugador> registrados = new ArrayList<>();
    private String apodoHeroeRegistrado = null;
    private String apodoVillanoRegistrado = null;

    public List<RegistroJugador> getRegistrados() {
        return Collections.unmodifiableList(registrados);
    }

    public void agregarJugador(String nombre, String apodo, String tipoStr) throws IllegalArgumentException {
        if (nombre == null || nombre.trim().isEmpty())
            throw new IllegalArgumentException("El nombre no puede estar vacío.");
        if (!ValidacionApodos.esValido(apodo))
            throw new IllegalArgumentException("Apodo inválido (3-10 caracteres, solo letras y espacios).");

        final String apodoTrim = apodo.trim();
        boolean apodoDuplicado = registrados.stream()
                .anyMatch(r -> r.getApodo().equalsIgnoreCase(apodoTrim));
        if (apodoDuplicado)
            throw new IllegalArgumentException("Ya existe un personaje con ese apodo.");

        TipoPersonaje tipo;
        if (tipoStr == null) throw new IllegalArgumentException("Tipo no seleccionado.");
        switch (tipoStr.toLowerCase()) {
            case "heroe":
            case "héroe": tipo = TipoPersonaje.HEROE; break;
            case "villano": tipo = TipoPersonaje.VILLANO; break;
            default: throw new IllegalArgumentException("Tipo desconocido: " + tipoStr);
        }

        if (tipo == TipoPersonaje.HEROE) {
            if (apodoHeroeRegistrado != null)
                throw new IllegalArgumentException("Ya hay un Héroe registrado.");
            apodoHeroeRegistrado = apodoTrim;
        } else {
            if (apodoVillanoRegistrado != null)
                throw new IllegalArgumentException("Ya hay un Villano registrado.");
            apodoVillanoRegistrado = apodoTrim;
        }

        registrados.add(new RegistroJugador(nombre.trim(), apodoTrim, tipo));
    }

    public boolean eliminarPorApodo(String apodo) {
        if (apodo == null || apodo.isBlank()) return false;
        final String ap = apodo.trim();
        Optional<RegistroJugador> found = registrados.stream()
                .filter(r -> r.getApodo().equalsIgnoreCase(ap))
                .findFirst();

        if (found.isEmpty()) return false;

        RegistroJugador r = found.get();
        if (r.getTipo() == TipoPersonaje.HEROE && apodoHeroeRegistrado != null &&
                apodoHeroeRegistrado.equalsIgnoreCase(ap)) {
            apodoHeroeRegistrado = null;
        }
        if (r.getTipo() == TipoPersonaje.VILLANO && apodoVillanoRegistrado != null &&
                apodoVillanoRegistrado.equalsIgnoreCase(ap)) {
            apodoVillanoRegistrado = null;
        }
        return registrados.remove(r);
    }

    public boolean tieneHeroeYVillano() {
        return apodoHeroeRegistrado != null && apodoVillanoRegistrado != null;
    }

    public void iniciarBatallaDesdeRegistro() {
        if (!tieneHeroeYVillano()) {
            throw new IllegalStateException("Debe haber 1 Héroe y 1 Villano registrados antes de iniciar.");
        }
        iniciarBatalla(apodoHeroeRegistrado, apodoVillanoRegistrado);
    }


    // ==============================================================
    // === [ACTIVIDAD 2] CONFIGURACIÓN DE PARTIDA (vida, fuerza, etc.) ===
    // ==============================================================

    private static class ConfigPartida {
        int vidaInicial, fuerzaInicial, defensaInicial, bendicionInicial, cantidadBatallas;
    }
    private ConfigPartida config;

    public void configurarPartida(int vida, int fuerza, int defensa, int bendicion, int cantidad) {
        ConfigPartida c = new ConfigPartida();
        c.vidaInicial = vida;
        c.fuerzaInicial = fuerza;
        c.defensaInicial = defensa;
        c.bendicionInicial = bendicion;
        c.cantidadBatallas = (cantidad == 2 || cantidad == 3 || cantidad == 5) ? cantidad : 3;
        this.config = c;
    }


    // ==============================================================
    // === LÓGICA ORIGINAL DE BATALLA ===
    // ==============================================================

    public void iniciarBatalla(String apodoHeroe, String apodoVillano) {
        Random rnd = new Random();

        // Usa configuración si existe, sino valores aleatorios
        int vidaH   = (config != null) ? config.vidaInicial     : 130 + rnd.nextInt(31);
        int fuerzaH = (config != null) ? config.fuerzaInicial   : 24 + rnd.nextInt(9);
        int defH    = (config != null) ? config.defensaInicial  : 8  + rnd.nextInt(6);
        int bendH   = (config != null) ? config.bendicionInicial: 30 + rnd.nextInt(71);

        int vidaV   = (config != null) ? config.vidaInicial     : 130 + rnd.nextInt(31);
        int fuerzaV = (config != null) ? config.fuerzaInicial   : 24 + rnd.nextInt(9);
        int defV    = (config != null) ? config.defensaInicial  : 8  + rnd.nextInt(6);
        int bendV   = (config != null) ? config.bendicionInicial: 30 + rnd.nextInt(71);

        heroe   = new Heroe(apodoHeroe, vidaH, fuerzaH, defH, bendH);
        villano = new Villano(apodoVillano, vidaV, fuerzaV, defV, bendV);

        int turno = 1;
        Personaje actual = heroe;
        Personaje enemigo = villano;

        if (listener != null) listener.onEstado(heroe, villano);

        while (heroe.estaVivo() && villano.estaVivo()) {
            if (listener != null) listener.onTurno(turno, actual, enemigo);
            System.out.println("----- Turno " + turno + " - " + actual.getNombre() + " -----");

            actual.aplicarEstadosAlInicioDelTurno();
            if (!actual.estaVivo()) break;

            String antesArma = (actual.getArmaActual() != null) ? actual.getArmaActual().getNombre() : "-";
            int vidaAntesEnemigo = enemigo.getVida();

            actual.decidirAccion(enemigo);

            String despuesArma = (actual.getArmaActual() != null) ? actual.getArmaActual().getNombre() : "-";
            if (!antesArma.equals(despuesArma)) {
                String ev = actual.getNombre() + " invocó " + despuesArma;
                eventosEspeciales.add(ev);
                System.out.println("[ACCION] " + ev);
                if (listener != null) listener.onAccion(ev);
            } else {
                if (enemigo.getVida() != vidaAntesEnemigo) {
                    String ev = actual.getNombre() + " dañó a " + enemigo.getNombre()
                            + " (" + vidaAntesEnemigo + " -> " + enemigo.getVida() + ")";
                    System.out.println("[ACCION] " + ev);
                    if (listener != null) listener.onAccion(ev);
                } else {
                    String ev = actual.getNombre() + " no hizo daño visible este turno.";
                    System.out.println("[ACCION] " + ev);
                    if (listener != null) listener.onAccion(ev);
                }
            }

            System.out.println(heroe);
            System.out.println(villano);
            if (listener != null) listener.onEstado(heroe, villano);

            Personaje temp = actual;
            actual = enemigo;
            enemigo = temp;
            turno++;
        }

        String ganador = heroe.estaVivo() ? heroe.getNombre() : villano.getNombre();
        String resumen = "Heroe: " + heroe.getNombre()
                + " | Villano: " + villano.getNombre()
                + " | Ganador: " + ganador
                + " | Turnos: " + turno;

        historial.add(resumen);

        if (listener != null)
            listener.onFin(resumen, heroe, villano, turno, eventosEspeciales, historial);

        String reporte = Reportes.generar(heroe, villano, eventosEspeciales, historial, turno);
        VistaConsola.mostrarReporte(reporte);
    }
}

