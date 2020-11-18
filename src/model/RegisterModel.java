package model;

public class RegisterModel extends Model {
	private String name;

	public RegisterModel() { }
	
	public RegisterModel(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}

