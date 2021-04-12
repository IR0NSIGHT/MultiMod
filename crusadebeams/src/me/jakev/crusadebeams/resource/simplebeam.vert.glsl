#version 120



uniform bool handheld;
uniform float size;

varying vec3 outOrigPos;
varying vec3 outNormal;


void main()
{
	
//outOrigPos = gl_Vertex.xyz;
	outOrigPos.x = gl_Vertex.x*1.0;
	outOrigPos.y = gl_Vertex.y*1.0;
	outOrigPos.z = gl_Vertex.z-0.5;

        //Width of beam scales with size
        float maxCount = 10000.0;
        float minSize = 0.8;
        float maxSize = 5.0;
        if(handheld == true){
            minSize = 0.04;
            maxSize = 0.04;
        }
	outOrigPos.x *= maxSize - max((maxCount - size) / maxCount,0.0) * (maxSize - minSize);
	gl_TexCoord[0] = gl_MultiTexCoord0;
	
	outOrigPos.y *= maxSize - max((maxCount - size) / maxCount,0.0) * (maxSize - minSize);
	gl_TexCoord[0] = gl_MultiTexCoord0;
	
	outNormal = normalize( gl_NormalMatrix * gl_Normal );
	
	gl_Position = gl_ModelViewProjectionMatrix * vec4(outOrigPos, 1.0);
	
	
}

