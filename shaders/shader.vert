#version 330
in vec2 inPos;
in vec3 inColor;
out vec3 vertColor;
out vec2 vertInPos;

void main() {
	vertInPos = inPos;
	gl_Position = vec4(inPos, 0.0, 1.0);
}