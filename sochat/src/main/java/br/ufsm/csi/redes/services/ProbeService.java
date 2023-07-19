package br.ufsm.csi.redes.services;

import br.ufsm.csi.redes.models.User;
import br.ufsm.csi.redes.thread.ReceiveProbe;
import br.ufsm.csi.redes.thread.SendProbe;
import br.ufsm.csi.redes.thread.UpdateList;

import java.util.HashSet;
import java.util.Set;

public class ProbeService {
    private final Set<User> userList = new HashSet<>();

    private ProbeService() {
        this.initUpdateThread();
        this.initSendProbe();
        this.initReceiveProbe();
    }

    public static void init() {
        new ProbeService();
    }

    private void initUpdateThread() {
        UpdateList runnable = new UpdateList(userList);
        Thread thread = new Thread(runnable);
        thread.setName("Update list");

        thread.start();
    }

    private void initSendProbe() {
        SendProbe runnable = new SendProbe(userList);
        Thread thread = new Thread(runnable);
        thread.setName("Send probe");

        thread.start();
    }

    private void initReceiveProbe() {
        ReceiveProbe runnable = new ReceiveProbe(userList);
        Thread thread = new Thread(runnable);
        thread.setName("Receive probe");

        thread.start();
    }
}
