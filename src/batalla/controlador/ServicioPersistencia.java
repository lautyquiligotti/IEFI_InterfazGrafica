package batalla.controlador;

import batalla.modelo.Personaje;
import batalla.modelo.Heroe;
import batalla.modelo.Villano;
import batalla.modelo.Arma;
import batalla.modelo.EspadaSimple; 
import batalla.modelo.EspadaSagrada;
import batalla.modelo.EspadaCelestial;
import batalla.modelo.HozOxidada;
import batalla.modelo.HozVenenosa;
import batalla.modelo.HozMortifera;
import batalla.modelo.RegistroBatalla; // Importar
import batalla.modelo.ResumenJugador; // Importar
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map; // Importar
import java.util.LinkedHashMap; // Importar

public class ServicioPersistencia {
    
    // [CORREGIDO] Archivo de guardado manual (usado por ControladorConfiguracion)
    public static final String FILE_NAME = "batalla_guardada.txt";
    
    // [AÑADIDO] Archivos de historial permanente (Punto 5 - Estadísticas)
    public static final String HISTORIAL_FILE = "historial_batallas.txt";
    public static final String RANKING_FILE = "personajes.txt";
    
    private static final String SEPARATOR = "|";

    /**
     * Contenedor de datos del estado de la partida cargada.
     */
    public static class EstadoPartidaGuardada { 
        public Personaje heroe;
        public Personaje villano;
        public int partidaActual;
        public int totalPartidas;
        
        public String heroeArmaNombre;
        public int heroeSupremos;
        public String villanoArmaNombre;
        public int villanoSupremos;
    }
    
    // Reconstruye un objeto Arma a partir de su nombre.
    private static Arma crearArmaPorNombre(String nombre) {
        return switch (nombre.replace("_", " ")) {
            case "Espada Simple" -> new EspadaSimple();
            case "Espada Sagrada" -> new EspadaSagrada();
            case "Espada Celestial" -> new EspadaCelestial();
            case "Hoz Oxidada" -> new HozOxidada();
            case "Hoz Venenosa" -> new HozVenenosa();
            case "Hoz Mortifera" -> new HozMortifera();
            default -> null; 
        };
    }
    
    // Lógica para escribir el estado de un Personaje.
    private static void escribirPersonaje(BufferedWriter writer, Personaje p) throws IOException {
        String tipo = (p instanceof Heroe) ? "Heroe" : "Villano";
        
        String linea1 = String.format("%s%s%s%s%d%s%d%s%d%s%d",
                tipo, SEPARATOR, p.getNombre(), SEPARATOR, p.getVida(), SEPARATOR,
                p.getFuerza(), SEPARATOR, p.getDefensa(), SEPARATOR, p.getBendicion());
        writer.write(linea1);
        writer.newLine();

        String armaNombre = (p.getArmaActual() != null) ? p.getArmaActual().getNombre() : "NULL";
        String linea2 = String.format("%s%s%d", armaNombre.replace(" ", "_"), SEPARATOR, p.getSupremosUsados());
        writer.write(linea2);
        writer.newLine();

        List<String> armasInv = new ArrayList<>();
        for (Arma a : p.getArmasInvocadas()) {
            armasInv.add(a.getNombre().replace(" ", "_"));
        }
        writer.write(String.join(SEPARATOR, armasInv));
        writer.newLine();
    }
    
    // Lógica para leer el estado de un Personaje.
    private static Personaje leerPersonaje(BufferedReader reader, EstadoPartidaGuardada estado, boolean isHeroe) throws IOException, IllegalArgumentException {
        String[] stats = reader.readLine().split("\\" + SEPARATOR);
        if (stats.length < 6) throw new IOException("Error en formato de línea 1 del personaje.");
        String tipo = stats[0].trim();
        String nombre = stats[1].trim();
        int vida = Integer.parseInt(stats[2].trim());
        int fuerza = Integer.parseInt(stats[3].trim());
        int defensaBase = Integer.parseInt(stats[4].trim());
        int bendicion = Integer.parseInt(stats[5].trim());

        String[] armaData = reader.readLine().split("\\" + SEPARATOR);
        if (armaData.length < 2) throw new IOException("Error en formato de línea 2 del personaje.");
        String armaNombre = armaData[0].trim();
        int supremos = Integer.parseInt(armaData[1].trim());

        reader.readLine(); // 3. Ignorar la línea de armas invocadas (no se puede reconstruir)
        
        Personaje p;
        if (tipo.equals("Heroe")) {
            p = new Heroe(nombre, vida, fuerza, defensaBase, bendicion);
            if (isHeroe) {
                 estado.heroeArmaNombre = armaNombre;
                 estado.heroeSupremos = supremos;
            }
        } else if (tipo.equals("Villano")) {
            p = new Villano(nombre, vida, fuerza, defensaBase, bendicion);
            if (!isHeroe) {
                 estado.villanoArmaNombre = armaNombre;
                 estado.villanoSupremos = supremos;
            }
        } else {
            throw new IllegalArgumentException("Tipo de personaje desconocido: " + tipo);
        }
        
        return p;
    }
    
    // =======================================================
    // 🔹 Guardado/Cargado MANUAL (batalla_guardada.txt)
    // =======================================================
    public static void guardarPartida(Personaje heroe, Personaje villano, int partidaActual, int totalPartidas) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            writer.write(partidaActual + SEPARATOR + totalPartidas);
            writer.newLine();
            escribirPersonaje(writer, heroe);
            escribirPersonaje(writer, villano);
        }
    }

    public static EstadoPartidaGuardada cargarPartida() throws IOException, IllegalArgumentException {
        EstadoPartidaGuardada estado = new EstadoPartidaGuardada();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            line = reader.readLine();
            if (line == null) throw new IOException("Archivo de guardado vacío.");
            String[] meta = line.split("\\" + SEPARATOR);
            estado.partidaActual = Integer.parseInt(meta[0].trim());
            estado.totalPartidas = Integer.parseInt(meta[1].trim());
            estado.heroe = leerPersonaje(reader, estado, true);
            estado.villano = leerPersonaje(reader, estado, false);

        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("No se encontró el archivo de partida guardada (" + FILE_NAME + ").");
        }
        return estado;
    }

    // =======================================================
    // 🔹 [AÑADIDO] Guardado/Cargado HISTÓRICO (Punto 5 - Permanente)
    // =======================================================

    /** Guarda un solo resultado de batalla en el historial. */
    public static void guardarResultadoBatalla(RegistroBatalla registro) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(HISTORIAL_FILE, true))) { // true = append
            String linea = String.format("%d,%s,%s,%s,%d",
                    registro.getNumero(),
                    registro.getHeroe(),
                    registro.getVillano(),
                    registro.getGanador(),
                    registro.getTurnos());
            writer.write(linea);
            writer.newLine();
        }
    }

    /** Carga las últimas 5 batallas del historial. */
    public static List<RegistroBatalla> cargarHistorialBatallas() {
        List<RegistroBatalla> historial = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(HISTORIAL_FILE))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                try {
                    String[] p = linea.split(",");
                    historial.add(new RegistroBatalla(
                            Integer.parseInt(p[0]), p[1], p[2], p[3], Integer.parseInt(p[4])
                    ));
                } catch (Exception e) {
                    System.err.println("Error al leer línea de historial (saltada): " + linea);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("No se encontró " + HISTORIAL_FILE + ", se creará uno nuevo.");
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // Devolver solo los últimos 5 (según PDF)
        int total = historial.size();
        return historial.subList(Math.max(0, total - 5), total);
    }
    
    /** Carga el ranking completo desde personajes.txt */
    public static List<ResumenJugador> cargarRankingPersonajes() {
        Map<String, ResumenJugador> mapa = leerRankingMap();
        return new ArrayList<>(mapa.values());
    }
    
    /** Actualiza el ranking permanente (personajes.txt) */
    public static void actualizarRankingPersonajes(List<ResumenJugador> resumenesDeEstaBatalla) throws IOException {
        Map<String, ResumenJugador> mapa = leerRankingMap();

        for (ResumenJugador rNuevo : resumenesDeEstaBatalla) {
            String apodo = rNuevo.getApodo();
            ResumenJugador rViejo = mapa.getOrDefault(apodo, rNuevo);

            if (rViejo == rNuevo) {
                mapa.put(apodo, rNuevo);
            } else {
                ResumenJugador actualizado = new ResumenJugador(
                    rNuevo.getNombre(), 
                    rViejo.getApodo(), 
                    rViejo.getTipo(),  
                    rNuevo.getVidaFinal(), // Vida final de esta batalla
                    rViejo.getVictorias() + rNuevo.getVictorias(), // Sumar victorias
                    rViejo.getSupremosUsados() + rNuevo.getSupremosUsados(), // Sumar supremos
                    rViejo.getArmasInvocadas() + rNuevo.getArmasInvocadas() // Sumar armas
                );
                mapa.put(apodo, actualizado);
            }
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(RANKING_FILE, false))) { // false = sobrescribir
            for (ResumenJugador r : mapa.values()) {
                String linea = String.format("%s,%s,%s,%d,%d,%d,%d",
                        r.getNombre(), r.getApodo(), r.getTipo(), r.getVidaFinal(),
                        r.getVictorias(), r.getSupremosUsados(), r.getArmasInvocadas()
                );
                writer.write(linea);
                writer.newLine();
            }
        }
    }

    /** Helper para leer personajes.txt a un Mapa para fácil actualización. */
    private static Map<String, ResumenJugador> leerRankingMap() {
        Map<String, ResumenJugador> mapa = new LinkedHashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(RANKING_FILE))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                try {
                    String[] p = linea.split(",");
                    ResumenJugador r = new ResumenJugador(
                            p[0], p[1], p[2], Integer.parseInt(p[3]), 
                            Integer.parseInt(p[4]), Integer.parseInt(p[5]), Integer.parseInt(p[6])
                    );
                    mapa.put(r.getApodo(), r); // Usar apodo como clave
                } catch (Exception e) {
                    System.err.println("Error al leer línea de ranking (saltada): " + linea);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("No se encontró " + RANKING_FILE + ", se creará uno nuevo.");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mapa;
    }
}