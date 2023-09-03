package lk.ijse.groupchatapplication;

import java.io.*;
import java.net.Socket;

/**
 * Created By shamodha_s_rathnamalala
 * Date : 5/21/2023
 * Time :10:51 PM
 */

public class ClientHandler extends Thread {
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String userName;
    private Server server;

    public ClientHandler(Socket socket, Server server) {
        try {
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.setUserName(bufferedReader.readLine());
            this.server = server;
            this.server.broadcastMessage(this, userName+"/#sendingClientName#/"+"hello i'm join your to chat ..!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public void run() {
        while (true) {
            try {
                String message = bufferedReader.readLine();
                if (message != null) {
                    server.broadcastMessage(this, message);
                }
            } catch (IOException e) {
                server.broadcastMessage(this, userName+"/#sendingClientName#/"+"i'm left the chat ..!");
                server.removeClient(this);
                break;
            }
        }
        try {
            bufferedReader.close();
            bufferedWriter.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        try {
            bufferedWriter.write(message);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}