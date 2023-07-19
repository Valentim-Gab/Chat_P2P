package br.ufsm.csi.redes.server;

import lombok.SneakyThrows;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.ufsm.csi.redes.models.Message;
import br.ufsm.csi.redes.views.ChatClientSwing;

public class Server implements Runnable {
    private ChatClientSwing chatClient;

    public Server(ChatClientSwing chatClient) {
        this.chatClient = chatClient;
    }

    @Override
    @SneakyThrows
    public void run() {
        try (ServerSocket server = new ServerSocket(8081)) {
            while (true) {
                Socket socket = server.accept();

                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

                Message message = new ObjectMapper().readValue((String) in.readObject(), Message.class);

                if (message.getType() == Message.Type.DISCONNECT) {
                    this.chatClient.appendMessage(message, () -> message.getUser().getName() + " desconectou.\n\n");

                    continue;
                }

                this.chatClient.appendMessage(message, () -> {
                    return String.format("%s: %s%n", message.getUser().getName(), message.getContent());
                });

                in.close();
                out.close();
            }
        }
    }
}
