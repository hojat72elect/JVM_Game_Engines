#version 450
#extension GL_ARB_separate_shader_objects : enable

layout (location = 0) in vec3 worldPosition;

layout(location = 0) out vec4 albedo_out;
layout(location = 1) out vec4 normal_out;

const vec3 baseColor = vec3(0.18,0.27,0.47);

void main()
{
	float red = (-0.00022*(abs(worldPosition.y)-2800) + 0.18 * 0.5);
	float green = (-0.00025*(abs(worldPosition.y)-2800) + 0.27 * 0.5);
	float blue = (-0.00019*(abs(worldPosition.y)-2800) + 0.47 * 0.5);
	
	albedo_out = vec4(red,green,blue,1);
	normal_out = vec4(0.0,0.0,0.0,1.0);
}