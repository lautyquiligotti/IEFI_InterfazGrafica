/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package batalla.vista;


import batalla.modelo.ResumenJuego;
import batalla.modelo.RegistroBatalla;
import batalla.modelo.servicio.ServicioEstadisticas;
import batalla.vista.modelo.ModeloTablaRanking;

import javax.swing.DefaultListModel;
import java.util.List;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;


/**
 *
 * @author rebor
 */
public class VentanaReporteFinal extends javax.swing.JFrame {

    
       public VentanaReporteFinal(ResumenJuego resumen,
                               List<String> eventosEspeciales,
                               ServicioEstadisticas servicio) {
        initComponents();                 // generado por NetBeans: NO borrar
        setTitle("Reporte Final - Resumen del Juego");
        setLocationRelativeTo(null);      // centrar

        // --- TAB Ranking ---
        tablaRanking.setModel(new ModeloTablaRanking(resumen.getJugadores()));
        // Orden por encabezados (reemplaza setAutoCreateRowSorter(true))
        TableRowSorter<TableModel> sorter =
                new TableRowSorter<>(tablaRanking.getModel());
        tablaRanking.setRowSorter(sorter);

        // --- TAB Historial (últimas 5) ---
        DefaultListModel<String> modelo = new DefaultListModel<>();
        List<RegistroBatalla> batallas = resumen.getBatallas();
        int desde = Math.max(0, batallas.size() - 5);
        for (int i = batallas.size() - 1; i >= desde; i--) {
            RegistroBatalla b = batallas.get(i);
            String linea = String.format(
                "BATALLA #%d - Heroe: %s | Villano: %s | Ganador: %s | Turnos: %d",
                b.getNumero(), b.getHeroe(), b.getVillano(), b.getGanador(), b.getTurnos()
            );
            modelo.addElement(linea);
        }
        listaHistorial.setModel(modelo);

        // --- TAB Estadísticas ---
        completarEstadisticas(resumen, eventosEspeciales, servicio);
    }

    // ================== MÉTODO AUXILIAR ==================
    private void completarEstadisticas(ResumenJuego resumen,
                                       List<String> eventosEspeciales,
                                       ServicioEstadisticas servicio) {
        var max = servicio.mayorDanioEnEventos(eventosEspeciales);
        var larga = servicio.batallaMasLarga(resumen.getBatallas());
        var armas = servicio.totalArmasInvocadas(resumen.getJugadores());
        var supremos = servicio.totalSupremos(resumen.getJugadores());
        var porcentajes = servicio.porcentajeVictoriasPorTipo(resumen.getJugadores());

        StringBuilder sb = new StringBuilder();
        sb.append("Mayor dano en un solo ataque: ").append(max.monto)
          .append(" (").append(max.jugador).append(")\n");

        sb.append("Batalla mas larga: ").append(larga.turnos)
          .append(" turnos - Ganador: ").append(larga.ganador).append("\n\n");

        sb.append("Total de armas invocadas (por personaje):\n");
        for (var e : armas.entrySet()) {
            sb.append(" - ").append(e.getKey()).append(": ").append(e.getValue()).append("\n");
        }

        sb.append("\nAtaques supremos ejecutados (por personaje):\n");
        for (var e : supremos.entrySet()) {
            sb.append(" - ").append(e.getKey()).append(": ").append(e.getValue()).append("\n");
        }

        if (!porcentajes.isEmpty()) {
            sb.append("\nPorcentaje de victorias por tipo:\n");
            for (var e : porcentajes.entrySet()) {
                sb.append(" - ").append(e.getKey()).append(": ")
                  .append(String.format("%.1f%%", e.getValue())).append("\n");
            }
        }

        txtEstadisticas.setText(sb.toString());
        txtEstadisticas.setCaretPosition(0);
    }



    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tablaRanking = new javax.swing.JTable();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtEstadisticas = new javax.swing.JTextArea();
        jPanel7 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        listaHistorial = new javax.swing.JList<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        tablaRanking.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane2.setViewportView(tablaRanking);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 647, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 374, Short.MAX_VALUE)
        );

        jTabbedPane2.addTab("Ranking", jPanel5);

        txtEstadisticas.setEditable(false);
        txtEstadisticas.setColumns(20);
        txtEstadisticas.setLineWrap(true);
        txtEstadisticas.setRows(5);
        txtEstadisticas.setWrapStyleWord(true);
        jScrollPane1.setViewportView(txtEstadisticas);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 647, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 374, Short.MAX_VALUE)
        );

        jTabbedPane2.addTab("Estadisticas", jPanel6);

        listaHistorial.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane3.setViewportView(listaHistorial);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 647, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 374, Short.MAX_VALUE)
        );

        jTabbedPane2.addTab("Historial", jPanel7);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane2)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane2)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents



    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JList<String> listaHistorial;
    private javax.swing.JTable tablaRanking;
    private javax.swing.JTextArea txtEstadisticas;
    // End of variables declaration//GEN-END:variables
}
