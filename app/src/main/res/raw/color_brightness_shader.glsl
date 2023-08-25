#version 330 core

in vec3 fragPosition;
out vec4 fragColor;

uniform float brightnessFactor;  // Adjust this factor for brightness

void main()
{
    vec3 color = vec3(0.5, 0.5, 1.0);  // Example base color
    vec3 adjustedColor = color * brightnessFactor;
    fragColor = vec4(adjustedColor, 1.0);
}
