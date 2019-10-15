package ttp.Utils;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import ttp.TTPInstance;
import ttp.TTPSolution;

public class drawTSP {
  public static void draw(TTPInstance ttpi, int[] tour, String filename) {
    try {
      double citySize = 10;
      int lineWidth = 2;
      double maxPixels=5000;
      
      double minX=Double.POSITIVE_INFINITY;
      double minY=Double.POSITIVE_INFINITY;
      double maxX=Double.NEGATIVE_INFINITY;
      double maxY=Double.NEGATIVE_INFINITY;
      for(int i=0; i<ttpi.numberOfNodes; i++){
    	  if(ttpi.nodes[i][1]>maxX)
    		  maxX=ttpi.nodes[i][1];
    	  
    	  if(ttpi.nodes[i][1]<minX)
    		  minX=ttpi.nodes[i][1];
    	  
    	  if(ttpi.nodes[i][2]>maxY)
    		  maxY=ttpi.nodes[i][2];
    	  
    	  if(ttpi.nodes[i][2]<minY)
    		  minY=ttpi.nodes[i][2];
      }
      double scale = maxPixels/maxX;
      maxX=maxX*scale;
      maxY=maxY*scale;
      minX=minX*scale;
      minY=minY*scale;
      double shuffleX=50-minX;
      double shuffleY=50-minY;
      
      //System.out.println("scale:"+scale+"\t minX: "+minX+"\t maxX:"+maxX+"\t minY:"+minY+"\t maxY:"+maxY+"\t shufX:"+shuffleX+"\t shufY:"+shuffleY);
      BufferedImage bi = new BufferedImage((int)(maxX+shuffleX+100), (int)(maxY+shuffleY+100), BufferedImage.TYPE_INT_ARGB);

      Graphics2D ig2 = bi.createGraphics();

      Ellipse2D.Double city = new Ellipse2D.Double();
      ig2.setStroke(new BasicStroke(lineWidth));
      city.width = citySize;
      city.height = citySize;
      
	  double x = shuffleX+(ttpi.nodes[tour[0]][1]*scale);
	  double y = shuffleY+(ttpi.nodes[tour[0]][2]*scale);
      city.x = x-citySize/2;
      city.y = y-citySize/2;
      ig2.setPaint(Color.red);
      ig2.draw(city);
      ig2.fill(city);
      
      double prevX=x;
      double prevY=y;
      ig2.setPaint(Color.black);
      for(int i=1; i<ttpi.numberOfNodes; i++){
    	  x = shuffleX+(ttpi.nodes[tour[i]][1]*scale);
    	  y = shuffleY+(ttpi.nodes[tour[i]][2]*scale);
          city.x = x-citySize/2;
          city.y = y-citySize/2;
          ig2.draw(city);
          ig2.fill(city);
          Line2D.Double line = new Line2D.Double(prevX,prevY,x,y);
          ig2.draw(line);
          prevX=x;
          prevY=y;
      }
      
      ImageIO.write(bi, "PNG", new File(filename));
      
    } catch (IOException ie) {
      ie.printStackTrace();
    }
  }
  
  public static void draw(TTPInstance ttpi, TTPSolution ttps, String filename) {
	    try {
	      double citySize = 10;
	      int lineWidth = 2;
	      double maxPixels=5000;
	      
	      int[] tour=ttps.tspTour;
	      int[] pp=ttps.packingPlan;
	      ttpi.evaluate(ttps, false);
	      
	      int itemsPerCity = ttpi.numberOfItems / (ttpi.numberOfNodes-1);
	      double colourFactor=0.36/itemsPerCity;
	      
	      double minX=Double.POSITIVE_INFINITY;
	      double minY=Double.POSITIVE_INFINITY;
	      double maxX=Double.NEGATIVE_INFINITY;
	      double maxY=Double.NEGATIVE_INFINITY;
	      for(int i=0; i<ttpi.numberOfNodes; i++){
	    	  if(ttpi.nodes[i][1]>maxX)
	    		  maxX=ttpi.nodes[i][1];
	    	  
	    	  if(ttpi.nodes[i][1]<minX)
	    		  minX=ttpi.nodes[i][1];
	    	  
	    	  if(ttpi.nodes[i][2]>maxY)
	    		  maxY=ttpi.nodes[i][2];
	    	  
	    	  if(ttpi.nodes[i][2]<minY)
	    		  minY=ttpi.nodes[i][2];
	      }
	      double scale = maxPixels/maxX;
	      maxX=maxX*scale;
	      maxY=maxY*scale;
	      minX=minX*scale;
	      minY=minY*scale;
	      double shuffleX=50-minX;
	      double shuffleY=50-minY;
	      
	      //System.out.println("scale:"+scale+"\t minX: "+minX+"\t maxX:"+maxX+"\t minY:"+minY+"\t maxY:"+maxY+"\t shufX:"+shuffleX+"\t shufY:"+shuffleY);
	      BufferedImage bi = new BufferedImage((int)(maxX+shuffleX+100), (int)(maxY+shuffleY+100+30), BufferedImage.TYPE_INT_ARGB);

	      Graphics2D ig2 = bi.createGraphics();

	      Ellipse2D.Double city = new Ellipse2D.Double();
	      ig2.setStroke(new BasicStroke(lineWidth));
	      city.width = citySize;
	      city.height = citySize;
	      
		  double x = shuffleX+(ttpi.nodes[tour[0]][1]*scale);
		  double y = shuffleY+(ttpi.nodes[tour[0]][2]*scale);
	      city.x = x-citySize/2;
	      city.y = y-citySize/2;
	      ig2.setPaint(Color.red);
	      ig2.draw(city);
	      ig2.fill(city);
	      
	      double prevX=x;
	      double prevY=y;
	      ig2.setPaint(Color.black);
	      
	      for(int i=1; i<ttpi.numberOfNodes; i++){
	    	  int itemsInCity=0;
	    	  for(int j=0;j<itemsPerCity;j++){
	    		  if(pp[((i-1)*itemsPerCity)+j]==1)
	    			  itemsInCity++;
	    	  }
	    	  ig2.setPaint(Color.getHSBColor((float)(0.36-(itemsInCity*colourFactor)), (float)1.0, (float)0.75));
	    	  x = shuffleX+(ttpi.nodes[tour[i]][1]*scale);
	    	  y = shuffleY+(ttpi.nodes[tour[i]][2]*scale);
	          city.x = x-citySize/2;
	          city.y = y-citySize/2;
	          ig2.draw(city);
	          ig2.fill(city);
	          ig2.setPaint(Color.black);
	          Line2D.Double line = new Line2D.Double(prevX,prevY,x,y);
	          ig2.draw(line);
	          prevX=x;
	          prevY=y;
	      }
	      
	      

	      ig2.setFont(new Font(null, Font.PLAIN, 38));
	      ig2.drawString( ttpi.problemName, 20, (int) (maxY+shuffleY+100) );

	      ig2.drawString( "OB: ", 300, (int) (maxY+shuffleY+100) );
	      ig2.drawString(String.valueOf(ttps.objectiveScore), 370, (int) (maxY+shuffleY+100) );

	      ig2.drawString( "Leftover KC: ", 800, (int) (maxY+shuffleY+100) );
	      ig2.drawString(String.valueOf(ttps.finalCapacityFree), 1025, (int) (maxY+shuffleY+100) );

	      ig2.drawString( "TotW: ", 1250, (int) (maxY+shuffleY+100) );
	      ig2.drawString(String.valueOf(ttps.finalWeight), 1375, (int) (maxY+shuffleY+100) );
	      
	      ig2.drawString( "TotP: ", 1625, (int) (maxY+shuffleY+100) );
	      ig2.drawString(String.valueOf(ttps.finalProfit), 1725, (int) (maxY+shuffleY+100) );

	      File theImgFile = new File(filename);

	      // if the directory does not exist, create it
	      /*if (!theImgFile.exists()) {
	        try{
	        	theImgFile.mkdir();
	         } catch(Exception e){
	            System.out.println("Exception Writing out image file!");
	         }        
	      }*/
	      ImageIO.write(bi, "PNG", theImgFile);
	      
	    } catch (IOException ie) {
	      ie.printStackTrace();
	    }
	  }
}
