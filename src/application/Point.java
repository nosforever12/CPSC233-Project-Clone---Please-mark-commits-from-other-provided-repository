package application;

public class Point {
	private double x, y;
	
	
	/**
	 * Point constructor. Sets the x and y value of the point.
	 * @param setX
	 * @param setY
	 */
	Point(double setX, double setY){
		x = setX;
		y = setY;
	}
	
	/**
	 * Uses the average x and y coordinate from p1 and p2 to create a new point.
	 * Sets coordinates to xBound/yBound if they do not fall within those values (causing them to not appear in canvas)
	 * @param p1 - first point
	 * @param p2 - second point
	 * @param xBound - maximum x value
	 * @param yBound - minimum y value
	 * @return new point object
	 */
	static Point solveMidpoint(Point p1, Point p2, int xBound, int yBound) {
		double newX = (p1.getX()+p2.getX())/2;
		double newY = (p1.getY()+p2.getY())/2;
		double x = (newX > xBound) ? xBound : newX;
		double y = (newY < yBound) ? yBound : newY;
		Point point = new Point(x,y);
		return point;
	}
	
	/**
	 * Checks for points that are too close to each other and moves them to prevent overlapping text. 
	 * @param h - hypotenuse
	 * @param o - opposite
	 * @param a - adjacent
	 * @param t - theta - angle
	 **/
	static void moveOverlappingPoints(Point h, Point o, Point a, Point t) {
		Point[] p = {h,o,a,t};
		int minY = 15;
		int minX = 50;
		for(int i = 0; i < 4; i++) {
			for(int j = 0; j < 4; j++) {
				if(j==i) continue;
				double p1X = p[i].getX();
				double p1Y = p[i].getY();
				double p2X = p[j].getX();
				double p2Y = p[j].getY();
				//move point to minX/Y relative to other point if they are too close
				if(Math.abs(p1Y - p2Y) < minY && Math.abs(p1X - p2X) < minX) {
					if(p1Y <= p2Y) p[j].setY(p2Y + minY - (p2Y-p1Y));
					else p[i].setY(p1Y + minY - (p1Y-p2Y));
				}
			}
		}
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}
	
}
