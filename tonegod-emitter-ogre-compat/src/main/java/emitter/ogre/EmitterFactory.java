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
package emitter.ogre;

import java.util.logging.Logger;

import emitter.ogre.emitters.BoxEmitter;
import emitter.ogre.emitters.CylinderEmitter;
import emitter.ogre.emitters.EllipsoidEmitter;
import emitter.ogre.emitters.HollowEllipsoidEmitter;
import emitter.ogre.emitters.PointEmitter;
import emitter.ogre.emitters.RingEmitter;

public abstract class EmitterFactory {
	private static final Logger LOG = Logger.getLogger(EmitterFactory.class.getName());

	private static EmitterFactory instance;

	public static EmitterFactory get() {
		if (instance == null) {
			instance = new EmitterFactory() {
				@Override
				public OGREParticleEmitter createEmitter(String shape, OGREParticleScript script) {
					// Emitter shape
					if (shape.equalsIgnoreCase("box")) {
						return new BoxEmitter(script);
					} else if (shape.equalsIgnoreCase("cylinder")) {
						return new CylinderEmitter(script);
					} else if (shape.equalsIgnoreCase("point")) {
						return new PointEmitter(script);
					} else if (shape.equalsIgnoreCase("ring")) {
						return new RingEmitter(script);
					} else if (shape.equalsIgnoreCase("ellipsoid")) {
						return new EllipsoidEmitter(script);
					} else if (shape.replace(" ", "").equalsIgnoreCase("hollowellipsoid")) {
						return new HollowEllipsoidEmitter(script);
					} else {
						LOG.warning(String.format("TODO: unknown shape %s", shape));
						return new PointEmitter(script);
					}
				}
			};
		}
		return instance;
	}

	/**
	 * Set an alternative emitter factory.
	 *
	 * @param factory
	 *            emitter factory
	 */
	public static void set(EmitterFactory instance) {
		EmitterFactory.instance = instance;
	}

	public abstract OGREParticleEmitter createEmitter(String shape, OGREParticleScript script);
}
