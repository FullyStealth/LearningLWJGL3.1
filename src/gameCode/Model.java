package gameCode;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;

public class Model {
	private int draw_count;
	private int v_id;
	private int t_id;
	private int i_id;
	public Model(float[] vertices, float[] tex_coords, int[] indeces) {
		draw_count = indeces.length;

		v_id = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, v_id);
		glBufferData(GL_ARRAY_BUFFER, createFloatBuffer(vertices), GL_STATIC_DRAW);
		
		t_id = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, t_id);
		glBufferData(GL_ARRAY_BUFFER, createFloatBuffer(tex_coords), GL_STATIC_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		
		i_id = glGenBuffers();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, i_id);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, createIntBuffer(indeces), GL_STATIC_DRAW);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
		
	}
	protected void finalize(){
		glDeleteBuffers(v_id);
		glDeleteBuffers(t_id);
		glDeleteBuffers(i_id);
	}
	public void render(){
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		
		glBindBuffer(GL_ARRAY_BUFFER, v_id);
		glVertexAttribPointer(0, 3, GL_FLOAT, false ,0,0);
		
		glBindBuffer(GL_ARRAY_BUFFER, t_id);
		glVertexAttribPointer(1, 2, GL_FLOAT, false ,0,0);
		
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, i_id);
		glDrawElements(GL_TRIANGLES, draw_count, GL_UNSIGNED_INT, 0);
		
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
	}
	private FloatBuffer createFloatBuffer(float[] data){
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}
	private IntBuffer createIntBuffer(int[] data){
		IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}
}
