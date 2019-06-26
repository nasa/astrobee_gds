varying vec2 texCoord0; 

/**
 * LuminanceToGradient
 * Shader expects a luminance texture on unit 0 and a 
 * gradient texture on unit1. Luminance value is used as a lookup
 * into the gradient. Gradient is a 2D texture for convenience only. 
 */
void main()
{
	// standard transform of vertex
	gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;

	// pass tex coord of unit 0 to fragment shader
	texCoord0 = vec2(gl_MultiTexCoord0);
}
