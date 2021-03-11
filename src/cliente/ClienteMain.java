package cliente;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

//	public static void main(String[] args) {
//        Socket socket;
//        DataOutputStream mensaje;
//        final String HOST = "localhost";
//        final int PUERTO = 2017;
//        try {
//            //Creamos nuestro socket
//            socket = new Socket(HOST, PUERTO);
//     
//            mensaje = new DataOutputStream(socket.getOutputStream());
//            Scanner sc = new Scanner(System.in);
//            String comando;
//            
//            do {
//            	comando = sc.nextLine();
//            	socket = new Socket(HOST, PUERTO);
//            	mensaje.writeUTF(comando);
//            }
//            while (!comando.equals("exit"));         
//            
//            socket.close();
// 
//        } catch (UnknownHostException e) {
//            System.out.println("El host no existe o no está activo.");
//        } catch (IOException e) {
//            System.out.println("Error de entrada/salida.");
//        }
//	}
import java.net.Socket;
import java.util.Scanner;
	public class ClienteMain {
	    private Socket socket;
	    private DataInputStream bufferDeEntrada = null;
	    private DataOutputStream bufferDeSalida = null;
	    Scanner teclado = new Scanner(System.in);
	    final String COMANDO_TERMINACION = "exit";

	    public void levantarConexion(String ip, int puerto) {
	        try {
	            socket = new Socket(ip, puerto);
	            mostrarTexto("Conexión establecida correctamente con el servidor.");
	            System.out.print("\n[Cliente] => ");
	        } catch (Exception e) {
	            mostrarTexto("Excepción al levantar conexión: " + e.getMessage());
	            System.exit(0);
	        }
	    }

	    public static void mostrarTexto(String s) {
	        System.out.print(s);
	    }

	    public void abrirFlujos() {
	        try {
	            bufferDeEntrada = new DataInputStream(socket.getInputStream());
	            bufferDeSalida = new DataOutputStream(socket.getOutputStream());
	            bufferDeSalida.flush();
	        } catch (IOException e) {
	            mostrarTexto("Error en la apertura de flujos");
	        }
	    }

	    public void enviar(String s) {
	        try {
	            bufferDeSalida.writeUTF(s);
	            bufferDeSalida.flush();
	        } catch (IOException e) {
	            mostrarTexto("IOException on enviar");
	        }
	    }

	    public void cerrarConexion() {
	        try {
	            bufferDeEntrada.close();
	            bufferDeSalida.close();
	            socket.close();
	            mostrarTexto("Conexión terminada\n");
	        } catch (IOException e) {
	            mostrarTexto("IOException on cerrarConexion()");
	        }finally{
	            System.exit(0);
	        }
	    }

	    public void ejecutarConexion(String ip, int puerto) {
	        Thread hilo = new Thread(new Runnable() {
	            @Override
	            public void run() {
	                try {
	                    levantarConexion(ip, puerto);
	                    abrirFlujos();
	                    recibirDatos();
	                } finally {
	                    cerrarConexion();
	                }
	            }
	        });
	        hilo.start();
	    }

	    public void recibirDatos() {
	        String st = "";
	        try {
	            do {
	                st = (String) bufferDeEntrada.readUTF();
	                mostrarTexto("[Servidor] => " + st);
	                System.out.print("[Cliente] => ");
	            } while (!st.equals(COMANDO_TERMINACION));
	        } catch (IOException e) {}
	    }

	    public void escribirDatos() {
	        String entrada = "";
	        while (true) {
	            entrada = teclado.nextLine();
	            if (entrada.equals("exit")) {
	            	mostrarTexto("Cerramos sesion\n");
	            	cerrarConexion();
	            }
	            if(entrada.length() > 0)
	                enviar(entrada);
	        }
	    }

	    public static void main(String[] argumentos) {
	        ClienteMain cliente = new ClienteMain();
	        Scanner escaner = new Scanner(System.in);
	        String ip = "localhost";

	        String puerto = "5050";
	        cliente.ejecutarConexion(ip, Integer.parseInt(puerto));
	        cliente.escribirDatos();
	    }
	}

