#version 330

const int MAX_JOINTS = 50;//max joints allowed in a skeleton
const int MAX_WEIGHTS = 3;//max number of joints that can affect a vertex

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;

uniform vec2 textureAtlasSize;
uniform vec2 textureAtlasOffset;

uniform mat4 jointTransform[MAX_JOINTS];

uniform vec4 plane;

in vec3 in_Position;
in vec2 in_TexCoord;
in vec3 in_Normal;

out vec2 pass_TexCoord;
out vec3 pass_SurfaceNormal;

void main(void) {
	vec4 totalLocalPos = vec4(0.0);
	vec4 totalNormal = vec4(0.0);
	for(int i = 0; i < MAX_WEIGHTS; i++){
		mat4 jointTransform = jointTransforms[in_Joints[i]];
		vec4 posePosition = jointTransform * vec4(in_Position, 1.0);
		totalLocalPos += posePosition * in_Weights[i];
		vec4 worldNormal = jointTransform * vec4(in_Normal, 0.0);
		totalNormal += worldNormal * in_Weights[i];
	}	
	vec4 worldPosition = modelMatrix * totalLocalPos;
	gl_ClipDistance[0] = dot(worldPosition, plane);
	vec4 positionRelativeToCamera = viewMatrix * worldPosition;
	gl_Position = projectionMatrix * positionRelativeToCamera;
	pass_TexCoord = (in_TexCoord / textureAtlasSize) + textureAtlasOffset;
	pass_SurfaceNormal = (modelMatrix * vec4(totalNormal, 0.0)).xyz;
}