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

import static emitter.ogre.OGREParticleConfiguration.parseVector3f;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.Vector3f;

import emitter.influencers.ParticleInfluencer;
import emitter.ogre.AbstractOGREParticleAffector;
import emitter.ogre.OGREParticleConfiguration;
import emitter.ogre.OGREParticleScript;
import emitter.particle.ParticleData;

/**
 * Attempt to mimic Linear Force Affector
 * (http://www.ogre3d.org/docs/manual/manual_40.html#Linear-Force-Affector)
 */
public class LinearForceAffector extends AbstractOGREParticleAffector {

	public enum Application {

		AVERAGE, ADD
	}

	private boolean enabled = true;
	private Vector3f force = new Vector3f(0, -100f, 0);
	private Application application = Application.ADD;

	public LinearForceAffector(OGREParticleScript script) {
		super(script);
	}

	public LinearForceAffector(OGREParticleScript script, Vector3f force, Application application) {
		super(script);
		this.force = force;
		this.application = application;
	}

	@Override
	public void update(ParticleData p, float tpf) {
		if (enabled) {
			switch (application) {
			case ADD:
				p.velocity.addLocal(force.mult(tpf));
				break;
			case AVERAGE:
				p.velocity.set(p.velocity.add(force).divideLocal(2f));
				break;
			}
		}
	}

	@Override
	public void initialize(ParticleData p) {
	}

	@Override
	public void reset(ParticleData p) {
	}

	public Vector3f getForce() {
		return force;
	}

	public void setForce(Vector3f force) {
		this.force = force;
	}

	public Application getApplication() {
		return application;
	}

	public void setApplication(Application application) {
		this.application = application;
	}

	@Override
	public void write(JmeExporter ex) throws IOException {
		OutputCapsule oc = ex.getCapsule(this);
		force.write(ex);
		oc.write(application.name(), "application", Application.ADD.name());
	}

	@Override
	public void read(JmeImporter im) throws IOException {
		InputCapsule ic = im.getCapsule(this);
		application = Application.valueOf(ic.readString("application", Application.ADD.name()));
		force.read(im);
	}

	@Override
	public ParticleInfluencer clone() {
		LinearForceAffector clone = new LinearForceAffector(script);
		clone.setApplication(application);
		clone.setForce(force.clone());
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
		return LinearForceAffector.class;
	}

	@Override
	protected void writeAffector(PrintWriter pw) {
		pw.println(String.format("\t\tforce_vector %s", OGREParticleConfiguration.formatForWrite(force)));
		pw.println(String.format("\t\tadd %s", application.name().toLowerCase()));
		pw.flush();
	}

	public boolean parse(String[] args, int lineNumber) throws ParseException {
		if (args[0].equals("force_vector")) {
			setForce(parseVector3f(args));
		} else if (args[0].equals("force_application")) {
			if (args.length == 2) {
				if (args[1].equals("add")) {
					// this is "traditional" apparently
					setApplication(LinearForceAffector.Application.ADD);
				} else if (args[1].equals("average")) {
					setApplication(LinearForceAffector.Application.AVERAGE);
				} else {
					throw new ParseException("Expected value of average or add at line " + lineNumber, 0);
				}
			} else {
				throw new ParseException(
						"Expected either add or average for force_application at line " + lineNumber + ".", 0);
			}
		} else {
			return false;
		}
		return true;
	}
}
