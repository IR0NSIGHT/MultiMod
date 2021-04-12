#version 120

varying vec3 outOrigPos;
varying vec3 outNormal;

uniform sampler3D noiseTex;
uniform vec4 thrustColor0;
uniform vec4 thrustColor1;
uniform float beamTime;
uniform float ticks;
uniform float texCoordMult;
uniform float lenDiff;
uniform float zoomFac;
	void main()
	{
	    //higher value => less width
	    float beamWidth = 0.5;
	
	    
	    vec2 uv = vec2(gl_TexCoord[0].x, gl_TexCoord[0].y) -0.5;
			
		vec3 finalColor = vec3( 0 );
		//vec3 colorAdj = vec3( 2.0 , 0.5 , 0.25 );
	        
	    //normalize thrustColor
	    vec3 thrustColor = normalize(thrustColor1.rgb);
	    vec3 colorAdj = vec3(0.0);
	    colorAdj += thrustColor.rgb;
		
	
	        //determines the beam intensity width
		float u = min(0.28, abs(2.0 / ((uv.x) * beamWidth)) / (gl_FragCoord.w));
		float pulse = abs(0.0 / ((uv.y - sin(beamTime*3.3)*0.00 + 0.0)));
	
	
		finalColor +=  u * colorAdj;
	        finalColor += pulse * colorAdj;
	
		float edgeAlpha = 4.0 - abs(1.0 - (gl_TexCoord[0].x * 2.0));;
	        
	    //some fun experimenting with moving pulses
	    //edgeAlpha *= 12.0 + abs(sin(uv.y*beamTime) * 23.0);
	
	    //start of beam doesn't clip through origin point, 0.5 is uv.y max as its offset by -0.5
	    float threshold = 0.5;
	    if(uv.y > threshold) {
	        edgeAlpha -= (uv.y-threshold)/(0.5-uv.y);
	    }
	
	    //discarding low alpha values for transparency reasons
	    if(edgeAlpha < 0.15+ (0.6-zoomFac)*0.8){
	        discard;
	    }
	    vec3 tc3 = vec3(uv.x+(0.00*beamTime), sin(beamTime*3.0), gl_TexCoord[0].y/1.0*sin(beamTime*0.0) );
	    float noise = texture3D(noiseTex,tc3).r;
	    finalColor.rgb += (zoomFac * -0.2)+noise*0.07;
	    gl_FragColor = vec4( finalColor, edgeAlpha);
	  
	
	
	}





   