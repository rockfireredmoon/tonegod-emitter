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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.icebeans.Property;
import org.icebeans.Property.Hint;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.ColorRGBA;

import emitter.influencers.ParticleInfluencer;
import emitter.ogre.AbstractOGREParticleAffector;
import emitter.ogre.OGREParticleConfiguration;
import emitter.ogre.OGREParticleScript;
import emitter.particle.ParticleData;

/**
 * Attempt to mimic ColourInterpolator Affector
 * (http://www.ogre3d.org/docs/manual/manual_40.html#ColourInterpolator-Affector)
 */
public class ColourInterpolatorAffector extends AbstractOGREParticleAffector {

	final static int MAX_STAGES = 6;
	private boolean enabled = true;
	private List<Float> times = new ArrayList<Float>();
	private List<ColorRGBA> colours = new ArrayList<ColorRGBA>();

	public ColourInterpolatorAffector(OGREParticleScript script) {
		super(script);
		for (int i = 0; i < MAX_STAGES; i++) {
			colours.add(new ColorRGBA(0.5f, 0.5f, 0.5f, 0.0f));
			times.add(1f);
		}
	}

	@Override
	public void update(ParticleData p, float tpf) {
		float lifeTime = p.startlife;
		float timeToLive = p.life;
		float particleTime = 1.0f - (timeToLive / lifeTime);
		ColorRGBA col;
		ColorRGBA col2;
		if (particleTime <= times.get(0)) {
			p.color.set(colours.get(0));
		} else if (particleTime >= times.get(times.size() - 1)) {
			p.color.set(colours.get(times.size() - 1));
		} else {
			for (int i = 0; i < times.size() - 1; i++) {
				if (particleTime >= times.get(i) && particleTime < times.get(i + 1)) {
					particleTime -= times.get(i);
					particleTime /= times.get(i + 1) - times.get(i);
					col = colours.get(i);
					col2 = colours.get(i + 1);
					p.color.set((col2.r * particleTime) + (col.r * (1.0f - particleTime)),
							(col2.g * particleTime) + (col.g * (1.0f - particleTime)),
							(col2.b * particleTime) + (col.b * (1.0f - particleTime)),
							(col2.a * particleTime) + (col.a * (1.0f - particleTime)));
					break;
				}
			}
		}
	}

	@Override
	public void initialize(ParticleData p) {
		p.setData("totalTimeToLive", 0);
		p.setData("timeToLive", 0);
	}

	@Override
	public void reset(ParticleData p) {
		p.setData("totalTimeToLive", 0);
		p.setData("timeToLive", 0);
	}

	@Override
	public void write(JmeExporter ex) throws IOException {
		OutputCapsule oc = ex.getCapsule(this);
	}

	@Override
	public void read(JmeImporter im) throws IOException {
		InputCapsule ic = im.getCapsule(this);
	}

	@Override
	public ParticleInfluencer clone() {
		ColourInterpolatorAffector clone = new ColourInterpolatorAffector(script);
		for (ColorRGBA a : colours) {
			clone.colours.add(a.clone());
		}
		for (Float f : times) {
			clone.times.add(f);
		}
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
		return ColourInterpolatorAffector.class;
	}

	public void setTime(int index, float time) {
		times.set(index, time);
	}

	public void setColour(int index, ColorRGBA parseColour) {
		colours.get(index).set(parseColour);
	}

    @Property(label = "Colour 0", weight = 10)
	public void setColour0(ColorRGBA color) {
		setColour(0, color);
	}
    
    @Property
	public ColorRGBA getColour0() {
		return colours.get(0);
	}

    @Property(label = "Time 0", weight = 20, hint = Hint.SMALL_SECOND_TIME)
	public void setTime0(float time) {
		setTime(0, time);
	}

    @Property
	public float getTime0() {
		return times.get(0);
	}

    @Property(label = "Colour 1", weight = 30)
	public void setColour1(ColorRGBA color) {
		setColour(1, color);
	}

    @Property
	public ColorRGBA getColour1() {
		return colours.get(1);
	}

    @Property(label = "Time 1", weight = 40, hint = Hint.SMALL_SECOND_TIME)
	public void setTime1(float time) {
		setTime(1, time);
	}

    @Property
	public float getTime1() {
		return times.get(1);
	}

    @Property(label = "Colour 2", weight = 50)
	public void setColour2(ColorRGBA color) {
		setColour(2, color);
	}

    @Property
	public ColorRGBA getColour2() {
		return colours.get(2);
	}

    @Property(label = "Time 2", weight = 60, hint = Hint.SMALL_SECOND_TIME)
	public void setTime2(float time) {
		setTime(2, time);
	}

    @Property
	public float getTime2() {
		return times.get(2);
	}

    @Property(label = "Colour3", weight = 70)
	public void setColour3(ColorRGBA color) {
		setColour(3, color);
	}

    @Property
	public ColorRGBA getColour3() {
		return colours.get(3);
	}

    @Property(label = "Time 3", weight = 80, hint = Hint.SMALL_SECOND_TIME)
	public void setTime3(float time) {
		setTime(3, time);
	}
    
    @Property
	public float getTime3() {
		return times.get(3);
	}

    @Property(label = "Colour4", weight = 90)
	public void setColour4(ColorRGBA color) {
		setColour(4, color);
	}

    @Property
	public ColorRGBA getColour4() {
		return colours.get(4);
	}

    @Property(label = "Time 4", weight = 100, hint = Hint.SMALL_SECOND_TIME)
	public void setTime4(float time) {
		setTime(4, time);
	}

    @Property
	public float getTime4() {
		return times.get(4);
	}

    @Property(label = "Colour 5", weight = 110)
	public void setColour5(ColorRGBA color) {
		setColour(5, color);
	}

    @Property
	public ColorRGBA getColour5() {
		return colours.get(5);
	}

    @Property(label = "Time 5", weight = 120, hint = Hint.SMALL_SECOND_TIME)
	public void setTime5(float time) {
		setTime(5, time);
	}

    @Property
	public float getTime5() {
		return times.get(5);
	}

	@Override
	protected void writeAffector(PrintWriter pw) {
		int i = 0;
		Iterator<ColorRGBA> colIt = colours.iterator();
		Iterator<Float> timeIt = times.iterator();
		while (colIt.hasNext() && timeIt.hasNext()) {
			pw.println(String.format("\t\tcolour%d %s", i, OGREParticleConfiguration.formatForWrite(colIt.next())));
			pw.println(String.format("\t\ttime%d %1.1f", i, timeIt.next()));
			i++;
		}
		pw.flush();
	}

	public boolean parse(String[] args, int lineNumber) throws ParseException {
		if (args[0].startsWith("time")) {
			if (args.length == 2) {
				setTime(Integer.parseInt(args[0].substring(4)), Float.parseFloat(args[1]));
			} else {
				throw new ParseException("Expected float value after " + args[0] + " at line " + lineNumber + ".", 0);
			}
		} else if (args[0].startsWith("colour")) {
			if (args.length >= 4) {
				setColour(Integer.parseInt(args[0].substring(6)), OGREParticleConfiguration.parseColour(args));
			} else {
				throw new ParseException("Expected colour value after " + args[0] + " at line " + lineNumber + ".", 0);
			}
		} else {
			return false;
		}
		return true;
	}
}
