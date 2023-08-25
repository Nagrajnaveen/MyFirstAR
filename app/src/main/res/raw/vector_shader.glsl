#version 330 core

layout(location = 0) in vec3 inPosition;
out vec3 fragPosition;

void main()
{
    fragPosition = inPosition;
    gl_Position = vec4(inPosition, 1.0);
}
