package entity;

import java.io.Serializable;



public class Costs implements Serializable{	
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String type;
	public String sum;
	public String date;
	public String category;
	public String description;
	
	public Costs() {
		
	}
	
	public Costs(String Type, String Sum, String Date, String Category, String Description) {
		this.type = Type;
		this.sum = Sum;
		this.date = Date;
		this.category = Category;
		this.description = Description;
	}
	
	public String getType() {
		return this.type;
	}
	
	public String getSum() {
		return this.sum;
	}
	
	public String getDate() {
		return this.date;
	}
	
	public String getCategory() {
		return this.category;
	}
	
	public String getDescription() {
		return this.description;
	}

	public void setType(String Type) {
		this.type = Type;
	}
	
	public void setSum(String Sum) {
		this.sum = Sum;
	}
	
	public void setDate(String Date) {
		this.date = Date;
	}
	
	public void setCategory(String Category) {
		this.category = Category;
	}
	
	public void setDescription(String Description) {
		this.description = Description;
	}
	
}
