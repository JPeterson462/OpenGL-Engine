#version 330

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;

uniform vec2 textureAtlasSize;
uniform vec2 textureAtlasOffset;

uniform vec4 plane;

in vec3 in_Position;
in vec2 in_TexCoord;
in vec3 in_Normal;

out vec2 pass_TexCoord;
out vec3 pass_SurfaceNormal;

void main(void) {
	vec4 worldPosition = modelMatrix * vec4(in_Position, 1.0);
	gl_ClipDistance[0] = dot(worldPosition, plane);
	vec4 positionRelativeToCamera = viewMatrix * worldPosition;
	gl_Position = projectionMatrix * positionRelativeToCamera;
	pass_TexCoord = (in_TexCoord / textureAtlasSize) + textureAtlasOffset;
	pass_SurfaceNormal = (modelMatrix * vec4(in_Normal, 0.0)).xyz;
}