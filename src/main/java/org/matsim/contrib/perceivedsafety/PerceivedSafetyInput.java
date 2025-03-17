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

import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.TransportMode;

/**
 * ptzouras @author.
 */
public final class PerceivedSafetyInput {
	private PerceivedSafetyInput() {}

	static double computePerceivedSafetyScore(Link link, String mode, int thre) {
		int varpsafe = 4;
		if (mode.equals(TransportMode.car)) {
			varpsafe = (int) link.getAttributes().getAttribute(PerceivedSafetyNewAttributes.PERCEIVED_SAFETY_CAR);
		}
		if (mode.equals("ebike")) {
			varpsafe = (int) link.getAttributes().getAttribute(PerceivedSafetyNewAttributes.PERCEIVED_SAFETY_EBIKE);
		}
		if (mode.equals("escoot")) {
			varpsafe = (int) link.getAttributes().getAttribute(PerceivedSafetyNewAttributes.PERCEIVED_SAFETY_ESCOOTER);
		}
		if (mode.equals("walk")) {
			varpsafe = (int) link.getAttributes().getAttribute(PerceivedSafetyNewAttributes.PERCEIVED_SAFETY_WALK);
		}
		return(varpsafe - thre);
	}
}
