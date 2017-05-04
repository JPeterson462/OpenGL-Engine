#version 150

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;

uniform vec3 lightPosition;
uniform float lightRadius;

uniform vec3 cameraPosition;

in vec3 in_Position;

out float pass_FadeFactor;
out float pass_Directional;
out vec3 pass_ToCameraVector;
out vec3 pass_ToLightVector;
out vec2 pass_TexCoord;
out float pass_Depth;

void main(void) {
	if (lightRadius > 0) {
		vec4 worldPosition = modelMatrix * vec4(in_Position, 1.0);
		vec4 positionRelativeToCamera = viewMatrix * worldPosition;
		vec4 projectedPosition = projectionMatrix * positionRelativeToCamera;
		gl_Position = projectedPosition;
		pass_FadeFactor = clamp(length(lightPosition - worldPosition.xyz) / lightRadius, 0.0, 1.0);
		pass_ToCameraVector = (inverse(viewMatrix) * vec4(0.0, 0.0, 0.0, 1.0)).xyz - worldPosition.xyz;
		pass_ToLightVector = worldPosition.xyz - lightPosition;
		pass_Directional = 1.0;
		pass_TexCoord = (projectedPosition.xy / projectedPosition.w) * 0.5 + 0.5;
		pass_Depth = projectedPosition.z / projectedPosition.w;
	} else {
		gl_Position = vec4(in_Position, 1.0);
		pass_FadeFactor = 1.0;
		pass_ToCameraVector = vec3(0.0);
		pass_ToLightVector = vec3(0.0);
		pass_Directional = 0.0;
		pass_TexCoord = in_Position.xy * 0.5 + 0.5;
		pass_Depth = in_Position.z;
	}
}