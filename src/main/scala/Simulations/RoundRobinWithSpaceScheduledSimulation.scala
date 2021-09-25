package Simulations

import Utils.{CloudSimUtils, CreateLogger, ObtainConfigReference}
import org.cloudbus.cloudsim.datacenters.network.NetworkDatacenter
import org.cloudbus.cloudsim.network.topologies.NetworkTopology
import org.cloudbus.cloudsim.network.DelayMatrix
import org.cloudbus.cloudsim.network.topologies.readers.TopologyReaderBrite
import org.cloudbus.cloudsim.allocationpolicies.{VmAllocationPolicyFirstFit, VmAllocationPolicyRoundRobin, VmAllocationPolicySimple}
import org.cloudbus.cloudsim.brokers.{DatacenterBroker, DatacenterBrokerSimple}
import org.cloudbus.cloudsim.cloudlets.Cloudlet
import org.cloudbus.cloudsim.core.CloudSim
import org.cloudbus.cloudsim.datacenters.Datacenter
import org.cloudbus.cloudsim.schedulers.cloudlet.{CloudletSchedulerSpaceShared, CloudletSchedulerTimeShared}
import org.cloudbus.cloudsim.vms.Vm
import org.cloudsimplus.builders.tables.CloudletsTableBuilder
import org.cloudsimplus.listeners.EventInfo

import scala.collection.JavaConverters.*

/**
This class executes IaaS simulation using CloudSim framework
The user has control over the following things with regards to IaaS :

VM and its allocation policy used by the datacenter for VM allocation
Cloudlets and its utilization model to be used by cloudlet .
 **/

class RoundRobinWithSpaceScheduledSimulation

object RoundRobinWithSpaceScheduledSimulation:
  val config = ObtainConfigReference("cloudSimulator") match {
    case Some(value) => value
    case None => throw new RuntimeException("Cannot obtain a reference to the config data.")
  }

  val logger = CreateLogger(classOf[RoundRobinWithSpaceScheduledSimulation])

  def Start() =
    logger.info("Inside RoundRobinWithSpaceScheduledSimulation class")
    val simulation = new CloudSim()
    logger.info("Allocation RoundRobin Policy to the datacenter")
    val vmPolicy = new VmAllocationPolicyRoundRobin()
    logger.info("Using Space shared scheduler")
    val cloudletScheduler = new CloudletSchedulerSpaceShared()
    val datacenter: Datacenter = CloudSimUtils.createDatacenter("RoundRobinSimulation", simulation, false, vmPolicy)
    datacenter.setSchedulingInterval(config.getLong("cloudSimulator.RoundRobinSimulation.scheduleInterval"))
    val broker: DatacenterBroker = new DatacenterBrokerSimple(simulation)
    val vmList: List[Vm] = CloudSimUtils.createVirtualMachine("RoundRobinSimulation", cloudletScheduler)
    val cloudletList: List[Cloudlet] = CloudSimUtils.createCloudlets("RoundRobinSimulation", false)
    def onClockTickListener(evt: EventInfo): Unit = {
      vmList.foreach((vm: Vm) => System.out.printf("\t\tTime %5.2f: Vm %d CPU Usage: %5.2f%% Cloudlet: #%d CPU : %2d   RAM usage: %.2f%% (%d MB)%n",
        evt.getTime, vm.getId, vm.getCpuPercentUtilization * 100.0, vm.getCloudletScheduler.getCloudletExecList.size, vm.getNumberOfPes,
        vm.getRam.getPercentUtilization * 100, vm.getRam.getAllocatedResource))
    }
    simulation.addOnClockTickListener(onClockTickListener)
    broker.submitVmList(vmList.asJava)
    broker.submitCloudletList(cloudletList.asJava)
    Thread.sleep(1000)
    logger.info("Starting the RoundRobinWithSpaceScheduledSimulation")
    simulation.start()
    val finishedCloudlets: List[Cloudlet] = broker.getCloudletFinishedList().asScala.toList
    new CloudletsTableBuilder(finishedCloudlets.asJava).build()





