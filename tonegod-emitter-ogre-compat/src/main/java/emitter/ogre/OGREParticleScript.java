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

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

import emitter.EmitterMesh.DirectionType;

public class OGREParticleScript {

	private final EmitterFactory emitterFactory;
	private final AffectorFactory affectorFactory;

	public enum BillboardType {
		POINT, ORIENTED_COMMON, ORIENTED_SELF, PERPENDICULAR_COMMON, PERPENDICULAR_SELF
	}

	public enum BillboardOrigin {

		TOP_LEFT, TOP_CENTER, TOP_RIGHT, CENTER_LEFT, CENTER, CENTER_RIGHT, BOTTOM_LEFT, BOTTOM_CENTER, BOTTOM_RIGHT
	}

	public enum BillboardRotation {

		VERTEX, TEXCOORD
	}

	private final static Logger LOG = Logger.getLogger(OGREParticleScript.class.getName());
	private List<OGREParticleEmitter> emitters = new ArrayList<OGREParticleEmitter>();
	private List<OGREParticleAffector> affectors = new ArrayList<OGREParticleAffector>();
	private int quota = 10;
	private boolean localSpace;
	private boolean cullEach;
	private float nonVisibleUpdateTimeout;
	// private Vector3f commonDirection = new Vector3f(0, 0, 1);
	private Vector3f commonDirection = new Vector3f(0, 0, 1);
	private Vector2f particleSize = new Vector2f(100, 100);
	private BillboardType billboardType = BillboardType.POINT;
	// private Vector3f commonUpVector = new Vector3f(0, 1, 0);
	private Vector3f commonUpVector = new Vector3f(0, 1, 0);
	private String materialName;
	private String texture;
	// DirectionRandomiser
	private String name;
	private boolean sorted;
	private boolean pointRendering;
	private boolean accurateFacing;
	private float iterationInterval;
	private final OGREParticleConfiguration configuration;
	private BillboardOrigin billboardOrigin = BillboardOrigin.CENTER;
	private BillboardRotation billboardRotation = BillboardRotation.TEXCOORD;
	private DirectionType directionType = DirectionType.Normal;

	public OGREParticleScript(String name, OGREParticleConfiguration configuration) {
		this(name, configuration, EmitterFactory.get(), AffectorFactory.get());
	}

	public OGREParticleScript(String name, OGREParticleConfiguration configuration, EmitterFactory emitterFactory,
			AffectorFactory affectorFactory) {
		this.name = name;
		this.affectorFactory = affectorFactory;
		this.configuration = configuration;
		this.emitterFactory = emitterFactory;
	}

	public List<OGREParticleAffector> getAffectors() {
		return affectors;
	}

	public float getNonVisibleUpdateTimeout() {
		return nonVisibleUpdateTimeout;
	}

	public Vector3f getCommonDirection() {
		return commonDirection;
	}

	public Vector3f getCommonUpVector() {
		return commonUpVector;
	}

	public boolean isPointRendering() {
		return pointRendering;
	}

	public boolean isAccurateFacing() {
		return accurateFacing;
	}

	public float getIterationInterval() {
		return iterationInterval;
	}

	public OGREParticleConfiguration getConfiguration() {
		return configuration;
	}

	public void setMaterialName(String materialName) {
		this.materialName = materialName;
	}

	public String getTexture() {
		return texture;
	}

	public void setTexture(String texture) {
		this.texture = texture;
	}

	public int getQuota() {
		return quota;
	}

	public boolean isLocalSpace() {
		return localSpace;
	}

	public boolean isCullEach() {
		return cullEach;
	}

	public Vector2f getParticleSize() {
		return particleSize;
	}

	public void setParticleSize(Vector2f particleSize) {
		this.particleSize = particleSize;
	}

	public DirectionType getDirectionType() {
		return directionType;
	}

	public void setDirectionType(DirectionType directionType) {
		this.directionType = directionType;
	}

	public BillboardType getBillboardType() {
		return billboardType;
	}

	public void setBillboardType(BillboardType billboardType) {
		this.billboardType = billboardType;
	}

	public String getMaterialName() {
		return materialName;
	}

	public boolean isSorted() {
		return sorted;
	}

	public BillboardOrigin getBillboardOrigin() {
		return billboardOrigin;
	}

	public void setBillboardOrigin(BillboardOrigin billboardOrigin) {
		this.billboardOrigin = billboardOrigin;
	}

	public BillboardRotation getBillboardRotation() {
		return billboardRotation;
	}

	public void setBillboardRotation(BillboardRotation billboardRotation) {
		this.billboardRotation = billboardRotation;
	}

	public void setCullEach(boolean cullEach) {
		if (cullEach) {
			LOG.info("TODO: this would cull individual particles instead of whole node. Not sure if JME can do this.");
		}
		this.cullEach = cullEach;
	}

	public void setSorted(boolean sorted) {
		this.sorted = sorted;
	}

	public void setNumParticles(int numParticles) {
		this.quota = numParticles;
	}

	public void setLocalSpace(boolean localSpace) {
		this.localSpace = localSpace;
	}

	public void setIterationInterval(float iterationInterval) {
		this.iterationInterval = iterationInterval;
	}

	public void setNonVisibleUpdateTimeout(float nonVisibleUpdateTimeout) {
		this.nonVisibleUpdateTimeout = nonVisibleUpdateTimeout;
	}

	public void setBillboardParam(String name, String value) {
		if (name.equals("type")) {
			this.billboardType = BillboardType.valueOf(value.toUpperCase());
		} else if (name.equals("origin")) {
			this.billboardOrigin = BillboardOrigin.valueOf(value.toUpperCase());
		} else if (name.equals("rotation_type")) {
			this.billboardRotation = BillboardRotation.valueOf(value.toUpperCase());
		} else {
			LOG.warning(String.format("TODO: unsupported billboard parameter %s = %s", name, value));
		}
	}

	public void setCommonDirection(Vector3f commonDirection) {
		this.commonDirection = commonDirection;
	}

	public void setCommonUpVector(Vector3f commonUpVector) {
		this.commonUpVector = commonUpVector;
	}

	public void setPointRendering(boolean pointRendering) {
		this.pointRendering = pointRendering;
	}

	public void setAccurateFacing(boolean accurateFacing) {
		this.accurateFacing = accurateFacing;
	}

	public List<OGREParticleEmitter> getEmitters() {
		return emitters;
	}

	public String getName() {
		return name;
	}

	public OGREParticleAffector createAffector(String affectorName) {
		OGREParticleAffector affector = affectorFactory.createAffector(affectorName, this);
		affectors.add(affector);
		return affector;
	}

	public OGREParticleEmitter createEmitter(String shapeName) {
		OGREParticleEmitter emitter = emitterFactory.createEmitter(shapeName, this);
		emitters.add(emitter);
		return emitter;
	}

	public void write(OutputStream fos, boolean b) throws IOException {
		LOG.fine(String.format("Writing script %s", getName()));
		PrintWriter pw = new PrintWriter(fos);
		pw.println(String.format("particle_system %s", getName()));
		pw.println("{");
		pw.println(String.format("\tmaterial %s", getMaterialName()));
		pw.println(String.format("\tparticle_width %1.1f", getParticleSize().x));
		pw.println(String.format("\tparticle_height %1.1f", getParticleSize().y));
		pw.println(String.format("\tquota %d", getQuota()));
		pw.println(String.format("\tcull_each %s", isCullEach()));
		pw.println(String.format("\tsorted %s", isSorted()));
		if (getDirectionType() != null)
			pw.println(String.format("\tdirection_type %s", getDirectionType().name().toLowerCase()));

		pw.println(String.format("\tlocal_space %s", isLocalSpace()));
		pw.println(String.format("\titeration_interval %1.1f", getIterationInterval()));
		pw.println(String.format("\titeration_interval %1.1f", getNonVisibleUpdateTimeout()));
		pw.println(String.format("\tbillboard_type %s", getBillboardType().name().toLowerCase()));
		pw.println(String.format("\tbillboard_origin %s", getBillboardOrigin().name().toLowerCase()));
		pw.println(String.format("\tbillboard_rotation %s", getBillboardRotation().name().toLowerCase()));
		pw.println(String.format("\tpoint_rendering %s", isPointRendering()));
		pw.println(String.format("\taccurate_facing %s", isAccurateFacing()));
		if (getCommonDirection() != null) {
			pw.println(String.format("\tcommon_direction %s",
					OGREParticleConfiguration.formatForWrite(getCommonDirection())));
		}
		if (getCommonUpVector() != null) {
			pw.println(String.format("\tcommon_up_vector %s",
					OGREParticleConfiguration.formatForWrite(getCommonUpVector())));
		}
		pw.println(String.format("\tlocal_space %s", isLocalSpace()));
		pw.flush();
		for (OGREParticleEmitter e : emitters) {
			pw.println();
			pw.flush();
			e.write(fos, b);
		}

		for (OGREParticleAffector a : affectors) {
			pw.println();
			pw.flush();
			a.write(fos, b);
		}

		pw.println("}");
		pw.flush();
	}
}
