package messenger.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Random;

public class Main {

    private static JTextArea createTextArea(String text) {
        JTextArea field = new JTextArea();
        field.setEditable(false);
        field.setLineWrap(true);
        field.setWrapStyleWord(true);
        field.setText(text);

        String s = "";

        int size = new Random().nextInt(1000);
        for (int i = 0; i < size; i++) {
            s += "a";
        }
        field.setText(s);
        field.setBackground(colors[new Random().nextInt(colors.length)]);

        return field;
    }

    private static final Color[] colors = {
            Color.BLACK,
            Color.GREEN,
            Color.ORANGE,
            Color.RED,
            Color.BLUE
    };

    private static JScrollPane scrollPane;

    public static void main(String[] args) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JFrame frame = new JFrame();
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(panel, BorderLayout.PAGE_START);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setVisible(true);

        panel.addComponentListener(new ComponentAdapter() {

            private boolean isReplaced = false;

            @Override
            public void componentResized(ComponentEvent e) {
                if (panel.getHeight() >= frame.getHeight() && !isReplaced) {
                    scrollPane = new JScrollPane(panel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

                    if (frame.getContentPane().getComponentCount() > 0) {
                        frame.getContentPane().remove(0);
                    }

                    frame.getContentPane().add(scrollPane, BorderLayout.CENTER);

                    JScrollBar vertical = scrollPane.getVerticalScrollBar();
                    vertical.setValue(vertical.getMaximum());

                    frame.revalidate();
                    frame.repaint();

                    isReplaced = true;
                }
            }
        });

        if (!false)
            new Thread(() -> {
                for (int i = 0; i < 100; i++) {
                    final int j = i;
                    SwingUtilities.invokeLater(() -> {
                        JTextArea field = new JTextArea();
                        field.setEditable(false);
                        field.setLineWrap(true);
                        field.setWrapStyleWord(true);
                        field.setText("String " + j);

                        panel.add(createTextArea(""));
                        panel.revalidate();
                        panel.repaint();

                        if (scrollPane != null) {
                            JScrollBar vertical = scrollPane.getVerticalScrollBar();
                            vertical.setValue(vertical.getMaximum());
                        }
                    });

                    try {
                        Thread.sleep(1000L);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
    }
}