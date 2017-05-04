#version 150

const int MAX_JOINTS = 50;//max joints allowed in a skeleton
const int MAX_WEIGHTS = 3;//max number of joints that can affect a vertex

const int MAX_LIGHTS = 4;

uniform vec3 lightPosition[MAX_LIGHTS];

in vec3 in_Position;
in vec2 in_TexCoord;
in vec3 in_Normal;
in ivec3 in_Joints;
in vec3 in_Weights;

out vec2 pass_TexCoord;
out vec3 pass_SurfaceNormal;
out vec3 pass_ToLightVector[MAX_LIGHTS];
out vec3 pass_ToCameraVector;
out float pass_Visibility;

uniform mat4 jointTransforms[MAX_JOINTS];
uniform mat4 projectionMatrix;
uniform mat4 modelMatrix;
uniform mat4 viewMatrix;

uniform vec4 plane;

const float density = 0.0035;
const float gradient = 5.0;

void main(void){
	
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
	vec4 positionRelativeToCamera = viewMatrix * worldPosition;
	gl_Position = projectionMatrix * positionRelativeToCamera;
	gl_ClipDistance[0] = dot(totalLocalPos, plane);
	pass_TexCoord = in_TexCoord;
	pass_SurfaceNormal = (modelMatrix * vec4(in_Normal, 0.0)).xyz;
	for (int i = 0; i < MAX_LIGHTS; i++) {
		pass_ToLightVector[i] = lightPosition[i] - worldPosition.xyz;
	}
	pass_ToCameraVector = (inverse(viewMatrix) * vec4(0.0, 0.0, 0.0, 1.0)).xyz - worldPosition.xyz;
	float distance = length(positionRelativeToCamera.xyz);
	pass_Visibility = exp(-pow((distance * density), gradient));
	pass_Visibility = clamp(pass_Visibility, 0.0, 1.0);

}