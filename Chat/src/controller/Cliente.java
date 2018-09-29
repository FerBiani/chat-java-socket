package controller;

import static com.sun.javafx.util.Utils.split;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import view.HomeController;

public class Cliente extends Thread{
    
    private Socket conexao;
    public String mensagem;
    public String destinatarios;
    private HomeController cnt;
    
    public Cliente(Socket socket, HomeController cnt) {
        this.conexao = socket;
        this.cnt = cnt;
    }
    
    public String mensagem(String destinatarios, String mensagem){
        return destinatarios+":"+mensagem;
    }
    
    
    @Override
    public void run()
    {
        try {
            
            //recebe mensagens de outro cliente através do servidor
            Scanner entrada = new Scanner(conexao.getInputStream());
            //cria variavel de mensagem
            String msg;
            while (true)
            {
                // pega o que o servidor enviou
                msg = entrada.nextLine();
                
                if(msg.equals("login:false")){
                    cnt.retornarErro("Já existe um usuário conectado com este nome!");
                }
                
                if(split(msg, ":")[0].equals("transmitir")){
                    cnt.receberMensagem(msg.split(":", 4)[3], msg.split(":", 4)[1]);
                }
                
                if(msg.split(":")[0].equals("lista_usuarios")){
                    cnt.listarUsuarios(msg.split(":",2)[1]);  
                }
                
            }
        } catch (IOException e) {
            // caso ocorra alguma exceção de E/S, mostra qual foi.
            System.out.println("Ocorreu uma Falha... .. ." + 
				" IOException: " + e);
        }
    }
   
}