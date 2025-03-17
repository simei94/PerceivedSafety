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

package org.matsim.contrib.scenarioAthens.run;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;

//// new modules, new modules... PsafeChoices package.
import org.matsim.contrib.perceivedsafety.PerceivedSafetyConfigGroup;
// import org.matsim.contrib.Psafe.PsafeInput;
// import org.matsim.contrib.Psafe.PsafeNewAttrib;
import org.matsim.contrib.perceivedsafety.PerceivedSafetyModule;

import org.matsim.core.config.groups.ReplanningConfigGroup.StrategySettings;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.ScoringConfigGroup.ActivityParams;
import org.matsim.core.config.groups.ScoringConfigGroup.ModeParams;
import org.matsim.core.config.groups.QSimConfigGroup;

import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy.OverwriteFileSetting;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.vehicles.VehicleType;
import org.matsim.vehicles.VehicleUtils;
import org.matsim.vehicles.VehiclesFactory;

import java.util.ArrayList;
import java.util.List;

// import static org.matsim.core.config.groups.ControlerConfigGroup.RoutingAlgorithmType.FastAStarLandmarks;
/**
 * @author ptzouras
 */
public class RunScenarioAthens {
	private static final Logger LOG = LogManager.getLogger(RunScenarioAthens.class);

	public static void main(String[] args) {
		Config config;
		if (args.length == 1) {
			LOG.info("A user-specified config.xml file was provided. Using it...");
			config = ConfigUtils.loadConfig(args[0], new PerceivedSafetyConfigGroup());
			fillConfigWithBicycleStandardValues(config);
		} else if (args.length == 0) {
			LOG.info("No config.xml file was provided. Using 'standard' example files given in this contrib's resources folder.");

			config = ConfigUtils.createConfig("ScenarioAthensEquity/");

			config.addModule(new PerceivedSafetyConfigGroup());
			
			fillConfigWithBicycleStandardValues(config);

			config.network().setInputFile("network_psafest_scenario00.xml"); // Modify this
			config.plans().setInputFile("cropped_plans.xml");
		
		} else {
			throw new RuntimeException("More than one argument was provided. There is no procedure for this situation. Thus aborting!"
								     + " Provide either (1) only a suitable config file or (2) no argument at all to run example with given example of resources folder.");
		}
		config.controller().setLastIteration(500);
		
		new RunScenarioAthens().run(config);
	}

	static void fillConfigWithBicycleStandardValues(Config config) {
		config.controller().setWriteEventsInterval(1);

	    PerceivedSafetyConfigGroup perceivedSafetyConfigGroup = (PerceivedSafetyConfigGroup) config.getModules().get(PerceivedSafetyConfigGroup.GROUP_NAME);
	
        perceivedSafetyConfigGroup.setMarginalUtilityOfPerceivedSafetyCarPerM(0.44); // different beta psafes
        perceivedSafetyConfigGroup.setMarginalUtilityOfPerceivedSafetyEBikePerM(0.84);
        perceivedSafetyConfigGroup.setMarginalUtilityOfPerceivedSafetyEScooterPerM(0.76);
        perceivedSafetyConfigGroup.setMarginalUtilityOfPerceivedSafetyWalkPerM(0.33);
        
        perceivedSafetyConfigGroup.setMarginalUtilityOfPerceivedSafetyCarPerMSd(0.20);
        perceivedSafetyConfigGroup.setMarginalUtilityOfPerceivedSafetyEBikePerMSd(0.22);
        perceivedSafetyConfigGroup.setMarginalUtilityOfPerceivedSafetyEScooterPerMSd(0.07);
        perceivedSafetyConfigGroup.setMarginalUtilityOfPerceivedSafetyWalkPerMSd(0.17);
        
      	perceivedSafetyConfigGroup.setDMaxCarPerM(0); // in meters or kilometers??? if  0 then weighted average
      	perceivedSafetyConfigGroup.setDMaxEBikePerM(0); // in meters or kilometers???
      	perceivedSafetyConfigGroup.setDMaxEScooterPerM(0); // in meters or kilometers???
        perceivedSafetyConfigGroup.setDMaxWalkPerM(0); // in meters or kilometers???
      	
      	perceivedSafetyConfigGroup.setInputPerceivedSafetyThresholdPerM(4);
		
		List<String> mainModeList = new ArrayList<>();
		
		mainModeList.add(TransportMode.car);
		// mainModeList.add("car");
		mainModeList.add("ebike");
		mainModeList.add("escoot");
		// mainModeList.add("walk");
		
		config.qsim().setMainModes(mainModeList);
		
		config.transit().setUseTransit(false);
		// config.qsim().setUsingTravelTimeCheckInTeleportation(false);

		config.replanning().setMaxAgentPlanMemorySize(5);
		config.replanning().addStrategySettings( new StrategySettings().setStrategyName("ChangeExpBeta" ).setWeight(0.8 ) );
		config.replanning().addStrategySettings( new StrategySettings().setStrategyName("ReRoute" ).setWeight(0.05 ) );
//		config.replanning().addStrategySettings( new StrategySettings().setStrategyName("ChangeTripMode" ).setWeight(0.2);

		final StrategySettings strategySettings = new StrategySettings(Id.create("1", StrategySettings.class));
		strategySettings.setStrategyName("ChangeTripMode");
		strategySettings.setWeight(0.15);
		config.replanning().addStrategySettings(strategySettings);
		// config.setParam("changeMode", "modes", "car,walk");
		String[] str = {TransportMode.car, "ebike","escoot"};
		config.changeMode().setModes(str);

		config.scoring().addActivityParams( new ActivityParams("home").setTypicalDuration(12*60*60 ) );
		config.scoring().addActivityParams( new ActivityParams("work").setTypicalDuration(8*60*60 ) );
		
		config.scoring().addActivityParams( new ActivityParams("shop").setTypicalDuration(8*60*60 ) );
		config.scoring().addActivityParams( new ActivityParams("other").setTypicalDuration(8*60*60 ) );
		config.scoring().addActivityParams( new ActivityParams("education").setTypicalDuration(8*60*60 ) );
		config.scoring().addActivityParams( new ActivityParams("recreation").setTypicalDuration(8*60*60 ) );

		config.scoring().addModeParams( new ModeParams(TransportMode.car).setConstant(0.).setMarginalUtilityOfDistance(-0.000042 ).setMarginalUtilityOfTraveling(-1.8 ).setMonetaryDistanceRate(0.) );
		config.scoring().addModeParams( new ModeParams("ebike").setConstant(0.10 ).setMarginalUtilityOfDistance(-0.000112 ).setMarginalUtilityOfTraveling(-2.4 ).setMonetaryDistanceRate(0. ) );
		config.scoring().addModeParams( new ModeParams("escoot").setConstant(-0.41).setMarginalUtilityOfDistance(-0.0002484 ).setMarginalUtilityOfTraveling(-3.0).setMonetaryDistanceRate(0.) );
		// config.planCalcScore().addModeParams( new ModeParams("walk").setConstant(-0.35).setMarginalUtilityOfDistance(0.0).setMarginalUtilityOfTraveling(-2.5).setMonetaryDistanceRate(0.) );
		
		config.routing().setNetworkModes(mainModeList);
		config.routing().setRoutingRandomness(3.);
	}

	public void run(Config config) {
		config.global().setNumberOfThreads(1);
		
		config.controller().setOverwriteFileSetting(OverwriteFileSetting.deleteDirectoryIfExists);

		Scenario scenario = ScenarioUtils.loadScenario(config);

		// set config such that the mode vehicles come from vehicles data:
		scenario.getConfig().qsim().setVehiclesSource(QSimConfigGroup.VehiclesSource.modeVehicleTypesFromVehiclesData);

		// now put hte mode vehicles into the vehicles data:
		final VehiclesFactory vf = VehicleUtils.getFactory();
		
		// scenario.getVehicles().addVehicleType( vf.createVehicleType(Id.create(TransportMode.car, VehicleType.class ) ) );
		scenario.getVehicles().addVehicleType( vf.createVehicleType(Id.create(TransportMode.car, VehicleType.class ) ).setMaximumVelocity(25.0 ).setPcuEquivalents(1.0 ) );
		scenario.getVehicles().addVehicleType( vf.createVehicleType(Id.create("ebike", VehicleType.class ) ).setMaximumVelocity(6.95 ).setPcuEquivalents(0.50 ) );
		scenario.getVehicles().addVehicleType( vf.createVehicleType(Id.create("escoot", VehicleType.class ) ).setMaximumVelocity(6.95 ).setPcuEquivalents(0.25 ) );
		// scenario.getVehicles().addVehicleType( vf.createVehicleType(Id.create("walk", VehicleType.class ) ).setMaximumVelocity(1.38 ).setPcuEquivalents(0.125 ) );
		
		
		Controler controler = new Controler(scenario);

		
		controler.addOverridingModule(new PerceivedSafetyModule()); // without that the model is running with the classical algorithms

		controler.run();
	}
}
