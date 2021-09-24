package Simulations.Example

import Simulations.Example.CloudletShedulerTimeShared
import Utils.{CloudSimUtils, CreateLogger, ObtainConfigReference}
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicySimple
import org.cloudbus.cloudsim.brokers.{DatacenterBroker, DatacenterBrokerSimple}
import org.cloudbus.cloudsim.cloudlets.{Cloudlet, CloudletSimple}
import org.cloudbus.cloudsim.core.CloudSim
import org.cloudbus.cloudsim.datacenters.{Datacenter, DatacenterSimple}
import org.cloudbus.cloudsim.hosts.{Host, HostSimple}
import org.cloudbus.cloudsim.provisioners.{PeProvisionerSimple, ResourceProvisioner, ResourceProvisionerSimple}
import org.cloudbus.cloudsim.resources.{Pe, PeSimple}
import org.cloudbus.cloudsim.schedulers.cloudlet.{CloudletScheduler, CloudletSchedulerSpaceShared, CloudletSchedulerTimeShared}
import org.cloudbus.cloudsim.schedulers.vm.{VmScheduler, VmSchedulerTimeShared}
import org.cloudbus.cloudsim.utilizationmodels.{UtilizationModel, UtilizationModelDynamic, UtilizationModelFull}
import org.cloudbus.cloudsim.vms.{Vm, VmSimple}
import org.cloudsimplus.builders.tables.CloudletsTableBuilder

import java.util
import scala.collection.JavaConverters.*

class CloudletShedulerTimeShared

object CloudletShedulerTimeShared:
  val config = ObtainConfigReference("cloudSimulator") match {
    case Some(value) => value
    case None => throw new RuntimeException("Cannot obtain a reference to the config data.")
  }
  val logger = CreateLogger(classOf[CloudletShedulerTimeShared])

  def Start() =
    val simulation = new CloudSim()
    val cloudletScheduler = new CloudletSchedulerTimeShared()
    val datacenter0: Datacenter = CloudSimUtils.createDatacenter("simulation1", simulation, false, new VmAllocationPolicySimple)
    val broker0: DatacenterBroker = new DatacenterBrokerSimple(simulation)
    val vmList: List[Vm] = CloudSimUtils.createVirtualMachine("simulation1", cloudletScheduler)
    val cloudletList: List[Cloudlet] = CloudSimUtils.createCloudlets("simulation1", true)
    broker0.submitVmList(vmList.asJava)
    broker0.submitCloudletList(cloudletList.asJava)
    simulation.start()
    val finishedCloudlets: List[Cloudlet] = broker0.getCloudletFinishedList().asScala.toList
    new CloudletsTableBuilder(finishedCloudlets.asJava).build()



