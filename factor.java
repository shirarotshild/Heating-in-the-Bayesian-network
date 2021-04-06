import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
/* class that implement factor/
 * 
 */
public class factor {
	static int count=1;
	String name="f"+count;
	String value="";
	Hashtable cpt=new Hashtable<String,String>();
	int countP=0;
	int countM=0;
	//////////constructor\\\\\\\\\\

	/*/
	 * The constructor initializes the cpt according to the | and gives the factor a name
	 */
	public factor(Hashtable<String, String> cpt, String [] E) {
		Iterator<Map.Entry<String, String> > iterator = cpt.entrySet().iterator(); 
		String tempName="";
		while (iterator.hasNext()) { 
			Map.Entry<String, String>  entry = iterator.next(); 
			String key=entry.getKey();
			String tempKey=key.substring(2, key.length()-1);
			if(key.indexOf('|') != -1) {
				tempKey=key.substring(2,key.indexOf('|'))+","+key.substring(key.indexOf('|')+1,key.length()-1);
			}
			tempName=tempKey;
			String [] tempArr=tempKey.split(",");
			boolean flag=false;
			for(int i=0; i<E.length; i++) {
				for(int j=0; j<tempArr.length; j++) {
					if(E[i].substring(0,E[i].indexOf('=')).equals(tempArr[j].substring(0, E[i].indexOf('=')))){
						if(!E[i].substring(E[i].indexOf('=')+1).equals(tempArr[j].substring(E[i].indexOf('=')+1))){
							if(!flag) {
								iterator.remove();
								flag=true;
							}
						}
					}
				}
			}
		} 
		Set<String> keys =cpt.keySet();
		for( String k : keys) {
			String tempKey=k.substring(2, k.length()-1);
			if(k.indexOf('|') != -1) {
				tempKey=k.substring(2,k.indexOf('|'))+","+k.substring(k.indexOf('|')+1,k.length()-1);
			}
			String [] temp=tempKey.split(",");
			tempKey="";
			for(int i=0; i< temp.length; i++) {
				boolean flag=false;
				for(int j=0; j<E.length; j++) {
					if(temp[i].substring(0,temp[i].indexOf("=")).equals(E[j].substring(0,E[j].indexOf("=")))) {
						flag=true;
					}
				}
				if(!flag) {
					tempKey+=temp[i];
					if(i!=temp.length-1)tempKey+=",";
				}
			}
			this.cpt.put(tempKey, cpt.get(k));
		}
		String [] tempArr=tempName.split(",");
		for(int j=0; j<tempArr.length; j++) {
			boolean flag=false;
			for(int i=0; i<E.length; i++) {
				if(E[i].substring(0,E[i].indexOf('=')).equals(tempArr[j].substring(0, E[i].indexOf('=')))){
					flag=true;
				}
			}
			if(!flag) {
				this.name +=","+tempArr[j].substring(0, tempArr[j].indexOf('='));
			}
		}
		count++;
	} 
	/*/
	 * Multiplication between 2 factors with hidden
	 */
	public void join(factor f,String hidden) {
		this.countM+=f.countM;
		this.countP+=f.countP;
		Hashtable<String, String> tempcpt=new Hashtable <String , String>();
		String var=searchCommonVar(f.name);
		Set<String> mykeys = this.cpt.keySet();
		Set<String> otherkeys = f.cpt.keySet();
		for( String mk : mykeys) {
			for( String ok : otherkeys) {
				if(ChecksIfTheKeysMatch(mk, ok, var)) {
					String key=newKey(mk,ok);
					double val=Double.parseDouble((String) this.cpt.get(mk))*Double.parseDouble((String)f.cpt.get(ok));
					this.countM++;
					tempcpt.put(key, String.valueOf(val));
				}
			}		
		}
		this.cpt=tempcpt;
		this.name=newName(this.name, f.name ,hidden);
	}
	/*/
	 * Multiplication between 2 factors with hidden
	 */
	public void join(factor f) {
		this.countM+=f.countM;
		this.countP+=f.countP;
		Hashtable<String, String> tempcpt=new Hashtable <String , String>();
		String var=searchCommonVar(f.name);
		Set<String> mykeys = this.cpt.keySet();
		Set<String> otherkeys = f.cpt.keySet();
		for( String mk : mykeys) {
			for( String ok : otherkeys) {
				if(ChecksIfTheKeysMatch(mk, ok, var)) {
					String key=newKey(mk,ok);
					double val=Double.parseDouble((String) this.cpt.get(mk))*Double.parseDouble((String)f.cpt.get(ok));
					this.countM++;
					tempcpt.put(key, String.valueOf(val));
				}
			}		
		}
		this.cpt=tempcpt;
	}
	/*/
	 * the function eliminate the factor by var
	 */
	public void eliminate(String  var) {
		String []n=this.name.split(",");
		if(n.length==2)return;
		String cheak="";
		for(int i=1; i< n.length; i++) {
			if(!n[i].equals(var)) {
				cheak+=n[i];
				if(i!= n.length-1) {
					cheak+=",";
				}
			}
		}
		Hashtable<String, String> tempcpt=new Hashtable <String , String>();
		Iterator<Map.Entry<String, String> > iterator1 = cpt.entrySet().iterator(); 
		while (iterator1.hasNext()) { 
			Map.Entry<String, String>  entry1 = iterator1.next();
			double v=Double.parseDouble(entry1.getValue());
			String key="";
			iterator1.remove();
			Iterator<Map.Entry<String, String> > iterator2 = cpt.entrySet().iterator(); 
			while (iterator2.hasNext()) {
				Map.Entry<String, String>  entry2 = iterator2.next(); 
				if(ChecksIfTheKeysMatch(entry1.getKey(),entry2.getKey(),cheak)){
					v+=Double.parseDouble(entry2.getValue());
					this.countP++;
					key=newKey(entry1.getKey(),entry2.getKey(),var);
					iterator2.remove();
					iterator1=cpt.entrySet().iterator(); 
				}
			}
			tempcpt.put(key, String.valueOf(v));

		}
		this.cpt=tempcpt;
		this.name=this.name.substring(0,this.name.indexOf(",")+1)+cheak;

	}
	/*/
	 * Checks if the factors needs to be multiplied
	 */
	private boolean  ChecksIfTheKeysMatch(String mykey, String otherkey, String var) {
		String [] mk=mykey.split(",");
		String [] ok=otherkey.split(",");
		String [] v=var.split(",");
		for(int i=0; i<v.length; i++) {
			for(int j=0; j<mk.length; j++) {
				if(mk[j].substring(0,mk[j].indexOf("=")).equals(v[i])) {
					for(int k=0; k<ok.length; k++) {
						if(ok[k].substring(0,ok[k].indexOf("=")).equals(v[i])) {
							if(!ok[k].equals(mk[j])) {
								return false;
							}
						}
					}
				}
			}
		}
		return true;
	}


	/*/
	 * the function look for the common var
	 */
	private String searchCommonVar(String name) {
		String ans="";
		String [] myName=this.name.split(",");
		String [] otherName=name.split(",");
		for(int i=1; i<myName.length; i++) {
			for(int j=1; j<otherName.length; j++) {
				if(myName[i].equals(otherName[j])) {
					ans+=myName[i]+",";
				}
			}
		}
		return ans;
	}
	/*/
	 * The function gets 2 keys and returns a new key with all the values
	 */
	private String newKey(String mykey, String otherkey) {
		String key=otherkey;
		String [] mk=mykey.split(",");
		String [] ok=otherkey.split(",");
		for(int i=0; i<mk.length; i++) {
			boolean flag=false;
			for(int j=0; j<ok.length; j++ ) {
				if(mk[i].substring(0,mk[i].indexOf("=")).equals(ok[j].substring(0,ok[j].indexOf("=")))) {
					flag=true;
				}
			}
			if(!flag) {
				key=key+","+mk[i];
			}
		}
		return key;
	}
	/*/
	 * The function gets 2 keys and returns a new key with all the values except  val 
	 */
	private String newKey(String mykey, String otherkey,String var) {
		String key=otherkey;
		String [] mk=mykey.split(",");
		String [] ok=otherkey.split(",");
		for(int i=0; i<mk.length; i++) {
			boolean flag=false;
			for(int j=0; j<ok.length; j++ ) {
				if(mk[i].substring(0,mk[i].indexOf("=")).equals(ok[j].substring(0,ok[j].indexOf("=")))) {
					flag=true;
				}
			}
			if(!flag) {
				key=key+","+mk[i];
			}
		}
		mk=key.split(",");
		key="";
		for(int i=0; i<mk.length; i++) {
			if(!mk[i].substring(0,mk[i].indexOf("=")).equals(var)) {
				key+=mk[i];
				if(i!=mk.length-1) key+=",";
			}
		}
		return key;
	}
	/*/
	 * The function gets 2 names and returns a new name with all the values
	 */
	private String newName(String myName , String otherName, String var) {
		String name=otherName;
		String [] mn=myName.split(",");
		String [] on=otherName.split(",");
		for(int i=1; i<mn.length; i++) {
			boolean flag=false;
			for(int j=1; j<on.length; j++ ) {
				if(mn[i].equals(on[j])) {
					flag=true;
				}
			}
			if(!flag) {
				name=name+","+mn[i];
			}
		}
		return name;
	}
	public String toString() {
		return "name:"+this.name+"\n"+"cpt:"+cpt.toString()+"\n";
	}



	
}

