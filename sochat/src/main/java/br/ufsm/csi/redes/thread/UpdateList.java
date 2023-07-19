package br.ufsm.csi.redes.thread;

import br.ufsm.csi.redes.App;
import br.ufsm.csi.redes.models.User;
import br.ufsm.csi.redes.views.ChatClientSwing;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class UpdateList implements Runnable {
    final Set<User> userList;
    final ChatClientSwing chatClient;

    public UpdateList(Set<User> userList) {
        this.chatClient = App.getChatClient();
        this.userList = userList;
    }

    @SneakyThrows
    @Override
    public void run() {
        while (true) {
            Thread.sleep(8000);
            List<User> listRemove = new ArrayList<>();
            synchronized (userList) {
                for (User user : userList) {
                    long milisDifference = System.currentTimeMillis() - user.getTimeStampProbe();
                    if (milisDifference > 30_000) {
                        listRemove.add(user);
                    }
                }

                for (User user : listRemove) {
                    userList.remove(user);
                    chatClient.userList(user, "remove");
                }
            }
        }
    }
}