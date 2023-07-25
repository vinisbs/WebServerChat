package org.academiadecodigo.bootcamp88;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientChat {

    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;

public void start(String serverAdress, int serverPort) {
    try {
        socket = new Socket(serverAdress,serverPort);
        System.out.println("Conectado na bagaça...");

        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new PrintWriter(socket.getOutputStream(),true);

        Thread receiveThread = new Thread(new ReceiveMessageRunnable());
        receiveThread.start();

        BufferedReader userInputReader = new BufferedReader(new InputStreamReader(System.in));
        String userInput;
        while((userInput = userInputReader.readLine())!=null) {
            writer.println(userInput);
        }
    } catch (IOException e) {
        e.printStackTrace();
    }finally {
        try {
            if (socket !=null){
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
private class ReceiveMessageRunnable implements Runnable {

    @Override
    public void run() {
        try {
            String message;
            while((message = reader.readLine())!=null){
                System.out.println("veio do servidor..." + message);
            }
        } catch(IOException e){
            e.printStackTrace();
        }
    }
}

    public static void main( String[] args ) {

    ClientChat client = new ClientChat();
    client.start("localhost",8080);

    }
}
