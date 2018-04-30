package sudoku.hw5;

import java.awt.event.ActionEvent;
import java.net.InetSocketAddress;
import java.net.Socket;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;

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
		 new Thread(()-> {
			 try {
				 Socket socket = new Socket();
				 socket.connect(new InetSocketAddress("127.0.0.1", 8000), 5000); // timeout in millis
				 pairAsClient(socket);
			 } catch (Exception e1) { }
		 }).start();
	 }

	 private void pairAsClient(Socket socket) {
		 network = new NetworkAdapter(socket);
		 network.setMessageListener(this); // see the next slide
		 network.writeJoin();
		 network.receiveMessages(); // loop till disconnected
	 }

	 private void pairAsServerr(Socket socket) {
		 
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
			 board = new Board(y,others);
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
