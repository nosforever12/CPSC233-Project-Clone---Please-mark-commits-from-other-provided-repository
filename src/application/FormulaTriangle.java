package application;

import javafx.scene.canvas.Canvas;

/**
 * Child class of Triangle, overrides methods such that unknown 
 * side lengths/angle are solved for in terms of the given values treated 
 * as variables, outputting algebraic formulas instead of concrete values.
 */
public class FormulaTriangle extends Triangle {
	/**FormulaTriangle Constructor. All values are passed to the parent class Triangle,
	 * as any differences in the process between the two triangle types are correctly handled
	 * with overridden methods, causing differences in the constructor to be unnecessary.
	 * @param inputHyp - value entered for hypotenuse side length
	 * @param inputOpp - value entered for opposite side length
	 * @param inputAdj - value entered for adjacent side length
	 * @param inputAng - value entered for angle theta
	 * @param angleModeDegrees - degree mode - true for degrees, false for radians
	 */
	FormulaTriangle(String inputHyp, String inputOpp, String inputAdj, String inputAng, boolean angleModeDegrees, Canvas canvasToDrawOn){
		super(inputHyp, inputOpp, inputAdj, inputAng, angleModeDegrees, canvasToDrawOn);
	}
	
	/**
	 * Solves for a formula representing the missing values of the triangle's sidelengths or angle
	 * using trigonometry. String values containing these formulas are put into the triangle's hashmap 
	 * under the keys "hyp", "opp", "adj", "ang", etc. along with the method/formula used under the key 
	 * "solveMethod". Unknown parameters are entered as 0, and will be solved for.
	 * @param hyp - hypotenuse side length of the triangle
	 * @param opp - opposite side length of the triangle
	 * @param adj - adjacent side length of the triangle
	 * @param ang - theta - angle between hypotenuse and adjacent side lengths
	 * @param angleModeDegrees - degree mode (degrees if true, radians if false) of value entered for theta
	 */
    @Override
	protected void calculateMissingValues(double hyp, double opp, double adj, double ang, boolean angleModeDegrees) {
		String solveMethod = "";
		String hypInput = getInfo("HypotenuseInput");
		String oppInput = getInfo("OppositeInput");
		String adjInput = getInfo("AdjacentInput");
		String angInput = getInfo("Angle θInput");
		
		//if side length/angle values aren't equal to 0, they have a value given by the user,
		//and therefore should be used to calculate the values of the rest of the triangle.
		if(hyp!=0 && opp!=0) {
			setInfo("ang", "aSin(" + oppInput + " / " + hypInput + ")");
			solveMethod += "θ = aSin(o/h) \nRearranged from: sinθ = o/h";
			setInfo("adj", "sqrt(" + hypInput + "² - " + oppInput + "²)");
		} else if(hyp!=0 && adj!=0) {
			setInfo("ang", "aCos(" + adjInput + " / " + hypInput + ")");
			solveMethod += "θ = aCos(a/h) \nRearranged from: cosθ = a/h";
			setInfo("opp", "sqrt(" + hypInput + "² - " + adjInput + "²)");
		} else if(hyp!=0 && ang!=0) {
			setInfo("opp", hypInput + " * sin(" + angInput + ")");
			setInfo("adj", hypInput + " * cos(" + angInput + ")");
			solveMethod += "o = h*sin(θ) \nRearranged from: sinθ = o/h \n\nTrig. Formula Used: a = h*cos(θ) \nRearranged from: cosθ = a/h";
		} else if(opp!=0 && adj!=0) {
			setInfo("ang", "aTan(" + oppInput + " / " + adjInput + ")");
			solveMethod += "θ = aTan(o/a) \nRearranged from: tanθ = o/a";
			setInfo("hyp", "sqrt(" + adjInput + "² + " + oppInput + "²)");
		} else if(opp!=0 && ang!=0) {
			setInfo("adj", oppInput + " / tan(" + angInput + ")");
			solveMethod += "a = o/tan(θ) \nRearranged from: tanθ = o/a";
			setInfo("hyp", "sqrt(" + adjInput + "² + " + oppInput + "²)");
		} else if(adj!=0 && ang!=0) {
			setInfo("opp", adjInput + " * tan(" + angInput + ")");
			solveMethod += "o = a*tan(θ) \nRearranged from: tanθ = o/a";
			setInfo("hyp", "sqrt(" + adjInput + "² + " + oppInput + "²)");
		}
		
		//setting the triangle's actual sidelengths to a default of 1,1 for the
		//opposite and adjacent, so that a triangle may be drawn later to be labeled.
		setOpp(1);
		setAdj(1);
		setHyp(Math.sqrt(getOpp()*getOpp() + getAdj()*getAdj()));
		
		//we can store the solve method here since it does not
		//need additional formatting or checks to be displayed to the user
		setInfo("solveMethod", solveMethod);
	}
    
	/**
	 * The user may want to solve for a algebraic formula for a language outside of 
	 * java, so all variable names are valid.
	 * @param textField - the label of the input text field being checked
	 * @param text - text within the input text field being checked
	 * @param degreesMode - whether the program is currently calculating in degrees or radians
	 * @return String value describing the error causing the input to be invalid (empty string if no error) 
	 */
	@Override
    protected String checkError(String textField, String text, boolean degreesMode) {
    	return getErrorDescription();
    }
	
	
	/**
	 * Stores side length values to the hashmap under the keys "h", "o", "a", "t" using previously stored 
	 * validated user input. These are the final values that will be assigned to the labels and displayed to the user.
	 */
	@Override
	protected void storeInfoInHashMap(){
		if(!getInfo("HypotenuseInput").isEmpty()) setInfo("hyp", getInfo("HypotenuseInput"));
		if(!getInfo("OppositeInput").isEmpty()) setInfo("opp", getInfo("OppositeInput"));
		if(!getInfo("AdjacentInput").isEmpty()) setInfo("adj", getInfo("AdjacentInput"));
		if(!getInfo("Angle θInput").isEmpty()) setInfo("ang", getInfo("Angle θInput"));
	}
}
