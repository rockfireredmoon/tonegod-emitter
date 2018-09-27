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
import java.util.logging.Logger;

import com.jme3.asset.AssetManager;
import com.jme3.asset.AssetNotFoundException;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

import emitter.Emitter;
import emitter.influencers.ParticleInfluencer;
import emitter.ogre.influencers.AngleInfluencer;
import emitter.ogre.influencers.DirectionInfluencer;
import emitter.ogre.influencers.InitialColourInfluencer;
import emitter.ogre.influencers.InitialSizeInfluencer;
import emitter.particle.ParticleDataPointMesh;
import emitter.particle.ParticleDataTriMesh;

public abstract class AbstractOGREParticleEmitter implements OGREParticleEmitter {

	protected transient PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
	public final static Quaternion X_HALF_PI = new Quaternion(new float[] { FastMath.HALF_PI, 0, 0 });
	private final static Logger LOG = Logger.getLogger(AbstractOGREParticleEmitter.class.getName());
	private float repeatDelayMin = 0;
	private float repeatDelayMax = 0;
	private float durationMax = 0;
	private float durationMin = 0;
	private final OGREParticleScript script;
	private Vector3f localTranslation = new Vector3f();
	private Vector3f direction = new Vector3f(1, 0, 0);
	private float velocityMax = 1f;
	private float velocityMin = 1f;
	private float particlesPerSec = 10;
	private float highLife = 5f;
	private float lowLife = 5f;
	private float angle = Float.MIN_VALUE;
	private boolean durationSet;
	private ColorRGBA endColour = ColorRGBA.White;
	private ColorRGBA startColour = ColorRGBA.White;

	public AbstractOGREParticleEmitter(OGREParticleScript script) {
		this.script = script;
	}

	@Override
	public AbstractOGREParticleEmitter clone() {
		throw new UnsupportedOperationException();
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		changeSupport.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		changeSupport.removePropertyChangeListener(listener);
	}

	public void setParticlesPerSec(float particlesPerSec) {
		this.particlesPerSec = particlesPerSec;
	}

	public void setLocalTranslation(Vector3f localTranslation) {
		this.localTranslation = localTranslation;
	}

	public void setDirection(Vector3f direction) {
		this.direction = direction;
	}

	public void setAngle(float angle) {
		this.angle = angle;
	}

	public void setVelocity(float velocity) {
		velocityMin = velocityMax = velocity;
	}

	public void setVelocityMin(float velocityMin) {
		this.velocityMin = velocityMin;
	}

	public void setVelocityMax(float velocityMax) {
		this.velocityMax = velocityMax;
	}

	public void setTimeToLive(float timeToLive) {
		setHighLife(timeToLive);
		setLowLife(timeToLive);
	}

	public void setLowLife(float lowLife) {
		this.lowLife = lowLife;
	}

	public void setHighLife(float highLife) {
		this.highLife = highLife;
	}

	public void setDuration(float duration) {
		this.durationMin = duration;
		this.durationMax = duration;
		durationSet = true;
	}

	public void setDurationMin(float durationMin) {
		this.durationMin = durationMin;
		durationSet = true;
	}

	public void setDurationMax(float durationMax) {
		this.durationMax = durationMax;
		durationSet = true;
	}

	public void setRepeatDelay(float repeatDelay) {
		this.repeatDelayMax = repeatDelay;
		this.repeatDelayMin = repeatDelay;
	}

	public void setRepeatDelayMin(float repeatDelayMin) {
		this.repeatDelayMin = repeatDelayMin;
	}

	public void setRepeatDelayMax(float repeatDelayMax) {
		this.repeatDelayMax = repeatDelayMax;
	}

	public float getParticlesPerSec() {
		return particlesPerSec;
	}

	public Vector3f getLocalTranslation() {
		return localTranslation;
	}

	public Vector3f getDirection() {
		return direction;
	}

	public float getAngle() {
		return angle;
	}

	public float getVelocityMin() {
		return velocityMin;
	}

	public float getVelocityMax() {
		return velocityMax;
	}

	public float getLowLife() {
		return lowLife;
	}

	public float getHighLife() {
		return highLife;
	}

	public float getDurationMin() {
		return durationMin;
	}

	public float getDurationMax() {
		return durationMax;
	}

	public float getRepeatDelayMax() {
		return repeatDelayMax;
	}

	public float getRepeatDelayMin() {
		return repeatDelayMin;
	}

	public ColorRGBA getStartColour() {
		return startColour;
	}

	public ColorRGBA getEndColour() {
		return endColour;
	}

	public void setEndColour(ColorRGBA endColour) {
		this.endColour = endColour;
	}

	public void setStartColour(ColorRGBA startColour) {
		this.startColour = startColour;
	}

	public void setColour(ColorRGBA colour) {
		startColour = endColour = colour;
	}

	public OGREParticleScript getScript() {
		return script;
	}

	public Emitter createEmitter(AssetManager assetManager) {
		TimedEmitter emitter = new TimedEmitter();

		LOG.info(String.format("Creating emitter %s", getClass()));

		// The material / texture
		if (script.getTexture() != null) {
			LOG.info(String.format("   Sprite: %s", script.getTexture()));
			emitter.setSprite(script.getTexture());
		} else {
			if (script.getMaterialName() != null) {
				try {
					LOG.info(String.format(" Material: %s", script.getMaterialName()));
					ParticleMaterial mat = ParticleMaterialFactory.get()
							.createParticleMaterial(assetManager, script.getMaterialName(), this);
					if (mat != null) {
						emitter.setMaterial(mat.getMaterial(), mat.getUniformName());
					} else {
						throw new AssetNotFoundException(String.format("No material %s.", script.getMaterialName()));
					}
				} catch (AssetNotFoundException anfe) {
					LOG.severe(
							String.format("No material found for %s, ignoring this for now", script.getMaterialName()));
					emitter.setSprite("emitter/images/bgx.jpg");

				}
			} else {
				LOG.severe(String.format("No material for %s, using default.", getClass()));
				emitter.setSprite("emitter/images/bgx.jpg");
			}
		}

		// Max particles allowed in system
		LOG.info(String.format("   No. Particles: %d", script.getQuota()));
		emitter.setMaxParticles(script.getQuota());

		// Maximum life of particle (random number between these values)
		LOG.info(String.format("   Life: %f -> %f", lowLife, highLife));
		emitter.setLifeMinMax(lowLife, highLife);

		// Number of particles per second to emit
		LOG.info(String.format("   Emission Rate: %f", particlesPerSec));
		emitter.setEmissionsPerSecond(particlesPerSec);

		// Initial colour range
		LOG.info(String.format("   Start Colour Range: %s -> %s", startColour, endColour));
		emitter.addInfluencer(new InitialColourInfluencer(startColour, endColour));

		// Size
		LOG.info(String.format("   Particle Size: %f x %f", script.getParticleSize().x, script.getParticleSize().y));

		InitialSizeInfluencer sz = new InitialSizeInfluencer(script.getParticleSize());
		emitter.addInfluencer(sz);

		// Direction velocity
		LOG.info(String.format("   Velocity: %f -> %f (%s)", velocityMin, velocityMax, direction));
		DirectionInfluencer directionInfluencer = new DirectionInfluencer(direction, velocityMin, velocityMax);
		emitter.addInfluencer(directionInfluencer);

		// If angle is set, the maximum angle (in degrees) which emitted
		// particles may
		// deviate from the direction of the emitter (see direction). Setting
		// this
		// to 10 allows particles to deviate up to 10 degrees in any direction
		// away
		// from the emitter’s direction. A value of 180 means emit in any
		// direction,
		// whilst 0 means emit always exactly in the direction of the emitter.

		AngleInfluencer angleInfluencer = new AngleInfluencer(angle * FastMath.DEG_TO_RAD);
		emitter.addInfluencer(angleInfluencer);

		// Add other affectors
		for (ParticleInfluencer pi : script.getAffectors()) {
			LOG.info(String.format("    Affector: %s (%s)", pi.getClass(), pi));
			emitter.addInfluencer(pi);
		}

		// Emitter shape
		createEmitterShape(emitter);

		// Position
		LOG.info(String.format("    Positioned @ %s", localTranslation));
		emitter.setLocalTranslation(localTranslation);

		// Duration
		if (durationSet && (durationMin > 0 || durationMax > 0)) {
			emitter.setDurationMin(durationMin);
			emitter.setDurationMax(durationMax);
			LOG.info(String.format("    Duration %f -> %f", durationMin, durationMax));

			// Repeat
			emitter.setRepeatDelayMin(repeatDelayMin);
			emitter.setRepeatDelayMax(repeatDelayMax);
			LOG.info(String.format("    Repeat Delay %f -> %f", repeatDelayMin, repeatDelayMax));
		}

		// Mesh type
		if (script.isPointRendering()) {
			LOG.info("    Point Mesh");
			emitter.setParticleType(ParticleDataPointMesh.class);
		} else {
			LOG.info("    Tri Mesh");
			emitter.setParticleType(ParticleDataTriMesh.class);
		}

		// Only used for Oriented_Common and Perpendicul_Common
		emitter.setCommonDirection(script.getCommonDirection());
		emitter.setCommonUpVector(script.getCommonUpVector());

		// TODO Doesn't seem to truly match. We'll see.....
		// TODO group.accurateFacing
		// (http://www.ogre3d.org/docs/manual/manual_35.html#particle_005faccurate_005ffacing)
		if (script.getBillboardType().equals(OGREParticleScript.BillboardType.POINT)) {
			// The default arrangement, this approximates spherical particles
			// and the billboards always fully face the camera.
			LOG.info(String.format("    Billboard mode: Camera (%s)", script.getBillboardType()));
			emitter.setBillboardMode(Emitter.BillboardMode.Camera);
		} else if (script.getBillboardType().equals(OGREParticleScript.BillboardType.ORIENTED_COMMON)) {
			LOG.warning(String.format("Unsupported billboard type %s", script.getBillboardType()));
			emitter.setBillboardMode(Emitter.BillboardMode.Oriented_Common);
		} else if (script.getBillboardType().equals(OGREParticleScript.BillboardType.PERPENDICULAR_COMMON)) {
			LOG.warning(String.format("Unsupported billboard type %s", script.getBillboardType()));

			// emitter.setBillboardMode(Emitter.BillboardMode.Camera);
			// emitter.setBillboardMode(Emitter.BillboardMode.UNIT_Y);
		} else if (script.getBillboardType().equals(OGREParticleScript.BillboardType.ORIENTED_SELF)) {
			/*
			 * Particles are oriented around their own direction vector, which
			 * acts as their local Y axis. As the particle changes direction, so
			 * the billboard reorients itself to face this way. Good for laser
			 * fire, fireworks and other ’streaky’ particles that should look
			 * like they are traveling in their own direction.
			 */
			emitter.setBillboardMode(Emitter.BillboardMode.Oriented_Self);
		} else if (script.getBillboardType().equals(OGREParticleScript.BillboardType.PERPENDICULAR_SELF)) {
			// Particles are oriented around their own direction vector, which
			// acts as their local Y axis.
			// As the particle changes direction, so the billboard reorients
			// itself to face this way.
			// Good for laser fire, fireworks and other ’streaky’ particles that
			// should look like they are
			// traveling in their own direction.
			LOG.info(String.format("    Billboard mode: Velocity (%s)", script.getBillboardType()));
			emitter.setBillboardMode(Emitter.BillboardMode.Velocity);
		} else {
			LOG.warning(String.format("Unsupported billboard type %s", script.getBillboardType()));
		}

		// Emission point
		switch (script.getBillboardOrigin()) {
		case TOP_CENTER:
			emitter.setParticleEmissionPoint(Emitter.ParticleEmissionPoint.Particle_Edge_Top);
			break;
		case BOTTOM_CENTER:
			emitter.setParticleEmissionPoint(Emitter.ParticleEmissionPoint.Particle_Edge_Bottom);
			break;
		case CENTER:
			emitter.setParticleEmissionPoint(Emitter.ParticleEmissionPoint.Particle_Center);
			break;
		default:
			LOG.warning(String.format("Unsupported particle emission origin %s", script.getBillboardOrigin()));
			emitter.setParticleEmissionPoint(Emitter.ParticleEmissionPoint.Particle_Center);
			break;
		}

		// Type ... needs work
		// if(script.getBillboardType().equals(OGREParticleScript.BillboardType.PERPENDICULAR_SELF))
		// {
		// emitter.setParticlesFollowEmitter(true);
		// }

		emitter.setDirectionType(script.getDirectionType());

		// Rotation type (not supported?)
		LOG.info(String.format("    Billboard rotation: %s", script.getBillboardRotation()));
		LOG.warning(String.format("TODO: Billboard rotation type %s not supported", script.getBillboardRotation()));

		emitter.setParticlesPerEmission(1);
		// emitter.setDirectionType(EmitterMesh.DirectionType.Normal);

		// Rotation
		// Vector3f upVector = script.getCommonUpVector();
		// if (upVector == null) {
		// upVector = new Vector3f(0, 1, 0);
		// }
		Vector3f upVector = Vector3f.UNIT_Y;
		Quaternion emitterRotation = new Quaternion();
		Vector3f dir = direction;
		if (dir == null) {
			dir = new Vector3f(0, 0, 1);
		}

		emitterRotation.lookAt(dir.normalize(), upVector);
		float[] angles = new float[3];
		emitterRotation.toAngles(angles);
		LOG.info(String.format("   Rotation: %s, Up: %s (%3.2f, %3.2f, %3.2f)", dir, upVector,
				angles[0] * FastMath.RAD_TO_DEG, angles[1] * FastMath.RAD_TO_DEG, angles[2] * FastMath.RAD_TO_DEG));
		// emitter.setLocalRotation(emitterRotation);

		// TODO
		// What the ...... for some reason this doesn't work when using the quat
		// directly. I noticed that converting to/from euler angles and creating
		// the
		// quat from that works... hell knows.

		Quaternion q1 = new Quaternion(new float[] { angles[0], angles[1], angles[2] });
		emitter.setLocalRotation(q1);

		return emitter;
	}

	public String getName() {
		String n = getClass().getSimpleName();
		if (n.endsWith("Emitter")) {
			n = n.substring(0, n.length() - 7);
		}
		return n;
	}

	protected abstract void createEmitterShape(Emitter emitter);

	public void write(OutputStream fos, boolean b) throws IOException {
		PrintWriter pw = new PrintWriter(fos);
		pw.println(String.format("\temitter %s", getName()));
		pw.println("\t{");
		pw.println(String.format("\t\tangle %1.1f", angle));
		if (!startColour.equals(endColour)) {
			pw.println(
					String.format("\t\tcolour_range_start %s", OGREParticleConfiguration.formatForWrite(startColour)));
			pw.println(String.format("\t\tcolour_range_end %s", OGREParticleConfiguration.formatForWrite(endColour)));
		} else {
			pw.println(String.format("\t\tcolour %s", OGREParticleConfiguration.formatForWrite(startColour)));
		}
		pw.println(String.format("\t\tdirection %s", OGREParticleConfiguration.formatForWrite(direction)));
		pw.println(String.format("\t\temission_rate %1.1f", particlesPerSec));
		pw.println(String.format("\t\tposition %s", OGREParticleConfiguration.formatForWrite(localTranslation)));
		if (velocityMax != velocityMax) {
			pw.println(String.format("\t\tvelocity_min %1.1f", velocityMin));
			pw.println(String.format("\t\tvelocity_max %1.1f", velocityMax));
		} else {
			pw.println(String.format("\t\tvelocity %1.1f", velocityMin));
		}

		if (lowLife != highLife) {
			pw.println(String.format("\t\ttime_to_live_min %1.1f", lowLife));
			pw.println(String.format("\t\ttime_to_live_min %1.1f", highLife));
		} else {
			pw.println(String.format("\t\ttime_to_live %1.1f", lowLife));
		}
		if (durationSet) {
			if (durationMin != durationMax) {
				pw.println(String.format("\t\tduration_min %1.1f", durationMin));
				pw.println(String.format("\t\tduration_max %1.1f", durationMax));
			} else {
				pw.println(String.format("\t\tduration %1.1f", durationMin));
			}
			if (repeatDelayMin != repeatDelayMax) {
				pw.println(String.format("\t\trepeat_delay_min %1.1f", repeatDelayMin));
				pw.println(String.format("\t\trepeat_delay_max %1.1f", repeatDelayMax));
			} else {
				pw.println(String.format("\t\trepeat_delay %1.1f", repeatDelayMin));
			}
		}
		writeEmitter(pw);
		pw.println("\t}");
		pw.flush();
	}

	protected abstract void writeEmitter(PrintWriter pw);
}
