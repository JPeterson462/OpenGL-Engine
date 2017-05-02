#version 150

const int MAX_JOINTS = 50;
const int MAX_WEIGHTS = 3;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

uniform mat4 boneMatrices[MAX_JOINTS];

in vec3 in_Position;
in vec2 in_TexCoord;
in vec3 in_Normal;
in ivec3 in_Joints;
in vec3 in_Weights;

out vec2 pass_TexCoord;
out vec3 pass_Normal;

void main(void) {
	vec4 totalLocalPos = vec4(0.0);
	vec4 totalNormal = vec4(0.0);
	for(int i = 0; i < MAX_WEIGHTS; i++){
		mat4 jointTransform = boneMatrices[in_Joints[i]];
		vec4 posePosition = jointTransform * vec4(in_Position, 1.0);
		totalLocalPos += posePosition * in_Weights[i];
		vec4 worldNormal = jointTransform * vec4(in_Normal, 0.0);
		totalNormal += worldNormal * in_Weights[i];
	}
	gl_Position = projectionMatrix * viewMatrix * totalLocalPos;
	pass_Normal = totalNormal.xyz;
	pass_TexCoord = in_TexCoord;
}