package Simulations

import Utils.CloudSimUtils.config
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

/**
 * Test cases to check whether the config values matches or not using AnyFlatSpec
 */

class ConfigTest extends AnyFlatSpec with Matchers {

  val simulationName = "simulation1"
  behavior of "Configuration Parameters Module"

  it should "check the host config" in {
    config.getLong("cloudSimulator." + simulationName + ".host.RAMInMBs") shouldBe 2048E0
  }

  it should "check the vm config" in {
    config.getLong("cloudSimulator." + simulationName + ".vm.mipsCapacity") shouldBe 800E0
  }

  it should "check the cloudlets config" in {
    config.getInt("cloudSimulator." + simulationName + ".cloudlet.HostCloudlets") shouldBe 20E0
  }

  it should "check the utilizationRatio" in {
    config.getDouble("cloudSimulator.utilizationRatio") shouldBe 0.5E0
  }
}