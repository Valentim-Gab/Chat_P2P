package br.ufsm.csi.redes.views;

import br.ufsm.csi.redes.enums.UserStatus;
import br.ufsm.csi.redes.models.Message;
import br.ufsm.csi.redes.models.User;
import br.ufsm.csi.redes.server.Client;
import lombok.SneakyThrows;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.*;
import java.util.function.Supplier;

public class ChatClientSwing extends JFrame {
    private User myUser;
    private DefaultListModel<User> dfListModel;
    private final JTabbedPane tabbedPane = new JTabbedPane();
    private final Set<User> openChats = new HashSet<>();
    private final Map<String, JTextArea> chatAreas = new HashMap<>();

    @SneakyThrows
    public ChatClientSwing() {
        setLayout(new GridBagLayout());

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Chat P2P - Redes de Computadores");
        String nomeUsuario = JOptionPane.showInputDialog(this, "Digite seu nome de usuário: ");
        this.myUser = new User(nomeUsuario, UserStatus.DISPONIVEL, 0L, InetAddress.getLocalHost());

        this.setSize(800, 600);
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        final int x = (screenSize.width - this.getWidth()) / 2;
        final int y = (screenSize.height - this.getHeight()) / 2;
        this.setLocation(x, y);

        JScrollPane sidebar = new JScrollPane(this.getSidebar());

        tabbedPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                if (e.getButton() == MouseEvent.BUTTON3) {
                    JPopupMenu popupMenu = new JPopupMenu();
                    final int tab = tabbedPane.getUI().tabForCoordinate(tabbedPane, e.getX(), e.getY());
                    JMenuItem item = new JMenuItem("Fechar");
                    item.addActionListener(e1 -> {
                        PainelChatPVT panel = (PainelChatPVT) tabbedPane.getComponentAt(tab);

                        openChats.remove(panel.getUsuario());
                        chatAreas.remove(panel.getUsuario().getName());
                        tabbedPane.remove(tab);

                        Client.send(myUser, panel.getUsuario(), Message.Type.DISCONNECT, null);
                    });
                    popupMenu.add(item);
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });

        add(sidebar, new GridBagConstraints(0, 0, 1, 1, 0.1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));
        add(tabbedPane, new GridBagConstraints(1, 0, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));
        setVisible(true);
    }

    private JPanel createUserInfo() {
        JPanel panel = new JPanel(new BorderLayout());

        // Create the user name label and the three buttons
        JLabel userNameLabel = new JLabel(this.myUser.getName());
        panel.add(userNameLabel, BorderLayout.NORTH);

        // Add the buttons to a panel
        JPanel buttons = new JPanel();

        for (UserStatus status : UserStatus.values()) {
            JButton button = new JButton();
            button.setText(status.getSymbol());

            button.setForeground(status.getForeground());
            button.setBackground(status.getBackground());

            button.setSelected(true);
            button.addActionListener(actionEvent -> {
                this.myUser.setStatus(status);
            });

            button.setFocusPainted(false);
            button.setBorderPainted(false);
            button.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

            // Center the text inside the button
            button.setHorizontalAlignment(JButton.CENTER);
            button.setVerticalAlignment(JButton.CENTER);

            button.setPreferredSize(new Dimension(30, 30));

            buttons.add(button);
        }

        // Add the buttons panel to the user panel
        panel.add(buttons, BorderLayout.SOUTH);

        return panel;
    }

    private JComponent createList() {
        dfListModel = new DefaultListModel<User>();

        JList<User> listaChat = new JList<>(dfListModel);

        listaChat.addMouseListener(new MouseAdapter() {
            @SuppressWarnings("unchecked")
            public void mouseClicked(MouseEvent evt) {
                JList<User> list = (JList<User>) evt.getSource();
                if (evt.getClickCount() == 2) {
                    int index = list.locationToIndex(evt.getPoint());
                    User user = (User) list.getModel().getElementAt(index);

                    openTab(user);
                }
            }
        });

        listaChat.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                User user = (User) value;
                return user.toLabel();
            };
        });

        return listaChat;
    }

    private JPanel getSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());

        JPanel userPanel = this.createUserInfo();
        sidebar.add(userPanel, BorderLayout.NORTH);

        sidebar.add(new JScrollPane(this.createList()), BorderLayout.CENTER);

        sidebar.setMaximumSize(getMaximumSize());

        return sidebar;
    }

    class PainelChatPVT extends JPanel {
        JTextArea areaChat;
        JTextField inputField;
        User user;

        PainelChatPVT(User user) {
            setLayout(new GridBagLayout());
            areaChat = new JTextArea();
            this.user = user;
            areaChat.setEditable(false);
            inputField = new JTextField();
            inputField.addActionListener(e -> {
                ((JTextField) e.getSource()).setText("");
                Client.send(myUser, user, e.getActionCommand());

                areaChat.append(myUser.getName() + ": " + e.getActionCommand() + "\n");
            });
            add(new JScrollPane(areaChat), new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER,
                    GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
            add(inputField, new GridBagConstraints(0, 1, 1, 1, 1, 0, GridBagConstraints.SOUTH, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));

            chatAreas.put(user.getName(), areaChat);
        }

        public User getUsuario() {
            return user;
        }

        public void setUsuario(User user) {
            this.user = user;
        }
    }

    public void userList(User user, String action) {
        if (action.equalsIgnoreCase("add"))
            dfListModel.addElement(user);
        else if (action.equalsIgnoreCase("remove"))
            dfListModel.removeElement(user);
    }

    public User getMyUser() {
        return myUser;
    }

    public void setMyUser(User myUser) {
        this.myUser = myUser;
    }

    public void appendMessage(Message message, Supplier<String> supplier) {
        JTextArea area = this.chatAreas.get(message.getUser().getName());

        // chat nao aberto no recipient, abrir!
        // se for msg de desconexão -> n abrir
        if (area == null && !message.getType().equals(Message.Type.DISCONNECT)) {
            this.openTab(message.getUser());
        }

        area = this.chatAreas.get(message.getUser().getName());

        area.append(supplier.get());
    }

    public void openTab(User user) {
        if (openChats.add(user)) {
            tabbedPane.add(user.toString(), new PainelChatPVT(user));
        }
    }

    public String getEndBroadcast() {
        return "255.255.255.255";
    }
}
