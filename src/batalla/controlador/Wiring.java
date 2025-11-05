package batalla.controlador;

import batalla.vista.ConfiguracionInicial;
import javax.swing.JOptionPane;
import javax.swing.SpinnerNumberModel;

public class Wiring {

    public static void configurar(ConfiguracionInicial vista, ControladorBatalla controlador) {

        // ==============================
        // 1) Combo "Tipo" (Héroe / Villano)
        // ==============================
        vista.getCmbTipo().removeAllItems();
        vista.getCmbTipo().addItem("Heroe");
        vista.getCmbTipo().addItem("Villano");

        // ==============================
        // 2) Registro de jugadores
        // ==============================
        // Agregar
        vista.getBtnAgregar().addActionListener(e -> {
            try {
                controlador.agregarJugador(
                    vista.getTxtNombre1().getText(),
                    vista.getTxtApodo().getText(),
                    (String) vista.getCmbTipo().getSelectedItem()
                );
                JOptionPane.showMessageDialog(vista, "Personaje agregado ✅");
                vista.getTxtNombre1().setText("");
                vista.getTxtApodo().setText("");
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(
                    vista, ex.getMessage(), "Validación", JOptionPane.WARNING_MESSAGE
                );
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                    vista, "Error inesperado: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE
                );
            }
        });

        // Eliminar
        vista.getBtnEliminar().addActionListener(e -> {
            String apodo = vista.getTxtApodo().getText();
            boolean ok = controlador.eliminarPorApodo(apodo);
            if (ok) {
                JOptionPane.showMessageDialog(vista, "Personaje eliminado ✅");
            } else {
                JOptionPane.showMessageDialog(
                    vista, "No se encontró ese apodo.", "Aviso", JOptionPane.INFORMATION_MESSAGE
                );
            }
        });

        // ==============================
        // 3) Configuración de partida: Spinners y Combo de cantidad
        // ==============================
        // Spinners (valor inicial, mínimo, máximo, paso)
        vista.getSpnVidainicial().setModel(    new SpinnerNumberModel(120, 100, 160, 1)); // Vida
        vista.getSpnFuerzainicial1().setModel( new SpinnerNumberModel(20,  15,  25,  1)); // Fuerza
        vista.getSpnFuerzainicial().setModel(  new SpinnerNumberModel(10,  8,   13,  1)); // Defensa
        vista.getSpnBendicioninicial().setModel(new SpinnerNumberModel(50,  30,  100, 1)); // Bendición

        // Combo "Cantidad de batallas" (2,3,5)
        vista.getCmbCantidadBatallas().removeAllItems();
        vista.getCmbCantidadBatallas().addItem("2");
        vista.getCmbCantidadBatallas().addItem("3");
        vista.getCmbCantidadBatallas().addItem("5");
        vista.getCmbCantidadBatallas().setSelectedItem("3");

        // ==============================
        // 4) Botones de control (Iniciar / Cargar / Salir)
        // ==============================
        // Iniciar Batalla
        vista.getBtnIniciar().addActionListener(e -> {
            try {
                // Validar que haya 1 HÉROE y 1 VILLANO registrados
                if (!controlador.tieneHeroeYVillano()) {
                    JOptionPane.showMessageDialog(
                        vista,
                        "Debe haber 1 HÉROE y 1 VILLANO antes de iniciar la batalla.",
                        "Validación",
                        JOptionPane.WARNING_MESSAGE
                    );
                    return;
                }

                // Tomar valores de configuración
                int vida      = (Integer) vista.getSpnVidainicial().getValue();
                int fuerza    = (Integer) vista.getSpnFuerzainicial1().getValue();
                int defensa   = (Integer) vista.getSpnFuerzainicial().getValue();
                int bendicion = (Integer) vista.getSpnBendicioninicial().getValue();
                int cantidad  = Integer.parseInt((String) vista.getCmbCantidadBatallas().getSelectedItem());

                // Configurar partida y lanzar batalla
                controlador.configurarPartida(vida, fuerza, defensa, bendicion, cantidad);
                controlador.iniciarBatallaDesdeRegistro();

                JOptionPane.showMessageDialog(vista, "¡Batalla iniciada correctamente!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                    vista,
                    "Error al iniciar batalla: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        });

        // Cargar Batalla Guardada (placeholder)
        vista.getBtnCargar().addActionListener(e -> {
            JOptionPane.showMessageDialog(
                vista,
                "Funcionalidad de carga no implementada todavía.",
                "Aviso",
                JOptionPane.INFORMATION_MESSAGE
            );
        });

        // Salir
        vista.getBtnSalir().addActionListener(e -> System.exit(0));

        // ==============================
        // 5) Botón "Cambiar" → randomizar dentro del rango
        // ==============================
        java.util.Random rnd = new java.util.Random();
        vista.getBtnCambiar().addActionListener(e -> {
            vista.getSpnVidainicial().setValue(   100 + rnd.nextInt(61)); // 100..160
            vista.getSpnFuerzainicial1().setValue(15  + rnd.nextInt(11)); // 15..25
            vista.getSpnFuerzainicial().setValue( 8   + rnd.nextInt(6));  // 8..13
            vista.getSpnBendicioninicial().setValue(30 + rnd.nextInt(71)); // 30..100
        });
    }
}


