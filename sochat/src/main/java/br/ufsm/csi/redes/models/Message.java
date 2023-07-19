package br.ufsm.csi.redes.models;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    public static enum Type {
        DISCONNECT,
        MESSAGE
    }

    private Type type;
    private User user;
    private String content;
}
