package morie;
import java.awt.*;
import java.awt.List;
import java.util.*;
import org.opencv.core.*;
import org.opencv.imgcodecs.*;
import org.opencv.imgproc.*;


public class morie {
	public static java.util.List<Mat> planes = new ArrayList<>(); //this holds the real part of the dft
	public static Mat complexI = new Mat(); //this holds the complex numbers of the dft
	public static Mat dftDim(Mat image)
	{
		System.out.println("Finding the optimal dft image size");
		Mat padded = new Mat();
		int pixelRows = Core.getOptimalDFTSize(image.rows()); //sets a variable to be the optimum size for dft
		int pixelCols = Core.getOptimalDFTSize(image.cols()); //sets a variable to be the optimum size for dft
		System.out.println("Optimal dft size found");
		Core.copyMakeBorder(image, padded, 0, pixelRows - image.rows(), 0, pixelCols - image.cols(), Core.BORDER_CONSTANT, Scalar.all(0)); //modifies the image so that had the optimum length and width for dft
		return padded;
	}
	public static Mat magnitudeFind(Mat complexI) {
		System.out.println("Finding magnitude");
		java.util.List<Mat> planesList = new ArrayList<>();
		Mat mag = new Mat();
		Core.split(complexI, planesList); //splits the image channels into and array of mats and a mat
		Core.magnitude(planesList.get(0), planesList.get(1), mag); //finds the magnitudes of the array;
		return mag;
	}
	public static void doMoire() {
		System.out.println("Starting");
		Mat image= Imgcodecs.imread("C:\\Users\\Borab\\Documents\\Morie\\moire2.jpg", Imgcodecs.IMREAD_GRAYSCALE); //loads the image and converts it to grayscale
		System.out.println("Image loaded");
		Mat padded = dftDim(image);
		padded.convertTo(padded, CvType.CV_32F); //converts the Mat to hold 32 bit signed floats
		planes.add(padded);
		planes.add(Mat.zeros(padded.size(), CvType.CV_32F));
		Core.merge(planes, complexI);  //merges the arrays so that, while they hold different values, they both are used to create a single image
		System.out.println("Mats merged");
		Core.dft(complexI, complexI); //find the dft of the image
		System.out.println("Dft computed");
		Mat magnitude = magnitudeFind(complexI);
		System.out.println("Magnitudes found");
		int size = (int) (magnitude.total()*magnitude.channels()); //calculates the size of the mat magnitude
		float[] array = new float[size]; //initializes a new array for sorting
		for(int i = 0; i < magnitude.cols(); i++) { //populates array with the values of magnitude
			for (int j = 0; j < magnitude.rows(); j++) { //iterates through magnitude
				array[i*magnitude.rows() + j] = (float) (magnitude.get(j, i)[0]); //adds the relevant values in magnitude to array (with casting)
			}
		}
		Arrays.sort(array); //sorts the array
		int percentage = (int) array[(int) ((array.length-1)*0.8)]; //finds the value at the nth percentile
		//Imgproc.threshold(magnitude, magnitude, percentage, 2, Imgproc.THRESH_BINARY_INV); //changes the top 20% of values to 0
		//System.out.println("Magnitudes thresholded");
		for(int i = 0; i < magnitude.cols(); i++) { //iterates through the cols in magnitude
			for (int j = 0; j < magnitude.rows(); j++) { //iterates through the rows in magnitude
				float[] ar0 = {0,0};
				float[] ar1 = {1,1};
				float[] arr = new float[2]; 
				if(magnitude.get(j, i)[0] >= percentage) {  //changes the top n% of values to 0
					magnitude.put(j, i, ar0);
				} else { //and the rest to 1
					magnitude.put(j, i, ar1);
				}
				arr[0] = (float) (magnitude.get(j, i)[0]*complexI.get(j, i)[0]); //multiplies the value in magnitude by the value in the corresponding index in complexI
				arr[1] = (float) (magnitude.get(j, i)[0]*complexI.get(j, i)[1]); //magnitude holds 0s or 1s only because of the thresholding
				complexI.put(j, i, arr); //the values multiplied by one will remain the same while the values multipled by 0 will become 0
			}
		}
		Core.idft(complexI, complexI); //inverses the transform
		System.out.println("Inverse DFT computed");
		Mat restore = new Mat();
		Core.split(complexI, planes); //splits the array into a mat and an array of mats
		Core.normalize(planes.get(0), restore, 0, 255, Core.NORM_MINMAX); //normalizes the array so that it can be viewed
		restore.convertTo(restore, CvType.CV_8U); //converted to a byte image
		System.out.println("Image processed");
		Imgcodecs.imwrite("C:\\Users\\Borab\\Documents\\Morie\\moireComplete2.jpg", restore); //saves image location
	}
}