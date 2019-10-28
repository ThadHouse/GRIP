package edu.wpi.grip.ui.pipeline.source;

import edu.wpi.grip.core.sources.CameraSource;
import edu.wpi.grip.ui.components.ExceptionWitnessResponderButton;
import edu.wpi.grip.ui.components.StartStoppableButton;
import edu.wpi.grip.ui.pipeline.OutputSocketController;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.stage.Popup;
import javafx.stage.Stage;

/**
 * Provides controls for a {@link CameraSource}.
 */
public final class CameraSourceController extends SourceController<CameraSource> {

  private final StartStoppableButton.Factory startStoppableButtonFactory;
  private final SettingsController.Factory settingsFactory;

  @Inject
  CameraSourceController(
      final EventBus eventBus,
      final OutputSocketController.Factory outputSocketControllerFactory,
      final StartStoppableButton.Factory startStoppableButtonFactory,
      final SettingsController.Factory settingsFactory,
      final ExceptionWitnessResponderButton.Factory exceptionWitnessResponderButtonFactory,
      @Assisted final CameraSource cameraSource) {
    super(eventBus, outputSocketControllerFactory, exceptionWitnessResponderButtonFactory,
        cameraSource);
    this.settingsFactory = settingsFactory;
    this.startStoppableButtonFactory = startStoppableButtonFactory;
  }

  @FXML
  @Override
  public void initialize() throws Exception {
    super.initialize();
    addControls(startStoppableButtonFactory.create(getSource()));
    // TODO: Only show setting manually
  }

  @FXML
  @Override
  public void settings() {

    SettingsController controller = settingsFactory.create(this.getSource());
    Stage dialogStage = new Stage();
    dialogStage.setScene(controller.getRoot().getScene());
    dialogStage.show();
//    Popup popup = new Popup();
//    Label label = new Label("This is a popup!");
//    popup.getContent().add(label);
//    label.setMinWidth(80);
//    label.setMinHeight(50);
//    Stage stage = new Stage();
//    stage.setScene(popup);
//    stage.show();
//    popup.show(stage);
  }

  public interface Factory {
    CameraSourceController create(CameraSource cameraSource);
  }

}
