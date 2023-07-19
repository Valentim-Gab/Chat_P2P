package br.ufsm.csi.redes.thread;

import br.ufsm.csi.redes.App;
import br.ufsm.csi.redes.enums.UserStatus;
import br.ufsm.csi.redes.models.Probe;
import br.ufsm.csi.redes.models.User;
import br.ufsm.csi.redes.views.ChatClientSwing;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.Set;

public class ReceiveProbe implements Runnable {
    final Set<User> userList;
    final ChatClientSwing chatClient;

    public ReceiveProbe(Set<User> userList) {
        this.chatClient = App.getChatClient();
        this.userList = userList;
    }

    @SneakyThrows
    @Override
    public void run() {
        try (DatagramSocket socket = new DatagramSocket(8080)) {
            while (true) {
                byte[] buffer = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buffer, 0, buffer.length);
                socket.receive(packet);

                InetAddress packetAddr = packet.getAddress();
                InetAddress machineAddr = this.getOutboundAddress(packet.getSocketAddress());

                if (packetAddr.getHostAddress().equals(machineAddr.getHostAddress())) {
                    continue;
                }

                String pacoteStr = new String(buffer, 0, packet.getLength(), StandardCharsets.UTF_8);
                Probe probe = new ObjectMapper().readValue(pacoteStr, Probe.class);

                User user = User.builder()
                        .timeStampProbe(System.currentTimeMillis())
                        .name(probe.getUser())
                        .status(UserStatus.valueOf(probe.getStatus()))
                        .address(packet.getAddress())
                        .build();

                synchronized (userList) {
                    userList.remove(user);
                    chatClient.userList(user, "remove");
                    userList.add(user);
                    chatClient.userList(user, "add");
                }
            }
        }
    }

    private InetAddress getOutboundAddress(SocketAddress remoteAddress) throws SocketException {
        DatagramSocket sock = new DatagramSocket();
        sock.connect(remoteAddress);

        final InetAddress localAddress = sock.getLocalAddress();

        sock.disconnect();
        sock.close();
        sock = null;

        return localAddress;
    }
}
