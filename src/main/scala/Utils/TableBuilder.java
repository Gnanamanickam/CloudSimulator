package Utils;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.core.Identifiable;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;
import org.cloudsimplus.builders.tables.Table;
import org.cloudsimplus.builders.tables.TableColumn;

import java.util.List;

// Copied the file from CloudletsTableBuilder and modified to suit the simulation requirement .
public class TableBuilder extends CloudletsTableBuilder {

    private static final String TIME_FORMAT = "%.0f";
    private static final String SECONDS = "Seconds";
    private static final String CPU_CORES = "CPU cores";
    private static final String CURRENCY_FORMAT = "%.2f";

    public TableBuilder(List<? extends Cloudlet> list) {
        super(list);
    }

    /**
     * Instantiates a builder to print the list of Cloudlets using the
     * given {@link Table}.
     *
     * @param list the list of Cloudlets to print
     * @param table the {@link Table} used to build the table with the Cloudlets data
     */

    // Overriding the method available in CloudletsTableBuilder to include the cost in the simulation table
    @Override
    protected void createTableColumns() {
        final String ID = "ID";
        addColumnDataFunction(getTable().addColumn("Cloudlet", ID), Identifiable::getId);
        addColumnDataFunction(getTable().addColumn("Status "), task -> task.getStatus().name());
        addColumnDataFunction(getTable().addColumn("DC", ID), task -> task.getVm().getHost().getDatacenter().getId());
        addColumnDataFunction(getTable().addColumn("Host", ID), task -> task.getVm().getHost().getId());
        addColumnDataFunction(getTable().addColumn("Host PEs ", CPU_CORES), task -> task.getVm().getHost().getWorkingPesNumber());
        addColumnDataFunction(getTable().addColumn("VM", ID), task -> task.getVm().getId());
        addColumnDataFunction(getTable().addColumn("VM PEs   ", CPU_CORES), task -> task.getVm().getNumberOfPes());
        addColumnDataFunction(getTable().addColumn("CloudletLen", "MI"), Cloudlet::getLength);
        addColumnDataFunction(getTable().addColumn("CloudletPEs", CPU_CORES), Cloudlet::getNumberOfPes);
        TableColumn col = getTable().addColumn("StartTime", SECONDS).setFormat(TIME_FORMAT);
        addColumnDataFunction(col, Cloudlet::getExecStartTime);
        col = getTable().addColumn("FinishTime", SECONDS).setFormat(TIME_FORMAT);
        addColumnDataFunction(col, task -> roundTime(task,task.getFinishTime()));
        col = getTable().addColumn("ExecTime", SECONDS).setFormat(TIME_FORMAT);
        addColumnDataFunction(col, task -> roundTime(task,task.getActualCpuTime()));
        col = getTable().addColumn("RAM Utilized", "%").setFormat(CURRENCY_FORMAT);
        addColumnDataFunction(col, task -> task.getUtilizationOfRam());
        col = getTable().addColumn("Cost Per Sec", "$").setFormat(CURRENCY_FORMAT);
        addColumnDataFunction(col, task -> task.getCostPerSec());
        col = getTable().addColumn("Cost Per Bandwidth", "$").setFormat(CURRENCY_FORMAT);
        addColumnDataFunction(col, task -> task.getCostPerBw());
        col = getTable().addColumn("Accumulated Bandwidth Cost", "$").setFormat(CURRENCY_FORMAT);
        addColumnDataFunction(col, task -> task.getAccumulatedBwCost());
        col = getTable().addColumn("Total Cost", "$").setFormat(CURRENCY_FORMAT);
        addColumnDataFunction(col, task -> task.getTotalCost());
    }

    /**
     * Rounds a given time so that decimal places are ignored.
     * Sometimes a Cloudlet start at time 0.1 and finish at time 10.1.
     * Previously, in such a situation, the finish time was rounded to 11 (Math.ceil),
     * giving the wrong idea that the Cloudlet took 11 seconds to finish.
     * This method makes some little adjustments to avoid such a precision issue.
     *
     * @param cloudlet the Cloudlet being printed
     * @param time the time to round
     * @return
     */


    private double roundTime(final Cloudlet cloudlet, final double time) {

        if(time - cloudlet.getExecStartTime() < 1){
            return time;
        }
        final double startFraction = cloudlet.getExecStartTime() - (int) cloudlet.getExecStartTime();
        return Math.round(time - startFraction);
    }
}

