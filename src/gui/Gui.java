package gui;

import org.joml.Matrix4f;
import org.joml.Vector4f;

import assets.Assets;
import gameCode.Camera;
import gameCode.Shader;
import gameCode.Window;

public class Gui {
	private Shader shader;
	private Camera camera;
	public Gui(Window window){
		shader = new Shader("gui");
		camera = new Camera(window.getWidth(), window.getHeight());
	}
	public void resizeCamera(Window window){
		camera.setProjection(window.getWidth(), window.getHeight());
	}
	public void Render(){
		Matrix4f mat = new Matrix4f();
		camera.getProjection().scale(87, mat);
		
		mat.translate(-3,-3,0);
		
		shader.bind();
		shader.setUniform("projection", mat);
		shader.setUniform("color", new Vector4f(0,0,0,0.4f));
		
		Assets.getModel().render();
	}
}
