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

    // Uncomment the one which you want to execute


    IaaSSimulation.Start()
//    PaaSSimulation1.Start()
//    PaaSSimulation2.Start()
//    RoundRobinWithSpaceScheduledSimulation.Start()
//    BestFitWithTimeScheduledSimulation.Start()
//    SaaSSimulation.Start()


//     println("Choose a simulation to run")
//    val a = scala.io.StdIn.readInt()
//    a match {
//      case 1 => new IaaSSimulation
//      case 2 => new PaaSSimulation1
//      case 3 => new PaaSSimulation2
//      case 4 => new RoundRobinWithSpaceScheduledSimulation
//      case 5 => new BestFitWithTimeScheduledSimulation
//      case 6 => new SaaSSimulation
//    }

    logger.info("Finished cloud simulation...")

class Simulation