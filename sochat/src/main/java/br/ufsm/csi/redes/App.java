package br.ufsm.csi.redes;

import br.ufsm.csi.redes.services.ProbeService;
import br.ufsm.csi.redes.services.RecipientService;
import br.ufsm.csi.redes.views.ChatClientSwing;

import java.net.UnknownHostException;

public class App {
    private static App INSTANCE;
    private ChatClientSwing chatClient;

    public static void main(String[] args) throws UnknownHostException {
        INSTANCE = new App();

        ProbeService.init();
        RecipientService.init();
    }

    public App() {
        this.chatClient = new ChatClientSwing();
    }

    public static ChatClientSwing getChatClient() {
        if (INSTANCE == null) {
            throw new RuntimeException("Não instanciado!");
        }

        return INSTANCE.chatClient;
    }
}
