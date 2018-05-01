package sudoku.hw5;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.InetSocketAddress;
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
import javax.swing.border.TitledBorder;

import sudoku.dialog.SudokuDialog;
import sudoku.hw5.NetworkAdapter.MessageListener;
import sudoku.hw5.NetworkAdapter.MessageType;
import sudoku.model.Board;

public class HW5Main extends SudokuDialog implements MessageListener{


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
				Socket socket = new Socket();
				//socket.connect(new InetSocketAddress("127.0.0.1", 8000), 5000); // timeout in millis
				
				try {
					JFrame networkPanel = networkPanel(socket);
				} catch (UnknownHostException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	}
	
	private JFrame networkPanel(Socket socket) throws UnknownHostException {
        JFrame frame = new JFrame("Network");

        JPanel panel = new JPanel(new GridLayout(4,1));
        panel.setPreferredSize(new Dimension(300, 400));

        //Player sub-panel
        final JPanel playerPanel = new JPanel(new GridLayout(3,2));
        playerPanel.setBorder(new TitledBorder("Player"));

        String[] hostInfo = InetAddress.getLocalHost().toString().split("/");
        playerPanel.add(new JLabel("Host name:"));
        playerPanel.add(newTxtField(hostInfo[0]));
        playerPanel.add(new JLabel("IP number:"));
        playerPanel.add(newTxtField(hostInfo[1]));
        playerPanel.add(new JLabel("Port number:"));
        playerPanel.add(newTxtField(Integer.toString(socket.getLocalPort())));
        panel.add(playerPanel);
        
        //Peer sub-panel
        JPanel peerPanel = new JPanel(new GridLayout(3,2));
        peerPanel.setBorder(new TitledBorder("Peer"));
        

        peerPanel.add(new JLabel("Host name/IP:"));
        JTextArea peerHostName = newTxtField(null);
        peerHostName.setText("127.0.0.1"); 
        peerPanel.add(peerHostName);
        peerPanel.add(new JLabel("Port number:"));
        JTextArea peerPortNumber = newTxtField(null);
        peerPortNumber.setText("8000"); 
        peerPanel.add(peerPortNumber);
        JButton connect = new JButton("Connect");
        JButton disconnect = new JButton("Disconnect");
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
        			  
        			  } 
        			} );
        peerPanel.add(connect);
        peerPanel.add(disconnect);
        panel.add(peerPanel);
        
        //Display
        String[] options = new String[] {"Cancel"};
        frame.add(panel);
        frame.setSize(300, 400);
		frame.setVisible(true);
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

	private void pairAsServer(Socket socket) {
		   network = new NetworkAdapter(socket);
		   network.setMessageListener(this); 
		   network.receiveMessages();
	}

	private NetworkAdapter network;

	protected void fillNumber(int x, int y, int n) {
		board.insert(x,y,n);
		if (network != null) { network.writeFill(x, y, n); } 
	}	

	/** Called when a message is received from the peer. */
	public void messageReceived(MessageType type, int x, int y, int z, int[] others) {
		System.out.println(type.toString());
		switch (type) {
		case FILL:
			// peer filled the square (x, y) with the number z
			board.insert(x, y, z);
			break;
		case JOIN_ACK:
			//board = new Board(y,others);
			//boardPanel.setBoard(board);
			break;
		case NEW:
			board = new Board(x,others);
			//boardPanel.setBoard(board);
			break;
		}
		boardPanel.repaint();
	}
}
