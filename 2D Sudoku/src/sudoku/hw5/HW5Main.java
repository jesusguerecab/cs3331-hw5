package sudoku.hw5;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

import sudoku.dialog.SudokuDialog;
import sudoku.hw5.NetworkAdapter.MessageListener;
import sudoku.hw5.NetworkAdapter.MessageType;
import sudoku.model.Board;

public class HW5Main extends SudokuDialog implements MessageListener{

	JTextArea peerHostName;
	JTextArea peerPortNumber;
	JTextArea consolePanel;
	ServerSocket server;
	Socket socket;

	public HW5Main() {
		super();
	}

	public static void main(String[] args) {
		new HW5Main();
	}

	private ImageIcon NETWORK_OFF, NETWORK_ON;
	private JButton networkButton;

	protected JToolBar initToolBar() {
		JToolBar toolBar = super.initToolBar();
		NETWORK_OFF = createImageIcon("wifi-red.png");
		networkButton = new JButton(NETWORK_OFF);
		networkButton.addActionListener(this::networkButtonClicked);
		networkButton.setToolTipText("Pair");
		networkButton.setFocusPainted(false);
		toolBar.add(networkButton, toolBar.getComponentCount() - 1);
		return toolBar;
	}

	private void networkButtonClicked(ActionEvent e) { 
		try {
			socket = new Socket();
			server = new ServerSocket(0);
			
			JFrame networkPanel = networkPanel(socket);
			networkPanel.setVisible(true);
			
			System.out.println(InetAddress.getLocalHost().getHostAddress());
			new Thread(() -> { 
				try {
					Socket client = server.accept();
					pairAsServer(client);
				} catch(Exception a) { }
				}).start();
		} catch (Exception e1) {
			
		}
	}

	private JFrame networkPanel(Socket socket) throws UnknownHostException {
		JFrame frame = new JFrame("Network");

		JPanel panel = new JPanel(new GridLayout(4,1));
		panel.setPreferredSize(new Dimension(300, 400));
		
        final JPanel playerPanel = new JPanel(new GridLayout(3,2));
        playerPanel.setBorder(new TitledBorder("Player"));
        
        String[] hostInfo = InetAddress.getLocalHost().toString().split("/");
        playerPanel.add(new JLabel("Host name:"));
        playerPanel.add(newTxtField(hostInfo[0]));
        playerPanel.add(new JLabel("IP number:"));
        playerPanel.add(newTxtField(hostInfo[1]));
        playerPanel.add(new JLabel("Port number:"));
        playerPanel.add(newTxtField(Integer.toString(server.getLocalPort())));
        panel.add(playerPanel);
        
        //Display
        String[] options = new String[] {"Cancel"};
        frame.add(panel);
        frame.setSize(300, 400);
        
		//Peer sub-panel
		JPanel peerPanel = new JPanel(new GridLayout(3,2));
		peerPanel.setBorder(new TitledBorder("Peer"));
		
		//Bottom Panel
		JPanel botPanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();


		peerPanel.add(new JLabel("Host name/IP:"));
		peerHostName = newTxtField(null);
		peerHostName.setText("127.0.0.1"); 
		peerPanel.add(peerHostName);
		peerPanel.add(new JLabel("Port number:"));
		peerPortNumber = newTxtField(null);
		peerPortNumber.setText("8000"); 
		peerPanel.add(peerPortNumber);
		JButton connect = new JButton("Connect");
		JButton disconnect = new JButton("Disconnect");
		consolePanel = newTxtField(" ");
		JButton close = new JButton("Close");

		connect.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) { 
				new Thread(()-> {
					try {
						socket.connect(new InetSocketAddress(peerHostName.getText(), Integer.parseInt(peerPortNumber.getText())), 5000);
						pairAsClient(socket);
					} catch (Exception a) { }
				}).start();
			} 
		} );
		disconnect.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) { 
				network.close();
				try {
					socket.close();
				} catch (IOException e1) {

				}
			} 
		} );
		peerPanel.add(connect);
		peerPanel.add(disconnect);
		close.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) { 
				Window win = SwingUtilities.getWindowAncestor(panel);
				win.dispose();
			} 
		} );
		panel.add(peerPanel);
		panel.add(consolePanel);
		
		
		c.fill = GridBagConstraints.LAST_LINE_END;
		c.gridx = 5;
		c.gridy = 2;
		botPanel.add(close, c);
		panel.add(botPanel);
		//panel.add(close);
		
		return frame;
	}

	private JTextArea newTxtField(String str) {
		JTextArea txtField = new JTextArea(1,10);
		txtField.setBorder(BorderFactory.createLineBorder(Color.gray));
		txtField.setSize(1, 1);
		txtField.setText(str);
		if(str!=null)
			txtField.setEditable(false);
		return txtField;
	}
	
	private void pairAsClient(Socket socket) {
		network = new NetworkAdapter(socket);
		network.setMessageListener(this); // see the next slide
		network.writeJoin();
		network.receiveMessages(); // loop till disconnected
	}

	private void pairAsServer(Socket client) {
		network = new NetworkAdapter(client);
		network.setMessageListener(this); 
		int[] sudoku = {1,9,0,2,4,1,0,3,6,1,0,4,7,1,0,6,5,1,1,0,8,1,1,2,6,1,1,3,9,1,1,6,3,1,1,8,2,1,2,0,9,1,2,1,7,1,2,5,3,1,2,8,6,1,3,1,5,1,3,3,2,1,3,6,1,1,3,7,8,1,3,8,7,1,4,1,8,1,4,3,7,1,4,4,6,1,4,5,1,1,4,7,9,1,5,0,1,1,5,1,4,1,5,5,9,1,5,7,6,1,6,0,7,1,6,1,9,1,6,4,1,1,6,8,4,1,7,0,5,1,7,2,1,1,7,3,4,1,7,6,7,1,7,8,8,1,8,2,3,1,8,4,5,1,8,5,7,1,8,6,9,1};
		network.writeJoinAck(9, sudoku);
		network.receiveMessages();
	}

	private NetworkAdapter network;

	protected void fillNumber(int x, int y, int n) {
		board.insert(x,y,n);
		if (network != null) { network.writeFill(x, y, n); } 
	}	

	public void 
	
	/** Called when a message is received from the peer. */
	public void messageReceived(MessageType type, int x, int y, int z, int[] others) {
		System.out.println(type.toString());
		switch (type) {
		case FILL:
			// peer filled the square (x, y) with the number z
			board.insert(x, y, z);
			break;
		case JOIN_ACK:
			if(x==1) {
				board = new Board(x,y,others);
				boardPanel.setBoard(board);
			}
			boardPanel.repaint();
			break;
		case NEW:
			board = new Board(x,others);
			//boardPanel.setBoard(board);
			break;
		case JOIN:
		}
		boardPanel.repaint();
	}
}
