package gameCode;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.opengl.*;

import assets.Assets;
import entity.Entity;
import gui.Gui;
import World.TileRenderer;
import World.World;

public class Main {

	public Main(){
		Window.setCallback();

		if(!glfwInit()){
			System.err.println("GLFW could not initialise");
			System.exit(1);
		}

		Window window = new Window();
		window.setSize(640, 480);
		//	window.setFullscreen(true);
		window.createWindow("Testing");



		GL.createCapabilities();


		glEnable(GL_BLEND);

		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

		Camera camera = new Camera(window.getWidth(), window.getHeight());

		glEnable(GL_TEXTURE_2D);

		TileRenderer tiles = new TileRenderer();

		Assets.initAsset();

		Shader shader = new Shader("shader");

		World world = new World("test_world", camera);
		world.calculateView(window);

		Gui gui = new Gui(window);

		glClearColor(0,0,0,0);

		double frame_cap = 1.0/60.0;

		double frame_time = 0;
		int frames = 0;

		double time = Timer.getTime();
		double unprocessed = 0;

		while(!window.shouldClose()){
			boolean can_render = false;
			double time_2 = Timer.getTime();
			double passed = time_2 - time;
			unprocessed+=passed;
			frame_time += passed;

			time = time_2;

			while(unprocessed >= frame_cap){
				if(window.hasResized()){
					camera.setProjection(window.getWidth(), window.getHeight());
					gui.resizeCamera(window);
					world.calculateView(window);
					glViewport(0,0,window.getWidth(), window.getHeight());
				}
				
				unprocessed -= frame_cap;
				can_render = true;
				if(window.getInput().isKeyPressed(GLFW_KEY_ESCAPE)){
					glfwSetWindowShouldClose(window.getWindow(), true);
				}

				world.update((float)frame_cap, window, camera);

				world.correctCamera(camera, window);
				window.update();
				if(frame_time >= 1.0){
					frame_time = 0;
					System.out.println("FPS: " + frames);
					frames = 0;
				}
			}
			if(can_render){
				glClear(GL_COLOR_BUFFER_BIT);
				//				tex.bind(0);
				//				shader.bind();
				//				shader.setUniform("sampler", 0);
				//				shader.setUniform("projection", camera.getProjection().mul(target));
				//				model.render();

				world.render(tiles, shader, camera);

				gui.Render();
				
				window.swapBuffers();
				frames++;
			}
		}
		Assets.deleteAsset();
		glfwTerminate();
	}
	public static void main(String[] args) {
		new Main();
	}

}
