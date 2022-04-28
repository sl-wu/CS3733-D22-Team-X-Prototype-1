package edu.wpi.cs3733.D22.teamX.controllers;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.util.Timer;
import java.util.TimerTask;

import com.sun.javafx.font.directwrite.RECT;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import nu.pattern.OpenCV;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;
import org.opencv.videoio.VideoCapture;

import javax.swing.*;

public class FaceDetectionController {
  @FXML private Button start_btn, backToLoginScreen;
  @FXML private ImageView currentFrame;
  @FXML private AnchorPane rootElement;
  @FXML private CheckBox haarClassifier, lbpClassifier;
  private Timer timer;

  private VideoCapture capture = new VideoCapture();
  private boolean cameraActive;
  private CascadeClassifier faceCascade;
  private int absoluteFaceSize;
  private Image camStream;

  protected void init(){
    this.capture = new VideoCapture();
    this.faceCascade = new CascadeClassifier();
    this.absoluteFaceSize = 0;
  }
  @FXML
  protected void startCamera(ActionEvent event) {
    // System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    OpenCV.loadLocally();
    // check: the main class is accessible?
   if (!this.cameraActive) {
     this.haarClassifier.setDisable(true);
     this.lbpClassifier.setDisable(true);
     // start the video capture
     this.capture.open(0);
       // get the ImageView object for showing the video stream
       final ImageView frameView = currentFrame;
       // check if the capture stream is opened
       if (!this.capture.isOpened()) {
         this.cameraActive = true;

         // grab a frame every 33 ms (30 frames/sec)
         TimerTask frameGrabber =
                 new TimerTask() {
                   @Override
                   public void run() {
                     camStream = grabFrame();
                     Platform.runLater(
                             new Runnable() {
                               @Override
                               public void run() {
                                 currentFrame.setImage(camStream);
                                 currentFrame.setFitWidth(600);
                                 currentFrame.setPreserveRatio(true);
                               }
                             });
                   }
                 };
         this.timer = new Timer();
         // set the timer scheduling, this allow you to perform frameGrabber every 33ms;
         this.timer.schedule(frameGrabber, 0, 33);
         this.start_btn.setText("Stop Camera");
       }
       else {
         this.cameraActive = false;
         this.start_btn.setText("Start Camera");
         //enable setting checkboxes
         this.haarClassifier.setDisable(false);
         this.lbpClassifier.setDisable(false);

         // stop the timer
         if (this.timer != null) {
           this.timer.cancel();
           this.timer = null;
         }
         // release the camera
         this.capture.release();
         // clear the image container
         currentFrame.setImage(null);
       }
   }
  }

  private Image grabFrame() {
    // init
    Image imageToShow = null;
    Mat frame = new Mat();
    // check if the capture is open
    if (this.capture.isOpened()) {
      try {
        // read the current frame
        this.capture.read(frame);
        // if the frame is not empty, process it
        if (!frame.empty()) {
          //face detection
          this.detectAndDisplay(frame);
          // convert the image to gray scale
          // Imgproc.cvtColor(frame, frame, Imgproc.COLOR_BGR2GRAY);
          // convert the Mat object (OpenCV) to Image (JavaFX)
          imageToShow = mat2Image(frame);
        }
      } catch (Exception e) {
        // log the error
        System.err.println("ERROR: " + e.getMessage());
        e.printStackTrace();
      }
    }
    return imageToShow;
  }

  /**
   * Perform face detection and show a rectangle around the detected face.
   *
   * @param frame
   *            the current frame
   */
  private void detectAndDisplay(Mat frame)
  {
    // init
    MatOfRect faces = new MatOfRect();
    Mat grayFrame = new Mat();

    // convert the frame in gray scale
    Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);
    // equalize the frame histogram to improve the result
    Imgproc.equalizeHist(grayFrame, grayFrame);

    // compute minimum face size (20% of the frame height)
    if (this.absoluteFaceSize == 0)
    {
      int height = grayFrame.rows();
      if (Math.round(height * 0.2f) > 0)
      {
        this.absoluteFaceSize = Math.round(height * 0.2f);
      }
    }

    // detect faces
    this.faceCascade.detectMultiScale(grayFrame, faces, 1.1, 2, 0 | Objdetect.CASCADE_SCALE_IMAGE, new Size(
            this.absoluteFaceSize, this.absoluteFaceSize), new Size());

    // each rectangle in faces is a face
    Rect[] facesArray = faces.toArray();
    for (int i = 0; i < facesArray.length; i++)
      Core.rectangle(frame, facesArray[i].tl(), facesArray[i].br(), new Scalar(0, 255, 0, 255), 3);

  }

  /**
   * When the Haar checkbox is selected, deselect the other one and load the
   * proper XML classifier
   *
   */
  @FXML
  protected void haarSelected()
  {
    // check whether the lpb checkbox is selected and deselect it
    if (this.lbpClassifier.isSelected())
      this.lbpClassifier.setSelected(false);

    this.checkboxSelection("resources/haarcascades/haarcascade_frontalface_alt.xml");
  }

  /**
   *
   When the LBP checkbox is selected, deselect the other one and load the
   * proper XML classifier
   */
  @FXML
  protected void lbpSelected()
  {
    // check whether the haar checkbox is selected and deselect it
    if (this.haarClassifier.isSelected())
      this.haarClassifier.setSelected(false);

    this.checkboxSelection("resources/lbpcascades/lbpcascade_frontalface.xml");
  }

  /**
   * Common operation for both checkbox selections
   *
   * @param classifierPath
   *            the absolute path where the XML file representing a training
   *            set for a classifier is present
   */
  private void checkboxSelection(String... classifierPath)
  {
    // load the classifier(s)
    for (String xmlClassifier : classifierPath)
    {
      this.faceCascade.load(xmlClassifier);
    }

    // now the capture can start
    this.start_btn.setDisable(false);
  }

  /**
   * Convert a Mat object (OpenCV) in the corresponding Image for JavaFX
   *
   * @param frame
   *            the {@link Mat} representing the current frame
   * @return the {@link Image} to show
   */
  private Image mat2Image(Mat frame) {
    // create a temporary buffer
    MatOfByte buffer = new MatOfByte();
    // encode the frame in the buffer
    Imgcodecs Highgui = new Imgcodecs();
    Highgui.imencode(".png", frame, buffer);
    // build and return an Image created from the image encoded in the buffer
    return new Image(new ByteArrayInputStream(buffer.toArray()));
  }

  public void setRootElement(AnchorPane root) {
    this.rootElement = root;
  }
}