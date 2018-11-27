package App;

import Interface.Corba.Project;
import Models.ProjectIdentifier;
import Utility.ICenterServer;
import Utility.ReplicaResponse;
import Models.Region;
import Server.RegionalServer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CenterServer implements ICenterServer, Runnable {

    RegionalServer server;
    Region location;

    public CenterServer(String code) throws Exception {
        Region location = Region.fromString(code);
        this.server = new RegionalServer(location);
    }

    @Override
    public void run() {
        server.Start();
    }

    @Override
    public ReplicaResponse createMRecord(String managerID, String firstName, String lastName, int employeeID, String mailID, Project project, String location) {
        ProjectIdentifier projId  = new ProjectIdentifier(11);

        try {
            projId.setId(project.projectId);
        } catch (Exception ex) {
            projId  = new ProjectIdentifier(10001);
        }

        Models.Project projConverted = new Models.Project(projId, project.projectName, project.clientName);

        return new ReplicaResponse(true, server.createManagerRecord(managerID, firstName, lastName, employeeID, mailID, projConverted, location));
    }

    @Override
    public ReplicaResponse createERecord(String managerID, String firstName, String lastName, int employeeID,
            String mailID, String projectID) {
        return new ReplicaResponse(true, server.createEmployeeRecord(managerID, firstName, lastName, employeeID, mailID, projectID));
    }

    @Override
    public ReplicaResponse getRecordCounts(String managerID) {
        return new ReplicaResponse(true, server.getRecordCount(managerID));
    }

    @Override
    public ReplicaResponse editRecord(String managerID, String recordID, String fieldName, String newValue) {
        return new ReplicaResponse(true, server.editRecord(managerID, recordID, fieldName, newValue));
    }

    @Override
    public ReplicaResponse transferRecord(String managerID, String recordID, String location) {
        return new ReplicaResponse(true, server.transferRecord(managerID, recordID, location));
    }

    @Override
    public void softwareFailure(String managerID) {
        return;
    }

    @Override
    public void replicaCrash(String managerID) {
        server.Stop();
    }

}
