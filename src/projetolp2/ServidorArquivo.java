
package projetolp2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public class ServidorArquivo implements Runnable, Protocolos{
    
    private String nome = null;
    
    public static Object myLock1;
    public static Object myLock2;
    public static Object myLock3;
    
    public static LinkedList<String> arquivosDisponiveis;
    private static LinkedList<ServidorArquivo> threadsAtivos;
    String path = "C:/Users/GM/Desktop/ArquivosProjetoLP2/ArquivosServidor/";
    public static File arquivo;
    
    Socket ns;
    
    DataInputStream in;
    DataOutputStream out;
    
    ObjectInputStream objIn; //Gerenciar arquivos
    ObjectOutputStream objOut;
    FileInputStream fileIn;
    FileOutputStream fileOut;
    FileOutputStream file; //Gerenciar arquivos
    DataOutputStream salvaArq;
    FileInputStream arq;
    DataInputStream salva;
    
    public ServidorArquivo(Socket ns) throws IOException{
        carregaEmarquivo();
        this.ns = ns;
        //arquivosDisponiveis = new LinkedList<String>();
       
    }
    
    public String getNome(){
        return this.nome;
    }

    public void run() {
        
        try {
            
            in = new DataInputStream(ns.getInputStream());
            out = new DataOutputStream(ns.getOutputStream());
            
            
            
            
            //objIn = new ObjectInputStream(ns.getInputStream());
            //objOut = new ObjectOutputStream(ns.getOutputStream());
             /*
             obj = new ObjectInputStream(ns.getInputStream());
             file = new FileOutputStream("C:/Users/Igor/Desktop/Recebido.mp3");
             byte[] buffer = new byte[4096];
             */
           
             nome =(in.readUTF().toString());
             
             System.out.println("Nome: "+nome);
        
        String operacao;    
        while(true){
            //out.writeUTF(JOptionPane.showInputDialog("ServidorMessage"));
         
             operacao = in.readUTF();  
             System.out.println(operacao);
             if(operacao.equals(PESQUISAR)){
                 pesquisar();
             }else if(operacao.equals(LISTAR)){
                 listar();
             }else if(operacao.equals(FAZERUPLOAD)){
                 recebeArquivo();
             }else if(operacao.equals(FAZERDOWNLOAD)){
                 fazUpload();
             }else if(operacao.equals(SAIR)){
                 logOut();
             }else if(operacao.equals(ENVIARMSNCHAT)){
                 chatRecebe();
             }
                 
                 
                 
            /*int len = obj.read(buffer); //4 bits 
            if( len == -1) //Se ja transferiu tudo
            break;
            file.write(buffer, 0, len); //Remonta o arquivo
            
            */ }
        }catch (Exception e) {}
    }
    
    private void enviaMensagem(String mensagem){
        try {
            out.writeUTF(mensagem);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    private String recebeMensagem()
    {
        String retorno = null;
        try {
            retorno = in.readUTF();
        } catch (IOException ex) {
            Logger.getLogger(TelaCliente.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            return retorno;
        }
        
    }
    
    private void enviaArquivo(String caminho)
    {
        try {
            this.fileIn = new FileInputStream(caminho);
            this.objOut = new ObjectOutputStream(ns.getOutputStream());
            
            byte[] buffer = new byte[4096];
            int len;
            
            while(true){
                len = fileIn.read(buffer);
                if(len == -1) {
                    break;
                }
                
                objOut.write(buffer, 0, len);
                objOut.flush();
            }
                System.out.println("Servidor enviou");
                fileIn.close();
             
            }catch(SocketException e){
                System.err.println("Cliente Cancelou Download!!");
            }catch (FileNotFoundException ex) {
                Logger.getLogger(TelaCliente.class.getName()).log(Level.SEVERE, null, ex);
            }catch (IOException ex) {
                Logger.getLogger(TelaCliente.class.getName()).log(Level.SEVERE, null, ex);
            }
        
    }
    private void recebeArquivo()
    {
        int tamanho = Integer.valueOf(recebeMensagem()); //Recebe tam do arquivo
        String nomeArquivo = recebeMensagem();
        
        try {
            fileOut = new FileOutputStream(path+nomeArquivo);
            objIn = new ObjectInputStream(ns.getInputStream());
            
            synchronized (myLock1){
                if(arquivosDisponiveis.contains(nomeArquivo)){
                    arquivosDisponiveis.remove(nomeArquivo);
                }
            }

            byte[] buffer = new byte[4096];
            int len = 0;
            int total =0;
            
            while(total < tamanho){
                len = objIn.read(buffer);
                total += len;
                fileOut.write(buffer, 0, len); 
            }
            
            System.out.println("Arquivo recebido ");
            fileOut.close(); //Libera o arquivo para ser usado
            
            synchronized (myLock1){ //Para multiplas threads adicionarem arquivos
                if(!arquivosDisponiveis.contains(nomeArquivo)){
                    arquivosDisponiveis.add(nomeArquivo);
                    salvaEmArquivo();
                }
            }
            
            }catch(SocketException e){
                System.err.println("Cliente Cancelou Upload!!");
            }catch (FileNotFoundException ex) {
                Logger.getLogger(TelaCliente.class.getName()).log(Level.SEVERE, null, ex);
            }catch (IOException ex) {
                Logger.getLogger(TelaCliente.class.getName()).log(Level.SEVERE, null, ex);
            }catch (Exception ex) {
                Logger.getLogger(TelaCliente.class.getName()).log(Level.SEVERE, null, ex);
            }
    }
    
    private void salvaEmArquivo()
    {
      File arquivo2 = new File(path+"data.txt");
      arquivo2.delete();
      File arquivo = new File(path+"data.txt");
      
     try(PrintWriter pw = new PrintWriter(arquivo) ){
      
         for(int i = 0; i < arquivosDisponiveis.size(); i++){
           pw.println(arquivosDisponiveis.get(i));
       }
       
     }catch(IOException ex){
       ex.printStackTrace();
     }finally{
         
     }
   }
    
    private synchronized void carregaEmarquivo(){
        
        File arquivo = new File(path+"data.txt");
        try( InputStream in = new FileInputStream(arquivo) ){
          Scanner scan = new Scanner(in);
          String n = null;
          while( scan.hasNext() ){
              n = scan.nextLine();
            if(!arquivosDisponiveis.contains(n))
              arquivosDisponiveis.add(n);
          }
        }catch(IOException ex){
          ex.printStackTrace();
        }
    }
    
    private void fazUpload() throws FileNotFoundException{
        String nome = recebeMensagem();
        System.out.println("Nome arquivo: "+nome);
        
        
        File file = new File(path+nome);
        
        enviaMensagem(String.valueOf(file.length()));
        enviaArquivo((path+nome));
        
    }
    private void listar()
    {
        for(int i=0; i < arquivosDisponiveis.size();i++)
            enviaMensagem(arquivosDisponiveis.get(i));

        enviaMensagem(FIMLISTAGEM);
    }
    
    private void logOut(){
      try{  
        ns.close();
        in.close();
        out.close();
        objIn.close(); 
        objOut.close();
        fileIn.close();
        fileOut.close();
        file.close();
        String id = this.toString();
     
        
      }catch(Exception e) {}
      finally{
          
          synchronized (myLock2){  
            threadsAtivos.remove(this);
            System.out.println("Removendo....");
        }
      }
    }
    
    private void pesquisar(){
       String texto = recebeMensagem();
            
       for(int i = 0; i < arquivosDisponiveis.size(); i++ ){
            if( arquivosDisponiveis.get(i).startsWith(texto) ){
                enviaMensagem(arquivosDisponiveis.get(i));
             }
         }
       enviaMensagem(SEMARQUIVO); //Nao foram achados arquivos
         
       for(int i=0; i < threadsAtivos.size(); i++)
           System.out.println(threadsAtivos.get(i).toString());
    }
    
    public synchronized void chatRecebe(){
        String msn = recebeMensagem();
        
        for(int i=0; i < threadsAtivos.size(); i++){
           if( threadsAtivos.get(i).getNome().equals(THREADCHAT)){
               threadsAtivos.get(i).enviaMensagem(msn);
               System.out.println("Enviou "+ msn+"para: "+ threadsAtivos.get(i).getNome());
           }
            //System.out.print("NOME: "+threadsAtivos.get(i).getNome());
        } 
    }
            
    public static void main(String[] args) throws IOException{
        
        Socket ns;
        ServerSocket s = new ServerSocket(4444);
        
        
        ExecutorService exe = Executors.newCachedThreadPool();
        arquivosDisponiveis = new LinkedList<>();
        threadsAtivos = new LinkedList<>();
        ServidorArquivo novo;
        
        myLock1 = new Object();
        myLock2 = new Object();
        myLock3 = new Object();
        
        while(true){
            ns = s.accept();
            novo = new ServidorArquivo(ns);
            exe.execute(novo);
            threadsAtivos.add(novo);
            System.out.println("NumThreads: " + Thread.activeCount());
        }    
    } 
}