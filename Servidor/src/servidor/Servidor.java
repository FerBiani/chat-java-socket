package servidor;

import static com.sun.javafx.util.Utils.split;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class Servidor extends Thread{
    private Socket cliente;
    private static ArrayList<GerenciadorClientes> clientes = new ArrayList();
    private static ArrayList<String> nomeClientes = new ArrayList();
    private boolean existeCliente;
    
    public Servidor(Socket cliente) {
        this.cliente = cliente;
    }
    
    public void login(String nomeCliente, PrintStream saida) throws IOException{
        
        existeCliente = false;
              
        for(Iterator<GerenciadorClientes> i = clientes.iterator(); i.hasNext();){
            GerenciadorClientes g = i.next();
            if(g.getNome().equals(nomeCliente)){
               System.out.println("O cliente existe!");
               existeCliente = true;
            }
        }
          
        if(!existeCliente && nomeCliente.matches("[a-zA-Z0-9 ]*") && !nomeCliente.equals("")){
            saida.println("login:true");
            GerenciadorClientes gc = new GerenciadorClientes(nomeCliente, this.cliente);
            clientes.add(gc);
            nomeClientes.add(gc.getNome());
            System.out.println("Cliente conectado: "+ gc.getNome());
        }else{
            saida.println("login:false");
        }
        
        //envia a lista de usuários logados para todos os clientes quando alguém faz login
        for(GerenciadorClientes cliente : clientes){
           PrintStream saidaGeral = new PrintStream(cliente.getSocket().getOutputStream());
           saidaGeral.println("lista_usuarios:"+String.join(";", nomeClientes));
        }
 
    }
    
    public void mensagem(String destinatarios, String mensagem, int remetentePorta) throws IOException{  
        
        String remetente = "";
        String dest = "";
        
        for (GerenciadorClientes gc : clientes) {
            if(gc.getSocket().getPort() == remetentePorta){
                remetente = gc.getNome();
            }
        }
        
        if(destinatarios.equals("*")){
            ArrayList<String> nomeClientesTemp = new ArrayList();
            for(String nomeCliente : nomeClientes){
                if(!nomeCliente.equals(remetente)){
                    nomeClientesTemp.add(nomeCliente);
                }
            }
            destinatarios = String.join(";",nomeClientesTemp);
            nomeClientesTemp.clear();
        }
        
        for (int i = 0; i < clientes.size(); i++) {
            for (int j = 0; j < destinatarios.split(";").length; j++) {
                 if(clientes.get(i).getNome().equals(destinatarios.split(";")[j])){
                    System.out.println("Enviando para: "+clientes.get(i).getNome());
                    PrintStream saida = new PrintStream(clientes.get(i).getSocket().getOutputStream());
                    saida.println("transmitir:"+remetente+":"+String.join(";", destinatarios)+":"+mensagem);
                     System.out.println("transmitir:"+remetente+":"+String.join(";", destinatarios)+":"+mensagem);
                    //saida.println(mensagem);
                }
            }
        }

    }
  
    public static void main(String[] args) throws IOException {
        try{
            ServerSocket servidor = new ServerSocket(6666);
        
            System.out.println("Servidor iniciado na porta 6666");
        
            while(true){
                Socket cliente = servidor.accept();
                // cria uma nova thread para tratar essa conexão
                Servidor s = new Servidor(cliente);
                s.start();
                //clientes.add(cliente);    
            }
        }catch(Exception e){}
    }
    
    @Override
    public void run(){
        try {
            
        //conexão de saída de texto    
        PrintStream saida = new PrintStream(cliente.getOutputStream());
        
        //conexão de entrada de texto    
        Scanner entrada = new Scanner(cliente.getInputStream());
        
        int remetentePorta = cliente.getPort();
        
        //Scanner teclado = new Scanner(System.in);
        String nomeUsuario = "";
        while(entrada.hasNextLine()){
            String msg = entrada.nextLine();
            switch (split(msg, ":")[0]) {
                case "login":
                    login(msg.split(":",2)[1], saida);
                    nomeUsuario = msg.split(":",2)[1];
                    break;
                case "mensagem":
                    mensagem(split(msg, ":")[1], msg.split(":",3)[2], remetentePorta);
                    break;
                default:
                    System.out.println(msg);
                    System.out.println("Mensagem inválida!");
            }

        }
        
        if(!existeCliente){

            //Exclui o usuário que saiu do chat
            for(Iterator<GerenciadorClientes> i = clientes.iterator(); i.hasNext();){
                GerenciadorClientes gc = i.next();
                if(gc.getNome().equals(nomeUsuario)){;
                    i.remove();
                    nomeClientes.remove(gc.getNome());
                }
            }

            //Envia a nova lista de usuários
            for(Iterator<GerenciadorClientes> i = clientes.iterator(); i.hasNext();){
                GerenciadorClientes gc = i.next();
                PrintStream saidaGeral = new PrintStream(gc.getSocket().getOutputStream());
                saidaGeral.println("lista_usuarios:"+String.join(";", nomeClientes));
            }

            entrada.close();
            
            System.out.println(nomeUsuario+" saiu!");
            
        }
        
        } catch (IOException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
}
