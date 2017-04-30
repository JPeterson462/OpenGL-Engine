package test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import engine.Assets;
import engine.Engine;
import engine.Entity;
import engine.FirstPersonCamera;
import engine.RawImage;
import engine.Settings;
import engine.audio.Music;
import engine.audio.SoundEffect;
import engine.gui.GUI;
import engine.gui.Image;
import engine.input.FirstPersonCameraController;
import engine.input.Key;
import engine.input.KeyboardListener;
import engine.input.Modifiers;
import engine.models.Model;
import engine.models.ModelImporter;
import engine.particles.BasicParticleEmitter;
import engine.physics.Body;
import engine.physics.CubeCollisionBounds;
import engine.physics.Universe;
import engine.rendering.GUIRenderer;
import engine.rendering.Geometry;
import engine.rendering.Light;
import engine.rendering.Material;
import engine.rendering.ParticleRenderer;
import engine.rendering.SceneRenderer;
import engine.rendering.Shader;
import engine.rendering.SkyboxRenderer;
import engine.rendering.TerrainRenderer;
import engine.rendering.TextRenderer;
import engine.rendering.VertexTemplate;
import engine.rendering.WaterRenderer;
import engine.terrain.HeightmapTerrainGenerator;
import engine.terrain.ProceduralTerrainGenerator;
import engine.terrain.Terrain;
import engine.terrain.TerrainGenerator;
import engine.terrain.TerrainTile;
import engine.terrain.TerrainTexturePack;
import engine.text.Font;
import engine.text.FontImporter;
import engine.text.TextBuffer;
import engine.text.TextEffect;
import engine.water.Water;
import engine.water.WaterTile;
import utils.Screenshot;
import utils.StringUtils;

public class TestApplication {

	private static Shader shader;

	private static Entity entity;

	private static SceneRenderer sceneRenderer;

	private static GUIRenderer guiRenderer;

	private static TextRenderer textRenderer;

	private static ParticleRenderer particleRenderer;

	private static Model fern;

	private static Material fernMaterial;

	private static Geometry fernGeometry;

	private static FirstPersonCameraController controller;

	private static Universe universe;

	private static Font font;

	private static Terrain terrain;

	private static RawImage heightmap;
	
	private static Music music;
	
	private static SoundEffect effect;

	private static TerrainGenerator newGenerator(int x, int z) {
//		return new ProceduralTerrainGenerator(0, 35, 15, x, z);
		return new HeightmapTerrainGenerator(heightmap, 50, 0, x, z);
	}

	public static void main(String[] args) throws FileNotFoundException {
		Settings settings = new Settings(new FileInputStream("settings.cfg"));
		settings.write(new FileOutputStream("settings.cfg"));
		FirstPersonCamera camera = new FirstPersonCamera(settings.fov, settings.aspectRatio, settings.nearPlane, settings.farPlane, new Vector3f(10, 10, -10), new Vector3f(0, 5, 0));
		Engine engine = new Engine(settings);
		ModelImporter.flags.setBit(0, true);
		ModelImporter.flags.setBit(1, false);
		engine.setInit((e) -> {
			try {
				heightmap = new RawImage(engine.getResource("textures/heightmap.png"));

				Assets.attachTo(e);
				font = FontImporter.loadFont("Consolas.fnt", e);
				TextBuffer buf0 = new TextBuffer("Hello World", new Vector2f(50, 50), new Vector2f(200, 100), new Vector4f(0, 0, 0, 1), 72, font, e, 12);
				textRenderer = new TextRenderer(Assets.newShader("fragmentText.glsl", "vertexText.glsl", VertexTemplate.POSITION_TEXCOORD_COLOR), e.getSettings().width, e.getSettings().height);
				textRenderer.addText(buf0);

				particleRenderer = new ParticleRenderer(Assets.newInstancedShader("fragmentParticle.glsl", "vertexParticle.glsl", 
						new int[] { 0, 1, 5, 6 }, new String[] { "in_Position", "modelViewMatrix", "textureAtlasOffset", "blendFactor" }), camera, e);
				particleRenderer.addEmitter(new BasicParticleEmitter(e.getRenderingBackend().createTexture(e.getResource("textures/cosmic.png"), false, true), 
						new Vector3f(30, 10, 30), 0.002f, 25, 0.3f, 3, 4, 0.1f, 0.4f, 0.8f, 60f, new int[] { 4, 4 }));

				fern = Assets.newModel("fern.obj", false);
				fernMaterial = Assets.newMaterial("fern.png");
				fernGeometry = e.getRenderingBackend().createGeometry(fern.getVertices(), fern.getIndices());
				entity = new Entity("models/tree.obj", "textures/tree.png", e, 1, 1, 0, 0);
				entity.setScale(3);
				shader = Assets.newShader("fragmentDefault.glsl", "vertexDefault.glsl", VertexTemplate.POSITION_TEXCOORD_NORMAL);
				Shader normalMappedShader = Assets.newShader("fragmentNormals.glsl", "vertexNormals.glsl", VertexTemplate.POSITION_TEXCOORD_NORMAL_TANGENT);

				TerrainTexturePack pack = Assets.newTerrainTexturePack("grass.png", "dirt.png", "pinkFlowers.png", "path.png", "blendMap.png");
				Shader terrainShader = Assets.newShader("fragmentTerrain.glsl", "vertexTerrain.glsl", VertexTemplate.POSITION_TEXCOORD_NORMAL);

				TerrainTile[][] tiles = new TerrainTile[3][3];
				tiles[0][0] = new TerrainTile(newGenerator(0, 0), e, pack);
				tiles[1][0] = new TerrainTile(newGenerator(1, 0), e, pack);
				tiles[2][0] = new TerrainTile(newGenerator(2, 0), e, pack);
				tiles[0][1] = new TerrainTile(newGenerator(0, 1), e, pack);
				tiles[1][1] = new TerrainTile(newGenerator(1, 1), e, pack);
				tiles[2][1] = new TerrainTile(newGenerator(2, 1), e, pack);
				tiles[0][2] = new TerrainTile(newGenerator(0, 2), e, pack);
				tiles[1][2] = new TerrainTile(newGenerator(1, 2), e, pack);
				tiles[2][2] = new TerrainTile(newGenerator(2, 2), e, pack);
//				TerrainTile[][] tiles = new TerrainTile[2][2];
//				tiles[0][0] = new TerrainTile(newGenerator(0, 0), e, pack);
//				tiles[0][1] = new TerrainTile(newGenerator(0, 1), e, pack);
//				tiles[1][0] = new TerrainTile(newGenerator(1, 0), e, pack);
//				tiles[1][1] = new TerrainTile(newGenerator(1, 1), e, pack);
				terrain = new Terrain(tiles);

				GUI gui = new GUI();
				Image i0 = new Image("id0", "google_logo.png");
				i0.getPosition().set(100, 100);
				i0.getSize().set(200, 70);
				gui.getWidgets().add(i0);
				guiRenderer = new GUIRenderer(gui, Assets.newShader("fragmentGui.glsl", "vertexGui.glsl", VertexTemplate.POSITION_TEXCOORD), 1280, 720);

				SkyboxRenderer skybox = new SkyboxRenderer(Assets.newShader("fragmentSkybox.glsl", "vertexSkybox.glsl", VertexTemplate.POSITION), e);

				ArrayList<WaterTile> waterTiles = new ArrayList<>();
				waterTiles.add(new WaterTile(80, 22, 80));
				Water water = new Water(waterTiles);
				WaterRenderer waterRenderer = new WaterRenderer(Assets.newShader("fragmentWater.glsl", "vertexWater.glsl", VertexTemplate.POSITION), e, water);
				
				sceneRenderer = new SceneRenderer(shader, normalMappedShader, camera, new TerrainRenderer(terrainShader, terrain), 
						engine.getSettings().backgroundColor, skybox, waterRenderer);
				sceneRenderer.addLight(new Light(new Vector3f(0, 10000, -7000), new Vector3f(0.4f, 0.4f, 0.4f)));
				sceneRenderer.addLight(new Light(new Vector3f(185, 10, -293), new Vector3f(2, 0, 0), new Vector3f(1, 0.01f, 0.002f)));
				sceneRenderer.addLight(new Light(new Vector3f(370, 17, -300), new Vector3f(0, 2, 2), new Vector3f(1, 0.01f, 0.002f)));
				//				sceneRenderer.addLight(new Light(new Vector3f(293, 7, -305), new Vector3f(2, 2, 0), new Vector3f(1, 0.01f, 0.002f)));
				sceneRenderer.addLight(new Light(new Vector3f(10, 10, 10), new Vector3f(2, 2, 0), new Vector3f(1, 0.01f, 0.002f)));
				sceneRenderer.addEntity(entity);

				Model lamp = Assets.newModel("lamp.obj", false);
				Material lampMaterial = Assets.newMaterial("lamp.png");
				Geometry lampGeometry = e.getRenderingBackend().createGeometry(lamp.getVertices(), lamp.getIndices());
				Entity lamp1 = new Entity(lamp, lampMaterial, lampGeometry, 1, 1, 0, 0);
				lamp1.setPosition(new Vector3f(185, terrain.getHeightAt(185, -293), -293));
				Entity lamp2 = new Entity(lamp, lampMaterial, lampGeometry, 1, 1, 0, 0);
				lamp2.setPosition(new Vector3f(370, terrain.getHeightAt(370, -300), -300));
				Entity lamp3 = new Entity(lamp, lampMaterial, lampGeometry, 1, 1, 0, 0);
				lamp3.setPosition(new Vector3f(293, terrain.getHeightAt(293, -305), -305));

				Model crate = Assets.newModel("crate.obj", true);
				Material crateMaterial = Assets.newMaterial("crate.png", "crateNormal.png");
				crateMaterial.setReflectivity(0.5f);
				crateMaterial.setShineDamper(10);
				Geometry crateGeometry = e.getRenderingBackend().createGeometry(crate.getVertices(), crate.getIndices());

				sceneRenderer.addEntity(lamp1);
				sceneRenderer.addEntity(lamp2);
				sceneRenderer.addEntity(lamp3);
				Entity crateEntity = new Entity(crate, crateMaterial, crateGeometry, 1, 1, 0, 0);
				crateEntity.setScale(0.1f);
				sceneRenderer.addEntity(crateEntity);

				Random random = new Random();
				for (int i = 0; i < 600; i++) {
					Entity entity = new Entity(fern, fernMaterial, fernGeometry, 2, 2, nextInt(random), nextInt(random));					
					entity.setPosition(new Vector3f(nextCoordinate(random), 0, nextCoordinate(random)));
					entity.getPosition().y = terrain.getHeightAt(entity.getPosition().x, entity.getPosition().z);
					entity.setScale(0.6f);
					entity.setTransparency(true);
					entity.getMaterial().setReflectivity(Material.NO_REFLECTIVITY);
					sceneRenderer.addEntity(entity);
				}

				e.getKeyboard().addListener(new KeyboardListener() {
					@Override
					public void onKeyTyped(Key key, Modifiers modifiers) {
						if (key.equals(Key.F2)) {
							System.out.println("Screenshot!");
							try {
								FileOutputStream screenshot = new FileOutputStream("Screenshot " + StringUtils.getDate() + ".png");
								Screenshot.takeScreenshot(0, 0, settings.width, settings.height, screenshot, Screenshot.FORMAT_PNG);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}

					@Override
					public void onLetterTyped(char letter, Modifiers modifiers) {

					}
				});

				//				universe = new Universe(new CollisionListener() {
				//					@Override
				//					public void onCollision(Body b1, Body b2) { }
				//
				//					@Override
				//					public void onTerrainCollision(Body b1) { }
				//				}, new Vector3f(0, -2000, 0), terrain);
				Body body = new Body(new CubeCollisionBounds(new Vector3f(5, 5, 5)), new Vector3f(0, 0, 0), new Quaternionf(), 1);
				//				universe.addBody(body);

				controller = new FirstPersonCameraController(camera, e.getKeyboard(), e.getMouse(), body, e.getSettings().mouseSensitivity, 5);
				controller.setSpeed(50);
				
//				music = e.getAudioBackend().loadMusic(e.getResource("sounds/01_Critical_Acclaim.ogx.ogg"), AudioFormat.VORBIS);
//				e.getAudioBackend().setBackgroundMusic(music);
//				e.getAudioBackend().setGain(0.5f);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});
		float[] lastFPS = {0};
		engine.setUpdate((e) -> {
			float nowFPS = e.getFPS(), delta = 0;
			if (nowFPS != lastFPS[0]) {
				System.out.println("FPS: " + nowFPS);
				lastFPS[0] = nowFPS;
			}
			if (nowFPS > 0) {
				delta = 1f / nowFPS;
			}
			//			universe.update(delta);
			controller.update(delta, terrain);
			sceneRenderer.render(delta, engine.getMouse(), engine.getSettings().width, engine.getSettings().height);
			particleRenderer.update(delta);
			particleRenderer.render(e);
			guiRenderer.render(e);
			textRenderer.render(e);
		});
		engine.run();
	}

	private static float nextCoordinate(Random random) {
		return random.nextFloat() * 1500 - 0;
	}

	private static int nextInt(Random random) {
		return random.nextInt(2);
	}

}
