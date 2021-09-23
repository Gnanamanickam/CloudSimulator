package Simulations

import Utils.{CreateLogger, ObtainConfigReference}

class SaaSSimulation {

  val config = ObtainConfigReference("cloudSimulator") match {
    case Some(value) => value
    case None => throw new RuntimeException("Cannot obtain a reference to the config data.")
  }

  val logger = CreateLogger(classOf[IaaSSimulation])

  def Start() =
    logger.info("Starting the SaaSSimulation")

}
