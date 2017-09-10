

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

/**
 *
 * @author daan
 */
public class Main {

    protected static ServerSocket server;
    protected static MulticastSocket multicastSocket;
    public static ArrayList<Client> clientList;
    protected static Vector<String> blackList;
    private static Integer MAX_USERS_ALLOWED = 4;
    private static String WELCOME_PHRASE;
    private static String CUSTOM_NAME;
    public final String VERSION = "v0.1";
    public static boolean SHOW_MSGS = true;
    public static boolean SHOW_VOICE_MSGS = true;

    /**
     * *
     * @param args
     */
    public static void main(String[] args) {

        /*MAX_USERS_CONNECTED = args[0];
        WELCOME_PHRASE = args[1];*/
        clientList = new ArrayList<>();
        blackList = new Vector<>();

        System.out.println(" -----------------------------------------------");
        System.out.println("| Software developed by: Daniel 'Daan' Liberato |");
        System.out.println("| Bugs/Doubts contact: dlj.daniel@gmail.com     |");
        System.out.println("| This is a simple multithread server.          |");
        System.out.println(" -----------------------------------------------");
        System.out.println("             Initializing server...             ");

        try {
            //Initialize server on port 6789
            //server = new ServerSocket(6789);
            server = new ServerSocket(6789, 100, InetAddress.getLocalHost());

            System.out.println(" ---------------- Server info ------------------");
            System.out.println("Address: " + InetAddress.getLocalHost().getHostAddress());
            System.out.println("Canonical Name: " + InetAddress.getLocalHost().getCanonicalHostName());
            System.out.println("Name: " + InetAddress.getLocalHost().getHostName());

            ConsoleController console = new ConsoleController();
            Thread threadConsole = new Thread(console);
            threadConsole.start();
            System.out.println("Console initiated");
            
            Thread threadVoice = new Thread(new VoiceDataReceiver(), "Thread voice");
            threadVoice.start();

            System.out.println("\nWaiting for clients...");

            while (true) {
                Client client = new Client();
                Thread threadClient = new Thread(client);
                threadClient.start();
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    public static synchronized void sendClientList() {
        Client c = null;
        for (Iterator<Client> clientIterator = clientList.iterator(); clientIterator.hasNext();) {
            c = clientIterator.next();
            c.writeBufferedWriter("!CL" + clientList.toString());
        }

    }

}
