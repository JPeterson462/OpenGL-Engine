#version 330

const int MAX_LIGHTS = 4;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;

uniform vec3 lightPosition[MAX_LIGHTS];

uniform vec2 textureAtlasSize;
uniform vec2 textureAtlasOffset;
uniform vec4 plane;

in vec3 in_Position;
in vec2 in_TexCoord;
in vec3 in_Normal;
in vec3 in_Tangent;

out vec2 pass_TexCoord;
out vec3 pass_SurfaceNormal;
out vec3 pass_ToLightVector[MAX_LIGHTS];
out vec3 pass_ToCameraVector;
out float pass_Visibility;

const float density = 0;
const float gradient = 5.0;

void main(void) {
	mat4 modelViewMatrix = viewMatrix * modelMatrix;
	vec4 worldPosition = modelMatrix * vec4(in_Position, 1.0);
	gl_ClipDistance[0] = dot(worldPosition, plane);
	vec4 positionRelativeToCamera = modelViewMatrix * vec4(in_Position, 1.0);
	gl_Position = projectionMatrix * positionRelativeToCamera;
	pass_TexCoord = (in_TexCoord / textureAtlasSize) + textureAtlasOffset;
	pass_SurfaceNormal = (modelViewMatrix * vec4(in_Normal, 0.0)).xyz;
	
	vec3 norm = normalize(pass_SurfaceNormal);
	vec3 tang = normalize((modelViewMatrix * vec4(in_Tangent, 0.0)).xyz);
	vec3 bitang = normalize(cross(norm, tang));
	mat3 toTangentSpace = mat3(
		tang.x, bitang.x, norm.x,
		tang.y, bitang.y, norm.y,
		tang.z, bitang.z, norm.z
	);
	
	for (int i = 0; i < MAX_LIGHTS; i++) {
		pass_ToLightVector[i] = toTangentSpace * (lightPosition[i] - worldPosition.xyz);
	}
	pass_ToCameraVector = toTangentSpace * (-positionRelativeToCamera.xyz);
	float distance = length(positionRelativeToCamera.xyz);
	pass_Visibility = exp(-pow((distance * density), gradient));
	pass_Visibility = clamp(pass_Visibility, 0.0, 1.0);
}