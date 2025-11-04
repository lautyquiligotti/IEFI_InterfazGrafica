package batalla.controlador;

import batalla.vista.ConfiguracionInicial;
import javax.swing.JOptionPane;
import javax.swing.SpinnerNumberModel;

public class Wiring {

    public static void configurar(ConfiguracionInicial vista, ControladorBatalla controlador) {

        // 1) Combo tipo
        vista.getCmbTipo().removeAllItems();
        vista.getCmbTipo().addItem("Heroe");
        vista.getCmbTipo().addItem("Villano");

        // 2) Agregar personaje
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

        // 3) Eliminar por apodo
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

        // 4) Spinners (valor inicial, mínimo, máximo, paso)
        vista.getSpnVidainicial().setModel(new SpinnerNumberModel(120, 100, 160, 1)); // Vida
        vista.getSpnFuerzainicial1().setModel(new SpinnerNumberModel(20, 15, 25, 1)); // Fuerza
        vista.getSpnFuerzainicial().setModel(new SpinnerNumberModel(10, 8, 13, 1));   // Defensa
        vista.getSpnBendicioninicial().setModel(new SpinnerNumberModel(50, 30, 100, 1)); // Bendición

        // 5) Botón "Cambiar" (randomiza dentro del rango)
        java.util.Random rnd = new java.util.Random();
        vista.getBtnCambiar().addActionListener(e -> {
            vista.getSpnVidainicial().setValue(100 + rnd.nextInt(61));   // 100..160
            vista.getSpnFuerzainicial1().setValue(15 + rnd.nextInt(11)); // 15..25
            vista.getSpnFuerzainicial().setValue(8 + rnd.nextInt(6));    // 8..13
            vista.getSpnBendicioninicial().setValue(30 + rnd.nextInt(71)); // 30..100
        });
    } 

} 

