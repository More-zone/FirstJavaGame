package com.tutorial.main;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;


public class Game extends Canvas implements Runnable {

	public static final int WIDTH = 1280, HEIGHT = WIDTH / 12 * 9;
    
    private Thread thread;
    private boolean running = false;
    
    private Handler handler;
    private HUD hud;
    private Spawn spawner;
    private Menu menu;
    
    public enum STATE {
    	Menu,
    	Game;
    }
    
    public STATE gameState = STATE.Menu;
    
    public Game(){
    	handler = new Handler();
        menu = new Menu(this, handler);
    	this.addKeyListener(new KeyInput(handler));
    	this.addMouseListener(menu);
    	
    	new Window(WIDTH, HEIGHT, "LET'S BUILD A GAME", this);
    	
    	hud = new HUD();
    	spawner = new Spawn(handler, hud);
        
        //if(gameState == STATE.Game){
        //}
    }
    
    public synchronized void start(){
        thread = new Thread(this);
        thread.start();
        running = true;
    }
    
    public synchronized void stop(){
        try{
            thread.join();
            running = false;
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
	public void run() {
		this.requestFocus();
		// Game Loop
        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;
        long timer = System.currentTimeMillis();
        int frames = 0;
        while(running){
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            while(delta >= 1){
                tick();
                delta--;
            }
            if(running)
                render();
            frames++;
            
            if(System.currentTimeMillis() - timer > 1000){
                timer += 1000;
                System.out.println("FPS: " + frames);
                frames = 0;
            }
        }
        stop();
	}

	private void tick(){
        handler.tick();
        if(gameState == STATE.Game) {
        	hud.tick();
        	spawner.tick();
        }else if(gameState == STATE.Menu) {
        	menu.tick();
        }
    }
    
    private void render(){
        BufferStrategy bs = this.getBufferStrategy();
        if (bs == null){
            // not recommended to go over 3 buffers
            this.createBufferStrategy(3);
            return;
        }
        
        Graphics g = bs.getDrawGraphics();
        
        
        g.setColor(Color.black);
        g.fillRect(0, 0, WIDTH, HEIGHT);
        
        
        handler.render(g);
        
        if(gameState == STATE.Game){
        	hud.render(g);
        }else if(gameState == STATE.Menu) {
        	menu.render(g);
        }
        
        
        g.dispose();
        bs.show();
    }
    
    public static int clampInt(int var, int min, int max) {
    	if(var >= max)
    		return var = max;
    	else if(var <= min)
    		return var = min;
    	else
    		return var;
    }
    
    public static float clamp(float var, float min, float max) {
    	if(var >= max)
    		return var = max;
    	else if(var <= min)
    		return var = min;
    	else
    		return var;
    }
    
    public static void main(String args[]){
        new Game();
    }
}
