package com.example.server;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class ServerUI extends JFrame{
	
	private JButton startButton, stopButton, displayButton, displayAllButton;
	JTextField plateNoField, bodyNoField; 
	
	public ServerUI() {
		createServerGUI();
	}
	
	private void createServerGUI() {
		this.setName("ServerFrame");
		this.setTitle("Taxi Server");
		this.setLocationRelativeTo(null);
		this.setLayout(new BorderLayout());
		/*this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				int choice = JOptionPane.showConfirmDialog(ServerUI.this, "Do you want to exit Taxi Server?", "Exit Taxi Server", JOptionPane.YES_NO_OPTION);
				if(choice == JOptionPane.YES_OPTION)
					System.exit(0);
			}
		});*/
		
		JLabel plateNoLabel = new JLabel("Plate No:");
		plateNoField = new JTextField();
		plateNoField.setPreferredSize(new Dimension(100, 20));
		
		JLabel bodyNoLabel = new JLabel("Body No:");
		bodyNoField = new JTextField();
		bodyNoField.setPreferredSize(new Dimension(100, 20));
		
		JPanel mapPanel = new JPanel();
		mapPanel.add(plateNoLabel);
		mapPanel.add(plateNoField);
		mapPanel.add(bodyNoLabel);
		mapPanel.add(bodyNoField);
		
		startButton = new JButton("START");
		startButton.addActionListener(new ButtonListener());
		
		stopButton = new JButton("STOP");
		stopButton.addActionListener(new ButtonListener());
		
		displayButton = new JButton("DISPLAY TAXI");
		displayButton.addActionListener(new ButtonListener());
		
		displayAllButton = new JButton("DISPLAY ALL TAXI");
		displayAllButton.addActionListener(new ButtonListener());
		
		JButton locateButton = new JButton("LOCATE TAXI");
		JButton exitButton = new JButton("EXIT");
		
		JPanel ctrlPanel = new JPanel(new GridLayout(2, 3, 2, 2));
		ctrlPanel.add(startButton);
		ctrlPanel.add(stopButton);
		ctrlPanel.add(displayButton);
		ctrlPanel.add(displayAllButton);
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
				Constants.database = new Database();
				new Thread(new Server()).start();
			}
			
			else if(e.getSource() == stopButton)
			{
				if(Constants.isThreadRunning)
				{
					Constants.isThreadRunning = false;
					try {
						Constants.database.clearTaxiData();
						Server.serverSocket.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
				}
			}
			
			else if(e.getSource() == displayButton)
			{
				String plateNo = plateNoField.getText(), bodyNo = bodyNoField.getText();
				
				if(!plateNo.equals("") || !bodyNo.equals(""))
					try {
						Constants.database.displayTaxi(plateNo, bodyNo);
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
			}
			
			else if(e.getSource() == displayAllButton)
			{
				if(Constants.database != null)
				{
					try {
						Constants.database.displayAllTaxi();
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
				}
			}
		}
	}
}