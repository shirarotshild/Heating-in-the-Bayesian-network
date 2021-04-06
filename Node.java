import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
/*/
 * class that implementing var in the Bayesian network
 */
public class Node {
	String [] values;// The values ​​that the variable can have
	String [] parents;// the parents of the var
	Hashtable cpt =new Hashtable<String, String>(); //the cpt of the var
	Boolean flag=false; // helper var
	//////////constructor\\\\\\\\\\
	public Node (String values, String parents) {
		this.values=values.split(",");
		this.parents=parents.split(",");
	}
	/*/
	 * copy constructor
	 */
	public Node(Node d) {
		this.values=d.values;
		this.parents=d.parents;
		Set<String> keys = d.cpt.keySet();
		for(String key: keys){
			this.cpt.put(key, d.cpt.get(key));
		}
	}
	//////////Methods\\\\\\\\\\
	/*/
	 * Checks if the cpt contains a key
	 */
	public double containsKey(String Key) {
		Key=Key.substring(2, Key.indexOf("|"))+","+Key.substring(Key.indexOf("|")+1,Key.length()-1);
		String [] arrKey=Key.split(",");
		Set<String> keys = this.cpt.keySet();
		for(String key: keys){
			if(key.indexOf("|")!=-1 ) {
				String tempk=key.substring(2,key.indexOf("|"))+","+key.substring(key.indexOf("|")+1,key.length()-1);
				String [] tempKey=tempk.split(",");
				boolean flag=compareArrays(arrKey,tempKey);
				if(flag) {
					return Double.parseDouble((String) cpt.get(key));
				}
			}
		}
		return -1;
	}
	///////////helper function\\\\\\\\\\
	/*/
	 * Checks if 2 arrays are equal
	 */
	private boolean compareArrays(String[] arr1, String[] arr2) {
		HashSet<String> set1 = new HashSet<String>(Arrays.asList(arr1));
		HashSet<String> set2 = new HashSet<String>(Arrays.asList(arr2));
		return set1.equals(set2);
	}

	public String toString() {
		return "valuse:"+Arrays.toString(values)+" flag: "+flag+"\n"+"parents:"+Arrays.toString(parents)+"\n"+cpt.toString()+"\n";
	}
}
