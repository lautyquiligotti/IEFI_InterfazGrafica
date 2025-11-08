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
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ServicioPersistencia {
    
    // [CORREGIDO]: Hacemos que FILE_NAME sea public para que otros controladores puedan usarlo en mensajes.
    public static final String FILE_NAME = "batalla_guardada.txt";
    private static final String SEPARATOR = "|";

    /**
     * Contenedor de datos del estado de la partida cargada.
     * Es una clase anidada STATIC y PUBLIC para ser accesible desde otros controladores.
     */
    public static class EstadoPartidaGuardada { 
        public Personaje heroe;
        public Personaje villano;
        public int partidaActual;
        public int totalPartidas;
        
        // Atributos de estado que la carga intentará recuperar
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
        
        // 1. Stats básicos (Tipo, Nombre, Vida, Fuerza, DefensaBase, %Bendicion)
        String linea1 = String.format("%s%s%s%s%d%s%d%s%d%s%d",
                tipo, SEPARATOR, p.getNombre(), SEPARATOR, p.getVida(), SEPARATOR,
                p.getFuerza(), SEPARATOR, p.getDefensa(), SEPARATOR, p.getBendicion());
        writer.write(linea1);
        writer.newLine();

        // 2. Arma Actual y Supremos Usados
        String armaNombre = (p.getArmaActual() != null) ? p.getArmaActual().getNombre() : "NULL";
        String linea2 = String.format("%s%s%d", armaNombre.replace(" ", "_"), SEPARATOR, p.getSupremosUsados());
        writer.write(linea2);
        writer.newLine();

        // 3. Lista de Armas Invocadas (nombres separados por '|')
        List<String> armasInv = new ArrayList<>();
        for (Arma a : p.getArmasInvocadas()) {
            armasInv.add(a.getNombre().replace(" ", "_"));
        }
        writer.write(String.join(SEPARATOR, armasInv));
        writer.newLine();
    }
    
    // Lógica para leer el estado de un Personaje.
    private static Personaje leerPersonaje(BufferedReader reader, EstadoPartidaGuardada estado, boolean isHeroe) throws IOException, IllegalArgumentException {
        // 1. Stats básicos
        String[] stats = reader.readLine().split("\\" + SEPARATOR);
        if (stats.length < 6) throw new IOException("Error en formato de línea 1 del personaje.");
        String tipo = stats[0].trim();
        String nombre = stats[1].trim();
        int vida = Integer.parseInt(stats[2].trim());
        int fuerza = Integer.parseInt(stats[3].trim());
        int defensaBase = Integer.parseInt(stats[4].trim());
        int bendicion = Integer.parseInt(stats[5].trim());

        // 2. Arma Actual y Supremos Usados
        String[] armaData = reader.readLine().split("\\" + SEPARATOR);
        if (armaData.length < 2) throw new IOException("Error en formato de línea 2 del personaje.");
        String armaNombre = armaData[0].trim();
        int supremos = Integer.parseInt(armaData[1].trim());

        // 3. Lista de Armas Invocadas (se lee, pero no se puede reconstruir en Personaje)
        String armasInvLine = reader.readLine();
        
        // Crear personaje.
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
    // 🔹 Guardar Partida (Uso Público)
    // =======================================================
    public static void guardarPartida(Personaje heroe, Personaje villano, int partidaActual, int totalPartidas) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            // 1. Escribir metadatos de la partida
            writer.write(partidaActual + SEPARATOR + totalPartidas);
            writer.newLine();

            // 2. Escribir estado del Héroe
            escribirPersonaje(writer, heroe);

            // 3. Escribir estado del Villano
            escribirPersonaje(writer, villano);
        }
    }

    // =======================================================
    // 🔹 Cargar Partida (Uso Público)
    // =======================================================
    public static EstadoPartidaGuardada cargarPartida() throws IOException, IllegalArgumentException {
        EstadoPartidaGuardada estado = new EstadoPartidaGuardada();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;

            // 1. Leer metadatos de la partida
            line = reader.readLine();
            if (line == null) throw new IOException("Archivo de guardado vacío.");
            String[] meta = line.split("\\" + SEPARATOR);
            estado.partidaActual = Integer.parseInt(meta[0].trim());
            estado.totalPartidas = Integer.parseInt(meta[1].trim());

            // 2. Leer estado del Héroe
            estado.heroe = leerPersonaje(reader, estado, true);

            // 3. Leer estado del Villano
            estado.villano = leerPersonaje(reader, estado, false);

        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("No se encontró el archivo de partida guardada (" + FILE_NAME + ").");
        }
        return estado;
    }
}