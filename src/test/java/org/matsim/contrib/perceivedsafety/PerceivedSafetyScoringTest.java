package org.matsim.contrib.perceivedsafety;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.population.*;
import org.matsim.application.ApplicationUtils;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.Controler;
import org.matsim.core.population.PopulationUtils;
import org.matsim.core.scenario.MutableScenario;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.examples.ExamplesUtils;
import org.matsim.testcases.MatsimTestUtils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

import static org.matsim.contrib.perceivedsafety.PerceivedSafetyUtils.E_BIKE;
import static org.matsim.contrib.perceivedsafety.PerceivedSafetyUtils.E_SCOOTER;

public class PerceivedSafetyScoringTest {

    @RegisterExtension
    MatsimTestUtils utils = new MatsimTestUtils();

    private static final Id<Person> PERSON_ID = Id.createPersonId("testPerson");

    @Test
    public void testPerceivedSafetyScoring() {
//        TODO: steps
//        create sample person in sample population
//        get "expected" value from old approach.
//        run sim and compare new score to old score, where new score = new sccore!!
//        run sim for each different mode?!

        for (String mode : Set.of(E_BIKE, E_SCOOTER)) {
            Path context = Paths.get(ExamplesUtils.getTestScenarioURL("equil").toString());

            Config config = ConfigUtils.loadConfig(context.resolve("config.xml").toString());

//        general config settings
            config.controller().setOutputDirectory(utils.getOutputDirectory());
            config.scoring().setWriteExperiencedPlans(true);
            config.scoring().getActivityParams()
                    .forEach(a -> a.setScoringThisActivityAtAll(false));

//      add perceivedSafetyCfgGroup and configure
            PerceivedSafetyConfigGroup perceivedSafetyConfigGroup = ConfigUtils.addOrGetModule(config, PerceivedSafetyConfigGroup.class);
            PerceivedSafetyUtils.fillConfigWithBicyclePerceivedSafetyDefaultValues(perceivedSafetyConfigGroup);

//        TODO: not sure if this works, we may have to run ScenarioUtils.createMutableScenario and then load it afterwards?
            MutableScenario scenario = (MutableScenario) ScenarioUtils.loadScenario(config);
            createAndAddTestPopulation(scenario, mode);

//            add mode to network links
            scenario.getNetwork().getLinks().values()
                    .forEach(l -> {
                        Set<String> allowedModes = new java.util.HashSet<>(Set.of(mode));
                        allowedModes.addAll(l.getAllowedModes());
                        l.setAllowedModes(allowedModes);
                    });

            Controler controler = new Controler(scenario);
            controler.addOverridingModule(new PerceivedSafetyModule());
            controler.run();

//            read experienced plans and get score of plan
            String experiencedPlansPath = ApplicationUtils.globFile(Paths.get(utils.getOutputDirectory()), "*output_experienced_plans.xml.gz").toString();
            Population experiencedPlans = PopulationUtils.readPopulation(experiencedPlansPath);

//            score of plan should only consist of leg scores as all act types are set to "dont score"
//            TODO set experienced value
            Assertions.assertEquals("TODO", experiencedPlans.getPersons().get(PERSON_ID).getSelectedPlan().getScore());
        }
    }

    private void createAndAddTestPopulation(MutableScenario scenario, String mode) {
        Population pop = PopulationUtils.createPopulation(scenario.getConfig());
        PopulationFactory fac = pop.getFactory();

        Activity home = fac.createActivityFromLinkId("h", Id.createLinkId("20"));
        home.setEndTime(8 * 3600.);
        Leg leg = fac.createLeg(mode);
//        TODO: ideally the agent only travels 1 link. maybe we have to set the destination act (below) to link 20, too?!
        Activity work = fac.createActivityFromLinkId("w", Id.createLinkId("21"));
        work.setEndTime(9 * 3600.);

        Plan plan = fac.createPlan();
        plan.addActivity(home);
        plan.addLeg(leg);
        plan.addActivity(work);

        Person person = fac.createPerson(PERSON_ID);
        person.addPlan(plan);
        person.setSelectedPlan(plan);
//        TODO: maybe set some person attrs if needed?

        pop.addPerson(person);
        scenario.setPopulation(pop);
    }
}
