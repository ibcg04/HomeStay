package ec.edu.espol;
import ec.edu.espol.ui.HomeStayGUI;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        if (args.length == 0 || !"--console".equalsIgnoreCase(args[0])) {
            HomeStayGUI.mostrar();
            return;
        }
        gestionarAplicacion();
    }

    public static void gestionarAplicacion() {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("==========Bienvenido a HomeStay!==========");
            System.out.println("1. Iniciar sesión como Huesped");
            System.out.println("2. Iniciar sesión como Anfitrión");
            System.out.println("3. Registrar nuevo usuario");
            System.out.println("4. Salir");

            int opcion = sc.nextInt();
            sc.nextLine();

            switch (opcion) {
                case 1:
                    System.out.println("Iniciando sesión como Huesped...");
                    InicioSesion.iniciarSesionHuesped(sc);            
                    break;
                case 2:
                    System.out.println("Iniciando sesión como Anfitrión...");
                    InicioSesion.iniciarSesionAnfitrion(sc);
                    break;
                case 3:
                    System.out.println("Registrando nuevo usuario...");
                    RegistrarUsuario.registro(sc);
                    break;
                case 4:
                    System.out.println("Saliendo del programa...");
                    sc.close();
                    return;
                default:
                    System.out.println("Opción no válida. Por favor, intente de nuevo.");
                    break;
            }
        }
    }
}
