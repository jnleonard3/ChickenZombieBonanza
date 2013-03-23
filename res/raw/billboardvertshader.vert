uniform mat4 projMat;
uniform mat4 viewMat;
uniform mat4 modelMat;

attribute vec4 vPosition;
attribute vec4 vColor;
attribute vec2 vTextureCoord;

varying vec4 fragColor;
varying vec2 textCoord;

void main()
{
    vec4 cameraSpaceObjectPos = viewMat * modelMat * vec4(0.0,0.0,0.0,1.0);
    
    float angle = atan(cameraSpaceObjectPos[0],cameraSpaceObjectPos[2]) + 1.57;
        
	mat4 clipRotation = mat4(1.0);
    clipRotation[0][0] = cos(angle);
    clipRotation[1][0] = sin(angle);
    clipRotation[0][1] = -sin(angle);
    clipRotation[1][1] = cos(angle);
    
    fragColor = vColor;
    
    textCoord = vTextureCoord;
    
    gl_Position = projMat * viewMat * modelMat * clipRotation * vPosition;
}