package euro.milhoes.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.util.Iterator;
import java.util.TreeSet;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Statistics.java
 * Purpose: Statistics of the numbers in Euro Millions draws.
 *
 * @author Bruno Ferreira
 * @version 1.10 8/10/11
 */
//date format: mm/dd/yy
public class Statistics extends Activity implements Runnable{
	/* TextViews where the numbers and the number of times that number appeard
	 * in a draw will be presented to the user.
	 * I used six 'cause I show them like a table, for example:
	 * "tv1\n tv2\n tv3\n
	 * 		(...)
	 *  tv1\n tv2\n tv3\n"
	 * Where tv1 shows the numbers, tv2 shows the sign '->', and tv3 shows the number of times
	 * a number came out in a draw.
	 * The other three TextViews are for stars.*/
	TextView tv1;
	TextView tv2;
	TextView tv3;
	TextView tv4;
	TextView tv5;
	TextView tv6;

	/* Strings that will be used to be shown in TextViews*/
	String text1 = new String();
	String text2 = new String();
	String text3 = new String();
	String text4 = new String();
	String text5 = new String();
	String text6 = new String();

	/*Progress dialog to be shown when the application is updating data.*/
	ProgressDialog pd;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//setting the R.layout.statistics, referencing the file statistics.xml
		setContentView(R.layout.statistics);

		//Initializing the strings
		text1 = new String();
		text2 = new String();
		text3 = new String();
		text4 = new String();
		text5 = new String();
		text6 = new String();

		//Initializing the TextViews, referencing the id's of the TextViews
		tv1=(TextView)findViewById(R.id.textView1);
		tv2=(TextView)findViewById(R.id.textView2);
		tv3=(TextView)findViewById(R.id.textView3);
		tv4=(TextView)findViewById(R.id.textView4);
		tv5=(TextView)findViewById(R.id.textView5);
		tv6=(TextView)findViewById(R.id.textView6);

		/* The two files where the statistics will be saved. If one of this files doesn't exists
		 * it means that it's the first time the app is running, so the data will be
		 * loaded from the files that are in the "raw" folder. In the next executions of the app
		 * data will be loaded from this two files that are created in the first execution of 
		 * the application. Every time the user press the "actualizar" button in the menu, this
		 * files will be modified, if there's modifications in the website with the EuroMillion's
		 * results and statistics. */
		File bolasStats = getApplicationContext().getFileStreamPath("ballsStats");
		File estrelasStats = getApplicationContext().getFileStreamPath("starsStats");

		try {
			//if the file whit the name "ballsStats" exists...
			if(bolasStats.exists()){ 


				Pair aux = null;//variable to extract a number and the number of times it appeard
				String filename = bolasStats.getName();
				FileInputStream fis = openFileInput(filename);

				// the treeset with the numbers organized by the number of times which one appeard
				TreeSet <Pair> statBalls = (TreeSet <Pair>) FileDealer.loadData(fis);

				// the iterator of the treeset used to "travel" the organized set of numbers
				Iterator <Pair> iteraTree = statBalls.iterator();			

				/* In this cycle we "travel" the TreeSet and we put the numbers and the number of times
				 * that a number appeard in a draw and also the signal in the middle to separate the two
				 * parts of the information. At the en we'll have, for example:
				 * " text1(number) | text2(signal) | text3(number of times)
				 *      1 (ENTER)        -> (ENTER)        	50(ENTER)
				 *      12(ENTER)        -> (ENTER)        	48(ENTER)
				 *      50(ENTER)		 -> (ENTER)			34(ENTER)
				 *                 (.........................)
				 *      43(ENTER)		 -> (ENTER)			1 (ENTER) 				*/
				while(iteraTree.hasNext()){
					aux = iteraTree.next();
					text1 += Integer.toString(aux.num) +"\n";
					text2 += " -> " +"\n";
					text3 += Integer.toString(aux.freq)+"\n";	
				}

				//showing the string in the respectives TextViews
				tv1.setText(text1);
				tv2.setText(text2);
				tv3.setText(text3);

				//if the files don't exist...
			}else{
				Pair aux = null;

				//...we load the data from the file in the raw folder
				InputStream is = getResources().openRawResource(R.raw.ballsstatistics);
				ObjectInputStream ois;
				ois = new ObjectInputStream(is);

				// getting the TreeSet from the file...
				TreeSet <Pair> statBalls = (TreeSet <Pair>) ois.readObject();

				ois.close();
				String filename = bolasStats.getName();

				/* ...and saving it in the "ballsStats" file that i refered previously. 
				 * If we enter this else, it means that (probably) it's the first time 
				 * this application is running in a device so, once we can't modify a file
				 * in a raw folder, we have to copy its content to another file ant modify 
				 * this file in the future. I did this cause a device it's not always connected to
				 * internet, but this application will work the same way, however the user only 
				 * can update the data in the app if he's connected to internet.*/
				FileOutputStream fos = openFileOutput(filename, Context.MODE_PRIVATE);
				FileDealer.storeData(statBalls, fos);

				/* Again, an iterator and a cycle to "deal" with the information and then
				   show it to the user in TextViews.*/
				Iterator <Pair> iteraTree = statBalls.iterator();			
				while(iteraTree.hasNext()){
					aux = iteraTree.next();
					text1 += Integer.toString(aux.num) +"\n";
					text2 += " -> " +"\n";
					text3 += Integer.toString(aux.freq)+"\n";	
				}
				tv1.setText(text1);
				tv2.setText(text2);
				tv3.setText(text3);

			}

			//If there's an exception, a message will be shown and the app will close.
		} catch (StreamCorruptedException e) {
			e.printStackTrace();
			showAlertForExceptions();
		} catch (IOException e) {
			e.printStackTrace();
			showAlertForExceptions();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			showAlertForExceptions();
		}

		/* This "try block" works pretty same as the previous "try block" that I explained previously.
		 * However, this bloc is for the statistics of the stars, and the previous was for the statistics
		 * of normal balls.*/
		try{
			if(estrelasStats.exists()){
				Pair aux = null;
				String filename = estrelasStats.getName();
				FileInputStream fis = openFileInput(filename);
				TreeSet <Pair> statStars = (TreeSet<Pair>) FileDealer.loadData(fis);
				Iterator <Pair> iteraTreeStars = statStars.iterator();
				while(iteraTreeStars.hasNext()){
					aux = iteraTreeStars.next();
					text4 += Integer.toString(aux.num) +"\n";
					text5 += " -> " +"\n";
					text6 += Integer.toString(aux.freq)+"\n";	
				}
				tv4.setText(text4);
				tv5.setText(text5);
				tv6.setText(text6);
			}else{
				Pair aux = null;
				InputStream is = getResources().openRawResource(R.raw.starsstatistics);
				ObjectInputStream ois;
				ois = new ObjectInputStream(is);
				TreeSet <Pair> statStars = (TreeSet <Pair>) ois.readObject();
				ois.close();
				String filename = estrelasStats.getName();
				FileOutputStream fos = openFileOutput(filename, Context.MODE_PRIVATE);
				FileDealer.storeData(statStars, fos);
				Iterator <Pair> iteraTreeStars = statStars.iterator();
				while(iteraTreeStars.hasNext()){
					aux = iteraTreeStars.next();
					text4 += Integer.toString(aux.num) +"\n";
					text5 += " -> " +"\n";
					text6 += Integer.toString(aux.freq)+"\n";	
				}
				tv4.setText(text4);
				tv5.setText(text5);
				tv6.setText(text6);
			}
		} catch (StreamCorruptedException e) {
			e.printStackTrace();
			showAlertForExceptions();
		} catch (IOException e) {		
			e.printStackTrace();
			showAlertForExceptions();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			showAlertForExceptions();
		}
	}
	/**
	 * Method to create a menu with a button with text and a icon
	 * where the user can update the statistics of EuroMillions draws.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	/**
	 * This method is called when the user clicks on "actualizar button".
	 * A Progress Dialog will appear while the application contacts the website to extract
	 * the information to update the application. 
	 * A thread is created to extract that information. 
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.icontext:
			pd = ProgressDialog.show(this, "A actualizar...", "A actualizar as estatisticas.", true,
					false);

			Thread thread = new Thread(this);
			thread.start();

			break;
		}
		return true;
	}

	/**
	 * The thread will get data from the website and will create the strings to be shown in
	 * TextViews. 
	 */
	@Override
	public void run() {

		//The strings are initialized again to store new information.
		text1 = new String();
		text2 = new String();
		text3 = new String();
		text4 = new String();
		text5 = new String();
		text6 = new String();

		//Getting the information from the website...
		TreeSet <Pair> statBalls = HTMLDealer.ballsStatistics();
		TreeSet <Pair> statStars = HTMLDealer.starsStatistics();	

		/* If there's no information returned from the previous HTML methods calls, 
		 * it means that (probably) the device is not connected to network...
		 */
		if(statBalls != null && statStars !=null ){
			try {

				//Getting the files to store the new information from the website
				File bolasStats = getApplicationContext().getFileStreamPath("ballsStats");
				File estrelasStats = getApplicationContext().getFileStreamPath("starsStats");

				Pair aux = null;

				/* this is pretty same the code we've seen before,
				 * i'm just storing the new data and preparing the strings to show
				 * in the application. Here i'm doing this for normal balls statistics.
				 * */
				String filename = bolasStats.getName();
				FileOutputStream fos;
				fos = openFileOutput(filename, Context.MODE_PRIVATE);
				FileDealer.storeData(statBalls, fos);
				Iterator <Pair> iteraTree = statBalls.iterator();			
				while(iteraTree.hasNext()){
					aux = iteraTree.next();
					text1 += Integer.toString(aux.num) +"\n";
					text2 += " -> " +"\n";
					text3 += Integer.toString(aux.freq)+"\n";	
				}

				/* this is pretty same the code we've seen before,
				 * i'm just storing the new data in the file and preparing the strings to show
				 * in the application. Here i'm doing this for stars statistics.
				 * */
				String filename2 = estrelasStats.getName();
				FileOutputStream fos2 = openFileOutput(filename2, Context.MODE_PRIVATE);
				FileDealer.storeData(statStars, fos2);
				Iterator <Pair> iteraTreeStars = statStars.iterator();
				while(iteraTreeStars.hasNext()){
					aux = iteraTreeStars.next();
					text4 += Integer.toString(aux.num) +"\n";
					text5 += " -> " +"\n";
					text6 += Integer.toString(aux.freq)+"\n";	
				}

				/*at the end, i sen an empty message to the handler with the number zero,
				 * he will know what to do with this "zero".*/
				handler.sendEmptyMessage(0);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				//as we've seen, if there's an exception, we show an alert and... exit!
				showAlertForExceptions();
			}
		}else{
			/* If there's no information returned from the previous HTML methods calls, 
			 * it means that (probably) the device is not connected to network, so we
			 * do nothing, just send an empty message with the number 1 to handler 
			 * and he will do the rest.
			 */
			handler.sendEmptyMessage(1);
		}
	}

	/**
	 * This is the handler used in the previous "run" method. 
	 * Handle is responsible to show the strings created previously, with the new information
	 * from the website, in the TextViews in the app.
	 */
	private Handler handler = new Handler() {		
		@Override
		public void handleMessage(Message msg) {

			/*If the handler method received an empty message with the number 0 in it,
			 * it means that we received successfully information from the website, so we're
			 * ready to show it to the user.*/
			if(msg.what == 0){

				/* we dismiss the progress dialog so 
				 * we can successfully set the TextViews 
				 * with the new information*/
				pd.dismiss();

				//Setting the textviews with the strings...
				tv1.setText(text1);
				tv2.setText(text2);
				tv3.setText(text3);
				tv4.setText(text4);
				tv5.setText(text5);
				tv6.setText(text6);

				//Showing a little message that the data was successfully updated
				dadosActualizados();

				/*If the handler method received an empty message with the number 1 in it,
				 * it means that (probably) the device was not connected to the internet,
				 * so we can't obtain information from the website...*/	
			}else 
				if (msg.what==1){

					//so... we dismiss the progress dialog...					
					pd.dismiss(); 

					/*and we show an alert message, 
					 * telling the user to check his 
					 * connection to the internet*/
					showAlertForExceptions2(); 
				}
		}

	};

	/**
	 * Alert message to be shown when an exception occurs.
	 */
	private void showAlertForExceptions(){
		new AlertDialog.Builder( this )
		.setTitle( "Erro!" )
		.setMessage( "Ocorreu um erro. Reinicie a aplicação."  )
		.setPositiveButton( "Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Log.d("AlertDialog", "Positive");
				finish();
			}
		})
		.show();
	}

	/**
	 * Alert message to be shown (normally) when the device is not connected to 
	 * internet, and the user tried to update the application.
	 */
	private void showAlertForExceptions2(){
		new AlertDialog.Builder( this )
		.setTitle( "Erro!" )
		.setMessage( "Ocorreu um erro.\n Verifique se está ligado á internet e tente novamente."  )
		.setPositiveButton( "Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Log.d("AlertDialog", "Positive");
				finish();
			}
		})
		.show();
	}

	/**
	 * Little message to be shown when a successfully update occurs.
	 */
	private void dadosActualizados(){
		Toast.makeText(this, "As estatisticas foram actualizadas.", Toast.LENGTH_LONG).show();
	}
}