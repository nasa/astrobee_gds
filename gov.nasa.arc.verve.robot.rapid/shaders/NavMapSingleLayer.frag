uniform sampler2D cellTex;
uniform sampler2D gradientTex;
uniform sampler2D layerTex;

uniform float layerAlpha;

uniform float layerMin;
uniform float layerRange;

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

    float layerRaw  = signedByteTexture2D(layerTex,  texCoordTile).x;

    float layerNrm = (layerRaw-layerMin)/layerRange;
    float layerVal = 0.1125 + 0.775*layerNrm;
    
    // get the coordinate for the color ramp lookup
    vec2 layerLookup = vec2(0.5, 1.0-layerVal);
    
    // get the color at the computed coordinates
    vec4 layerColor = texture2D( gradientTex, layerLookup );
    float alpha = 1;
    if(layerAlpha < 0.99) {
    	alpha = layerAlpha * (0.35+0.65*(1.0-layerNrm));
	}	
		
    vec4 final = layerColor * texture2D( cellTex, texCoordCell);
    
    gl_FragColor = mix(final, grayClr, grayMix); 
    gl_FragColor.a = alpha;
}
