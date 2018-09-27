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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;

import emitter.EmitterMesh.DirectionType;

/**
 * Creates particle emitters from OGRE particle scripts.
 */
public class OGREParticleConfiguration {

	// Various states when reading configuration file
	private String line = null;
	private State state = State.OUTTER;
	private int lineNo = 0;
	private boolean expectOpeningBrace = false;
	private OGREParticleScript em = null;
	private boolean hasOpeningBrace;
	private final static Logger LOG = Logger.getLogger(OGREParticleConfiguration.class.getName());
	private OGREParticleEmitter emitter;
	private boolean hasClosingBrace;
	private OGREParticleAffector affector;
	private boolean skipToNextBrace;
	private Map<String, OGREParticleScript> scripts = new LinkedHashMap<String, OGREParticleScript>();

	enum State {

		OUTTER, PARTICLE_SYSTEM, EMITTER, AFFECTOR
	}

	public void addScript(OGREParticleScript script) {
		scripts.put(script.getName(), script);
	}

	public void removeScript(OGREParticleScript script) {
		scripts.remove(script.getName());
	}

	public OGREParticleScript getScript(String name) {
		final OGREParticleScript child = scripts.get(name);
		if (child == null) {
			throw new IllegalArgumentException("No particle group named " + name);
		}
		return child;
	}

	public void write(OutputStream fos, boolean b) throws IOException {
		PrintWriter pw = new PrintWriter(fos);
		int i = 0;
		for (Map.Entry<String, OGREParticleScript> en : scripts.entrySet()) {
			if (i > 0) {
				pw.println();
				pw.flush();
			}
			final OGREParticleScript value = en.getValue();
			value.write(fos, b);
			i++;
		}
		fos.flush();
	}

	public static String formatForWrite(Vector3f vec) {
		return String.format("%1.1f %1.1f %1.1f", vec.x, vec.y, vec.z);
	}

	public static String formatForWrite(ColorRGBA col) {
		return String.format("%1.1f %1.1f %1.1f", col.r, col.g, col.b, col.a);
	}

	public void load(InputStream in) throws IOException {
		scripts.clear();
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		while ((line = reader.readLine()) != null) {
			lineNo++;
			line = line.trim();
			if (line.equals("") || line.startsWith("//")) {
				//
			} else {
				try {
					// Not sure if valid syntax, but maybe { can be on same line
					hasOpeningBrace = line.endsWith("{");
					if (hasOpeningBrace) {
						line = line.substring(0, line.length() - 1);
					}

					if (expectOpeningBrace) {
						if (!hasOpeningBrace) {
							throw new IOException("Expected opening brance at line " + lineNo);
						}
						expectOpeningBrace = false;
					}

					if (!hasOpeningBrace) {

						hasClosingBrace = line.endsWith("}");

						// System.err.println(String.format(">> '%s' - ho: %s
						// eo: %s hc: %s st: %s", line, hasOpeningBrace,
						// expectOpeningBrace, hasClosingBrace, state));

						if (hasClosingBrace) {
							skipToNextBrace = false;
							switch (state) {
							case EMITTER:
								state = State.PARTICLE_SYSTEM;
								break;
							case AFFECTOR:
								state = State.PARTICLE_SYSTEM;
								break;
							case PARTICLE_SYSTEM:
								state = State.OUTTER;
								break;
							}
						} else {
							if (!skipToNextBrace) {
								if (state.equals(State.OUTTER)) {
									String[] args = line.split("\\s+");

									if (args[0].equals("particle_system")) {
										if (args.length == 2) {
											em = new OGREParticleScript(args[1], this);
											LOG.info(
													"-----------------------------------------------------------------------");
											LOG.info(String.format("Found particle system %s", em.getName()));
											scripts.put(args[1], em);
											state = State.PARTICLE_SYSTEM;
											expectOpeningBrace = !hasOpeningBrace;
										} else {
											throw new IOException(
													"Expected single particle_system name at line " + lineNo);
										}
									} else {
										throw new IOException(
												"Unexpected section name '" + line + "' at line " + lineNo + ".");
									}

								} else if (state.equals(State.PARTICLE_SYSTEM)) {
									readParticleSystemSection(scripts);
								} else if (state.equals(State.EMITTER)) {
									readEmitterSection(scripts);
								} else if (state.equals(State.AFFECTOR)) {
									readAffectorSection(scripts);
								}
							}
						}
					}
				} catch (NumberFormatException nfe) {
					LOG.log(Level.SEVERE, String.format("Number format error at %d", lineNo), nfe);
				}
			}
		}
	}

	private void readAffectorSection(Map<String, OGREParticleScript> backingObject) throws IOException {
		String[] args = line.split("\\s+");
		try {
			if (!affector.parse(args, lineNo)) {
				skipToNextBrace = true;
				LOG.info(String.format("TODO: unknown affector %s at line %d", affector, lineNo));
			}
		} catch (ParseException ex) {
			throw new IOException("Error parsing emitter attributes.", ex);
		}
	}

	public static Vector3f parseVector3f(String[] args) throws NumberFormatException {
		return new Vector3f(Float.parseFloat(args[1]), Float.parseFloat(args[2]), Float.parseFloat(args[3]));
	}

	private void readEmitterSection(Map<String, OGREParticleScript> backingObject) throws IOException {
		String[] args = line.split("\\s+");
		if (args[0].equals("angle")) {
			if (args.length == 2) {
				emitter.setAngle(Float.parseFloat(args[1]));
			} else {
				throw new IOException("Expected single angle at line " + lineNo + ".");
			}
		} else if (args[0].equals("colour")) {
			if (args.length > 3) {
				emitter.setColour(parseColour(args));
			} else {
				throw new IOException("Expected at least 3 color values at line " + lineNo + ".");
			}
		} else if (args[0].equals("colour_range_start")) {
			if (args.length > 3) {
				emitter.setStartColour(parseColour(args));
			} else {
				throw new IOException("Expected at least 3 color values  at line " + lineNo + ".");
			}
		} else if (args[0].equals("colour_range_end")) {
			if (args.length > 3) {
				emitter.setEndColour(parseColour(args));
			} else {
				throw new IOException("Expected at least 3 color values at line " + lineNo + ".");
			}
		} else if (args[0].equals("direction")) {
			if (args.length > 3) {
				emitter.setDirection(parseVector3f(args));
			} else {
				throw new IOException("Expected vector at line " + lineNo + ".");
			}
		} else if (args[0].equals("emission_rate")) {
			if (args.length == 2) {
				float r = Float.parseFloat(args[1]);
				emitter.setParticlesPerSec(r);
			} else {
				throw new IOException("Expected single direction at line " + lineNo + ".");
			}
		} else if (args[0].equals("position")) {
			if (args.length > 3) {
				emitter.setLocalTranslation(parseVector3f(args));
			} else {
				throw new IOException("Expected vector at line " + lineNo + ".");
			}
		} else if (args[0].equals("velocity")) {
			if (args.length == 2) {
				emitter.setVelocity(Float.parseFloat(args[1]));
			} else {
				throw new IOException("Expected single velocity at line " + lineNo + ".");
			}
		} else if (args[0].equals("velocity_min")) {
			if (args.length == 2) {
				emitter.setVelocityMin(Float.parseFloat(args[1]));
			} else {
				throw new IOException("Expected single velocity_min at line " + lineNo + ".");
			}
		} else if (args[0].equals("velocity_max")) {
			if (args.length == 2) {
				emitter.setVelocityMax(Float.parseFloat(args[1]));
			} else {
				throw new IOException("Expected single velocity_max at line " + lineNo + ".");
			}
		} else if (args[0].equals("time_to_live")) {
			if (args.length == 2) {
				emitter.setTimeToLive(Float.parseFloat(args[1]));
			} else {
				throw new IOException("Expected single time_to_live at line " + lineNo + ".");
			}
		} else if (args[0].equals("time_to_live_min")) {
			if (args.length == 2) {
				emitter.setLowLife(Float.parseFloat(args[1]));
			} else {
				throw new IOException("Expected single time_to_live_min at line " + lineNo + ".");
			}
		} else if (args[0].equals("time_to_live_max")) {
			if (args.length == 2) {
				emitter.setHighLife(Float.parseFloat(args[1]));
			} else {
				throw new IOException("Expected single time_to_live_min at line " + lineNo + ".");
			}
		} else if (args[0].equals("duration")) {
			if (args.length == 2) {
				emitter.setDuration(Float.parseFloat(args[1]));
			} else {
				throw new IOException("Expected single duration at line " + lineNo + ".");
			}
		} else if (args[0].equals("duration_min")) {
			if (args.length == 2) {
				emitter.setDurationMin(Float.parseFloat(args[1]));
			} else {
				throw new IOException("Expected single duration_min at line " + lineNo + ".");
			}
		} else if (args[0].equals("duration_max")) {
			if (args.length == 2) {
				emitter.setDurationMax(Float.parseFloat(args[1]));
			} else {
				throw new IOException("Expected single duration_max at line " + lineNo + ".");
			}
		} else if (args[0].equals("repeat_delay")) {
			if (args.length == 2) {
				emitter.setRepeatDelay(Float.parseFloat(args[1]));
			} else {
				throw new IOException("Expected single repeat_delay at line " + lineNo + ".");
			}
		} else if (args[0].equals("repeat_delay_min")) {
			if (args.length == 2) {
				emitter.setRepeatDelayMin(Float.parseFloat(args[1]));
			} else {
				throw new IOException("Expected single repeat_delay_min at line " + lineNo + ".");
			}
		} else if (args[0].equals("repeat_delay_max")) {
			if (args.length == 2) {
				emitter.setRepeatDelayMax(Float.parseFloat(args[1]));
			} else {
				throw new IOException("Expected single repeat_delay_max at line " + lineNo + ".");
			}
		} else {
			try {
				if (!emitter.parse(args, lineNo)) {
					LOG.log(Level.WARNING,
							String.format("Unknown emitter attribute at line %d. '%s'.", lineNo, args[0]));
				}
			} catch (ParseException ex) {
				throw new IOException("Error parsing emitter attributes.", ex);
			}
		}
	}

	public static ColorRGBA parseColour(String[] args) {
		if (args.length == 5) {
			return new ColorRGBA(Float.parseFloat(args[1]), Float.parseFloat(args[2]), Float.parseFloat(args[3]),
					Float.parseFloat(args[4]));
		} else {
			return new ColorRGBA(Float.parseFloat(args[1]), Float.parseFloat(args[2]), Float.parseFloat(args[3]), 1f);
		}
	}

	private void readParticleSystemSection(Map<String, OGREParticleScript> backingObject) throws IOException {
		String[] args = line.split("\\s+");
		if (args[0].equals("emitter")) {
			if (args.length == 2) {
				StringBuilder b = new StringBuilder();
				for (int i = 1; i < args.length; i++) {
					if (i > 1) {
						b.append(' ');
					}
					b.append(args[i]);
				}
				emitter = em.createEmitter(b.toString());
				state = State.EMITTER;
				expectOpeningBrace = !hasOpeningBrace;
			} else {
				throw new IOException("Expected single emitter name at line " + lineNo + ".");
			}
		} else if (args[0].equals("affector")) {
			if (args.length == 2) {
				state = State.AFFECTOR;
				affector = em.createAffector(args[1]);
				expectOpeningBrace = !hasOpeningBrace;
			} else {
				throw new IOException("Expected single affector name at line " + lineNo + ".");
			}
		} else {
			readParticleSystemAttribute(args);
		}
	}

	private void readParticleSystemAttribute(String[] args) throws IOException, NumberFormatException {
		if (args[0].equals("quota")) {
			if (args.length == 2) {
				em.setNumParticles(Integer.parseInt(args[1]));
			} else {
				throw new IOException("Expected single quota value at line " + lineNo + ".");
			}
		} else if (args[0].equals("material")) {
			if (args.length == 2) {
				em.setMaterialName(args[1]);
			} else {
				throw new IOException("Expected single material value at line " + lineNo + ".");
			}
		} else if (args[0].equals("texture")) {
			if (args.length == 2) {
				em.setTexture(args[1]);
			} else {
				throw new IOException("Expected single texture value at line " + lineNo + ".");
			}
		} else if (args[0].equals("particle_width")) {
			if (args.length == 2) {
				em.getParticleSize().x = Float.parseFloat(args[1]);
			} else {
				throw new IOException("Expected single particle_width value at line " + lineNo + ".");
			}
		} else if (args[0].equals("particle_height")) {
			if (args.length == 2) {
				em.getParticleSize().y = Float.parseFloat(args[1]);
			} else {
				throw new IOException("Expected single particle_height value at line " + lineNo + ".");
			}
		} else if (args[0].equals("cull_each")) {
			if (args.length == 2) {
				em.setCullEach(Boolean.parseBoolean(args[1]));
			} else {
				throw new IOException("Expected single cull_each value at line " + lineNo + ".");
			}
		} else if (args[0].equals("renderer")) {
			if (args.length != 2 || !args[1].equals("billboard")) {
				throw new IOException("Expected 'billboard' renderer at line " + lineNo + ".");
			}
		} else if (args[0].equals("sorted")) {
			if (args.length == 2) {
				em.setSorted(Boolean.parseBoolean(args[1]));
			} else {
				throw new IOException("Expected single sorted value at line " + lineNo + ".");
			}
		} else if (args[0].equals("local_space")) {
			if (args.length == 2) {
				em.setLocalSpace(Boolean.parseBoolean(args[1]));
			} else {
				throw new IOException("Expected single local_space value at line " + lineNo + ".");
			}
		} else if (args[0].equals("iteration_interval")) {
			if (args.length == 2) {
				em.setIterationInterval(Float.parseFloat(args[1]));
			} else {
				throw new IOException("Expected single iteration_interval value at line " + lineNo + ".");
			}
		} else if (args[0].equals("nonvisible_update_timeout")) {
			if (args.length == 2) {
				em.setNonVisibleUpdateTimeout(Float.parseFloat(args[1]));
			} else {
				throw new IOException("Expected single nonvisible_update_timeout value at line " + lineNo + ".");
			}
		} else if (args[0].startsWith("billboard_")) {
			if (args.length == 2) {
				em.setBillboardParam(args[0].substring(10), args[1]);
			} else {
				throw new IOException("Expected single billboard_* value at line " + lineNo + ".");
			}
		} else if (args[0].startsWith("direction_type")) {
			if (args.length == 2) {
				for (DirectionType t : DirectionType.values()) {
					if (t.name().equalsIgnoreCase(args[1])) {
						em.setDirectionType(t);
					}
				}
			} else {
				throw new IOException("Expected single billboard_* value at line " + lineNo + ".");
			}
		} else if (args[0].equals("common_direction")) {
			if (args.length > 3) {
				em.setCommonDirection(parseVector3f(args));
			} else {
				throw new IOException("Expected 3 common_direction values at line " + lineNo + ".");
			}
		} else if (args[0].equals("common_up_vector")) {
			if (args.length > 3) {
				em.setCommonUpVector(parseVector3f(args));
			} else {
				throw new IOException("Expected 3 common_up values at line " + lineNo + ".");
			}
		} else if (args[0].equals("point_rendering")) {
			if (args.length == 2) {
				em.setPointRendering(Boolean.parseBoolean(args[1]));
			} else {
				throw new IOException("Expected single point_rendering value at line " + lineNo + ".");
			}
		} else if (args[0].equals("accurate_facing")) {
			if (args.length == 2) {
				em.setAccurateFacing(Boolean.parseBoolean(args[1]));
			} else {
				throw new IOException("Expected single accurate_facing value at line " + lineNo + ".");
			}
		} else {
			LOG.log(Level.WARNING,
					String.format("Unknown particle_system attribute at line %d. '%s'.", lineNo, args[0]));
		}
	}

}
