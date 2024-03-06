package application;
	
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.fxml.FXMLLoader;


public class Main extends Application {
	
	private static int flag;
	@Override
	public void start(Stage primaryStage) {
		if(flag == 1)
		{
			this.startResolver(primaryStage);
			return;
		}
		try {
			StackPane root = (StackPane)FXMLLoader.load(getClass().getResource("UX.fxml"));
			Scene scene = new Scene(root,700,200);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setTitle("Regional Server Sync v1.0");
			primaryStage.getIcons().add(new Image("file:synchronize.png"));
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) {
		if(args.length > 0 && args[0].equals("1")) flag = 1;
		else flag = 0;
		launch(args);
		
	}
	public void startResolver(Stage primaryStage)
	{
		try {
			StackPane root = (StackPane)FXMLLoader.load(getClass().getResource("resolver.fxml"));
			Scene scene = new Scene(root,700,700);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setTitle("Resolver v1.0");
			primaryStage.getIcons().add(new Image("file:resolver.jpg"));
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}	
	}
}
