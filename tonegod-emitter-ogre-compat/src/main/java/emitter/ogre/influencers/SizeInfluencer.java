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

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.Vector2f;

import emitter.influencers.ParticleInfluencer;
import emitter.particle.ParticleData;

/**
 * Very simple Influencer that sets the particle to a fixed size when it is
 * initalized. An attempt to mimic particle_width and particle_height
 * (http://www.ogre3d.org/docs/manual/manual_35.html#particle_005fwidth) in OGRE
 * particle system
 */
public class SizeInfluencer implements ParticleInfluencer {

	private boolean enabled = true;
	private Vector2f size = new Vector2f(1, 1);

	@Override
	public void update(ParticleData p, float tpf) {
	}

	@Override
	public void initialize(ParticleData p) {
		p.size.setX(size.x).setY(size.y);
	}

	@Override
	public void reset(ParticleData p) {
	}

	public Vector2f getSize() {
		return size;
	}

	public void setSize(Vector2f size) {
		this.size = size;
	}

	@Override
	public void write(JmeExporter ex) throws IOException {
		OutputCapsule oc = ex.getCapsule(this);
		oc.write(size.x, "width", 1);
		oc.write(size.y, "height", 1);
	}

	@Override
	public void read(JmeImporter im) throws IOException {
		InputCapsule ic = im.getCapsule(this);
		size.x = ic.readFloat("width", 1);
		size.y = ic.readFloat("height", 1);
	}

	@Override
	public ParticleInfluencer clone() {
		try {
			SizeInfluencer clone = (SizeInfluencer) super.clone();
			clone.setSize(size.clone());
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
		return SizeInfluencer.class;
	}
}
