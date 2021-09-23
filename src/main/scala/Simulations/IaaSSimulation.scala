package Simulations

import Utils.{CreateLogger, ObtainConfigReference}
import org.cloudbus.cloudsim.datacenters.network.NetworkDatacenter
import org.cloudbus.cloudsim.network.topologies.NetworkTopology
import org.cloudbus.cloudsim.network.DelayMatrix
import org.cloudbus.cloudsim.network.topologies.readers.TopologyReaderBrite

class IaaSSimulation {

  object IaaSSimulation :
    val config = ObtainConfigReference("cloudSimulator") match {
      case Some(value) => value
      case None => throw new RuntimeException("Cannot obtain a reference to the config data.")
    }

    val logger = CreateLogger(classOf[IaaSSimulation])

    def Start() =
      logger.info("Starting the IaaSSimulation")

}
