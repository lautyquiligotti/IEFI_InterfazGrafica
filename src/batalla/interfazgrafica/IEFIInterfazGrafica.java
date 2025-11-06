package batalla.interfazgrafica;

import batalla.vista.ConfiguracionInicial;
import batalla.controlador.ControladorBatalla;
import batalla.controlador.ControladorConfigurarcionDeBatalla; // nombre exacto
import javax.swing.*;

public class IEFIInterfazGrafica extends JFrame {

    public IEFIInterfazGrafica() {
        // 1) Crear controladores y vista
        ControladorBatalla ctrlBatalla = new ControladorBatalla();    // solo pelea (A3)
        ConfiguracionInicial vistaConfig = new ConfiguracionInicial();// pantalla de config (A2)

        // 2) Conectar vista + TU controlador de config (A2)
        ControladorConfigurarcionDeBatalla.configurar(vistaConfig, ctrlBatalla);

        // 3) Mostrar
        vistaConfig.setLocationRelativeTo(null);
        vistaConfig.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(IEFIInterfazGrafica::new);
    }
}




