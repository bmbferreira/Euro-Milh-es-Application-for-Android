package euro.milhoes.app;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
/**
 * FileDealer.java
 * Purpose: Save and load information to and from a given File.
 *
 * @author Bruno Ferreira
 * @version 1.10 8/11/11
 */
//date format: mm/dd/yy
public abstract class FileDealer {
	/**
	 * Method to save/store data (an Object) in a file.
	 */
	protected static void storeData (Object data, FileOutputStream fos){
		try {			
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(data);
			oos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Method to load data (an Object) from a file.
	 */
	protected static Object loadData (FileInputStream fis){
		try {
			
			ObjectInputStream ois = new ObjectInputStream(fis);
			Object data = ois.readObject();
			ois.close();
			return data;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
}
