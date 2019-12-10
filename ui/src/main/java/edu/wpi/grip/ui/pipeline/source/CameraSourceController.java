package edu.wpi.grip.ui.pipeline.source;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.cscore.VideoMode;
import edu.wpi.cscore.VideoProperty;
import edu.wpi.grip.core.sources.CSWebcamFrameGrabber;
import edu.wpi.grip.core.sources.CameraSource;
import edu.wpi.grip.ui.components.ExceptionWitnessResponderButton;
import edu.wpi.grip.ui.components.StartStoppableButton;
import edu.wpi.grip.ui.pipeline.OutputSocketController;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;
import org.bytedeco.javacv.FrameGrabber;

/**
 * Provides controls for a {@link CameraSource}.
 */
public final class CameraSourceController extends SourceController<CameraSource> {

  private final StartStoppableButton.Factory startStoppableButtonFactory;

  @Inject
  CameraSourceController(
      final EventBus eventBus,
      final OutputSocketController.Factory outputSocketControllerFactory,
      final StartStoppableButton.Factory startStoppableButtonFactory,
      final ExceptionWitnessResponderButton.Factory exceptionWitnessResponderButtonFactory,
      @Assisted final CameraSource cameraSource) {
    super(eventBus, outputSocketControllerFactory, exceptionWitnessResponderButtonFactory,
        cameraSource);
    this.startStoppableButtonFactory = startStoppableButtonFactory;
  }

  @FXML
  @Override
  public void initialize() throws Exception {
    super.initialize();
    addControls(startStoppableButtonFactory.create(getSource()));
    // TODO: Only show setting manually
  }

  private static class VideoModeHolder {
    public VideoMode videoMode;
    @Override
    public String toString() {
      return "PixelFormat: " + videoMode.pixelFormat.name() + " Width: " + videoMode.width + " Height: " + videoMode.height + " Fps: " + videoMode.fps;
    }
  }

  @FXML
  @Override
  public void settings() {

    final Stage dialog = new Stage();
    dialog.initModality(Modality.NONE);
    //dialog.initOwner(primaryStage);
    VBox dialogVbox = new VBox(20);
    dialogVbox.getChildren().add(new Text("This is a Dialog"));

    FrameGrabber grabber = getSource().getFrameGrabber();
    if (grabber instanceof CSWebcamFrameGrabber) {
      CSWebcamFrameGrabber csGrabber = (CSWebcamFrameGrabber)grabber;
      UsbCamera camera = csGrabber.getCamera();
      VideoMode[] videoModes = camera.enumerateVideoModes();
      VideoModeHolder[] fixedModes = new VideoModeHolder[videoModes.length];
      for (int i = 0; i < fixedModes.length; i++) {
        fixedModes[i] = new VideoModeHolder();
        fixedModes[i].videoMode = videoModes[i];
      }
      ObservableList<VideoModeHolder> modeList = FXCollections.observableArrayList(fixedModes);
      ComboBox<VideoModeHolder> modeComboBox = new ComboBox<>(modeList);
      modeComboBox.getSelectionModel().selectFirst();
      dialogVbox.getChildren().add(modeComboBox);
      modeComboBox.setOnAction(e -> {
        VideoModeHolder holder = modeComboBox.getSelectionModel().getSelectedItem();
        System.out.println(holder);
        camera.setVideoMode(holder.videoMode);
      });


      VideoProperty[] properties = camera.enumerateProperties();

      // Handle Integer Properties
      for (VideoProperty property : properties) {
        if (property.isInteger() && !property.getName().startsWith("raw_")) {
          HBox hbox = new HBox(20);
          Label label = new Label(property.getName());
          hbox.getChildren().add(label);
          Slider slider = new Slider(property.getMin(), property.getMax(), property.get());

          Button defaultButton = new Button("Default");
          defaultButton.setOnAction(e -> {
            slider.valueProperty().set(property.getDefault());
          });
          hbox.getChildren().add(defaultButton);
          hbox.getChildren().add(slider);
          Label label2 = new Label(Double.toString(property.get()));
          hbox.getChildren().add(label2);
          slider.setBlockIncrement(1);
          slider.setMajorTickUnit(1);
          slider.setMinorTickCount(0);
          slider.setShowTickLabels(true);
          slider.setSnapToTicks(true);

          slider.valueProperty().addListener(e -> {
            label2.setText(Double.toString(slider.getValue()));
            property.set((int)slider.getValue());
          });

          dialogVbox.getChildren().add(hbox);

        }
      }


      // Handle String Properties
      for (VideoProperty property : properties) {
        if (property.isString() && !property.getName().startsWith("raw_")) {
          HBox hbox = new HBox(20);
          Label label = new Label(property.getName());
          hbox.getChildren().add(label);

          TextField input = new TextField(property.getString());

          hbox.getChildren().add(input);

          Button button = new Button("Set");
          button.setOnAction(e -> {
            property.setString(input.getText());
          });

          hbox.getChildren().add(button);

          dialogVbox.getChildren().add(hbox);

        }
      }

      // Handle Enum Properties
      for (VideoProperty property : properties) {
        if (property.isEnum() && !property.getName().startsWith("raw_")) {
          HBox hbox = new HBox(20);
          Label label = new Label(property.getName());
          hbox.getChildren().add(label);

          ObservableList<String> enumList = FXCollections.observableArrayList(
                  property.getChoices()
          );

          ComboBox<String> enumComboBox = new ComboBox<>(enumList);
          enumComboBox.getSelectionModel().selectFirst();

          enumComboBox.setOnAction(e -> {
            property.set(enumComboBox.getSelectionModel().getSelectedIndex());
          });
          hbox.getChildren().add(enumComboBox);

          dialogVbox.getChildren().add(hbox);

        }
      }
    }

    
    ScrollPane s1 = new ScrollPane();
    s1.setContent(dialogVbox);
    Scene dialogScene = new Scene(s1);

    dialog.setScene(dialogScene);
    dialog.show();
  }

  public interface Factory {
    CameraSourceController create(CameraSource cameraSource);
  }

}
