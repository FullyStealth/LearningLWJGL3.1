package World;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import collision.AABB;
import entity.Entity;
import entity.Player;
import entity.Transform;
import gameCode.Camera;
import gameCode.Shader;
import gameCode.Texture;
import gameCode.Window;

public class World {
	private int viewX;
	private int viewY;
	private byte[] tiles;
	private AABB[] bounding_boxes;
	private List<Entity> entities;
	private int width;
	private int height;
	private int scale;
	
	private Matrix4f world;
	
	public World(String world, Camera camera){
		try {
			BufferedImage tile_sheet = ImageIO.read(Texture.prepareFile("./worlds/" + world + "/tiles.png"));
			BufferedImage entity_sheet = ImageIO.read(Texture.prepareFile("./worlds/" + world + "/entities.png"));
			
			width = tile_sheet.getHeight();
			height = tile_sheet.getWidth();
			scale = 16;
			
			this.world = new Matrix4f().setTranslation(new Vector3f(0));
			this.world.scale(scale);
			
			int[] colorTileSheet = tile_sheet.getRGB(0,0,width,height,null,0,width);
			int[] colorEntitySheet = entity_sheet.getRGB(0, 0, width, height, null, 0, width);
			
			tiles = new byte[width * height];
			bounding_boxes = new AABB[width * height];
			entities = new ArrayList<Entity>();
			
			Transform transform;
			
			for(int y = 0; y < height; y++){
				for(int x = 0; x < width; x++){
					int red = (colorTileSheet[x + y * width] >> 16) & 0xff;
					int entity_index = (colorEntitySheet[x + y * width] >> 16) & 0xFF;
					int entity_alpha = (colorEntitySheet[x + y * width] >> 24) & 0xFF;
					
					Tile t;
					try{
						t = Tile.tiles[red];
					}catch(ArrayIndexOutOfBoundsException e){
						t = null;
					}
					
					if(t != null){
						setTile(t, x, y);
					}
					if(entity_alpha > 0){
						transform = new Transform();
						transform.pos.x = x*2;
						transform.pos.y = -y*2;
						switch(entity_index){
						case 1:
							Player player = new Player(transform);
							entities.add(player);
							camera.getPosition().set(transform.pos.mul(-scale, new Vector3f()));
							break;
						default:
							break;
						}
					}
				}
			}
/*			entities.add(new Player(new Transform()));
			
			Transform t = new Transform();
	 		t.pos.x = 0;
	 		t.pos.y = -4;
			
			entities.add(new Entity(new Animation(5,15,"anim"), t){
				@Override
				public void update(float delta, Window window, Camera camera,
						World world) {
					Vector2f movement = new Vector2f();
					
					if(window.getInput().isKeyDown(GLFW_KEY_UP)){
						movement.add(0,10*delta);
					}
					if(window.getInput().isKeyDown(GLFW_KEY_LEFT)){
						movement.add(-10*delta,0);
					}
					if(window.getInput().isKeyDown(GLFW_KEY_DOWN)){
						movement.add(0,-10*delta);
					}
					if(window.getInput().isKeyDown(GLFW_KEY_RIGHT)){
						movement.add(10*delta,0);
					}
					move(movement);

				}
			}
			);
*/			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public World() {
		width = 64;
		height = 64;
		scale = 16;
		
		tiles = new byte[width * height];
		bounding_boxes = new AABB[width * height];
		
		world = new Matrix4f().setTranslation(new Vector3f(0));
		world.scale(scale);
	}
	public void calculateView(Window window){
		viewX = (window.getWidth()/(scale * 2)) + 4;
		viewY = (window.getHeight()/(scale * 2)) + 4;
	}
	public Matrix4f getWorldMatrix(){
		return world;
	}
	
	public void render(TileRenderer render, Shader shader, Camera camera){
		int posX = (int)camera.getPosition().x / (scale * 2);
		int posY = (int)camera.getPosition().y / (scale * 2);
		
		for(int i = 0; i < viewX; i++){
			for(int j = 0; j < viewY; j++){
				Tile t = getTile(i-posX-(viewX/2) + 1, j+posY-(viewY/2));
				if(t != null){
					render.renderTile(t, i-posX-(viewX/2) + 1, -j-posY+(viewY/2), shader, world, camera);
				}
			}
		}
		for(Entity entities : entities){
			entities.render(shader, camera, this);
		}
	}
	public void update(float delta, Window window, Camera camera){
		for(Entity entities : entities){
			entities.update(delta, window, camera, this);
		}
		for(int i = 0; i < entities.size(); i++){
			for(int j = i + 1; j < entities.size(); j++){
				entities.get(i).collideWithEntity(entities.get(j));
			}
			entities.get(i).collideWithTiles(this);
		}
	}
	public void correctCamera(Camera camera, Window window){
		Vector3f pos = camera.getPosition();
		
		int w = -width * scale * 2;
		int h = height * scale * 2;
		
		if(pos.x >  (-window.getWidth()/2)+ scale){
			pos.x = -(window.getWidth()/2) + scale;
		}
		if(pos.x < w + (window.getWidth()/2) + scale){
			pos.x = w + (window.getWidth()/2) + scale;
		}
		if(pos.y < (window.getHeight()/2) - scale){
			pos.y = (window.getHeight()/2) - scale;
		}
		if(pos.y > h - (window.getHeight()/2) - scale){
			pos.y = h - (window.getHeight()/2) - scale;
		}
	}
	public void setTile(Tile tile, int x, int y){
		tiles[x + y * width] = tile.getId();
		if(tile.isSolid()){
			bounding_boxes[x + y * width] = new AABB(new Vector2f(x*2, -y*2), new Vector2f(1,1));
		}else{
			bounding_boxes[x + y * width] = null;
		}
	}
	public Tile getTile(int x, int y){
		try{
			return Tile.tiles[tiles[x + y * width]];
		}catch(ArrayIndexOutOfBoundsException e){
			return null;
		}
	}
	public AABB getTileBoundingBox(int x, int y){
		try{
			return bounding_boxes[x + y * width];
		}catch(ArrayIndexOutOfBoundsException e){
			return null;
		}
	}
	public int getScale(){
		return scale;
	}

}
