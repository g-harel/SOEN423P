package Interface.Corba;


/**
* Interface/Corba/Project.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from CORBA.idl
* Wednesday, November 21, 2018 10:47:27 o'clock PM EST
*/

public final class Project implements org.omg.CORBA.portable.IDLEntity
{
  public String projectId = null;
  public String clientName = null;
  public String projectName = null;

  public Project ()
  {
  } // ctor

  public Project (String _projectId, String _clientName, String _projectName)
  {
    projectId = _projectId;
    clientName = _clientName;
    projectName = _projectName;
  } // ctor

} // class Project
