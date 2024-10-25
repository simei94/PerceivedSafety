package org.matsim.contrib.Psafe;
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
import java.util.Random;

class PsafeNewStyleScoring implements SumScoringFunction.LegScoring, SumScoringFunction.ArbitraryEventScoring{ 

	private final CharyparNagelLegScoring delegate ; // default scoring function
	
	// variables for mean marginal utilities of each mode
	// i.e. mean beta parameters
	private final double marginalUtilityOfPerceivedSafety_car; // 4 first/last mile are considered only
	private final double marginalUtilityOfPerceivedSafety_ebike;
	private final double marginalUtilityOfPerceivedSafety_escoot;
	private final double marginalUtilityOfPerceivedSafety_walk;
	// next the standard deviation params
	private final double marginalUtilityOfPerceivedSafety_car_sd;
	private final double marginalUtilityOfPerceivedSafety_ebike_sd;
	private final double marginalUtilityOfPerceivedSafety_escoot_sd;
	private final double marginalUtilityOfPerceivedSafety_walk_sd;
	// the maximum accepted (unsafe) distance, after which I change route
	private final double Dmax_car;
	private final double Dmax_ebike;
	private final double Dmax_escoot;
	private final double Dmax_walk;
	
	private final int inputPsafeThreshold;
	
	private double beta_psafe = 0.; // 0 if none of the modes below are provided, e.g PT
	private double sd_psafe = 0.; // same for the standard deviation
	private double dmax = 1.; // while dmax is set to one
	private String mode = "wrong_mode"; // no mode
	
	private double sumPerceivedSafety; // it gives sum(psafe*distance of link i) from UtilityUtils
	
	private final String carMode;
	private final String ebikeMode;
	private final String escootMode;
	private final String walkMode;
	
	private final Network network; // call the network
	private double additionalScore = 0.; // the additional score we get each time, start with zero
	
	Random generator = new Random();
	
	PsafeNewStyleScoring( final ScoringParameters params, Network network, Set<String> ptModes, PsafeConfigGroup psafeConfigGroup) {
		delegate = new CharyparNagelLegScoring( params, network, ptModes ) ;
		// get marginal utilities from the run file...mean values
		this.marginalUtilityOfPerceivedSafety_car = psafeConfigGroup.getMarginalUtilityOfPerceivedSafety_car_m();
		this.marginalUtilityOfPerceivedSafety_ebike = psafeConfigGroup.getMarginalUtilityOfPerceivedSafety_ebike_m();
		this.marginalUtilityOfPerceivedSafety_escoot = psafeConfigGroup.getMarginalUtilityOfPerceivedSafety_escoot_m();
		this.marginalUtilityOfPerceivedSafety_walk = psafeConfigGroup.getMarginalUtilityOfPerceivedSafety_walk_m();
		// get marginal utilities from the run file...standard deviation values
		this.marginalUtilityOfPerceivedSafety_car_sd = psafeConfigGroup.getMarginalUtilityOfPerceivedSafety_car_m_sd();
		this.marginalUtilityOfPerceivedSafety_ebike_sd = psafeConfigGroup.getMarginalUtilityOfPerceivedSafety_ebike_m_sd();
		this.marginalUtilityOfPerceivedSafety_escoot_sd = psafeConfigGroup.getMarginalUtilityOfPerceivedSafety_escoot_m_sd();
		this.marginalUtilityOfPerceivedSafety_walk_sd = psafeConfigGroup.getMarginalUtilityOfPerceivedSafety_walk_m_sd();
		// get dmax calibration parameters from the run file
		this.Dmax_car = psafeConfigGroup.getDmax_car_m();
		this.Dmax_ebike = psafeConfigGroup.getDmax_ebike_m();
		this.Dmax_escoot = psafeConfigGroup.getDmax_escoot_m();
		this.Dmax_walk = psafeConfigGroup.getDmax_walk_m();
		// different threshold can be used, preferably it has to be equal to 4
		this.inputPsafeThreshold = psafeConfigGroup.getInputPsafeThreshold_m(); 

		this.carMode = TransportMode.car;
		this.ebikeMode = psafeConfigGroup.getEbikeMode();
		this.escootMode = psafeConfigGroup.getEscootMode();
		this.walkMode = psafeConfigGroup.getWalkMode();
		
		this.network = network ;}

	private void calcLegScore( final Leg leg ) {
		if(carMode.equals(leg.getMode())) { // if car then
			beta_psafe = marginalUtilityOfPerceivedSafety_car; // set mean beta_psafe, depends on the transport mode
			sd_psafe = marginalUtilityOfPerceivedSafety_car_sd; // set sd beta_psafe, depends on the transport mode
			dmax = Dmax_car; // set dmax, depends on the transport mode
	        mode = leg.getMode();}
		if(ebikeMode.equals(leg.getMode())) {
			beta_psafe = marginalUtilityOfPerceivedSafety_ebike;
			sd_psafe = marginalUtilityOfPerceivedSafety_ebike_sd;
			dmax = Dmax_ebike;
			mode = leg.getMode();}
		if (escootMode.equals(leg.getMode())) {
			beta_psafe = marginalUtilityOfPerceivedSafety_escoot;
			sd_psafe = marginalUtilityOfPerceivedSafety_escoot_sd;
			dmax = Dmax_escoot;
			mode = leg.getMode();}
		if (walkMode.equals(leg.getMode())) {
			beta_psafe = marginalUtilityOfPerceivedSafety_walk;
			sd_psafe = marginalUtilityOfPerceivedSafety_walk_sd;;
			dmax = Dmax_walk;
			mode = leg.getMode();}
		
		if (!isSameStartAndEnd(leg)) {
			NetworkRoute networkRoute = (NetworkRoute) leg.getRoute();
			List<Id<Link>> linkIds = new ArrayList<>(networkRoute.getLinkIds());
			linkIds.add(networkRoute.getEndLinkId());
			
			sumPerceivedSafety = 0.; // set of sumPerceivedSafety equal to zero before iterating of links 
			double sumDistance = 0. ; // set of sumDistance equal to zero before iterating links
			for (Id<Link> linkId : linkIds) {
				Link link = network.getLinks().get(linkId);
				double distance = link.getLength(); // this is the length of link i
				double scoreOnLink = PsafeInput.computePsafeScore(network.getLinks().get(linkId), 
						mode, inputPsafeThreshold); // take the score of each link based on the ratings provided in the network file
				sumDistance += distance; // keep the total distance of the leg..			
			    sumPerceivedSafety += scoreOnLink * distance; // estimate based on the new utility function I developec
			}
			
			if (dmax == 0) {
				dmax = sumDistance;} // in case you want to estimate the additional score based on the weighted mean
			
			sumPerceivedSafety = sumPerceivedSafety/dmax; //divide with dmax at the end
			// run Monte Carlo Simulation for the safety perceptions 
			double r = generator.nextGaussian();
			additionalScore = (beta_psafe + r * sd_psafe) * sumPerceivedSafety; // multiply with the random beta parameter
		    // System.out.println("The additional psafe score is " + additionalScore);
		}
	  }
	    
	private static boolean isSameStartAndEnd(Leg leg) {
		return leg.getRoute().getStartLinkId().toString().equals(leg.getRoute().getEndLinkId().toString());
	}

	@Override public void finish(){
		delegate.finish();
	}

	@Override public double getScore(){
		return delegate.getScore() + this.additionalScore ; // add the additional score to the default score of MATSim
	}

	@Override public void handleLeg( Leg leg ){
		delegate.handleLeg( leg );
		calcLegScore( leg );
	}

	@Override public void handleEvent( Event event ){
		delegate.handleEvent( event );
	}
}
