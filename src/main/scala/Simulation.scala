import Simulations.Simulations.PaaSSimulation2
import Utils.{CreateLogger, ObtainConfigReference}
import Simulations.{CloudletShedulerSpaceShared, CloudletShedulerTimeShared, FirstFitWithTimeScheduledSimulation, IaaSSimulation, LinuxCompletelyFairScheduler, PaaSSimulation1, RoundRobinWithSpaceScheduledSimulation, SharingHostPEsUsingVmSchedulerSpaceShared, SharingHostPEsUsingVmSchedulerTimeShared, TwoCloudletsAndOneSpaceSharedVm, TwoCloudletsAndOneTimeSharedVm, VmSchedulerTimeShared}
import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory

object Simulation:
  val logger = CreateLogger(classOf[Simulation])

  @main def runSimulation =
    logger.info("Constructing a cloud model...")
    FirstFitWithTimeScheduledSimulation.Start()
    logger.info("Finished cloud simulation...")

class Simulation