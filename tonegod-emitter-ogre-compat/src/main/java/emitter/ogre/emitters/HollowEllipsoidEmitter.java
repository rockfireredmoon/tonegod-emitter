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
package emitter.ogre.emitters;

import java.io.PrintWriter;
import java.text.ParseException;
import java.util.logging.Logger;

import com.jme3.math.Vector3f;
import com.jme3.scene.shape.Torus;

import emitter.Emitter;
import emitter.ogre.OGREParticleScript;

public class HollowEllipsoidEmitter extends Abstract3DEmitter {

	private static final Logger LOG = Logger.getLogger(HollowEllipsoidEmitter.class.getName());
	private Vector3f innerSize = new Vector3f(0.5f, 0.5f, 0.5f);

	public HollowEllipsoidEmitter(OGREParticleScript group) {
		super(group);
	}

	public Vector3f getInnerSize() {
		return innerSize;
	}

	public void setInnerSize(Vector3f innerSize) {
		this.innerSize = innerSize;
	}

	@Override
	public boolean parse(String[] args, int lineNo) throws ParseException {
		boolean ok = super.parse(args, lineNo);
		if (!ok) {
			if (args[0].equals("inner_height")) {
				if (args.length == 2) {
					innerSize.y = (Float.parseFloat(args[1]));
					ok = true;
				} else {
					throw new ParseException("Expected single inner_height at line " + lineNo + ".", 0);
				}
			} else if (args[0].equals("inner_width")) {
				if (args.length == 2) {
					innerSize.x = (Float.parseFloat(args[1]));
					ok = true;
				} else {
					throw new ParseException("Expected single inner_width at line " + lineNo + ".", 0);
				}
			} else if (args[0].equals("inner_depth")) {
				if (args.length == 2) {
					innerSize.z = (Float.parseFloat(args[1]));
					ok = true;
				} else {
					throw new ParseException("Expected single inner_depth at line " + lineNo + ".", 0);
				}
			}
		}
		return ok;
	}

	@Override
	protected void createEmitterShape(Emitter emitter) {
		LOG.info("    Tri Mesh");
		emitter.setUseRandomEmissionPoint(true);

		float fac = innerSize == null ? 0.25f : innerSize.x / size.x / 2f;

		Torus torus = new Torus(32, 32, fac, 0.5f);
		LOG.info(String.format("    Hollow Ellipsoid emitter shape of %f x %f x %f", size.x, size.y, size.z));
		emitter.setShape(torus);
		emitter.setLocalScale(size);
	}

	@Override
	protected void writeEmitter(PrintWriter pw) {
		super.writeEmitter(pw);
		pw.println(String.format("\t\tinner_width %1.1f", innerSize.x));
		pw.println(String.format("\t\tinner_height %1.1f", innerSize.y));
		pw.println(String.format("\t\tinner_depth %1.1f", innerSize.z));
	}
}
