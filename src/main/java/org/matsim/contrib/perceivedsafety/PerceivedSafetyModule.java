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
		bindScoringFunctionFactory().to(PerceivedSafetyScoringFunctionFactory.class).in(Singleton.class);

		this.installOverridingQSimModule(new AbstractQSimModule() {
			@Inject EventsManager events;
			@Inject Scenario scenario;
			@Override protected void configureQSim(){
				final ConfigurableQNetworkFactory factory = new ConfigurableQNetworkFactory(events, scenario);
				bind(QNetworkFactory.class).toInstance(factory);}
		});
	}
}
