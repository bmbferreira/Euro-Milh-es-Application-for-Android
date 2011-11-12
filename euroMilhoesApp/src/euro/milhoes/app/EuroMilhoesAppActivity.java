package euro.milhoes.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.Random;
import java.util.TreeSet;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.ads.*;

/**
 * EuroMilhoesAppActivity.java
 * Purpose: Principal layout and the principal menu of the Application.
 *
 * @author Bruno Ferreira
 * @version 1.10 8/10/11
 */
//date format: mm/dd/yy
public class EuroMilhoesAppActivity extends Activity implements Runnable, OnClickListener{

	private AdView adView;//adView, for ads

	//Buttons to generate a key, to show statistics and to update the data of the app
	private Button gerarChave, estatisticas, actualizar, about;

	//TextViews to show data in the layout
	private TextView k1,k2,k3,k4,k5,s1,s2, balls, stars, tvTitle;

	ProgressDialog pd;//Progress dialog to show while updating

	String b,s,dma;//Strings to store balls, stars and day/month/year, respectively

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		// Look up the AdView as a resource and load a request.
		AdView adView = (AdView)this.findViewById(R.id.adView);
		AdRequest request = new AdRequest();
		request.setTesting(false);
		adView.loadAd(request);
		
		try {

			/*This file is used to store the result of the last draw*/
			File result = getApplicationContext().getFileStreamPath("result");

			/* If the file exists, it (probably) means that this is not the first
			 * tim the application is running on this device, so we'll load the data
			 * from the file and we will show it in the TextViews*/
			if(result.exists()){
				/*Initializing the TextViews*/
				balls = new TextView(this);
				balls = (TextView)findViewById(R.id.textViewResBalls);
				stars = new TextView(this);
				stars = (TextView)findViewById(R.id.textViewResStars);

				/*Loading data from the file*/
				String filename = result.getName();
				FileInputStream fis = openFileInput(filename);
				String [] res  = (String [])FileDealer.loadData(fis);

				/*Initializing strings to store the numbers of the balls and the stars*/
				b = new String();
				s = new String();

				/*We "travel" the string array previously extracted from the file, 
				 * to extract the numbers of the balls and store it with some spaces betwen in
				 * a normal string. Then, we get the two last numbers corresponding the stars' numbers.
				 * We divide balls and stars in two different strings so we can put them in different
				 * textviews and then give them differente colors, to show some prettier output.*/
				int i = 0;
				for(i = 0; i<5;i++){
					b += res[i] + " ";
				}
				s += " " + res[i] ;
				s += " " + res[i+1] ;	
				balls.setText(b);
				stars.setText(s);
			}

			/* If the file doesn't exist, it means it's the first time we're running the application,
			 * so we have to load data from the file in the "raw" folder and then store it in a new
			 * file 'cause we can't modify the files in the raw folder. We do this 'cause er need to
			 * mantain a stat for the application, 'cause the divice isn't always connected to internet.*/
			else{				
				/*Initializing textviews*/
				balls = new TextView(this);
				balls = (TextView)findViewById(R.id.textViewResBalls);
				stars = new TextView(this);
				stars = (TextView)findViewById(R.id.textViewResStars);

				/*Loading data from the "raw file"*/
				InputStream is = getResources().openRawResource(R.raw.resultado);
				ObjectInputStream ois = new ObjectInputStream(is);
				String [] res = (String []) ois.readObject();
				ois.close();

				/*Saving data to another file*/
				String filename = result.getName();
				FileOutputStream fos = openFileOutput(filename, Context.MODE_PRIVATE);
				FileDealer.storeData(res, fos);

				/*We "travel" the string array previously extracted from the file, 
				 * to extract the numbers of the balls and store it with some spaces betwen in
				 * a normal string. Then, we get the two last numbers corresponding the stars' numbers.
				 * We divide balls and stars in two different strings so we can put them in different
				 * textviews and then give them differente colors, to show some prettier output.*/
				b = new String();
				s = new String();
				int i = 0;
				for(i = 0; i<5;i++){
					b += res[i] + " ";
				}
				s += " " + res[i] ;
				s += " " + res[i+1] ;	
				balls.setText(b);
				stars.setText(s);
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

		/* This "try block" works pretty the same as the previous "try block" that I explained previously.
		 * However, this block is to show the Date of the draw as a Title, and the previous 
		 * was for the result of the draw.*/
		try {
			File diaMesAno = getApplicationContext().getFileStreamPath("diaMesAno");
			if(diaMesAno.exists()){
				String filename = diaMesAno.getName();
				FileInputStream fis = openFileInput(filename);
				String dataDoResultado = (String)FileDealer.loadData(fis);
				tvTitle = new TextView(this);
				tvTitle = (TextView)findViewById(R.id.textViewResultado);
				tvTitle.setText("Resultado\n("+dataDoResultado+ "):");
			}
			else{
				InputStream is = getResources().openRawResource(R.raw.mesdiaano);
				ObjectInputStream ois = new ObjectInputStream(is);
				String dataDoResultado = (String) ois.readObject();
				ois.close();
				String filename = diaMesAno.getName();
				FileOutputStream fos = openFileOutput(filename, Context.MODE_PRIVATE);
				FileDealer.storeData(dataDoResultado, fos);
				tvTitle = new TextView(this);
				tvTitle = (TextView)findViewById(R.id.textViewResultado);
				tvTitle.setText("Resultado\n("+dataDoResultado+ "):");
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

		//Button to generate a pseudo-random key, so the user can use it in a bet
		this.gerarChave = (Button)this.findViewById(R.id.button1);
		//Initializing the textviews
		k1=new TextView(this);
		k1=(TextView)findViewById(R.id.textView1); 
		k2=new TextView(this);
		k2=(TextView)findViewById(R.id.textView2); 
		k3=new TextView(this);
		k3=(TextView)findViewById(R.id.textView3); 
		k4=new TextView(this);
		k4=(TextView)findViewById(R.id.textView4); 
		k5=new TextView(this);
		k5=(TextView)findViewById(R.id.textView5); 
		s1=new TextView(this);
		s1=(TextView)findViewById(R.id.textViewStar1); 
		s2=new TextView(this);
		s2=(TextView)findViewById(R.id.textViewStar2); 

		//Listener to the generate key button
		this.gerarChave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//Calling method "gerarChave" which returns the key in a ArrayList of Integers
				ArrayList <Integer> resultado = gerarChave();				
				//We then show the key generated in the layout, with the stars in a "gold color"
				k1.setText(resultado.get(0).toString());
				k2.setText(resultado.get(1).toString());
				k3.setText(resultado.get(2).toString());
				k4.setText(resultado.get(3).toString());
				k5.setText(resultado.get(4).toString());
				s1.setText(resultado.get(5).toString());
				s2.setText(resultado.get(6).toString());
			}
		});

		//A button to see the statistics
		this.estatisticas = (Button)this.findViewById(R.id.button2);
		//Also a listener to the button.
		this.estatisticas.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				/*With a click in this button we create a new Intent, which will lead us to a new
				 * "part" of the application, where will be shown the stats of each number, but
				 * this is no longer responsability of this class, as we can see, we call
				 * "setClass method that will take us to the class responsible to deal with
				 * the statistics.*/
				Intent intentStats = new Intent();
				intentStats.setClass(EuroMilhoesAppActivity.this, Statistics.class);
				startActivity(intentStats);
			}
		});

		//Button to update the data of the application.
		this.actualizar = (Button) this.findViewById(R.id.button3);
		actualizar.setOnClickListener(this);

		//A button to see the "About" section
		this.about = (Button)this.findViewById(R.id.button4);
		//Also a listener to the button.
		this.about.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				/*With a click in this button we create a new Intent, which will lead us to a new
				 * "part" of the application*/
				Intent aboutCPA = new Intent();
				aboutCPA.setClass(EuroMilhoesAppActivity.this,AboutCPA.class);
				aboutCPA.putExtra("extra_dev_name","Bruno Ferreira");
				startActivity(aboutCPA); 
			}
		});
	}

	/**
	 * Method to generate a new pseudo-random key consisting in 5 normal numbers and two stars in the last
	 * two positions.
	 * 
	 * @return an ArrayList of Integers containing the pseudo-random key generated in the method.
	 * */
	private ArrayList <Integer> gerarChave(){
		//I used an arrayList mostly 'cause it has a very usefull method, which is "contains"
		ArrayList <Integer> chave = new ArrayList <Integer> ();
		Random r = new Random();

		/* Generating numbers for normal balls */
		for(int i = 0; i<5; i++){
			/* we do plus one 'cause we tryin' to find a random 
			 * number between 0 and 49 (both inclusive) and
			 * what we want is a number between 1 and 50 (both inclusive)*/
			int n = r.nextInt(50)+1;

			/*If the number already exists, we do random until we get a number that doesn't
			 * exists in the array, so we can have no equal numbers in the final key*/
			while(chave.contains(n)){
				n = r.nextInt(50)+1;
			}
			chave.add(n);			
		}   	

		/* Generating two random numbers for the two stars */
		int estrela1 = r.nextInt(11)+1;
		int estrela2 = r.nextInt(11)+1;
		while(estrela1 == estrela2){
			estrela2 = r.nextInt(11)+1;
		}
		chave.add(estrela1);
		chave.add(estrela2);
		return chave;

	}

	/**
	 * Method to create a menu with a button with text and a icon
	 * where the user can update the data of EuroMillions draws.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	/**
	 * This method is called when the user clicks on "actualizar" button.
	 * A Progress Dialog will appear while the application contacts the website to extract
	 * the information to update the application. 
	 * A thread is created to extract that information. 
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.icontext:
			pd = ProgressDialog.show(this, "A actualizar...", "A actualizar os dados da aplicação.", true,
					false);
			Thread thread = new Thread(this);
			thread.start();

			break;
		case R.id.icontext2:
			/*With a click in this button we create a new Intent, which will lead us to a new
			 * "part" of the application*/
			Intent aboutCPA = new Intent();
			aboutCPA.setClass(EuroMilhoesAppActivity.this,AboutCPA.class);
			aboutCPA.putExtra("extra_dev_name","Bruno Ferreira");
			startActivity(aboutCPA); 
			break;
		}
		return true;
	}

	/**
	 * The thread will get data from the website and will create the strings to be shown in
	 * the TextViews. 
	 * We get the date of the draw and its result. We also get the statistics but we don't show them in the
	 * handler method cause we're not in the statistics layout at the moment, however, the files will be
	 * updated.
	 */
	@Override
	public void run() {
		String [] res = HTMLDealer.getResultado(); //getting tha latest result from the website
		dma = new String(); //initializing the String again
		dma = HTMLDealer.getDiaMesAno(); //getting the date of the result from the website
		TreeSet <Pair> ballStats= HTMLDealer.ballsStatistics(); //stats of normal balls
		TreeSet <Pair> starStats= HTMLDealer.starsStatistics(); //stats of star balls
		/* If there's no information returned from the previous HTML methods calls, 
		 * it means that (probably) the device is not connected to network...
		 */
		if(res != null && dma !=null && ballStats != null && starStats != null ){	
			try {

				/* In this "block" of code we get the two files that contains the statistics
				 * and we update them, but once we're not in the "statistics layout", we just
				 * save them in the correct files and we do nothing more.*/
				File bolasStats = getApplicationContext().getFileStreamPath("ballsStats");
				File estrelasStats = getApplicationContext().getFileStreamPath("starsStats");
				FileOutputStream fosBalls =openFileOutput(bolasStats.getName(), Context.MODE_PRIVATE);
				FileOutputStream fosStars = openFileOutput(estrelasStats.getName(), Context.MODE_PRIVATE);
				FileDealer.storeData(ballStats, fosBalls);
				FileDealer.storeData(starStats, fosStars);

				/* Then, we get (or create, whatever) the file to store the result and other file to store
				 * the date of the draw. We initialize the two strings, 'b' and 's', to store the 
				 * normal balls' numbers and the stars' numbers respectively, with some spaces
				 * between the numbers.*/
				File result = getApplicationContext().getFileStreamPath("result");
				String filename = result.getName();
				FileOutputStream fos = openFileOutput(filename, Context.MODE_PRIVATE);
				FileDealer.storeData(res, fos);//storing the result in the file
				b = new String();
				s = new String();
				int i = 0;
				for(i = 0; i<5;i++){
					b += res[i] + " "; //string for normal balls
				}
				/* and the string for the two stars.
				 * we put the stars in a different string to put them in a different color.*/
				s += " " + res[i] ;
				s += " " + res[i+1] ;

				/*The Date of the result, also obtained from the website must be stored
				 * in another file to be loaded in the future.*/
				File diaMesAno = getApplicationContext().getFileStreamPath("diaMesAno");
				String filename2 = diaMesAno.getName();//getting the name of the file
				FileOutputStream fos2;
				fos2 = openFileOutput(filename2, Context.MODE_PRIVATE);
				FileDealer.storeData(dma, fos2);//storing the Date in the file

				//we send an empty message with the number 0, the handler will know what to do...
				handler.sendEmptyMessage(0);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				//alert if an exception occurs. The app is then finished.
				showAlertForExceptions();
			}
		}else{
			/*if the device is not connected to internet, 
			 * we send an empty message with number 1 to handler,
			 * and he will know what to do.*/
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
				 * with the new information */
				pd.dismiss();

				//Setting the textviews with the strings...
				balls.setText(b);
				stars.setText(s);
				tvTitle.setText("Resultado\n("+dma+ "):");

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
		Toast.makeText(this, "Os dados foram actualizados.", Toast.LENGTH_LONG).show();
	}

	/**
	 * This method is called when the user clicks on "actualizar button".
	 * A Progress Dialog will appear while the application contacts the website to extract
	 * the information to update the application. 
	 * A thread is created to extract that information. 
	 */
	/* I had to do this in a different way that i did in the statistics.java 'cause
	 * in this class i have a button in the principal layout and another button in 
	 * the menu that do tha same thing, so this method is used for the button in the layout
	 * and the method "onOptionsItemSelected(MenuItem item)" is used for the menu button*/ 
	@Override
	public void onClick(View arg0) {
		pd = ProgressDialog.show(this, "A actualizar...", "A actualizar os dados da aplicação.", true,
				false);

		Thread thread = new Thread(this);
		thread.start();
	}
}