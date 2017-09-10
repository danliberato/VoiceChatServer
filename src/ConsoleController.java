/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.util.Iterator;
import java.util.Scanner;

/**
 *
 * @author daan
 */
public class ConsoleController extends Main implements Runnable {

    private final Scanner scanner;
    private String command;

    public ConsoleController() {
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void run() {

        System.out.println("Type '-help' for command list");

        do {

            command = scanner.next().trim();

            //HELP
            if (command.equalsIgnoreCase("-help")) {
                this.showHelp();
            }//KICK
            else if (command.equalsIgnoreCase("-k")) {
                this.kickClient(this.getClientByIP(scanner.next().trim()));
            }//VERSION
            else if (command.equalsIgnoreCase("-v")) {
                System.out.println("Version: " + VERSION);
            }//MESSAGE
            else if (command.equalsIgnoreCase("-m")) {
                this.sendMessageByConsole();
            }//BAN
            else if (command.equalsIgnoreCase("-b")) {
                this.banClient(this.getClientByIP(scanner.next().trim()));
            }//LIST
            else if (command.equalsIgnoreCase("-l")) {
                this.listClients();
            }//TOGGLEMESSAGES
            else if (command.equalsIgnoreCase("-sm")) {
                this.toggleMessages();
            }//EXIT
            else if (command.equalsIgnoreCase("-exit")) {
                System.out.println("Exiting server...");
            } else {
                System.out.println("Type '-help' for command list");
            }

        } while (!command.equalsIgnoreCase("-exit"));

        this.serverDisconnect();
    }

    private void showHelp() {
        //implementar ajuda do servidor
        System.out.println("Use: -command [args]");
        System.out.println("Options:");
        System.out.println("    -b  [host IP]           Ban a host by IP (under  progress...)");
        System.out.println("    -exit                   Close all connections and terminate teh application");
        System.out.println("    -help                   Show this list");
        System.out.println("    -k  [host IP]           Kicks a host by IP");
        System.out.println("    -l                      List all clients");
        System.out.println("    -m  [-p host IP ] \"message\"    Sends a message to all clients connected");
        System.out.println("    -sm                     Toggle show messages");
        System.out.println("    -v                      Shows the application version");
    }

    private void listClients() {

        if (clientList.isEmpty()) {
            System.out.println("No clients connected!");
        } else {
            System.out.println("Client List:");
            for (Iterator<Client> clientIterator = clientList.iterator(); clientIterator.hasNext();) {
                Client c = clientIterator.next();
                System.out.println("    " + c.getHostName() + " (" + c.getHostIp() + ")");
            }
        }

    }

    private synchronized void serverDisconnect() {
        //It uses Iterator because its thread safe (avoid ConcurrentModificationException)
        for (Iterator<Client> clientIterator = clientList.iterator(); clientIterator.hasNext();) {
            Client c = clientIterator.next();
            c.writeBufferedWriter("!shutdown");
        }
        clientList.clear();

        System.out.println("EXIT!");
        System.exit(0);
    }

    private void sendMessageByConsole() {
        String ip = scanner.next();
        
    }

    private synchronized void kickClient(Client c) {
        if (c != null) {
            c.writeBufferedWriter("!kicked");
            //c.disconnectClient();
            System.out.println("Client " + c.getHostName() + " kicked.");
        } else {
            System.out.println("Client not found.");
        }
    }

    private void banClient(Client c) {
        System.out.println("Are you sure you want to ban " + c.getHostName() + " (" + c.getHostIp() + ")? \n(YeS/no)");
        if (scanner.next().trim().equals("YeS")) {
            if (blackList.add(c.getHostMacAddress())) {
                c.writeBufferedWriter("!banned");
                System.out.println("Client " + c.getHostName() + " banned.");
                clientList.remove(c);
            }
        }
    }

    private Client getClientByIP(String ip) {
        Client c = null;
        for (Iterator<Client> clientIterator = clientList.iterator(); clientIterator.hasNext();) {
            c = clientIterator.next();
            if (c.getHostIp().equals(ip)) {
                break;
            }
        }
        return c;
    }

    private void toggleMessages() {
        if(SHOW_MSGS){
            System.out.println("Hiding messages...");
            SHOW_MSGS = false;
        }else{
            System.out.println("Showing messages...");
            SHOW_MSGS = true;
        }
    }

}
