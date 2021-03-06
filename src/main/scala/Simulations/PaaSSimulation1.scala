package Simulations

import Utils.{CloudSimUtils, CreateLogger, ObtainConfigReference, TableBuilder}
import org.cloudbus.cloudsim.allocationpolicies.{VmAllocationPolicyBestFit, VmAllocationPolicyFirstFit}
import org.cloudbus.cloudsim.brokers.{DatacenterBroker, DatacenterBrokerSimple}
import org.cloudbus.cloudsim.cloudlets.Cloudlet
import org.cloudbus.cloudsim.core.CloudSim
import org.cloudbus.cloudsim.datacenters.Datacenter
import org.cloudbus.cloudsim.schedulers.cloudlet.{CloudletSchedulerSpaceShared, CloudletSchedulerTimeShared}
import org.cloudbus.cloudsim.vms.Vm
//import org.cloudsimplus.builders.tables.CloudletsTableBuilder

import scala.collection.JavaConverters.*

/**
This class executes PaaS simulation using CloudSim framework
The cloudlets which acts as the service which the user wants to run on the VM
are assigned to the Datacenters based on user requirement.


 **/

class PaaSSimulation1

object PaaSSimulation1:
  val config = ObtainConfigReference("cloudSimulator") match {
    case Some(value) => value
    case None => throw new RuntimeException("Cannot obtain a reference to the config data.")
  }

  val logger = CreateLogger(classOf[PaaSSimulation1])

  def Start() =
    logger.info("Inside PaaSSimulation class")
    val simulation = new CloudSim()
    logger.info("Allocation Policy to the datacenter")
    val vmPolicyFirstFit = new VmAllocationPolicyFirstFit()
    val vmPolicyBestFit = new VmAllocationPolicyBestFit()
    logger.info("Assigning scheduler")
    val cloudletTimeScheduler = new CloudletSchedulerTimeShared()
    val cloudletSpaceScheduler = new CloudletSchedulerSpaceShared()
    logger.info("Creating Datacenter1")
    val datacenter: Datacenter = CloudSimUtils.createDatacenter("PaaSSimulation1", simulation, true, vmPolicyFirstFit)
    val broker: DatacenterBroker = new DatacenterBrokerSimple(simulation)
    val vmList: List[Vm] = CloudSimUtils.createVirtualMachine("PaaSSimulation1", cloudletTimeScheduler)
    val cloudletList: List[Cloudlet] = CloudSimUtils.createCloudlets("PaaSSimulation1", true)
    broker.submitVmList(vmList.asJava)
    broker.submitCloudletList(cloudletList.asJava)
    Thread.sleep(1000)
    // Netwotk Topology implementation
    val topology = "topology.brite"
    CloudSimUtils.networkConfiguration(topology, simulation, datacenter, broker)
    logger.info("Starting the PaaSSimulation1")
    simulation.start()
//    val finishedCloudlets: List[Cloudlet] = broker.getCloudletFinishedList().asScala.toList
    System.out.println(CloudSimUtils.calculateCost(broker))
    val finishedCloudlets = broker.getCloudletFinishedList()
    new TableBuilder(finishedCloudlets).build()
