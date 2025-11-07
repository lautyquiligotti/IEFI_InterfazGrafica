package batalla.interfazgrafica;

import batalla.vista.ConfiguracionInicial;
import batalla.controlador.ControladorBatalla;
import batalla.controlador.ControladorConfigurarcionDeBatalla;
import javax.swing.SwingUtilities;

public class IEFIInterfazGrafica {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // 1) Crear controladores y vista
            ControladorBatalla ctrlBatalla = new ControladorBatalla();
            ConfiguracionInicial vistaConfig = new ConfiguracionInicial();

            // 2) Conectar vista + controlador de configuración
            ControladorConfigurarcionDeBatalla.configurar(vistaConfig, ctrlBatalla);

            // 3) Mostrar
            vistaConfig.setLocationRelativeTo(null);
            vistaConfig.setVisible(true);
        });
    }
}


