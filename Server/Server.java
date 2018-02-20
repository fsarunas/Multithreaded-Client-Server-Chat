import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.OutputStream;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.ArrayList;

public class Server implements Runnable {
	private int serverPort;
	private List<Socket> allClients;
	private ServerSocket serverSocket;
	private DataOutputStream dataOutputStream;

	public Server(int port) {
		serverPort = port;
		allClients = new ArrayList<Socket>();
	}

	public void run() {

		try {
			serverSocket = new ServerSocket(serverPort);
		} catch (Exception e) {System.out.println("Failed to create server.");}

		System.out.println("--- SERVER CREATED ---");
		while(true) {
			try {
				Socket clientSocket = serverSocket.accept();
				allClients.add(clientSocket);
				System.out.println("\n --- NEW CLIENT HAS CONNECTED TO THE SERVER ---");
				System.out.println("\nCLIENT'S IP ADDRESS: " + clientSocket.getInetAddress().getHostAddress());
				Runnable connectionHandler = new ConnectionHandler(clientSocket, this);
				new Thread(connectionHandler).start();
			} catch (Exception e) {}
		}
	}

	public void sendResponse(String str) {
		for(Socket socket : allClients) {
			try {
				dataOutputStream = new DataOutputStream(socket.getOutputStream());
				dataOutputStream.writeUTF(str);
			} catch(Exception e) {}
		}
	}

// ############################################################################################################

	public static void main(String[] args) {
		Server server = new Server(7777);
		new Thread(server).start();
	}

// ############################################################################################################

	private class ConnectionHandler implements Runnable {
		private DataInputStream dataInputStream;
		private Socket clientSocket;
		private Server server;

		public ConnectionHandler(Socket clientSocket, Server server) {
			this.clientSocket = clientSocket;
			this.server = server;
		}

		public void run() {
			try {
				dataInputStream = new DataInputStream(clientSocket.getInputStream());

				while(true) {
					String inputMessage = "";
					inputMessage = dataInputStream.readUTF();
					server.sendResponse(clientSocket.getInetAddress().getHostAddress() + ": " + inputMessage + "\n");
				}
			} catch(Exception e) {}
		}
	}
}