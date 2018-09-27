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
import com.jme3.export.OutputCapsule;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;

import emitter.influencers.ParticleInfluencer;
import emitter.particle.ParticleData;

/**
 * Very simple Influencer that alters the initial rotation by a random amount
 * for each particle. Attempt to mimic the 'angle' attribute.
 * (http://www.ogre3d.org/docs/manual/manual_37.html#angle)
 */
public class AngleInfluencer implements ParticleInfluencer {

	private boolean enabled = true;
	private float angle;

	public AngleInfluencer(float angle) {
		this.angle = angle;
	}

	@Override
	public void update(ParticleData p, float tpf) {

	}

	@Override
	public void initialize(ParticleData p) {
		Quaternion deviateQuat = new Quaternion(new float[] { (FastMath.rand.nextFloat() * angle * 2) - angle,
				(FastMath.rand.nextFloat() * angle * 2) - angle, (FastMath.rand.nextFloat() * angle * 2) - angle });
		p.velocity.set(deviateQuat.mult(p.velocity));

		// Quaternion dirQuat = new Quaternion();
		// dirQuat.lookAt(direction, upVector);
		// Quaternion deviateQuat = new Quaternion(new float[] {
		// (FastMath.rand.nextFloat() * angle * 2 ) - angle,
		// (FastMath.rand.nextFloat() * angle * 2 ) - angle,
		// (FastMath.rand.nextFloat() * angle * 2 ) - angle});
		// dirQuat.multLocal(deviateQuat);
		// float[] angles = new float[3];
		// dirQuat.toAngles(angles);
		// p.angles.set(angles[0],angles[1], angles[2]);
	}

	@Override
	public void reset(ParticleData p) {
	}

	@Override
	public void write(JmeExporter ex) throws IOException {
		OutputCapsule oc = ex.getCapsule(this);
		oc.write(angle, "angle", angle);
	}

	@Override
	public void read(JmeImporter im) throws IOException {
		// TODO
	}

	@Override
	public ParticleInfluencer clone() {
		try {
			AngleInfluencer clone = (AngleInfluencer) super.clone();
			clone.angle = angle;
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
		return AngleInfluencer.class;
	}
}
