#version 330
in vec2 vertInPos;
out vec4 outColor;
uniform float imageWidth;
uniform float imageHeight;
uniform float exposure;
uniform float gamma;
uniform float lumaR;
uniform float lumaG;
uniform float lumaB;
uniform float mappingType;
uniform sampler2D image;

/*float gamma = 2.2;*/

/*vec3 rgb2hsv(vec3 c) {
    vec4 K = vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0);
    vec4 p = mix(vec4(c.bg, K.wz), vec4(c.gb, K.xy), step(c.b, c.g));
    vec4 q = mix(vec4(p.xyw, c.r), vec4(c.r, p.yzx), step(p.x, c.r));

    float d = q.x - min(q.w, q.y);
    float e = 1.0e-10;
    return vec3(abs(q.z + (q.w - q.y) / (6.0 * d + e)), d / (q.x + e), q.x);
}

vec3 hsv2rgb(vec3 c) {
    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}*/

vec3 linearToneMapping(vec3 color)
{
	color = clamp(exposure * color, 0., 1.);
	color = pow(color, vec3(1. / gamma));
	return color;
}

vec3 simpleReinhardToneMapping(vec3 color)
{
	color *= exposure/(1. + color / exposure);
	color = pow(color, vec3(1. / gamma));
	return color;
}

vec3 lumaBasedReinhardToneMapping(vec3 color)
{
	float luma = dot(color, vec3(lumaR, lumaG, lumaB));
	float toneMappedLuma = luma / (1. + luma);
	color *= toneMappedLuma / luma;
	color = pow(color, vec3(1. / gamma));
	return color;
}

vec3 RomBinDaHouseToneMapping(vec3 color)
{
    color = exp( -1.0 / ( 2.72*color + 0.15 ) );
	color = pow(color, vec3(1. / gamma));
	return color;
}

vec3 filmicToneMapping(vec3 color)
{
	color = max(vec3(0.), color - vec3(0.004));
	color = (color * (6.2 * color + .5)) / (color * (6.2 * color + 1.7) + 0.06);
	return color;
}

vec3 uncharted2ToneMapping(vec3 color)
{
	float A = 0.15;
	float B = 0.50;
	float C = 0.10;
	float D = 0.20;
	float E = 0.02;
	float F = 0.30;
	float W = 11.2;
/*	float exposure = 2.;*/
	color *= exposure;
	color = ((color * (A * color + C * B) + D * E) / (color * (A * color + B) + D * F)) - E / F;
	float white = ((W * (A * W + C * B) + D * E) / (W * (A * W + B) + D * F)) - E / F;
	color /= white;
	color = pow(color, vec3(1. / gamma));
	return color;
}

void main() {
    vec4 color = texture2D(image, vertInPos);
    if(mappingType == 0){
        color.rgb = linearToneMapping(color.rgb);
    }
    else if(mappingType == 1){
        color.rgb = simpleReinhardToneMapping(color.rgb);
        }
    else if(mappingType == 2){
        color.rgb = lumaBasedReinhardToneMapping(color.rgb);
        }
    else if(mappingType == 3){
        color.rgb = RomBinDaHouseToneMapping(color.rgb);
        }
    else if(mappingType == 4){
        color.rgb = filmicToneMapping(color.rgb);
        }
    else if(mappingType == 5){
        color.rgb = uncharted2ToneMapping(color.rgb);
    }

    outColor = color;
}