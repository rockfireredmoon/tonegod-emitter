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
import java.nio.ByteBuffer;
import java.text.ParseException;

import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.texture.Image;
import com.jme3.texture.Image.Format;
import com.jme3.texture.Texture;

import emitter.influencers.ParticleInfluencer;
import emitter.ogre.AbstractOGREParticleAffector;
import emitter.ogre.OGREParticleScript;
import emitter.particle.ParticleData;

/**
 */
public class ColourImageAffector extends AbstractOGREParticleAffector {

	private boolean enabled = true;
	private String image;
	private Image bitmap;
	private int x = 0;
	private ByteBuffer buf;

	public ColourImageAffector(OGREParticleScript script) {
		super(script);
	}

	public ColourImageAffector(OGREParticleScript script, String image) {
		super(script);
		this.image = image;
	}

	@Override
	public void update(ParticleData p, float tpf) {
		if (x == bitmap.getWidth()) {
			buf.rewind();
			x = 0;
		}
		if (bitmap.getFormat() == Format.RGBA8) {
			p.color.r = (float) (buf.get() & 0xff) / 255f;
			p.color.g = (float) (buf.get() & 0xff) / 255f;
			p.color.b = (float) (buf.get() & 0xff) / 255f;
			p.color.a = (float) (buf.get() & 0xff) / 255f;
			x++;
		} else if (bitmap.getFormat() == Format.BGR8) {
			p.color.b = (float) (buf.get() & 0xff) / 255f;
			p.color.g = (float) (buf.get() & 0xff) / 255f;
			p.color.r = (float) (buf.get() & 0xff) / 255f;
			p.color.a = 1;
			x++;
		} else {
			throw new RuntimeException(String.format("Unsupported image format %s", bitmap.getFormat()));
		}
	}

	@Override
	public void initialize(ParticleData p) {
		Texture loadTexture = p.emitter.getAssetManager().loadTexture("Effects/" + image);
		bitmap = loadTexture.getImage();
		buf = bitmap.getData(0);
		buf.rewind();
		System.out.println("BUFLE: " + buf.limit() + " WID: " + bitmap.getWidth());
	}

	@Override
	public void reset(ParticleData p) {
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	@Override
	public void write(JmeExporter ex) throws IOException {
	}

	@Override
	public void read(JmeImporter im) throws IOException {
	}

	@Override
	public ParticleInfluencer clone() {
		ColourImageAffector clone = new ColourImageAffector(script, image);
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
		return ColourImageAffector.class;
	}

	@Override
	protected void writeAffector(PrintWriter pw) {
		pw.println(String.format("\t\timage %s", image));
		pw.flush();
	}

	public boolean parse(String[] args, int lineNumber) throws ParseException {
		if (args[0].equals("image")) {
			if (args.length == 2) {
				image = args[1];
			} else {
				throw new ParseException("Expected texture path value after image at line " + lineNumber + ".", 0);
			}
		} else {
			return false;
		}
		return true;
	}
}
