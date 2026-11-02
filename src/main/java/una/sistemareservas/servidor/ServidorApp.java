package una.sistemareservas.servidor;

import una.sistemareservas.datos.ConexionDB;
import una.sistemareservas.protocolo.Protocolo;
import una.sistemareservas.service.Service;
import una.sistemareservas.utilidades.ConfiguracionBackend;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;


public class ServidorApp {

    public static void main(String[] args) {
        System.out.println("=== Sistema de Reservas - Backend ===");

        if (!probarBaseDeDatos()) {
            System.exit(1);
            return;
        }

        int puerto = ConfiguracionBackend.getInt("servidor.puerto", Protocolo.PUERTO);
        Servidor servidor = new Servidor(puerto, new Service());
        try {
            servidor.iniciar();
        } catch (IOException e) {
            System.err.println("No se pudo abrir el puerto " + puerto + ": " + e.getMessage());
            System.err.println("¿Ya hay otro Backend corriendo? Ciérrelo o cambie servidor.puerto en backend.properties.");
            System.exit(1);
            return;
        }

        Runtime.getRuntime().addShutdownHook(new Thread(servidor::detener));
        System.out.println("Servidor escuchando en el puerto " + servidor.getPuerto());
        mostrarDirecciones();
        servidor.atenderConexiones();
    }

    private static boolean probarBaseDeDatos() {
        System.out.println("Conectando a la base de datos como '" + ConexionDB.getUsuario() + "'...");
        try (Connection con = ConexionDB.getConnection()) {
            System.out.println("Base de datos OK.");
            return true;
        } catch (SQLException e) {
            System.err.println("ERROR: no se pudo conectar a MySQL.");
            System.err.println("Detalle: " + e.getMessage());
            String mensaje = String.valueOf(e.getMessage()).toLowerCase();
            if (mensaje.contains("access denied")) {
                System.err.println("-> La clave o el usuario no coinciden. Corra database/1_sistemaReservas.sql o revise db.clave en backend.properties.");
            } else if (mensaje.contains("unknown database")) {
                System.err.println("-> La base de datos no existe. Corra los scripts de la carpeta database (1, 2 y 3, en ese orden).");
            } else if (mensaje.contains("communications link failure") || mensaje.contains("connection refused")) {
                System.err.println("-> MySQL no está encendido o no está en el puerto 3306.");
            }
            return false;
        }
    }

    private static void mostrarDirecciones() {
        try {
            for (NetworkInterface interfaz : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                if (!interfaz.isUp() || interfaz.isLoopback()) {
                    continue;
                }
                for (InetAddress direccion : Collections.list(interfaz.getInetAddresses())) {
                    if (direccion.getAddress().length == 4) { // Solo IPv4
                        System.out.println("  IP de esta máquina en la red: " + direccion.getHostAddress());
                    }
                }
            }
        } catch (IOException e) {
            // No es importante
        }
    }
}
