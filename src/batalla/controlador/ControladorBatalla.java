package batalla.controlador;

import batalla.modelo.*;
import batalla.vista.VistaConsola;
import java.util.*;

// ✅ Tu clase original + agregados para ACTIVIDAD 2 (registro de jugadores)
public class ControladorBatalla {

    // ÚNICO listener válido
    private BatallaListener listener;
    public void setListener(BatallaListener listener) { this.listener = listener; }

    private Heroe heroe;
    private Villano villano;
    private final List<String> eventosEspeciales = new ArrayList<>();
    private final List<String> historial = new ArrayList<>();

    public ControladorBatalla() {}

    // ==============================================================
    // =====================  [ACT2] NUEVO  =========================
    // ============  Registro previo para Configuración  ============
    // ==============================================================

    // [ACT2] Tipo de personaje para el registro
    public enum TipoPersonaje { HEROE, VILLANO }

    // [ACT2] DTO liviano para guardar lo que viene de la vista
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

    // [ACT2] Lista de registrados y “slots” únicos (1 héroe + 1 villano)
    private final List<RegistroJugador> registrados = new ArrayList<>();
    private String apodoHeroeRegistrado = null;
    private String apodoVillanoRegistrado = null;

    // [ACT2] Exponer lista como solo-lectura para depurar/mostrar
    public List<RegistroJugador> getRegistrados() {
        return Collections.unmodifiableList(registrados);
    }

    // [ACT2] Validaciones del primer parcial + regla de unicidad por apodo
    public void agregarJugador(String nombre, String apodo, String tipoStr) throws IllegalArgumentException {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío.");
        }
        if (!ValidacionApodos.esValido(apodo)) { // usa tu clase de validación
            throw new IllegalArgumentException("Apodo inválido (3-10 caracteres, solo letras y espacios).");
        }
        final String apodoTrim = apodo.trim();
        boolean apodoDuplicado = registrados.stream()
                .anyMatch(r -> r.getApodo().equalsIgnoreCase(apodoTrim));
        if (apodoDuplicado) {
            throw new IllegalArgumentException("Ya existe un personaje con ese apodo.");
        }

        // Normalizamos el tipo
        TipoPersonaje tipo;
        if (tipoStr == null) throw new IllegalArgumentException("Tipo no seleccionado.");
        switch (tipoStr.toLowerCase()) {
            case "heroe":
            case "héroe": tipo = TipoPersonaje.HEROE; break;
            case "villano": tipo = TipoPersonaje.VILLANO; break;
            default: throw new IllegalArgumentException("Tipo desconocido: " + tipoStr);
        }

        // Regla: solo 1 héroe y 1 villano
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

    // [ACT2] Eliminar por apodo (limpia slots si corresponde)
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

    // [ACT2] Chequeo previo para habilitar “Iniciar Batalla”
    public boolean tieneHeroeYVillano() {
        return apodoHeroeRegistrado != null && apodoVillanoRegistrado != null;
    }

    // [ACT2] Helper para iniciar usando lo registrado desde la vista
    public void iniciarBatallaDesdeRegistro() {
        if (!tieneHeroeYVillano()) {
            throw new IllegalStateException("Debe haber 1 Héroe y 1 Villano registrados antes de iniciar.");
        }
        iniciarBatalla(apodoHeroeRegistrado, apodoVillanoRegistrado);
    }
    // ===================  /[ACT2] NUEVO  =========================


    // ==============================================================
    // ==================  TU LÓGICA ORIGINAL  ======================
    // ==============================================================

    public void iniciarBatalla(String apodoHeroe, String apodoVillano) {
        Random rnd = new Random();

        // ⚠️ Tu modelo construye Heroe/Villano con (apodo, stats...)
        // Si en tu Personaje “nombre” representa esto, mantenemos tu firma.
        heroe = new Heroe(apodoHeroe, 130 + rnd.nextInt(31), 24 + rnd.nextInt(9), 8 + rnd.nextInt(6), 30 + rnd.nextInt(71));
        villano = new Villano(apodoVillano, 130 + rnd.nextInt(31), 24 + rnd.nextInt(9), 8 + rnd.nextInt(6), 30 + rnd.nextInt(71));

        int turno = 1;
        Personaje actual = heroe;
        Personaje enemigo = villano;

        // Notificar estado inicial
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
