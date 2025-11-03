/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package batalla.vista;

import batalla.controlador.ControladorBatalla;
import batalla.controlador.*;

import java.util.Scanner;

/**
 *
 * @author Usuario
 */
public class VistaConsola {

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

    public static void mostrarReporte(String reporte) {
        System.out.println(reporte);
    }

    public static void mostrarEventoEspecial(String evento) {
        System.out.println("[EVENTO ESPECIAL] " + evento);
    }
}
