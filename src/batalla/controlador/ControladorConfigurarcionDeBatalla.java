package batalla.controlador;

// --- Herramientas que vamos a usar ---
import batalla.vista.ConfiguracionInicial;   // La 1ra ventana (el .form)
import batalla.vista.VentanaPrincipalJuego; // La 2da ventana (la del juego)
import javax.swing.JOptionPane;           
import javax.swing.SpinnerNumberModel;   
import java.util.*;                       
import java.io.FileNotFoundException;     


public class ControladorConfigurarcionDeBatalla {

    
    public static class ConfigPartida { //Clase configuracion de partida/parametros DTO PARA PASAR VALORES
        public int vida, fuerza, defensa, bendicion, cantidad;
    }

    
    private static class EstadoA2 { //Clase estado 
        enum TipoPersonaje { HEROE, VILLANO }                                                                                                                                                                    

        
        static class RegistroJugador { //Clase para Regitrar un jugador 
            final String nombre, apodo; 
            final TipoPersonaje tipo;
            RegistroJugador(String n, String a, TipoPersonaje t) {
                nombre = n; apodo = a; tipo = t;
            }
        }

        // Lo metemos en una lista 
        final List<RegistroJugador> registrados = new ArrayList<>();
        
        //Creamos Apodos 
        String apodoHeroe = null;
        String apodoVillano = null;

        
        void agregarJugador(String nombre, String apodo, String tipoStr) { //Metodo que se utiliza para agregar jugador 
            if (nombre == null || nombre.trim().isEmpty())
                throw new IllegalArgumentException("El nombre no puede estar vacío.");
            
            if (!ValidacionApodos.esValido(apodo))
                throw new IllegalArgumentException("Apodo inválido (3-10 caracteres, solo letras y espacios).");
            
            String ap = apodo.trim();
            boolean dup = registrados.stream().anyMatch(r -> r.apodo.equalsIgnoreCase(ap));
            if (dup) throw new IllegalArgumentException("Ya existe un personaje con ese apodo.");
            
           
            if (tipoStr == null) throw new IllegalArgumentException("Tipo no seleccionado.");
            TipoPersonaje tipo;
            switch (tipoStr.toLowerCase()) {
                case "heroe": case "héroe": tipo = TipoPersonaje.HEROE; break;
                case "villano":             tipo = TipoPersonaje.VILLANO; break;
                default: throw new IllegalArgumentException("Tipo desconocido: " + tipoStr);
            }

            //Validamos que no se repita el pj 
            if (tipo == TipoPersonaje.HEROE) {
                if (apodoHeroe != null) throw new IllegalArgumentException("Ya hay un Héroe registrado.");
                apodoHeroe = ap; 
            } else {
                if (apodoVillano != null) throw new IllegalArgumentException("Ya hay un Villano registrado.");
                apodoVillano = ap; 
            }
            
            // Si Cumple con las condicione se añade 
            registrados.add(new RegistroJugador(nombre.trim(), ap, tipo));
        }

        
        boolean eliminarPorApodo(String apodo) {
            
            Optional<RegistroJugador> f = registrados.stream()//Filtramos por apodo 
                    .filter(r -> r.apodo.equalsIgnoreCase(apodo.trim()))
                    .findFirst();
            if (f.isEmpty()) return false; // No estaba en la lista

            // Si lo encontramos, bajamos la "bandera"
            var r = f.get();
            if (r.tipo == TipoPersonaje.HEROE) apodoHeroe = null; //Pregunta
            if (r.tipo == TipoPersonaje.VILLANO) apodoVillano = null;
            
            return registrados.remove(r); //Se elimina 
        }

        
        boolean tieneHeroeYVillano() {
            return apodoHeroe != null && apodoVillano != null;
        }

        //Verificamos 
        RegistroJugador getHeroe() {
            return registrados.stream().filter(r -> r.tipo == TipoPersonaje.HEROE).findFirst().orElse(null);
        }
        RegistroJugador getVillano() {
            return registrados.stream().filter(r -> r.tipo == TipoPersonaje.VILLANO).findFirst().orElse(null);
        }
    } 

    
    
    public static void configurar(ConfiguracionInicial vista, ControladorBatalla ctrlBatalla) { // unimos Formularios
        
        
        final EstadoA2 estado = new EstadoA2(); //Instaciamos 
        
        final Random rnd = new Random(); 

        
        
        vista.getCmbTipo().removeAllItems();
        vista.getCmbTipo().addItem("Heroe");
        vista.getCmbTipo().addItem("Villano");
        
        vista.getSpnVidainicial().setModel(new SpinnerNumberModel(120, 100, 160, 1));//Conectamos
        vista.getSpnFuerzainicial1().setModel(new SpinnerNumberModel(20, 15, 25, 1));
        vista.getSpnDefensainicial().setModel(new SpinnerNumberModel(10, 8, 13, 1));
        vista.getSpnBendicioninicial().setModel(new SpinnerNumberModel(50, 30, 100, 1));

        vista.getCmbCantidadBatallas().removeAllItems();
        vista.getCmbCantidadBatallas().addItem("2");
        vista.getCmbCantidadBatallas().addItem("3");
        vista.getCmbCantidadBatallas().addItem("5");
        vista.getCmbCantidadBatallas().setSelectedItem("3");

        

        // Botón "RANDOMIZAR"
        vista.getBtnCambiar().addActionListener(e -> {
            vista.getSpnVidainicial().setValue(100 + rnd.nextInt(61));
            vista.getSpnFuerzainicial1().setValue(15 + rnd.nextInt(11));
            vista.getSpnDefensainicial().setValue(8 + rnd.nextInt(6));
            vista.getSpnBendicioninicial().setValue(30 + rnd.nextInt(71));
        });
        vista.getBtnCambiar().doClick(); // Lo apretamos una vez al inicio

        // Botón "AGREGAR"
        vista.getBtnAgregar().addActionListener(e -> {
            try {
                // Llama al "Guardia de Seguridad" (el método agregarJugador)
                estado.agregarJugador(
                        vista.getTxtNombre1().getText(),
                        vista.getTxtApodo().getText(),
                        (String) vista.getCmbTipo().getSelectedItem()
                );
                // Si el guardia no se quejó (no hubo error), mostramos OK
                JOptionPane.showMessageDialog(vista, "Personaje agregado ✅");
                vista.getTxtNombre1().setText("");
                vista.getTxtApodo().setText("");
            } catch (IllegalArgumentException ex) {
                // Si el guardia se quejó (tiró un error), mostramos el error
                JOptionPane.showMessageDialog(vista, ex.getMessage(), "Validación", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(vista, "Error inesperado: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Botón "ELIMINAR"
        vista.getBtnEliminar().addActionListener(e -> {
            // Llama al "Guardia" de eliminar y avisa si pudo o no
            boolean ok = estado.eliminarPorApodo(vista.getTxtApodo().getText());
            JOptionPane.showMessageDialog(
                    vista,
                    ok ? "Personaje eliminado ✅" : "No se encontró ese apodo.",
                    ok ? "OK" : "Aviso",
                    JOptionPane.INFORMATION_MESSAGE
            );
        });
        //hasta aca 
     
        vista.getBtnIniciar().addActionListener(e -> {
            try {
                
                if (!estado.tieneHeroeYVillano()) {
                    JOptionPane.showMessageDialog(
                            vista, "Debe haber 1 HÉROE y 1 VILLANO.",
                            "Validación", JOptionPane.WARNING_MESSAGE
                    );
                    return; 
                }

                
                ConfigPartida cfg = new ConfigPartida();
                cfg.vida = (Integer) vista.getSpnVidainicial().getValue();
                cfg.fuerza = (Integer) vista.getSpnFuerzainicial1().getValue();
                cfg.defensa = (Integer) vista.getSpnDefensainicial().getValue();
                cfg.bendicion = (Integer) vista.getSpnBendicioninicial().getValue();
                cfg.cantidad = Integer.parseInt((String) vista.getCmbCantidadBatallas().getSelectedItem());
                if (cfg.cantidad != 2 && cfg.cantidad != 3 && cfg.cantidad != 5) cfg.cantidad = 3;

               
                EstadoA2.RegistroJugador hReg = estado.getHeroe();
                EstadoA2.RegistroJugador vReg = estado.getVillano();

                
                batalla.modelo.Heroe heroeUI = new batalla.modelo.Heroe(
                        hReg != null ? hReg.nombre : estado.apodoHeroe, cfg.vida, cfg.fuerza, cfg.defensa, cfg.bendicion);
                batalla.modelo.Villano villanoUI = new batalla.modelo.Villano(
                        vReg != null ? vReg.nombre : estado.apodoVillano, cfg.vida, cfg.fuerza, cfg.defensa, cfg.bendicion);

                
                VentanaPrincipalJuego vpj = new VentanaPrincipalJuego();
                ControladorVentanaPrincipalJuego ctrlJuego =
                        new ControladorVentanaPrincipalJuego(vpj, ctrlBatalla, heroeUI, villanoUI, cfg.cantidad);

                
                ctrlBatalla.setListener(ctrlJuego);

                
                Thread t = new Thread(() -> {
                    
                    for (int i = 1; i <= cfg.cantidad; i++) {
                        
                        ctrlJuego.actualizarPartida(i, cfg.cantidad);
                        
                        ctrlBatalla.iniciarBatalla(heroeUI.getNombre(), villanoUI.getNombre(), cfg);
                    }
                });

               
                javax.swing.SwingUtilities.invokeLater(() -> {
                    vpj.setVisible(true); 
                    vista.dispose();      
                    t.start();            
                });

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                        vista, "Error al iniciar: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE
                );
            }
        });

        // ===================================================================
        // 7) Botón "CARGAR"
        // ===================================================================
        vista.getBtnCargar().addActionListener(e -> {
            try {
                
                ServicioPersistencia.EstadoPartidaGuardada estadoCargado =
                        ServicioPersistencia.cargarPartida();

                
                VentanaPrincipalJuego vpj = new VentanaPrincipalJuego();
                ControladorVentanaPrincipalJuego ctrlJuego =
                        new ControladorVentanaPrincipalJuego(
                                vpj, ctrlBatalla,
                                estadoCargado.heroe, 
                                estadoCargado.villano, 
                                estadoCargado.totalPartidas
                        );
                
                ctrlBatalla.setListener(ctrlJuego);

                
                javax.swing.SwingUtilities.invokeLater(() -> {
                    vpj.setVisible(true);
                    vista.dispose();
                    // Este es el cartelito que querías vos:
                    JOptionPane.showMessageDialog(
                            vpj,
                            "Partida base cargada (Héroe y Villano restaurados).\n" +
                            "Nota: Arma/effects transitorios no se restauran con esta estructura.",
                            "Carga exitosa",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                });

            } catch (FileNotFoundException ex1) {
                
                JOptionPane.showMessageDialog(vista,
                        "No se encontró el archivo '" + ServicioPersistencia.FILE_NAME + "'. Guarde una partida primero.",
                        "Error de Carga", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) {
                // Otro error (ej. archivo roto)
                JOptionPane.showMessageDialog(vista,
                        "Error al cargar la partida: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        
        vista.getBtnSalir().addActionListener(e -> System.exit(0)); // Cierra el programa
    }
}