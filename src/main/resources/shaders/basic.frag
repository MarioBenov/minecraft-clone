#version 150 core

in vec3 vertexColor;
in vec2 textureCoord;

out vec4 fragColor;

uniform vec3 tintColor;
uniform sampler2D myTexImage;

uniform bool useTexture;

void main() {
    if(useTexture) {
        vec4 texturePixelColor = texture(myTexImage, textureCoord);
        fragColor = vec4(tintColor, 1.0) * texturePixelColor;
    } else {
        fragColor = vec4(vertexColor, 1.0);
    }
}