package org.cloudbus.cloudsim.examples.container;

import java.util.List;
import java.util.Calendar;
import java.util.ArrayList;
import java.util.LinkedList;
import java.text.DecimalFormat;

import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.container.containerPlacementPolicies.ContainerPlacementPolicyFirstFit;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.container.containerProvisioners.ContainerBwProvisionerSimple;
import org.cloudbus.cloudsim.container.containerProvisioners.ContainerPe;
import org.cloudbus.cloudsim.container.containerProvisioners.ContainerRamProvisionerSimple;
import org.cloudbus.cloudsim.container.containerProvisioners.CotainerPeProvisionerSimple;
import org.cloudbus.cloudsim.container.containerVmProvisioners.ContainerVmBwProvisionerSimple;
import org.cloudbus.cloudsim.container.containerVmProvisioners.ContainerVmPe;
import org.cloudbus.cloudsim.container.containerVmProvisioners.ContainerVmPeProvisionerSimple;
import org.cloudbus.cloudsim.container.containerVmProvisioners.ContainerVmRamProvisionerSimple;
import org.cloudbus.cloudsim.container.core.*;
import org.cloudbus.cloudsim.container.resourceAllocators.ContainerAllocationPolicy;
import org.cloudbus.cloudsim.container.resourceAllocators.ContainerAllocationPolicyRS;
import org.cloudbus.cloudsim.container.resourceAllocators.ContainerVmAllocationPolicy;
import org.cloudbus.cloudsim.container.resourceAllocators.ContainerVmAllocationPolicySimple;
import org.cloudbus.cloudsim.container.schedulers.ContainerCloudletSchedulerDynamicWorkload;
import org.cloudbus.cloudsim.container.schedulers.ContainerCloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.container.schedulers.ContainerSchedulerTimeShared;
import org.cloudbus.cloudsim.container.schedulers.ContainerVmSchedulerTimeSharedOverSubscription;
import org.cloudbus.cloudsim.container.utils.IDs;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.cloudbus.cloudsim.UtilizationModelNull;

public class ContainerExample1 {
    /**
     * The cloudlet list.
     */
    private static List<ContainerCloudlet> cloudletList;

    /**
     * The vmlist.
     */
    private static List<ContainerVm> vmList;

    /**
     * The vmlist.
     */

    private static List<Container> containerList;

    /**
     * The hostList.
     */

    private static List<ContainerHost> hostList;

    /**
     * Creates main() to run this example.
     *
     * @param args the args
     */

    public static void main(String[] args) {
        Log.printLine("Starting ContainerExample1...");

        try {

            int num_user = 1; // number of cloud users
            Calendar calendar = Calendar.getInstance(); // Calendar whose fields have been initialized with the current
                                                        // date
                                                        // and time.
            boolean trace_flag = false;

            CloudSim.init(num_user, calendar, trace_flag);

            /**
             * Defining the container allocation Policy. This policy determines how
             * Containers are allocated to VMs in the data center.
             *
             */

            ContainerAllocationPolicy containerAllocationPolicy = new ContainerAllocationPolicyRS(new ContainerPlacementPolicyFirstFit());

            /**
             * Create the set of hosts
             */

            ArrayList<ContainerVmPe> peList = new ArrayList<ContainerVmPe>();

            peList.add(new ContainerVmPe(0, new ContainerVmPeProvisionerSimple((double) 37274)));
            //peList.add(new ContainerVmPe(1, new ContainerVmPeProvisionerSimple((double) 37274)));

            /**
             * Add new ContainerHost (ID, RamProvisioner, BwProvisioner, Storage,
             * ProcessingElement VM, Scheduler)
             */

            hostList = new ArrayList<ContainerHost>();

            hostList.add(new ContainerHostDynamicWorkload(IDs.pollId(ContainerHost.class),
                    new ContainerVmRamProvisionerSimple(65536), new ContainerVmBwProvisionerSimple(1000000L), 1000000L,
                    peList, new ContainerVmSchedulerTimeSharedOverSubscription(peList)));

            /**
             * The container allocation policy which defines the allocation of VMs to
             * containers.
             */

            ContainerVmAllocationPolicy vmAllocationPolicy = new ContainerVmAllocationPolicySimple(hostList);

            /**
             * The overbooking factor for allocating containers to VMs. This factor is used
             * by the broker for the allocation process.
             */

            int overBookingFactor = 80;
            ContainerDatacenterBroker broker = createBroker(overBookingFactor);
            int brokerId = broker.getId();

            /**
             * Creating the cloudlet, container and VM lists for submitting to the broker.
             */
            cloudletList = new ArrayList<ContainerCloudlet>();
            ContainerCloudlet cloudlet1 = new ContainerCloudlet(IDs.pollId(ContainerCloudlet.class), 35000, 1, 300L, 300L,
            new UtilizationModelFull(), new UtilizationModelFull(), new UtilizationModelFull());
            cloudlet1.setUserId(brokerId);
            ContainerCloudlet cloudlet2 = new ContainerCloudlet(IDs.pollId(ContainerCloudlet.class), 35000, 1, 300L, 300L,
            new UtilizationModelFull(), new UtilizationModelFull(), new UtilizationModelFull());
            cloudlet2.setUserId(brokerId);

            cloudletList.add(cloudlet1);
            cloudletList.add(cloudlet2);

            containerList = new ArrayList<Container>();

            Container container1 = new Container(IDs.pollId(Container.class), brokerId, 9320.0D, 1, 256, 2500, 200L, "Xen",
            new ContainerCloudletSchedulerDynamicWorkload(9320.0D, 1), 300.0D);
            Container container2 = new Container(IDs.pollId(Container.class), brokerId, 9320.0D, 1, 256, 2500, 200L, "Xen",
            new ContainerCloudletSchedulerDynamicWorkload(9320.0D, 1), 300.0D);

            containerList.add(container1);
            containerList.add(container2);

            ArrayList<ContainerPe> peContainerList = new ArrayList<ContainerPe>();

            peContainerList.add(new ContainerPe(0, new CotainerPeProvisionerSimple((double) 9400)));
            peContainerList.add(new ContainerPe(1, new CotainerPeProvisionerSimple((double) 9400)));

            vmList = new ArrayList<ContainerVm>();

            vmList.add(new ContainerVm(IDs.pollId(ContainerVm.class), brokerId, (double) 37274 / 2, (float) 2048,
                    100000, 2500, "Xen", new ContainerSchedulerTimeShared(peContainerList),
                    new ContainerRamProvisionerSimple((float) 2048), new ContainerBwProvisionerSimple(100000),
                    peContainerList));

            String logAddress = "./Results";

            @SuppressWarnings("unused")
            ContainerDatacenter e = (ContainerDatacenter) createDatacenter("datacenter", ContainerDatacenter.class,
                    hostList, vmAllocationPolicy, containerAllocationPolicy,
                    getExperimentName("ContainerExample1", String.valueOf(overBookingFactor)), 300.0D, logAddress);

            broker.submitCloudletList(cloudletList);
            broker.submitContainerList(containerList);

            broker.submitVmList(vmList);

            //Log.printConcatLine(cloudlet1.getCloudletId(),container1.getId());
            broker.bindCloudletToContainer(cloudlet1.getCloudletId(),container1.getId());
            broker.bindCloudletToContainer(cloudlet2.getCloudletId(),container2.getId());
            

            /**
             * Determining the simulation termination time according to the cloudlet's
             * workload.
             */

            CloudSim.terminateSimulation(86400.00);

            /**
             * Starting the simualtion.
             */

            CloudSim.startSimulation();

            /**
             * Stopping the simualtion.
             */

            CloudSim.stopSimulation();

            List<ContainerCloudlet> newList = broker.getCloudletReceivedList();
            printCloudletList(newList);

        } catch (Exception e) {
            e.printStackTrace();
            Log.printLine("Unwanted errors happen");
        }
    }

    /**
     * Creates the broker.
     *
     * @param overBookingFactor
     * @return the datacenter broker
     */
    private static ContainerDatacenterBroker createBroker(int overBookingFactor) {

        ContainerDatacenterBroker broker = null;

        try {
            broker = new ContainerDatacenterBroker("Broker", overBookingFactor);
        } catch (Exception var2) {
            var2.printStackTrace();
            System.exit(0);
        }

        return broker;
    }

    /**
     * Prints the Cloudlet objects.
     *
     * @param list list of Cloudlets
     */
    private static void printCloudletList(List<ContainerCloudlet> list) {
        int size = list.size();
        Cloudlet cloudlet;

        String indent = "    ";
        Log.printLine();
        Log.printLine("========== OUTPUT ==========");
        Log.printLine("Cloudlet ID" + indent + "STATUS" + indent + "Data center ID" + indent + "VM ID" + indent + "Time"
                + indent + "Start Time" + indent + "Finish Time");

        DecimalFormat dft = new DecimalFormat("###.##");
        for (int i = 0; i < size; i++) {
            
            cloudlet = list.get(i);
            Log.print(indent + cloudlet.getCloudletId() + indent + indent);
            //Log.printLine(cloudlet.getCloudletStatusString());
            if (cloudlet.getCloudletStatusString() == "Success") {
                Log.print("SUCCESS");

                Log.printLine(indent + indent + cloudlet.getResourceId() + indent + indent + indent + cloudlet.getVmId()
                        + indent + indent + dft.format(cloudlet.getActualCPUTime()) + indent + indent
                        + dft.format(cloudlet.getExecStartTime()) + indent + indent
                        + dft.format(cloudlet.getFinishTime()));
            }
        }
        Log.printLine();
    }

    private static String getExperimentName(String... args) {
        StringBuilder experimentName = new StringBuilder();

        for (int i = 0; i < args.length; ++i) {
            if (!args[i].isEmpty()) {
                if (i != 0) {
                    experimentName.append("_");
                }

                experimentName.append(args[i]);
            }
        }

        return experimentName.toString();
    }

    public static ContainerDatacenter createDatacenter(String name,
            Class<? extends ContainerDatacenter> datacenterClass, List<ContainerHost> hostList,
            ContainerVmAllocationPolicy vmAllocationPolicy, ContainerAllocationPolicy containerAllocationPolicy,
            String experimentName, double schedulingInterval, String logAddress) throws Exception {
        String arch = "x86";
        String os = "Linux";
        String vmm = "Xen";
        double time_zone = 10.0D;
        double cost = 3.0D;
        double costPerMem = 0.05D;
        double costPerStorage = 0.001D;
        double costPerBw = 0.0D;
        ContainerDatacenterCharacteristics characteristics = new ContainerDatacenterCharacteristics(arch, os, vmm,
                hostList, time_zone, cost, costPerMem, costPerStorage, costPerBw);
        ContainerDatacenter datacenter;

        datacenter = new ContainerDatacenter(name, characteristics, vmAllocationPolicy, containerAllocationPolicy,
                new LinkedList<Storage>(), schedulingInterval, experimentName, logAddress);

        return datacenter;

    }
}