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
// NTUA Team: no change in the imports
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.Event;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.core.population.routes.NetworkRoute;
import org.matsim.core.scoring.SumScoringFunction;
import org.matsim.core.scoring.functions.CharyparNagelLegScoring;
import org.matsim.core.scoring.functions.ScoringParameters;
import org.matsim.api.core.v01.TransportMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SplittableRandom;

/**
 * ptzouras @author.
 */
class PerceivedSafetyScoring implements SumScoringFunction.LegScoring, SumScoringFunction.ArbitraryEventScoring {

	// default scoring function
	private final CharyparNagelLegScoring delegate ;

	// variables for mean marginal utilities of each mode
	// i.e. mean beta parameters

	// 4 first/last mile are considered only
	private final double marginalUtilityOfPerceivedSafetyCar;
	private final double marginalUtilityOfPerceivedSafetyEBike;
	private final double marginalUtilityOfPerceivedSafetyEScooter;
	private final double marginalUtilityOfPerceivedSafetyWalk;
	// next the standard deviation params
	private final double marginalUtilityOfPerceivedSafetyCarSd;
	private final double marginalUtilityOfPerceivedSafetyEBikeSd;
	private final double marginalUtilityOfPerceivedSafetyEscooterSd;
	private final double marginalUtilityOfPerceivedSafetyWalkSd;
	// the maximum accepted (unsafe) distance, after which agents change route
	private final double dMaxCar;
	private final double dMaxEBike;
	private final double dMaxEScooter;
	private final double dMaxWalk;

	private final int inputPerceivedSafetyThreshold;

	// 0 if none of the modes below are provided, e.g PT
	private double betaPerceivedSafety = 0.;
	// same for the standard deviation
	private double sdPerceivedSafety = 0.;
	// while dmax is set to one
	private double dMax = 1.;
	// no mode
//	TODO: think about better naming here
	private String mode = "wrong_mode";

	// it gives sum (perceivedSafety * distance of link i) from UtilityUtils
	private double sumPerceivedSafety;

	private final String carMode;
	private final String eBikeMode;
	private final String eScooterMode;
	private final String walkMode;

	// call the network
	private final Network network;
	// the additional score we get each time, start with zero
	private double additionalScore = 0.;
	SplittableRandom generator = new SplittableRandom();

	PerceivedSafetyScoring(final ScoringParameters params, Network network, Set<String> ptModes, PerceivedSafetyConfigGroup perceivedSafetyConfigGroup) {
		delegate = new CharyparNagelLegScoring( params, network, ptModes ) ;
		// get marginal utilities from the run file...mean values
		this.marginalUtilityOfPerceivedSafetyCar = perceivedSafetyConfigGroup.getMarginalUtilityOfPerceivedSafetyCarPerM();
		this.marginalUtilityOfPerceivedSafetyEBike = perceivedSafetyConfigGroup.getMarginalUtilityOfPerceivedSafetyEBikePerM();
		this.marginalUtilityOfPerceivedSafetyEScooter = perceivedSafetyConfigGroup.getMarginalUtilityOfPerceivedSafetyEScooterPerM();
		this.marginalUtilityOfPerceivedSafetyWalk = perceivedSafetyConfigGroup.getMarginalUtilityOfPerceivedSafetyWalkPerM();
		// get marginal utilities from the run file...standard deviation values
		this.marginalUtilityOfPerceivedSafetyCarSd = perceivedSafetyConfigGroup.getMarginalUtilityOfPerceivedSafetyCarPerMSd();
		this.marginalUtilityOfPerceivedSafetyEBikeSd = perceivedSafetyConfigGroup.getMarginalUtilityOfPerceivedSafetyEBikePerMSd();
		this.marginalUtilityOfPerceivedSafetyEscooterSd = perceivedSafetyConfigGroup.getMarginalUtilityOfPerceivedSafetyEScooterPerMSd();
		this.marginalUtilityOfPerceivedSafetyWalkSd = perceivedSafetyConfigGroup.getMarginalUtilityOfPerceivedSafetyWalkPerMSd();
		// get dmax calibration parameters from the run file
		this.dMaxCar = perceivedSafetyConfigGroup.getDMaxCarPerM();
		this.dMaxEBike = perceivedSafetyConfigGroup.getDMaxEBikePerM();
		this.dMaxEScooter = perceivedSafetyConfigGroup.getDMaxEScooterPerM();
		this.dMaxWalk = perceivedSafetyConfigGroup.getDMaxWalkPerM();
		// different threshold can be used, preferably it has to be equal to 4
		this.inputPerceivedSafetyThreshold = perceivedSafetyConfigGroup.getInputPerceivedSafetyThresholdPerM();

		this.carMode = TransportMode.car;
		this.eBikeMode = perceivedSafetyConfigGroup.getEBikeMode();
		this.eScooterMode = perceivedSafetyConfigGroup.getEScooterMode();
		this.walkMode = perceivedSafetyConfigGroup.getWalkMode();

		this.network = network;
	}

	private void calcLegScore( final Leg leg ) {
		if(carMode.equals(leg.getMode())) {
			// if car then
			// set mean beta_psafe, depends on the transport mode
			betaPerceivedSafety = marginalUtilityOfPerceivedSafetyCar;
			// set sd beta_psafe, depends on the transport mode
			sdPerceivedSafety = marginalUtilityOfPerceivedSafetyCarSd;
			// set dmax, depends on the transport mode
			dMax = dMaxCar;
			mode = leg.getMode();
		}
		if(eBikeMode.equals(leg.getMode())) {
			betaPerceivedSafety = marginalUtilityOfPerceivedSafetyEBike;
			sdPerceivedSafety = marginalUtilityOfPerceivedSafetyEBikeSd;
			dMax = dMaxEBike;
			mode = leg.getMode();
		}
		if (eScooterMode.equals(leg.getMode())) {
			betaPerceivedSafety = marginalUtilityOfPerceivedSafetyEScooter;
			sdPerceivedSafety = marginalUtilityOfPerceivedSafetyEscooterSd;
			dMax = dMaxEScooter;
			mode = leg.getMode();
		}
		if (walkMode.equals(leg.getMode())) {
			betaPerceivedSafety = marginalUtilityOfPerceivedSafetyWalk;
			sdPerceivedSafety = marginalUtilityOfPerceivedSafetyWalkSd;
			dMax = dMaxWalk;
			mode = leg.getMode();
		}

		if (!isSameStartAndEnd(leg)) {
			NetworkRoute networkRoute = (NetworkRoute) leg.getRoute();
			List<Id<Link>> linkIds = new ArrayList<>(networkRoute.getLinkIds());
			linkIds.add(networkRoute.getEndLinkId());

			// set of sumPerceivedSafety equal to zero before iterating of links
			sumPerceivedSafety = 0.;
			// set of sumDistance equal to zero before iterating links
			double sumDistance = 0. ;
			for (Id<Link> linkId : linkIds) {
				Link link = network.getLinks().get(linkId);
				// this is the length of link i
				double distance = link.getLength();
				// take the score of each link based on the ratings provided in the network file
				double scoreOnLink = PerceivedSafetyInput.computePerceivedSafetyScore(network.getLinks().get(linkId),
						mode, inputPerceivedSafetyThreshold);
				// keep the total distance of the leg..
				sumDistance += distance;
				// estimate based on the new utility function I developec
				sumPerceivedSafety += scoreOnLink * distance;
			}

			// in case you want to estimate the additional score based on the weighted mean
			if (dMax == 0) {
				dMax = sumDistance;
			}

			//divide by dmax at the end
			sumPerceivedSafety = sumPerceivedSafety / dMax;
			// run Monte Carlo Simulation for the safety perceptions
			double r = generator.nextGaussian();
			// multiply with the random beta parameter
			additionalScore = (betaPerceivedSafety + r * sdPerceivedSafety) * sumPerceivedSafety;
		}
	  }

	  private static boolean isSameStartAndEnd(Leg leg) {
		return leg.getRoute().getStartLinkId().toString().equals(leg.getRoute().getEndLinkId().toString());
	}

	@Override public void finish(){
		delegate.finish();
	}

	@Override public double getScore() {
		// add the additional score to the default score of MATSim
		return delegate.getScore() + this.additionalScore;
	}

	@Override public void handleLeg( Leg leg ){
		delegate.handleLeg( leg );
		calcLegScore( leg );
	}

	@Override public void handleEvent( Event event ){
		delegate.handleEvent( event );
	}
}
