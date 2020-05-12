package edu.kis.powp.jobs2d.command;

import edu.kis.powp.jobs2d.Job2dDriver;

import java.util.Iterator;
import java.util.List;

public class DefaultCompoundCommand implements ICompoundCommand {

    private List<DriverCommand> listOfDriverCommand = null;

    public DefaultCompoundCommand(List<DriverCommand> collection) {
        this.listOfDriverCommand = collection;
    }

    @Override
    public void execute(Job2dDriver driver) {
        for (DriverCommand c : listOfDriverCommand) {
            c.execute(driver);
        }
    }

    @Override
    public Iterator<DriverCommand> iterator() {
        return listOfDriverCommand.iterator();
    }

    @Override
    public DriverCommand clone(){
        return ICompoundCommand.super.clone();
    }
}