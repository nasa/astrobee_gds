uniform sampler2D cellTex;
uniform sampler2D gradientTex;
uniform sampler2D certaintyTex;
uniform sampler2D normalsTex;

uniform float zSign;

uniform float certaintyMin;
uniform float certaintyRange;
uniform float certaintyThresh;

uniform float cosMaxAngleDiff; // cosine of the max slope

uniform vec4  grayClr;
uniform float grayMix;

varying vec2 texCoordCell;
varying vec2 texCoordTile;

uniform float normScale;
varying mat3  normalMatrix;
varying vec3  lightDir0;
varying vec3  lHalfVec0;

/** @return vec4 in range -1:1 from signed byte data that was packed into an unsigned byte texture */ 
vec4 signedByteTexture2D(sampler2D tex, vec2 texCoord) {
	return 2.0*(-0.5+texture2D(tex, texCoord));
}

/**
 *
 */
void main() {
	//-- per-pixel normal from texture
    vec4 ns = signedByteTexture2D(normalsTex, texCoordTile);
    float normX = zSign*ns.r*normScale;
    float normY = zSign*ns.a*normScale;
    float normZ = zSign*(1.0 - (normX*normX + normY*normY));
    vec3 normal = vec3(normX, normY, normZ);
    vec3 ecNormal = normalMatrix * vec3(normX, normY, normZ);
    
    float NdotLD0 = max(dot(ecNormal, lightDir0), 0.0);
    float NdotHV0 = max(dot(ecNormal, lHalfVec0), 0.0);

    float specular0 = pow(NdotHV0, 30.0);

    float certainty = signedByteTexture2D(certaintyTex, texCoordTile).x;
    float certVal = (certainty-certaintyMin)/certaintyRange;
    
	float NdotUp = abs( dot(normal, vec3(0.0, 0.0, 1.0)) );
	
    // get the coordinate for the color ramp lookup
	float slopeRange = 1.0-cosMaxAngleDiff;
	float slopeVal = clamp(0.1125 + 0.775*(NdotUp-cosMaxAngleDiff)/slopeRange, 0.0, 1.0);
    vec2 lookup = vec2( 0.5, slopeVal);

    vec4 final = texture2D( gradientTex, lookup ) * texture2D( cellTex, texCoordCell);
    gl_FragColor = mix(final, grayClr, grayMix);
    
    if(certVal < certaintyThresh) {
        gl_FragColor.a = 0.0;
    }
    else {
        gl_FragColor.a = 1.0;
    }

}
