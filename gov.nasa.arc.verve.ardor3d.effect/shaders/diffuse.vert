varying vec3 normal;
varying vec3 vertexToLight;

void main()
{
	gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;

	normal = gl_NormalMatrix * gl_Normal; 

	vec3 vertexModelView = vec3(gl_ModelViewMatrix * gl_Vertex);
    vec3 light0 = gl_LightSource[0].position.xyz;
    
	vertexToLight = light0 - vertexModelView; 
}
