/**
 * Tonegod Emitter OGRE Compatibility Library 
 * 
 * Copyright (c) 2017, Emerald Icemoon
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: 
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer. 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution. 
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies, 
 * either expressed or implied, of the FreeBSD Project.
 */
package emitter.ogre;

import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;

import com.jme3.asset.AssetManager;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;

import emitter.Emitter;

public interface OGREParticleEmitter {

	OGREParticleScript getScript();

	void setAngle(float angle);

	void setDirection(Vector3f direction);

	void setVelocity(float velocity);

	void setVelocityMin(float velocityMin);

	void setVelocityMax(float velocityMax);

	void setTimeToLive(float timeToLive);

	void setDuration(float duration);

	void setDurationMax(float durationMax);

	void setDurationMin(float durationMin);

	void setRepeatDelayMax(float repeatDelayMax);

	void setRepeatDelayMin(float repeatDelayMin);

	void setRepeatDelay(float repeatDelay);

	void setParticlesPerSec(float particlesPerSec);

	void setLocalTranslation(Vector3f localTranslation);

	void setLowLife(float lowLife);

	void setHighLife(float highLife);

	void setColour(ColorRGBA colour);

	void setStartColour(ColorRGBA startColour);

	void setEndColour(ColorRGBA startColour);

	Emitter createEmitter(AssetManager assetManager);

	boolean parse(String[] args, int lineNumber) throws ParseException;

	void write(OutputStream fos, boolean b) throws IOException;
}
