varying vec2      texCoord; 
uniform sampler2D texture0; 

void main()
{
	gl_FragColor = texture2D(texture0, texCoord);
}
