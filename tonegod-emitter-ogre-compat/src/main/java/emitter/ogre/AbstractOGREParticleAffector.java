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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import emitter.influencers.ParticleInfluencer;

public abstract class AbstractOGREParticleAffector implements OGREParticleAffector {

	protected transient PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
	protected final OGREParticleScript script;

	public AbstractOGREParticleAffector(OGREParticleScript script) {
		this.script = script;
	}

	public OGREParticleScript getScript() {
		return script;
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		changeSupport.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		changeSupport.removePropertyChangeListener(listener);
	}

	@Override
	public abstract ParticleInfluencer clone();

	public String getName() {
		String cn = getClass().getSimpleName();
		if (cn.endsWith("Affector")) {
			cn = cn.substring(0, cn.length() - 8);
		}
		return cn;
	}

	public void write(OutputStream fos, boolean b) throws IOException {
		PrintWriter pw = new PrintWriter(fos);
		pw.println(String.format("\taffector %s", getName()));
		pw.println("\t{");
		writeAffector(pw);
		pw.println("\t}");
		pw.flush();
	}

	protected abstract void writeAffector(PrintWriter pw);
}
