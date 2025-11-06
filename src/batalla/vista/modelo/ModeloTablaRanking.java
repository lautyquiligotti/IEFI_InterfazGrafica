/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package batalla.vista.modelo;

import batalla.modelo.ResumenJugador;
import javax.swing.table.AbstractTableModel;
import java.util.*;

public class ModeloTablaRanking extends AbstractTableModel {
    private final String[] columnas = {"Nombre","Apodo","Tipo","Vida Final","Victorias","Supremos Usados"};
    private final List<ResumenJugador> datos;

    public ModeloTablaRanking(List<ResumenJugador> datos) {
        List<ResumenJugador> copia = new ArrayList<>(datos);
        // Orden por victorias descendente
        copia.sort((a,b) -> Integer.compare(b.getVictorias(), a.getVictorias()));
        this.datos = copia;
    }

    @Override public int getRowCount() { return datos.size(); }
    @Override public int getColumnCount() { return columnas.length; }
    @Override public String getColumnName(int c) { return columnas[c]; }

    @Override public Object getValueAt(int fila, int col) {
        ResumenJugador r = datos.get(fila);
        return switch (col) {
            case 0 -> r.getNombre();
            case 1 -> r.getApodo();
            case 2 -> r.getTipo();
            case 3 -> r.getVidaFinal();
            case 4 -> r.getVictorias();
            case 5 -> r.getSupremosUsados();
            default -> "";
        };
    }
}
