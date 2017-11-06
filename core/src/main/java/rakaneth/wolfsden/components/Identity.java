package rakaneth.wolfsden.components;

import com.badlogic.ashley.core.Component;

public class Identity implements Component {
	public String name;
	public String desc;
	public String id;
	
	public Identity(String name, String id)
	{
		this.name = name;
		this.id = id;
		this.desc = "No description";
	}
}
