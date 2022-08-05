package application;

import java.text.DecimalFormat;
import java.util.HashMap;

public class Triangle {
	private double h, o, a;
	private Point ho, ha, oa;
	private HashMap<String, String> info = new HashMap<String, String>();
	
	/**
	 * Triangle constructor. Sets the side length values as well as it's label information.
	 * @param setH - hypotenuse length
	 * @param setO - opposite length
	 * @param setA - adjacent length
	 * @param setInfoH - formatted hypotenuse length
	 * @param setInfoO - formatted opposite length
	 * @param setInfoA - formatted adjacent length
	 * @param setInfoT - formatted degrees/radians value
	 * @param setInfoSolveMethod - solve method and original formula
	 */
	Triangle(double setH, double setO, double setA, String setInfoH, String setInfoO, 
			String setInfoA, String setInfoT, String setInfoSolveMethod) {
		this.h = setH;
		this.o = setO;
		this.a = setA;	
		info.put("h", setInfoH);
		info.put("o", setInfoO);
		info.put("a", setInfoA);
		info.put("t", setInfoT);
		info.put("solveMethod", setInfoSolveMethod);
		normalizePoints();
	}
	
	/**
	 * Solves for the missing values, then creates a triangle with those values and solve method.
	 * @param h - hypotenuse length
	 * @param o - opposite length
	 * @param a - adjacent length
	 * @param t - theta - angle between hypotenuse and adjacent
	 * @param d - degree mode (degrees if true, radians if false)
	 * @return new triangle object
	 */
	static Triangle solveTriangle(double h, double o, double a, double t, boolean d){
		String solveMethod = "";
		String angleMode = d ? "°" : "rad";
		
		if(d) t = Math.toRadians(t);
		
		if(h!=0 && o!=0) {
			t = Math.asin(Math.abs(o)/Math.abs(h));
			solveMethod += "θ = aSin(o/h) \nRearranged from: sinθ = o/h";
			a = Math.sqrt(h*h - o*o);
		} else if(h!=0 && a!=0) {
			t = Math.acos(Math.abs(a)/Math.abs(h));
			solveMethod += "θ = aCos(a/h) \nRearranged from: cosθ = a/h";
			o = Math.sqrt(h*h - a*a);
		} else if(h!=0 && t!=0) {
			o = h*Math.sin(t);
			a = h*Math.cos(t);
			solveMethod += "o = h*sin(θ) \nRearranged from: sinθ = o/h \n\nFormula Used: a = h*cos(θ) \nRearranged from: cosθ = a/h";
		} else if(o!=0 && a!=0) {
			t = Math.atan(Math.abs(o)/Math.abs(a));
			solveMethod += "θ = aTan(o/a) \nRearranged from: tanθ = o/a";
			h = Math.sqrt(a*a + o*o);
		} else if(o!=0 && t!=0) {
			a = o/Math.tan(t);
			solveMethod += "a = o/tan(θ) \nRearranged from: tanθ = o/a";
			h = Math.sqrt(a*a + o*o);
		} else if(a!=0 && t!=0) {
			o = a*Math.tan(t);
			solveMethod += "o = a*tan(θ) \nRearranged from: tanθ = o/a";
			h = Math.sqrt(a*a + o*o);
		}
		if(d) t = Math.toDegrees(t);
		Triangle triangle = new Triangle(h,o,a,format2DP(h),format2DP(o),format2DP(a),format2DP(t)+angleMode,solveMethod);
		return triangle;
	}
	
	

	/**
	 * Sets number to 2 decimal places.
	 * @param n - number
	 * @return the number set to 2 decimal places
	 */
	static String format2DP(double n){
		DecimalFormat dec2 = new DecimalFormat("#0.00");
		return dec2.format(n);
	}
	

	/**
	 *Creates points for the triangle.
	 *Reflects the points of the triangle over the x/y axis such that they are all positive. 
	 *Scales up/down the size (point coordinates) of the triangle such that it's largest length 
	 *(relative to canvas edge length) uses the entire canvas (maxW, maxH)
	 */
	void normalizePoints(){
		double maxW = 360;
		double maxH = 180;
		double scale = 1;
		
		//determine the side length with length closest to it's respective window side length
		//scale triangle size to use the entire canvas without distorting x:y ratio
		if(Math.abs(o)/maxH > Math.abs(a)/maxW) scale = maxH / Math.abs(o);
		else scale = maxW / Math.abs(a);
		o *= scale;
		a *= scale;
		h = Math.sqrt(o*o + a*a);
		
		//side length opposite is reversed to counter-act canvas' flipped y axis
		this.ho = new Point(a/2,-o/2);
		this.ha = new Point(-a/2,o/2);
		this.oa = new Point(a/2,o/2);
		
		//reflecting triangle across x/y axis as needed to keep all triangle points on main quadrant
		if(ho.getX() < 0) translatePoints(Math.abs(ho.getX()), 0);
		if(ho.getY() < 0) translatePoints(0, Math.abs(ho.getY()));
		if(ha.getX() < 0) translatePoints(Math.abs(ha.getX()), 0);
		if(ha.getY() < 0) translatePoints(0, Math.abs(ha.getY()));
		if(oa.getX() < 0) translatePoints(Math.abs(oa.getX()), 0);
		if(oa.getY() < 0) translatePoints(0, Math.abs(oa.getY()));
	}
	
	/**
	 * Moves all points of the triangle right by x, down by y.
	 * @param x - horizontal direction
	 * @param y - vertical direction
	 */
	void translatePoints(double x, double y){
		ho.setX(ho.getX()+x);
		ho.setY(ho.getY()+y);
		ha.setX(ha.getX()+x);
		ha.setY(ha.getY()+y);
		oa.setX(oa.getX()+x);
		oa.setY(oa.getY()+y);
	}
	
	public double getH() {
		return h;
	}

	public void setH(double h) {
		this.h = h;
	}

	public double getO() {
		return o;
	}

	public void setO(double o) {
		this.o = o;
	}

	public double getA() {
		return a;
	}

	public void setA(double a) {
		this.a = a;
	}

	public Point getHo() {
		Point newHo = new Point(ho.getX(), ho.getY());
		return newHo;
	}
	
	public Point getHa() {
		Point newHa = new Point(ha.getX(), ha.getY());
		return newHa;
	}
	
	public Point getOa() {
		Point newOa = new Point(oa.getX(), oa.getY());
		return newOa;
	}

	/**
	 * Returns value of info.get(key) (Does not return a reference)
	 * @param key
	 * @return String value of info.get(key)
	 */
	public String getInfo(String key) {
		String newStr = new String(info.get(key));
		return newStr;
	}
}