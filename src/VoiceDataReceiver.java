
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author daan
 */
public class VoiceDataReceiver implements Runnable {

    public DatagramSocket datagramSocket;
    byte[] buffer = new byte[256];

    @Override
    public void run() {
        int i = 0;
        try {
            datagramSocket = new DatagramSocket(6789);
            Client c;
            DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);
            System.out.println("Listening client voice");
            while (true) {
                datagramSocket.receive(incoming);
                buffer = incoming.getData();
                for (Iterator<Client> clientIterator = Main.clientList.iterator(); clientIterator.hasNext();) {
                    c = clientIterator.next();
                    
                }

                System.out.println("#" + i++);

            }
        } catch (IOException ex) {
            Logger.getLogger(VoiceDataReceiver.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Stop listening client voice");
    }
}
