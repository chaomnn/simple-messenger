package messenger;

import messenger.transport.Transport;
import messenger.transport.protocol.Identification;
import messenger.ui.MainUI;
import messenger.ui.SignInUI;

public class Main2 {

    private static SignInUI signInUI;

    private static MainUI mainUI;

    public static void main(String[] args) {
        showSignInUI();
    }

    public static void signIn(String login) {
        Transport.sendData(new Identification(login));
        signInUI.setVisible(false);
        showMainUI();
    }

    private static void showSignInUI() {
        signInUI = new SignInUI();
        signInUI.setVisible(true);
    }

    private static void showMainUI() {
        mainUI = new MainUI();
        mainUI.setVisible(true);
    }
}