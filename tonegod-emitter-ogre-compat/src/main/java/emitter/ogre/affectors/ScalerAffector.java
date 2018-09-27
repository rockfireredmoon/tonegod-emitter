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
package emitter.ogre.affectors;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;

import emitter.influencers.ParticleInfluencer;
import emitter.ogre.AbstractOGREParticleAffector;
import emitter.ogre.OGREParticleScript;
import emitter.particle.ParticleData;

/**
 * Very simple Influencer that scales the particle by a certain amount each
 * second. An attempt to mimic "OGRE Scaler Affector"
 * (http://www.ogre3d.org/docs/manual/manual_40.html#Scaler-Affector)
 */
public class ScalerAffector extends AbstractOGREParticleAffector {

	private boolean initialized = false;
	private boolean enabled = true;
	private float rate = 1f;

	public ScalerAffector(OGREParticleScript script) {
		super(script);
	}

	@Override
	public void update(ParticleData p, float tpf) {
		if (enabled) {
			// TODO this division by 2f was found by trial and error. I'm not
			// sure why
			// it is required, but it makes particles look right size when
			// compared
			// in OgreParticleLab. See also InitialSizeInfluence
			float ds = (rate * tpf) / 2f;
			p.size.set(p.size.x + ds, p.size.y + ds, p.size.z);
		}
	}

	@Override
	public void initialize(ParticleData p) {
	}

	@Override
	public void reset(ParticleData p) {
	}

	public float getRate() {
		return rate;
	}

	public void setRate(float rate) {
		this.rate = rate;
	}

	@Override
	public void write(JmeExporter ex) throws IOException {
		OutputCapsule oc = ex.getCapsule(this);
		oc.write(rate, "rate", 0);
	}

	@Override
	public void read(JmeImporter im) throws IOException {
		InputCapsule ic = im.getCapsule(this);
		rate = ic.readFloat("rate", 0f);
	}

	@Override
	public ParticleInfluencer clone() {
		ScalerAffector clone = new ScalerAffector(script);
		clone.setRate(rate);
		clone.setEnabled(enabled);
		return clone;
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
		return ScalerAffector.class;
	}

	@Override
	protected void writeAffector(PrintWriter pw) {
		pw.println(String.format("\t\trate %1.1f", rate));
		pw.flush();
	}

	public boolean parse(String[] args, int lineNumber) throws ParseException {
		if (args[0].equals("rate")) {
			if (args.length == 2) {
				setRate(Float.parseFloat(args[1]));
			} else {
				throw new ParseException("Expected single rate for scaler at line " + lineNumber + ".", 0);
			}
		} else {
			return false;
		}
		return true;
	}
}
