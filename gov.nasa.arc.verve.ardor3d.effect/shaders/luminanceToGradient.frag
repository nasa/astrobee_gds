varying vec2      texCoord0; 
uniform sampler2D texture0; 
uniform sampler2D texture1; 

/**
 * LuminanceToGradient
 * Shader expects a luminance texture on unit 0 and a 
 * gradient texture on unit1. Luminance value is used as a lookup
 * into the gradient. Gradient is a 2D texture for convenience only
 * (i.e. editing in gimp, etc)
 * The gradient image should have max at top of image, min at bottom
 * when viewed in image editing software. 
 */
void main()
{
	float lum = texture2D(texture0, texCoord0);
	
	// treat the 2D gradient as a 1D texture
	vec2 lookup = vec2(0.5, 1-lum);
	
	gl_FragColor = texture2D(texture1, lookup);
}
