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

import java.util.logging.Logger;

import com.jme3.scene.shape.Cylinder;

import emitter.Emitter;
import emitter.ogre.OGREParticleScript;
import emitter.particle.ParticleDataTriMesh;

public class CylinderEmitter extends Abstract3DEmitter {

	private static final Logger LOG = Logger.getLogger(CylinderEmitter.class.getName());

	public CylinderEmitter(OGREParticleScript group) {
		super(group);
	}

	@Override
	protected void createEmitterShape(Emitter emitter) {
		LOG.info("    Tri Mesh");
		emitter.setParticleType(ParticleDataTriMesh.class);
		Cylinder boxTestES = new Cylinder(8, 16, 0.5f, 0.5f, 0.5f, true, false);
		emitter.setShape(boxTestES);
		emitter.setUseRandomEmissionPoint(true);
		emitter.setLocalScale(size.x, size.y, Math.max(0.1f, size.z));
		LOG.info(String.format("    Cyclinder emitter shape of %f x %f x %f", size.x, size.y, size.z));
	}
}
