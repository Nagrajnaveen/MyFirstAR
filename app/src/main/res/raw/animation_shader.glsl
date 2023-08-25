#version 330 core

in vec3 fragPosition;
out vec4 fragColor;

uniform float time;  // Time in seconds for animation

void main()
{
    vec3 baseColor = vec3(0.5, 0.5, 1.0);  // Example base color
    vec3 animationColor = vec3(sin(time), cos(time), abs(sin(time * 0.5)));  // Simple animation effect
    fragColor = vec4(baseColor + animationColor, 1.0);
}
