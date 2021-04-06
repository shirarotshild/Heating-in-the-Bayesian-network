import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
public class Algo3 {
	BayesianNetwork bn;
	ArrayList<factor> factors;
	String querie="";

	public Algo3(BayesianNetwork bn, String querie) {
		this.bn=new BayesianNetwork(bn);
		this.querie=querie;
		this.factors=new ArrayList<factor>();
	}
	public double [] start(){
		String [] arr=querie.substring(querie.indexOf('|')+1,querie.length()).split(",");
		DeletingUnnecessarykeys();//Deleting unnecessary key from the cpt
		//Initialize the factors and add them to the list
		Set<String> keys = this.bn.hashTable.keySet();
		for(String key: keys){
			Hashtable cpt =new Hashtable<String, String>();
			Set<String> K = this.bn.hashTable.get(key).cpt.keySet();
			for(String k : K) {
				cpt.put(k, this.bn.hashTable.get(key).cpt.get(k));
			}
			factor f=new factor(cpt , arr);
			if(cpt.size()!=1)
				factors.add(f);
		}
		arr=(querie.substring(0,querie.indexOf('|'))+","+querie.substring(querie.indexOf('|')+1)).split(",");
		String []hidden=searhhidden(bn.variables,arr);
		if(hidden != null)hidden=sort(hidden);//Sorts by heuristic function
		double [] ans= Algo2(hidden);
		return ans;
	}
	/*/
	 * Calculate the probability of the query according to Algo2
	 */
	public double [] Algo2(String [] hidden) {
		if(hidden != null) {
			for(int i=0; i<hidden.length; i++) {
				int []index= {-1,-1};
				while(factors.size()>1) {
					index=searchIndex(hidden[i]);
					if(index[1] == -1 ) {
						break;
					}
					factors.get(index[0]).join(factors.get(index[1]),hidden[i]);
					factors.remove(index[1]);

				}
				if(factors.size() == 1) {
					factors.get(0).eliminate(hidden[i]);
				}
				else {
					factors.get(index[0]).eliminate(hidden[i]);
				}
			}

		}
		while(factors.size()>1) {
			factors.get(0).join(factors.get(1));
			factors.remove(1);
		}
		Set<String> keys =factors.get(0).cpt.keySet();
		double denominator=0;
		double namerator=0;
		for(String key : keys) {
			if(denominator!=0)factors.get(0).countP++;
			denominator+=Double.parseDouble((String) factors.get(0).cpt.get(key));
			if(key.equals(querie.substring(0,querie.indexOf("|"))+",")) {
				namerator=Double.parseDouble((String) factors.get(0).cpt.get(key));
			}
			if(key.equals(querie.substring(0,querie.indexOf("|")))) {
				namerator=Double.parseDouble((String) factors.get(0).cpt.get(key));
			}
		}
		double [] ans= new double [3];
		ans[0]=namerator/denominator;
		ans[1]=factors.get(0).countP;
		ans[2]=factors.get(0).countM;
		return ans;
	}
	/*/
	 * The function returns an array of indexes of the factor we want to multiply;
	 */
	private int [] searchIndex(String var) {
		int size=0;
		int [] ans= {-1,-1};
		int sumName=0;
		for(int i=0; i<factors.size(); i++) {
			if(factors.get(i).name.contains(var)) {
				if(size ==0) {
					ans[0]=i;
					size=factors.get(i).cpt.size();
					sumName=ASCIIsum(factors.get(i).name.split(","));
				}
				else if(size > factors.get(i).cpt.size()){
					ans[0]=i;
					size=factors.get(i).cpt.size();
					sumName=ASCIIsum(factors.get(i).name.split(","));
				}
				else if(size == factors.get(i).cpt.size()) {
					int tempSum=ASCIIsum(factors.get(i).name.split(","));
					if(tempSum < sumName) {
						sumName=tempSum;
						ans[0]=i;
						size=factors.get(i).cpt.size();

					}

				}
			}
		}
		size=0;
		for(int i=0; i<factors.size(); i++) {
			if(i == ans[0]) i++;
			if(i == factors.size()) return ans;
			if(factors.get(i).name.contains(var)) {
				if(size ==0) {
					ans[1]=i;
					size=factors.get(i).cpt.size();
				}
				else if(size >factors.get(i).cpt.size()){
					ans[1]=i;
					size=factors.get(i).cpt.size();
				}
			}
		}
		return ans;
	}
	/*/
	 * The function deletes factors that are not needed as required by Algo2
	 */
	private void DeletingUnnecessarykeys() {
		String tempquerie=querie.substring(0,querie.indexOf('|'))+","+querie.substring(querie.indexOf('|')+1);
		String [] arr=tempquerie.split(",");
		for(int i=0; i<arr.length; i++) {
			bn.hashTable.get(arr[i].substring(0,arr[i].indexOf("="))).flag=true;
			DeletingUnnecessarykeys(bn.hashTable.get(arr[i].substring(0,arr[i].indexOf("="))).parents);
		}
		Set<String> keys = bn.hashTable.keySet();
		Iterator<String> itr = keys.iterator();
		while (itr.hasNext()) {
			if(!bn.hashTable.get(itr.next()).flag) 
			{
				itr.remove();

			}
		}

	}
	/*/
	 * Recursive function pass on to parents
	 */
	private void DeletingUnnecessarykeys(String [] parent) {
		if(parent[0].equals("none"))return;
		for(int i=0; i<parent.length; i++) {
			bn.hashTable.get(parent[i]).flag=true;
			DeletingUnnecessarykeys(bn.hashTable.get(parent[i]).parents);

		}
	}
	/*/
	 * The function searches for the variables that did not appear in the query
	 */
	private String [] searhhidden(String [] variables, String [] temp) {
		String var="";
		for(int i=0; i<variables.length; i++) {
			boolean flag=false;
			for(int j=0; j<temp.length; j++) {
				if(variables[i].equals(temp[j].substring(0,variables[i].length()))) {
					flag =true;
				}
			}
			if(!flag) {
				var+=variables[i]+",";
			}
		}
		if(var.equals("") == false) {
			return var.split(",");
		}
		return null;}
	/*/
	 * Calculates the ASCIIsum of the factor name
	 */
	private int ASCIIsum( String [] name) {
		int count=0;
		for(int i=1; i<name.length; i++) {
			for(int j=1; j<name[i].length(); j++) {
				int asciiValue = name[i].charAt(j);
				count+=asciiValue;
			}
		}
		return count;
	}
	/*/
	 * Sorts by heuristic function
	 */
	private String [] sort(String []hidden) {
		int [] sizeFactor=new int [hidden.length];
		for(int i=0; i<hidden.length; i++) {
			sizeFactor[i]=sizeFactor(hidden[i]);
		}
		int temp = 0; 
		String tempVar="";
		for (int i = 0; i <sizeFactor.length; i++) {     
			for (int j = i+1; j <sizeFactor.length; j++) {     
				if(sizeFactor[i] >sizeFactor[j]) {      //swap elements if not in order
					temp = sizeFactor[i];   
					tempVar=hidden[i];
					sizeFactor[i] = sizeFactor[j];    
					sizeFactor[j] = temp;    
				}     
			}  
		}
		return hidden;
	}
	private int sizeFactor(String var) {
		int size=0;
		for(int i=0; i<factors.size(); i++) {
			if(factors.get(i).name.contains(var)) {
				if(size ==0) {
					size=factors.get(i).cpt.size();
				}
				else if(size < factors.get(i).cpt.size()){
					size=factors.get(i).cpt.size();
				}
			}
		}
		return size;
	}
}