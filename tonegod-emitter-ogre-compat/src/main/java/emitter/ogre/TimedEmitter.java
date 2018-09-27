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

import com.jme3.math.FastMath;

import emitter.Emitter;

/**
 * Extension to {@link Emitter} that allows a duration (or random duration
 * range) to be set for all particles. Once this time is reached, no more
 * particles will be emitted, but current ones will continue till they naturally
 * die. A repeat time (or random repeat range) can be set. Once expired, the
 * emitter will wait for this duration, then start emitting again. This is to
 * mimic one of the features of the OGRE particle system.
 * 
 * @author rockire
 */
public class TimedEmitter extends Emitter {
	private long expire = Long.MAX_VALUE;
	private long repeatAt = Long.MAX_VALUE;
	private boolean emit = true;
	private float durationMin;
	private float durationMax;
	private float repeatDelayMin;
	private float repeatDelayMax;
	private boolean durationSet;
	private float timescale = 1f;

	public void setDurationMin(float seconds) {
		durationMin = seconds;
		durationSet = true;
	}

	public void setDurationMax(float seconds) {
		durationMax = seconds;
		durationSet = true;
	}

	public void setDuration(float seconds) {
		durationMin = durationMax = seconds;
		durationSet = true;
	}

	public void setRepeatDelayMin(float seconds) {
		repeatDelayMin = seconds;
	}

	public void setRepeatDelayMax(float seconds) {
		repeatDelayMax = seconds;
	}

	public void setRepeat(float seconds) {
		repeatDelayMin = repeatDelayMax = seconds;
	}

	@Override
	public void emitNextParticle() {
		long now = System.currentTimeMillis();
		if (!emit && now > repeatAt) {
			// Now it's time to repeat
			repeatAt = Long.MAX_VALUE;
			calcNewExpire(now);
			emit = true;
		} else if (emit && now > expire) {
			// Expired, stop emitting
			if (repeatDelayMin > 0 || repeatDelayMax > 0) {
				// Repeat
				calcNewRepeat(now);
			}
			emit = false;
		} else if (expire == Long.MAX_VALUE && durationSet) {
			// Calculate first expire
			calcNewExpire(now);
		}
		if (emit) {
			super.emitNextParticle();
		}
	}

	private void calcNewExpire(long now) {
		if (timescale == 0)
			expire = Long.MAX_VALUE;
		else {
			float delayMs = (durationMin + ((durationMax - durationMin) * FastMath.rand.nextFloat())) * 1000f
					/ timescale;
			expire = (long) delayMs + now;
		}
	}

	private void calcNewRepeat(long now) {
		if (timescale == 0)
			repeatAt = Long.MAX_VALUE;
		else {
			float delayMs = (repeatDelayMin + ((repeatDelayMax - repeatDelayMin) * FastMath.rand.nextFloat())) * 1000f
					/ timescale;
			repeatAt = (long) delayMs + now;
		}
	}

	@Override
	public void update(float tpf) {
		super.update(tpf * timescale);
	}

	public void setTimeScale(float ts) {
		timescale = ts;
	}

}
