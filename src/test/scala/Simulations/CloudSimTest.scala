package Simulations

import Utils.{CloudSimUtils, CreateLogger, ObtainConfigReference}
import org.cloudbus.cloudsim.cloudlets.Cloudlet
import org.cloudbus.cloudsim.hosts.Host
import org.cloudbus.cloudsim.vms.Vm
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.funsuite.AnyFunSuite
import org.cloudbus.cloudsim.allocationpolicies.{VmAllocationPolicy, VmAllocationPolicyRoundRobin, VmAllocationPolicySimple}
import org.cloudbus.cloudsim.brokers.{DatacenterBroker, DatacenterBrokerSimple}
import org.cloudbus.cloudsim.cloudlets.{Cloudlet, CloudletSimple}
import org.cloudbus.cloudsim.core.CloudSim
import org.cloudbus.cloudsim.datacenters.{Datacenter, DatacenterSimple}
import org.cloudbus.cloudsim.hosts.{Host, HostSimple}
import org.cloudbus.cloudsim.network.topologies.BriteNetworkTopology
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple
import org.cloudbus.cloudsim.resources.{Pe, PeSimple}
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerSpaceShared
import org.cloudbus.cloudsim.schedulers.vm.{VmSchedulerSpaceShared, VmSchedulerTimeShared}
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull
import org.cloudbus.cloudsim.vms.{Vm, VmSimple}
import org.scalatest.matchers.should.Matchers.a
import org.scalatest.matchers.should.Matchers.shouldBe

import java.util
import scala.collection.mutable.ListBuffer
import scala.collection.JavaConverters.*
import java.util.List;

/**
 * Unit test cases using FunSuite to verify the creation of dc's, host's, vm's , cloudlets successfully
 */

class CloudSimTest extends AnyFunSuite {

  val logger = CreateLogger(classOf[CloudSimTest])
  val simulation = new CloudSim()
  val simulationName = "simulation1"


  test("Check if VM is created") {
    logger.info("Test Vm creation")
    val cloudletScheduler = new CloudletSchedulerSpaceShared()
    val vmList = CloudSimUtils.createVirtualMachine(simulationName, cloudletScheduler)
    vmList.asJava shouldBe a[List[Vm]]
  }

  test("Check if Datacenter is created") {
    logger.info("Test Datacenter creation")
    val vmPolicy = new VmAllocationPolicySimple()
    val datacenter: Datacenter = CloudSimUtils.createDatacenter(simulationName, simulation, false, vmPolicy )
    datacenter shouldBe a[Datacenter]
  }

  test("Check if Host is created") {
    logger.info("Test Host creation")

    val config = ObtainConfigReference("cloudSimulator") match {
      case Some(value) => value
      case None => throw new RuntimeException("Cannot obtain a reference to the config data.")
    }

    val RAM: Long = config.getLong("cloudSimulator." + simulationName + ".host.RAMInMBs")
    val BandWidth: Long = config.getLong("cloudSimulator." + simulationName + ".host.StorageInMBs")
    val Storage: Long = config.getLong("cloudSimulator." + simulationName + ".host.BandwidthInMBps")
    val hostPes = config.getInt("cloudSimulator." + simulationName + ".HostPes")
    val peList = new util.ArrayList[Pe](hostPes)
    (1 to hostPes).map(Pes =>
      peList.add(new PeSimple(config.getLong("cloudSimulator." + simulationName + ".host.mipsCapacity"), new PeProvisionerSimple))
    )
    val host: Host = new HostSimple(RAM, BandWidth, Storage, peList)
    host shouldBe a[Host]
  }

  test("Check if Cloudlet is created") {
    logger.info("Test Cloudlet creation")
    val cloudletList = CloudSimUtils.createCloudlets(simulationName, false)
    cloudletList shouldBe a[List[Cloudlet]]
  }

  test("Check if NetworkTopology is created") {
    logger.info("Test NetworkTopology creation")
    val networkTopology = BriteNetworkTopology.getInstance("topology.brite")
    simulation.setNetworkTopology(networkTopology)
    assert(simulation.getNetworkTopology != null)
  }


}

