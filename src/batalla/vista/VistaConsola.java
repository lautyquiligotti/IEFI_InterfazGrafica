package batalla.vista;

import batalla.controlador.ValidacionApodos;
import batalla.controlador.ControladorBatalla;
import java.util.Scanner;
import java.util.function.Consumer;

public class VistaConsola {

    private static Consumer<String> onEventoEspecial;
    private static Consumer<String> onReporte;

    public static void setOnEventoEspecial(Consumer<String> c) { onEventoEspecial = c; }
    public static void setOnReporte(Consumer<String> c) { onReporte = c; }

    // main para uso en modo consola (opcional)
    public static void main(String[] args) {
        try (Scanner sc = new Scanner(System.in)) {
            System.out.println("=== SISTEMA DE BATALLA (CONSOLA) ===");
            System.out.print("Ingrese apodo del Héroe: ");
            String apodoHeroe = sc.nextLine();
            while (!ValidacionApodos.esValido(apodoHeroe)) {
                System.out.print("Apodo inválido. Intente nuevamente: ");
                apodoHeroe = sc.nextLine();
            }

            System.out.print("Ingrese apodo del Villano: ");
            String apodoVillano = sc.nextLine();
            while (!ValidacionApodos.esValido(apodoVillano)) {
                System.out.print("Apodo inválido. Intente nuevamente: ");
                apodoVillano = sc.nextLine();
            }

            ControladorBatalla controlador = new ControladorBatalla();
            controlador.iniciarBatalla(apodoHeroe, apodoVillano);
        }
    }

    // Métodos usados por el modelo/controlador para notificar salidas
    public static void mostrarReporte(String reporte) {
        System.out.println(reporte);
        if (onReporte != null) onReporte.accept(reporte);
    }

    public static void mostrarEventoEspecial(String evento) {
        System.out.println("[EVENTO] " + evento);
        if (onEventoEspecial != null) onEventoEspecial.accept(evento);
    }
}
