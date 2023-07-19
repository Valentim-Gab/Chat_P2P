package br.ufsm.csi.redes.thread;

import br.ufsm.csi.redes.App;
import br.ufsm.csi.redes.models.Probe;
import br.ufsm.csi.redes.models.User;
import br.ufsm.csi.redes.views.ChatClientSwing;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.Set;

public class SendProbe implements Runnable {
    final Set<User> userList;
    final ChatClientSwing chatClient;

    public SendProbe(Set<User> userList) {
        this.chatClient = App.getChatClient();
        this.userList = userList;
    }

    @SneakyThrows
    @Override
    public void run() {
        try (DatagramSocket datagramSocket = new DatagramSocket()) {
            while (true) {
                Probe probe = Probe.builder()
                        .status(String.valueOf(chatClient.getMyUser().getStatus()))
                        .messageType("pobre")
                        .user(chatClient.getMyUser().getName())
                        .build();

                byte[] pacote = new ObjectMapper().writeValueAsString(probe).getBytes(StandardCharsets.UTF_8);

                DatagramPacket packet = new DatagramPacket(
                        pacote,
                        0,
                        pacote.length,
                        InetAddress.getByName(chatClient.getEndBroadcast()),
                        8080);
                datagramSocket.setBroadcast(true);
                datagramSocket.send(packet);
                Thread.sleep(5000);
            }
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }
}
