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

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.FastMath;

import emitter.influencers.ParticleInfluencer;
import emitter.ogre.AbstractOGREParticleAffector;
import emitter.ogre.OGREParticleScript;
import emitter.particle.ParticleData;

/**
 * Influencer that mimics OGRE's Rotator Affector
 * (http://www.ogre3d.org/docs/manual/manual_40.html#Rotator-Affector).
 */
public class RotatorAffector extends AbstractOGREParticleAffector {

	private boolean enabled = true;
	private float speedRangeStart;
	private float speedRangeEnd;
	private float rangeStart;
	private float rangeEnd;

	public RotatorAffector(OGREParticleScript script) {
		super(script);
	}

	@Override
	public void update(ParticleData p, float tpf) {
		if (enabled) {
			p.angles.set(0, 0, p.angles.z + (tpf * p.rotationSpeed.z));
		}
	}

	@Override
	public void initialize(ParticleData p) {
		// Pick a random rotation speed and start in the ranges given
		float rotSpeed = speedRangeStart + ((speedRangeEnd - speedRangeStart) * FastMath.rand.nextFloat());
		float rotAngle = rangeStart + ((rangeEnd - rangeStart) * FastMath.rand.nextFloat());

		p.angles.set(0, 0, rotAngle);
		p.rotationSpeed.set(0, 0, rotSpeed);
	}

	@Override
	public void reset(ParticleData p) {
	}

	@Property(label = "Speed Start", weight = 10)
	@FloatRange(min = -Float.MAX_VALUE)
	public float getSpeedRangeStart() {
		return speedRangeStart;
	}

	@Property(label = "Speed End", weight = 20)
	@FloatRange(min = -Float.MAX_VALUE)
	public float getSpeedRangeEnd() {
		return speedRangeEnd;
	}

	@Property(label = "Range Start", weight = 20, hint = Property.Hint.ANGLE)
	@FloatRange(min = -Float.MAX_VALUE)
	public float getRangeStart() {
		return rangeStart;
	}

	@Property(label = "Range End", weight = 20, hint = Property.Hint.ANGLE)
	@FloatRange(min = -Float.MAX_VALUE)
	public float getRangeEnd() {
		return rangeEnd;
	}

	@Property
	public void setSpeedRangeStart(float speedRangeStart) {
		this.speedRangeStart = speedRangeStart;
	}

	@Property
	public void setSpeedRangeEnd(float speedRangeEnd) {
		this.speedRangeEnd = speedRangeEnd;
	}

	@Property
	public void setRangeStart(float rangeStart) {
		this.rangeStart = rangeStart;
	}

	@Property
	public void setRangeEnd(float rangeEnd) {
		this.rangeEnd = rangeEnd;
	}

	@Override
	public void write(JmeExporter ex) throws IOException {
		OutputCapsule oc = ex.getCapsule(this);
		oc.write(speedRangeStart, "speedRangeStart", 0);
		oc.write(speedRangeEnd, "speedRangeEnd", 0);
		oc.write(rangeStart, "rangeStart", 0);
		oc.write(rangeEnd, "rangeEnd", 0);
	}

	@Override
	public void read(JmeImporter im) throws IOException {
		InputCapsule ic = im.getCapsule(this);
		speedRangeStart = ic.readFloat("speedRangeStart", 0f);
		speedRangeEnd = ic.readFloat("speedRangeEnd", 0f);
		rangeStart = ic.readFloat("rangeStart", 0f);
		rangeEnd = ic.readFloat("rangeEnd", 0f);
	}

	@Override
	public ParticleInfluencer clone() {
		RotatorAffector clone = new RotatorAffector(script);
		clone.setSpeedRangeStart(speedRangeStart);
		clone.setSpeedRangeEnd(speedRangeEnd);
		clone.setRangeStart(rangeStart);
		clone.setRangeEnd(rangeEnd);
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
		return RotatorAffector.class;
	}

	@Override
	protected void writeAffector(PrintWriter pw) {
		pw.println(String.format("\t\trotation_speed_range_start %1.1f", speedRangeStart));
		pw.println(String.format("\t\trotation_speed_range_end %1.1f", speedRangeEnd));
		pw.println(String.format("\t\trotation_range_start %1.1f", rangeStart));
		pw.println(String.format("\t\trotation_range_end %1.1f", rangeEnd));
		pw.flush();
	}

	public boolean parse(String[] args, int lineNumber) throws ParseException {
		if (args[0].equals("rotation_speed_range_start")) {
			if (args.length == 2) {
				setSpeedRangeStart(Float.parseFloat(args[1]) * FastMath.DEG_TO_RAD);
			} else {
				throw new ParseException(
						"Expected single rotation_speed_range_start for rotator at line " + lineNumber + ".", 0);
			}
		} else if (args[0].equals("rotation_speed_range_end")) {
			if (args.length == 2) {
				setSpeedRangeEnd(Float.parseFloat(args[1]) * FastMath.DEG_TO_RAD);
			} else {
				throw new ParseException(
						"Expected single rotation_speed_range_end for rotator at line " + lineNumber + ".", 0);
			}
		} else if (args[0].equals("rotation_range_start")) {
			if (args.length == 2) {
				setRangeStart(Float.parseFloat(args[1]) * FastMath.DEG_TO_RAD);
			} else {
				throw new ParseException("Expected single rotation_range_start for rotator at line " + lineNumber + ".",
						0);
			}
		} else if (args[0].equals("rotation_range_end")) {
			if (args.length == 2) {
				setRangeEnd(Float.parseFloat(args[1]) * FastMath.DEG_TO_RAD);
			} else {
				throw new ParseException("Expected single rotation_range_end for rotator at line " + lineNumber + ".",
						0);
			}
		} else {
			return false;
		}
		return true;
	}
}
