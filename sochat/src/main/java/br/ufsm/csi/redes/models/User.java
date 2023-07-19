package br.ufsm.csi.redes.models;

import br.ufsm.csi.redes.enums.UserStatus;
import lombok.*;

import java.net.InetAddress;

import javax.swing.JLabel;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private String name;
    private UserStatus status;
    private Long timeStampProbe;
    private InetAddress address;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        User user = (User) o;

        return name.equals(user.name);

    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    public String toString() {
        return this.getName();
    }

    public JLabel toLabel() {
        JLabel label = new JLabel();
        label.setText(this.status.getSymbol() + " " + this.getName());
        label.setForeground(status.getForeground());
        label.setBackground(status.getBackground());
        label.setOpaque(true);

        return label;
    }
}