package rakaneth.wolfsden.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.badlogic.ashley.core.Component;

public class Identity implements Component
{
  public String name;
  public String desc;
  public String id;

  public Identity(String id)
  {
    this.id = id;
    this.name = "No Name";
    this.desc = "No Description";
  }

  public Identity(String name, String id)
  {
    this.name = name;
    this.id = id;
    this.desc = "No description";
  }

  public Identity(String name, String id, String desc)
  {
    this.name = name;
    this.id = id;
    this.desc = desc;
  }
}
