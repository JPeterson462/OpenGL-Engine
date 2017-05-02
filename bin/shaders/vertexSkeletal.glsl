#version 150

const int MAX_JOINTS = 50;//max joints allowed in a skeleton
const int MAX_WEIGHTS = 3;//max number of joints that can affect a vertex

in vec3 in_Position;
in vec2 in_TexCoord;
in vec3 in_Normal;
in ivec3 in_Joints;
in vec3 in_Weights;

out vec2 pass_textureCoords;
out vec3 pass_normal;

uniform mat4 jointTransforms[MAX_JOINTS];
uniform mat4 projectionViewMatrix;

void main(void){
	
	vec4 totalLocalPos = vec4(0.0);
	vec4 totalNormal = vec4(0.0);
	
	for(int i=0;i<MAX_WEIGHTS;i++){
		mat4 jointTransform = jointTransforms[in_Joints[i]];
		vec4 posePosition = jointTransform * vec4(in_Position, 1.0);
		totalLocalPos += posePosition * in_Weights[i];
		
		vec4 worldNormal = jointTransform * vec4(in_Normal, 0.0);
		totalNormal += worldNormal * in_Weights[i];
	}
	
	gl_Position = projectionViewMatrix * totalLocalPos;
	pass_normal = totalNormal.xyz;
	pass_textureCoords = in_TexCoord;

}