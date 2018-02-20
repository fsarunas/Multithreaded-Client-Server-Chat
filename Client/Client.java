import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.GridLayout;
import java.awt.BorderLayout;

public class Client {
	private String serverIP = "localhost";
	private int serverPort = 7777;
	private DataInputStream dataInputStream;
	private DataOutputStream dataOutputStream;
	private Socket socket;

	public Client() {
		try {
			socket = new Socket(serverIP, serverPort);
			dataInputStream = new DataInputStream(socket.getInputStream());
			dataOutputStream = new DataOutputStream(socket.getOutputStream());
		} catch (Exception e) {throw new RuntimeException(e);}

		GUI gui = new GUI(this);
		
		while(true) {
			String inputMessage = "";
			try {
				inputMessage = dataInputStream.readUTF();
				gui.getResponse(inputMessage);
			} catch (Exception e) {}
		}
	}

	public void sendMessage(String outputMessage) {
		try {
			dataOutputStream.writeUTF(outputMessage);
		} catch (Exception e) {}
	}

//##########################################################################\\

	public static void main(String[] args) {
		new Client();
	}

//##########################################################################\\

	private class GUI extends JFrame {
		private JTextArea textArea;

		public GUI(Client client) {
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			setSize(400,400);

			JPanel panel = new JPanel();
			panel.setLayout(new BorderLayout());

			JPanel bottomPanel = new JPanel();
			bottomPanel.setLayout(new BorderLayout());

			JTextField textField = new JTextField();
			textField.requestFocusInWindow();
			textField.addActionListener(e -> {
				String str = textField.getText();
				if(!str.equals("")) {
					client.sendMessage(textField.getText());
					textField.setText("");
				}
			});
			JButton button = new JButton("Send");
			button.addActionListener(e -> {
				String str = textField.getText();
				if(!str.equals("")) {
					client.sendMessage(textField.getText());
					textField.setText("");
				}
			});

			bottomPanel.add(textField, BorderLayout.CENTER);
			bottomPanel.add(button, BorderLayout.EAST);

			textArea = new JTextArea();
			textArea.setEditable(false);
			textArea.setLineWrap(true);
			panel.add(textArea, BorderLayout.CENTER);
			panel.add(bottomPanel, BorderLayout.SOUTH);

			add(panel);
			setVisible(true);
		}

		public void getResponse(String str) {
			textArea.append(str);
		}
	}
}