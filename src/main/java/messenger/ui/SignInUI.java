package messenger.ui;

import javax.swing.*;
import java.awt.*;

public class SignInUI extends JFrame {

    private static final String TITLE = "Sign In";

    public SignInUI() {
        super(TITLE);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setLayout(new GridBagLayout());
        getContentPane().add(new SignInForm(), new GridBagConstraints());
        setResizable(false);
        pack();
        centerOnScreen();
    }

    private void centerOnScreen() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(screenSize.width / 2 - getSize().width / 2, screenSize.height / 2 - getSize().height / 2);
    }
}