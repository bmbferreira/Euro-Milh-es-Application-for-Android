package euro.milhoes.app;


import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.TreeSet;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
/**
 * HTMLDealer.java
 * Purpose: Get data from the website and return it
 *
 * @author Bruno Ferreira
 * @version 1.10 8/10/11
 */
//date format: mm/dd/yy
public abstract class HTMLDealer {
	/**
	 * Method to get the date of the last draw from the website "http://www.euro-millions.com/results.asp".
	 * This site can be changed in the future, if the site changes his layout (Html) or disappear.
	 * @returns a String containing the date of the last draw
	 * */
	public static String getDiaMesAno(){
		URL url;//the Url to get information		
		try {
			/* This site can be changed in the future, but then we have to take a look at
			 * JSoup API and try to get data from another website. */
			url = new URL("http://www.euro-millions.com/results.asp");
			Document doc = Jsoup.parse(url, 3000);
			/*Selecting the first table with the class "tableR" from the website refered before*/
			Element table = doc.select("table[class=tableR]").first();
			/*Selecting the first column of the table*/
			Element tr = table.select("tr").first();
			/*Extracting the content from the first line of the first column to a string */
			String th = tr.select("th").first().text();
			/*Translating the date extracted before*/
			String dateTranslated = translateDate(th);
			return dateTranslated; //return the string with the dat of the draw	
			/*If an exception occurs we return null and we'll deal with it in the class that
			 *calls this method.*/
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * Method to get the resutl of the last draw from the website "http://www.euro-millions.com/results.asp".
	 * This site can be changed in the future, if the site changes his layout (Html) or disappear.
	 * @returns an array of strings containing the numbers of the normal balls and the stars of the last draw.
	 * */
	public static String [] getResultado(){
		URL url;//the Url to get information		
		String [] resultado = new String [7] ;//a sting array, 5 normal balls + 2 stars
		try {
			/* This site can be changed in the future, but then we have to take a look at
			 * JSoup API and try to get data from another website. */
			url = new URL("http://www.euro-millions.com/results.asp");
			Document doc = Jsoup.parse(url, 3000);
			/*Selecting the first table with the class "tableR" from the website refered before*/
			Element table = doc.select("table[class=tableR]").first();
			/*Selecting the first table inside the table selected before*/
			Element table2 = table.select("table").first();
			/*Getting an iterator to "travel" the line with the class "euro-ball". We don't need to select
			 * the column in here 'cause we have just one column.*/
			Iterator<Element> ite = table2.select("td[class=euro-ball]").iterator();
			/*Getting an iterator to "travel" the line with the class "euro-lucky-star".*/
			Iterator<Element> ite2 = table2.select("td[class=euro-lucky-star]").iterator();
			int c = 0;//int to set the position to put a number in the String array
			/*In this first while we are putting number by number the normal balls in the array,
			 * so we use the iterator to "travel" the line with the class "euro-ball"*/
			while (ite.hasNext()){
				resultado[c] = ite.next().text();
				c++;
			}
			/*In this second while we are filling the array with the stars.*/
			while (ite2.hasNext()){
				resultado[c] = ite2.next().text();
				c++;
			}			
			return resultado; //return an array of strings with the numbers of the draw
			/*If an exception occurs we return null and we'll deal with it in the class that
			 *calls this method.*/
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * Method to get the statistics of the numbers of normal balls in every draws of euro millions
	 * from the website "http://www.euro-millions.com/statisticss.asp".
	 * This site can be changed in the future, if the site changes his layout (Html) or disappear.
	 * @returns a TreeSet of Pairs containing a pair of number of the ball -> number of times it appeard.
	 * */
	/* I used a TreeSet cause it sorts the information as I want to. A Pair is a object implemented by
	 * me which consists in two numbers, in this case a number is the number of the ball and the other number
	 * is the number of times that ball appeard.*/
	public static TreeSet <Pair> ballsStatistics(){
		URL url;//the Url to get information
		TreeSet <Pair> statisticsBalls = new TreeSet <Pair>();//a TreeSet of Pairs
		try {
			/* This site can be changed in the future, but then we have to take a look at
			 * JSoup API and try to get data from another website. */
			url = new URL("http://www.euro-millions.com/statistics.asp");
			Document doc = Jsoup.parse(url, 3000);
			/*Select the first table with the class "statistics", in the site refered before*/
			Element table = doc.select("table[class=statistics]").first();
			/*Getting an iterator to "travel" all the lines which have the class "ball".*/
			Iterator<Element> ite = table.select("td[class=ball]").iterator();
			/*Getting an iterator to "travel" all the lines which have the segment of text
			 * "nowrap=nowrap"*/
			Iterator<Element> ite2 = table.select("td[nowrap=nowrap]").iterator();
			/*The HTML code that we're dealing with in the page statistics looks like this:
			 * <td class="ball" nowrap="nowrap">1</td>
			 * <td nowrap="nowrap">= 42</td>
			 * <td class="ball" nowrap="nowrap">11</td>
			 * <td nowrap="nowrap">= 48</td>
			 * <td class="ball" nowrap="nowrap">21</td>
			 * <td nowrap="nowrap">= 49</td>
			 * 			(......)
			 * So, we first have the number, and in the next <td...> we have the number of times
			 * that number appeard. So, in the next while we "travel" the two iterators created before,
			 * with the first we extract the number, and with the second we extract the number of times it 
			 * appeard. Pretty simple.
			 * */
			while(ite.hasNext() && ite2.hasNext()){
				ite2.next();//I do next in here 'cause it "catches" the line of the number...
				String numAndFreq = ite.next().text() + ite2.next().text();//string used to create a pair
				Pair p = createPair(numAndFreq); //creating a pair
				statisticsBalls.add(p);// adding the pair to the TreeSet			
			}
			return statisticsBalls;//returning the TreeSet
			/*If an exception occurs we return null and we'll deal with it in the class that
			 *calls this method.*/
		}catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Method to get the statistics of the numbers of star balls in every draws of euro millions
	 * from the website "http://www.euro-millions.com/statisticss.asp".
	 * This site can be changed in the future, if the site changes his layout (Html) or disappear.
	 * @returns a TreeSet of Pairs containing a pair of number of the star -> number of times it appeard.
	 * */
	/* I used a TreeSet cause it sorts the information as I want to. A Pair is a object implemented by
	 * me which consists in two numbers, in this case a number is the number of the ball/star
	 * and the other number is the number of times that ball appeard.*/
	public static TreeSet <Pair> starsStatistics(){
		URL url;
		try {
			/* This site can be changed in the future, but then we have to take a look at
			 * JSoup API and try to get data from another website. */
			url = new URL("http://www.euro-millions.com/statistics.asp");
			Document doc = Jsoup.parse(url, 3000);
			/*Select the second table with the class "statistics", in the site refered before*/
			Element table2 = doc.select("table[class=statistics]").get(1);
			/*Getting an iterator to "travel" all the lines which have the class "lucky-star".*/
			Iterator<Element> iteStars = table2.select("td[class=lucky-star]").iterator();
			/*Getting an iterator to "travel" all the lines which have the segment of text
			 * "nowrap=nowrap"*/
			Iterator<Element> ite2Stars = table2.select("td[nowrap=nowrap]").iterator();
			TreeSet <Pair> statisticsStars = new TreeSet <Pair>();//a TreeSet of Pairs
			//This cycle is the same in the method before, for the normal balls
			while(iteStars.hasNext() && ite2Stars.hasNext()){
				ite2Stars.next();
				String numAndFreq = iteStars.next().text() + ite2Stars.next().text();
				Pair p = createPair(numAndFreq);
				statisticsStars.add(p);			
			}
			return statisticsStars;//returning the TreeSet
			/*If an exception occurs we return null and we'll deal with it in the class that
			 *calls this method.*/
		}catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Method to create a Pair given a String, containing something like number= frequency
	 * @returns a new Pair
	 * */
	private static Pair createPair(String numEFreq){
		/* Splitting = and a space we get two numbers, corresponfing 
		 * to a number and the number of times and the number of times that number
		 * appeard.*/ 
		String [] aux = numEFreq.split("= "); 
		/*Parsing numbers from Strings to Integers, so we can create a Pair with them*/
		Integer n = Integer.parseInt(aux[0]);// the number
		Integer f = Integer.parseInt(aux[1]);// and the number of times a number appeard
		Pair pair = new Pair(n.intValue(),f.intValue());//creating a new pair using the two integers		
		return pair;//returning the new pair
	}

	/**
	 * Method to translate some words in the Date from english to portuguese.
	 * @param s - a string with the format "dayOfTheWeek month day year" in english
	 * @return a string with the format "day ofTheWeek day month year" in portuguese
	 */
	private static String translateDate(String s){
		/*We should receive a String like this "Tuesday August 9th 2011"*/
		String [] parts = s.split(" ");//splitting the spaces we get a array with 4 strings in it
		String translated = "";//string translated to return
		if(parts.length == 4){//if the array is length 4, then it's good

			/*I just paid attention to tuesday and friday cause there's when the draws happens*/
			if(parts[0].compareToIgnoreCase("tuesday") == 0){
				translated += "Terça-Feira ";
			}else if(parts[0].compareToIgnoreCase("friday") == 0){
				translated += "Sexta-Feira ";
			}

			/* Here I ignore the th, st, nd and rd cause we don't use that in portuguese.
			 * I paid special attention cause the day of the month can only have 2 digits or 1 digit, 
			 * so i got two ifs that check wheter the second "slot" of the array has 3 digits (number + th/rd/nd/st)
			 * or if it has 4 digits (two number + th/rd/nd/st, and then i do a substring to "extract" just
			 * the number of the day, and "delete" the words next to them.*/
			if(parts[2].contains("th")
					||parts[2].contains("st") 
					||parts[2].contains("nd")
					||parts[2].contains("rd")){
				if(parts[2].length() == 3){
					translated += parts[2].substring(0,1);
				}else 
					if(parts[2].length() == 4){
						translated += parts[2].substring(0,2);
					}
			}		

			/*Here I translate the months, so i have twelve if's to do it*/
			if(parts[1].compareToIgnoreCase("january") == 0){
				translated += " Janeiro ";
			}else if(parts[1].compareToIgnoreCase("february") == 0){
				translated += " Fevereiro ";
			}else if(parts[1].compareToIgnoreCase("march") == 0){
				translated += " Março ";
			}else if(parts[1].compareToIgnoreCase("april") == 0){
				translated += " Abril ";
			}else if(parts[1].compareToIgnoreCase("may") == 0){
				translated += " Maio ";
			}else if(parts[1].compareToIgnoreCase("june") == 0){
				translated += " Junho ";
			}else if(parts[1].compareToIgnoreCase("july") == 0){
				translated += " Julho ";
			}else if(parts[1].compareToIgnoreCase("august") == 0){
				translated += " Agosto ";
			}else if(parts[1].compareToIgnoreCase("september") == 0){
				translated += " Setembro ";
			}else if(parts[1].compareToIgnoreCase("october") == 0){
				translated += " Outubro ";
			}else if(parts[1].compareToIgnoreCase("november") == 0){
				translated += " Novembro ";
			}else if(parts[1].compareToIgnoreCase("december") == 0){
				translated += " Dezembro ";
			}

			/*And in the end, I add the year*/
			translated += parts[3];

			/*And the string is translated and ready to be returned*/
			return translated;
		}else{/*If the string isn't legth 4, then we have some problem and we return null*/
			return null;
		}
	}
}
