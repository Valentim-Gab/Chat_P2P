package br.ufsm.csi.redes.services;

import br.ufsm.csi.redes.App;
import br.ufsm.csi.redes.server.Server;
import br.ufsm.csi.redes.views.ChatClientSwing;

public class RecipientService {
    private final ChatClientSwing chatClient;

    private RecipientService() {
        this.chatClient = App.getChatClient();

        this.initServer();
    }

    public static void init() {
        new RecipientService();
    }

    private void initServer() {
        Server runnable = new Server(this.chatClient);
        Thread thread = new Thread(runnable);
        thread.setName("Message recipient");

        thread.start();
    }
}
