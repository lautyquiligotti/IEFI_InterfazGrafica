package batalla.vista;

import batalla.controlador.ControladorBatalla;
import batalla.modelo.Personaje;
import batalla.vista.VistaConsola;
import batalla.controlador.BatallaListener;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class VentanaPrincipalJuego extends JFrame implements BatallaListener {


    // --- Controlador ---
    private final ControladorBatalla controlador = new ControladorBatalla();

    // --- Estado de UI ---
    private int numeroPartida = 1;
    private int totalPartidas = 5;
    private int turnoActual = 0;

    // Para calcular % barras
    private int vidaMaxHeroe = 200;
    private int vidaMaxVillano = 200;

    // --- Menú ---
    private JMenuItem miPausar, miGuardar, miSalir;
    private JMenuItem miHistorial, miEstadisticas, miRanking;

    // --- Panel superior ---
    private final JLabel lblPartida = new JLabel("Partida 1/5");
    private final JLabel lblTurno   = new JLabel("Turno 0");

    // --- Panel central (dos columnas) ---
    // Héroe
    private final JLabel lblNombreH = new JLabel("-");
    private final JProgressBar pbVidaH = new JProgressBar(0, 200);
    private final JProgressBar pbBendH = new JProgressBar(0, 100);
    private final JLabel lblArmaH = new JLabel("-");
    private final JLabel lblEstadoH = new JLabel("-");

    // Villano
    private final JLabel lblNombreV = new JLabel("-");
    private final JProgressBar pbVidaV = new JProgressBar(0, 200);
    private final JProgressBar pbBendV = new JProgressBar(0, 100);
    private final JLabel lblArmaV = new JLabel("-");
    private final JLabel lblEstadoV = new JLabel("-");

    // --- Log inferior ---
    private final JTextArea txtLog = new JTextArea(8, 80);

    // --- Datos para "Ver" ---
    private final List<String> historial = new ArrayList<>();
    private final List<String> eventos = new ArrayList<>();
    private String ultimoReporte = "";

    public VentanaPrincipalJuego() {
        super("Batalla Épica");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        // Conectar controlador y vista consola
        controlador.setListener(this);
        VistaConsola.setOnEventoEspecial(this::agregarEvento);
        VistaConsola.setOnReporte(rep -> {
            ultimoReporte = rep;
            mostrarDialogoReporte(rep);
        });

        setJMenuBar(crearMenu());
        add(crearContenido());

        // Botón para iniciar nueva partida (diálogo)
        SwingUtilities.invokeLater(this::dialogoIniciar);
    }

    private JMenuBar crearMenu() {
        JMenuBar mb = new JMenuBar();

        JMenu mPartida = new JMenu("Partida");
        miPausar = new JMenuItem("Pausar (opcional)");
        miPausar.setEnabled(false); // el controlador actual corre continuo
        miGuardar = new JMenuItem("Guardar partida...");
        miSalir = new JMenuItem("Salir");

        miGuardar.addActionListener(e -> guardarAArchivo());
        miSalir.addActionListener(e -> dispose());

        mPartida.add(miPausar);
        mPartida.add(miGuardar);
        mPartida.addSeparator();
        mPartida.add(miSalir);

        JMenu mVer = new JMenu("Ver");
        miHistorial = new JMenuItem("Historial de Partidas");
        miEstadisticas = new JMenuItem("Estadísticas generales");
        miRanking = new JMenuItem("Ranking de personajes");

        miHistorial.addActionListener(e -> mostrarHistorial());
        miEstadisticas.addActionListener(e -> mostrarEstadisticas());
        miRanking.addActionListener(e -> mostrarRanking());

        mVer.add(miHistorial);
        mVer.add(miEstadisticas);
        mVer.add(miRanking);

        mb.add(mPartida);
        mb.add(mVer);
        return mb;
    }

    private Component crearContenido() {
        JPanel root = new JPanel(new BorderLayout(10,10));
        root.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        // --- Superior ---
        JPanel top = new JPanel(new GridLayout(1,2,10,10));
        estilizarTitulo(lblPartida);
        estilizarTitulo(lblTurno);
        top.add(wrap(lblPartida));
        top.add(wrap(lblTurno));

        // --- Centro ---
        JPanel center = new JPanel(new GridLayout(1,2,10,10));
        center.add(crearPanelPersonaje("HÉROE", lblNombreH, pbVidaH, pbBendH, lblArmaH, lblEstadoH));
        center.add(crearPanelPersonaje("VILLANO", lblNombreV, pbVidaV, pbBendV, lblArmaV, lblEstadoV));

        // --- Inferior ---
        txtLog.setEditable(false);
        txtLog.setLineWrap(true);
        txtLog.setWrapStyleWord(true);
        ((DefaultCaret)txtLog.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE); // autoscroll
        JScrollPane sp = new JScrollPane(txtLog);
        sp.setBorder(BorderFactory.createTitledBorder("Log de eventos"));

        root.add(top, BorderLayout.NORTH);
        root.add(center, BorderLayout.CENTER);
        root.add(sp, BorderLayout.SOUTH);
        return root;
    }

    private JPanel crearPanelPersonaje(String titulo, JLabel lblNombre,
                                       JProgressBar pbVida, JProgressBar pbBend,
                                       JLabel lblArma, JLabel lblEstado) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createTitledBorder(titulo));

        lblNombre.setFont(lblNombre.getFont().deriveFont(Font.BOLD, 18f));
        p.add(lblNombre);
        p.add(Box.createVerticalStrut(6));

        pbVida.setStringPainted(true);
        pbBend.setStringPainted(true);

        p.add(new JLabel("Vida"));
        p.add(pbVida);
        p.add(Box.createVerticalStrut(4));
        p.add(new JLabel("% Bendición / Maldición"));
        p.add(pbBend);
        p.add(Box.createVerticalStrut(6));

        p.add(new JLabel("Arma equipada:"));
        p.add(lblArma);
        p.add(Box.createVerticalStrut(4));
        p.add(new JLabel("Estado:"));
        p.add(lblEstado);

        return p;
    }

    private JPanel wrap(JComponent c) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p.add(c);
        return p;
    }

    private void estilizarTitulo(JLabel l) {
        l.setFont(l.getFont().deriveFont(Font.BOLD, 18f));
    }

    // ====== Diálogo para iniciar ======
    private void dialogoIniciar() {
        JTextField tfHeroe = new JTextField();
        JTextField tfVillano = new JTextField();
        Object[] msg = {
                "Apodo del héroe:", tfHeroe,
                "Apodo del villano:", tfVillano
        };
        int ok = JOptionPane.showConfirmDialog(this, msg, "Nueva Partida", JOptionPane.OK_CANCEL_OPTION);
        if (ok == JOptionPane.OK_OPTION) {
            limpiarUI();
            lblPartida.setText("Partida " + numeroPartida + "/" + totalPartidas);
            agregarLog("Comienza la batalla entre " + tfHeroe.getText() + " y " + tfVillano.getText());
            // Ejecutamos en otro hilo para no congelar la UI (el controlador recorre todo el combate)
            new Thread(() -> controlador.iniciarBatalla(tfHeroe.getText(), tfVillano.getText()), "battle-thread").start();
        }
    }

    private void limpiarUI() {
        turnoActual = 0;
        lblTurno.setText("Turno 0");
        txtLog.setText("");
        eventos.clear();
        // reset visual
        actualizarPersonajeH("-", 0, 0, "-", "-");
        actualizarPersonajeV("-", 0, 0, "-", "-");
    }

    // ====== Guardar ======
    private void guardarAArchivo() {
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File("partida.txt"));
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (FileWriter fw = new FileWriter(fc.getSelectedFile())) {
                fw.write("=== LOG ===\n");
                fw.write(txtLog.getText());
                fw.write("\n\n=== REPORTE ===\n");
                fw.write(ultimoReporte == null ? "(sin reporte aún)" : ultimoReporte);
                JOptionPane.showMessageDialog(this, "Guardado en: " + fc.getSelectedFile().getAbsolutePath());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error al guardar: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ====== Vistas "Ver" ======
    private void mostrarHistorial() {
        JTextArea ta = new JTextArea(15, 60);
        ta.setEditable(false);
        StringBuilder sb = new StringBuilder();
        int n = 1;
        for (int i = historial.size() - 1; i >= 0; i--) {
            sb.append("BATALLA #").append(n++).append(" - ").append(historial.get(i)).append("\n");
        }
        if (sb.length() == 0) sb.append("(sin partidas)");
        ta.setText(sb.toString());
        JOptionPane.showMessageDialog(this, new JScrollPane(ta), "Historial de Partidas", JOptionPane.PLAIN_MESSAGE);
    }

    private void mostrarEstadisticas() {
        // Estadísticas sencillas calculadas desde "historial"
        int total = historial.size();
        long victoriasHeroe = historial.stream().filter(s -> s.contains("Ganador: ") && s.contains("Heroe:")).count(); // dummy
        String msg = """
                Partidas jugadas: %d
                (Sugerencia: podemos guardar ganadores reales y mostrar top de turnos, daño, etc.)
                """.formatted(total);
        JOptionPane.showMessageDialog(this, msg, "Estadísticas generales", JOptionPane.INFORMATION_MESSAGE);
    }

    private void mostrarRanking() {
        // Placeholder simple
        JTextArea ta = new JTextArea("""
                Ranking (placeholder)
                - Top ganadores por cantidad de victorias
                - Podemos poblarlo parsing de "historial" o guardando contadores en el controlador.
                """);
        ta.setEditable(false);
        JOptionPane.showMessageDialog(this, new JScrollPane(ta), "Ranking de personajes", JOptionPane.PLAIN_MESSAGE);
    }

    // ====== Métodos utilitarios de UI ======
    private void agregarLog(String linea) {
        SwingUtilities.invokeLater(() -> txtLog.append("• " + linea + "\n"));
    }

    private void agregarEvento(String evento) {
        eventos.add(evento);
        agregarLog(evento);
    }

    private void actualizarPersonajeH(String nombre, int vida, int bend, String arma, String estado) {
        lblNombreH.setText(nombre);
        pbVidaH.setMaximum(vidaMaxHeroe);
        pbVidaH.setValue(Math.max(0, vida));
        pbVidaH.setString(vida + " / " + vidaMaxHeroe);
        pbBendH.setValue(bend);
        pbBendH.setString(bend + " %");
        lblArmaH.setText(arma);
        lblEstadoH.setText(estado);

        // resaltar crítico
        pbVidaH.setForeground(vida <= vidaMaxHeroe * 0.2 ? Color.RED : UIManager.getColor("ProgressBar.foreground"));
    }

    private void actualizarPersonajeV(String nombre, int vida, int bend, String arma, String estado) {
        lblNombreV.setText(nombre);
        pbVidaV.setMaximum(vidaMaxVillano);
        pbVidaV.setValue(Math.max(0, vida));
        pbVidaV.setString(vida + " / " + vidaMaxVillano);
        pbBendV.setValue(bend);
        pbBendV.setString(bend + " %");
        lblArmaV.setText(arma);
        lblEstadoV.setText(estado);

        pbVidaV.setForeground(vida <= vidaMaxVillano * 0.2 ? Color.RED : UIManager.getColor("ProgressBar.foreground"));
    }

    private static String armaDe(Personaje p) {
        return (p.getArmaActual() != null) ? p.getArmaActual().getNombre() : "-";
    }

    // ====== Implementación de Listener del controlador ======
    @Override
    public void onTurno(int turno, Personaje actual, Personaje enemigo) {
        this.turnoActual = turno;
        SwingUtilities.invokeLater(() -> lblTurno.setText("Turno " + turnoActual));
        agregarLog("Turno " + turno + " - " + actual.getNombre());
    }

    @Override
    public void onAccion(String texto) {
        agregarLog(texto);
    }

    @Override
    public void onEstado(Personaje heroe, Personaje villano) {
        // En el primer estado tomamos las vidas como máximos visuales
        if (turnoActual == 0) {
            vidaMaxHeroe = Math.max(heroe.getVida(), 1);
            vidaMaxVillano = Math.max(villano.getVida(), 1);
            pbVidaH.setMaximum(vidaMaxHeroe);
            pbVidaV.setMaximum(vidaMaxVillano);
        }
        actualizarPersonajeH(heroe.getNombre(), heroe.getVida(), /*bend*/ porcentaje(heroe),
                armaDe(heroe), "-");
        actualizarPersonajeV(villano.getNombre(), villano.getVida(), porcentaje(villano),
                armaDe(villano), "-");
    }

    private int porcentaje(Personaje p) {
        // Truco: usamos toString() que ya imprime ", %bend/mald=NN"
        try {
            String s = p.toString();
            int i = s.indexOf("%bend/mald=");
            if (i >= 0) {
                int j = s.indexOf(',', i+1);
                String num = (j > i) ? s.substring(i + "%bend/mald=".length(), j).trim() : s.substring(i + "%bend/mald=".length()).trim();
                return Integer.parseInt(num);
            }
        } catch (Exception ignored) {}
        return 0;
    }

    @Override
    public void onFin(String resumen, Personaje heroe, Personaje villano, int turnos,
                      List<String> eventosEspeciales, List<String> historial) {
        this.historial.add(resumen);
        agregarLog("¡Fin de la batalla! " + resumen);
        // Avanzar contador de partidas
        numeroPartida = Math.min(numeroPartida + 1, totalPartidas);
        SwingUtilities.invokeLater(() -> lblPartida.setText("Partida " + numeroPartida + "/" + totalPartidas));
        // (El reporte completo llega por VistaConsola.mostrarReporte y abre diálogo)
    }

    private void mostrarDialogoReporte(String reporte) {
        JTextArea ta = new JTextArea(reporte, 20, 70);
        ta.setEditable(false);
        JScrollPane sp = new JScrollPane(ta);
        JOptionPane.showMessageDialog(this, sp, "Reporte final", JOptionPane.PLAIN_MESSAGE);
    }

    // ==== main de la app gráfica ====
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VentanaPrincipalJuego().setVisible(true));
    }
}
