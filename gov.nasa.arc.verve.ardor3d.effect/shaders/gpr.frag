varying vec2      texCoord0; 
uniform sampler2D texData; 
uniform sampler2D texGradient; 

uniform float     dataMin;
uniform float     dataMax;
uniform float     dataRange;

uniform int       renderMode;

/**
 * GPR
 * Expects signed values in texData
 *
 * Shader expects a luminance texture on unit 0 and a 
 * gradient texture on unit1. Luminance value is used as a lookup
 * into the gradient. Gradient is a 2D texture for convenience only
 * (i.e. editing in gimp, etc)
 *
 * The gradient image should have max at top of image, min at bottom
 * when viewed in image editing software. 
 * 
 * renderMode 0: mirrored around 0
 * renderMode 1: data min = low gradient, data max = high gradient
 */
void main()
{
	//vec4  neg = vec4( 0.0, 0.0, 0.0, 0.0);
	
	vec4  lum4 = texture2D(texData, texCoord0);	
	float lum  = lum4.x;
	float val;
	
	if( renderMode == 0 ) {
		val = abs(clamp(lum, 0.0, 1.0) / dataMax);
	}
	else {
		val = (lum - dataMin) / dataRange;
	}
	
	// treat the 2D gradient as a 1D texture
	vec2 lookup = vec2(0.5, 1.0-val);
	
	gl_FragColor = texture2D(texGradient, lookup);// - neg;
}
