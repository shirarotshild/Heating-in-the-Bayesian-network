import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;

public class Algo1 {
	BayesianNetwork bn;
	String querie="";
	//////////constructor\\\\\\\\\\
	public Algo1 (BayesianNetwork bn, String querie) {
		this.bn=bn;
		this.querie=querie;	
	}
	//////////Methods\\\\\\\\\\
	/*/
	 * Arranges the input and sends the probabilities to Algo1 and calculates the final answer
	 */
	public double [] start(){
		String line=this.querie;
		//Calculate the probability of the query according to Algo1
		double[] ans=Algo1(this.querie);
		ArrayList<Double>allAns= new ArrayList<Double>();
		String val=line.substring(line.indexOf("=")+1,line.indexOf("|"));
		//Calculate the complementary probability
		for(int i=0; i<bn.hashTable.get(line.substring(0,line.indexOf("="))).values.length; i++) {
			if(!val.equals(bn.hashTable.get(line.substring(0,line.indexOf("="))).values[i])) {
				line=line.substring(0,line.indexOf("="))+"="+bn.hashTable.get(line.substring(0,line.indexOf("="))).values[i]+"|"
						+line.substring(line.indexOf("|")+1);
				double[] tempAns=Algo1(line);
				allAns.add(tempAns[0]);
				ans[1]+=tempAns[1];
				ans[2]+=tempAns[2];
			}

		}
		//Calculates the denominator
		double denominator=ans[0];
		for(int i=0; i<allAns.size(); i++) {
			denominator+=allAns.get(i);
			ans[1]++;
		}
		//Normalizes the answer
		ans[0]=ans[0]/denominator;
		return ans;
	}

	/*/
	 * Calculate the probability of the query according to Algo1
	 */
	public  double [] Algo1 (String queries) {
		this.querie=queries;
		int index=0;
		double []ans= new double [3];
		String endAns="";
		//Looking for the index of the char '|'
		for(int i=0; i<querie.length(); i++) {
			if (querie.charAt(i) == '|') {
				index=i;
				break;
			}
		}
		querie=querie.substring(0,index)+","+querie.substring(index+1);
		String var="";
		//Check which variables are not found in querie.
		String [] temp=querie.split(",");
		for(int i=0; i<bn.variables.length; i++) {
			boolean flag=false;
			for(int j=0; j<temp.length; j++) {
				if(bn.variables[i].equals(temp[j].substring(0,bn.variables[i].length()))) {
					flag =true;
				}
			}
			if(!flag) {
				var+=bn.variables[i]+",";
			}
		}
		if(var.equals("") == false) {
			String []arrVar=var.split(",");
			ArrayList<String[]> value = new ArrayList<String[]>();
			for (int i = 0; i <arrVar.length; i++) {
				value.add(bn.hashTable.get(arrVar[i]).values);
			}
			//Looking for all the possible permutations of the hidden
			String varQuerie=querie;
			querie="";
			combine(value, new String[value.size()], 0);
			String []arrVal=querie.split(",");
			querie="";
			for(int i=0; i<arrVal.length; i++) {
				String []tempVal=arrVal[i].split(" ");
				querie+=varQuerie;
				for(int j=0; j<tempVal.length; j++) {
					querie+=","+arrVar[j]+"="+tempVal[j];
				}
				querie+=" ";
			}
			//Calculate the probability according to the complete probability formula
			String [] arrQuerie=querie.split(" ");
			for(int i=0; i<arrQuerie.length; i++) {
				String []arr=arrQuerie[i].split(",");
				String tempQuerie="";
				for(int j=0; j<arr.length; j++) {
					tempQuerie+="P("+arr[j];
					int ind = arr[j].indexOf("=");
					if(!bn.hashTable.get(arr[j].substring(0,ind)).parents[0].equals("none")) {
						tempQuerie+="|";
						for(int k=0; k<bn.hashTable.get(arr[j].substring(0,ind)).parents.length; k++) {
							for(int l=0; l<arr.length; l++) {
								if(bn.hashTable.get(arr[j].substring(0,ind)).parents[k].equals(arr[l].substring(0,ind)) ) {
									tempQuerie+=arr[l];
									if(k!= bn.hashTable.get(arr[j].substring(0,ind)).parents.length-1) tempQuerie+=",";
								}
							}
						}
					}
					tempQuerie+=") ";
				}
				String []calculatQuerie=tempQuerie.split(" ");
				double calculat=1;
				for(int j=0; j<calculatQuerie.length; j++) {
					String val=(String) bn.hashTable.get(calculatQuerie[j].substring(2,calculatQuerie[j].indexOf("="))).cpt.get(calculatQuerie[j]);
					double convertVal=Double.parseDouble(val);
					//Counts the number of multiplications
					if(calculat!=1)ans[2]++;
					calculat*=convertVal;

				}
				//Counts the number of plus
				if(ans[0]!=0)ans[1]++;
				ans[0]+=calculat;
			}
		}
		return ans;

	}
	//////////helper functions\\\\\\\\\\

	/*/
	 * The function looking for all the possible permutations of the hidden
	 */

	private void combine(ArrayList<String[]> input, String[] current, int k) {
		if(k == input.size()) {
			for(int i = 0; i < k; i++) {
				querie+=current[i]+" ";
			}
			querie+=",";
		} 
		else {            
			for(int j = 0; j < input.get(k).length; j++) {
				current[k] = input.get(k)[j];
				combine(input, current, k + 1);
			}       
		}
	}

}