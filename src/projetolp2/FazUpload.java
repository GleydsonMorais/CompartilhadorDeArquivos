
package projetolp2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Date;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

public class FazUpload extends Thread implements Protocolos{

    private ObjectOutputStream objOut;
    private FileInputStream fileIn;
    private DataInputStream in;
    private DataOutputStream out;
    private Socket s;
    public String mensagem;
    private String caminho;
    private String nomeArquivo;
    private long tamanho;
    private TelaTransferencia tela;
    private JTextArea j;
    public FazUpload(Socket s, String caminho, long tamanho, String nomeArquivo, JTextArea j) throws IOException{
        this.s = s;
        this.caminho = caminho;
        this.tamanho = tamanho;
        this.nomeArquivo = nomeArquivo;
        in = new DataInputStream(s.getInputStream());    //Para conversar
        out = new DataOutputStream(s.getOutputStream());
        tela = new TelaTransferencia(this);
        tela.setVisible(true);
        System.out.println("Tamanho: "+tamanho);
        enviaMensagem(FAZERUPLOAD);
        this.j = j;
    }
    
    public void run(){
        
        

        float jaEnviado = 0;
        int porcentagem = 0;
        System.out.println("INICIOU O UPLOAD!!!!");
        
            enviaMensagem(FAZERUPLOAD); //requisita upload para ao servidor 
            enviaMensagem(String.valueOf(tamanho)); //Envia tam do arquivo pro servidor
            enviaMensagem(nomeArquivo);
            //enviaArquivo(caminhoArquivo);
        
            //System.out.println(caminhoArquivo);
            
            try {
                fileIn = new FileInputStream(caminho);
                objOut = new ObjectOutputStream(s.getOutputStream());

                byte[] buffer = new byte[4096];
                int len = 0;
                
                while(true){
                    len = fileIn.read(buffer);
                    porcentagem = (int) (( (jaEnviado += 4096) / tamanho) * 100);
                    
                    tela.setPorcentagem( porcentagem );
                    System.out.println( "Enviado: "+ porcentagem+"%" );
                    
                    if(tela.getPaused() == true){
                       tela.setPaused(false);
                       //JOptionPane.showMessageDialog(null, "Continuar ?");
                      // this.suspend();
                    }
                       

                    if(len == -1) {
                        break;
                    }
                    objOut.write(buffer, 0, len);
                  //  System.out.println( "Enviado: "+ porcentagem+"%" );
                    objOut.flush();

                }
                
                Date d = new Date();
                j.insert(d.getHours()+":"+d.getMinutes()+" ----- Uploaded: "+nomeArquivo+"\n", JFrame.WIDTH);
                tela.setVisible(false);
                
                System.out.println("Cliente: Enviado");
                fileIn.close();
                }catch (FileNotFoundException ex) {
                    JOptionPane.showMessageDialog(null, "Arquivo não encontrado!", "Erro", JOptionPane.ERROR_MESSAGE);
                }catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Desculpe, serviço temporariamente offline", "Aviso", JOptionPane.INFORMATION_MESSAGE);
                    System.exit(1);
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