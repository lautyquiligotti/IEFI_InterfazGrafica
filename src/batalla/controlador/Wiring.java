package batalla.controlador;

import batalla.vista.ConfiguracionInicial;
import javax.swing.JOptionPane;

public class Wiring {

    public static void configurar(ConfiguracionInicial vista, ControladorBatalla controlador) {

        // 🧩 1. Configurar el ComboBox
        vista.getCmbTipo().removeAllItems();
        vista.getCmbTipo().addItem("Heroe");
        vista.getCmbTipo().addItem("Villano");

        // 🧩 2. Botón AGREGAR → llama a ControladorBatalla.agregarJugador()
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
                JOptionPane.showMessageDialog(vista, ex.getMessage(), "Validación", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(vista, "Error inesperado: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // 🧩 3. Botón ELIMINAR → llama a ControladorBatalla.eliminarPorApodo()
        vista.getBtnEliminar().addActionListener(e -> {
            String apodo = vista.getTxtApodo().getText();
            boolean ok = controlador.eliminarPorApodo(apodo);

            if (ok) {
                JOptionPane.showMessageDialog(vista, "Personaje eliminado ✅");
            } else {
                JOptionPane.showMessageDialog(vista, "No se encontró ese apodo.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }
}


