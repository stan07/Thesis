package com.example.server;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class ServerUI extends JFrame{
	
	private Server server;
	private JButton startButton, stopButton;
	
	public ServerUI() {
		createServerGUI();
	}
	
	private void createServerGUI() {
		this.setName("ServerFrame");
		this.setTitle("Taxi Server");
		this.setLocationRelativeTo(null);
		this.setLayout(new BorderLayout());
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				int choice = JOptionPane.showConfirmDialog(ServerUI.this, "Do you want to exit Taxi Server?", "Exit Taxi Server", JOptionPane.YES_NO_OPTION);
				if(choice == JOptionPane.YES_OPTION)
					System.exit(0);
			}
		});
		
		JPanel mapPanel = new JPanel(new BorderLayout());
		
		startButton = new JButton("START");
		startButton.addActionListener(new ButtonListener());
		
		stopButton = new JButton("STOP");
		stopButton.addActionListener(new ButtonListener());
		
		JButton optionsButton = new JButton("OPTIONS");
		JButton taxiButton = new JButton("FIND TAXI");
		JButton locateButton = new JButton("LOCATE TAXI");
		JButton exitButton = new JButton("EXIT");
		
		JPanel ctrlPanel = new JPanel(new GridLayout(2, 3, 2, 2));
		ctrlPanel.add(startButton);
		ctrlPanel.add(stopButton);
		ctrlPanel.add(optionsButton);
		ctrlPanel.add(taxiButton);
		ctrlPanel.add(locateButton);
		ctrlPanel.add(exitButton);
		
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(mapPanel, BorderLayout.NORTH);
		mainPanel.add(ctrlPanel, BorderLayout.SOUTH);
		
		this.add(mainPanel);
		this.setSize(1280, 800);
		this.setVisible(true);
	}
	
	public static void main(String[] args){
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				new ServerUI();
			}
		});
	}
	
	
	private class ButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == startButton)
			{
				Constants.isThreadRunning = true;
				server = new Server();
				new Thread(server).start();
			}
			
			else if(e.getSource() == stopButton)
			{
				if(Constants.isThreadRunning)
				{
					Constants.isThreadRunning = false;
					try {
						server.serverSocket.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		}
	}
}