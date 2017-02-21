
package projetolp2;

import projetolp2.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public class GerenciaChat implements Protocolos, Runnable {
        
    private DataInputStream inTalk;
    private DataOutputStream outTalk;
    private Socket s;
    public String mensagem;
    private Object obj; //Manter a sincronia dos metodos
    
    public GerenciaChat(){
        
        try {
            s = new Socket("127.0.0.1", 4444);
            obj = new Object();
            inTalk = new DataInputStream(s.getInputStream());
            outTalk = new DataOutputStream(s.getOutputStream());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public void run()
    {
        try {
            outTalk.writeUTF(THREADCHAT); //Avisa que é um Thread do chat
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        while(true){
            mensagem = recebeMensagemChat();
        }
    }
    public String recebeMensagemChat()
    {
        synchronized(obj){
            String retorno = null;
            try {
                retorno = inTalk.readUTF();
                System.out.println(retorno);
            } catch (IOException ex) {
                
            }finally{
                return retorno;
            }
        }
    }
    public void enviaMensagemChat(String mensagem){
        synchronized(obj){
            try {
                outTalk.writeUTF(ENVIARMSNCHAT);
                outTalk.writeUTF(mensagem);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    public String getMensagem(){
        return mensagem;
    }
    
    public void Sair(){
        try {
            System.out.println("Fechando GerenciaChat");
            outTalk.writeUTF(SAIR);
            System.exit(0);
        } catch (IOException ex) {
            Logger.getLogger(GerenciaChat.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}