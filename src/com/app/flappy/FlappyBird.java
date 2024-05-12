package com.app.flappy;

import java.awt.Color;
import java.util.Random;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

/*Major Concepts of the game are:
 * -> The bird will have velocity only in the y direction 
 * -> The pipes are moving towards the birds
 * -> There will be gravity that will be acting on the bird in the downward direction
 * -> The intial velocity of the bird will be linearly decreased by gravity during each
 * frame and will eventually the upward velocity will become zero and the bird starts 
 * falling down*
 * -> To prevent the bird from falling down the user should press a key which will
 * update the velocityY of the bird to -9
 * ->We need to add the pipes. We create a seprate thread for the placing of the pipes
 * The properties of the pipes are defined in a Pipe Class
 * -> The pipes which are to be displayed needs some variation in their heights this is 
 * done using the Random class method
 * -> Manage the Game Over condition. Game Over is true when the bird falls on the ground
 * */

public class FlappyBird extends JPanel implements ActionListener,KeyListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int boardWidth=360;
	int boardHeight=640;
	Image backgroundImage;
	Image topPipe;
	Image bottomPipe;
	Image birdImage;
	
	//Bird
	int birdX=boardWidth/8;
	int birdY=boardHeight/2;
	int birdWidth=34;
	int birdHeight=24;
	
	//A Class to represent the bird
	class Bird{
		int x=birdX;
		int width=birdWidth;
		int height=birdHeight;
		int y=birdY;
		Image img;
		Bird(Image img){
			this.img=img;
		}
	}
	
	Bird bird;
	
	//Pipes
	int pipeX=boardWidth;
	int pipeY=0;
	int pipeWidth=64;
	int pipeHeight=512;
	
	class Pipe{
		int x=pipeX;
		int y=pipeY;
		int width=pipeWidth;
		int height=pipeHeight;
		boolean passed=false;
		Image img;
		Pipe(Image img){
			this.img=img;
		}
	}
	
	ArrayList<Pipe>pipes;
	Random random=new Random();
	//GameLoop
	Timer gameLoop;	
	Timer placePipesTimer;
	
	//Velocity
	int velocityY=-9;
	int velocityX=-4;//This is the velcoity with which the pipes comes towards the bird
	int gravity=1;	 //this will create the illusion of the bird moving forward.
	
	boolean gameOver=false;
	boolean pause=false;
	double score=0;
	
	FlappyBird(){
		this.setPreferredSize(new Dimension(boardWidth,boardHeight));
		this.setBackground(Color.BLUE);
		
		//add the keyListener
		this.setFocusable(true); // This will tell the program that it is the jpanel class that listens to the keyevents
		this.addKeyListener(this);
		
		backgroundImage=new ImageIcon(getClass().getResource("/images/flappybirdbg.png")).getImage();
		try{
			birdImage=new ImageIcon(getClass().getResource("/images/flappybird.png")).getImage();
		}catch(NullPointerException e) {
			System.out.println("The image is null");
		}
		topPipe=new ImageIcon(getClass().getResource("/images/toppipe.png")).getImage();
		bottomPipe=new ImageIcon(getClass().getResource("/images/bottompipe.png")).getImage();
		
		//Create an instance of the bird and passes the images as an arg
		bird=new Bird(birdImage);
		
		gameLoop = new Timer(1000/60,this);
		gameLoop.start();
		
		//ArrayList for placing the pipes
		pipes=new ArrayList<Pipe>();
		
		//New Thread to place the pipes in the background
		placePipesTimer=new Timer(1500,new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				placePipes();	
			}
			
		});
		placePipesTimer.start();
	}
	
	private void placePipes() {
		int openingSpace=pipeHeight/4;
		Pipe tPipe=new Pipe(topPipe);
		tPipe.y=(int) (tPipe.y-pipeHeight/4-Math.random()*(pipeHeight/2));
		pipes.add(tPipe);
		Pipe bPipe=new Pipe(bottomPipe);
		bPipe.y=tPipe.y+pipeHeight+openingSpace;
		pipes.add(bPipe);
	}
	
	//Game loop
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		//draw the bg
		g.drawImage(backgroundImage, 0, 0, boardWidth, boardHeight, null);
		
		//draw the bird
		g.drawImage(bird.img, bird.x, bird.y, bird.height, bird.width, null);
		
		//draw the pipes
		 for(Pipe p:pipes) {
			g.drawImage(p.img, p.x, p.y, p.width, p.height, null);
		}
		 
		//Show the status
		 g.setColor(Color.WHITE);
		 g.setFont(new Font("Arial",Font.PLAIN,32));
		 if(gameOver) {
			 g.drawString("GameOver "+String.valueOf((int)score), 10, 35);			 
		 }else {
			 g.drawString("Score: "+String.valueOf((int)score), 10, 35);
		 }
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		//System.out.println("draw");
		if(!gameOver) {
			move();
			repaint();			
		}else {
			gameLoop.stop();
			placePipesTimer.stop();
		}
		
		
	}

	//This method is used to update the velcoity of the bird(movement)
	private void move() {
		//Due to gravity birds velocity decreases
		velocityY+=gravity;	
		bird.y+=velocityY;
		bird.y=Math.max(bird.y, 0);
		if(bird.y<=0 || bird.y>=boardHeight) gameOver=true;
		for(Pipe p:pipes) {
			p.x+=velocityX;
			if(!p.passed && bird.x>p.x+p.width) {
				p.passed=true;
				score+=0.5;
			}
			if(collision(bird,p))gameOver=true;
		}
	}


	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode()==KeyEvent.VK_SPACE) {
			velocityY=-9;
			if(gameOver) {
				bird.x=birdX;
				bird.y=birdY;
				pipes.clear();
				score=0;
				gameOver=false;
				gameLoop.start();
				placePipesTimer.start();
			}
		}
//		if(e.getKeyCode()==KeyEvent.VK_ENTER) {
//			pause=(pause==false) ? true : false; 
//			System.out.println(pause);
//			System.out.println(gameOver + "1");
//		}
		
	}

	@Override
	public void keyTyped(KeyEvent e) {}
	@Override
	public void keyReleased(KeyEvent e) {}

	//Checking Collision
	private boolean collision(Bird bird,Pipe pipe) {
		/*Check if the bird is between the 2 ends of the upper pipe and lower pipe*/
		return bird.y+bird.width>pipe.y &&
				bird.y<pipe.y+pipe.height &&
				bird.x<pipe.x+pipe.width &&
				bird.x+bird.width>pipe.x;
	}
}
