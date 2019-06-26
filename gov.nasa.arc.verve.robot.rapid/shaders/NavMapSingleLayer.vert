varying vec2 texCoordCell;
varying vec2 texCoordTile;

void main() {

    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;

    texCoordCell = vec2( gl_MultiTexCoord0 );
    texCoordTile = vec2( gl_MultiTexCoord1 );
        
}
