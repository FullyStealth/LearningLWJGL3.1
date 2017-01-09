package gameCode;

import static org.lwjgl.glfw.GLFW.*;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;

public class Window {
	private long window;
	
	private int WIDTH, HEIGHT;
	private boolean fullscreen;
	
	private Input input;
	
	public static void setCallback(){
		glfwSetErrorCallback(GLFWErrorCallback.createPrint(System.err));
	}
	public Window() {
		setSize(640, 480);
		setFullscreen(false);
	}
	public void createWindow(String title){
		window = glfwCreateWindow(
				WIDTH,
				HEIGHT,
				title,
				fullscreen ? glfwGetPrimaryMonitor() : 0,
				0);
		if(window == 0){
			throw new IllegalStateException("Failed to create window");
		}
		if(!fullscreen){
			GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
			
			glfwSetWindowPos(
				window,
				(vidmode.width() - WIDTH)/2,
				(vidmode.height() - HEIGHT)/2
			);	
			glfwShowWindow(window);
		}
		glfwMakeContextCurrent(window);
		
		input = new Input(window);
	}
	public boolean shouldClose(){
		return glfwWindowShouldClose(window);
	}
	public void setSize(int width, int height){
		WIDTH = width;
		HEIGHT = height;
	}
	public void setFullscreen(boolean fullscreen){
		this.fullscreen = fullscreen;
	}
	public void update(){
		input.update();
		glfwPollEvents();
	}
	public int getWidth(){
		return WIDTH;
	}
	public int getHeight(){
		return HEIGHT;
	}
	public boolean isFullscreen(){
		return fullscreen;
	}
	public Input getInput(){
		return input;
	}
	public long getWindow(){
		return window;
	}
	public void swapBuffers(){
		glfwSwapBuffers(window);
	}
}
