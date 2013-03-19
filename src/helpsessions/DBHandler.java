package helpsessions;

import applet.BookingApplet;
import javax.mail.PasswordAuthentication;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.DataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.naming.Context;
import org.joda.time.LocalDate;

public class DBHandler implements Runnable {
    Context ctx;
    Statement statement;
    Connection conn;    //does declaring this here keep it open through all methods that use it? 
    ArrayList<Tutor> roster;     
    ArrayList<Tutor> workingList;    

    public DBHandler() {

    roster = new ArrayList<Tutor>();

     }
     public void run() {
     
     String userName = "*******";
     String password = "*******";

     String url = "jdbc:mysql://74.53.183.225:3306/laneycc_tutors";
     String qry = "select * FROM avail";

       try { 
            Class.forName("com.mysql.jdbc.Driver").newInstance();            
            conn = DriverManager.getConnection(url, userName, password);
            statement = conn.createStatement();
            ResultSet rs = statement.executeQuery(qry);
            
           // could/should I use a prepared statement here? 
           while (rs.next()) {
               Tutor t = new Tutor();
               t.setTutorID(Integer.parseInt(rs.getString("tutorID")));
               t.setName(rs.getString("name"));
               t.setEmail(rs.getString("email"));
               t.setMonStart(rs.getTime("monStart"));
               t.setMonEnd(rs.getTime("monEnd"));
               t.setTueStart(rs.getTime("tueStart"));
               t.setTueEnd(rs.getTime("tueEnd"));
               t.setWedStart(rs.getTime("wedStart"));
               t.setWedEnd(rs.getTime("wedEnd"));
               t.setThuStart(rs.getTime("thuStart"));
               t.setThuEnd(rs.getTime("thuEnd"));
               String specialties = (rs.getString("specialties"));
               t.specialties = new ArrayList<String>();
               for (String s : specialties.split(",")) {
                   t.specialties.add(s);
               }
               roster.add(t);
           }

        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DBHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(DBHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(DBHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(DBHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    //I should probably make each of these methods their own (inner-class) runnable, right?
    public ArrayList<Time[]> getTimes(String spec, String day){
        ArrayList<Time[]> unorderedList = new  ArrayList<Time[]>();
        ArrayList<Time[]> toReturn = new  ArrayList<Time[]>();

        //Find the tutors with the requested specialty and put their availibilites in a list
        for (Tutor t : roster){
            if (t.specialties.contains(spec)) {
                Time startTime = null;
                Time endTime = null;
                try {
                    startTime = (Time) t.getClass().getDeclaredField(day + "Start").get(t);
                    endTime = (Time) t.getClass().getDeclaredField(day + "End").get(t);
                } catch (IllegalArgumentException ex) {
                    Logger.getLogger(DBHandler.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalAccessException ex) {
                    Logger.getLogger(DBHandler.class.getName()).log(Level.SEVERE, null, ex);
                } catch (NoSuchFieldException ex) {
                    Logger.getLogger(DBHandler.class.getName()).log(Level.SEVERE, null, ex);
                } catch (SecurityException ex) {
                    Logger.getLogger(DBHandler.class.getName()).log(Level.SEVERE, null, ex);
                }

                Time[] toPut = {startTime, endTime};
                unorderedList.add(toPut);
            }
        }
        
        //remove any null time spans 
        for (int curr = 0; curr < unorderedList.size(); curr++) {
            if (unorderedList.get(curr)[0] == null) {
                unorderedList.remove(curr);
            }
        }
        
        //order the spans by their start times (would it be better/how would it differ to use Collections.sort()?)
        int size = unorderedList.size();
        for (int curr = 0; curr < size; curr++) {
            Time[] lowest = null;
            for (int incurr = 0; incurr < unorderedList.size(); incurr++) {
                lowest = unorderedList.get(0);
                if (unorderedList.get(incurr)[0].before(lowest[0])) {
                    lowest = unorderedList.get(incurr);
                }
            }
            unorderedList.remove(lowest);
            toReturn.add(lowest);
        }
        //concatenate contiguous timespans and remove redundant ones
        for (int curr = 1; curr < toReturn.size(); ) {   //would it be better to use an iterator for this?
            Time lastStart = toReturn.get(curr - 1)[0];
            Time lastEnd = toReturn.get(curr - 1)[1];
            Time currStart = toReturn.get(curr)[0];
            Time currEnd = toReturn.get(curr)[1];

      // If this span ends before or at the same time as the last one, it is redundant, so remove it (we know it starts after the last one does because they're ordered)
            if (!currEnd.after(lastEnd)) {
                toReturn.remove(curr);
            }
        //if this span starts during the last one and ends later than it, combine them and use the later end time
            else if (!currStart.after(lastEnd) && !currEnd.before(lastEnd)) {
                toReturn.get(curr - 1)[1] = currEnd;
                toReturn.remove(curr);
            } 
        //otherwise, the span is not contiguous with the last one, so leave it separate and move on
        //we don't have to worry about a span having to look more than one span back because they are in order
            else {
                curr++;               
        //this is the only time curr gets incremented because in other cases something is deleted, so curr points to something new
                
            }
        }
        return toReturn;
    }

    public Date setAppt(String day, Time time, String spec){
        ArrayList<Tutor> withSpec = new ArrayList<Tutor>();
    
        workingList = new ArrayList<Tutor>();
        
        //get a list of tutors with the requested specialty 
        for (Tutor t : roster) {
            if (t.specialties.contains(spec)) {
                withSpec.add(t);
            }
        }
        
        for (Tutor t : withSpec) {
            try {
                Time availStart = (Time) t.getClass().getDeclaredField(day + "Start").get(t);
                Time availEnd = (Time) t.getClass().getDeclaredField(day + "End").get(t);

                //if the requested time is within the tutor's availability, add them to the list 
                if (availStart != null && !time.before(availStart) && !time.after(availEnd)) {
                    workingList.add(t);     //would it be better to use a single list?
                }
            } catch (NoSuchFieldException ex) {Logger.getLogger(DBHandler.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SecurityException ex) {Logger.getLogger(DBHandler.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {Logger.getLogger(DBHandler.class.getName()).log(Level.SEVERE, null, ex);} }
        
       //figure out the actual date being requested
        LocalDate ld = new LocalDate();
        
        int dow = 9;
        if (day.matches("mon")) {
            dow = 1;
        } else if (day.matches("tue")) {
            dow = 2;
        } else if (day.matches("wed")) {
            dow = 3;
        } else if (day.matches("thu")) {
            dow = 4;
        }
        
        LocalDate target = null;
        
        //if the day of week requested is after today, schedule it this week 
        if (ld.getDayOfWeek() < dow) {
            target = ld.dayOfWeek().setCopy(dow);       
        //if the day of week requested already came this week (or is today) schedule it next week
        } else {
            target = ld.dayOfWeek().setCopy(dow).plusWeeks(1);
        }
        
        Date toReturn = new java.sql.Date(target.toDate().getTime());
        
        
     //Remove tutors who are already booked at that time.
        String sql = "SELECT time FROM `appointments`"
                + "WHERE tutorID = ? AND date = ?";
        try {
            Iterator it = workingList.iterator();
            while (it.hasNext()) {
                Tutor t = (Tutor) it.next();     //is this avoidable? 

                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, t.getTutorID());
                ps.setDate(2, toReturn);
                ResultSet rs = ps.executeQuery();
                
                inner:
                while (rs.next()) {
                    Time apptTime = rs.getTime("time");
                    System.out.println(t.getName() + "RS HAS : " + apptTime);
                    //15 minutes before and after 
                    long beforeLong = apptTime.getTime() - 900000;
                    long afterLong = apptTime.getTime() + 900000;
                    Time beforeTime = new Time(beforeLong);
                    Time afterTime = new Time(afterLong);   
                    
                    //if the requested time is within 30 minutes of a scheduled appointment, remove that tutor
                    if (!time.before(beforeTime) && !time.after(afterTime)) {
                        System.out.println("removed" + t.getName());
                        it.remove();
                        break inner;
                    }
                }
            }
            } catch(SQLException e) {e.printStackTrace();}
        
        return toReturn;  //return the actual date, so the GUI can display it 
    }

    
    boolean bookAppt(Time time, Date date, String tuteeName, String tuteeEmail){       

        //if all tutors are booked, tell the GUI to return a failed message / ask for another time
        if (workingList.isEmpty()){
            return false;
        }
        
        //if multiple tutors are available, pick a random one.
        else {
        Random rand = new Random();
        int picked = rand.nextInt(workingList.size());
        Tutor t = workingList.get(picked);
            //store the appointment in the database 
            String sql = "INSERT INTO `appointments`"
                    + "(`tutorID`, `tuteeName`, `tuteeEmail`, `time`, `date`)"
                    + ("VALUES(?, ?, ?, ?, ?)");
            try {
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, t.getTutorID());
                ps.setString(2, tuteeName);
                ps.setString(3, tuteeEmail);
                ps.setTime(4, time);
                ps.setDate(5, date);
                ps.executeUpdate();
           } catch (SQLException ex) {Logger.getLogger(DBHandler.class.getName()).log(Level.SEVERE, null, ex);}


         EmailSender es = new EmailSender(workingList.get(picked), date, time, tuteeName, tuteeEmail);
         Thread th = new Thread(es);
         th.start();
         
        }
        return true;
    }
    


    class EmailSender implements Runnable {
        Tutor t;
        Date date;
        Time time; 
        String tuteeName;
        String tuteeEmail;
        
        final String username = "laneycodingclub@gmail.com";
	final String password = "2013Java";
        
        public EmailSender(Tutor t, Date date, Time time, String tuteeName, String tuteeEmail){
            this.t = t;
            this.date = date;
            this.time = time;
            this.tuteeName = tuteeName;
            this.tuteeEmail = tuteeEmail;
        }
        
        @Override 
        public void run(){
        System.out.println("sending emails");
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");

            SimpleDateFormat timeFmt = new SimpleDateFormat("hh:mm a");
            SimpleDateFormat dateFmt = new SimpleDateFormat("EEEEEE, MMMM dd");
            String datePrint = dateFmt.format(date);
            String timePrint = timeFmt.format(time);

            Session session = Session.getInstance(props,
                    new javax.mail.Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });
                
              try {

                Message tutorMessage = new MimeMessage(session);
                tutorMessage.setFrom(new InternetAddress("laneycodingclub@gmail.com"));
                tutorMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(t.getEmail()));
                tutorMessage.setSubject("Programming Help Session Scheduled");
                tutorMessage.setText("Hey " + t.getName().split(" ")[0]
                        + "; \n\nYou have a new tutoring appointment scheduled on " + datePrint + " at " + timePrint
                        + " with " + tuteeName + ". \n \nIf you cannot make this appointment, please reply to this email as soon as possible. "
                        + "\n \nThanks for helping out!");
                Transport.send(tutorMessage);

                Message tuteeMessage = new MimeMessage(session);
                tuteeMessage.setFrom(new InternetAddress("laneycodingclub@gmail.com"));
                tuteeMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(tuteeEmail));
                tuteeMessage.setSubject("Confirmation of your Programming Help Session");
                tuteeMessage.setText("Hey " + tuteeName.split(" ")[0] + ": \n\n"
                        + "This message is to confirm the programming help session you scheduled for "
                        + datePrint + " at " + timePrint + ". You'll be meeting with " + t.getName() + "."
                        + "\n\nYour session will be held in room 123 at Berkeley City College (behind the Cashier's office on the first floor)"
                        + "\n\nIf you need to cancel or reschedule your session, please email laneycodingclub@gmail.com (or simply reply to this email) as soon as possible"
                        + "\n\nLook forward to seeing you then!");

                Transport.send(tuteeMessage);

            } catch (MessagingException e) {e.printStackTrace();}
        }
    }
}
