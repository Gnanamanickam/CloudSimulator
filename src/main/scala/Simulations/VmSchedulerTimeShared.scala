package Simulations

import Simulations.VmSchedulerTimeShared.{config, logger}
import Utils.{CloudSimUtils, CreateLogger, ObtainConfigReference}
import org.cloudbus.cloudsim.allocationpolicies.{VmAllocationPolicyFirstFit, VmAllocationPolicySimple}
import org.cloudbus.cloudsim.brokers.DatacenterBroker
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple
import org.cloudbus.cloudsim.cloudlets.Cloudlet
import org.cloudbus.cloudsim.cloudlets.CloudletSimple
import org.cloudbus.cloudsim.core.CloudSim
import org.cloudbus.cloudsim.datacenters.Datacenter
import org.cloudbus.cloudsim.datacenters.DatacenterSimple
import org.cloudbus.cloudsim.hosts.Host
import org.cloudbus.cloudsim.hosts.HostSimple
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple
import org.cloudbus.cloudsim.resources.Pe
import org.cloudbus.cloudsim.resources.PeSimple
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerSpaceShared
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerSpaceShared
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeShared
import org.cloudbus.cloudsim.vms.Vm
import org.cloudbus.cloudsim.vms.VmSimple
import org.cloudsimplus.builders.tables.CloudletsTableBuilder
import org.cloudsimplus.builders.tables.TextTableColumn

import java.util
import scala.collection.JavaConverters.*

class VmSchedulerTimeShare

object VmSchedulerTimeShared:
  val config = ObtainConfigReference("cloudSimulator") match {
    case Some(value) => value
    case None => throw new RuntimeException("Cannot obtain a reference to the config data.")
  }
  val logger = CreateLogger(classOf[VmSchedulerTimeShared])

  def Start() =

    val simulation = new CloudSim()
    val cloudletScheduler = new CloudletSchedulerSpaceShared()
    val datacenter0: Datacenter = CloudSimUtils.createDatacenter("simulation7", simulation, false, new VmAllocationPolicySimple)
    datacenter0.setSchedulingInterval(config.getLong("cloudSimulator.simulation7.scheduleInterval"))
    val hostList = datacenter0.getHostList
    val broker0: DatacenterBroker = new DatacenterBrokerSimple(simulation)
    val vmList: List[Vm] = CloudSimUtils.createVirtualMachine("simulation7", cloudletScheduler)
    val cloudletList: List[Cloudlet] = CloudSimUtils.createCloudlets("simulation7", false)
    broker0.submitVmList(vmList.asJava)
    broker0.submitCloudletList(cloudletList.asJava)
    simulation.start()
    new CloudletsTableBuilder(broker0.getCloudletFinishedList).addColumn(5, new TextTableColumn("Host MIPS", "total"), (c: Cloudlet) => c.getVm.getHost.getTotalMipsCapacity).addColumn(8, new TextTableColumn("VM MIPS", "total"), (c: Cloudlet) => c.getVm.getTotalMipsCapacity).addColumn(9, new TextTableColumn("  VM MIPS", "requested"), this.getVmRequestedMips).addColumn(10, new TextTableColumn("  VM MIPS", "allocated"), this.getVmAllocatedMips).build()
    logger.info("Hosts CPU usage History")
    hostList.toArray().map(host => printHostStateHistory )
    vmList.map(vm => logger.info("%s: Requested MIPS: %.0f Allocated MIPS: %.0f%n", vm, vm.getCurrentRequestedMips, vm.getMips))


  private def printHostStateHistory(host: Host): Unit = {
    System.out.printf("Host: %d%n", host.getId)
    System.out.println("-----------------------------------------------------------------------------------------------------")
    host.getStateHistory.stream.forEach(System.out.print)
    System.out.println()
  }

  private def getVmRequestedMips(c: Cloudlet): Double = {
    if (c.getVm.getStateHistory.isEmpty) return 0
    c.getVm.getStateHistory.get(c.getVm.getStateHistory.size - 1).getRequestedMips
  }

  private def getVmAllocatedMips(c: Cloudlet): Double = {
    if (c.getVm.getStateHistory.isEmpty) return 0
    c.getVm.getStateHistory.get(c.getVm.getStateHistory.size - 1).getAllocatedMips
  }

