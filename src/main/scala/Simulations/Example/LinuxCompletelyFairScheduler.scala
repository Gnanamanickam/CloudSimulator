package Simulations.Example

import Simulations.Example.LinuxCompletelyFairScheduler
import Utils.{CloudSimUtils, CreateLogger, ObtainConfigReference}
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicySimple
import org.cloudbus.cloudsim.brokers.{DatacenterBroker, DatacenterBrokerSimple}
import org.cloudbus.cloudsim.cloudlets.{Cloudlet, CloudletSimple}
import org.cloudbus.cloudsim.core.CloudSim
import org.cloudbus.cloudsim.datacenters.{Datacenter, DatacenterSimple}
import org.cloudbus.cloudsim.hosts.{Host, HostSimple}
import org.cloudbus.cloudsim.provisioners.{PeProvisionerSimple, ResourceProvisionerSimple}
import org.cloudbus.cloudsim.resources.{Pe, PeSimple}
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerSpaceShared
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeShared
import org.cloudbus.cloudsim.utilizationmodels.{UtilizationModel, UtilizationModelFull}
import org.cloudbus.cloudsim.vms.{Vm, VmSimple}
import org.cloudsimplus.builders.tables.{CloudletsTableBuilder, TextTableColumn}

import java.util
import scala.::
import scala.collection.JavaConverters.*

class LinuxCompletelyFairScheduler

object LinuxCompletelyFairScheduler:
  val config = ObtainConfigReference("cloudSimulator") match {
    case Some(value) => value
    case None => throw new RuntimeException("Cannot obtain a reference to the config data.")
  }
  val logger = CreateLogger(classOf[LinuxCompletelyFairScheduler])

  def Start() =
    val simulation = new CloudSim()
    val cloudletScheduler = new CloudletSchedulerSpaceShared()
    val datacenter0: Datacenter = CloudSimUtils.createDatacenter("simulation8", simulation, false, new VmAllocationPolicySimple)
    val broker0: DatacenterBroker = new DatacenterBrokerSimple(simulation)
    val vmList: List[Vm] = CloudSimUtils.createVirtualMachine("simulation8", cloudletScheduler)
    val cloudletList: List[Cloudlet] = CloudSimUtils.createCloudlets("simulation8", false)
    broker0.submitVmList(vmList.asJava)
    broker0.submitCloudletList(cloudletList.asJava)
    val cloudlet_number = config.getInt("cloudSimulator.simulation8.cloudlet.HostCloudlets");
    (0 to cloudlet_number/2).map(cn => cloudletList.asJava.get(cn).setPriority(4))
    simulation.start()
    val finishedCloudlets: List[Cloudlet] = broker0.getCloudletFinishedList().asScala.toList
    new CloudletsTableBuilder(finishedCloudlets.asJava).addColumn(2, new TextTableColumn("Priority"), null).build()


