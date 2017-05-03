package engine.shadows;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import engine.Camera;

public class ShadowBox {
	
	private static final float OFFSET = 10;
	
	private static final Vector4f UP = new Vector4f(0, 1, 0, 0);
	
	private static final Vector4f FORWARD = new Vector4f(0, 0, -1, 0);
	
	public static final float SHADOW_DISTANCE = 150;
	
	private float minX, maxX;
	
	private float minY, maxY;
	
	private float minZ, maxZ;
	
	private Matrix4f lightViewMatrix;
	
	private Camera camera;
	
	private float farHeight, farWidth, nearHeight, nearWidth;
	
	public ShadowBox(Matrix4f lightViewMatrix, Camera camera, float nearPlane, float fov, float aspectRatio) {
		this.lightViewMatrix = lightViewMatrix;
		this.camera = camera;
		calculateWidthsAndHeights(nearPlane, fov, aspectRatio);
	}
	
	private Vector4f forwardVector4f = new Vector4f();
	
	private Vector3f forwardVector = new Vector3f(), toFar = new Vector3f(), toNear = new Vector3f(), centerNear = new Vector3f(), centerFar = new Vector3f();
	
	public void update(float nearPlane) {
		Matrix4f rotation = calculateCameraRotationMatrix();
		rotation.transform(FORWARD, forwardVector4f);
		forwardVector.set(forwardVector4f.x, forwardVector4f.y, forwardVector4f.z);
		toFar.set(forwardVector);
		toFar.mul(SHADOW_DISTANCE);
		toNear.set(forwardVector);
		toNear.mul(nearPlane);
		centerNear.set(toNear).add(camera.getCenter());
		centerFar.set(toFar).add(camera.getCenter());
		Vector4f[] points = calculateFrustumVertices(rotation, forwardVector, centerNear, centerFar);
		minX = points[0].x;
		maxX = points[0].x;
		minY = points[0].y;
		maxY = points[0].y;
		minZ = points[0].z;
		maxZ = points[0].z;
		for (int i = 1; i < points.length; i++) {
			minX = Math.min(minX, points[i].x);
			maxX = Math.max(maxX, points[i].x);
			minY = Math.min(minY, points[i].y);
			maxY = Math.max(maxY, points[i].y);
			minZ = Math.min(minZ, points[i].z);
			maxZ = Math.max(maxZ, points[i].z);
		}
		maxZ += OFFSET;
	}
	
	private Vector4f center = new Vector4f(), transformedCenter = new Vector4f();
	
	private Matrix4f invertedLight = new Matrix4f();
	
	public Vector3f getCenter() {
		float x = (minX + maxX) / 2f;
		float y = (minY + maxY) / 2f;
		float z = (minZ + maxZ) / 2f;
		center.set(x, y, z, 1);
		lightViewMatrix.invert(invertedLight);
		invertedLight.transform(center, transformedCenter);
		return new Vector3f(transformedCenter.x, transformedCenter.y, transformedCenter.z);
	}
	
	public float getWidth() {
		return maxX - minX;
	}
	
	public float getHeight() {
		return maxY - minY;
	}
	
	public float getLength() {
		return maxZ - minZ;
	}
	
	private Vector4f tmpVector4f = new Vector4f();
	
	private Vector3f upVector = new Vector3f(), rightVector = new Vector3f(), downVector = new Vector3f(),
			leftVector = new Vector3f(), farTop = new Vector3f(), farBottom = new Vector3f(),
			nearTop = new Vector3f(), nearBottom = new Vector3f();
	
	private Vector4f[] points = new Vector4f[] {
			new Vector4f(), new Vector4f(), new Vector4f(), new Vector4f(),
			new Vector4f(), new Vector4f(), new Vector4f(), new Vector4f()
	};
	
	private Vector4f[] calculateFrustumVertices(Matrix4f rotation, Vector3f forwardVector,
			Vector3f centerNear, Vector3f centerFar) {
		rotation.transform(UP, tmpVector4f);
		upVector.set(tmpVector4f.x, tmpVector4f.y, tmpVector4f.z);
		forwardVector.cross(upVector, rightVector);
		downVector.set(-upVector.x, -upVector.y, -upVector.z);
		leftVector.set(-rightVector.x, -rightVector.y, -rightVector.z);
		farTop.set(centerFar).add(upVector.x * farHeight,
				upVector.y * farHeight, upVector.z * farHeight);
		farBottom.set(centerFar).add(downVector.x * farHeight,
				downVector.y * farHeight, downVector.z * farHeight);
		nearTop.set(centerNear).add(upVector.x * nearHeight,
				upVector.y * nearHeight, upVector.z * nearHeight);
		nearBottom.set(centerNear).add(downVector.x * nearHeight,
				downVector.y * nearHeight, downVector.z * nearHeight);
		points[0] = calculateLightSpaceFrustumCorner(farTop, rightVector, farWidth, points[0]);
		points[1] = calculateLightSpaceFrustumCorner(farTop, leftVector, farWidth, points[1]);
		points[2] = calculateLightSpaceFrustumCorner(farBottom, rightVector, farWidth, points[2]);
		points[3] = calculateLightSpaceFrustumCorner(farBottom, leftVector, farWidth, points[3]);
		points[4] = calculateLightSpaceFrustumCorner(nearTop, rightVector, nearWidth, points[4]);
		points[5] = calculateLightSpaceFrustumCorner(nearTop, leftVector, nearWidth, points[5]);
		points[6] = calculateLightSpaceFrustumCorner(nearBottom, rightVector, nearWidth, points[6]);
		points[7] = calculateLightSpaceFrustumCorner(nearBottom, leftVector, nearWidth, points[7]);
		return points;
	}
	
	private Vector3f point = new Vector3f();
	
	private Vector4f calculateLightSpaceFrustumCorner(Vector3f startPoint, Vector3f direction,
			float width, Vector4f point4f) {
		point.set(startPoint);
		point.add(direction.x * width, direction.y * width, direction.z * width);
		point4f.set(point.x, point.y, point.z, 1f);
		lightViewMatrix.transform(point4f);
		return point4f;
	}
	
	private Matrix4f cameraRotationMatrix = new Matrix4f();
	
	private Matrix4f calculateCameraRotationMatrix() {
		return cameraRotationMatrix.identity().rotateXYZ((float) Math.toRadians(-camera.getPitch()), (float) Math.toRadians(-camera.getYaw()), 0);
	}
	
	private void calculateWidthsAndHeights(float nearPlane, float fov, float aspectRatio) {
		farWidth = (float) (SHADOW_DISTANCE * Math.tan(Math.toRadians(fov)));
		nearWidth = (float) (nearPlane * Math.tan(Math.toRadians(fov)));
		farHeight = farWidth / aspectRatio;
		nearHeight = nearWidth / aspectRatio;
	}

}
