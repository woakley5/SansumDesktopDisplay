package SansumDesktopDisplay;

import com.backendless.Backendless;
import com.sun.javafx.application.PlatformImpl;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("Display.fxml"));
        primaryStage.setTitle("Main Display");
        primaryStage.setScene(new Scene(root, 1024, 768));
        primaryStage.show();
        primaryStage.setFullScreen(true);
    }

    @Override
    public void stop(){
        PlatformImpl.tkExit();
        System.exit(0);
    }


    public static void main(String[] args) {
        Backendless.initApp("A1E62F8F-86D6-C2D9-FFAA-E16DFCFAFC00", "4C0FE2F2-CE43-FD58-FF5C-A108BFE42B00");
        launch(args);
    }
}
