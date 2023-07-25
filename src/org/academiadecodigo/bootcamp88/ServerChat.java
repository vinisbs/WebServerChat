package org.academiadecodigo.bootcamp88;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerChat {

    private ServerSocket serverSocket;
    private List<ClientHandler> clients;

public void start(int port){
    try {
        // Inicializando o socket do servidor
    serverSocket = new ServerSocket(port);
    System.out.println("Servidor de corversinha rolando na porta...  " + port);

    clients = new ArrayList<>();

        // Aguardando conexões de clientes

    while(true){
        Socket clientSocket = serverSocket.accept();
        System.out.println( clientSocket.getInetAddress().getHostName()
                + "...entrou na sala.");

        // Criando uma thread para lidar com o cliente conectado

        ClientHandler clientHandler = new ClientHandler(clientSocket);
        clients.add(clientHandler);
        Thread clientThread = new Thread(clientHandler);
        clientThread.start();
        }
    } catch(IOException e) {
        e.printStackTrace();
    } finally {
        try {
            if(serverSocket != null){
                serverSocket.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}

private class ClientHandler implements Runnable {

    private Socket clientSocket;
    private BufferedReader reader;
    private PrintWriter writer;

    public ClientHandler(Socket clientSocket){
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {

            // Inicializando reader e writer(Streams)

    reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    writer = new PrintWriter(clientSocket.getOutputStream(),true);

            String clientMessage;

            while((clientMessage = reader.readLine())!=null){

// Enviando a mensagem recebida para os clientes conectados (menos o remetente!)

                System.out.println( clientSocket.getInetAddress().getHostName()
                        +"  disse..." + clientMessage);
                broadcastMessage(clientMessage,this);
            }
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            try{
                if(clientSocket !=null){
                    clientSocket.close();
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public void sendMessage(String message){
        writer.println(message);
    }
}
    public void broadcastMessage(String message, ClientHandler sender){
        for (ClientHandler clientHandler : clients){
            if(clientHandler != sender)
            clientHandler.sendMessage(message);
        }
    }

    public static void main( String[] args ) {
        ServerChat serverChat = new ServerChat();
        serverChat.start(8080);
    }
}
