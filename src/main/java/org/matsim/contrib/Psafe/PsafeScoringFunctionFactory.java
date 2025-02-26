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

package org.matsim.contrib.Psafe;

import com.google.inject.Inject;
import org.matsim.api.core.v01.Scenario;
// import org.matsim.api.core.v01.events.Event;
import org.matsim.api.core.v01.population.Person;
// import org.matsim.contrib.bicycle.BicycleConfigGroup.BicycleScoringType;
// import org.matsim.core.api.experimental.events.EventsManager;
// import org.matsim.core.events.handler.BasicEventHandler;
import org.matsim.core.scoring.ScoringFunction;
import org.matsim.core.scoring.ScoringFunctionFactory;
import org.matsim.core.scoring.SumScoringFunction;
import org.matsim.core.scoring.functions.*;

/**
 * @author ptzouras
 */

final class PsafeScoringFunctionFactory implements ScoringFunctionFactory {

	@Inject
	private ScoringParametersForPerson parameters;

	@Inject
	private Scenario scenario;


	@Inject
	private PsafeConfigGroup psafeConfigGroup;

	@Inject
	private PsafeScoringFunctionFactory() {
	}
	
	@Override
	public ScoringFunction createNewScoringFunction(Person person) {
		SumScoringFunction sumScoringFunction = new SumScoringFunction();

		final ScoringParameters params = parameters.getScoringParameters(person);
		sumScoringFunction.addScoringFunction(new CharyparNagelActivityScoring(params)) ;
		sumScoringFunction.addScoringFunction(new CharyparNagelAgentStuckScoring(params));
		sumScoringFunction.addScoringFunction(new CharyparNagelMoneyScoring( params ));

		sumScoringFunction.addScoringFunction(new PsafeNewStyleScoring(params, scenario.getNetwork(), scenario.getConfig().transit().getTransitModes(), psafeConfigGroup));

		return sumScoringFunction;
	}

}
