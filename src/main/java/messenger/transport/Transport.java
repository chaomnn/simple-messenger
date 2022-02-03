package messenger.transport;

import messenger.transport.protocol.Data;
import messenger.transport.protocol.Identification;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Transport {

    private static Socket socket;

    private static ObjectOutputStream socketOutput;

    private static ConcurrentLinkedQueue<Data> input = new ConcurrentLinkedQueue<>();

    public static Data sendData(Data data) {
        initSocket();

        try {
            socketOutput.writeObject(data);
            socketOutput.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return (data instanceof Identification) ? null : input.poll();
    }

    public static ConcurrentLinkedQueue<Data> getInput() {
        return input;
    }

    private static void initSocket() {
        if (socket != null) {
            return;
        }

        try {
            socket = new Socket("localhost", TransportConfiguration.PORT);
            System.out.println("Connection established.");
        } catch (IOException e) {
            System.out.println("Failed to establish connection to server.");
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        try {
            socketOutput = new ObjectOutputStream(socket.getOutputStream());
        } catch (Exception e) {
            System.out.println("Failed to establish I/O to server.");
            e.printStackTrace();
            closeSocket();
            throw new RuntimeException(e);
        }

        new Thread(() -> {
            try (ObjectInputStream socketInput = new ObjectInputStream(socket.getInputStream())) {
                while (!socket.isClosed()) {
                    try {
                        Object data = socketInput.readObject();

                        if (data instanceof Data) {
                            input.offer((Data) data);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                System.out.println("Failed to establish I/O to server.");
                e.printStackTrace();
                closeSocket();
                throw new RuntimeException();
            }
        }).start();
    }

    private static void closeSocket() {
        try {
            socket.close();
            socket = null;
            socketOutput = null;
            System.out.println("Connection closed.");
        } catch (Exception e) {
            System.out.println("Failed to close connection.");
            e.printStackTrace();
        }
    }
}