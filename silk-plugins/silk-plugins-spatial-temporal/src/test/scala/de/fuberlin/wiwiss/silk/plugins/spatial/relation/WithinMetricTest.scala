/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.fuberlin.wiwiss.silk.plugins.spatial.relation

import org.scalatest.Matchers
import org.scalatest.FlatSpec
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

/**
 * Tests the Within Metric.
 * @author Panayiotis Smeros <psmeros@di.uoa.gr> (National and Kapodistrian University of Athens)
 */

@RunWith(classOf[JUnitRunner])
class WithinMetricTest extends FlatSpec with Matchers {

  val metric = new WithinMetric()

  //Within evaluation.
  "WithinMetric test 1" should "return '0.0'" in {
    metric.evaluate("POINT (1 1)", "POLYGON ((0 0, 0 2, 2 2, 2 0, 0 0))", 0.0) should equal(0.0)
  }

  "WithinMetric test 2" should "return '1.0'" in {
    metric.evaluate("POINT (3 3)", "POLYGON ((0 0, 0 2, 2 2, 2 0, 0 0))", 0.0) should equal(0.0)
  }

  "WithinMetric test 3" should "return '0.0'" in {
    metric.evaluate("POINT(10.7381045 52.3717856)", "MULTIPOLYGON(((10.893498420715 52.459671020508,10.893568992615 52.455982208252,10.887680053711 52.444999694824,10.881719589233 52.437721252441,10.875638961792 52.437839508057,10.8756980896 52.434139251709,10.89395904541 52.433811187744,10.894018173218 52.430118560791,10.881840705872 52.430332183838,10.882029533386 52.419239044189,10.894219398499 52.419052124023,10.894149780273 52.422740936279,10.900320053101 52.418949127197,10.882098197937 52.415550231934,10.876010894776 52.415649414063,10.882160186768 52.411849975586,10.906549453735 52.411491394043,10.906619071961 52.40779876709,10.900520324707 52.407878875732,10.900718688965 52.396820068359,10.888519287109 52.396991729736,10.876379966736 52.393482208252,10.87041091919 52.386180877686,10.870540618896 52.378799438477,10.882800102234 52.374931335449,10.882870674133 52.371238708496,10.870658874512 52.371410369873,10.864689826965 52.364109039307,10.85249042511 52.364292144775,10.8709192276 52.356639862061,10.88943862915 52.345352172852,10.87735080719 52.338119506836,10.877409934998 52.334442138672,10.87135887146 52.330810546875,10.865248680115 52.330890655518,10.865309715271 52.327201843262,10.859268188477 52.323589324951,10.847049713135 52.323741912842,10.847108840942 52.320049285889,10.85327911377 52.31628036499,10.841059684754 52.316429138184,10.840880393982 52.32751083374,10.816468238831 52.327831268311,10.810300827026 52.331611633301,10.816358566284 52.335220336914,10.816248893738 52.342620849609,10.822351455689 52.342540740967,10.828388214111 52.34614944458,10.803998947144 52.346488952637,10.804107666016 52.339099884033,10.773630142212 52.339511871338,10.773529052734 52.346920013428,10.749018669128 52.358371734619,10.742930412292 52.358451843262,10.736889839172 52.354808807373,10.724710464478 52.35498046875,10.724618911743 52.362411499023,10.71840763092 52.373630523682,10.700149536133 52.373859405518,10.694019317627 52.3776512146,10.693978309631 52.381370544434,10.687889099121 52.381439208984,10.681769371033 52.385238647461,10.669628143311 52.381660461426,10.669560432434 52.389099121094,10.675608634949 52.392749786377,10.669449806213 52.400268554688,10.663398742676 52.396598815918,10.65721988678 52.407859802246,10.657109260559 52.419040679932,10.651029586792 52.419109344482,10.650938987732 52.43030166626,10.656988143921 52.433971405029,10.650838851929 52.441509246826,10.650809288025 52.445251464844,10.656830787659 52.452651977539,10.668990135193 52.45251083374,10.674969673157 52.463649749756,10.699268341064 52.463329315186,10.699338912964 52.455848693848,10.723629951477 52.455490112305,10.735818862915 52.451568603516,10.729740142822 52.45166015625,10.729779243469 52.447929382324,10.735889434814 52.444110870361,10.741970062256 52.444011688233,10.741889953613 52.451469421387,10.747960090637 52.451370239258,10.747919082642 52.455101013184,10.741810798645 52.458938598633,10.741740226746 52.466411590576,10.7477684021 52.470039367676,10.753840446472 52.469928741455,10.759799957275 52.481029510498,10.783889770508 52.495510101318,10.789989471436 52.491641998291,10.796129226685 52.484050750733,10.820328712463 52.487251281738,10.820369720459 52.483520507813,10.82643032074 52.483379364014,10.838599205017 52.479389190674,10.856839179993 52.475261688233,10.875031471252 52.474849700928,10.881158828735 52.471012115479,10.881279945374 52.463611602783,10.887419700623 52.459789276123,10.893498420715 52.459671020508)))", 0.0) should equal(0.0)
  }

}
