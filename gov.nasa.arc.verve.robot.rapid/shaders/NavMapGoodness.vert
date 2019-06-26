uniform sampler2D normalsTex;

uniform float zSign;

varying vec2 texCoordCell;
varying vec2 texCoordTile;

uniform float normScale;
varying vec3 normal;
varying mat3 normalMatrix;
varying vec3 lightDir0;
varying vec3 lHalfVec0;
varying float NdotLD0;
varying float NdotHV0;
varying float specular0;

/** @return vec4 in range -1:1 from signed byte data that was packed into an unsigned byte texture */ 
vec4 signedByteTexture2D(sampler2D tex, vec2 texCoord) {
	return 2.0*(-0.5+texture2D(tex, texCoord));
}

void main() {

    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;

    texCoordCell = vec2( gl_MultiTexCoord0 );
    texCoordTile = vec2( gl_MultiTexCoord1 );
        
    //-- normals --------------------------------
    lightDir0 = normalize(vec3(gl_LightSource[0].position));
	lHalfVec0 = normalize(gl_LightSource[0].halfVector.xyz);

    normalMatrix = gl_NormalMatrix;

    //-- per-vertex normal from texture
    vec4 ns = signedByteTexture2D(normalsTex, texCoordTile);
    float normX = zSign*ns.r*normScale;
    float normY = zSign*ns.a*normScale;
    float normZ = zSign*(1.0 - (normX*normX + normY*normY));
    normal = normalMatrix * vec3(normX, normY, normZ);
    
    ////-- vertex normal
    //normal       = normalize(normalMatrix * gl_Normal);
    
    NdotLD0 = max(dot(normal, lightDir0), 0.0);
    NdotHV0 = max(dot(normal, lHalfVec0), 0.0);
    specular0 = pow(NdotHV0, 25.0);
}
