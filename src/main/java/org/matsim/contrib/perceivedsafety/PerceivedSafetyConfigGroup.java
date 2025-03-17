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

import org.matsim.api.core.v01.TransportMode;
import org.matsim.core.config.ReflectiveConfigGroup;

import java.util.Map;

/**
 * ptzouras @author.
 */
public final class PerceivedSafetyConfigGroup extends ReflectiveConfigGroup {

	public static final String GROUP_NAME = "PerceivedSafety";
	private static final String INPUT_PERCEIVED_SAFETY_CAR = "marginalUtilityOfPerceivedSafety_car_m";
	private static final String INPUT_PERCEIVED_SAFETY_EBIKE = "marginalUtilityOfPerceivedSafety_EBike_m";
	private static final String INPUT_PERCEIVED_SAFETY_ESCOOTER = "marginalUtilityOfPerceivedSafety_EScooter_m";
	private static final String INPUT_PERCEIVED_SAFETY_WALK = "marginalUtilityOfPerceivedSafety_walk_m";
	private static final String INPUT_PERCEIVED_SAFETY_CAR_SD = "marginalUtilityOfPerceivedSafety_car_m_sd";
	private static final String INPUT_PERCEIVED_SAFETY_EBIKE_SD = "marginalUtilityOfPerceivedSafety_EBike_m_sd";
	private static final String INPUT_PERCEIVED_SAFETY_ESCOOTER_SD = "marginalUtilityOfPerceivedSafety_EScooter_m_sd";
	private static final String INPUT_PERCEIVED_SAFETY_WALK_SD = "marginalUtilityOfPerceivedSafety_walk_m_sd";
	private static final String INPUT_DMAX_CAR = "dMax_car_m";
	private static final String INPUT_DMAX_EBIKE = "dMax_EBike_m";
	private static final String INPUT_DMAX_ESCOOTER = "dMax_EScooter_m";
	private static final String INPUT_DMAX_WALK = "dMax_walk_m";
	private static final String EBIKE_MODE = "eBikeMode";
	private static final String ESCOOTER_MODE = "eScooterMode";
	private static final String WALK_MODE = "walkMode";
	private static final String INPUT_PERCEIVED_SAFETY_THRESHOLD = "inputPerceivedSafetyThreshold_m";

	private double marginalUtilityOfPerceivedSafetyCar;
	private double marginalUtilityOfPerceivedSafetyEBike;
	private double marginalUtilityOfPerceivedSafetyEScooter;
	private double marginalUtilityOfPerceivedSafetyWalk;
	private double marginalUtilityOfPerceivedSafetyCarSd;
	private double marginalUtilityOfPerceivedSafetyEBikeSd;
	private double marginalUtilityOfPerceivedSafetyEScooterSd;
	private double marginalUtilityOfPerceivedSafetyWalkSd;
	private double dMaxCar;
	private double dMaxEBike;
	private double dMaxEScooter;
	private double dMaxWalk;
	private int inputPerceivedSafetyThreshold;
	private String eBikeMode = "eBike";
	private String eScooterMode = "eScooter";

	public PerceivedSafetyConfigGroup(){
		super(GROUP_NAME);
		}

	@Override
	public Map<String, String> getComments() {
		Map<String,String> map = super.getComments();
		map.put(INPUT_PERCEIVED_SAFETY_CAR, "marginalUtilityOfPerceivedSafety_car");
		map.put(INPUT_PERCEIVED_SAFETY_EBIKE, "marginalUtilityOfPerceivedSafety_eBike");
		map.put(INPUT_PERCEIVED_SAFETY_ESCOOTER, "marginalUtilityOfPerceivedSafety_eScooter");
		map.put(INPUT_PERCEIVED_SAFETY_WALK, "marginalUtilityOfPerceivedSafety_walk");
		map.put(INPUT_PERCEIVED_SAFETY_CAR_SD, "marginalUtilityOfPerceivedSafety_car_sd");
		map.put(INPUT_DMAX_CAR, "dMax_car");
		map.put(INPUT_DMAX_EBIKE, "dMax_EBike");
		map.put(INPUT_DMAX_ESCOOTER, "dMax_EScooter");
		map.put(INPUT_PERCEIVED_SAFETY_THRESHOLD, "inputPerceivedSafetyThreshold");
		return map;
	}


	@StringSetter(INPUT_PERCEIVED_SAFETY_CAR)
	public void setMarginalUtilityOfPerceivedSafetyCarPerM(final double value) {
		// PROSTHESE MONTE CARLO SIMULATION
		this.marginalUtilityOfPerceivedSafetyCar = value;
	}
	@StringGetter(INPUT_PERCEIVED_SAFETY_CAR)
	public double getMarginalUtilityOfPerceivedSafetyCarPerM() {
		return this.marginalUtilityOfPerceivedSafetyCar;
	}

	@StringSetter(INPUT_PERCEIVED_SAFETY_EBIKE)
	public void setMarginalUtilityOfPerceivedSafetyEBikePerM(final double value) {
		this.marginalUtilityOfPerceivedSafetyEBike = value;
	}
	@StringGetter(INPUT_PERCEIVED_SAFETY_EBIKE)
	public double getMarginalUtilityOfPerceivedSafetyEBikePerM() {
		return this.marginalUtilityOfPerceivedSafetyEBike;
	}

	@StringSetter(INPUT_PERCEIVED_SAFETY_ESCOOTER)
	public void setMarginalUtilityOfPerceivedSafetyEScooterPerM(final double value) {
		this.marginalUtilityOfPerceivedSafetyEScooter = value;
	}
	@StringGetter(INPUT_PERCEIVED_SAFETY_ESCOOTER)
	public double getMarginalUtilityOfPerceivedSafetyEScooterPerM() {
		return this.marginalUtilityOfPerceivedSafetyEScooter;
	}

	@StringSetter(INPUT_PERCEIVED_SAFETY_WALK)
	public void setMarginalUtilityOfPerceivedSafetyWalkPerM(final double value) {
		this.marginalUtilityOfPerceivedSafetyWalk = value;
	}
	@StringGetter(INPUT_PERCEIVED_SAFETY_WALK)
	public double getMarginalUtilityOfPerceivedSafetyWalkPerM() {
		return this.marginalUtilityOfPerceivedSafetyWalk;
	}

	@StringSetter(INPUT_PERCEIVED_SAFETY_CAR_SD)
	public void setMarginalUtilityOfPerceivedSafetyCarPerMSd(final double value) {
		// PROSTHESE MONTE CARLO SIMULATION
		this.marginalUtilityOfPerceivedSafetyCarSd = value;
	}
	@StringGetter(INPUT_PERCEIVED_SAFETY_CAR_SD)
	public double getMarginalUtilityOfPerceivedSafetyCarPerMSd() {
		return this.marginalUtilityOfPerceivedSafetyCarSd;
	}

	@StringSetter(INPUT_PERCEIVED_SAFETY_EBIKE_SD)
	public void setMarginalUtilityOfPerceivedSafetyEBikePerMSd(final double value) {
		// PROSTHESE MONTE CARLO SIMULATION
		this.marginalUtilityOfPerceivedSafetyEBikeSd = value;
	}
	@StringGetter(INPUT_PERCEIVED_SAFETY_EBIKE_SD)
	public double getMarginalUtilityOfPerceivedSafetyEBikePerMSd() {
		return this.marginalUtilityOfPerceivedSafetyEBikeSd;
	}

	@StringSetter(INPUT_PERCEIVED_SAFETY_ESCOOTER_SD)
	public void setMarginalUtilityOfPerceivedSafetyEScooterPerMSd(final double value) {
		// PROSTHESE MONTE CARLO SIMULATION
		this.marginalUtilityOfPerceivedSafetyEScooterSd = value;
	}
	@StringGetter(INPUT_PERCEIVED_SAFETY_ESCOOTER_SD)
	public double getMarginalUtilityOfPerceivedSafetyEScooterPerMSd() {
		return this.marginalUtilityOfPerceivedSafetyEScooterSd;
	}

	@StringSetter(INPUT_PERCEIVED_SAFETY_WALK_SD)
	public void setMarginalUtilityOfPerceivedSafetyWalkPerMSd(final double value) {
		// PROSTHESE MONTE CARLO SIMULATION
		this.marginalUtilityOfPerceivedSafetyWalkSd = value;
	}
	@StringGetter(INPUT_PERCEIVED_SAFETY_WALK_SD)
	public double getMarginalUtilityOfPerceivedSafetyWalkPerMSd() {
		return this.marginalUtilityOfPerceivedSafetyWalkSd;
	}

	@StringSetter(INPUT_DMAX_CAR)
	public void setDMaxCarPerM(final double value) {
		this.dMaxCar = value;
	}
	@StringGetter(INPUT_DMAX_CAR)
	public double getDMaxCarPerM() {
		return this.dMaxCar;
	}

	@StringSetter(INPUT_DMAX_EBIKE)
	public void setDMaxEBikePerM(final double value) {
		this.dMaxEBike = value;
	}
	@StringGetter(INPUT_DMAX_EBIKE)
	public double getDMaxEBikePerM() {
		return this.dMaxEBike;
	}

	@StringSetter(INPUT_DMAX_ESCOOTER)
	public void setDMaxEScooterPerM(final double value) {
		this.dMaxEScooter = value;
	}
	@StringGetter(INPUT_DMAX_ESCOOTER)
	public double getDMaxEScooterPerM() {
		return this.dMaxEScooter;
	}

	@StringSetter(INPUT_DMAX_WALK)
	public void setDMaxWalkPerM(final double value) {
		this.dMaxWalk = value;
	}
	@StringGetter(INPUT_DMAX_WALK)
	public double getDMaxWalkPerM() {
		return this.dMaxWalk;
	}

	@StringGetter(EBIKE_MODE)
	public String getEBikeMode() {
		return this.eBikeMode;
	}

	@StringGetter(ESCOOTER_MODE)
	public String getEScooterMode() {
		return this.eScooterMode;
	}

	@StringGetter(WALK_MODE)
	public String getWalkMode() {
		return TransportMode.walk;
	}

	@StringSetter(INPUT_PERCEIVED_SAFETY_THRESHOLD)
	public void setInputPerceivedSafetyThresholdPerM(final int value) {
		this.inputPerceivedSafetyThreshold = value;
	}
	@StringGetter(INPUT_PERCEIVED_SAFETY_THRESHOLD)
	public int getInputPerceivedSafetyThresholdPerM() {
		return this.inputPerceivedSafetyThreshold;
	}
}
