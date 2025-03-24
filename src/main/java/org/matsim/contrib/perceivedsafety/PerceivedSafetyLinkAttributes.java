/* *********************************************************************** *
* project: org.matsim.*
* *********************************************************************** *
*                                                                         *
* copyright       : (C) ${2024} by the members listed in the COPYING,     *
*                   LICENSE and WARRANTY file.                            *
* email           : info at matsim dot org                                *
*                                                                         *
* *********************************************************************** *
*                                                                         *
*   This program is free software; you can redistribute it and/or modify  *
*   it under the terms of the GNU General Public License as published by  *
*   the Free Software Foundation; either version 2 of the License, or     *
*   (at your option) any later version.                                   *
*   See also COPYING, LICENSE and WARRANTY file                           *
*                                                                         *
* *********************************************************************** */

package org.matsim.contrib.perceivedsafety;

/**
 * ptzouras @author.
 */
public final class PerceivedSafetyLinkAttributes {
//	TODO: integrate this into ConfigGroup?!
	public static final String PERCEIVED_SAFETY_CAR = "carPerceivedSafety";
	public static final String PERCEIVED_SAFETY_EBIKE = "eBikePerceivedSafety";
	public static final String PERCEIVED_SAFETY_ESCOOTER = "eScooterPerceivedSafety";
	public static final String PERCEIVED_SAFETY_WALK = "walkPerceivedSafety";
	private PerceivedSafetyLinkAttributes() {}
}
