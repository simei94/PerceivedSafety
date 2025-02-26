# The MATSim Psafe Module

The Psafe Module extends the scoring function of [MATSim](https://github.com/matsim-org) so that perceived safety rates are considered. As can be seen in Equation below, travel disutility now depends on time, distance (i.e., cost), and safety. The beta coefficient ($\beta_{psafe,m(q)}$ parameter) represents the overall weight of safety perceptions in the plan selection process, influencing mode or route choice. It is inspired by [Bicycle](https://github.com/matsim-org/matsim-libs/tree/master/contribs/bicycle) contribution aiming to bring out an updated version. 

**The [MATSim Psafe Module](https://github.com/panogjuras/Psafe) repository contains:**
- [Psafe package](https://github.com/panosgjuras/Psafe/tree/main/src/main/java/org/matsim/contrib/Psafe): includes all the classes to extend the MATSim scoring so that perceived safety is considered.
- [ScenarioAthens run package](https://github.com/panosgjuras/Psafe/tree/main/src/main/java/org/matsim/contrib/scenarioAthens/run): contains the public class to run simulation experiments using the ScenarioAthens.
- [ScenarioAthens](https://github.com/panosgjuras/Psafe/tree/main/src/main/resources): some toy scenarios developed for the city center of Athens for further experimentation.

The Module can be utilized for research and education purposes. A Getting Started document will be prepared soon.

Overall, perceived safety is an attribute of the links incuded in the network. The calculation of scores can be performed based on the tools uploaded in Perceived_Safety_Choices repository (a new version will be online soon).

To incorporate perceived safety, a threshold level is established, where safety ratings below the threshold decrease trip utility and vice versa. Perceived safety values are then multiplied by the ratio of the distance of each link to the distance threshold (${cd}_{m\left(q\right)}$ parameter). The parameter represents the level of unsafe distance a road user is willing to tolerate during a short trip. This is closely linked to the user’s experience, familiarity with, and tolerance for minor unsafe gaps that may arise along their route. A lower distance threshold indicates a higher contribution of perceived safety to the overall utility of a specific transport mode. The Psafe module also enables the use of a variable distance threshold rather than a fixed one. It assesses the impact of perceived safety by applying a distance-weighted average. In this approach, the distance threshold corresponds to the total length of each trip.

$S_{trav,m(q)} = \left[ C_{m(q)} + \beta_{trav,m(q)} \times t_{trav,m(q)} + \beta_{mon} \times \Delta m_q + \left(\frac{\beta_{d,m(q)}}{\gamma_{d,m(q)} + \beta_{mon}}\right) \times d_{trav,q} \right]$

$+ \beta_{psafe,m(q)} \times \sum_i \left[ \frac{(psafe_{i,m} - c_{psafe}) \times d_{trav,i}}{cd_{m(q)}} \right]$

where:

$S_{trav,m(q)}$: sum of all travel (dis)utilities of trip \(q) (e.g., travel time, cost, distance, etc.);  
$C_{m(q)}$: mode specific constant of mode \(m);  
$\beta_{trav,m(q)}$: the marginal utility of travel time of mode \(m);  
$t_{trav,m(q)}$: the travel time in hours of using mode \(m) in trip \(q);  
$\beta_{mon}$: the marginal utility of money;  
$\Delta m_q$: the change in the monetary budget in euros (equal to zero in this case);  
$\beta_{d,m(q)}$: the marginal utility of distance of mode \(m);  
$\gamma_{d,m(q)}$: the monetary distance rate of mode \(m);  
$d_{trav,q}$: the travel distance of trip \(q) in meters;  
$d_{trav,i}$: the travel distance in link \(i) in meters, by definition: $d_{trav,i} \le d_{trav,q}$;  
$\beta_{psafe,m(q)}$: the marginal utility of perceived safety of mode (m);  
$psafe_{i,m}$: the perceived safety level of link \( i \) of mode \(m) in Levels;  
$c_{psafe}$: the perceived safety threshold (Level 4 is recommended);  
$cd_{m(q)}$: the distance threshold of mode \(m).

It is Maven project; all dependencies are included in the [pom.xml](pom.xml) file. MATSim is a major dependency that should be installed.

The tools contained in this repository were developed within various research project of [Laboratory of Transportation Engineering](http://lte.survey.ntua.gr/main/en/) of National Technical University of Athens.

When referencing the contrib, please use the following papers:
> Tzouras, P.G., Mitropoulos, L., Karolemeas, C., Stravropoulou, E., Vlahogianni, E.I., Kepaptsoglou, K., 2024. Agent-based simulation model of micro-mobility trips in heterogeneous and perceived unsafe road environments. Journal of Cycling and Micromobility Research 2, 100042. [https://doi.org/10.1016/j.jcmr.2024.100042]

