package org.matsim.contrib.Psafe;

import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.TransportMode;

public final class PsafeInput {

	static double computePsafeScore(Link link,String mode, int thre) {
		int varpsafe = 4;
		if (mode.equals(TransportMode.car)) {
			varpsafe = (int)link.getAttributes().getAttribute(PsafeNewAttrib.PERCEIVED_SAFETY_CAR);}
		if (mode.equals("ebike")) {
			varpsafe = (int)link.getAttributes().getAttribute(PsafeNewAttrib.PERCEIVED_SAFETY_EBIKE);}
		if (mode.equals("escoot")) {
			varpsafe = (int)link.getAttributes().getAttribute(PsafeNewAttrib.PERCEIVED_SAFETY_ESCOOT);}
		if (mode.equals("walk")) {
			varpsafe = (int)link.getAttributes().getAttribute(PsafeNewAttrib.PERCEIVED_SAFETY_WALK);}
		double score = varpsafe - thre;
		return(score);
	}
}