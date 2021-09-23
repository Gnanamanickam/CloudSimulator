package Simulations

import Utils.{ CreateLogger, ObtainConfigReference}

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
