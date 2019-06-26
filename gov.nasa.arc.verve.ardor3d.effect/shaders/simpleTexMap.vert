varying vec2 texCoord; 

void main()
{
	// Transforming The Vertex
	gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;

	// Passing The Texture Coordinate Of Texture Unit 0 To The Fragment Shader
	texCoord = vec2(gl_MultiTexCoord0);
}
