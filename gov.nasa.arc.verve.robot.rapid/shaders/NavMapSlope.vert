uniform float zSign;

varying vec2 texCoordCell;
varying vec2 texCoordTile;

varying mat3 normalMatrix;
varying vec3 lightDir0;
varying vec3 lHalfVec0;

void main() {

    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;

    texCoordCell = vec2( gl_MultiTexCoord0 );
    texCoordTile = vec2( gl_MultiTexCoord1 );
        
    //-- normals --------------------------------
    lightDir0 = normalize(vec3(gl_LightSource[0].position));
	lHalfVec0 = normalize(gl_LightSource[0].halfVector.xyz);

    normalMatrix = gl_NormalMatrix;
}
