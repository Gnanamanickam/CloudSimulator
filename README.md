# Gnanamanickam Arumugaperumal

# Overview

This project consists of different Simulation of cloud environments using CloudSim Framework along with a set of libraries in Scala language . Multiple simulations has been carried out with different configurations and policies and the result has been analyzed later . The project can be compiled using the SBT .

The datacenters are created using CloudSim and then the jobs are executed on them. We define the number of brokers in a Virtual Machines, Cloudlets for each simulation. Datacenter Broker policy is defined using the out-of-the-box methods in Cloudsim.

#### Prequesite

 * SBT should be installed in your device .
 * Java should be installed and the minimum version of 8 .
 * If IDE, Intellij should be installed in the system for convinience .

## Installation
    
* Clone the GIT repository by using git clone https://github.com/Gnanamanickam/CloudSimulator.git
* Run the following commands in the console

```
sbt clean compile test
```
```
sbt clean compile run
```
* It builds and compiles the project and runs the simulation defined .


* If you are using IntellIj clone the repository by using "Check out from Version Control and then Git."

* The scala version should be set in the Global Libraries under Project Structure in Files .
* The SBT configuration should be added by using Edit Configurations and then simulations can be ran in the IDE .
* Now Build the project and then run the simulation to get the generated output. 


## Simulation Models

#### IaaS

The IaaS stands for Infrastructure as a Service in which the service providers offer computing resources to the end user with networking and storage capabilities .

Thus in simulation it is modeled such that the end user has control over the VMs and Cloudlets while the datacenter and host wont be in user control (Service Providers usually have the control).


#### PaaS

PaaS which stands for Platform as a Service in which the provider offers the complete cloud platform to the end user .

The customer can choose the number of VMs and cloudlet characteristics while the provider has control over VMs and Datacenter.


#### SaaS

SaaS which stands for Software as a Service in which the cloud provider hosts the application in their cloud environment and makes it available to the end user .

The cloud provider has total control over all the cloud environment here  . The end user can only specify the number of cloudlets required .

#### Datacenter

The Datacenter simulation is carried on with different datacenter configuration and characteristics such as OS, VM, Cost etc and which datacenter Broker to use .
The capacity and technical capabilities of the datacenter has been changed with different cost specifications and VM policies and the difference can be found out .

## Implementation

* All the common methods required for the simulation such as createDatacenter, createVM, createHost etc has been given in the cloudUtil file and can be reused whenever required for different simulations.
* Logger class has been used to log the details of the execution in the console .
* Configuration file has been used to pass all the parameters required to execute a simulation and no hardcoded values have been used in the code .

#### Test

* Three test clases of SimulationTest , ConfigTest and UtilTest has been created
* The SimulationTest executes a simulation and checks whether the finishedcloudlets has a result in it .
* The configTest asserts whether the config file sends the expected value 
* The UnitTest checks whether the creation of datacenter, VM etc is happening successfully.

## Policies Available

#### VM Allocation Policies

* ***VmAllocationPolicyBestFit*** : It chooses the host which has most number of PEs in use for the VM . 
* ***VmAllocationPolicyFirstFit*** : It chooses the first host it finds with suitable resources to hold a given VM .
* ***VmAllocationPolicyRoundRobin*** : It chooses the host with suitable resources to hold a given VM in a circular way. That means when it selects a suitable Host to place a VM, it moves to the next suitable Host when a new VM has to be placed.Its the most time efficient one.
* ***VmAllocationPolicySimple*** : Its the opposite of BestFit policy . It chooses the host which has fewer PEs in use .

#### VM Scheduler Policites

* ***VmSchedulerTimeShared*** : It allocates one or more Pe to a VM, and allows sharing of PEs by multiple VMs.
* ***VmSchedulerSpaceShared*** : It allocates one or more Pe to a VM, and does not allows sharing of PEs by VMs.

#### Cloudlet Utilization Policies

* ***UtilizationModelDynamic*** : A Cloudlet UtilizationModel that allows to increase the utilization of the related resource along the simulation time.
* ***UtilizationModelFull*** : The UtilizationModelFull class is a simple model, according to which a Cloudlet always utilize all the available CPU capacity.
* ***UtilizationModelStochastic*** : The UtilizationModelStochastic class implements a model, according to which a Cloudlet generates random CPU utilization every time frame.


## Observations

#### IaaS Simulation


                                         SIMULATION RESULTS

|Cloudlet|Status |DC|Host|Host PEs |VM|VM PEs   |CloudletLen|CloudletPEs|StartTime|FinishTime|ExecTime
|--------|-------|--|----|---------|--|---------|-----------|-----------|---------|----------|--------
|      ID|       |ID|  ID|CPU cores|ID|CPU cores|         MI|  CPU cores|  Seconds|   Seconds| Seconds
|       1|SUCCESS| 1|   0|        4| 0|        2|        500|          1|        0|         1|       1
|       2|SUCCESS| 1|   1|        4| 1|        2|        500|          1|        0|         1|       1
|       3|SUCCESS| 1|   2|        4| 2|        2|        500|          1|        0|         1|       1
|       4|SUCCESS| 1|   3|        4| 3|        2|        500|          1|        0|       156|     155
|       5|SUCCESS| 1|   4|        4| 4|        2|        500|          1|        0|       156|     155
|       6|SUCCESS| 1|   0|        4| 0|        2|        500|          1|        0|       156|     155
|       7|SUCCESS| 1|   1|        4| 1|        2|        500|          1|        0|       156|     155


#### PaaS Simulation
#### SaaS Simulation
#### Round Robin with Space Shared Simulation
#### Best Fit Policy with Time shared Simulation
