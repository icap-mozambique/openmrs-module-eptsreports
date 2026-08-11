/**
 *
 */
package org.openmrs.module.eptsreports.reporting.library.cohorts;

/**
 * @author Stélio Moiane
 *
 */
public enum PrepKp {

	MILITARY(1902, "Militar"),

	MINER(1908, "Mineiro"),

	DRIVER(1903, "Camionista"),

	COUPLES_RESULT_ARE_DIFFERENT(1995, "Casais Serodiscordantes"),

	PREGNANT(1982, "Grávida"),

	BREASTFEEDING(6332, "Lactante");

	private int id;

	private String name;

	PrepKp(final int id, final String name) {
		this.id = id;
		this.name = name;
	}

	public int getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}
}
