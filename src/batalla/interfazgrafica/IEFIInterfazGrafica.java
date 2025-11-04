package batalla.interfazgrafica;

import batalla.vista.ConfiguracionInicial;
import batalla.controlador.ControladorBatalla;
import batalla.controlador.Wiring;
import javax.swing.*;

public class IEFIInterfazGrafica extends JFrame {

    public IEFIInterfazGrafica() {
        // Inicializamos el controlador (lógica del juego)
        ControladorBatalla controlador = new ControladorBatalla();

        // Creamos la ventana de configuración (vista)
        ConfiguracionInicial vistaConfiguracion = new ConfiguracionInicial();

        // Conectamos vista y controlador
        Wiring.configurar(vistaConfiguracion, controlador);

        // Mostramos la ventana centrada
        vistaConfiguracion.setLocationRelativeTo(null);
        vistaConfiguracion.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new IEFIInterfazGrafica();  // se ejecuta todo desde acá
        });
    }
}

