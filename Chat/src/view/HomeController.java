package view;

import controller.Cliente;
import java.applet.Applet;
import java.applet.AudioClip;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.*;

public class HomeController implements Initializable {
    
    @FXML Button btnEnviar; 
    @FXML Button btnConectar;
    @FXML Button btnLogar; 
    @FXML Button btnSair; 
    @FXML Button btnSelecionarTodos; 
    @FXML TextField inputMensagem;
    @FXML TextField inputLogin;
    @FXML TextField inputIp;
    @FXML TextField inputPorta;
    @FXML CheckBox ckbLocal;
    @FXML Label parametro;
    @FXML Label destinatario;
    @FXML Label erro;
    @FXML ListView<String> lista = new ListView<String>();
    @FXML ListView<String> campo = new ListView<String>();
    
    private PrintStream saida;
    private Socket socket;
    private ArrayList<String> destinatarios = new ArrayList();
    
    public void enviar(ActionEvent event) throws IOException{
        
            if(destinatario.getText().equals("mensagem:") || destinatario.getText().equals("")){
                erro.setText("Nenhum usuário selecionado!");
            }else{
                saida.println(parametro.getText()+":"+inputMensagem.getText());
                campo.getItems().add("Eu: "+inputMensagem.getText());
                inputMensagem.clear();

                campo.setCellFactory(cp -> new ListCell<String>() {
                private final Label label = new Label();
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        label.setText(item);
                        setGraphic(label);
                        if(item.startsWith("Eu:")){
                            this.setStyle("-fx-alignment: CENTER-RIGHT");
                        }else{
                            this.setStyle("-fx-alignment: CENTER-LEFT");
                        }
                    }
                }
            });

            campo.scrollTo(campo.getItems().size() - 1);
        
        }
    }
     
    public void logar(ActionEvent event) throws IOException{
        saida.println("login:"+inputLogin.getText()); 
        btnSair.setDisable(false);
        btnEnviar.setDisable(false);
        btnSelecionarTodos.setDisable(false);
        inputMensagem.setDisable(false);
        inputLogin.setDisable(true);
        btnLogar.setDisable(true);
        erro.setText("Bem-vindo(a) "+inputLogin.getText()+"!");
    }
    
    public void sair(ActionEvent event) throws IOException{
        System.exit(0);
    }
    
    public void receberMensagem(String mensagem, String remetente){
        Platform.runLater(() ->{
            campo.getItems().add(remetente+": "+mensagem); 
            
            campo.setCellFactory(cp -> new ListCell<String>() {
               private final Label label = new Label();
               @Override
               protected void updateItem(String item, boolean empty) {
                   super.updateItem(item, empty);
                   if (empty) {
                       setGraphic(null);
                   } else {
                       label.setText(item);
                       setGraphic(label);
                       if(!item.startsWith("Eu:")){
                           this.setStyle("-fx-alignment: CENTER-LEFT");
                       }else{
                           this.setStyle("-fx-alignment: CENTER-RIGHT");
                       }
                   }
               }
           });
            
            campo.scrollTo(campo.getItems().size() - 1);
         
        });
        
    }
    
    public void listarUsuarios(String usuarios){
        Platform.runLater(() ->{
            lista.setItems(FXCollections.observableArrayList(usuarios.split(";")));
        });
    }
    
    public void selecionarUsuario(){
        boolean jaSelecionado = false;
        String selecionado = lista.getSelectionModel().getSelectedItem().toString();
        for (Iterator<String> i = destinatarios.iterator(); i.hasNext();) {
            String dest = i.next();
            if(dest.equals(selecionado)){
                jaSelecionado = true;
                i.remove();
            }
        }
        if(!jaSelecionado && !selecionado.equals(inputLogin.getText())){
            erro.setText("Usuário selecionado!");
            destinatarios.add(selecionado);
        }
        else if(selecionado.equals(inputLogin.getText())){
            erro.setText("Não é possível selecionar você mesmo!");
        }
        parametro.setText("mensagem:"+String.join(";", destinatarios));
        destinatario.setText(String.join(";", destinatarios));
    }
    
    public void selecionarTodos(){
        parametro.setText("mensagem:*");
        destinatario.setText("todos");
    }
    
    public void retornarErro(String msg){
        Platform.runLater(() ->{
            erro.setText(msg);
            erro.setVisible(true);
            btnSair.setDisable(true);
            btnEnviar.setDisable(true);
            btnSelecionarTodos.setDisable(true);
            inputMensagem.setDisable(true);
            inputLogin.setDisable(false);
            btnLogar.setDisable(false);
        });
    }
    
    public void setarRedeLocal(){
        if(ckbLocal.isSelected()){
            inputIp.setText("127.0.0.1");
            inputIp.setDisable(true);
        }else{
            inputIp.setText("");
            inputIp.setDisable(false);
        }
    }
    
    public void conectar() throws IOException{
       
        Socket socket = new Socket(inputIp.getText(), Integer.parseInt(inputPorta.getText()));
        System.out.println("O cliente se conectou ao servidor!");

        saida = new PrintStream(socket.getOutputStream());

        Cliente cliente = new Cliente(socket, this);
        cliente.start();

        inputIp.setDisable(true);
        inputPorta.setDisable(true);
        btnConectar.setDisable(true);
        ckbLocal.setDisable(true);
        inputLogin.setDisable(false);
        btnLogar.setDisable(false);
        
    }
    
    public void shutdown() {
       System.exit(0);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    } 
    
}
