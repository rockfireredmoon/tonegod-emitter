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

import org.icebeans.FloatRange;
import org.icebeans.Property;

import com.jme3.math.Vector3f;

import emitter.ogre.AbstractOGREParticleEmitter;
import emitter.ogre.OGREParticleScript;

public abstract class Abstract3DEmitter extends AbstractOGREParticleEmitter {

	protected Vector3f size = new Vector3f(1, 1, 1);

	public Abstract3DEmitter(OGREParticleScript group) {
		super(group);
	}

    @Property(label = "Size", weight = 15, hint = Property.Hint.SCALE)
    @FloatRange(incr = 0.1f, precision = 3)
	public Vector3f getSize() {
		return size;
	}

	@Property
	public void setSize(Vector3f size) {
		this.size = size;
	}

	public boolean parse(String[] args, int lineNo) throws ParseException {
		if (args[0].equals("height")) {
			if (args.length == 2) {
				size.x = (Float.parseFloat(args[1]));
				return true;
			} else {
				throw new ParseException("Expected single height at line " + lineNo + ".", 0);
			}
		} else if (args[0].equals("width")) {
			if (args.length == 2) {
				size.y = (Float.parseFloat(args[1]));
				return true;
			} else {
				throw new ParseException("Expected single width at line " + lineNo + ".", 0);
			}
		} else if (args[0].equals("depth")) {
			if (args.length == 2) {
				size.z = (Float.parseFloat(args[1]));
				return true;
			} else {
				throw new ParseException("Expected single width at line " + lineNo + ".", 0);
			}
		}
		return false;
	}

	@Override
	protected void writeEmitter(PrintWriter pw) {
		pw.println(String.format("\t\twidth %1.1f", size.x));
		pw.println(String.format("\t\theight %1.1f", size.y));
		pw.println(String.format("\t\tdepth %1.1f", size.z));
	}
}
