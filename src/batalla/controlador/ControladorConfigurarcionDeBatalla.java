package batalla.controlador;

import batalla.vista.ConfiguracionInicial;
import batalla.vista.BatallaVentanaBasica;
import javax.swing.JOptionPane;
import javax.swing.SpinnerNumberModel;
import java.util.*;

public class ControladorConfigurarcionDeBatalla {

    public static class ConfigPartida {
        public int vida, fuerza, defensa, bendicion, cantidad;
    }

    private static class EstadoA2 {
        enum TipoPersonaje { HEROE, VILLANO }
        static class RegistroJugador {
            final String nombre, apodo; final TipoPersonaje tipo;
            RegistroJugador(String n, String a, TipoPersonaje t){ nombre=n; apodo=a; tipo=t; }
        }
        final List<RegistroJugador> registrados = new ArrayList<>();
        String apodoHeroe = null, apodoVillano = null;
        void agregarJugador(String nombre, String apodo, String tipoStr){
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

        boolean eliminarPorApodo(String apodo){
            if (apodo == null || apodo.isBlank()) return false;
            String ap = apodo.trim();
            Optional<RegistroJugador> f = registrados.stream()
                .filter(r -> r.apodo.equalsIgnoreCase(ap))
                .findFirst();
            if (f.isEmpty()) return false;
            var r = f.get();
            if (r.tipo == TipoPersonaje.HEROE  && apodoHeroe  != null && apodoHeroe.equalsIgnoreCase(ap))  apodoHeroe  = null;
            if (r.tipo == TipoPersonaje.VILLANO && apodoVillano!= null && apodoVillano.equalsIgnoreCase(ap)) apodoVillano= null;
            return registrados.remove(r);
        }
        boolean tieneHeroeYVillano(){ return apodoHeroe != null && apodoVillano != null; }
    }

    public static void configurar(ConfiguracionInicial vista, ControladorBatalla ctrlBatalla) {
        final EstadoA2 estado = new EstadoA2();
        final Random rnd = new Random();

        // 1) Combo tipo
        vista.getCmbTipo().removeAllItems();
        vista.getCmbTipo().addItem("Heroe");
        vista.getCmbTipo().addItem("Villano");

        // 2) Spinners y combo // configura los atributos iniciales
        vista.getSpnVidainicial().setModel(new SpinnerNumberModel(120, 100, 160, 1));
        vista.getSpnFuerzainicial1().setModel(new SpinnerNumberModel(20,  15,  25,  1));
        vista.getSpnDefensainicial().setModel(new SpinnerNumberModel(10,   8,  13,  1));
        vista.getSpnBendicioninicial().setModel(new SpinnerNumberModel(50,  30, 100,  1));

        vista.getCmbCantidadBatallas().removeAllItems();
        vista.getCmbCantidadBatallas().addItem("2");
        vista.getCmbCantidadBatallas().addItem("3");
        vista.getCmbCantidadBatallas().addItem("5");
        vista.getCmbCantidadBatallas().setSelectedItem("3");

        // 3) Random + default aleatorio
        vista.getBtnCambiar().addActionListener(e -> {
            vista.getSpnVidainicial().setValue(100 + rnd.nextInt(61));
            vista.getSpnFuerzainicial1().setValue(15  + rnd.nextInt(11));
            vista.getSpnDefensainicial().setValue(8   + rnd.nextInt(6));
            vista.getSpnBendicioninicial().setValue(30 + rnd.nextInt(71));
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

        // 6) Iniciar
        vista.getBtnIniciar().addActionListener(e -> {
            try {
                if (!estado.tieneHeroeYVillano()){
                    JOptionPane.showMessageDialog(vista,"Debe haber 1 HÉROE y 1 VILLANO.","Validación", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                ConfigPartida cfg = new ConfigPartida();
                cfg.vida      = (Integer) vista.getSpnVidainicial().getValue();
                cfg.fuerza    = (Integer) vista.getSpnFuerzainicial1().getValue();
                cfg.defensa   = (Integer) vista.getSpnDefensainicial().getValue();
                cfg.bendicion = (Integer) vista.getSpnBendicioninicial().getValue();
                cfg.cantidad  = Integer.parseInt((String) vista.getCmbCantidadBatallas().getSelectedItem());
                if (cfg.cantidad != 2 && cfg.cantidad != 3 && cfg.cantidad != 5) cfg.cantidad = 3;

                // abrir ventana básica y engancharla como listener
                BatallaVentanaBasica vb = new BatallaVentanaBasica();
                ctrlBatalla.setListener(vb);
                vb.setVisible(true);

                // correr N batallas segun cfg.cantidad
                for (int i = 1; i <= cfg.cantidad; i++) {
                    ctrlBatalla.iniciarBatalla(estado.apodoHeroe, estado.apodoVillano, cfg);
                }

            } catch (Exception ex){
                JOptionPane.showMessageDialog(vista, "Error al iniciar: " + ex.getMessage(),"Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Cargar / Salir
        vista.getBtnCargar().addActionListener(e ->
            JOptionPane.showMessageDialog(vista,"Funcionalidad de carga no implementada todavía.","Aviso",JOptionPane.INFORMATION_MESSAGE)
        );
        vista.getBtnSalir().addActionListener(e -> System.exit(0));
    }
}



