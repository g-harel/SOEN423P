package Interface.DEMS;

/**
* DEMS/ProjectHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from Project.idl
* Wednesday, October 17, 2018 7:07:31 o'clock PM EDT
*/

public final class ProjectHolder implements org.omg.CORBA.portable.Streamable
{
  public Project value = null;

  public ProjectHolder ()
  {
  }

  public ProjectHolder (Project initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = ProjectHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    ProjectHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return ProjectHelper.type ();
  }

}
