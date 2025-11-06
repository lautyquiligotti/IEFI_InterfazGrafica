package batalla.controlador; // paquete de controlador

import batalla.vista.ConfiguracionInicial; // configuracion incial (la vista) es el formulario con campos y botones
import javax.swing.JOptionPane;
import javax.swing.SpinnerNumberModel;
import java.util.*;

/**
 * Controlador de la Ventana de Configuración de Batalla (Actividad 2).
 * Mantiene el registro de jugadores, validaciones, random y configuración.
 * Al iniciar, llama a ControladorBatalla con apodos + config.
 */
public class ControladorConfigurarcionDeBatalla { // Esta clase se encarga de la vista y la logica de esta ventana

    // Aca configuramos toda la partida para pasarlo al controlador de batalla
    public static class ConfigPartida {
        public int vida, fuerza, defensa, bendicion, cantidad;
    }

    // Registro y validaciones, basicamente mantenemos los datos internos osea que personajes se registran o si hay un heroe o villano
    private static class EstadoA2 {
        enum TipoPersonaje { HEROE, VILLANO }
        // Registramos cada jugador aca
        static class RegistroJugador {
            final String nombre, apodo; final TipoPersonaje tipo;
            RegistroJugador(String n, String a, TipoPersonaje t){ nombre=n; apodo=a; tipo=t; }
        }
        // Lo guardamos en una lista
        final List<RegistroJugador> registrados = new ArrayList<>();
        String apodoHeroe = null, apodoVillano = null;

        // usamos un metodo para agregarlo al estado
        void agregarJugador(String nombre, String apodo, String tipoStr){
            // Valida que el nombre no este vacio
            if (nombre == null || nombre.trim().isEmpty())
                throw new IllegalArgumentException("El nombre no puede estar vacío.");
            if (!ValidacionApodos.esValido(apodo))
                throw new IllegalArgumentException("Apodo inválido (3-10 caracteres, solo letras y espacios).");

            String ap = apodo.trim();
            boolean dup = registrados.stream().anyMatch(r -> r.apodo.equalsIgnoreCase(ap));
            if (dup) throw new IllegalArgumentException("Ya existe un personaje con ese apodo.");

            if (tipoStr == null) throw new IllegalArgumentException("Tipo no seleccionado.");
            TipoPersonaje tipo;
            switch (tipoStr.toLowerCase()){
                case "heroe": case "héroe": tipo = TipoPersonaje.HEROE; break;
                case "villano":             tipo = TipoPersonaje.VILLANO; break;
                default: throw new IllegalArgumentException("Tipo desconocido: "+tipoStr);
            }

            if (tipo == TipoPersonaje.HEROE){
                if (apodoHeroe != null) throw new IllegalArgumentException("Ya hay un Héroe registrado.");
                apodoHeroe = ap;
            } else {
                if (apodoVillano != null) throw new IllegalArgumentException("Ya hay un Villano registrado.");
                apodoVillano = ap;
            }
            registrados.add(new RegistroJugador(nombre.trim(), ap, tipo));
        }

        // Este metodo elimina por apodo: se busca que el apodo este en la lista
        boolean eliminarPorApodo(String apodo){
            if (apodo == null || apodo.isBlank()) return false;
            String ap = apodo.trim();
            Optional<RegistroJugador> f = registrados.stream()
                .filter(r -> r.apodo.equalsIgnoreCase(ap))
                .findFirst();
            if (f.isEmpty()) return false;
            var r = f.get();
            if (r.tipo == TipoPersonaje.HEROE  && apodoHeroe  != null && apodoHeroe.equalsIgnoreCase(ap))  apodoHeroe  = null; //
            if (r.tipo == TipoPersonaje.VILLANO && apodoVillano!= null && apodoVillano.equalsIgnoreCase(ap)) apodoVillano= null;
            return registrados.remove(r); // si lo encuentra lo elimina
        }

        boolean tieneHeroeYVillano(){ return apodoHeroe != null && apodoVillano != null; }
    }

    /**
     * Método estático (lo invoca el main): enchufa vista + lógica A2
     * y lanza la batalla con ControladorBatalla cuando corresponde.
     */
    public static void configurar(ConfiguracionInicial vista, ControladorBatalla ctrlBatalla) {
        final EstadoA2 estado = new EstadoA2();
        final Random rnd = new Random();

        // 1) Combo tipo
        vista.getCmbTipo().removeAllItems();
        vista.getCmbTipo().addItem("Heroe");
        vista.getCmbTipo().addItem("Villano");

        // 2) Spinners y combo // configura los atributos iniciales
        vista.getSpnVidainicial().setModel(    new SpinnerNumberModel(120, 100, 160, 1));
        vista.getSpnFuerzainicial1().setModel( new SpinnerNumberModel(20,  15,  25,  1));
        vista.getSpnDefensainicial().setModel( new SpinnerNumberModel(10,   8,  13,  1));
        vista.getSpnBendicioninicial().setModel(new SpinnerNumberModel(50,  30, 100,  1));

        vista.getCmbCantidadBatallas().removeAllItems();
        vista.getCmbCantidadBatallas().addItem("2");
        vista.getCmbCantidadBatallas().addItem("3");
        vista.getCmbCantidadBatallas().addItem("5");
        vista.getCmbCantidadBatallas().setSelectedItem("3");

        // 3) Random + default aleatorio
        vista.getBtnCambiar().addActionListener(e -> {
            vista.getSpnVidainicial().setValue(      100 + rnd.nextInt(61)); // 100..160
            vista.getSpnFuerzainicial1().setValue(   15  + rnd.nextInt(11)); // 15..25
            vista.getSpnDefensainicial().setValue(   8   + rnd.nextInt(6));  // 8..13
            vista.getSpnBendicioninicial().setValue( 30  + rnd.nextInt(71)); // 30..100
        });
        vista.getBtnCambiar().doClick();

        // 4) Agregar
        vista.getBtnAgregar().addActionListener(e -> {
            try {
                estado.agregarJugador(
                    vista.getTxtNombre1().getText(),
                    vista.getTxtApodo().getText(),
                    (String) vista.getCmbTipo().getSelectedItem()
                );
                JOptionPane.showMessageDialog(vista,"Personaje agregado ✅");
                vista.getTxtNombre1().setText("");
                vista.getTxtApodo().setText("");
            } catch (IllegalArgumentException ex){
                JOptionPane.showMessageDialog(vista, ex.getMessage(),"Validación", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex){
                JOptionPane.showMessageDialog(vista, "Error inesperado: "+ex.getMessage(),"Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // 5) Eliminar
        vista.getBtnEliminar().addActionListener(e -> {
            boolean ok = estado.eliminarPorApodo(vista.getTxtApodo().getText());
            JOptionPane.showMessageDialog(
                vista,
                ok ? "Personaje eliminado ✅" : "No se encontró ese apodo.",
                ok ? "OK" : "Aviso",
                JOptionPane.INFORMATION_MESSAGE
            );
        });

        // 6) Iniciar → arma config y llama al ControladorBatalla (versión consola)
        vista.getBtnIniciar().addActionListener(e -> {
            try {
                if (!estado.tieneHeroeYVillano()){
                    JOptionPane.showMessageDialog(
                        vista,
                        "Debe haber 1 HÉROE y 1 VILLANO.",
                        "Validación",
                        JOptionPane.WARNING_MESSAGE
                    );
                    return;
                }

                // Leer la configuración del formulario
                ControladorConfigurarcionDeBatalla.ConfigPartida cfg =
                        new ControladorConfigurarcionDeBatalla.ConfigPartida();
                cfg.vida      = (Integer) vista.getSpnVidainicial().getValue();
                cfg.fuerza    = (Integer) vista.getSpnFuerzainicial1().getValue();
                cfg.defensa   = (Integer) vista.getSpnDefensainicial().getValue();
                cfg.bendicion = (Integer) vista.getSpnBendicioninicial().getValue();
                cfg.cantidad  = Integer.parseInt((String) vista.getCmbCantidadBatallas().getSelectedItem());
                if (cfg.cantidad != 2 && cfg.cantidad != 3 && cfg.cantidad != 5) cfg.cantidad = 3;

                // ✅ Lanza la batalla: usa los valores del formulario si vienen en cfg
                ctrlBatalla.iniciarBatalla(estado.apodoHeroe, estado.apodoVillano, cfg);

                JOptionPane.showMessageDialog(
                    vista,
                    "¡Batalla iniciada!",
                    "Inicio de batalla",
                    JOptionPane.INFORMATION_MESSAGE
                );
            } catch (Exception ex){
                JOptionPane.showMessageDialog(
                    vista,
                    "Error al iniciar: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        });

        // 7) Cargar (placeholder) y Salir
        vista.getBtnCargar().addActionListener(e ->
            JOptionPane.showMessageDialog(
                vista,
                "Funcionalidad de carga no implementada todavía.",
                "Aviso",
                JOptionPane.INFORMATION_MESSAGE
            )
        );
        vista.getBtnSalir().addActionListener(e -> System.exit(0));
    }
}








