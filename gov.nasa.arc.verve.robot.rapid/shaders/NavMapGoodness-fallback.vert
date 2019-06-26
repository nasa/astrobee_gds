uniform sampler2D normalsTex;

uniform float zSign;

varying vec2 texCoordCell;
varying vec2 texCoordTile;

uniform float normScale;
varying float NdotLD0;

void main() {

    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;

    texCoordCell = vec2( gl_MultiTexCoord0 );
    texCoordTile = vec2( gl_MultiTexCoord1 );
        
    //-- normals --------------------------------
    vec3 lightDir0 = normalize(vec3(-0.7, 0.1, 1.0)); // fast, hard coded lighting
    vec3 normal    = normalize(gl_Normal);
    NdotLD0 = max(dot(normal, lightDir0), 0.0);
}
