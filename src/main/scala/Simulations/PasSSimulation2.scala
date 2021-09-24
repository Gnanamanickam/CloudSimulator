package Simulations

package Simulations

import Utils.{CloudSimUtils, CreateLogger, ObtainConfigReference}
import org.cloudbus.cloudsim.allocationpolicies.{VmAllocationPolicyBestFit, VmAllocationPolicyFirstFit}
import org.cloudbus.cloudsim.brokers.{DatacenterBroker, DatacenterBrokerSimple}
import org.cloudbus.cloudsim.cloudlets.Cloudlet
import org.cloudbus.cloudsim.core.CloudSim
import org.cloudbus.cloudsim.datacenters.Datacenter
import org.cloudbus.cloudsim.schedulers.cloudlet.{CloudletSchedulerSpaceShared, CloudletSchedulerTimeShared}
import org.cloudbus.cloudsim.vms.Vm
import org.cloudsimplus.builders.tables.CloudletsTableBuilder

import scala.collection.JavaConverters.*

/**
This class executes PaaS simulation using CloudSim framework
The cloudlets which acts as the service which the user wants to run on the VM
are assigned to the Datacenters based on user requirement.


 **/

class PaaSSimulation2

object PaaSSimulation2:
  val config = ObtainConfigReference("cloudSimulator") match {
    case Some(value) => value
    case None => throw new RuntimeException("Cannot obtain a reference to the config data.")
  }

  val logger = CreateLogger(classOf[PaaSSimulation2])

  def Start() =
    logger.info("Inside PaaSSimulation class")
    val simulation = new CloudSim()
    logger.info("Allocation Policy to the datacenter")
    val vmPolicyFirstFit = new VmAllocationPolicyFirstFit()
    val vmPolicyBestFit = new VmAllocationPolicyBestFit()
    logger.info("Assigning scheduler")
    val cloudletTimeScheduler = new CloudletSchedulerTimeShared()
    val cloudletSpaceScheduler = new CloudletSchedulerSpaceShared()
    logger.info("Creating Datacenter2")
    val datacenter: Datacenter = CloudSimUtils.createDatacenter("PaaSSimulation2", simulation, true, vmPolicyBestFit)
    val broker: DatacenterBroker = new DatacenterBrokerSimple(simulation)
    val vmList: List[Vm] = CloudSimUtils.createVirtualMachine("PaaSSimulation2", cloudletSpaceScheduler)
    val cloudletList: List[Cloudlet] = CloudSimUtils.createCloudlets("PaaSSimulation2", false)
    broker.submitVmList(vmList.asJava)
    broker.submitCloudletList(cloudletList.asJava)
    Thread.sleep(1000)
    logger.info("Starting the PaaSSimulation1")
    simulation.start()
    val finishedCloudlets: List[Cloudlet] = broker.getCloudletFinishedList().asScala.toList
    new CloudletsTableBuilder(finishedCloudlets.asJava).build()
