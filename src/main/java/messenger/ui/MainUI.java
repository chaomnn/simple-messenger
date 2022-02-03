package messenger.ui;

import messenger.AppContext;
import messenger.transport.Transport;
import messenger.transport.protocol.Message;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

class ChatViewer extends JPanel {


    private JPanel root = new JPanel();

    ChatViewer() {
        super();

        setSize(new Dimension(800, 500));
        setPreferredSize(getSize());
        setMinimumSize(getSize());
        setMaximumSize(getSize());

        setLayout(new BorderLayout());
        add(new JScrollPane(root, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER), BorderLayout.CENTER);

        setLayout(new GridLayout(0, 1));
        setBackground(Color.RED);
        root.setBackground(Color.DARK_GRAY);
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));

        System.out.println(getSize());
        new Thread(() -> {
            while(true) {
                while (!Transport.getInput().isEmpty()) {
                    Object data = Transport.getInput().poll();
                    System.out.println(data);

                    SwingUtilities.invokeLater(() -> {
                        if (data instanceof Message) {
                            Message msg = (Message) data;
                            showMessage(msg);
                        }
                    });
                }

                try {
                    Thread.sleep(1000L);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    void showMessage(Message message) {
        JTextArea text = new JTextArea(message.getSender() + ": " + message.getContent());                            text.setWrapStyleWord(true);
        text.setLineWrap(true);
        text.setEditable(true);
        root.add(text);
        root.revalidate();
        root.repaint();
    }
}

class SendMessageForm extends JPanel {

    private static final int HEIGHT = 100;

    private JTextArea messageField;

    private JButton sendButton;

    SendMessageForm(ChatViewer chatViewer) {
        sendButton = new JButton("Send");
        sendButton.setSize(new Dimension(HEIGHT, HEIGHT));
        sendButton.setPreferredSize(sendButton.getSize());
        sendButton.setMinimumSize(sendButton.getSize());
        sendButton.setMaximumSize(sendButton.getSize());

        messageField = new JTextArea();
        messageField.setWrapStyleWord(true);
        messageField.setLineWrap(true);

        messageField.setSize(getWidth() - sendButton.getWidth(), HEIGHT);
        messageField.setPreferredSize(messageField.getSize());
        messageField.setMinimumSize(messageField.getSize());
        messageField.setMaximumSize(messageField.getSize());

        setLayout(new BorderLayout());
        add(messageField, BorderLayout.CENTER);
        add(sendButton, BorderLayout.LINE_END);

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String messageContent = messageField.getText().trim();

                String receiver = messageContent.substring(0, messageContent.indexOf(':'));

                String message = messageContent.substring(messageContent.indexOf(":") + 1);

                Message msg = new Message(AppContext.login, receiver, message);
                chatViewer.showMessage(msg);
                Transport.sendData(msg);
                messageField.setText("");
            }
        });
    }
}

public class MainUI extends JFrame {

    private static final String TITLE = "Messenger";

    private static final int WIDTH = 800;

    private static final int HEIGHT = 600;

    public MainUI() throws HeadlessException {
        super(TITLE + " :: " + AppContext.login);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setResizable(false);

        ChatViewer chatViewer = new ChatViewer();

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(chatViewer, BorderLayout.CENTER);
        getContentPane().add(new SendMessageForm(chatViewer), BorderLayout.PAGE_END);
    }
}