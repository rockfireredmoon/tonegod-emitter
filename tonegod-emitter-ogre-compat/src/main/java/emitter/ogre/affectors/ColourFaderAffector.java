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

import org.icebeans.FloatRange;
import org.icebeans.Property;
import org.icebeans.Property.Hint;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.Vector4f;

import emitter.influencers.ParticleInfluencer;
import emitter.ogre.AbstractOGREParticleAffector;
import emitter.ogre.OGREParticleScript;
import emitter.particle.ParticleData;

/**
 * Attempt to mimic ColourFader Affector
 * (http://www.ogre3d.org/docs/manual/manual_40.html#ColourFader-Affector)
 */
public class ColourFaderAffector extends AbstractOGREParticleAffector {

	private boolean enabled = true;
	private Vector4f adjustment = new Vector4f(0, 0, 0, 0);

	public ColourFaderAffector(OGREParticleScript script) {
		super(script);
	}

	public ColourFaderAffector(OGREParticleScript script, Vector4f adjustment) {
		super(script);
		this.adjustment = adjustment;
	}

	@Override
	public void update(ParticleData p, float tpf) {
		p.color.set(p.color.r + (adjustment.x * tpf), p.color.g + (adjustment.y * tpf),
				p.color.b + (adjustment.z * tpf), p.color.a + (adjustment.w * tpf));
	}

	@Override
	public void initialize(ParticleData p) {
	}

	@Override
	public void reset(ParticleData p) {
	}

    @Property
	public Vector4f getAdjustment() {
		return adjustment;
	}

    @Property(label = "Adjustment", weight = 10, hint = Hint.RGBA)
    @FloatRange(min = -Float.MAX_VALUE, incr = 0.01f)
	public void setAdjustment(Vector4f adjustment) {
		this.adjustment = adjustment;
	}

	@Override
	public void write(JmeExporter ex) throws IOException {
		OutputCapsule oc = ex.getCapsule(this);
		adjustment.write(ex);
	}

	@Override
	public void read(JmeImporter im) throws IOException {
		InputCapsule ic = im.getCapsule(this);
		adjustment.read(im);
	}

	@Override
	public ParticleInfluencer clone() {
		ColourFaderAffector clone = new ColourFaderAffector(script, adjustment.clone());
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
	public Class<?> getInfluencerClass() {
		return ColourFaderAffector.class;
	}

	@Override
	protected void writeAffector(PrintWriter pw) {
		pw.println(String.format("\t\tred %1.1f", adjustment.x));
		pw.println(String.format("\t\tgreen %1.1f", adjustment.y));
		pw.println(String.format("\t\tblue %1.1f", adjustment.z));
		pw.println(String.format("\t\talpha %1.1f", adjustment.w));
		pw.flush();
	}

	public boolean parse(String[] args, int lineNumber) throws ParseException {
		if (args[0].equals("red")) {
			if (args.length == 2) {
				getAdjustment().x = Float.parseFloat(args[1]);
			} else {
				throw new ParseException("Expected float value after red at line " + lineNumber + ".", 0);
			}
		} else if (args[0].equals("green1")) {
			if (args.length == 2) {
				getAdjustment().y = Float.parseFloat(args[1]);
			} else {
				throw new ParseException("Expected float value after green at line " + lineNumber + ".", 0);
			}
		} else if (args[0].equals("blue1")) {
			if (args.length == 2) {
				getAdjustment().z = Float.parseFloat(args[1]);
			} else {
				throw new ParseException("Expected float value after blue at line " + lineNumber + ".", 0);
			}
		} else if (args[0].equals("alpha1")) {
			if (args.length == 2) {
				getAdjustment().w = Float.parseFloat(args[1]);
			} else {
				throw new ParseException("Expected float value after alpha at line " + lineNumber + ".", 0);
			}
		} else {
			return false;
		}
		return true;
	}
}
