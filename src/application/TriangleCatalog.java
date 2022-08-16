package application;

import java.util.ArrayList;


/**
 * Object class containing a list of Triangle objects and specialized 
 * methods to validate, manage, and perform operations on that list.
 */
public class TriangleCatalog {
	private ArrayList<Triangle> triangleList;
	
	/**
	 * TriangleCatalog constructor. makes the list of triangle objects reference
	 * a newly created instance of a Triangle ArrayList.
	 */
	TriangleCatalog(){
		triangleList = new ArrayList<Triangle>();
	}
	
	/**
	 * Returns the triangle object in the triangleList directly preceding the
	 * one passed in as a parameter. If the current triangle is the first object in
	 * the list, null will be returned.
	 * @param currentTriangle - the triangle to get the previous in the list of.
	 * @return the triangle object before the currentTriangle in the list if available, otherwise null
	 */
	Triangle getPreviousTriangle(Triangle currentTriangle){
		//checking if the specified triangle is not the first object in the list
		if (triangleList.indexOf(currentTriangle) > 0) {
			return triangleList.get(triangleList.indexOf(currentTriangle)-1);
		}
		return null;
	}
	
	/**
	 * Returns the triangle object in the triangleList directly succeeding the
	 * one passed in as a parameter. If the current triangle is the last object in
	 * the list, null will be returned.
	 * @param currentTriangle - the triangle to get the next in the list of.
	 * @return the triangle object after the currentTriangle in the list if available, otherwise null
	 */
	Triangle getNextTriangle(Triangle currentTriangle){
		//checking if the specified triangle is not the last object in the list
		if(triangleList.indexOf(currentTriangle) < triangleList.size()-1)
			return triangleList.get(triangleList.indexOf(currentTriangle)+1);
		return null;
	}
	
	/**
	 * Adds the specified triangle object to the end of the list.
	 * @param triangleToAdd - triangle object to add to the list
	 */
	public void addTriangle(Triangle triangleToAdd){
		triangleList.add(triangleToAdd);
	}
	
	/**
	 * Removes the specified triangle object from the list if the list contains the triangle.
	 * @param triangleToRemove - triangle object to remove from the list
	 */
	public void removeTriangle(Triangle triangleToRemove) {
		//checking if the list contains the specified triangle
		if(triangleList.contains(triangleToRemove)) {
			triangleList.remove(triangleToRemove);
		}
	}
	
	/**
	 * Returns a reference to the triangle at the specified index in the list. If the
	 * specified index is outside of the list, the first object in the list will be returned.
	 * @param index - index in the list to retrieve a triangle object from
	 * @return triangle object at the specified index of the list if available, otherwise 
	 * the first triangle object in the list
	 */
	public Triangle getTriangle(int index) {
		//checking if the index is within the list
		if(index < triangleList.size()) {
			return triangleList.get(index);
		}
		return triangleList.get(0);
	}
	
	/**
	 * Returns the index of the specified triangle object within the list. If the list does not
	 * contain the specified triangle, an index of 0 will be returned.
	 * @param triangle - triangle object to get the index of within the list
	 * @return index of the specified triangle object in the list if available, otherwise 0
	 */
	public int getIndexInList(Triangle triangle) {
		//checking if the list contains the specified triangle
		if (triangleList.contains(triangle)) {
			//counting up until the object at the index matches the specified triangle, then returns that index.
			for(int i = 0; i < triangleList.size(); i++) {
				if(triangleList.get(i) == triangle) return i;
			}
		}
		return 0;
	}
	
	/**
	 * Returns the size of the list.
	 * @return size of the triangleList within the TriangleCatalog object
	 */
	public int getListSize(){
		return triangleList.size();
	}
}
