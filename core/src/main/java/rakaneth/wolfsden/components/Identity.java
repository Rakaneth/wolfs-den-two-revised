package rakaneth.wolfsden.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.badlogic.ashley.core.Component;

public class Identity implements Component
{
  public String       name;
  public String       desc;
  public String       id;
  public List<String> factions;

  public Identity(String id)
  {
    this.id = id;
    this.name = "No Name";
    this.desc = "No Description";
    this.factions = new ArrayList<String>();
  }

  public Identity(String name, String id)
  {
    this.name = name;
    this.id = id;
    this.desc = "No description";
    this.factions = new ArrayList<String>();
  }

  public Identity(String name, String id, String desc, String... factions)
  {
    this.name = name;
    this.id = id;
    this.desc = desc;
    this.factions = new ArrayList<String>(Arrays.asList(factions));
  }
}
