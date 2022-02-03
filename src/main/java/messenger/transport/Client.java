package messenger.transport;

import messenger.transport.protocol.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Date;

public class Client {
    public static void main(String[] args) {
        Socket socket;
        try {
            socket = new Socket("localhost", TransportConfiguration.PORT);
            System.out.println("Connection established.");
        } catch (IOException e) {
            System.out.println("Failed to establish connection to server.");
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        try {
            try (
                    ObjectOutputStream socketOutput = new ObjectOutputStream(socket.getOutputStream());
                    ObjectInputStream socketInput = new ObjectInputStream(socket.getInputStream())
            ) {
                while (!socket.isClosed()) {
//                    try {
//                        try {
////                            socketOutput.writeObject(new Message(new Date().toString()));
////                            socketOutput.flush();
//                        } catch (SocketException e) {
//                            throw new RuntimeException("Server closed connection or socket is broken.", e);
//                        } catch (IOException e) {
//                            throw new RuntimeException("Unknown I/O error occurred.", e);
//                        } catch (Exception e) {
//                            throw new RuntimeException("Unknown error occurred.", e);
//                        }
//                    } catch (Exception e) {
//                        System.out.printf("Failed to send data to server: %s%n", e.getMessage());
//                        if (!(e.getCause() instanceof SocketException)) {
//                            e.printStackTrace();
//                        }
//                        closeSocket(socket);
//                    }

                    try {
                        try {
                            Object data = socketInput.readObject();
                            System.out.println("Received data: " + data);
                        } catch (SocketException e) {
                            throw new RuntimeException("Server closed connection or socket is broken.", e);
                        } catch (ClassNotFoundException e) {
                            throw new RuntimeException("I/O protocol is broken or outdated.", e);
                        } catch (IOException e) {
                            throw new RuntimeException("Unknown I/O error occurred.", e);
                        } catch (Exception e) {
                            throw new RuntimeException("Unknown error occurred.", e);
                        }
                    } catch (Exception e) {
                        System.out.printf("Failed to receive data from server: %s%n", e.getMessage());
                        if (!(e.getCause() instanceof SocketException)) {
                            e.printStackTrace();
                        }
                        closeSocket(socket);
                    }
                }
            } catch (SocketException e) {
                throw new RuntimeException("Server closed connection or socket is broken.", e);
            } catch (IOException e) {
                throw new RuntimeException("Unknown I/O error occurred.", e);
            } catch (Exception e) {
                throw new RuntimeException("Unknown error occurred.", e);
            }
        } catch (Exception e) {
            System.out.printf("Failed to establish I/O to server: %s%n", e.getMessage());
            if (!(e.getCause() instanceof SocketException)) {
                e.printStackTrace();
            }
            closeSocket(socket);
        }
    }

    private static void closeSocket(Socket socket) {
        try {
            socket.close();
            System.out.println("Connection closed.");
        } catch (Exception e) {
            System.out.println("Failed to close connection.");
            e.printStackTrace();
        }
    }
}