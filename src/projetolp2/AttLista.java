package projetolp2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author estagio.gleydson
 */
class AttLista extends Thread implements Protocolos {

    /*
    private DataInputStream inAttLista;
    private DataOutputStream outAttLista;
    private Socket s;
    public String mensagem;
    */
    
    TelaCliente clienteAttLista;
    
    public AttLista(TelaCliente clienteAttLista) throws IOException{

        this.clienteAttLista = clienteAttLista;
        
        /*
        try {
            
            s = new Socket("127.0.0.1", 4444);
            inAttLista = new DataInputStream(s.getInputStream());
            outAttLista = new DataOutputStream(s.getOutputStream());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        */
    }

    AttLista() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public void run() {

        
        
        while(true){
            
            clienteAttLista.MetodoAttLista();
            try {
                Thread.sleep(3000);
            } catch (InterruptedException ex) {
                Logger.getLogger(AttLista.class.getName()).log(Level.SEVERE, null, ex);
            }
            clienteAttLista.MetodoAttLista();
        }
    }
    
}
