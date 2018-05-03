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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
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
	Socket client;
	Boolean panelOpen = false;
	JFrame networkFrame;
	
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
		if(networkFrame == null || !networkFrame.isVisible())
			try {
				if(socket == null)
					socket = new Socket();
				if(server == null)
					server = new ServerSocket(0);
				networkFrame = networkPanel(socket);
				networkFrame.setVisible(true);
				System.out.println(InetAddress.getLocalHost().getHostAddress());
				new Thread(() -> { 
					try {
						while(true) {
							client = server.accept();
							pairAsServer(client);
						}
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
		
		//Console
		consolePanel = new JTextArea(16, 58);
        consolePanel.setEditable(false); // set textArea non-editable
	    JScrollPane console = new JScrollPane(consolePanel);
	    console.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
	    //console.add(scroll);
	    
	    
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
					network.writeQuit();
					printToNetworkConsole(false,MessageType.QUIT);
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
		panel.add(console);


		c.fill = GridBagConstraints.LAST_LINE_END;
		c.gridx = 5;
		c.gridy = 2;
		botPanel.add(close, c);
		panel.add(botPanel);
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
		printToNetworkConsole(false,MessageType.JOIN);
		network.receiveMessages(); // loop till disconnected
	}

	private void pairAsServer(Socket client) {
		network = new NetworkAdapter(client);
		network.setMessageListener(this); 
		network.receiveMessages();
	}

	private NetworkAdapter network;

	protected void fillNumber(int x, int y, int n) {
		board.insert(x,y,n);
		if (network != null) { 
			network.writeFill(x, y, n); 
			printToNetworkConsole(false,MessageType.FILL,x,y,n);
		} 
	}

	/** Called when a message is received from the peer. */
	public void messageReceived(MessageType type, int x, int y, int z, int[] others) {
		System.out.println(type.toString());
		switch (type) {
		case FILL:
			printToNetworkConsole(true,type,x,y,z);
			// peer filled the square (x, y) with the number z
			board.insert(x, y, z);
			network.writeFillAck(x, y, z);
			printToNetworkConsole(false,MessageType.FILL_ACK,x,y,z);
			break;
		case JOIN_ACK:
			printToNetworkConsole(true,type,x,others);
			if(x==1) {
				board = new Board(x,y,others);
				boardPanel.setBoard(board);
			}
			boardPanel.repaint();
			break;
		case NEW:
			printToNetworkConsole(true,type,x,others);
			int response = JOptionPane.showOptionDialog(null, "Accept new game?", "New Game",
					JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE,
					null, null, null);

			if(response != 1) {
				board = new Board(1,x,others);
				boardPanel.setBoard(board);
				boardPanel.repaint();
			} else {
				network.writeQuit();
				printToNetworkConsole(false,MessageType.QUIT);
				try {
					socket.close();
				} catch (IOException e) {
				}
				try {
					client.close();
				} catch (IOException e) {
				}
			}
			break;
		case NEW_ACK:
			printToNetworkConsole(true,type,x);
			break;
		case FILL_ACK:
			printToNetworkConsole(true,type,x,y,z);
			break;
		case JOIN:
			printToNetworkConsole(true,type);
			response = JOptionPane.showOptionDialog(null, "Accept connection?", "New connection",
					JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE,
					null, null, null);

			if(response != 1) {
				network.writeJoinAck(board.size, board.getJoinArray());
				printToNetworkConsole(false,MessageType.JOIN_ACK,board.size, board.getJoinArray());
			} else {
				network.writeQuit();
				printToNetworkConsole(false,MessageType.QUIT);
				try {
					socket.close();
				} catch (IOException e) { }
			}
		case QUIT:
			printToNetworkConsole(true,type);
			try {
				socket.close();
			} catch (IOException e) { }
		}
		boardPanel.repaint();
	}

	protected void numberClicked(int number) {
		super.numberClicked(number);
		network.writeFill(board.getX(), board.getY(),number);
		printToNetworkConsole(false,MessageType.FILL,board.getX(),board.getY(),number);
	}

	protected void requestNewBoard() {
		super.requestNewBoard();
		network.writeNew(board.size, board.getJoinArray());
		printToNetworkConsole(false,MessageType.NEW,board.size, board.getJoinArray());
	}
	
    private void printToNetworkConsole(boolean IO,MessageType type) {
    	printToNetworkConsole(IO, type.toString());
    }
    
    private void printToNetworkConsole(boolean IO, MessageType type, int x) {
    	printToNetworkConsole(IO, type.toString()+":"+x);
    }
    
    private void printToNetworkConsole(boolean IO, MessageType type, int x, int[] others) {
    	printToNetworkConsole(IO, type.toString()+":"+ x + "," + arrayToString(others));
    }
    
    private void printToNetworkConsole(boolean IO, MessageType type, int x, int y, int v) {
    	printToNetworkConsole(IO, type.toString()+":"+ x + "," + y + "," + v);
    }
    
    private void printToNetworkConsole(boolean IO, MessageType type, int x, int y, int[] others) {
    	printToNetworkConsole(IO, type.toString()+":"+ x + "," + y + "," + arrayToString(others));
    }
    
    /**
     * 
     * @param IO true if input false if output
     * @param str
     */
	protected void printToNetworkConsole(boolean IO, String str) {
		String current = consolePanel.getText();
		current += "\n" + (IO?"<":">") + str;
		consolePanel.setText(current);
	}
	
	private String arrayToString(int[] arr) {
		String result = "";
		for(int i:arr)
			result += i + ",";
		return result;
	}
}
