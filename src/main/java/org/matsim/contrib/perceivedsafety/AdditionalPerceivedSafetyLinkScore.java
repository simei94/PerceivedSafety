package org.matsim.contrib.perceivedsafety;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.vehicles.Vehicle;

public interface AdditionalPerceivedSafetyLinkScore {
    double computeLinkBasedScore(Link link, Id<Vehicle> vehicleId);
    double computeTeleportationBasedScore(double distance, String mode);
    double computePerceivedSafetyValueOnLink(Link link, String mode, int threshold);
}
