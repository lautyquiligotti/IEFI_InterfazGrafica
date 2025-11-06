/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package batalla.vista;

/**
 *
 * @author Mauri
 */
import batalla.controlador.BatallaListener;
import batalla.modelo.Personaje;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class BatallaVentanaBasica extends JFrame implements BatallaListener {
    private final JTextArea area = new JTextArea(18, 50);

    public BatallaVentanaBasica() {
        super("Batalla (A2 - Básica)");
        area.setEditable(false);
        add(new JScrollPane(area), BorderLayout.CENTER);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
    }

    private void log(String s){ area.append(s + "\n"); }

    @Override public void onEstado(Personaje h, Personaje v){
        log(h.toString()); log(v.toString()); log("-----");
    }
    @Override public void onTurno(int t, Personaje actual, Personaje enemigo){
        log("Turno " + t + " — actúa " + actual.getNombre());
    }
    @Override public void onAccion(String evento){ log("[ACCION] " + evento); }
    @Override public void onFin(String resumen, Personaje h, Personaje v, int turnos,
                                List<String> eventos, List<String> historial){
        log("FIN: " + resumen);
    }
}
