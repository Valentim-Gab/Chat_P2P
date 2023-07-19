package br.ufsm.csi.redes.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Probe {
    private String messageType;
    private String user;
    private String status;
}
