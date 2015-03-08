package edu.cpp.cs585.shared;

public class TestDiff implements Comparable<TestDiff>{

	private String filename;
	private String email;
	private int asserts;
	private String linesAdded;
	private String linesDeleted;

	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public int getAsserts() {
		return asserts;
	}
	public void setAsserts(int asserts) {
		this.asserts = asserts;
	}
	public String getLinesAdded() {
		return linesAdded;
	}
	public void setLinesAdded(String linesAdded) {
		this.linesAdded = linesAdded;
	}
	public String getLinesDeleted() {
		return linesDeleted;
	}
	public void setLinesDeleted(String linesDeleted) {
		this.linesDeleted = linesDeleted;
	}
	
	public boolean equals(Object o){
		boolean isequal = true;
		if(o != null && o instanceof TestDiff && ((TestDiff)o).getEmail().contentEquals(this.email)){
			isequal = true;
		}else{
			isequal = false;
		}
		return isequal;
	}
	
	public int compareTo(TestDiff test){
		return this.asserts-test.asserts;
		
	}
}
