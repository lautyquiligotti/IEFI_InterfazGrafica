package batalla.interfazgrafica;

import batalla.controlador.ControladorBatalla;
import javax.swing.*;

public class IEFIInterfazGrafica extends JFrame {

    private ControladorBatalla controlador;

    public IEFIInterfazGrafica() {
        controlador = new ControladorBatalla();
        initUI();
    }

    private void initUI() {
        setTitle("Batalla Épica - Interfaz Gráfica");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JButton iniciarBtn = new JButton("Iniciar Batalla");
        iniciarBtn.addActionListener(e -> {
            String heroe = JOptionPane.showInputDialog("Nombre del héroe:");
            String villano = JOptionPane.showInputDialog("Nombre del villano:");
            controlador.iniciarBatalla(heroe, villano);
        });

        add(iniciarBtn);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new IEFIInterfazGrafica().setVisible(true);
        });
    }
}
