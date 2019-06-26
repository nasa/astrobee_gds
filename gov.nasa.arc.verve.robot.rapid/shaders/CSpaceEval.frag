uniform sampler2D cellTex;
uniform sampler2D gradientTex;
uniform sampler2D optimisticTex;
uniform sampler2D pessimisticTex;

uniform float optimisticMix;

uniform float goodnessMin;
uniform float goodnessRange;

uniform vec4  grayClr;
uniform float grayMix;

varying vec2  texCoordCell;
varying vec2  texCoordTile;

/** @return vec4 in range -1:1 from signed byte data that was packed into an unsigned byte texture */ 
vec4 signedByteTexture2D(sampler2D tex, vec2 texCoord) {
	return 2.0*(-0.5+texture2D(tex, texCoord));
}

/**
 *
 */
void main() {

    float optimistic  = signedByteTexture2D(optimisticTex,  texCoordTile).x;
    float pessimistic = signedByteTexture2D(pessimisticTex, texCoordTile).x;

    //float optVal = 0.1125 + 0.775*(optimistic-goodnessMin)/goodnessRange;
    //float pesVal = 0.1125 + 0.775*(pessimistic-goodnessMin)/goodnessRange;
    //
    //// get the coordinate for the color ramp lookup
    //vec2 optLookup = vec2(0.5, 1.0-optVal);
    //vec2 pesLookup = vec2(0.5, 1.0-pesVal);
    //
    //// get the color at the computed coordinates
    //vec4 optColor = texture2D( gradientTex, optLookup );
    //vec4 pesColor = texture2D( gradientTex, pesLookup );
	//
	//vec4 mixColor = mix(pesColor, optColor, optimisticMix) * texture2D( cellTex, texCoordCell);
	
	float optVal = 1.0 - (optimistic-goodnessMin)/goodnessRange;
	float pesVal = (pessimistic-goodnessMin)/goodnessRange;
	vec4 mixColor = vec4(optVal, 0.0, pesVal, 0.8);
	if(optVal+pesVal < 0.1)
		mixColor.a = 0.3;
		
	float alpha    = mixColor.a;
    vec4 final     = mixColor * texture2D( cellTex, texCoordCell);
    gl_FragColor   = mix(final, grayClr, grayMix);
    gl_FragColor.a = alpha;
}
