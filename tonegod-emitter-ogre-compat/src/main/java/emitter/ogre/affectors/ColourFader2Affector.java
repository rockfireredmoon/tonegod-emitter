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

import emitter.ogre.AbstractOGREParticleAffector;
import emitter.ogre.OGREParticleAffector;
import emitter.ogre.OGREParticleScript;
import emitter.particle.ParticleData;

/**
 * Attempt to mimic ColourFader Affector
 * (http://www.ogre3d.org/docs/manual/manual_40.html#ColourFader-Affector)
 */
public class ColourFader2Affector extends AbstractOGREParticleAffector {

	private boolean enabled = true;
	private Vector4f adjustment1 = new Vector4f(0, 0, 0, 0);
	private Vector4f adjustment2 = new Vector4f(0, 0, 0, 0);
	private float stateChange = 1;

	public ColourFader2Affector(OGREParticleScript script) {
		super(script);
	}

	public ColourFader2Affector(OGREParticleScript script, Vector4f adjustment1, Vector4f adjustment2) {
		super(script);
		this.adjustment1 = adjustment1;
		this.adjustment2 = adjustment2;
	}

	@Override
	public void update(ParticleData p, float tpf) {
		float aliveFor = p.startlife - p.life;
		if (aliveFor > stateChange) {
			p.color.set(p.color.r + (adjustment2.x * tpf), p.color.g + (adjustment2.y * tpf),
					p.color.b + (adjustment2.w * tpf), p.color.a + (adjustment2.z * tpf)).clamp();
		} else {
			p.color.set(p.color.r + (adjustment1.x * tpf), p.color.g + (adjustment1.y * tpf),
					p.color.b + (adjustment1.z * tpf), p.color.a + (adjustment1.w * tpf)).clamp();
		}
	}

	@Override
	public void initialize(ParticleData p) {
	}

	@Override
	public void reset(ParticleData p) {
		p.setData("aliveTime", 0f);
	}

    @Property(label = "Adjustment 1", weight = 10, hint = Hint.RGBA)
    @FloatRange(min = -Float.MAX_VALUE, incr = 0.01f)
	public Vector4f getAdjustment1() {
		return adjustment1;
	}

    @Property
	public void setAdjustment1(Vector4f adjustment1) {
		this.adjustment1 = adjustment1;
	}

    @Property(label = "State Change", weight = 20)
	public float getStateChange() {
		return stateChange;
	}

    @Property
	public void setStateChange(float stateChange) {
		this.stateChange = stateChange;
	}

    @Property(label = "Adjustment 2", weight = 30, hint = Hint.RGBA)
    @FloatRange(min = -Float.MAX_VALUE, incr = 0.01f)
	public Vector4f getAdjustment2() {
		return adjustment2;
	}

    @Property
	public void setAdjustment2(Vector4f adjustment2) {
		this.adjustment2 = adjustment2;
	}

	@Override
	public void write(JmeExporter ex) throws IOException {
		OutputCapsule oc = ex.getCapsule(this);
		adjustment1.write(ex);
		adjustment2.write(ex);
	}

	@Override
	public void read(JmeImporter im) throws IOException {
		InputCapsule ic = im.getCapsule(this);
		adjustment1.read(im);
		adjustment2.read(im);
	}

	@Override
	public OGREParticleAffector clone() {
		ColourFader2Affector clone = new ColourFader2Affector(script, adjustment1.clone(), adjustment2.clone());
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
		return ColourFader2Affector.class;
	}

	@Override
	protected void writeAffector(PrintWriter pw) {
		pw.println(String.format("\t\tred1 %1.1f", adjustment1.x));
		pw.println(String.format("\t\tgreen1 %1.1f", adjustment1.y));
		pw.println(String.format("\t\tblue1 %1.1f", adjustment1.z));
		pw.println(String.format("\t\talpha1 %1.1f", adjustment1.w));
		pw.println(String.format("\t\tred2 %1.1f", adjustment2.x));
		pw.println(String.format("\t\tgreen2 %1.1f", adjustment2.y));
		pw.println(String.format("\t\tblue2 %1.1f", adjustment2.z));
		pw.println(String.format("\t\talpha2 %1.1f", adjustment2.w));
		pw.println(String.format("\t\tstate_change %1.1f", stateChange));
		pw.flush();
	}

	public boolean parse(String[] args, int lineNumber) throws ParseException {
		if (args[0].equals("red1")) {
			if (args.length == 2) {
				getAdjustment1().x = Float.parseFloat(args[1]);
			} else {
				throw new ParseException("Expected float value after red1 at line " + lineNumber + ".", 0);
			}
		} else if (args[0].equals("green1")) {
			if (args.length == 2) {
				getAdjustment1().y = Float.parseFloat(args[1]);
			} else {
				throw new ParseException("Expected float value after green1 at line " + lineNumber + ".", 0);
			}
		} else if (args[0].equals("blue1")) {
			if (args.length == 2) {
				getAdjustment1().z = Float.parseFloat(args[1]);
			} else {
				throw new ParseException("Expected float value after blue1 at line " + lineNumber + ".", 0);
			}
		} else if (args[0].equals("alpha1")) {
			if (args.length == 2) {
				getAdjustment1().w = Float.parseFloat(args[1]);
			} else {
				throw new ParseException("Expected float value after alpha1 at line " + lineNumber + ".", 0);
			}
		} else if (args[0].equals("red2")) {
			if (args.length == 2) {
				getAdjustment2().x = Float.parseFloat(args[1]);
			} else {
				throw new ParseException("Expected float value after red2 at line " + lineNumber + ".", 0);
			}
		} else if (args[0].equals("green2")) {
			if (args.length == 2) {
				getAdjustment2().y = Float.parseFloat(args[1]);
			} else {
				throw new ParseException("Expected float value after green2 at line " + lineNumber + ".", 0);
			}
		} else if (args[0].equals("blue2")) {
			if (args.length == 2) {
				getAdjustment2().z = Float.parseFloat(args[1]);
			} else {
				throw new ParseException("Expected float value after blue2 at line " + lineNumber + ".", 0);
			}
		} else if (args[0].equals("alpha2")) {
			if (args.length == 2) {
				getAdjustment2().w = Float.parseFloat(args[1]);
			} else {
				throw new ParseException("Expected float value after alpha2 at line " + lineNumber + ".", 0);
			}
		} else if (args[0].equals("state_change")) {
			if (args.length == 2) {
				setStateChange(Float.parseFloat(args[1]));
			} else {
				throw new ParseException("Expected float value after state_change at line " + lineNumber + ".", 0);
			}
		} else {
			return false;
		}
		return true;
	}
}
