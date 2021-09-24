package Simulations

import Utils.{CloudSimUtils, CreateLogger}
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicySimple
import org.cloudbus.cloudsim.brokers.{DatacenterBroker, DatacenterBrokerSimple}
import org.cloudbus.cloudsim.cloudlets.Cloudlet
import org.cloudbus.cloudsim.core.CloudSim
import org.cloudbus.cloudsim.datacenters.Datacenter
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerSpaceShared
import org.cloudbus.cloudsim.vms.Vm
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers.a

import scala.collection.JavaConverters.*
import java.util.List

/**
 * Test class to check whether the simulation end has a result
 */

class SimulationTest extends AnyFunSuite {
  val logger = CreateLogger(classOf[SimulationTest])
  test("Check simulation") {
    logger.info("Test Simulation")
    val simulation = new CloudSim()
    val datacenter: Datacenter = CloudSimUtils.createDatacenter("simulation3", simulation, false, new VmAllocationPolicySimple)
    val broker: DatacenterBroker = new DatacenterBrokerSimple(simulation)
    val vmList = CloudSimUtils.createVirtualMachine("simulation3", new CloudletSchedulerSpaceShared())
    val cloudletList = CloudSimUtils.createCloudlets("simulation3", false)
    broker.submitVmList(vmList.asJava)
    broker.submitCloudletList(cloudletList.asJava)
    simulation.start()
    assert(broker.getCloudletFinishedList() != null)
    logger.info("Test Simulation ended")
  }

}
