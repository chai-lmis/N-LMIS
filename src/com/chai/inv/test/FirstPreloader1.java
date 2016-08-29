package com.chai.inv.test;
import javafx.application.Preloader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


public class FirstPreloader1 extends Preloader {
    ProgressBar bar;
    Stage stage;
 
    private Scene createPreloaderScene() {
    	ImageView imageView = new ImageView();
		Image image = new Image("/resources/icons/loader.gif");
		imageView.setImage(image);
       // bar = new ProgressBar();
        BorderPane p = new BorderPane();
        p.setCenter(imageView);
        return new Scene(p, 300, 150);        
    }
    
    @Override
	public void start(Stage stage) throws Exception {
        this.stage = stage;
        BorderPane p = new BorderPane();
        p.setStyle("-fx-background-color:transparent;");
        Label msg=new Label("Loading....");
        ImageView imageView = new ImageView();
		Image image = new Image("/resources/icons/loader.gif");
		imageView.setImage(image);
		p.setCenter(imageView);
		Scene scene=new Scene(p);
		stage.initStyle(StageStyle.UNDECORATED);
//		stage.setHeight(400);
//		stage.setWidth(1200);
		stage.setOpacity(0.6);
		stage.setMaximized(true);
        stage.setScene(scene);     
        stage.show();
    }
    
    @Override
    public void handleProgressNotification(ProgressNotification pn) {
        bar.setProgress(pn.getProgress());
    }
 
    @Override
    public void handleStateChangeNotification(StateChangeNotification evt) {
        if (evt.getType() == StateChangeNotification.Type.BEFORE_START) {
            stage.hide();
            System.out.println("loading stage Close");
        }
    }    
}
