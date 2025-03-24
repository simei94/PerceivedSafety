package org.matsim.contrib.perceivedsafety;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.matsim.api.core.v01.Scenario;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.controler.AbstractModule;
import org.matsim.core.mobsim.qsim.AbstractQSimModule;
import org.matsim.core.mobsim.qsim.qnetsimengine.ConfigurableQNetworkFactory;
import org.matsim.core.mobsim.qsim.qnetsimengine.QNetworkFactory;

/**
 * Perceived Safety Module, which enables the perceived safety scoring function.
 */
public final class PerceivedSafetyModule extends AbstractModule {

	/**
	 * installs the module.
	 */
	public void install() {
//		TODO: check if this is done properly or if this overrides the current scoring function!
//		Here, additional parameters should be added, but not by replacing the usual scoring function
//		bindScoringFunctionFactory().to(PerceivedSafetyScoringFunctionFactory.class).in(Singleton.class);

//		as expected: the above binding overwrites the usual scoring function instead of adding to it. We need the following ifrastructure to re-structure this:
//		1) PerceivedSafetyScoreEventsCreator.class which throws the scoring events. This class does what method calcLegScore of PerceivedSafetyScoring does.
//		2) AdditionalPerceivedSafetyLinkScore interface for
//		3) AdditionalPerceivedSafetyLinkScoreDefaultImpl which has all the marginal ut values from PerceivedSafetyScoring and a method computeLinkBasedScore which calcs the scores.
//		This method is then called in 1) to calc the scores.

//		add the scoring of perceived safety scores to the default matsim scoring
		this.addEventHandlerBinding().to(PerceivedSafetyScoreEventsCreator.class);
		this.bind(AdditionalPerceivedSafetyLinkScore.class).to(AdditionalPerceivedSafetyLinkScoreDefaultImpl.class);

		this.installOverridingQSimModule(new AbstractQSimModule() {
			@Inject EventsManager events;
			@Inject Scenario scenario;
			@Override protected void configureQSim(){
				final ConfigurableQNetworkFactory factory = new ConfigurableQNetworkFactory(events, scenario);
				bind(QNetworkFactory.class).toInstance(factory);}
		});
	}
}
