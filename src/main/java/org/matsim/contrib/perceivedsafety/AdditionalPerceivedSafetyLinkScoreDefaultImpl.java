package org.matsim.contrib.perceivedsafety;

import com.google.inject.Inject;
import jakarta.validation.constraints.NotNull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.core.config.ConfigUtils;
import org.matsim.vehicles.Vehicle;
import org.matsim.vehicles.Vehicles;

import java.util.SplittableRandom;

public class AdditionalPerceivedSafetyLinkScoreDefaultImpl implements AdditionalPerceivedSafetyLinkScore {
    private static final Logger log = LogManager.getLogger(AdditionalPerceivedSafetyLinkScoreDefaultImpl.class);

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

    private final String carMode;
    private final String eBikeMode;
    private final String eScooterMode;
    private final String walkMode;

    SplittableRandom generator = new SplittableRandom();
    private final Vehicles vehicles;

    @Inject
    AdditionalPerceivedSafetyLinkScoreDefaultImpl(Scenario scenario) {
        PerceivedSafetyConfigGroup perceivedSafetyConfigGroup = ConfigUtils.addOrGetModule(scenario.getConfig(), PerceivedSafetyConfigGroup.class);
//        TODO: how to translate this to ConfigGroup.ModeParams dialect?
        this.marginalUtilityOfPerceivedSafetyCar = perceivedSafetyConfigGroup.getMarginalUtilityOfPerceivedSafetyCarPerM();
        this.marginalUtilityOfPerceivedSafetyEBike = perceivedSafetyConfigGroup.getMarginalUtilityOfPerceivedSafetyEBikePerM();
        this.marginalUtilityOfPerceivedSafetyEScooter = perceivedSafetyConfigGroup.getMarginalUtilityOfPerceivedSafetyEScooterPerM();
        this.marginalUtilityOfPerceivedSafetyWalk = perceivedSafetyConfigGroup.getMarginalUtilityOfPerceivedSafetyWalkPerM();
        this.marginalUtilityOfPerceivedSafetyCarSd = perceivedSafetyConfigGroup.getMarginalUtilityOfPerceivedSafetyCarPerMSd();
        this.marginalUtilityOfPerceivedSafetyEBikeSd = perceivedSafetyConfigGroup.getMarginalUtilityOfPerceivedSafetyEBikePerMSd();
        this.marginalUtilityOfPerceivedSafetyEscooterSd = perceivedSafetyConfigGroup.getMarginalUtilityOfPerceivedSafetyEScooterPerMSd();
        this.marginalUtilityOfPerceivedSafetyWalkSd = perceivedSafetyConfigGroup.getMarginalUtilityOfPerceivedSafetyWalkPerMSd();
        this.dMaxCar = perceivedSafetyConfigGroup.getDMaxCarPerM();
        this.dMaxEBike = perceivedSafetyConfigGroup.getDMaxEBikePerM();
        this.dMaxEScooter = perceivedSafetyConfigGroup.getDMaxEScooterPerM();
        this.dMaxWalk = perceivedSafetyConfigGroup.getDMaxWalkPerM();
        this.carMode = TransportMode.car;
        this.eBikeMode = perceivedSafetyConfigGroup.getEBikeMode();
        this.eScooterMode = perceivedSafetyConfigGroup.getEScooterMode();
        this.walkMode = perceivedSafetyConfigGroup.getWalkMode();

        this.inputPerceivedSafetyThreshold = perceivedSafetyConfigGroup.getInputPerceivedSafetyThresholdPerM();

        this.vehicles = scenario.getVehicles();
    }



    @Override
    public double computeLinkBasedScore(@NotNull Link link, @NotNull Id<Vehicle> vehicleId) {
        if (vehicles.getVehicles().get(vehicleId) == null) {
            log.fatal("Vehicle with id {} on link {} is not linked to any vehicle of the scenario (null). This should never happen! Aborting!", vehicleId, link.getId());
            throw new NullPointerException();
        }

        String currentMode = vehicles.getVehicles().get(vehicleId).getType().getNetworkMode();

        if(currentMode.equals(carMode)) {
            // if car then
            // set mean beta_psafe, depends on the transport mode
            betaPerceivedSafety = marginalUtilityOfPerceivedSafetyCar;
            // set sd beta_psafe, depends on the transport mode
            sdPerceivedSafety = marginalUtilityOfPerceivedSafetyCarSd;
            // set dmax, depends on the transport mode
            dMax = dMaxCar;
        }
        if(currentMode.equals(eBikeMode)) {
            betaPerceivedSafety = marginalUtilityOfPerceivedSafetyEBike;
            sdPerceivedSafety = marginalUtilityOfPerceivedSafetyEBikeSd;
            dMax = dMaxEBike;
        }
        if (currentMode.equals(eScooterMode)) {
            betaPerceivedSafety = marginalUtilityOfPerceivedSafetyEScooter;
            sdPerceivedSafety = marginalUtilityOfPerceivedSafetyEscooterSd;
            dMax = dMaxEScooter;
        }

        double distance = link.getLength();
        double perceivedSafetyValueOnLink = computePerceivedSafetyValueOnLink(link, currentMode, inputPerceivedSafetyThreshold);
        double distanceBasedPerceivedSafety = perceivedSafetyValueOnLink * distance;

        // in case you want to estimate the additional score based on the weighted mean
        if (dMax == 0) {
            dMax = distance;
        }

        //divide by dmax at the end
        distanceBasedPerceivedSafety = distanceBasedPerceivedSafety / dMax;
        // run Monte Carlo Simulation for the safety perceptions
        double r = generator.nextGaussian();
        // multiply with the random beta parameter
        return (betaPerceivedSafety + r * sdPerceivedSafety) * distanceBasedPerceivedSafety;
    }

    @Override
    public double computeTeleportationBasedScore(double distance, String currentMode) {
        if (currentMode.equals(walkMode)) {
            betaPerceivedSafety = marginalUtilityOfPerceivedSafetyWalk;
            sdPerceivedSafety = marginalUtilityOfPerceivedSafetyWalkSd;
            dMax = dMaxWalk;
            mode = currentMode;
        }

//        TODO: the current scoring of walk is not working. a list of links is retrieved from the leg, which is 0 for teleported legs.
//        thus, the perceived safety score of walk legs only consists of the last link, which is added separately to the list of links.
//        I think one needs to come up with an estimation for mode walk based on the travelled distance.

//        because of above issues: currently returns 0.
        return 0;
    }

    @Override
    public double computePerceivedSafetyValueOnLink(Link link, String mode, int threshold) {
        int varPerceivedSafety = 4;
//        TODO: change to new net attr in configGroup
        if (mode.equals(carMode)) {
            varPerceivedSafety = (int) link.getAttributes().getAttribute(PerceivedSafetyLinkAttributes.PERCEIVED_SAFETY_CAR);
        }
        if (mode.equals(eBikeMode)) {
            varPerceivedSafety = (int) link.getAttributes().getAttribute(PerceivedSafetyLinkAttributes.PERCEIVED_SAFETY_EBIKE);
        }
        if (mode.equals(eScooterMode)) {
            varPerceivedSafety = (int) link.getAttributes().getAttribute(PerceivedSafetyLinkAttributes.PERCEIVED_SAFETY_ESCOOTER);
        }
        if (mode.equals(walkMode)) {
            varPerceivedSafety = (int) link.getAttributes().getAttribute(PerceivedSafetyLinkAttributes.PERCEIVED_SAFETY_WALK);
        }
        return (varPerceivedSafety - threshold);
    }
}
