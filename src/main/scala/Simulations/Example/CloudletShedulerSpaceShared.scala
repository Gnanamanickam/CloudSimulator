package Simulations.Example

import Simulations.Example.CloudletShedulerSpaceShared
import Utils.{CloudSimUtils, CreateLogger, ObtainConfigReference}
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicySimple
import org.cloudbus.cloudsim.brokers.{DatacenterBroker, DatacenterBrokerSimple}
import org.cloudbus.cloudsim.cloudlets.{Cloudlet, CloudletSimple}
import org.cloudbus.cloudsim.core.CloudSim
import org.cloudbus.cloudsim.datacenters.{Datacenter, DatacenterSimple}
import org.cloudbus.cloudsim.hosts.{Host, HostSimple}
import org.cloudbus.cloudsim.provisioners.{PeProvisionerSimple, ResourceProvisioner, ResourceProvisionerSimple}
import org.cloudbus.cloudsim.resources.{Pe, PeSimple}
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerSpaceShared
import org.cloudbus.cloudsim.schedulers.vm.{VmScheduler, VmSchedulerTimeShared}
import org.cloudbus.cloudsim.utilizationmodels.{UtilizationModel, UtilizationModelFull}
import org.cloudbus.cloudsim.vms.{Vm, VmSimple}
import org.cloudsimplus.builders.tables.CloudletsTableBuilder

import java.util
import scala.collection.JavaConverters.*

class CloudletShedulerSpaceShared

object CloudletSchedulerSpaceShared:
  val config = ObtainConfigReference("cloudSimulator") match {
    case Some(value) => value
    case None => throw new RuntimeException("Cannot obtain a reference to the config data.")
  }
  val logger = CreateLogger(classOf[CloudletShedulerSpaceShared])

  def Start() =
    val simulation = new CloudSim()
    val cloudletScheduler = new CloudletSchedulerSpaceShared()
    val datacenter0: Datacenter = CloudSimUtils.createDatacenter("simulation2", simulation, false, new VmAllocationPolicySimple)
    val broker0: DatacenterBroker = new DatacenterBrokerSimple(simulation)
    val vmList: List[Vm] = CloudSimUtils.createVirtualMachine("simulation2", cloudletScheduler)
    val cloudletList: List[Cloudlet] = CloudSimUtils.createCloudlets("simulation2", false)
    broker0.submitVmList(vmList.asJava)
    broker0.submitCloudletList(cloudletList.asJava)
    simulation.start()
    val finishedCloudlets: List[Cloudlet] = broker0.getCloudletFinishedList().asScala.toList
    new CloudletsTableBuilder(finishedCloudlets.asJava).build()

