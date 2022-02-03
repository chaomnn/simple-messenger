package messenger.ui;

import messenger.AppContext;
import messenger.Main;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

class SignInForm extends JComponent {

    private static final int FORM_MARGIN = 50;

    private static final int FIELD_WIDTH = 140;

    private static final int FIELD_HEIGHT = 30;

    private static final int NO_MARGIN = 0;

    private static final int BOTTOM_MARGIN = 10;

    private final JTextField loginField;

    private final JButton signInButton;

    SignInForm() {
        setBorder(new EmptyBorder(FORM_MARGIN, FORM_MARGIN, FORM_MARGIN, FORM_MARGIN));
        setLayout(new GridBagLayout());

        GridBagConstraints loginLabelOpts = new GridBagConstraints();
        loginLabelOpts.gridx = 0;
        loginLabelOpts.gridy = 0;
        loginLabelOpts.insets = new Insets(NO_MARGIN, NO_MARGIN, BOTTOM_MARGIN, NO_MARGIN);

        add(createLoginLabel(), loginLabelOpts);

        GridBagConstraints loginFieldOpts = new GridBagConstraints();
        loginFieldOpts.gridx = 0;
        loginFieldOpts.gridy = 1;
        loginFieldOpts.insets = new Insets(NO_MARGIN, NO_MARGIN, BOTTOM_MARGIN, NO_MARGIN);

        add(loginField = createLoginField(), loginFieldOpts);

        GridBagConstraints signInButtonOpts = new GridBagConstraints();
        signInButtonOpts.gridx = 0;
        signInButtonOpts.gridy = 2;

        add(signInButton = createSignInButton(), signInButtonOpts);

        signInButton.addActionListener((onClickEvent) -> {
            String loginFieldContent = loginField.getText();

            if ((loginFieldContent == null) || loginFieldContent.trim().isEmpty()) {
                return;
            }

            setVisible(false);

            String login = loginFieldContent.trim();
            AppContext.login = login;
            Main.signIn(login);
        });
    }

    private static JLabel createLoginLabel() {
        return new JLabel("Enter name:");
    }

    private static JTextField createLoginField() {
        JTextField field = new JTextField();
        field.setSize(new Dimension(FIELD_WIDTH, FIELD_HEIGHT));
        field.setPreferredSize(field.getSize());
        field.setMinimumSize(field.getSize());
        field.setMaximumSize(field.getSize());
        field.setHorizontalAlignment(JTextField.CENTER);
        return field;
    }

    private static JButton createSignInButton() {
        return new JButton("Sign in");
    }
}