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
package emitter.ogre.influencers;

import java.io.IOException;

import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;

import emitter.influencers.ParticleInfluencer;
import emitter.particle.ParticleData;

/**
 * Set initial direction
 */
public class DirectionInfluencer implements ParticleInfluencer {

	private boolean enabled = true;
	private Vector3f direction;
	private float velocityMin;
	private float velocityMax;

	public DirectionInfluencer(Vector3f direction, float velocityMin, float velocityMax) {
		this.direction = direction;
		this.velocityMax = velocityMax;
		this.velocityMin = velocityMin;
	}

	public Vector3f getDirection() {
		return direction;
	}

	public void setDirection(Vector3f direction) {
		this.direction = direction;
	}

	public float getVelocityMin() {
		return velocityMin;
	}

	public void setVelocityMin(float velocityMin) {
		this.velocityMin = velocityMin;
	}

	public float getVelocityMax() {
		return velocityMax;
	}

	public void setVelocityMax(float velocityMax) {
		this.velocityMax = velocityMax;
	}

	@Override
	public void update(ParticleData p, float tpf) {

	}

	@Override
	public void initialize(ParticleData p) {
		p.velocity.set(direction.mult(velocityMin + ((velocityMax - velocityMin) * FastMath.rand.nextFloat())));
	}

	@Override
	public void reset(ParticleData p) {
	}

	@Override
	public void write(JmeExporter ex) throws IOException {
		// TODO
	}

	@Override
	public void read(JmeImporter im) throws IOException {
		// TODO
	}

	@Override
	public ParticleInfluencer clone() {
		try {
			DirectionInfluencer clone = (DirectionInfluencer) super.clone();
			clone.direction = direction.clone();
			clone.setEnabled(enabled);
			return clone;
		} catch (CloneNotSupportedException e) {
			throw new AssertionError();
		}
	}

	@Override
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public boolean isEnabled() {
		return this.enabled;
	}

	@Override
	public Class getInfluencerClass() {
		return DirectionInfluencer.class;
	}
}
