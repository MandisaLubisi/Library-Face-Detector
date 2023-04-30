package pX;

//Imports
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.RenderedImage;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Base64;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Reflection;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * 
 * @author MT Lubisi
 *
 */
public class ClientPane extends GridPane {
	
	Socket s = null;
	//inputStreams
	InputStream is = null;
	BufferedReader br = null;
	//outputStreams
	OutputStream os = null;
	BufferedOutputStream bos = null;
	DataOutputStream dos = null;
	private String imageName ="";
	private String grayURL = "/api/GrayScale";
	private String fastURL = "/api/Fast";
	private ImageView imgViewOrg;
	private Button btnConnect;
	private Button btnGrayScale;
	private Button btnFast;
	private Button btnAttach;
	private Button btnDetect;
	private Text granted;
	private Text txtHeader;
	private Text specText;
	private ImageView imgViewGray;
	public ImageView imgViewFast;
	public ImageView imgViewDetect;
	private CascadeClassifier faceCascade;
	

	
	
  private void setupGUI() {

	  setHgap(10);
	  setVgap(10);
	  setPadding(new Insets(15, 15, 15, 15));
	  setStyle("-fx-background-color:black;-fx-border-width:15; -fx-font-weight:bold;"
			  + "-fx-border-color:gold;");
	  setAlignment(Pos.CENTER);
	  //Special FX
	  //Reflection
	  Reflection ref = new Reflection();
	  ref.setFraction(5);

	  //DropShadow
	  DropShadow ds = new DropShadow();
	  ds.setColor(Color.BLUEVIOLET);
	  ds.setOffsetX(3.0);
	  ds.setOffsetY(3.0);

	  txtHeader = new Text("4IR Library Face Detector");
	  txtHeader.setFont(Font.font("Times New Roman", 50));
	  txtHeader.setEffect(ref);
	  txtHeader.setEffect(ds);
	  
	  specText= new Text("Powered by Computer Science 2B");
	  specText.setFont(Font.font("Times New Roman", 30));
	  specText.setEffect(ref);
	  specText.setEffect(ds);
	
	  imgViewOrg = new ImageView();
	  btnConnect = new Button("Connect");
	  btnGrayScale = new
	  Button("Apply Grayscale");
	  btnAttach = new Button("Upload Facial Image");
	  btnFast = new Button("Apply Fast");
	  btnDetect = new Button("Detect Face");
	  granted = new Text("Access Granted!!");
	  granted.setFill(Color.GREEN);
	  granted.setFont(Font.font("Green",FontWeight.NORMAL, 30));
	  granted.setVisible(false);
	  add(granted, 0, 2);
	 
	 imgViewGray = new ImageView();
	 imgViewFast = new ImageView();
	 imgViewDetect = new ImageView();
	 add(imgViewOrg, 0,3);
	 imgViewGray.setVisible(false);
	 add(imgViewGray, 0,3);
	 add(imgViewFast, 0,3);
	 add(imgViewDetect, 0,3);
	 add(btnConnect,0,5);
	 add(btnGrayScale,1 ,5);
	 add(btnFast, 2, 5);
	 add(btnAttach, 0, 2);
	 add(btnDetect,3,5);
	 add(txtHeader, 0, 0);
	 add(specText, 0, 10);
	
	
}
  private void connect() {
	  
      try
		{
		  s = new Socket("localhost",5000);
		  JOptionPane.showMessageDialog(null,"Connection succesfully to port:5000 estalished\r\n" );
		  //bind streams
		  is = s.getInputStream();
		  br = new BufferedReader(new InputStreamReader(is));
		  os = s.getOutputStream();
		  bos = new BufferedOutputStream(os);
		  dos = new DataOutputStream(bos);
		  
		} catch(IOException e) {
			e.printStackTrace(); 
		}
	  
  }
  public ClientPane(Stage stage) {
	    //Set GUI for the application
		setupGUI();
		btnAttach.setOnAction(event->{
			
			// Load image for processing
			 FileChooser fc = new FileChooser();
			 fc.setTitle("Choose Image File");
			 fc.setInitialDirectory(new File("./data"));
			 File selectedfile = fc.showOpenDialog(stage);
			 imageName = selectedfile.getName();
			 Image readIMG = new
					 Image("file:data/"+imageName);
			 JOptionPane.showMessageDialog(null, imageName+"Successfully attached!");
			 imgViewOrg.setImage(readIMG);
			 btnAttach.setVisible(false);
			 
			 
			 
			
		});
		btnConnect.setOnAction((event)->{
		    //Connect to the Web API
			connect();
			btnConnect.setVisible(false);
				
		});
		btnGrayScale.setOnAction((event)->
		{
			String encodedFile = null;
			try {
				//DOS(BOS(OS))
				//Create a File handle
				File imageFile = new File("./data", imageName);
				//read the File into a FileInputStream
				FileInputStream fileInputStreamReader = new
						FileInputStream(imageFile);
				//Put the file contents into a byte[]
				byte[] bytes = new byte[(int)imageFile.length()];
				fileInputStreamReader.read(bytes);
				//Encode the bytes into a base64 format string
				encodedFile = new
						String(Base64.getEncoder().encodeToString(bytes));
				//get the bytes of this encoded string
				byte[] bytesToSend = encodedFile.getBytes();
				//Construct a POST HTTP REQUEST
				dos.write(("POST " + grayURL +" HTTP/1.1\r\n").getBytes());
				dos.write(("Content-Type: " +"application/text\r\n").getBytes());
				dos.write(("Content-Length: " + encodedFile.length()
				+"\r\n").getBytes());
				dos.write(("\r\n").getBytes());
				dos.write(bytesToSend);
				dos.write(("\r\n").getBytes());
				dos.flush();
				//txtArea.appendText("POST Request Sent\r\n");
				JOptionPane.showMessageDialog(null,"POST Request Sent\r\n" );
				//read text response
				String response = "";
				String line = "";
				while(!(line = br.readLine()).equals(""))
				{
					response += line +"\n";
				}
				System.out.println(response);
				String imgData = "";
				while((line = br.readLine())!=null)
				{
					imgData += line;
				}
				String base64Str =
						imgData.substring(imgData.indexOf('\'')+1,imgData.lastIndexOf('}')-1);
				byte[] decodedString = Base64.getDecoder().decode(base64Str);
				//Display the image
				Image grayImg = new Image(new ByteArrayInputStream(decodedString));
				//ImageIO.write((RenderedImage) grayImg, "JPG",new File("data","ImageGray.jpg"));
				
				imgViewOrg.setVisible(false);
				imgViewGray.setVisible(true);
				imgViewGray.setImage(grayImg);
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
			
			 btnGrayScale.setVisible(false);
		 }
		 
		);
		
		btnFast.setOnAction(e->{
			
			connect();
			String encodedFile2 = null;
			try {
				//DOS(BOS(OS))
				//Create a File handle
				File imageFile2 = new File("./data",imageName);
				//read the File into a FileInputStream
				FileInputStream fileInputStreamReader2 = new
						FileInputStream(imageFile2);
				//Put the file contents into a byte[]
				byte[] bytes2 = new byte[(int)imageFile2.length()];
				fileInputStreamReader2.read(bytes2);
				//Encode the bytes into a base64 format string
				encodedFile2 = new
						String(Base64.getEncoder().encodeToString(bytes2));
				//get the bytes of this encoded string
				byte[] bytesToSend2 = encodedFile2.getBytes();
				//Construct a POST HTTP REQUEST
				dos.write(("POST " + fastURL +" HTTP/1.1\r\n").getBytes());
				dos.write(("Content-Type: " +"application/text\r\n").getBytes());
				dos.write(("Content-Length: " + encodedFile2.length()
				+"\r\n").getBytes());
				dos.write(("\r\n").getBytes());
				dos.write(bytesToSend2);
				dos.write(("\r\n").getBytes());
				dos.flush();
				JOptionPane.showMessageDialog(null,"POST Request Sent\r\n" );
				///txtArea.appendText("POST Request Sent\r\n");
				//read text response
				String response2 = "";
				String line2 = "";
				while(!(line2 = br.readLine()).equals(""))
				{
					response2 += line2 +"\n";
				}
				System.out.println(response2);
				String imgData2 = "";
				while((line2 = br.readLine())!=null)
				{
					imgData2 += line2;
				}
				String base64Str2 =
						imgData2.substring(imgData2.indexOf('\'')+1,imgData2.lastIndexOf('}')-1);
				byte[] decodedString2 = Base64.getDecoder().decode(base64Str2);
				//Display the image
				Image fastImg = new Image(new ByteArrayInputStream(decodedString2));
				//ImageIO.write((RenderedImage) fastImg, "JPG",new File("data","ImageFast.jpg"));
				imgViewGray.setVisible(false);
				imgViewFast.setVisible(true);
				imgViewFast.setImage(fastImg);
			} 
			catch (IOException ex) {
				ex.printStackTrace();
			}finally
			{
			try {
			   dos.close();
			   s.close();}
			 catch (IOException io) {
					io.printStackTrace();
				}}
			 btnFast.setVisible(false);
			}
		
		);
		
		  btnDetect.setOnAction(e->{
			  
			  // load the native OpenCV library
			  System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
			  faceCascade = new CascadeClassifier();
			  faceCascade.load("resources/haarcascades/haarcascade_frontalface_alt.xml");
			  // Reading the input image
		       Mat image = Imgcodecs.imread("data/"+imageName);
		  
		        // Detecting faces
		       MatOfRect faceDetections = new MatOfRect();
		       faceCascade.detectMultiScale(image,
		                                      faceDetections);
		  
		        // Creating a rectangular box which represents for
		        // faces detected
		        for (Rect rect : faceDetections.toArray()) {
		            Imgproc.rectangle(
		                image, new Point(rect.x, rect.y),
		                new Point(rect.x + rect.width,
		                          rect.y + rect.height),
		                new Scalar(0, 255, 0));
		        }
		  
		        // Saving the output image
		   
		        Imgcodecs.imwrite("data/Output.jpg", image);
		        imgViewDetect.setImage(new Image("file:data/Output.jpg"));
		        granted.setVisible(true);
		  
		        // Display message for successful execution of
		        // program
		      
		        JOptionPane.showMessageDialog(null,"Face Detection Succesfull ,Thank you :)");
		        btnDetect.setVisible(false);
		        
			 
		  });
	}
  
	
  
  
 }

