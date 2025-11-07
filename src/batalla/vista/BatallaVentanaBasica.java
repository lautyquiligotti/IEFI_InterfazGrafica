package batalla.vista;

import batalla.controlador.BatallaListener;
import batalla.modelo.Personaje;
import java.util.List;
import javax.swing.*;

/**
 * Ventana que muestra el desarrollo de la batalla. 
 * Implementa BatallaListener para reaccionar a los eventos del controlador.
 */
public class BatallaVentanaBasica extends javax.swing.JFrame implements BatallaListener {

    // 🔹 Constructor
    public BatallaVentanaBasica() {
        initComponents();
        setTitle("Simulación de Batalla");
        setLocationRelativeTo(null);
    }

    // 🔹 Inicialización del formulario (diseño)
    @SuppressWarnings("unchecked")
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        txtResultado = new javax.swing.JTextArea();
        btnCerrar = new javax.swing.JButton();
        lblTitulo = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        lblTitulo.setFont(new java.awt.Font("Segoe UI", 1, 18));
        lblTitulo.setText("⚔️ Batalla en curso ⚔️");

        txtResultado.setEditable(false);
        txtResultado.setColumns(20);
        txtResultado.setRows(10);
        txtResultado.setText("Esperando inicio de batalla...\n");
        jScrollPane1.setViewportView(txtResultado);

        btnCerrar.setText("Cerrar");
        btnCerrar.addActionListener(evt -> dispose());

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(25, 25, 25)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(lblTitulo)
                                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(btnCerrar))
                                .addContainerGap(25, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addComponent(lblTitulo)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnCerrar)
                                .addContainerGap(20, Short.MAX_VALUE))
        );

        pack();
    }

    // ==============================================================
    // 🔹 Implementaciones de BatallaListener
    // ==============================================================

    // Muestra el estado general de ambos personajes
    @Override
    public void onEstado(Personaje heroe, Personaje villano) {
        String estado = String.format("""
                ⚔️ ESTADO DE BATALLA ⚔️

                🦸 Héroe:
                Nombre: %s
                Vida: %d | Fuerza: %d | Defensa: %d

                😈 Villano:
                Nombre: %s
                Vida: %d | Fuerza: %d | Defensa: %d
                """,
                heroe.getNombre(), heroe.getVida(), heroe.getFuerza(), heroe.getDefensa(),
                villano.getNombre(), villano.getVida(), villano.getFuerza(), villano.getDefensa()
        );

        txtResultado.setText(estado);
    }

    // Nuevo método requerido por la interfaz BatallaListener
    @Override
    public void onTurno(int turno, Personaje heroe, Personaje villano) {
        txtResultado.append("\n--- 🕐 Turno " + turno + " ---\n");
        txtResultado.append("Héroe: " + heroe.getNombre() + " (vida: " + heroe.getVida() + ")\n");
        txtResultado.append("Villano: " + villano.getNombre() + " (vida: " + villano.getVida() + ")\n");
    }

    // Muestra el resumen final
    @Override
    public void onFin(String ganador, Personaje heroe, Personaje villano, int ronda,
                      List<String> accionesHeroe, List<String> accionesVillano) {

        StringBuilder sb = new StringBuilder();
        sb.append("🏁 BATALLA FINALIZADA 🏁\n\n");
        sb.append("Ganador: ").append(ganador).append("\n");
        sb.append("Rondas jugadas: ").append(ronda).append("\n\n");

        sb.append("🦸 Acciones del héroe:\n");
        for (String a : accionesHeroe) {
            sb.append(" - ").append(a).append("\n");
        }

        sb.append("\n😈 Acciones del villano:\n");
        for (String a : accionesVillano) {
            sb.append(" - ").append(a).append("\n");
        }

        txtResultado.setText(sb.toString());
    }

    // Muestra una acción individual (ejemplo: "El héroe ataca con su espada")
    @Override
    public void onAccion(String mensaje) {
        txtResultado.append(mensaje + "\n");
    }

    // ==============================================================
    // 🔹 Main para pruebas independientes
    // ==============================================================

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> new BatallaVentanaBasica().setVisible(true));
    }

    // ==============================================================
    // 🔹 Variables del formulario
    // ==============================================================

    private javax.swing.JButton btnCerrar;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JTextArea txtResultado;
}
