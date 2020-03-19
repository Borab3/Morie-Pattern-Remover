package morie;
import org.opencv.core.*;
import org.opencv.imgcodecs.*;
import org.opencv.imgproc.*;


public class moireRun {

	public static void main(String[] args) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME); //loads the core library. This contains Mat methods
		morie.doMoire(); //runs the script in moire.java
	}
}
