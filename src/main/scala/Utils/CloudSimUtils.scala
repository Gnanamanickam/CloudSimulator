package Utils

import jdk.nashorn.internal.runtime.ConsString
import org.cloudbus.cloudsim.allocationpolicies.{VmAllocationPolicy, VmAllocationPolicyAbstract, VmAllocationPolicyBestFit, VmAllocationPolicyFirstFit, VmAllocationPolicyRandom, VmAllocationPolicyRoundRobin, VmAllocationPolicySimple}
import org.cloudbus.cloudsim.brokers.DatacenterBroker
import org.cloudbus.cloudsim.cloudlets.{Cloudlet, CloudletSimple}
import org.cloudbus.cloudsim.core.CloudSim
import org.cloudbus.cloudsim.datacenters.network.NetworkDatacenter
import org.cloudbus.cloudsim.datacenters.{Datacenter, DatacenterSimple}
import org.cloudbus.cloudsim.hosts.{Host, HostSimple}
import org.cloudbus.cloudsim.network.topologies.BriteNetworkTopology
import org.cloudbus.cloudsim.provisioners.{PeProvisionerSimple, ResourceProvisioner, ResourceProvisionerSimple}
import org.cloudbus.cloudsim.resources.{Pe, PeSimple}
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletScheduler
import org.cloudbus.cloudsim.schedulers.vm.{VmScheduler, VmSchedulerTimeShared}
import org.cloudbus.cloudsim.util.ResourceLoader
import org.cloudbus.cloudsim.utilizationmodels.{UtilizationModel, UtilizationModelDynamic, UtilizationModelFull}
import org.cloudbus.cloudsim.vms.{Vm, VmSimple}
import org.slf4j.LoggerFactory
import org.cloudbus.cloudsim.core.CloudSim
import org.cloudbus.cloudsim.datacenters.network.NetworkDatacenter
import org.cloudbus.cloudsim.distributions.ContinuousDistribution
import org.cloudbus.cloudsim.hosts.network.NetworkHost
import org.cloudbus.cloudsim.network.switches.{AbstractSwitch, AggregateSwitch, EdgeSwitch, RootSwitch, Switch}
import org.cloudsimplus.listeners.EventInfo

import java.io.{FileWriter, InputStreamReader, InvalidClassException}
import java.text.DecimalFormat
import scala.jdk.CollectionConverters.{BufferHasAsJava, CollectionHasAsScala}
import java.util
import java.util.Optional
import scala.beans.BeanProperty
import scala.collection.mutable.ListBuffer
import scala.math.random

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

  def createDatacenter(simulationName: String, cloudSim: CloudSim, Cost: Boolean, vmPolicy: VmAllocationPolicy): Datacenter = {
    logger.info("Creating DataCenter")
    val hostNumber = config.getInt("cloudSimulator." + simulationName + ".Hosts")
    val hostList = new util.ArrayList[Host](hostNumber)
    //    for (i <- 0 until hostNumber) {
    //      val host: Host = createHost(simulationName)
    //      hostList.add(host)
    //    }

    //    policy match {
    //      case "simple" => val vmPolicy = new VmAllocationPolicySimple()
    //      case "roundRobin" => val vmPolicy = new VmAllocationPolicyRoundRobin()
    //      case "bestFit" => val vmPolicy = new VmAllocationPolicyBestFit()
    //      case "firstFit" => val vmPolicy = new VmAllocationPolicyFirstFit()
    //    }


    (1 to hostNumber).map(hostNum => hostList.add(createHost(simulationName)))
    val dataCenter: Datacenter = new DatacenterSimple(cloudSim, hostList, vmPolicy)
    if (Cost) {
      logger.info("Set DataCenter Characteristics")
      dataCenter.getCharacteristics
        .setArchitecture(config.getString("cloudSimulator." + simulationName + ".datacenter.architecture"))
        .setOs(config.getString("cloudSimulator." + simulationName + ".datacenter.os"))
        .setVmm(config.getString("cloudSimulator." + simulationName + ".datacenter.vmm"))
        .setCostPerBw(config.getDouble("cloudSimulator." + simulationName + ".datacenter.costPerBw"))
        .setCostPerStorage(config.getDouble("cloudSimulator." + simulationName + ".datacenter.costPerStorage"))
        .setCostPerMem(config.getDouble("cloudSimulator." + simulationName + ".datacenter.costPerMem"))
        .setCostPerSecond(config.getDouble("cloudSimulator." + simulationName + ".datacenter.cost"))
    }

    return dataCenter
  }

  /**
   * Method to create Network DataCenter based on the given configuration
   *
   * @param simulationName The simulation for which VM has to be created based on the given config
   * @param cloudSim       sends and processes all discrete events during the simulation time.
   * @param Cost           Boolean value which returns true if the cost parameters are involved in creation
   * @return Returns the generated Network Datacenter
   */


  def createNetworkDatacenter(simulationName: String, cloudSim: CloudSim, Cost: Boolean, vmPolicy: VmAllocationPolicy): Datacenter = {
    logger.info("Creating Network DataCenter")
    val hostNumber = config.getInt("cloudSimulator." + simulationName + ".Hosts")
    val hostList = new util.ArrayList[Host](hostNumber)

    (1 to hostNumber).map(hostNum => hostList.add(createHost(simulationName)))
    val networkdataCenter: NetworkDatacenter = new NetworkDatacenter(cloudSim, hostList, vmPolicy)
    if (Cost) {
      logger.info("Set Network DataCenter Characteristics")
      networkdataCenter.getCharacteristics
        .setArchitecture(config.getString("cloudSimulator." + simulationName + ".datacenter.architecture"))
        .setOs(config.getString("cloudSimulator." + simulationName + ".datacenter.os"))
        .setVmm(config.getString("cloudSimulator." + simulationName + ".datacenter.vmm"))
        .setCostPerBw(config.getDouble("cloudSimulator." + simulationName + ".datacenter.costPerBw"))
        .setCostPerStorage(config.getDouble("cloudSimulator." + simulationName + ".datacenter.costPerStorage"))
        .setCostPerMem(config.getDouble("cloudSimulator." + simulationName + ".datacenter.costPerMem"))
        .setCostPerSecond(config.getDouble("cloudSimulator." + simulationName + ".datacenter.cost"))
      createNetwork(networkdataCenter, cloudSim)
    }

    return networkdataCenter
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
    val utilization: UtilizationModel  = if(utilizationDynamic == true)
      new UtilizationModelDynamic(config.getDouble("cloudSimulator." + simulationName + ".cloudlet.utilizationPercent"))
    else
      new UtilizationModelFull
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
   *    To map the CloudSim entities to BRITE entities
   *    @param topology Topology whose instance needs to be fetched from BriteNetwork
   *    @param Datacenter Datacenter to be passed inside the network topology
   */

  def networkConfiguration(topology: String, cloudSim: CloudSim, datacenter: Datacenter, datacenterBroker: DatacenterBroker): Unit ={
    val networkTopology = BriteNetworkTopology.getInstance(topology)
    cloudSim.setNetworkTopology(networkTopology)
    networkTopology.mapNode(datacenter, 0)
    logger.info(s"$datacenter mapped to 0")
    networkTopology.mapNode(datacenterBroker, 2)
    logger.info(s"$datacenter mapped to 2")
  }

  /**
   * Create a network with the required rootswitch, aggregate switch and edge switches .
   * @param datacenter : Pass the datacenter for which network has to be created
   * @param simulation : Pass the simulation
   */

  def createNetwork(datacenter: NetworkDatacenter, simulation: CloudSim): Unit ={

    val rootSwitches: RootSwitch = new RootSwitch(simulation, datacenter)

    val aggregateSwitches = (0 until config.getInt("cloudSimulator.aggregateSwitches")).map {
      _ => val aggregateSwitch = new AggregateSwitch(simulation, datacenter)
        datacenter.addSwitch(aggregateSwitch)
        aggregateSwitch.getUplinkSwitches.add(rootSwitches)
        rootSwitches.getDownlinkSwitches.add(aggregateSwitch)
        aggregateSwitch
    }

    val edgeSwitches = (0 until config.getInt("cloudSimulator.edgeSwitches")).map {

        value => val edgeSwitch = new EdgeSwitch(simulation, datacenter)
        datacenter.addSwitch(edgeSwitch)
        edgeSwitch.getUplinkSwitches.add(aggregateSwitches(value / AggregateSwitch.PORTS))
        aggregateSwitches(value / AggregateSwitch.PORTS).getDownlinkSwitches.add(edgeSwitch)
        edgeSwitch
    }

    datacenter.getHostList[NetworkHost].asScala.foreach { host =>
      val switchNum = Math.round(host.getId % Integer.MAX_VALUE / EdgeSwitch.PORTS)
      edgeSwitches(switchNum).connectHost(host)
    }
  }

  /**
   * Calculate the total cost of the broker execution
   *
   * @param broker Broker whose cost we want to calculate
   */
  def calculateCost(broker: DatacenterBroker): Double = {

    val cloudletList = broker.getCloudletFinishedList.asInstanceOf[java.util.List[Cloudlet]].asScala
    logger.info("Calculating Cost")
    val list: List[Int] = cloudletList.toList.map { (cloudlet: Cloudlet) =>
          cloudlet.getTotalCost.toInt
      //    if(cloudlet.isFinished){
      //      totalCost +=  cloudlet.getCostPerSec() * cloudlet.getActualCpuTime() * cloudlet.getCostPerBw()
      //    }
    }

    val totalCost = sum(list)

    logger.info("Total cost fo running all the task: " + totalCost)
    totalCost
  }

  /**
   * Recursive function to calculate sum
   */

  def sum(list: List[Int]): Int = list match {
    case Nil => 0
    case x :: xs => x + sum(xs)
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
