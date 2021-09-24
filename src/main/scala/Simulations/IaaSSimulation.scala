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
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared
import org.cloudbus.cloudsim.vms.Vm
import org.cloudsimplus.builders.tables.CloudletsTableBuilder

import scala.collection.JavaConverters.*

/**
This class executes IaaS simulation using CloudSim framework
The user has control over the following things with regards to IaaS :

VM and its allocation policy used by the datacenter for VM allocation
Cloudlets and its utilization model to be used by cloudlet .
 **/

class IaaSSimulation

  object IaaSSimulation:
    val config = ObtainConfigReference("cloudSimulator") match {
      case Some(value) => value
      case None => throw new RuntimeException("Cannot obtain a reference to the config data.")
    }

    val logger = CreateLogger(classOf[IaaSSimulation])

    def Start() =
      logger.info("Inside IaaSSimulation class")
      val simulation = new CloudSim()
      logger.info("Allocation First Fit Policy to the datacenter")
      val vmPolicy = new VmAllocationPolicyFirstFit()
      logger.info("Using Time shared scheduler")
      val cloudletScheduler = new CloudletSchedulerTimeShared()
      val datacenter: Datacenter = CloudSimUtils.createDatacenter("IaaSSimulation", simulation, false, vmPolicy)
      val broker: DatacenterBroker = new DatacenterBrokerSimple(simulation)
      val vmList: List[Vm] = CloudSimUtils.createVirtualMachine("IaaSSimulation", cloudletScheduler)
      val cloudletList: List[Cloudlet] = CloudSimUtils.createCloudlets("IaaSSimulation", false)
      broker.submitVmList(vmList.asJava)
      broker.submitCloudletList(cloudletList.asJava)
      Thread.sleep(1000)
      logger.info("Starting the IaaSSimulation")
      simulation.start()
      val finishedCloudlets: List[Cloudlet] = broker.getCloudletFinishedList().asScala.toList
      new CloudletsTableBuilder(finishedCloudlets.asJava).build()

