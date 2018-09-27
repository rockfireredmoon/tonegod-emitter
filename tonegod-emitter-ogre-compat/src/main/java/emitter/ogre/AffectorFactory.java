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

import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AffectorFactory {

	private static final Logger LOG = Logger.getLogger(AffectorFactory.class.getName());
	private static AffectorFactory instance;

	public static AffectorFactory get() {
		if (instance == null) {
			instance = new AffectorFactory() {
				@Override
				public OGREParticleAffector createAffector(String name, OGREParticleScript script) {
					ClassLoader cl = Thread.currentThread().getContextClassLoader();
					try {
						Class<? extends OGREParticleAffector> clazz = (Class<? extends OGREParticleAffector>) Class
								.forName(
										AffectorFactory.class.getPackage().getName() + ".affectors."
												+ name.replace(" ", "") + "Affector",
										true, cl == null ? AffectorFactory.class.getClassLoader() : cl);
						return clazz.getConstructor(OGREParticleScript.class).newInstance(script);
					} catch (Exception e) {
						LOG.log(Level.SEVERE, "Failed to load affector.", e);
					}
					return null;
				}
			};
		}
		return instance;
	}

	/**
	 * Set an alternative affector factory.
	 *
	 * @param factory
	 *            affector factory
	 */
	public static void set(AffectorFactory instance) {
		AffectorFactory.instance = instance;
	}

	public abstract OGREParticleAffector createAffector(String name, OGREParticleScript script);
}
