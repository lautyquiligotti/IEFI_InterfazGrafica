package batalla.vista;

import batalla.controlador.ControladorBatalla;
import batalla.controlador.ValidacionApodos;

import java.util.Scanner;
import java.util.function.Consumer;

public class VistaConsola {

    // ======== NUEVO: callbacks opcionales para la UI ========
    private static Consumer<String> onEventoEspecial; // lo usa la ventana para mostrar en el log
    private static Consumer<String> onReporte;        // lo usa la ventana para abrir el reporte

    public static void setOnEventoEspecial(Consumer<String> c) { onEventoEspecial = c; }
    public static void setOnReporte(Consumer<String> c) { onReporte = c; }

    // ======== MODO CONSOLA (igual que antes) ========
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("=== SISTEMA DE BATALLA ===");
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

        sc.close();
    }

    // ======== Salidas (consola + UI si está conectada) ========
    public static void mostrarReporte(String reporte) {
        System.out.println(reporte);              // sigue mostrando por consola
        if (onReporte != null) {                  // además notifica a la ventana si está registrada
            onReporte.accept(reporte);
        }
    }

    public static void mostrarEventoEspecial(String evento) {
        System.out.println("[EVENTO ESPECIAL] " + evento); // consola
        if (onEventoEspecial != null) {                    // y también a la ventana
            onEventoEspecial.accept(evento);
        }
    }
}
