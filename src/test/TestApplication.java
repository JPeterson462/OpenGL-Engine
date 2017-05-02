package test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import javax.xml.parsers.ParserConfigurationException;

import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.util.vector.Matrix4f;
import org.xml.sax.SAXException;

import animatedModel.AnimatedModel;
import animation.Animation;
import engine.Assets;
import engine.Engine;
import engine.Entity;
import engine.FirstPersonCamera;
import engine.RawImage;
import engine.Settings;
import engine.audio.AudioFormat;
import engine.audio.Music;
import engine.audio.SoundEffect;
import engine.audio.Soundtrack;
import engine.gui.Container;
import engine.gui.GUI;
import engine.gui.Image;
import engine.gui.Label;
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
import engine.rendering.Geometry;
import engine.rendering.Light;
import engine.rendering.Material;
import engine.rendering.Shader;
import engine.rendering.VertexTemplate;
import engine.rendering.passes.DefaultGUIRenderer;
import engine.rendering.passes.GUIRenderer;
import engine.rendering.passes.ParticleRenderer;
import engine.rendering.passes.PostProcessingRenderer;
import engine.rendering.passes.SceneRenderer;
import engine.rendering.passes.ShadowRenderer;
import engine.rendering.passes.SkyboxRenderer;
import engine.rendering.passes.TerrainRenderer;
import engine.rendering.passes.TextRenderer;
import engine.rendering.passes.WaterRenderer;
import engine.rendering.passes.WidgetRenderer;
import engine.terrain.HeightmapTerrainGenerator;
import engine.terrain.ProceduralTerrainGenerator;
import engine.terrain.Terrain;
import engine.terrain.TerrainGenerator;
import engine.terrain.TerrainTile;
import engine.terrain.TerrainTexturePack;
import engine.text.Font;
import engine.text.FontImporter;
import engine.water.Water;
import engine.water.WaterTile;
import loaders.AnimatedModelLoader;
import loaders.AnimationLoader;
import renderer.AnimatedModelRenderer;
import renderer.ICamera;
import utils.MatrixConv;
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
	
	private static Soundtrack soundtrack;
	
	private static long seed = 0;//System.currentTimeMillis();
	
	private static AnimatedModelRenderer modelRenderer;
	
	private static ICamera camera;
	
	private static AnimatedModel animatedModel;

	private static TerrainGenerator newGenerator(int x, int z) {
//		return new ProceduralTerrainGenerator(seed, 35, 15, x, z);
		return new HeightmapTerrainGenerator(heightmap, 40, 0, x, z);
	}

	public static void main(String[] args) throws SAXException, IOException, ParserConfigurationException {
		WaterRenderer.WATER_HEIGHT = 20;//TEST
		Settings settings = new Settings(new FileInputStream("settings.cfg"));
		settings.write(new FileOutputStream("settings.cfg"));
		FirstPersonCamera camera = new FirstPersonCamera(settings.fov, settings.aspectRatio, settings.nearPlane, settings.farPlane, new Vector3f(30, 10, 30));
		TestApplication.camera = new ICamera() {

			@Override
			public Matrix4f getViewMatrix() {
				return MatrixConv.convert(camera.getViewMatrix());
			}

			@Override
			public Matrix4f getProjectionMatrix() {
				return MatrixConv.convert(camera.getProjectionMatrix());
			}

			@Override
			public Matrix4f getProjectionViewMatrix() {
				org.joml.Matrix4f dest = new org.joml.Matrix4f();
				camera.getProjectionMatrix().mul(camera.getViewMatrix(), dest);
				return MatrixConv.convert(dest);
			}

			@Override
			public void move() {
				
			}
			
		};
		Engine engine = new Engine(settings);
		ModelImporter.flags.setBit(0, true);
		ModelImporter.flags.setBit(1, false);
		engine.setInit((e) -> {
			try {
				heightmap = new RawImage(engine.getResource("textures/heightmap.png"));
//				heightmap = new RawImage(engine.getResource("textures/heightmap1.jpg"));

				Assets.attachTo(e);
				
				soundtrack = Assets.newSoundtrack("AvengedSevenfold.zip", AudioFormat.VORBIS);
				
				font = FontImporter.loadFont("Consolas.fnt", e);
				textRenderer = new TextRenderer(Assets.newShader("fragmentText.glsl", "vertexText.glsl", VertexTemplate.POSITION_TEXCOORD_COLOR), e.getSettings().width, e.getSettings().height);

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
//				TerrainTile[][] tiles = new TerrainTile[1][1];
//				tiles[0][0] = new TerrainTile(newGenerator(0, 0), e, pack);
//				tiles[0][1] = new TerrainTile(newGenerator(0, 1), e, pack);
//				tiles[1][0] = new TerrainTile(newGenerator(1, 0), e, pack);
//				tiles[1][1] = new TerrainTile(newGenerator(1, 1), e, pack);
				terrain = new Terrain(tiles);

				SkyboxRenderer skybox = new SkyboxRenderer(Assets.newShader("fragmentSkybox.glsl", "vertexSkybox.glsl", VertexTemplate.POSITION), e);

				Light sun = new Light(new Vector3f(100000, 100000, -100000), new Vector3f(1.3f, 1.3f, 1.3f));
				
				ArrayList<WaterTile> waterTiles = new ArrayList<>();
				waterTiles.add(new WaterTile(80, WaterRenderer.WATER_HEIGHT, 80));
				Water water = new Water(waterTiles);
				WaterRenderer waterRenderer = new WaterRenderer(Assets.newShader("fragmentWater.glsl", "vertexWater.glsl", VertexTemplate.POSITION), e, water, Assets.newTexture("waterDUDV.png"), 
						Assets.newTexture("waterNormal.png"), sun);
				
				ShadowRenderer shadowRenderer = new ShadowRenderer(Assets.newShader("fragmentShadow.glsl", "vertexShadow.glsl", VertexTemplate.POSITION_TEXCOORD), e, camera);
				
//				Image i1 = new Image("id1", shadowRenderer.getShadowMap());
//				i1.getPosition().set(0, 0);
//				i1.getSize().set(640, 360);
				
				HashMap<Class<?>, WidgetRenderer> renderers = new HashMap<>();
				renderers.put(DefaultGUIRenderer.class, new DefaultGUIRenderer(Assets.newShader("fragmentGui.glsl", "vertexGui.glsl", VertexTemplate.POSITION_TEXCOORD)));
				renderers.put(TextRenderer.class, textRenderer);
				
				Container container = new Container("container0");			
				container.getPosition().set(100, 0);
				container.getSize().set(500, 500);
				
				Image i0 = new Image("id0", "google_logo.png");
				i0.getPosition().set(0, 100);
				i0.getSize().set(200, 70);
				container.addWidget(i0);
				
//				Label l0 = new Label("id1", font, "Hello World", new Vector4f(1, 1, 0, 1), 24, new Vector2f(200, 70));
//				container.addWidget(l0);
				
				container.layout();
				
				GUI gui = new GUI(container);
				guiRenderer = new GUIRenderer(gui, renderers, 1280, 720);
				
				PostProcessingRenderer postProcessing = new PostProcessingRenderer();
				
				modelRenderer = new AnimatedModelRenderer();
				
				Animation animation = AnimationLoader.loadAnimation(engine.getResource("models/model.dae"));
				animatedModel = AnimatedModelLoader.loadEntity(engine.getResource("models/model.dae"), engine.getResource("textures/animatedDiffuse.png"));
				animatedModel.doAnimation(animation);

				sceneRenderer = new SceneRenderer(shader, normalMappedShader, camera, new TerrainRenderer(terrainShader, terrain), 
						engine.getSettings().backgroundColor, skybox, waterRenderer, shadowRenderer, postProcessing);
				sceneRenderer.addLight(sun);
				sceneRenderer.addLight(new Light(new Vector3f(185, 10, -293), new Vector3f(2, 0, 0), new Vector3f(1, 0.01f, 0.002f)));
				sceneRenderer.addLight(new Light(new Vector3f(370, 17, -300), new Vector3f(0, 2, 2), new Vector3f(1, 0.01f, 0.002f)));
				//				sceneRenderer.addLight(new Light(new Vector3f(293, 7, -305), new Vector3f(2, 2, 0), new Vector3f(1, 0.01f, 0.002f)));
				sceneRenderer.addLight(new Light(new Vector3f(10, 10, 10), new Vector3f(2, 2, 0), new Vector3f(1, 0.01f, 0.002f)));
//				sceneRenderer.addEntity(entity);
				
//				Animation animation = AnimationImporter.loadAnimation("model.dae", e);
//				AnimatedModel animatedModel = AnimatedModelImporter.loadAnimatedModel("model.dae", e, Assets.newTexture("animatedDiffuse.png"));
//				animatedModel.doAnimation(animation);
//				Entity entity = new Entity(animatedModel);
//				entity.setPosition(new Vector3f(30, 30, 30));
//				entity.setScale(10);
//				sceneRenderer.addEntity(entity);

				Model lamp = Assets.newModel("lamp.obj", false);
				Material lampMaterial = Assets.newMaterial("lamp.png");
				Geometry lampGeometry = e.getRenderingBackend().createGeometry(lamp.getVertices(), lamp.getIndices());
				Entity lamp1 = new Entity(lamp, lampMaterial, lampGeometry, 1, 1, 0, 0);
				lamp1.setPosition(new Vector3f(185, terrain.getHeightAt(185, -293), -293));
				Entity lamp2 = new Entity(lamp, lampMaterial, lampGeometry, 1, 1, 0, 0);
				lamp2.setPosition(new Vector3f(370, terrain.getHeightAt(370, -300), -300));
				Entity lamp3 = new Entity(lamp, lampMaterial, lampGeometry, 1, 1, 0, 0);
				lamp3.setPosition(new Vector3f(293, terrain.getHeightAt(293, -305), -305));

//				Model crate = Assets.newModel("crate.obj", true);
//				Material crateMaterial = Assets.newMaterial("crate.png", "crateNormal.png");
//				crateMaterial.setReflectivity(0.5f);
//				crateMaterial.setShineDamper(10);
//				Geometry crateGeometry = e.getRenderingBackend().createGeometry(crate.getVertices(), crate.getIndices());

				sceneRenderer.addEntity(lamp1);
				sceneRenderer.addEntity(lamp2);
				sceneRenderer.addEntity(lamp3);
//				Entity crateEntity = new Entity(crate, crateMaterial, crateGeometry, 1, 1, 0, 0);
//				crateEntity.setScale(0.1f);
//				sceneRenderer.addEntity(crateEntity);

				Model tree = Assets.newModel("tree.obj", true);
				Material treeMaterial = Assets.newMaterial("tree.png");
				Geometry treeGeometry = Assets.newGeometry(tree);
				
				Random random = new Random();
				for (int i = 0; i < 600; i++) {
					entity = new Entity(fern, fernMaterial, fernGeometry, 2, 2, nextInt(random), nextInt(random));					
					entity.setPosition(new Vector3f(nextCoordinate(random), 0, nextCoordinate(random)));
					entity.getPosition().y = terrain.getHeightAt(entity.getPosition().x, entity.getPosition().z);
					entity.setScale(0.6f);
					entity.setTransparency(true);
					entity.getMaterial().setReflectivity(Material.NO_REFLECTIVITY);
					sceneRenderer.addEntity(entity);
					entity = new Entity(tree, treeMaterial, treeGeometry, 1, 1, 0, 0);
					entity.setPosition(new Vector3f(nextCoordinate(random), 0, nextCoordinate(random)));
					entity.getPosition().y = terrain.getHeightAt(entity.getPosition().x, entity.getPosition().z);
					entity.setScale(15);
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
		Runtime runtime = Runtime.getRuntime();
		engine.setUpdate((e) -> {
			float nowFPS = e.getFPS(), delta = 0;
			if (nowFPS != lastFPS[0]) {
				System.out.println("FPS: " + nowFPS + ", Memory: " + ((runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024)) + "mb");
				lastFPS[0] = nowFPS;
			}
			if (nowFPS > 0) {
				delta = 1f / nowFPS;
			}
//			soundtrack.update(e);
			//			universe.update(delta);
			controller.update(delta, terrain);
			sceneRenderer.render(delta, engine.getMouse(), engine.getSettings().width, engine.getSettings().height, engine);
			
			animatedModel.update(delta);
			modelRenderer.render(animatedModel, TestApplication.camera, new org.lwjgl.util.vector.Vector3f(0.2f, -0.3f, -0.8f));
			
//			particleRenderer.update(delta);
//			particleRenderer.render(e);
			guiRenderer.render(e);
//			textRenderer.render(e);
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
