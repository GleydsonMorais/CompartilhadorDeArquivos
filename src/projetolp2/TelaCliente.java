
package projetolp2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Date;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.Timer;

public class TelaCliente extends javax.swing.JFrame implements Protocolos {
    
    private DataInputStream in;
    private DataOutputStream out;

    private Socket s;
    private String pesquisa; //Var para armazenar a palavra de pesquisa
    private ObjectOutputStream objOut;
    private ObjectInput objIn;
    private FileInputStream fileIn;
    private FileOutputStream fileOut;
    private String path = "C:/Users/GM/Desktop/ArquivosProjetoLP2/ArquivosCliente/";
    private GerenciaChat chat;
    private FazUpload fazUpload;
    private FazDownload fazDownload;
    private String nome;
    private Thread t;
    private TimerTask time;;
    private long tamanho; //Armazena tam do arquivo
    
    
    private AttLista objAttLista = new AttLista(this);
    private Thread threadAttLista;
    
    
    
    public TelaCliente() throws IOException {
        
            
      
        try{
           //objAttLista = new AttLista();
           //threadAttLista = new Thread(objAttLista);

           s = new Socket("127.0.0.1", 4444);


           in = new DataInputStream(s.getInputStream());    //Para conversar
           out = new DataOutputStream(s.getOutputStream()); //Para conversar

           nome = "Cliente";
           out.writeUTF(nome); //manda nome pro servidor


           }catch(ConnectException e){
               JOptionPane.showMessageDialog(null, "Servidor está offline, tente novamente mais tarde", "Aviso", JOptionPane.INFORMATION_MESSAGE);
               System.exit(1);
           }
       
        
        try{
        initComponents();
        FEscolheArquivo.setEnabled(false);
        FEscolheArquivo.setVisible(false);
        DefaultListModel model2 = new DefaultListModel();
        this.s = s;
        
        }catch(Exception e){
            JOptionPane.showMessageDialog(rootPane, "Erro", "Erro inesperado, tente novamente", JOptionPane.ERROR_MESSAGE);
            s.close();
            System.exit(1);
        }
        //chat = new GerenciaChat();
        //t = new Thread(chat);
        //t.start();

        //this.objAttLista = new AttLista();
        //this.threadAttLista = new Thread(objAttLista);
        //threadAttLista.start();
        
        this.threadAttLista = new Thread(objAttLista);
        threadAttLista.start();
        
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
    
   
    private void enviaArquivo(String caminho)
    {
        float jaEnviado = 0;
        try {
            fileIn = new FileInputStream(caminho);
            objOut = new ObjectOutputStream(s.getOutputStream());
            
            byte[] buffer = new byte[4096];
            int len = 0;
           
            while(true){
                len = fileIn.read(buffer);
                System.out.println( "Enviado: "+((jaEnviado += 4096) / tamanho) * 100+"%" );
                
                if(len == -1) {
                    break;
                }
                objOut.write(buffer, 0, len);
                objOut.flush();
                
            }
            
            System.out.println("Cliente: Enviado");
            fileIn.close();
            }catch (FileNotFoundException ex) {
                JOptionPane.showMessageDialog(this, "Arquivo não encontrado!", "Erro", JOptionPane.ERROR_MESSAGE);
            }catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Desculpe, serviço temporariamente offline", "Aviso", JOptionPane.INFORMATION_MESSAGE);
                System.exit(1);
            }
        
    }
    
    private void recebeArquivo(String caminho)
    {
        int tamanho = Integer.valueOf(recebeMensagem()); //Recebe tam do arquivo
        System.out.println("Size: "+tamanho);
        
        try {
            fileOut = new FileOutputStream(caminho);
            objIn = new ObjectInputStream(s.getInputStream());
            
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
            
   
            }catch (FileNotFoundException ex) {
                Logger.getLogger(TelaCliente.class.getName()).log(Level.SEVERE, null, ex);
            }catch (SocketException ex) {
                JOptionPane.showMessageDialog(null, "Desculpe, serviço temporariamente offline", "Aviso", JOptionPane.INFORMATION_MESSAGE);
                System.exit(1);
            }catch (Exception ex) {
                
            }
    }
    
    public String getNome(){
        return this.nome;
    }
    
    private String informacoes(){
        String escolhido;
        escolhido = String.valueOf(LItens.getSelectedValuesList());
        escolhido = escolhido.replace("[", "");
        escolhido = escolhido.replace("]", "");
        
        return escolhido;
    }
    
    public void MetodoAttLista(){
        
        String arquivoEncontrado;
        
        enviaMensagem(LISTAR); //Envia protocolo de listagem
        
        DefaultListModel model = new DefaultListModel();
        
        LItens.setModel(model);
        
        while( ! (arquivoEncontrado = recebeMensagem()).equals(FIMLISTAGEM) ){
            model.addElement(arquivoEncontrado); 
        }
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jDesktopPane1 = new javax.swing.JDesktopPane();
        BUpload = new javax.swing.JButton();
        BLogOut = new javax.swing.JButton();
        BList = new javax.swing.JButton();
        BDownload = new javax.swing.JButton();
        TFSearch = new javax.swing.JTextField();
        BSearch = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        LItens = new javax.swing.JList();
        FEscolheArquivo = new javax.swing.JFileChooser();
        jScrollPane3 = new javax.swing.JScrollPane();
        LItens1 = new javax.swing.JList();
        jScrollPane2 = new javax.swing.JScrollPane();
        JTextArea = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        jDesktopPane1.setBackground(new java.awt.Color(204, 204, 204));

        BUpload.setText("Upload");
        BUpload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BUploadActionPerformed(evt);
            }
        });
        jDesktopPane1.add(BUpload);
        BUpload.setBounds(190, 10, 180, 30);

        BLogOut.setText("LogOut");
        BLogOut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BLogOutActionPerformed(evt);
            }
        });
        jDesktopPane1.add(BLogOut);
        BLogOut.setBounds(560, 10, 160, 30);

        BList.setText("List");
        BList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BListActionPerformed(evt);
            }
        });
        jDesktopPane1.add(BList);
        BList.setBounds(10, 10, 170, 30);

        BDownload.setText("Download");
        BDownload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BDownloadActionPerformed(evt);
            }
        });
        jDesktopPane1.add(BDownload);
        BDownload.setBounds(380, 10, 170, 30);
        jDesktopPane1.add(TFSearch);
        TFSearch.setBounds(10, 50, 540, 30);

        BSearch.setText("Search");
        BSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BSearchActionPerformed(evt);
            }
        });
        jDesktopPane1.add(BSearch);
        BSearch.setBounds(560, 50, 160, 30);

        LItens.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(LItens);

        jDesktopPane1.add(jScrollPane1);
        jScrollPane1.setBounds(10, 90, 710, 290);
        jDesktopPane1.add(FEscolheArquivo);
        FEscolheArquivo.setBounds(0, 50, 582, 397);

        LItens1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane3.setViewportView(LItens1);

        jDesktopPane1.add(jScrollPane3);
        jScrollPane3.setBounds(10, 90, 560, 290);

        JTextArea.setColumns(20);
        JTextArea.setRows(5);
        JTextArea.setEnabled(false);
        jScrollPane2.setViewportView(JTextArea);

        jDesktopPane1.add(jScrollPane2);
        jScrollPane2.setBounds(10, 400, 710, 100);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jDesktopPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 740, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jDesktopPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 520, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void BListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BListActionPerformed
        MetodoAttLista();
    }//GEN-LAST:event_BListActionPerformed
    
    private void BSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BSearchActionPerformed
        
        String arquivoEncontrado;
        pesquisa = TFSearch.getText();
        boolean achou = false;
        enviaMensagem(PESQUISAR);
        enviaMensagem(pesquisa);
       
        DefaultListModel model = new DefaultListModel();  
        LItens.setModel(model);
        
        while(true){
            arquivoEncontrado = recebeMensagem();
            if(arquivoEncontrado.equals(SEMARQUIVO))
               break;
            achou = true;
            model.addElement(arquivoEncontrado); 
        }if(!achou){
            LItens.setModel(new DefaultListModel());
            JOptionPane.showMessageDialog(this ,"Arquivo não encontrado", "Aviso", JOptionPane.INFORMATION_MESSAGE);
        }   
    }//GEN-LAST:event_BSearchActionPerformed

    private void BUploadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BUploadActionPerformed
        FEscolheArquivo.setEnabled(true); //Falta completar
        FEscolheArquivo.setVisible(true);
        FEscolheArquivo.showOpenDialog(this);
     //Armazena tam do arquivo
       try{ 
        Socket socket;
        socket = new Socket("127.0.0.1", 4444);
        
        String caminhoArquivo = "";
        String nomeArquivo = "";
        
        caminhoArquivo = FEscolheArquivo.getSelectedFile().getAbsolutePath();
        
        if(!caminhoArquivo.equals(null)){ //Se o usuario selecionou um arquivo
            tamanho = FEscolheArquivo.getSelectedFile().length();
            nomeArquivo = FEscolheArquivo.getSelectedFile().getName();
           // enviaMensagem(FAZERUPLOAD); //requisita upload para ao servidor
            fazUpload = new FazUpload(socket, caminhoArquivo, tamanho, nomeArquivo, JTextArea);
            
            //enviaMensagem(String.valueOf(tamanho)); //Envia tam do arquivo pro servidor
            //enviaMensagem(nomeArquivo);
            //enviaArquivo(caminhoArquivo);
        
            System.out.println(caminhoArquivo);
            fazUpload.start();
            ;
       
         }
       }catch(Exception e) {e.printStackTrace();}
    }//GEN-LAST:event_BUploadActionPerformed

    private void BDownloadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BDownloadActionPerformed
        String escolhido;
        escolhido = informacoes();
        //escolhido = JOptionPane.showInputDialog("Nome do arquivo: ");
        int tamanho;
        
        
        try{
            Socket socket;
            socket = new Socket("127.0.0.1", 4444);
            
            if( ! escolhido.equals("") ){ //Se o usuario nao escolheu arquivo
                fazDownload = new FazDownload(socket, path+escolhido, escolhido, JTextArea);
               // enviaMensagem(FAZERDOWNLOAD);
                //enviaMensagem(escolhido);
                //System.out.println("Escolhido: "+escolhido);
                //tamanho = Integer.valueOf(recebeMensagem()); //Recebe tam do arquivo
                fazDownload.start();
                //recebeArquivo(path+escolhido);
       
            }else
                JOptionPane.showMessageDialog(this, "Escolha um arquivo primeiro!", "Info", JOptionPane.INFORMATION_MESSAGE);
            }catch(Exception e) {e.printStackTrace();}
         
    }//GEN-LAST:event_BDownloadActionPerformed

    private void BLogOutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BLogOutActionPerformed
//        chat.Sair();
        enviaMensagem(SAIR);
        //chat = null;
        System.exit(0);
    }//GEN-LAST:event_BLogOutActionPerformed
    
    public static void main(String args[])  throws IOException  {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(TelaCliente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TelaCliente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TelaCliente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TelaCliente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        
        //AttLista attlista = new AttLista();
        //Thread threadAttLista = new Thread(attlista);
        //threadAttLista.start();
        
        
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new TelaCliente().setVisible(true);    
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        
        
        
        
        
       
        
        //objOut = new ObjectOutputStream(s.getOutputStream());
        //objIn = new ObjectInputStream(s.getInputStream());
        
        /*
        ObjectOutputStream obj = new ObjectOutputStream(s.getOutputStream());
        FileInputStream file = new FileInputStream("C:/Users/Igor/Documents/Musicas/exemplo.mp3");
        byte[] buffer = new byte[4096];
        */
      // while(nome == null)
           //nome = JOptionPane.showInputDialog("Digite seu nome: ");
        
        
        //while(true){
           // System.out.println(in.readUTF());
        
           /* int len = file.read(buffer);
            if(len == -1)
                break;
            obj.write(buffer, 0, len);
            */
            
            //in.close();
            //out.close();
            //s.close();
        //}
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BDownload;
    private javax.swing.JButton BList;
    private javax.swing.JButton BLogOut;
    private javax.swing.JButton BSearch;
    private javax.swing.JButton BUpload;
    private javax.swing.JFileChooser FEscolheArquivo;
    private javax.swing.JTextArea JTextArea;
    private javax.swing.JList LItens;
    private javax.swing.JList LItens1;
    private javax.swing.JTextField TFSearch;
    private javax.swing.JDesktopPane jDesktopPane1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    // End of variables declaration//GEN-END:variables
}