package application;

/**
 * Object class containing a simple x and y variable representing
 * the x and y coordinate of a point on a 2d plane.
 */
public class Point {
	private double x, y;
	
	/**
	 * Point constructor. Sets the x and y value of the point.
	 * @param setX - x coordinate value to set to the point
	 * @param setY - y coordinate value to set to the point
	 */
	Point(double setX, double setY){
		x = setX;
		y = setY;
	}
	
	/**
	 * @return x - coordinate value of Point object
	 */
	public double getX() {
		return x;
	}

	/**
	 * @param x - value of the x coordinate to set to the Point Object
	 */
	public void setX(double x) {
		this.x = x;
	}

	/**
	 * @return y - coordinate value of Point object
	 */
	public double getY() {
		return y;
	}

	/**
	 * @param y - value of the y coordinate to set to the Point Object
	 */
	public void setY(double y) {
		this.y = y;
	}
	
}
