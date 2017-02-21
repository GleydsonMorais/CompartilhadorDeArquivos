package projetolp2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

public class FazDownload extends Thread implements Protocolos {
 
    private DataInputStream in;
    private DataOutputStream out;
    
    private ObjectInputStream objIn;
    private FileInputStream fileIn;
    private Socket s;
    public String mensagem;
    private String caminho;
    private String escolhido;
    private FileOutputStream fileOut;
    private int tamanho;
    private TelaTransferencia tela;
    private JTextArea j;
    
    public FazDownload(Socket s, String caminho, String escolhido, JTextArea j){
        this.s = s;
        this.caminho = caminho;
        this.escolhido = escolhido;
        tela = new TelaTransferencia(this);
        tela.setVisible(true);
        this.j = j;
        try {
            in = new DataInputStream(s.getInputStream());    //Para conversar
            out = new DataOutputStream(s.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(FazDownload.class.getName()).log(Level.SEVERE, null, ex);
        }
       
        enviaMensagem(FAZDOWNLOAD);
    }
    
    @Override
    public void run(){
        

        float porcentagem;
        
        enviaMensagem(FAZERDOWNLOAD);
        enviaMensagem(escolhido);
        System.out.println("ENTROU NO RUN");
        tamanho = Integer.valueOf(recebeMensagem()); //Recebe tam do arquivo

        
        //fazDownload.start();
        try {
            fileOut = new FileOutputStream(caminho);
            objIn = new ObjectInputStream(s.getInputStream());
            
            byte[] buffer = new byte[4096];
            int len;
            float total =0;
            
            while(total < tamanho){
                
                porcentagem = ( (total / tamanho) * 100);
                len = objIn.read(buffer);
                total += len;
                System.out.println("Recebido: "+porcentagem);
                tela.setPorcentagem((int)porcentagem);
                
                if(tela.getPaused() == true){
                       tela.setPaused(false);
                       //JOptionPane.showMessageDialog(tela, "Continuar ?");
                }
                fileOut.write(buffer, 0, len);
                //System.out.println( "Recebido: "+porcentagem+"%" );
            
            }
            tela.setVisible(false);
            System.out.println("Arquivo recebido ");
            fileOut.close(); //Libera o arquivo para ser usado
            
                Date d = new Date();
                j.insert(d.getHours()+":"+d.getMinutes()+" ----- Downloaded: "+escolhido+"\n", JFrame.WIDTH);
       
            }catch (FileNotFoundException ex) {
                Logger.getLogger(TelaCliente.class.getName()).log(Level.SEVERE, null, ex);
            }catch (SocketException ex) {
                JOptionPane.showMessageDialog(null, "Desculpe, serviço temporariamente offline", "Aviso", JOptionPane.INFORMATION_MESSAGE);
                System.exit(1);
            }catch (Exception ex) {
                ex.printStackTrace();
            }
        
    }
    
     private void enviaMensagem(String mensagem){
        try {
            out.writeUTF(mensagem);
        }catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Desculpe, serviço temporariamente offline", "Aviso", JOptionPane.INFORMATION_MESSAGE);
                System.exit(1);
            }
    }
    private String recebeMensagem()
    {
        String retorno = null;
        try {
            retorno = in.readUTF();
        }catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Desculpe, serviço temporariamente offline", "Aviso", JOptionPane.INFORMATION_MESSAGE);
                System.exit(1);
            
        }finally{
            return retorno;
        }
    } 
}
