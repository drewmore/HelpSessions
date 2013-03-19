package helpsessions;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Time;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ButtonGroup;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

/**
 * @author drew moore
 */
public class MainPanel extends javax.swing.JPanel {
    DBHandler dbh;
    ButtonGroup timesBG;    
    String dayToPass;
    String specToPass;  //are these ugly?
    String tuteeNameToPass;
    String tuteeEmailToPass;     
    Time timeToPass;
    Date dateToPass;
    SimpleDateFormat sdf;

    public MainPanel(DBHandler dbh) {
        this.dbh = dbh;
        timesBG = new ButtonGroup();
        sdf = new SimpleDateFormat("hh:mm a");
        
        initComponents();

        centerPanel.setVisible(false);
        botPanel.setVisible(false);
        confPanel.setVisible(false);
        successPanel.setVisible(false);
        monPanel.setLayout(new GridBagLayout());
        tuePanel.setLayout(new GridBagLayout());        
        wedPanel.setLayout(new GridBagLayout());
        thuPanel.setLayout(new GridBagLayout());            

        GridBagConstraints gbc = new GridBagConstraints();
       
        ButtonGroup specBG = new ButtonGroup();
        specBG.add(cPlusBtn);
        specBG.add(cSharpBtn);
        specBG.add(javaBtn);
        specBG.add(pythonBtn);       
        
        javaBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                specToPass = "Java";
                showTimes();
                //if the user is going back...
                botPanel.setVisible(false);
                confPanel.setVisible(false);
                successPanel.setVisible(false);
            }
        });
        
        cPlusBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                specToPass = "Cplus";
                showTimes();
                botPanel.setVisible(false);
                confPanel.setVisible(false);
                successPanel.setVisible(false);
            }
        });        

        cSharpBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                specToPass = "Csharp";
                showTimes();
                botPanel.setVisible(false);
                confPanel.setVisible(false);
                successPanel.setVisible(false);
            }
        });
        
        pythonBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                specToPass = "Python";
                showTimes();
                botPanel.setVisible(false);
                confPanel.setVisible(false);
                successPanel.setVisible(false);
            }
        });       

         scheduleBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    java.util.Date d = sdf.parse(timesCombo.getSelectedItem().toString());
                    java.sql.Time t = new java.sql.Time(d.getTime());
                    showConfirm(t);
                } catch (ParseException ex) {Logger.getLogger(MainPanel.class.getName()).log(Level.SEVERE, null, ex);}  
             }
          });
         
         confirmBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showResult();
            }
         });
         
        goBackBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                centerPanel.setVisible(false);
                botPanel.setVisible(false);
                confPanel.setVisible(false);
                javaBtn.setSelected(false);
                cPlusBtn.setSelected(false);
                cSharpBtn.setSelected(false);
                pythonBtn.setSelected(false);
            }
        });
    }

    void showTimes(){
        ArrayList<Time[]> monList = dbh.getTimes(specToPass, "mon");
        ArrayList<Time[]> tueList = dbh.getTimes(specToPass, "tue");        
        ArrayList<Time[]> wedList = dbh.getTimes(specToPass, "wed");        
        ArrayList<Time[]> thuList = dbh.getTimes(specToPass, "thu");

        monPanel.removeAll();
        monPanel.repaint();
        tuePanel.removeAll();        
        tuePanel.repaint();  
        wedPanel.removeAll();
        wedPanel.repaint();
        thuPanel.removeAll();  
        thuPanel.repaint();  
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = gbc.RELATIVE;
        
        //necessary to pass day in as parameter so the button will know what day it represents
        for (Time[] t : monList){
            monPanel.add(new TimePanel(t, this, "mon"), gbc);   
            monPanel.revalidate();
        }
        for (Time[] t : tueList){
            tuePanel.add(new TimePanel(t, this, "tue"),gbc );
            tuePanel.revalidate();
        }        
        for (Time[] t : wedList){
            wedPanel.add(new TimePanel(t, this, "wed"), gbc);
            wedPanel.revalidate();
        }
        for (Time[] t : thuList){
            thuPanel.add(new TimePanel(t, this, "thu"), gbc);
            thuPanel.revalidate();
        }               
        centerPanel.setVisible(true);
    }
    
    void showFinalize(Time[] t) {
        timesCombo.removeAllItems();
        ArrayList<Time> timesBetween= new ArrayList<Time>();
        Time startTime = t[0];
        Time endTime = t[1];
        
        timesCombo.addItem(sdf.format(startTime));
        //add times to the 
        Calendar cal = Calendar.getInstance();
        cal.setTime(startTime);
        
        while (cal.getTime().before(endTime)) {
            cal.add(Calendar.MINUTE, 15);
            Time tt = new Time(cal.getTimeInMillis());
            timesCombo.addItem(sdf.format(tt));
        }
        botPanel.setVisible(true);
        successPanel.setVisible(false);
        revalidate();
    }

   void showConfirm(Time t) {
       //check that a name has been entered 
       if (nameField.getText().matches("")){
           JLabel lab = new JLabel("Please enter your name");     
           lab.setHorizontalAlignment(SwingConstants.CENTER);
           JDialog jd = new JDialog();
           jd.setMinimumSize(new Dimension(300, 100));
           jd.setLocationRelativeTo(this);
           jd.setContentPane(lab);
           jd.setVisible(true);
       }
       //check that a valid email (with a @ and .) have been entered
       else if (!emailField.getText().contains("@") || !emailField.getText().contains(".")){
           JLabel lab = new JLabel("Please enter a valid email address");     
           lab.setHorizontalAlignment(SwingConstants.CENTER);
           JDialog jd = new JDialog();
           jd.setMinimumSize(new Dimension(300, 100));
           jd.setLocationRelativeTo(this);
           jd.setContentPane(lab);
           jd.setVisible(true);
       }
       
       else {
           goBackBtn.setVisible(true);
           confirmBtn.setEnabled(true);
           tuteeNameToPass = nameField.getText();
           tuteeEmailToPass = emailField.getText();
           System.out.println("SPEC " + specToPass);
           System.out.println("day " + dayToPass);       
           java.sql.Date d = dbh.setAppt(dayToPass, t, specToPass);
           timeLabel.setText(sdf.format(t));
           timeToPass = t;
           dateToPass = d;
           SimpleDateFormat datefmt = new SimpleDateFormat("EEE, MMMM dd");

           dateLabel.setText(datefmt.format(d));

           confPanel.setVisible(true);
           }
   }
   
    void showResult(){
        
        boolean successful = dbh.bookAppt(timeToPass, dateToPass, tuteeNameToPass, tuteeEmailToPass) ;
        
        if(!successful) {
            resultHead.setText("Sorry!");
            resultText.setText("All tutors are booked at this time, please try another.");
            successPanel.setVisible(true);
            botPanel.setVisible(false);
            confPanel.setVisible(false);       
        }
        
        else {
            resultHead.setText("SUCCESS!");
            resultText.setText("Your appointment is confirmed, we'll see ya then!");           
            successPanel.setVisible(true);
            goBackBtn.setVisible(false);
            confirmBtn.setVisible(false);
        }
    }



    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        cPlusBtn = new javax.swing.JRadioButton();
        cSharpBtn = new javax.swing.JRadioButton();
        pythonBtn = new javax.swing.JRadioButton();
        javaBtn = new javax.swing.JRadioButton();
        centerPanel = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        monPanel = new javax.swing.JPanel();
        tuePanel = new javax.swing.JPanel();
        wedPanel = new javax.swing.JPanel();
        thuPanel = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        botPanel = new javax.swing.JPanel();
        timesCombo = new javax.swing.JComboBox();
        jLabel7 = new javax.swing.JLabel();
        nameField = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        emailField = new javax.swing.JTextField();
        scheduleBtn = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        confPanel = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        timeLabel = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        dateLabel = new javax.swing.JLabel();
        goBackBtn = new javax.swing.JButton();
        confirmBtn = new javax.swing.JButton();
        successPanel = new javax.swing.JPanel();
        resultHead = new javax.swing.JLabel();
        resultText = new javax.swing.JLabel();

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        setBackground(new java.awt.Color(99, 99, 99));
        setMaximumSize(new java.awt.Dimension(800, 600));
        setMinimumSize(new java.awt.Dimension(800, 600));
        setPreferredSize(new java.awt.Dimension(800, 600));

        jLabel1.setFont(new java.awt.Font("Calibri Light", 0, 22)); // NOI18N
        jLabel1.setText("Schedule a Help Session");

        jLabel2.setFont(new java.awt.Font("Calibri Light", 2, 15)); // NOI18N
        jLabel2.setText("What can we help you with? ");

        cPlusBtn.setFont(new java.awt.Font("Calibri Light", 0, 14)); // NOI18N
        cPlusBtn.setText("C++");

        cSharpBtn.setFont(new java.awt.Font("Calibri Light", 0, 14)); // NOI18N
        cSharpBtn.setText("C# ");

        pythonBtn.setFont(new java.awt.Font("Calibri Light", 0, 14)); // NOI18N
        pythonBtn.setText("Python");

        javaBtn.setFont(new java.awt.Font("Calibri Light", 0, 14)); // NOI18N
        javaBtn.setText("Java");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addGap(84, 84, 84)
                .addComponent(cPlusBtn)
                .addGap(18, 18, 18)
                .addComponent(cSharpBtn)
                .addGap(27, 27, 27)
                .addComponent(javaBtn)
                .addGap(18, 18, 18)
                .addComponent(pythonBtn)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(303, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addContainerGap(286, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(cPlusBtn)
                    .addComponent(cSharpBtn)
                    .addComponent(javaBtn)
                    .addComponent(pythonBtn))
                .addGap(0, 18, Short.MAX_VALUE))
        );

        centerPanel.setMaximumSize(new java.awt.Dimension(799, 240));
        centerPanel.setMinimumSize(new java.awt.Dimension(799, 240));

        jLabel3.setFont(new java.awt.Font("Calibri Light", 0, 14)); // NOI18N
        jLabel3.setText("Monday");

        jLabel4.setFont(new java.awt.Font("Calibri Light", 0, 14)); // NOI18N
        jLabel4.setText("Tuesday");

        jLabel5.setFont(new java.awt.Font("Calibri Light", 0, 14)); // NOI18N
        jLabel5.setText("Wednesday");

        monPanel.setBackground(new java.awt.Color(255, 255, 255));
        monPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout monPanelLayout = new javax.swing.GroupLayout(monPanel);
        monPanel.setLayout(monPanelLayout);
        monPanelLayout.setHorizontalGroup(
            monPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 170, Short.MAX_VALUE)
        );
        monPanelLayout.setVerticalGroup(
            monPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 165, Short.MAX_VALUE)
        );

        tuePanel.setBackground(new java.awt.Color(255, 255, 255));
        tuePanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout tuePanelLayout = new javax.swing.GroupLayout(tuePanel);
        tuePanel.setLayout(tuePanelLayout);
        tuePanelLayout.setHorizontalGroup(
            tuePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 170, Short.MAX_VALUE)
        );
        tuePanelLayout.setVerticalGroup(
            tuePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        wedPanel.setBackground(new java.awt.Color(255, 255, 255));
        wedPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout wedPanelLayout = new javax.swing.GroupLayout(wedPanel);
        wedPanel.setLayout(wedPanelLayout);
        wedPanelLayout.setHorizontalGroup(
            wedPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 170, Short.MAX_VALUE)
        );
        wedPanelLayout.setVerticalGroup(
            wedPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        thuPanel.setBackground(new java.awt.Color(255, 255, 255));
        thuPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout thuPanelLayout = new javax.swing.GroupLayout(thuPanel);
        thuPanel.setLayout(thuPanelLayout);
        thuPanelLayout.setHorizontalGroup(
            thuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 170, Short.MAX_VALUE)
        );
        thuPanelLayout.setVerticalGroup(
            thuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jLabel6.setFont(new java.awt.Font("Calibri Light", 0, 14)); // NOI18N
        jLabel6.setText("Thursday");

        javax.swing.GroupLayout centerPanelLayout = new javax.swing.GroupLayout(centerPanel);
        centerPanel.setLayout(centerPanelLayout);
        centerPanelLayout.setHorizontalGroup(
            centerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, centerPanelLayout.createSequentialGroup()
                .addContainerGap(29, Short.MAX_VALUE)
                .addGroup(centerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(monPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addGap(18, 18, 18)
                .addGroup(centerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(tuePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addGap(18, 18, 18)
                .addGroup(centerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(wedPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addGap(18, 18, 18)
                .addGroup(centerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(thuPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addContainerGap(28, Short.MAX_VALUE))
        );
        centerPanelLayout.setVerticalGroup(
            centerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(centerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(centerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(centerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(thuPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tuePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(wedPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(monPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(39, Short.MAX_VALUE))
        );

        timesCombo.setFont(new java.awt.Font("Calibri Light", 0, 13)); // NOI18N

        jLabel7.setFont(new java.awt.Font("Calibri Light", 0, 13)); // NOI18N
        jLabel7.setText("select a time:");

        nameField.setFont(new java.awt.Font("Calibri Light", 0, 13)); // NOI18N

        jLabel9.setFont(new java.awt.Font("Calibri Light", 0, 13)); // NOI18N
        jLabel9.setText("your email address:");

        emailField.setFont(new java.awt.Font("Calibri Light", 0, 13)); // NOI18N

        scheduleBtn.setFont(new java.awt.Font("Calibri Light", 0, 13)); // NOI18N
        scheduleBtn.setText("schedule");

        jLabel11.setFont(new java.awt.Font("Calibri Light", 0, 13)); // NOI18N
        jLabel11.setText("your name:");

        javax.swing.GroupLayout botPanelLayout = new javax.swing.GroupLayout(botPanel);
        botPanel.setLayout(botPanelLayout);
        botPanelLayout.setHorizontalGroup(
            botPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, botPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(botPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(botPanelLayout.createSequentialGroup()
                        .addGroup(botPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel7)
                            .addComponent(jLabel9)
                            .addComponent(jLabel11))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(botPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(nameField)
                            .addComponent(emailField, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(timesCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, botPanelLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(scheduleBtn)
                        .addGap(111, 111, 111)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        botPanelLayout.setVerticalGroup(
            botPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(botPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(botPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(timesCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addGap(18, 18, 18)
                .addGroup(botPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11))
                .addGap(9, 9, 9)
                .addGroup(botPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(emailField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addGap(18, 18, 18)
                .addComponent(scheduleBtn)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel8.setFont(new java.awt.Font("Calibri Light", 0, 13)); // NOI18N
        jLabel8.setText("Please confirm your appointment at");

        timeLabel.setFont(new java.awt.Font("Calibri Light", 1, 15)); // NOI18N
        timeLabel.setText("00:00 AM");

        jLabel13.setFont(new java.awt.Font("Calibri Light", 0, 13)); // NOI18N
        jLabel13.setText("on");

        dateLabel.setFont(new java.awt.Font("Calibri Light", 1, 15)); // NOI18N
        dateLabel.setText("Mon, Aug 12");

        goBackBtn.setFont(new java.awt.Font("Calibri Light", 0, 13)); // NOI18N
        goBackBtn.setText("go back");

        confirmBtn.setFont(new java.awt.Font("Calibri Light", 0, 13)); // NOI18N
        confirmBtn.setText("confirm");

        javax.swing.GroupLayout confPanelLayout = new javax.swing.GroupLayout(confPanel);
        confPanel.setLayout(confPanelLayout);
        confPanelLayout.setHorizontalGroup(
            confPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(confPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(timeLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dateLabel)
                .addGap(18, 18, 18)
                .addComponent(confirmBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(goBackBtn)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        confPanelLayout.setVerticalGroup(
            confPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, confPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(confPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(timeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13)
                    .addComponent(dateLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(goBackBtn)
                    .addComponent(confirmBtn))
                .addContainerGap())
        );

        resultHead.setFont(new java.awt.Font("Calibri Light", 2, 18)); // NOI18N
        resultHead.setText("SUCCESS!");

        resultText.setFont(new java.awt.Font("Calibri Light", 0, 13)); // NOI18N
        resultText.setText("Your appointment is confirmed. We'll see ya then!");

        javax.swing.GroupLayout successPanelLayout = new javax.swing.GroupLayout(successPanel);
        successPanel.setLayout(successPanelLayout);
        successPanelLayout.setHorizontalGroup(
            successPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, successPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(resultHead)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(resultText)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        successPanelLayout.setVerticalGroup(
            successPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(successPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(successPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(resultHead)
                    .addComponent(resultText))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(confPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(1, 1, 1)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(botPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(centerPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
            .addComponent(successPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(centerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(botPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(confPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(successPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel botPanel;
    private javax.swing.JRadioButton cPlusBtn;
    private javax.swing.JRadioButton cSharpBtn;
    private javax.swing.JPanel centerPanel;
    private javax.swing.JPanel confPanel;
    private javax.swing.JButton confirmBtn;
    private javax.swing.JLabel dateLabel;
    private javax.swing.JTextField emailField;
    private javax.swing.JButton goBackBtn;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JRadioButton javaBtn;
    private javax.swing.JPanel monPanel;
    private javax.swing.JTextField nameField;
    private javax.swing.JRadioButton pythonBtn;
    private javax.swing.JLabel resultHead;
    private javax.swing.JLabel resultText;
    private javax.swing.JButton scheduleBtn;
    private javax.swing.JPanel successPanel;
    private javax.swing.JPanel thuPanel;
    private javax.swing.JLabel timeLabel;
    private javax.swing.JComboBox timesCombo;
    private javax.swing.JPanel tuePanel;
    private javax.swing.JPanel wedPanel;
    // End of variables declaration//GEN-END:variables
}
