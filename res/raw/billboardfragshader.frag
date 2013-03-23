precision mediump float;

uniform sampler2D textureSampler;

varying vec4 fragColor;

varying vec2 textCoord;

void main()
{
    gl_FragColor = texture2D(textureSampler, textCoord);
}