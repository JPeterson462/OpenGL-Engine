package backends.opengl;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL32;
import org.lwjgl.system.MemoryUtil;

import com.esotericsoftware.minlog.Log;

import backends.RenderingBackend;
import engine.Engine;
import engine.Settings;
import engine.input.Key;
import engine.input.Keyboard;
import engine.input.Modifiers;
import engine.input.Mouse;
import engine.input.MouseButton;
import engine.rendering.Framebuffer;
import engine.rendering.Geometry;
import engine.rendering.InstancedGeometry;
import engine.rendering.Renderbuffer;
import engine.rendering.Shader;
import engine.rendering.Texture;
import engine.rendering.Vertex;
import engine.rendering.VertexTemplate;

public class GLRenderingBackend implements RenderingBackend {
	
	private long window;
	
	private GLMemory memory = new GLMemory();
	
	private Modifiers mouseModifiers = new Modifiers(false, false, false);
	
	private HashMap<MouseButton, Point> mouseDown = new HashMap<>();
	
	private int windowWidth, windowHeight;
	
	private class Point {
		int x, y;
	}

	public void setCulling(boolean enabled) {
		if (enabled) {
			GL11.glEnable(GL11.GL_CULL_FACE);
		} else {
			GL11.glDisable(GL11.GL_CULL_FACE);
		}
	}

	public void setDepth(boolean enabled) {
		if (enabled) {
			GL11.glEnable(GL11.GL_DEPTH_TEST);
		} else {
			GL11.glDisable(GL11.GL_DEPTH_TEST);
		}
	}

	public void setDepthBuffer(boolean enabled) {
		GL11.glDepthMask(enabled);
	}

	public void setAdditiveBlending(boolean enabled) {
		if (enabled) {
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		} else {
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		}
	}

	public void setBackgroundColor(float red, float green, float blue) {
		GL11.glClearColor(red, green, blue, 0);
	}
	
	public void checkError() {
		int error = GL11.glGetError();
		int errors = 0;
		while (error != GL11.GL_NO_ERROR) {
			errors++;
			Log.warn("OpenGL Error: " + error);
			error = GL11.glGetError();
		}
		if (errors > 0)
			throw new IllegalStateException("OpenGL Errors (" + errors + ")");
	}
	
	@Override
	public void createDisplay(Settings settings, Keyboard keyboard, Mouse mouse, Engine engine) {
		GLFWErrorCallback.createPrint(System.err).set();
		if (!GLFW.glfwInit())
			Log.error("Failed to create display", new IllegalStateException("Invalid GLFW Context"));
		GLFW.glfwDefaultWindowHints();
		GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
		GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, settings.resizable ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
		GLFWVidMode monitorSize = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
		if (settings.fullscreen && settings.nativeResolution) {
			window = GLFW.glfwCreateWindow(monitorSize.width(), monitorSize.height(), settings.title, settings.fullscreen ? GLFW.glfwGetPrimaryMonitor() : MemoryUtil.NULL, MemoryUtil.NULL);
		} else {
			window = GLFW.glfwCreateWindow(settings.width, settings.height, settings.title, settings.fullscreen ? GLFW.glfwGetPrimaryMonitor() : MemoryUtil.NULL, MemoryUtil.NULL);
			if (!settings.fullscreen)
				GLFW.glfwSetWindowPos(window, (monitorSize.width() - settings.width) / 2, (monitorSize.height() - settings.height) / 2);
		}
		GLFWInput.attachKeys();
		GLFWInput.attachMouseButtons();
		GLFW.glfwMakeContextCurrent(window);
		if (settings.vSync)
			GLFW.glfwSwapInterval(1);
		else
			GLFW.glfwSwapInterval(0);
		if (settings.windowIcons != null) {
			GLFW.glfwSetWindowIcon(window, GLFWUtils.loadIcons(settings));
		}
		GLFW.glfwSetKeyCallback(window, (w, glfwKey, scancode, action, mods) -> {
			if (w == window) {
				Modifiers modifiers = GLFWUtils.getModifiers(mods);
				Key key = new Key(glfwKey);
				switch (action) {
					case GLFW.GLFW_PRESS:
						keyboard.onKeyDown(key);
						break;
					case GLFW.GLFW_REPEAT:
						keyboard.onKeyTyped(key, modifiers);
						break;
					case GLFW.GLFW_RELEASE:
						keyboard.onKeyUp(key);
						keyboard.onKeyTyped(key, modifiers);
						break;
				}
			}
		});
		GLFW.glfwSetCharModsCallback(window, (w, letter, mods) -> {
			if (w == window) {
				keyboard.onLetterTyped((char) letter, GLFWUtils.getModifiers(mods));
			}
		});
		GLFW.glfwSetCursorPosCallback(window, (w, x, y) -> {
			if (w == window) {
				int oldx = mouse.getMouseX(), oldy = mouse.getMouseY();
				mouse.onCursorPos((int) x, (int) y);
				if (oldx >= 0 && oldy >= 0 && mouse.isButtonDown(MouseButton.LEFT)) {
					int dx = (int) x - oldx, dy = (int) y - oldy;
					Point pointDown = mouseDown.get(MouseButton.LEFT);
					mouse.onDrag(pointDown.x, pointDown.y, dx, dy, mouseModifiers);
				}
			}
		});
		GLFW.glfwSetMouseButtonCallback(window, (w, glfwButton, action, mods) -> {
			if (w == window) {
				Modifiers modifiers = GLFWUtils.getModifiers(mods);
				MouseButton button = new MouseButton(glfwButton);
				switch (action) {
					case GLFW.GLFW_PRESS:
						Point point = new Point();
						point.x = mouse.getMouseX();
						point.y = mouse.getMouseY();
						mouseDown.put(button, point);
						mouse.onButtonDown(button);
						break;
					case GLFW.GLFW_REPEAT:
						// Ignore
						break;
					case GLFW.GLFW_RELEASE:
						mouse.onButtonUp(button);
						mouse.onClick(mouse.getMouseX(), mouse.getMouseY(), button, modifiers);
						break;
				}
				mouseModifiers = modifiers;
			}
		});
		GLFW.glfwSetScrollCallback(window, (w, dx, dy) -> {
			if (w == window) {
				mouse.onScroll((int) dx, (int) dy, mouseModifiers);
			}
		});
		GLFW.glfwSetDropCallback(window, (w, count, files) -> {
			if (w == window) {
				engine.getFileDropCallback().onDropFiles(GLFWUtils.getPaths(files, count));
			}
		});
		GL.createCapabilities();
		if (!GL.getCapabilities().OpenGL33) {
			throw new IllegalStateException("This application requires OpenGL 3.3 or newer!");
		}
		windowWidth = settings.width;
		windowHeight = settings.height;
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
//		GL11.glEnable(GL11.GL_CULL_FACE);
//		GL11.glCullFace(GL11.GL_BACK);
		GL11.glClearColor(settings.backgroundColor.x, settings.backgroundColor.y, settings.backgroundColor.z, 0);
		if (settings.fullscreen && settings.nativeResolution)
			GL11.glViewport(0, 0, monitorSize.width(), monitorSize.height());
		else
			GL11.glViewport(0, 0, settings.width, settings.height);
	}

	@Override
	public void showDisplay() {
		GLFW.glfwShowWindow(window);
	}

	@Override
	public boolean isOpen() {
		return !GLFW.glfwWindowShouldClose(window);
	}

	@Override
	public void prepareContext() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
	}

	@Override
	public Geometry createGeometry(ArrayList<Vertex> vertices, ArrayList<Integer> indexList) {
		return GLGeometryBuilder.createGeometry(vertices, indexList, memory, () -> checkError());
	}

	@Override
	public InstancedGeometry createInstancedGeometry(ArrayList<Vertex> vertices, ArrayList<Integer> indexList, int instancedDataLength, int dimensions) {
		if (dimensions < 0)
			throw new IllegalArgumentException("Instanced geometry must have a positive number of dimensions");
		final int drawCall;
		if (indexList.size() % 3 == 0)
			drawCall = GL11.GL_TRIANGLES;
		else if (indexList.size() % 4 == 0)
			drawCall = GL11.GL_QUADS;
		else
		drawCall = 0;
		VertexTemplate template = vertices.get(0).getTemplate();
		switch (template) {
			case POSITION: {
				VertexBufferObject positionData = new VertexBufferObject(memory);
				VertexBufferObject instanceData = new VertexBufferObject(memory);
				VertexBufferObject indexData = new VertexBufferObject(memory);
				FloatBuffer positions = BufferUtils.createFloatBuffer(vertices.size() * dimensions);
				IntBuffer indices = BufferUtils.createIntBuffer(indexList.size());
				positions.limit(positions.capacity());
				indices.limit(indices.capacity());
				switch (dimensions) {
					case 1:
						for (int i = 0; i < vertices.size(); i++) {
							Vertex vertex = vertices.get(i);
							positions.put(i * 1 + 0, vertex.getPosition().x);
						}
						break;
					case 2:
						for (int i = 0; i < vertices.size(); i++) {
							Vertex vertex = vertices.get(i);
							positions.put(i * 2 + 0, vertex.getPosition().x);
							positions.put(i * 2 + 1, vertex.getPosition().y);
						}
						break;
					case 3:
						for (int i = 0; i < vertices.size(); i++) {
							Vertex vertex = vertices.get(i);
							positions.put(i * 3 + 0, vertex.getPosition().x);
							positions.put(i * 3 + 1, vertex.getPosition().y);
							positions.put(i * 3 + 2, vertex.getPosition().z);
						}
						break;
					default:
						Log.warn("Cannot handle instance geometry with " + dimensions + " dimensions");
						break;
				}
				for (int i = 0; i < indexList.size(); i++) {
					indices.put(i, indexList.get(i));
				}
				InstancedVertexArrayObject instancedVAO = new InstancedVertexArrayObject(memory, positionData, positions, dimensions, instanceData, instancedDataLength, indexData, indices);
				return new InstancedGeometry(instancedVAO) {
					public void updateInstances(FloatBuffer instanceBuffer) {
						VertexBufferObject instanceData = ((InstancedVertexArrayObject) getBackendData()).getInstances();
						instanceData.bind(GL15.GL_ARRAY_BUFFER);
						GL15.glBufferData(GL15.GL_ARRAY_BUFFER, instanceBuffer.limit() * 4, GL15.GL_STREAM_DRAW);
						GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, instanceBuffer);
						instanceData.unbind(GL15.GL_ARRAY_BUFFER);
					}
					
					public void renderGeometry(int count) {
						GL31.glDrawElementsInstanced(drawCall, indexList.size(), GL11.GL_UNSIGNED_INT, 0, count);
					}
				};
			}
			default:
				Log.warn("Cannot handle vertices of type " + template.name());
				break;
		}
		return null;
	}
	
	@Override
	public void updateGeometry(Geometry geometry, ArrayList<Vertex> vertices, ArrayList<Integer> indexList) {
		VertexTemplate template = vertices.get(0).getTemplate();
		switch (template) {
			case POSITION_TEXCOORD_COLOR: {
				VertexArrayObject vao = (VertexArrayObject) geometry.getBackendData();
				VertexBufferObject indexData = vao.getIndexBuffer();
				VertexBufferObject positionData = vao.getVBOs()[0];
				VertexBufferObject textureData = vao.getVBOs()[1];
				VertexBufferObject colorData = vao.getVBOs()[2];
				FloatBuffer positions = BufferUtils.createFloatBuffer(vertices.size() * 2);
				IntBuffer indices = BufferUtils.createIntBuffer(indexList.size());
				FloatBuffer texCoords = BufferUtils.createFloatBuffer(vertices.size() * 2);
				FloatBuffer colors = BufferUtils.createFloatBuffer(vertices.size() * 4);
				positions.limit(positions.capacity());
				indices.limit(indices.capacity());
				texCoords.limit(texCoords.capacity());
				colors.limit(colors.capacity());
				for (int i = 0; i < vertices.size(); i++) {
					Vertex vertex = vertices.get(i);
					positions.put(i * 2 + 0, vertex.getPosition().x);
					positions.put(i * 2 + 1, vertex.getPosition().y);
					texCoords.put(i * 2 + 0, vertex.getTextureCoord().x);
					texCoords.put(i * 2 + 1, vertex.getTextureCoord().y);
					colors.put(i * 4 + 0, vertex.getColor().x);
					colors.put(i * 4 + 1, vertex.getColor().y);
					colors.put(i * 4 + 2, vertex.getColor().z);
					colors.put(i * 4 + 3, vertex.getColor().w);
				}
				indexData.bind(GL15.GL_ELEMENT_ARRAY_BUFFER);
				indexData.upload(GL15.GL_ELEMENT_ARRAY_BUFFER, indices, false, 0);
				indexData.unbind(GL15.GL_ELEMENT_ARRAY_BUFFER);
				positionData.bind(GL15.GL_ARRAY_BUFFER);
				positionData.upload(GL15.GL_ARRAY_BUFFER, positions, false, 2);
				textureData.bind(GL15.GL_ARRAY_BUFFER);
				textureData.upload(GL15.GL_ARRAY_BUFFER, texCoords, false, 2);
				colorData.bind(GL15.GL_ARRAY_BUFFER);
				colorData.upload(GL15.GL_ARRAY_BUFFER, colors, false, 4);
				colorData.unbind(GL15.GL_ARRAY_BUFFER);
				break;
			}
			default:
				Log.warn("Cannot handle vertices of type " + template.name());
				break;
		}
	}
	
	@Override
	public Shader createShader(InputStream fragment, InputStream vertex, VertexTemplate vertices) {
		return GLShaderBuilder.createShader(fragment, vertex, vertices, memory);
	}

	@Override
	public Shader createInstancedShader(InputStream fragment, InputStream vertex, int[] attributes, String[] names) {
		return GLShaderBuilder.createInstancedShader(fragment, vertex, memory, attributes, names);
	}
	
	@Override
	public Texture createTexture(InputStream stream, boolean mipmapAlways, boolean clampEdges) {
		return GLTextureBuilder.createTexture(stream, mipmapAlways, clampEdges, memory);
	}

	@Override
	public Texture createCubemap(InputStream[] stream) {
		return GLTextureBuilder.createCubemap(stream, memory);
	}
	
	@Override
	public Framebuffer createFramebuffer(int width, int height, int colorAttachments, boolean hasDepthBuffer) {
		GLFramebuffer glFbo = new GLFramebuffer(memory, colorAttachments, width, height, windowWidth, windowHeight);
		int[] colorAttachmentList = new int[colorAttachments];
		for (int i = 0; i < colorAttachments; i++) {
			colorAttachmentList[i] = GLTextureBuilder.createEmptyTexture(width, height);
			GL32.glFramebufferTexture(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0 + i, colorAttachmentList[i], 0);
		}
		Texture[] colorAttachmentTextures = new Texture[colorAttachments];
		for (int i = 0; i < colorAttachments; i++) {
			colorAttachmentTextures[i] = new Texture(new GLTexture(colorAttachmentList[i], GL11.GL_TEXTURE_2D, memory), 
					width, height, Texture.TEXTURE_RGB);
		}
		if (hasDepthBuffer) {
			int renderbuffer = GLTextureBuilder.createEmptyRenderbuffer(width, height);
			GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL30.GL_RENDERBUFFER, renderbuffer);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
			GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, 0);
			glFbo.checkStatus();
			glFbo.unbind();
			checkError();
			return new Framebuffer(glFbo, new Renderbuffer(new GLRenderbuffer(renderbuffer, memory)), colorAttachmentTextures);
		} else {
			int texture = GL11.glGenTextures();
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
			GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_DEPTH_COMPONENT, width, height, 0, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, (ByteBuffer) null);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
			GL32.glFramebufferTexture(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, texture, 0);
			glFbo.checkStatus();
			glFbo.unbind();
			checkError();
			return new Framebuffer(glFbo, new Texture(new GLTexture(texture, GL11.GL_TEXTURE_2D, memory), width, height, Texture.TEXTURE_ALPHA), colorAttachmentTextures);
		}
	}
		
	@Override
	public void updateContext() {
		GLFW.glfwSwapBuffers(window);
		GLFW.glfwPollEvents();
		checkError();
	}
	
	@Override
	public void hideDisplay() {
		GLFW.glfwHideWindow(window);
	}

	@Override
	public void destroyDisplay() {
		memory.destroy();
		GLFW.glfwDestroyWindow(window);
		GLFW.glfwTerminate();
	}

}
