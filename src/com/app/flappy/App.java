package com.app.flappy;
import java.awt.Dimension;

import javax.swing.JFrame;

public class App {
	public static void main(String[] args) {
		int boardHeight=640;
		int boardWidth=360;
		
		JFrame frame=new JFrame("Flappy Birds");
		frame.setSize(new Dimension(boardWidth,boardHeight));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		
		FlappyBird jPanel=new FlappyBird();
		frame.add(jPanel);
		frame.pack();
		frame.requestFocus();
		frame.setVisible(true);
		
	}
}
