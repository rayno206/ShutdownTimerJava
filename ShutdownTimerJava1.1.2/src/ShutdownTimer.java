/*
 * Author: Patrick Mai
 * Email: rayno206@gmail.com
 * GitHub: https://github.com/rayno206
*/

import java.awt.Dimension;
import java.awt.Toolkit;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.function.UnaryOperator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.Reflection;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import javafx.util.converter.IntegerStringConverter;

public class ShutdownTimer extends Application{

		//Stages and Scenes
		private static Stage window;
		private static Scene timerScene;
		private static Text title;

		//Buttons, TextFields, Spinners, Labels for timerScene
		private static Button btnStart, btnCancel;
		private static Label author, hourLabel, minuteLabel, secondLabel, clLabel2, clLabel3;
		private static Tooltip authorTooltip;
		//private static TextField txthour, txtminute, txtsecond;
		private Spinner spHour, spMinute, spSecond;
		private ProgressBar bar;
		private ProgressIndicator pi;
		private ComboBox comboBox;
		private static ObservableList<String> options = FXCollections.observableArrayList("Timing Shutdown","Timing Restart","Timing Sleep");

		//Layout
		public static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		private static VBox vboxTimer;
		private static HBox hboxTitle, hboxAuthor;
		private static BorderPane borderTimer;
		private static GridPane gridTimer, gridBtn;

		//variables
		private long timeLeft = 0;
		private TimeCountdown tcd;
		private String pTypes = "";
		private Task copyWorker;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		window = primaryStage;

		//scenes
		SceneTimer();
		SceneGUI();
		window.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				System.out.println("App Stopped and Exited");
				Platform.exit();
                System.exit(0);
			}
		});
		window.setTitle("Shutdown Timer");
		window.getIcons().add(new Image("ShutdownIcon.png"));
		window.setScene(timerScene);
		window.setResizable(false);
		window.show();
	}

	public void SceneGUI() {
		//double width = (screenSize.getWidth()*0.989);
		//double height = (screenSize.getHeight()*0.925);
		borderTimer = new BorderPane();
		borderTimer.setId("border");
		borderTimer.setPadding(new Insets(10, 50, 90, 50));
		borderTimer.setCenter(vboxTimer);

		timerScene = new Scene(borderTimer);
		timerScene.getStylesheets().add(getClass().getClassLoader().getResource("style.css").toExternalForm());
	}

	public void SceneTimer() {
		gridTimer = new GridPane();
		//gridTimer.setPadding(new Insets(0, 0, 25, 0));
		gridTimer.setHgap(5);
		gridTimer.setVgap(5);
		gridTimer.setAlignment(Pos.CENTER);

		gridBtn = new GridPane();
		//gridBtn.setPadding(new Insets(140, 20, 25, 20));
		gridBtn.getStylesheets().add(getClass().getResource("striped-progress.css").toExternalForm());
		gridBtn.setHgap(5);
		gridBtn.setVgap(5);
		gridBtn.setAlignment(Pos.CENTER);

		vboxTimer = new VBox();
		vboxTimer.setAlignment(Pos.CENTER);
		hboxAuthor = new HBox();
		hboxAuthor.setAlignment(Pos.CENTER_RIGHT);
		//Reflection for text
		Reflection r = new Reflection();
		r.setFraction(0.7f);
		gridBtn.setEffect(r);

		title = new Text("Shutdown Timer");
		author = new Label("Author");
		authorTooltip = new Tooltip();
		authorTooltip.setText("designed by Patrick Mai");
		author.setTooltip(authorTooltip);

		hourLabel = new Label("HH");
		minuteLabel = new Label("MM");
		secondLabel = new Label("SS");
		clLabel2 = new Label(" : ");
		clLabel3 = new Label(" : ");
		SpinnerTimer();
		TimeProgressBar();
		pi = new ProgressIndicator(0);
		spMinute.getEditor().setText(60+"");
		btnStart = new Button("Start");
		btnStart.setPrefWidth(70);
		btnCancel = new Button("Cancel");
		btnCancel.setPrefWidth(70);
		btnCancel.setDisable(true);
		btnCancel.setStyle("-fx-opacity: 0.7; -fx-text-fill: white");

		comboBox = new ComboBox();
		comboBox.setItems(options);
		comboBox.getSelectionModel().selectFirst();
		comboBox.setPrefWidth(210);

		gridTimer.add(hourLabel, 0, 0);
		gridTimer.add(minuteLabel, 2, 0);
		gridTimer.add(secondLabel, 4, 0);
		gridTimer.add(clLabel2, 1, 1);
		gridTimer.add(clLabel3, 3, 1);
		gridTimer.add(spHour, 0, 1);
		gridTimer.add(spMinute, 2, 1);
		gridTimer.add(spSecond, 4, 1);
		gridBtn.add(gridTimer, 0, 0);
		gridBtn.add(comboBox, 0, 1);
		gridBtn.add(bar, 0, 2);
		gridBtn.add(pi, 1, 2);
		gridBtn.add(btnStart, 1, 0);
		gridBtn.add(btnCancel, 1, 1);

		gridBtn.setId("gridPane");
		btnStart.setId("btnStart");
		btnCancel.setId("btnCancel");
		title.setId("title");
		author.setId("author");
		authorTooltip.setId("authorTooltip");
		hourLabel.setId("lBel");
		minuteLabel.setId("lBel");
		secondLabel.setId("lBel");
		clLabel2.setId("lBel");
		clLabel3.setId("lBel");
		/*
		spHour.setId("spinner");
		spMinute.setId("spinner");
		spSecond.setId("spinner");
		comboBox.setId("spinner");
		*/

		hboxAuthor.getChildren().addAll(author);
		vboxTimer.getChildren().addAll(title, hboxAuthor, gridBtn);

		btnStart.setOnAction(e -> {
			//get values from spinners, comboBoxes
			timeLeft = (Long.parseLong(spHour.getEditor().getText())*3600) + (Long.parseLong(spMinute.getEditor().getText()) * 60) + (Long.parseLong(spSecond.getEditor().getText()));
			pTypes = comboBox.getValue().toString();
			// disable/enable spinners, comboBoxes and buttons
			spHour.setDisable(true);
			spMinute.setDisable(true);
			spSecond.setDisable(true);
			comboBox.setDisable(true);
			btnStart.setDisable(true);
			btnCancel.setDisable(false);
			// change the text color and opacity of the spinners, comboBoxes and buttons
			spHour.setStyle("-fx-opacity: 1; -fx-text-fill: black;-fx-background-color: white");
			spMinute.setStyle("-fx-opacity: 1; -fx-text-fill: black;-fx-background-color: white");
			spSecond.setStyle("-fx-opacity: 1; -fx-text-fill: black;-fx-background-color: white");
			comboBox.setStyle("-fx-opacity: 1; -fx-text-fill: black;-fx-background-color: white");
			btnStart.setStyle("-fx-opacity: 0.7; -fx-text-fill: white");
			btnCancel.setStyle("-fx-opacity: 1");
			// ProgressBar - reset the progress of the bar and pi
			bar.setProgress(0);
			pi.setProgress(0);
			// actions
			tcd = new TimeCountdown(spHour, spMinute, spSecond, pTypes, timeLeft, bar, pi);
			tcd.start();
		});

		btnCancel.setOnAction(e -> {
			// disable/enable spinners, comboBoxes and buttons
			spHour.setDisable(false);
			spMinute.setDisable(false);
			spSecond.setDisable(false);
			comboBox.setDisable(false);
			btnStart.setDisable(false);
			btnCancel.setDisable(true);
			// reset the values of spinners and comboBoxes back to their default values
			spHour.getEditor().setText("0");
			spMinute.getEditor().setText("60");
			spSecond.getEditor().setText("0");
			comboBox.getSelectionModel().selectFirst();
			// change the text color and opacity of the spinners, comboBoxes and buttons
			btnStart.setStyle("-fx-opacity: 1");
			btnCancel.setStyle("-fx-opacity: 0.7; -fx-text-fill: white");
			// progressBar
			bar.setProgress(0);
			pi.setProgress(0);
			// actions
			tcd.stopMe();
			System.out.println("Shutdown Cancelled");
		});
	}

	public void TimeProgressBar() {
		bar = new ProgressBar(0);
		bar.setPrefWidth(210);
		int maximumStatus = 12;
	    IntegerProperty statusCountProperty = new SimpleIntegerProperty(1);
	    Timeline timelineBar = new Timeline(new KeyFrame(Duration.millis(500), new KeyValue(statusCountProperty, maximumStatus)));
	    timelineBar.setCycleCount(Timeline.INDEFINITE);
	    timelineBar.play();
	    statusCountProperty.addListener((ov, PrevStatus, NewStatus) -> {
	        bar.pseudoClassStateChanged(PseudoClass.getPseudoClass("status" + PrevStatus.intValue()), false);
	        bar.pseudoClassStateChanged(PseudoClass.getPseudoClass("status" + NewStatus.intValue()), true);
	    });
	}

	public void SpinnerTimer() {
		spHour = new Spinner();
		spMinute = new Spinner();
		spSecond = new Spinner();
		NumberFormat format = NumberFormat.getIntegerInstance();
        UnaryOperator<TextFormatter.Change> filter = c -> {
            if (c.isContentChange()) {
                ParsePosition parsePosition = new ParsePosition(0);
                // NumberFormat evaluates the beginning of the text
                format.parse(c.getControlNewText(), parsePosition);
                if (parsePosition.getIndex() == 0 ||
                        parsePosition.getIndex() < c.getControlNewText().length()) {
                    // reject parsing the complete text failed
                    return null;
                }
            }
            return c;
        };

        TextFormatter<Integer> spHourFormatter = new TextFormatter<Integer>(new IntegerStringConverter(), 0, filter);
        TextFormatter<Integer> spMinuteFormatter = new TextFormatter<Integer>(new IntegerStringConverter(), 60, filter);
        TextFormatter<Integer> spSecondFormatter = new TextFormatter<Integer>(new IntegerStringConverter(), 0, filter);

        //Hour
        spHour.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10000));
        spHour.setEditable(true);
        spHour.getEditor().setTextFormatter(spHourFormatter);
        spHour.setPrefWidth(55);
        //Minute
        spMinute.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10000));
        spMinute.setEditable(true);
        spMinute.getEditor().setTextFormatter(spMinuteFormatter);
        spMinute.setPrefWidth(55);
        //Second
        spSecond.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10000));
        spSecond.setEditable(true);
        spSecond.getEditor().setTextFormatter(spSecondFormatter);
        spSecond.setPrefWidth(55);
	}
}
