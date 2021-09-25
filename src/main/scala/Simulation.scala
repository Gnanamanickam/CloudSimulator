import Simulations.Simulations.PaaSSimulation2
import Utils.{CreateLogger, ObtainConfigReference}
import Simulations.{BestFitWithTimeScheduledSimulation, IaaSSimulation, PaaSSimulation1, RoundRobinWithSpaceScheduledSimulation, SaaSSimulation}
import com.typesafe.config.ConfigFactory
import org.cloudbus.cloudsim.core.Simulation
import org.slf4j.LoggerFactory

object Simulation:
  val logger = CreateLogger(classOf[Simulation])

  @main def runSimulation =
    logger.info("Constructing a cloud model...")


     println("Press the number to run the simulation")
    printf("1.IaaSSimulation %n" +
      "2.PaaSSimulation1 %n" +
      "3.PaaSSimulation2 %n" +
      "4.RoundRobinWithSpaceScheduledSimulation %n" +
      "5.BestFitWithTimeScheduledSimulation %n" +
      "6.SaaSSimulation %n")

    val a = scala.io.StdIn.readInt()
    a match {
      case 1 => IaaSSimulation.Start()
      case 2 => PaaSSimulation1.Start()
      case 3 => PaaSSimulation2.Start()
      case 4 => RoundRobinWithSpaceScheduledSimulation.Start()
      case 5 => BestFitWithTimeScheduledSimulation.Start()
      case 6 => SaaSSimulation.Start()
    }

    logger.info("Finished cloud simulation...")

class Simulation