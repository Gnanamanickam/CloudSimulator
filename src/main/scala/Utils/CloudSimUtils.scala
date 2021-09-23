package Utils

import jdk.nashorn.internal.runtime.ConsString
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicySimple
import org.cloudbus.cloudsim.brokers.DatacenterBroker
import org.cloudbus.cloudsim.cloudlets.{Cloudlet, CloudletSimple}
import org.cloudbus.cloudsim.core.CloudSim
import org.cloudbus.cloudsim.datacenters.{Datacenter, DatacenterSimple}
import org.cloudbus.cloudsim.hosts.{Host, HostSimple}
import org.cloudbus.cloudsim.provisioners.{PeProvisionerSimple, ResourceProvisioner, ResourceProvisionerSimple}
import org.cloudbus.cloudsim.resources.{Pe, PeSimple}
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletScheduler
import org.cloudbus.cloudsim.schedulers.vm.{VmScheduler, VmSchedulerTimeShared}
import org.cloudbus.cloudsim.utilizationmodels.{UtilizationModel, UtilizationModelDynamic, UtilizationModelFull}
import org.cloudbus.cloudsim.vms.{Vm, VmSimple}
import org.slf4j.LoggerFactory

import java.text.DecimalFormat
import scala.jdk.CollectionConverters.{BufferHasAsJava, CollectionHasAsScala}
import java.util
import scala.collection.mutable.ListBuffer

object CloudSimUtils {

  val logger = LoggerFactory.getLogger(CloudSimUtils.getClass)

  val config = ObtainConfigReference("cloudSimulator") match {
    case Some(value) => value
    case None => throw new RuntimeException("Cannot obtain a reference to the config data.")
  }

  //  object CloudSimUtil:
  /**
   * Method to create VM for a specific config
   *
   * @param simulationName    The simulation for which VM has to be created based on the given config
   * @param cloudletScheduler policy which generated Vm should respect while cloudlet allocations
   * @return Returns the generated Virtual Machine
   */

  def createVirtualMachine(simulationName: String, cloudletScheduler: CloudletScheduler): List[Vm] = {
    logger.info("Creating VM")
    val vmPes = List(new PeSimple(config.getLong("cloudSimulator." + simulationName + ".vm.mipsCapacity")))
    val vms = config.getInt("cloudSimulator." + simulationName + ".Vms")
    val vmPesLength = config.getInt("cloudSimulator." + simulationName + ".VmPes")

    val list = (1 to vms).toList.map( size =>
      new VmSimple(config.getLong("cloudSimulator." + simulationName + ".vm.mipsCapacity"), vmPesLength, cloudletScheduler)
        .setRam(config.getLong("cloudSimulator." + simulationName + ".vm.RAMInMBs"))
        .setBw(config.getLong("cloudSimulator." + simulationName + ".vm.BandwidthInMBps"))
        .setSize(config.getLong("cloudSimulator." + simulationName + ".vm.StorageInMBs"))
        .setCloudletScheduler(cloudletScheduler)
    )

    return list
  }

  /**
   * Method to create DataCenter based on the given configuration
   *
   * @param simulationName The simulation for which VM has to be created based on the given config
   * @param cloudSim       sends and processes all discrete events during the simulation time.
   * @param Cost           Boolean value which returns true if the cost parameters are involved in creation
   * @return Returns the generated Datacenter
   */

  def createDatacenter(simulationName: String, cloudSim: CloudSim, Cost: Boolean): Datacenter = {
    logger.info("Creating DataCenter")
    val hostNumber = config.getInt("cloudSimulator." + simulationName + ".Hosts")
    val hostList = new util.ArrayList[Host](hostNumber)
    //    for (i <- 0 until hostNumber) {
    //      val host: Host = createHost(simulationName)
    //      hostList.add(host)
    //    }
    (1 to hostNumber).map(hostNum => hostList.add(createHost(simulationName)))
    val dataCenter: Datacenter = new DatacenterSimple(cloudSim, hostList, new VmAllocationPolicySimple)
    if (Cost) {
      logger.info("Set DataCenter Characteristics")
      dataCenter.getCharacteristics
        .setArchitecture(config.getString("architecture"))
        .setOs(config.getString("os"))
        .setVmm(config.getString("vmm"))
        .setCostPerBw(config.getDouble("costPerBw"))
        .setCostPerStorage(config.getDouble("costPerStorage"))
        .setCostPerMem(config.getDouble("costPerMem"))
        .setCostPerSecond(config.getDouble("costPerSec"))
    }

    return dataCenter
  }

  /**
   * Method to create HOST based on the given configuration
   *
   * @param simulationName The simulation for which VM has to be created based on the given config
   * @return Returns the generated Host
   */

  def createHost(simulationName: String): Host = {
    logger.info("Creating Host")
    val hostPes = config.getInt("cloudSimulator." + simulationName + ".HostPes")
    val peList = new util.ArrayList[Pe](hostPes)
    //List of Host's Processing Elements(CPU)
    logger.info("Creating Processing Element")
    (1 to hostPes).map(Pes =>
      peList.add(new PeSimple(config.getLong("cloudSimulator." + simulationName + ".host.mipsCapacity"), new PeProvisionerSimple))
    )
    val RAM: Long = config.getLong("cloudSimulator." + simulationName + ".host.RAMInMBs")
    val BandWidth: Long = config.getLong("cloudSimulator." + simulationName + ".host.StorageInMBs")
    val Storage: Long = config.getLong("cloudSimulator." + simulationName + ".host.BandwidthInMBps")

    val RAMProvisioner: ResourceProvisioner = new ResourceProvisionerSimple
    val BWProvisioner: ResourceProvisioner = new ResourceProvisionerSimple
    val VMScheduler: VmScheduler = new VmSchedulerTimeShared

    val host: Host = new HostSimple(RAM, BandWidth, Storage, peList)
    host.setRamProvisioner(RAMProvisioner).setBwProvisioner(BWProvisioner).setVmScheduler(VMScheduler)
    host.enableStateHistory()
    return host
  }

  /**
   * Method to create Cloudlets based on the given configuration
   *
   * @param simulationName The simulation for which VM has to be created based on the given config
   * @param utlizationDynamic Returns true if utilization model is dynamic else set to full
   * @return Returns the generated Cloudlets
   */

  def createCloudlets(simulationName: String, utilizationDynamic: Boolean): List[Cloudlet] = {
    logger.info("Creating Cloudlets")
    val hostCloudlets = config.getInt("cloudSimulator." + simulationName + ".cloudlet.HostCloudlets")
    var utilization: UtilizationModel  = null // Used var to dynamically set the values .
    if(utilizationDynamic == true)
      utilization = new UtilizationModelDynamic(config.getInt("cloudSimulator." + simulationName + ".cloudlet.utilizationPercent"))
    else
      utilization = new UtilizationModelFull
    val Cloudlet_Length = config.getInt("cloudSimulator." + simulationName + ".cloudlet.cloudletLength")
    val Cloudlet_PES = config.getInt("cloudSimulator." + simulationName + ".cloudlet.Pes")
    val list = (1 to hostCloudlets).toList.map(hostCloudlet =>
      new CloudletSimple(hostCloudlet, Cloudlet_Length, Cloudlet_PES)
        .setFileSize(config.getInt("cloudSimulator." + simulationName + ".cloudlet.fileSize"))
        .setOutputSize(config.getInt("cloudSimulator." + simulationName + ".cloudlet.outputSize"))
        .setUtilizationModel(utilization)
    )
    return list
  }

  /**
   * Calculate the total cost of the broker execution
   *
   * @param broker Broker whose cost we want to calculate
   */
  def calculateCost(broker: DatacenterBroker): Double = {

    val cloudletList = broker.getCloudletFinishedList.asInstanceOf[java.util.List[Cloudlet]].asScala
    logger.info("Calculating Cost")
    var totalCost: Double = 0
    cloudletList.map { (cloudlet: Cloudlet) =>
      totalCost += cloudlet.getTotalCost
    }
    logger.info("Total cost fo running all the task: " + totalCost)
    totalCost
  }

  /**
   * Convert the scala list to java list
   */

  def toJavaList[T](list: List[T]): util.List[T] = {
    val arrayList = new util.ArrayList[T]
    list.map(arrayList.add(_))
    arrayList
  }

  /**
   * Convert the array to list in scala
   */

  implicit def arrayToList[A](a: Array[A]): List[A] = a.toList


}