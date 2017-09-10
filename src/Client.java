/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is responsible for managing a client. It contais Host name, Ip,
 * MAC Adress (soon) and all information about the socket
 *
 * @author daan
 */
public class Client implements Runnable {

    private String hostName;
    private String room;
    private String hostMacAddress;
    private String hostIp;
    private Socket socket;
    private InputStream inputStream;
    private InputStreamReader inputStreamReader;
    private BufferedReader bufferedReader;
    private OutputStream outputStream;
    private Writer writer;
    private BufferedWriter bufferedWriter;
    private SimpleDateFormat sdf;

    /**
     * @Constructor
     */
    public Client() {
        try {
            this.socket = Main.server.accept();
            /*--- IO Stream ---*/
            this.inputStream = socket.getInputStream();
            this.inputStreamReader = new InputStreamReader(inputStream);
            this.bufferedReader = new BufferedReader(inputStreamReader);
            this.outputStream = socket.getOutputStream();
            this.writer = new OutputStreamWriter(outputStream);
            this.bufferedWriter = new BufferedWriter(writer);
            /*--- End of IO stream ---*/
            this.sdf = new SimpleDateFormat("HH:mm:ss");
        } catch (IOException e) {
            e.getStackTrace();
        }
    }

    @Override
    public void run() {

        String[] parts;
        String operation;
        String msg;
        String dataFormatada = "";
        hostIp = socket.getInetAddress().getHostAddress();

        try {
            //Try to establish connection with server
            do {
                Date hora = Calendar.getInstance().getTime();// format the date
                dataFormatada = sdf.format(hora);

                //split the message into parts
                //[0]: type of operation [!connect][!message][!standby][!disconnect];
                //[1]: user name (If is a new client) OR message (if its already connected)
                operation = "!standby";
                msg = "";
                parts = bufferedReader.readLine().split("\\|");
                operation = parts[0];
                if (operation.equals("!connect")) {
                    this.setHostName(parts[1]);
                    this.setHostMacAddress(parts[2]);
                    if (!Main.blackList.contains(this.getHostMacAddress())) {
                        Main.clientList.add(this);
                        msg = "Connected!";
                        Main.sendClientList();
                    } else {
                        this.writeBufferedWriter("!banned");
                        System.out.println(this.getHostName() + " - Banned!");
                        break;
                    }
                } else if (operation.equals("!message")) {
                    msg = parts[1];
                } else if (operation.equals("!disconnect")) {
                    break;
                }
                if (Main.SHOW_MSGS) {
                    System.out.println("[" + dataFormatada + "] " + hostName + " (" + hostIp + "): " + msg);//print message
                }
                sendToAll(msg);

            } while (socket.isConnected());

        } catch (NullPointerException e) {
            System.out.println("normal exception");
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, e);
        } catch (IOException ex) {
            System.out.println("Client app crashed!");
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }

        msg = "Disconnected";
        System.out.println("[" + dataFormatada + "] " + hostName + " (" + hostIp + "): " + msg);//print message
        this.disconnectClient();
    }

    /*
    * Closes all client connections, remove from the list and sends a message to all
     */
    public void disconnectClient() {

        try {
            this.sendToAll(" Disconnected");
            Main.clientList.remove(this);
            Main.sendClientList();
            inputStream.close();
            inputStreamReader.close();
            bufferedReader.close();
            this.getSocket().close();
            super.finalize();
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Throwable ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * *
     * Multicast method
     *
     * @param msg String
     */
    protected synchronized void sendToAll(String msg) {

        for (Iterator<Client> clientIterator = Main.clientList.iterator(); clientIterator.hasNext();) {
            Client c = clientIterator.next();
            if (!c.equals(this)) {
                c.writeBufferedWriter(this.getHostName() + " says -> " + msg);
            }
        }
    }

    public String getHostIp() {
        return this.hostIp;
    }

    public String getHostName() {
        return this.hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getHostMacAddress() {
        return this.hostMacAddress;
    }

    public void setHostMacAddress(String hostMacAddress) {
        this.hostMacAddress = hostMacAddress;
    }

    public void setHostIp(String hostIp) {
        this.hostIp = hostIp;
    }

    public void writeBufferedWriter(String msg) {
        try {
            this.bufferedWriter.write(msg + "\r\n");
            this.bufferedWriter.flush();
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Socket getSocket() {
        return this.socket;
    }

    @Override
    public String toString() {
        return this.getHostName();
    }
}
