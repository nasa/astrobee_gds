uniform sampler2D cellTex;
uniform sampler2D gradientTex;
uniform sampler2D goodnessTex;
uniform sampler2D certaintyTex;
uniform sampler2D normalsTex;

uniform float zSign;

uniform float goodnessMin;
uniform float goodnessRange;

uniform float certaintyMin;
uniform float certaintyRange;
uniform float certaintyThresh;

uniform vec4  grayClr;
uniform float grayMix;

varying vec2 texCoordCell;
varying vec2 texCoordTile;

uniform float normScale;
varying float NdotLD0;

/** @return vec4 in range -1:1 from signed byte data that was packed into an unsigned byte texture */ 
vec4 signedByteTexture2D(sampler2D tex, vec2 texCoord) {
	return 2.0*(-0.5+texture2D(tex, texCoord));
}

/**
 *
 */
void main() {
    float goodness = signedByteTexture2D(goodnessTex, texCoordTile).x;
    float certainty = signedByteTexture2D(certaintyTex, texCoordTile).x;

    float goodVal = 0.1125 + 0.775*(goodness-goodnessMin)/goodnessRange;
    float certVal = (certainty-certaintyMin)/certaintyRange;

    // get the coordinate for the color ramp lookup
    vec2 lookup = vec2(0.5, 1.0-goodVal);

    float darken = NdotLD0*certVal;
    // get the color at the computed coordinates
    vec4 color = texture2D( gradientTex, lookup ) * texture2D( cellTex, texCoordCell);
	
    vec4 final = color*darken;
    gl_FragColor = mix(final, grayClr, grayMix);
    
    if(certVal < certaintyThresh) {
        gl_FragColor.a = 0.0;
    }
    else {
        gl_FragColor.a = 1.0;
    }
    
}
