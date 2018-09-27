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
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;

import emitter.influencers.ParticleInfluencer;
import emitter.particle.ParticleData;

/**
 * Very simple Influencer that sets the start colour either to a fixed value or
 * random colour within a range. An attempt to mimic 'colour'.
 * 'colour_range_start' and 'colour_range_end'.
 * (http://www.ogre3d.org/docs/manual/manual_37.html#colour)
 */
public class InitialColourInfluencer implements ParticleInfluencer {

	private boolean enabled = true;
	private ColorRGBA rangeEnd = ColorRGBA.White;
	private ColorRGBA rangeStart = ColorRGBA.White;

	public InitialColourInfluencer() {
	}

	public InitialColourInfluencer(ColorRGBA colour) {
		this(colour, colour);
	}

	public InitialColourInfluencer(ColorRGBA rangeStart, ColorRGBA rangeEnd) {
		this.rangeStart = rangeStart;
		this.rangeEnd = rangeEnd;
	}

	@Override
	public void update(ParticleData p, float tpf) {
	}

	@Override
	public void initialize(ParticleData p) {
		// '1f -' because otherwise '1' would never be reached, and we never
		// want the
		// colour anyway.
		if (rangeStart.equals(rangeEnd)) {
			p.color.set(rangeStart);
		} else {
			p.color.r = rangeStart.r + (FastMath.nextRandomFloat() * (rangeEnd.r - rangeStart.r));
			p.color.g = rangeStart.g + (FastMath.nextRandomFloat() * (rangeEnd.g - rangeStart.g));
			p.color.b = rangeStart.b + (FastMath.nextRandomFloat() * (rangeEnd.b - rangeStart.b));
			p.color.a = rangeStart.a + (FastMath.nextRandomFloat() * (rangeEnd.a - rangeStart.a));
		}
	}

	@Override
	public void reset(ParticleData p) {
	}

	public ColorRGBA getColour() {
		return rangeStart;
	}

	public void setColour(ColorRGBA colour) {
		rangeStart = rangeEnd = colour;
	}

	public ColorRGBA getRangeEnd() {
		return rangeEnd;
	}

	public void setRangeEnd(ColorRGBA rangeEnd) {
		this.rangeEnd = rangeEnd;
	}

	public ColorRGBA getRangeStart() {
		return rangeStart;
	}

	public void setRangeStart(ColorRGBA rangeStart) {
		this.rangeStart = rangeStart;
	}

	@Override
	public void write(JmeExporter ex) throws IOException {
		OutputCapsule oc = ex.getCapsule(this);
		if (rangeStart != null) {
			rangeStart.write(ex);
		}
		if (rangeEnd != null) {
			rangeEnd.write(ex);
		}
		ColorRGBA c;
	}

	@Override
	public void read(JmeImporter im) throws IOException {
		// TODO
	}

	@Override
	public ParticleInfluencer clone() {
		try {
			InitialColourInfluencer clone = (InitialColourInfluencer) super.clone();
			clone.setRangeStart(rangeStart.clone());
			clone.setRangeEnd(rangeEnd.clone());
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
		return InitialColourInfluencer.class;
	}
}
