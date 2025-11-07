package batalla.modelo;

import java.util.Random;
import java.util.ArrayList;

/**
 * Clase base abstracta para todos los personajes del juego.
 * Contiene la lógica común de atributos, bendiciones, armas y efectos por turno.
 */
public abstract class Personaje {
    protected String nombre;
    protected int vida;
    protected int fuerza;
    protected int defensaBase;

    protected Arma armaActual; // Arma equipada actualmente
    protected Bendicion fuenteDePoder; // Bendición celestial o del vacío
    protected int porcentajeBendicion; // 0..100

    // Estados por turnos
    private int venenoTurnosRestantes = 0;
    private int venenoDanioPorTurno = 0;
    private int defensaBuffTurnosRestantes = 0;
    private int defensaBuffExtra = 0;

    protected final Random rnd = new Random();

    // Lista de todas las armas invocadas (para el reporte)
    protected ArrayList<Arma> armasInvocadas = new ArrayList<>();

    // Contador de ataques supremos ejecutados
    private int supremosUsados = 0;

    // 🔹 Constructor
    public Personaje(String nombre, int vida, int fuerza, int defensa,
                     Bendicion fuente, int porcentajeBendicion) {
        this.nombre = nombre;
        this.vida = vida;
        this.fuerza = fuerza;
        this.defensaBase = defensa;
        this.fuenteDePoder = fuente;
        this.porcentajeBendicion = Math.max(0, Math.min(100, porcentajeBendicion));
    }

    // ======= GETTERS Y ESTADO GENERAL =======
    public boolean estaVivo() { 
        return vida > 0; 
    }

    public String getNombre() { 
        return nombre; 
    }

    public int getVida() { 
        return vida; 
    }

    public Arma getArmaActual() { 
        return armaActual; 
    }

    public ArrayList<Arma> getArmasInvocadas() { 
        return armasInvocadas; 
    }

    public int getDefensaActual() { 
        return defensaBase + defensaBuffExtra; 
    }

    // 🔹 Getters adicionales requeridos por la vista
    public int getFuerza() {
        return fuerza;
    }

    public int getDefensa() {
        return defensaBase;
    }

    // ======= SUPREMOS =======
    public void registrarSupremoUsado() {
        supremosUsados++;
    }

    public int getSupremosUsados() {
        return supremosUsados;
    }

    // ======= EFECTOS DE ESTADO =======
    public void aplicarEstadosAlInicioDelTurno() {
        if (venenoTurnosRestantes > 0) {
            vida -= venenoDanioPorTurno;
            venenoTurnosRestantes--;
            if (vida < 0) vida = 0;
        }
        if (defensaBuffTurnosRestantes > 0) {
            defensaBuffTurnosRestantes--;
            if (defensaBuffTurnosRestantes == 0) {
                defensaBuffExtra = 0;
            }
        }

        // Aumenta bendición/maldición progresivamente
        if (porcentajeBendicion < 100) {
            porcentajeBendicion += 10;
            if (porcentajeBendicion > 100) {
                porcentajeBendicion = 100;
            }
        }
    }

    // ======= DAÑO Y CURACIÓN =======
    public void recibirDanio(int danio) {
        int danioReal = Math.max(0, danio - getDefensaActual());
        vida -= danioReal;
        if (vida < 0) vida = 0;
    }

    public void recibirDanioDirecto(int danio) {
        vida -= danio;
        if (vida < 0) vida = 0;
    }

    public void curar(int puntos) {
        if (puntos > 0) {
            vida += puntos;
        }
    }

    // ======= EFECTOS ESPECIALES =======
    public void aplicarVeneno(int danioPorTurno, int turnos) {
        venenoDanioPorTurno = Math.max(venenoDanioPorTurno, danioPorTurno);
        venenoTurnosRestantes = Math.max(venenoTurnosRestantes, turnos);
    }

    public void aplicarBuffDefensa(int extra, int turnos) {
        defensaBuffExtra += extra;
        defensaBuffTurnosRestantes = Math.max(defensaBuffTurnosRestantes, turnos);
    }

    // ======= ATAQUE E INVOCACIÓN =======
    public void atacar(Personaje enemigo) {
        int base = fuerza + (armaActual != null ? armaActual.getDanioExtra() : 0);
        enemigo.recibirDanio(base);
        if (armaActual != null) {
            armaActual.usarEfectoEspecial(enemigo);
        }
    }

    // Invocar un arma nueva
    public void invocarArma() {
        Arma nueva = fuenteDePoder.decidirArma(porcentajeBendicion);
        if (nueva != null) {
            nueva.setPortador(this);
            armaActual = nueva;                 
            armasInvocadas.add(nueva);          
        } else {
            System.out.println(nombre + " no pudo invocar un arma.");
        }
    }

    // ======= ACCIÓN DE TURNO =======
    public abstract void decidirAccion(Personaje enemigo);

    @Override
    public String toString() {
        return nombre + " [vida=" + vida + 
               ", fuerza=" + fuerza +
               ", defensa=" + getDefensaActual() +
               ", arma=" + (armaActual != null ? armaActual.getNombre() : "-") +
               ", %bend/mald=" + porcentajeBendicion +
               ", supremosUsados=" + supremosUsados + "]";
    }
}
