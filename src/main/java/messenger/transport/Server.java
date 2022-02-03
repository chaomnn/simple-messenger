package messenger.transport;

import messenger.transport.protocol.Identification;
import messenger.transport.protocol.Message;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Server {

    private static final ConcurrentMap<String, ObjectOutputStream> sessions = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        final ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(9999);
            System.out.println("Server started.");
        } catch (IOException e) {
            System.out.println("Failed to start server.");
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        while (serverSocket.isBound() && !serverSocket.isClosed()) {
            Socket clientSocket = null;
            try {
                clientSocket = serverSocket.accept();
                System.out.println("Income client socket connection accepted.");
            } catch (IOException e) {
                System.out.println("Failed to accept income client socket connection.");
                e.printStackTrace();
            }

            if (clientSocket != null) {
                try {
                    handleClientSocket(clientSocket);
                } catch (Exception e) {
                    System.out.println("Failed to handle client socket connection.");
                    e.printStackTrace();
                }
            }
        }
    }

    private static void handleClientSocket(Socket clientSocket) {
        new Thread(() -> {
            try {
                try (
                        ObjectOutputStream socketOutput = new ObjectOutputStream(clientSocket.getOutputStream());
                        ObjectInputStream socketInput = new ObjectInputStream(clientSocket.getInputStream())
                ) {
                    while (!clientSocket.isClosed()) {
                        Object data = null;
                        try {
                            try {
                                data = socketInput.readObject();
                                System.out.println(data);

                                if (data instanceof Identification) {
                                    Identification identification = (Identification) data;
                                    sessions.put(identification.getId(), socketOutput);
                                }

                                if (data instanceof Message) {
                                    Message message = (Message) data;

                                    if (sessions.containsKey(message.getReceiver())) {
                                        try {
                                            sessions.get(message.getReceiver()).writeObject(message);
                                            sessions.get(message.getReceiver()).flush();
                                        } catch (Exception e) {
                                            sessions.remove(message.getReceiver());
                                        }
                                    }
                                }
                            } catch (SocketException|EOFException e) {
                                throw new RuntimeException("Client closed connection or socket is broken.", e);
                            } catch (ClassNotFoundException e) {
                                throw new RuntimeException("I/O protocol is broken or outdated.", e);
                            } catch (IOException e) {
                                throw new RuntimeException("Unknown I/O error occurred.", e);
                            } catch (Exception e) {
                                throw new RuntimeException("Unknown error occurred.", e);
                            }
                        } catch (Exception e) {
                            System.out.printf("Failed to receive data from client: %s%n", e.getMessage());
                            if (!(e.getCause() instanceof SocketException || e.getCause() instanceof EOFException)) {
                                e.printStackTrace();
                            }
                            closeClientSocket(clientSocket);
                        }
                    }
                } catch (SocketException|EOFException e) {
                    throw new RuntimeException("Client closed connection or socket is broken.", e);
                } catch (IOException e) {
                    throw new RuntimeException("Unknown I/O error occurred.", e);
                } catch (Exception e) {
                    throw new RuntimeException("Unknown error occurred.", e);
                }
            } catch (Exception e) {
                System.out.printf("Failed to establish I/O to client: %s%n", e.getMessage());
                if (!(e.getCause() instanceof SocketException || e.getCause() instanceof EOFException)) {
                    e.printStackTrace();
                }
                closeClientSocket(clientSocket);
            }
        }).start();
    }

    private static void closeClientSocket(Socket clientSocket) {
        try {
            clientSocket.close();
            System.out.println("Client socket connection closed.");
        } catch (Exception e) {
            System.out.println("Failed to close client socket connection.");
            e.printStackTrace();
        }
    }
}