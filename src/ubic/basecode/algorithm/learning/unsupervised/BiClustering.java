package ubic.basecode.algorithm.learning.unsupervised;

//Refactoring the codes from Biclustering.java, copyright by Yizong Cheng, 2002
//Citation: Cheng Y and Church GM, "Biclustering of expression data",
//Proceedings of ISMB 2000, 93-103.

import java.util.Random;

public class BiClustering {
    private short[][] matrix = null;
    private int numberOfRows = 0;
    private int numberOfColumns = 0;
    private Random rand = new Random();
    private int maxScore = 1200;
    private int minHeight = 5;
    private int minWidth = 10;
    private int batchThreshold = 100;
    private boolean[] remainingR = null;
    private boolean[] remainingC = null;
    private double[] rowMean = null;
    private double[] columnMean = null;
    private double[] rowScore = null;
    private double[] columnScore = null;
    private double mean = 0;
    private int smWidth = 0;
    private int smHeight = 0;
    private double HScore = 0;
    private final static int UniformDistibutionA = 800;
    private final static int UniformDistibutionB = 1600;
    private final static double ScalingRaio = 100.0;
    public BiClustering(double[][] dataMatrix, int maxScore, int batchThreshold){
    	this.maxScore = maxScore;
    	this.batchThreshold = batchThreshold;
    	init(dataMatrix);
    }
	public BiClustering(double[][] dataMatrix){
    	init(dataMatrix);
	}
	private void init(double[][] dataMatrix){
    	numberOfRows = dataMatrix.length;
    	if(numberOfRows > 0){
    		numberOfColumns = dataMatrix[0].length;
    	}
    	matrix = new short[numberOfRows][numberOfColumns];
    	for(int i = 0; i <numberOfRows; i++){
    		for(int j = 0; j < numberOfColumns; j++){
    			if(Double.isNaN(dataMatrix[i][j]))
    				matrix[i][j] = (short)(rand.nextInt(UniformDistibutionB) - UniformDistibutionA);
    			else
    				matrix[i][j] = (short)(dataMatrix[i][j] * ScalingRaio);
    		}
    	}
        remainingR = new boolean[numberOfRows];
        remainingC = new boolean[numberOfColumns];
        rowMean = new double[numberOfRows];
        columnMean = new double[numberOfColumns];
        rowScore = new double[numberOfRows];
        columnScore = new double[numberOfColumns];
    }
    private void scoring(){
        mean = 0;
        for (int j = 0; j < numberOfColumns; j++) if (remainingC[j]) 
           columnMean[j] = 0;
        for (int i = 0; i < numberOfRows; i++) if (remainingR[i]){
           rowMean[i] = 0;
           for (int j = 0; j < numberOfColumns; j++) if (remainingC[j]){
              rowMean[i] += matrix[i][j];
              columnMean[j] += matrix[i][j];
           }
           mean += rowMean[i];     
           rowMean[i] /= smWidth;
        }
        for (int j = 0; j < numberOfColumns; j++) if (remainingC[j])
           columnMean[j] /= smHeight;
        mean /= smWidth * smHeight;
        HScore = 0;
        for (int j = 0; j < numberOfColumns; j++) if (remainingC[j])
           columnScore[j] = 0;
        for (int i = 0; i < numberOfRows; i++) if (remainingR[i]){
           rowScore[i] = 0;
           for (int j = 0; j < numberOfColumns; j++) if (remainingC[j]) {
              double r = matrix[i][j] - rowMean[i] - columnMean[j] + mean;
              r = r * r;
              rowScore[i] += r;
              columnScore[j] += r;
           }
           HScore += rowScore[i];
           rowScore[i] /= smWidth;   
        }
        HScore /= smWidth * smHeight;
        for (int j = 0; j < numberOfColumns; j++) if (remainingC[j]) 
           columnScore[j] /= smHeight;
     }  

     public double getBicluster(int rowIndex[], int colIndex[]){ 
        for (int i = 0; i < numberOfRows; i++) remainingR[i] = true;
        for (int j = 0; j < numberOfColumns; j++) remainingC[j] = true;
        smWidth = numberOfColumns;
        smHeight = numberOfRows;
        scoring();
        int index = 0;
        while ((HScore > maxScore) && (index > -1)){
           if (smHeight > batchThreshold){
              for (int i = 0; i < numberOfRows; i++)
                 if (remainingR[i] && (rowScore[i] > HScore)){
                    remainingR[i] = false;
                    smHeight--;
                 }
           }else{
              double ms = 0;
              index = -1;
              boolean row = true;
              if (smHeight > minHeight){
                 for (int i = 0; i < numberOfRows; i++)    
                    if (remainingR[i] && (rowScore[i] > ms)){
                       ms = rowScore[i];
                       index = i;
                    }
              }
              if (smWidth > minWidth){
                 for (int i = 0; i < numberOfColumns; i++)    
                    if (remainingC[i] && (columnScore[i] > ms)){
                       ms = columnScore[i];
                       index = i;
                       row = false;
                    }
              }
              if (index > -1)
                 if (row){
                    remainingR[index] = false;
                    smHeight--;
                 }else{
                    remainingC[index] = false;
                    smWidth--;
                 }
           }
           scoring();
         }
        int numberofRowIndex = 0;
        int numberofColIndex = 0;
        index = 0;
        for (int i = 0; i < numberOfRows; i++) if (remainingR[i]) numberofRowIndex++;
        rowIndex = new int[numberofRowIndex];
        for (int i = 0; i < numberOfRows; i++) if (remainingR[i]) rowIndex[index++] = i;
        index = 0;
        for (int j = 0; j < numberOfColumns; j++) if (remainingC[j]) numberofColIndex++;
        colIndex = new int[numberofColIndex];
        for (int j = 0; j < numberOfColumns; j++) if (remainingC[j]) colIndex[index++] = j;

        for (int i = 0; i < numberOfRows; i++) if (remainingR[i])
            for (int j = 0; j < numberOfColumns; j++) if (remainingC[j])
               matrix[i][j] = (short)(rand.nextInt(UniformDistibutionB) - UniformDistibutionA);
        
        return HScore;
     }
}
