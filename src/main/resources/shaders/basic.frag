#version 150 core

in vec3 vertexColor;
in vec3 vertexNormal;
in vec2 textureCoord;
in vec3 fragPos;

out vec4 fragColor;

uniform vec3 tintColor = vec3(0.5, 0.5, 0.5);
uniform sampler2D myTexImage;

uniform bool useTexture = false;
uniform bool useBlinn = false;

// All the Blinn
uniform vec3 lightPos;
uniform vec3 viewPos;
uniform vec3 ambientColor = vec3(1.0, 1.0, 1.0);
uniform float ambientStrength = 0.5;
uniform vec3 diffuseColor = vec3(1.0, 1.0, 1.0);
uniform float diffuseStrength = 0.5;
uniform vec3 specularColor = vec3(1.0, 1.0, 1.0);
uniform float specularStrength = 0.5;
uniform int specularShininess = 32;

void main() {
    vec3 objectColor;

    if(useTexture) {
        vec4 texturePixelColor = texture(myTexImage, textureCoord);
        objectColor = tintColor * vec3(texturePixelColor);
    } else {
        objectColor = vertexColor;
    }

    if(useBlinn) {
        vec3 normalDir = normalize(vertexNormal);
        vec3 lightDir = normalize(lightPos - fragPos);

        vec3 viewDir = normalize(viewPos - fragPos);
        vec3 reflectDir = reflect(-lightDir, normalDir);

        float diffusePower = max(dot(normalDir, lightDir), 0.0);
        float specularPower = pow(max(dot(viewDir, reflectDir), 0.0), specularShininess);

        vec3 ambient = ambientColor * ambientStrength;
        vec3 diffuse = diffusePower * diffuseColor * diffuseStrength;
        vec3 specular = specularPower * specularColor * specularStrength;

        vec3 result = (ambient + specular + diffuse) * objectColor;
        fragColor = vec4(result, 1.0);
    } else {
        fragColor = vec4(objectColor, 1.0);
    }
}