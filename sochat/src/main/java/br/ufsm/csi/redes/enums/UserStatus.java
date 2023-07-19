package br.ufsm.csi.redes.enums;

import java.awt.Color;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@AllArgsConstructor
public enum UserStatus {
    DISPONIVEL("⬤", "Disponível", new Color(0x3b, 0xa5, 0x5c)),
    NAO_PERTURBE("⛔", "Não perturbe", new Color(0xed, 0x42, 0x45)),
    VOLTO_LOGO("🕑", "Volto logo", new Color(0xfa, 0xa6, 0x1a));

    @Getter
    @NonNull
    String symbol;

    @Getter
    @NonNull
    String label;

    @Getter
    @NonNull
    Color background;

    @Getter
    Color foreground = Color.WHITE;
}
