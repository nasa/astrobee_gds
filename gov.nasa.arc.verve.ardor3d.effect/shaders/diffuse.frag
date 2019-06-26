varying vec3 normal;
varying vec3 vertexToLight;

void main()
{
	vec4 ambientClr = vec4(0.1, 0.0, 0.0, 1.0);
	vec4 diffuseClr = vec4(0.3, 0.3, 0.7, 1.0);

	vec3 nNormal = normalize(normal);
	vec3 nVertexToLight = normalize(vertexToLight);

	float value = clamp(dot(normal, nVertexToLight), 0.0, 1.0);

	// Calculating The Final Color
	gl_FragColor = ambientClr + diffuseClr * value;
}
