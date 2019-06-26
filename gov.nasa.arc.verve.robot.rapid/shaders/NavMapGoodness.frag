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
varying vec3 normal;
varying mat3 normalMatrix;
varying vec3 lightDir0;
varying vec3 lHalfVec0;
varying float NdotLD0;
varying float NdotHV0;
varying float specular0;

/** @return vec4 in range -1:1 from signed byte data that was packed into an unsigned byte texture */ 
vec4 signedByteTexture2D(sampler2D tex, vec2 texCoord) {
	return 2.0*(-0.5+texture2D(tex, texCoord));
}

/**
 *
 */
void main() {
	////-- per-pixel normal from texture
    //vec4 ns = signedByteTexture2D(normalsTex, texCoordTile);
    //float normX = zSign*ns.r*normScale;
    //float normY = zSign*ns.a*normScale;
    //float normZ = zSign*(1.0 - (normX*normX + normY*normY));
    //normal = normalMatrix * vec3(normX, normY, normZ);
	////normal = normalize(normal); // needed for per-vertex normals
    //NdotLD0 = max(dot(normal, lightDir0), 0.0);
    //NdotHV0 = max(dot(normal, normalize(lHalfVec0)), 0.0);
    //float specular0 = pow(NdotHV0, 30.0);

    float goodness = signedByteTexture2D(goodnessTex, texCoordTile).x;
    float certainty = signedByteTexture2D(certaintyTex, texCoordTile).x;

    float goodVal = 0.1125 + 0.775*(goodness-goodnessMin)/goodnessRange;
    float certVal = (certainty-certaintyMin)/certaintyRange;

    // get the coordinate for the color ramp lookup
    vec2 lookup = vec2(0.5, 1.0-goodVal);

    float darken = 0.2+NdotLD0*certVal;
    // get the color at the computed coordinates
    vec4 color = texture2D( gradientTex, lookup ) * texture2D( cellTex, texCoordCell);
	
	vec4 final = clamp(0.8*color*darken + ((0.1+color*0.15)*specular0), 0.0, 1.0);
    gl_FragColor = mix(final, grayClr, grayMix);
    
    if(certVal < certaintyThresh) {
        gl_FragColor.a = 0.0;
    }
    else {
        gl_FragColor.a = color.a;
    }
    
}
