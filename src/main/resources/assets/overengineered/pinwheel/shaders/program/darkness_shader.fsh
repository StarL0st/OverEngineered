#include veil:fog
#include veil:space_helper

// The first color attachment from `in`
uniform sampler2D DiffuseSampler0;
// The depth attachment from `in`
uniform sampler2D DiffuseDepthSampler;

const float FogStart = -120;
const float FogEnd = 30;
uniform vec4 FogColor;
uniform int FogShape;

in vec2 texCoord;

out vec4 fragColor;

void main() {
    // Sample from the screen
    vec4 baseColor = texture(DiffuseSampler0, texCoord);
    // Sample from the depth texture
    float depthSample = texture(DiffuseDepthSampler, texCoord).r;
    // Calculate the camera-relative position
    vec3 pos = screenToLocalSpace(texCoord, depthSample).xyz;

    // For fog, find the distance from the player
    float vertexDistance = fog_distance(pos, FogShape);
    // Output the mixed fog with the vanilla fog equation
    fragColor = linear_fog(baseColor, vertexDistance, FogStart, FogEnd, vec4(0, 0, 0, 0.95));
}