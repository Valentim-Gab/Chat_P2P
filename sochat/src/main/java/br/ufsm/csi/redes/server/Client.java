package br.ufsm.csi.redes.server;

import br.ufsm.csi.redes.models.Message;
import br.ufsm.csi.redes.models.User;
import lombok.SneakyThrows;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Client {
    public static void send(User myUser, User user, String content) {
        Client.send(myUser, user, Message.Type.MESSAGE, content);
    }

    @SneakyThrows
    public static void send(User myUser, User user, Message.Type type, String content) {
        try (Socket socket = new Socket(user.getAddress(), 8081)) {
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            Message message = Message
                    .builder()
                    .content(content)
                    .user(myUser)
                    .type(type)
                    .build();

            out.flush();
            out.writeObject(new ObjectMapper().writeValueAsString(message));

            in.close();
            out.close();
            socket.close();
        }
    }
}
