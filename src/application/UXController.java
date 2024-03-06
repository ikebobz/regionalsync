package application;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;



public class UXController {
	
public static int timer = 0;
	
List<Facility> facilities;
int initial = 0;
int facility = 0;
String host;
public static String serverState = "Server is offline";
	
@FXML
RadioButton rbtnInit,rbtnUpd;

@FXML
public Label description;

@FXML
ProgressIndicator progressBar;

@FXML
HBox piContainer;

@FXML
ChoiceBox facilityID;

@FXML
Button btnUpl,btnTstCon;

@FXML
private void initialize()
{
ToggleGroup tgroup = new ToggleGroup();
rbtnInit.setToggleGroup(tgroup);
rbtnUpd.setToggleGroup(tgroup);
FacilityDAO facilityDAO = new FacilityDAO();
facilities = facilityDAO.getFacilities();
int index = 0;
facilities.forEach(f -> {
	facilityID.getItems().add(f.getFacilityName());
	
});
this.piContainer.setVisible(false);
this.progressBar.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
this.rbtnUpd.setSelected(true);
this.readProperties();
if(initial == 0) this.rbtnInit.setDisable(true);
else this.rbtnInit.setDisable(false);
initChoiceBox();
this.btnUpl.setDisable(true);
this.testConnection();

}

@FXML
private void OnUploadButtonClicked()
{
	
	if(facility == 0)
	{
		Alert notice = new Alert(AlertType.WARNING);
		notice.setHeaderText("Please select a facility");
		notice.show();
		return;
	}
	Alert alert = new Alert(AlertType.CONFIRMATION);
	Button okButton = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
	okButton.addEventFilter(ActionEvent.ACTION, event -> {
		//piContainer.setVisible(true);
		
	     Synchronizer synchronizer = new Synchronizer(piContainer,description,facility);
 		 synchronizer.getDatabaseCredentials();
		 Thread thread = new Thread(synchronizer);
		 thread.start();
		 
	});
	alert.setHeaderText("Push Confirmation !!");
	alert.setContentText("Are you sure you want to proceed with the push ?");
	alert.show();
}
   private int getFacilityIdFromName(String facilityname)
   {
	   
	   Optional<Facility> facilityObject = facilities.stream().filter(facility -> facility.getFacilityName().equalsIgnoreCase(facilityname)).findFirst();
	   return facilityObject.isPresent() ? facilityObject.get().getFacilityId() : 0;
   }
   
   private void initChoiceBox()
   {
	   facilityID.setOnAction(ev -> {
		   String facilityname = facilityID.getSelectionModel().getSelectedItem().toString();
		   System.out.println(getFacilityIdFromName(facilityname));
		   this.facility = this.getFacilityIdFromName(facilityname);
	   });
   }
   
	private void readProperties()
	{
		try {
			try(InputStream is = new FileInputStream("application.properties"))
		    {
			Properties prop = new Properties();
			prop.load(is);
			initial = Integer.parseInt(prop.getProperty("initial"));
			host = prop.getProperty("host");
				    	
		    }
			
		}
			catch(Exception ex)
			{
				System.out.println(ex.getMessage());
			}
	}
@FXML
private void OnTestConnectButtonClicked()
{
this.testConnection();	
}
private static boolean isReachable(String host, int openPort, int timeOutMillis) {
    try {
        try (Socket soc = new Socket()) {
            soc.connect(new InetSocketAddress(host, openPort), timeOutMillis);
        }
        return true;
    } catch (IOException ex) {
        return false;
    }
}
private void testConnection()
{
	
	this.description.setText("Please wait while I test Server availabity........");
	this.piContainer.setVisible(true);
	Thread testThread = new Thread(new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(UXController.isReachable(host, 3389, 10000))
				UXController.serverState = "Server is Online";
			else UXController.serverState = "Server is Offline";
			 Platform.runLater(new Runnable() {

					@Override
					public void run() {
						Alert notice = new Alert(AlertType.INFORMATION);
						notice.setHeaderText(UXController.serverState);
						notice.show();
						if(UXController.serverState.equalsIgnoreCase("Server is online"))
							btnUpl.setDisable(false);
						else btnUpl.setDisable(true);
						    piContainer.setVisible(false);
						
					    
					}});
			
			
		}});
	
	testThread.start();

}
}
