package applet;



import helpsessions.DBHandler;
import helpsessions.DBHandler;
import helpsessions.MainPanel;
import helpsessions.MainPanel;
import java.lang.reflect.InvocationTargetException;
import javax.swing.JApplet;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

public class BookingApplet extends JApplet {
    DBHandler dbh;
    JProgressBar jpb;
    boolean done;
    public void init() {
        dbh = new DBHandler();
        try {
            
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run(){            
                    createGUI();
                }
                
                
            });
       
        } catch(InvocationTargetException | InterruptedException e){e.printStackTrace();}
      

    }
    
    
    public void createGUI(){

       Thread t = new Thread(dbh);
       done = false;
       t.start();
       
        MainPanel mp = new MainPanel(dbh);
        setContentPane(mp);
        
    }



}