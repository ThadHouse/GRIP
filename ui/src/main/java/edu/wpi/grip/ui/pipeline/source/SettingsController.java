package edu.wpi.grip.ui.pipeline.source;

import com.google.inject.assistedinject.Assisted;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.cscore.VideoMode;
import edu.wpi.grip.core.sources.CSWebcamFrameGrabber;
import edu.wpi.grip.core.sources.CameraSource;
import edu.wpi.grip.ui.Controller;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import edu.wpi.grip.ui.annotations.ParametrizedController;
import com.google.common.eventbus.EventBus;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import org.bytedeco.javacv.FrameGrabber;

import javax.inject.Inject;

@ParametrizedController(url = "Settings.fxml")
public class SettingsController implements Controller {

    private final EventBus eventBus;
    private final CameraSource cameraSource;

    @FXML
    private VBox root;

    @FXML
    private HBox controls;

    @FXML
    private ComboBox modeSelector;

    @Inject
    SettingsController(final EventBus eventBus, @Assisted final CameraSource cameraSource) {
        this.eventBus = eventBus;
        this.cameraSource = cameraSource;
    }

    @Override
    public VBox getRoot() {
        return root;
    }

    @FXML
    private void initialize() {
        FrameGrabber grabber = cameraSource.getFrameGrabber();
        if (grabber instanceof CSWebcamFrameGrabber) {
            System.out.println("InstanceOf Correct!");
            CSWebcamFrameGrabber csGrabber = (CSWebcamFrameGrabber)grabber;
            UsbCamera camera = csGrabber.getCamera();
            VideoMode[] videoModes = camera.enumerateVideoModes();
            modeSelector.getItems().addAll(videoModes);
            System.out.println("Combobox Added!");
        }
    }

    public interface Factory {
        SettingsController create(CameraSource cameraSource);
    }
}
