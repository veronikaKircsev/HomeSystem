package at.fhv.sysarch.lab2;

import akka.actor.typed.ActorSystem;
import at.fhv.sysarch.lab2.homeautomation.HomeAutomationController;

import java.io.File;
import java.util.regex.Pattern;

public class HomeAutomationSystem {

    public static void main(String[] args) {

        // at start delete all old receipt files
        String filePath = "receipt\\d+\\.txt$";
        Pattern pattern = Pattern.compile(filePath);
        File directory = new File("./");
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.exists() && pattern.matcher(file.getName()).matches()) {
                if (file.delete()) {
                    System.out.println("Die Datei wurde erfolgreich gelöscht: " + filePath);
                } else {
                    System.out.println("Die Datei konnte nicht gelöscht werden: " + filePath);
                }
            }
        }


        ActorSystem<Void> home = ActorSystem.create(HomeAutomationController.create(), "HomeAutomation");
    }


}
