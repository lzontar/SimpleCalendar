/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.*;
import java.util.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

/**
 *
 * @author Luka
 */
//



//custom ComboBoxUI
class CustomCBUI extends javax.swing.plaf.basic.BasicComboBoxUI {
    //combobox arrow custom design
    @Override protected JButton createArrowButton() {
       return new javax.swing.plaf.basic.BasicArrowButton(
           javax.swing.plaf.basic.BasicArrowButton.SOUTH,
           new Color(179,179,179), Color.GRAY,
           Color.WHITE, null);
   }
    //combobox scroller custom design
   @Override
   protected javax.swing.plaf.basic.ComboPopup createPopup() {
       return new javax.swing.plaf.basic.BasicComboPopup(comboBox) {
           @Override
           protected JScrollPane createScroller() {
               JScrollPane scroller = new JScrollPane(list, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                       JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
               //custom vertical scroller
               scroller.getVerticalScrollBar().setUI(new VerticalScroller());
               return scroller;
           }
       };
   }
}

//custom scroller for ComboBox-es
class VerticalScroller extends javax.swing.plaf.basic.BasicScrollBarUI {
    //dimension of width 0 and height 0
    private final Dimension dimension = new Dimension();
    
    //create decreaseButton dimensions width 0, height 0 (empty)
    @Override
    protected JButton createDecreaseButton(int orientation) {

      return new JButton() {
        @Override
        public Dimension getPreferredSize() {
          return dimension;
        }
      };
    }
    //create increaseButton dimensions width 0, height 0(empty)
    @Override
    protected JButton createIncreaseButton(int orientation) {
      return new JButton() {
        @Override
        public Dimension getPreferredSize() {
          return dimension;
        }
      };
    }
    //design of combobox thumb
    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
        Graphics2D g2 = (Graphics2D) g.create();
        Color color = new Color(179,179,179);
        JScrollBar scroller = (JScrollBar) c;
        //if scroll bar is not enable we must abort
        if (!scroller.isEnabled()) {
          return;
        } 
        g2.setPaint(color);
        g2.fillRoundRect(r.x, r.y, r.width, r.height, 10, 10);
        g2.setPaint(Color.WHITE);
        g2.drawRoundRect(r.x, r.y, r.width, r.height, 10, 10);
        g2.dispose();
      }
    
}
//custom comboBox
class CustomComboBox extends javax.swing.plaf.basic.BasicComboBoxRenderer {
    
  public Component getListCellRendererComponent(JList list, Object value,
      int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected,
            cellHasFocus);
        this.setOpaque(true);
        this.setFont(new Font("Open Sans",Font.ITALIC, 14));
        this.setVerticalAlignment(JLabel.CENTER);
        this.setText(value.toString());
        
        //if cell is selected we color it
        if(isSelected) {
             this.setBackground(new Color(179,179,179));
             this.setForeground(Color.WHITE);
        }else {
            this.setBackground(Color.WHITE);
            this.setForeground(Color.BLACK);
        }
        return this;
  }
  
}

//custom border
 class RoundedBorder extends javax.swing.border.AbstractBorder {

        private final Color color;

        public RoundedBorder(Color c) {
            color = c;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            //draw a circle(used for circling the sundays and holidays in calendar)
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setColor(color);
            g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
            g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            g2d.setStroke(new BasicStroke(4));
            g2d.drawOval(x + width/2 - height/2,y + 5,height,height - 10);
            g2d.dispose();
        }
    }

//renderer for calendarTab
class CalendarRenderer extends javax.swing.table.DefaultTableCellRenderer {
  //month of current calendar
  int month;
  //year of current calendar
  int year;
  //holidays that happen only once, read from text document
  private HashMap<Integer, HashMap<Integer,ArrayList<Integer>>> oneTimeHolidays;
  //holidays that happen every year, read from text document
  private HashMap<Integer, ArrayList<Integer>> repHolidays; 
   
  //ColorRenderer constructor
  public CalendarRenderer(int month, int year, HashMap<Integer, HashMap<Integer,ArrayList<Integer>>> oneTimeHolidays, HashMap<Integer, ArrayList<Integer>> repHolidays) {
      //current month
      this.month = month;
      //current year
      this.year = year;
      //holidays from text document
      this.oneTimeHolidays = oneTimeHolidays;
      this.repHolidays = repHolidays;
  }
  
  
  
  @Override
  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
    //mark sundays
    if(col == 6) {
        this.setBackground(new Color(179, 179, 179));
        this.setForeground(Color.WHITE);
        if(value != null) {
            this.setBorder(new RoundedBorder(new Color(179, 0, 0)));
            
        }
        
        
    } else {
        //mark holidays
        checkForHolidays(value, row);
        
    }
    //input value(day of month)
    if(value != null) {
        this.setText(value.toString());
    } else {
        this.setText("");
    }
    //styling
    this.setHorizontalAlignment(JLabel.CENTER);
    this.setFont(new Font("Open Sans", Font.BOLD, 16));
    //jtable background color(parent of component)
    table.setBackground(new Color(0, 0, 0));
  //return  JLabel which renders the cell.
  return this;
  }
    
  
    //check if date with day=value and this.month and this.year is a holiday
    public void checkForHolidays(Object value, int row) {
        //check if element exists
        if(this.repHolidays != null) {
            if(this.repHolidays.get(this.month) != null) {
                if(this.repHolidays.get(this.month).contains(value)) {
                    this.setBackground(new Color(217, 217, 217));
                    this.setForeground(Color.BLACK);
                    if(value != null) {
                        this.setBorder(new RoundedBorder(new Color(179, 0, 0)));
                    }
                } else {
                    checkForNonRepetitiousHolidays(value, row);
                }

            } else {
                checkForNonRepetitiousHolidays(value, row);
            }
        } else {
            checkForNonRepetitiousHolidays(value, row);
        }
        
    }
    //check if this date is a non-repetitious holiday
    public void checkForNonRepetitiousHolidays(Object value, int row) {
        //check if element exists
        if(this.oneTimeHolidays != null) {
            if(this.oneTimeHolidays.get(this.year) != null) {
                if(this.oneTimeHolidays.get(this.year).get(this.month) != null) {
                    if(this.oneTimeHolidays.get(this.year).get(this.month).contains(value)) {
                        this.setBackground(new Color(217, 217, 217));
                        this.setForeground(Color.BLACK);
                        if(value != null) {
                            this.setBorder(new RoundedBorder(new Color(179, 0, 0)));
                        }
                    } else {
                        this.setBackground(new Color(217, 217, 217));
                        this.setForeground(Color.BLACK);
                        this.setBorder(noFocusBorder);
                    }

                } else {
                    this.setBackground(new Color(217, 217, 217));
                    this.setForeground(Color.BLACK);
                    this.setBorder(noFocusBorder);
                }
            }
            else {
                this.setBackground(new Color(217, 217, 217));
                this.setForeground(Color.BLACK);
                this.setBorder(noFocusBorder);
            }

        } else {
            this.setBackground(new Color(217, 217, 217));
            this.setForeground(Color.BLACK);
            this.setBorder(noFocusBorder);
        }
    }
}

public class SimpleCalendar extends javax.swing.JFrame {

    /**
     * Creates new form NewJFrame
     */
    public SimpleCalendar() {
        initComponents();
        //set custom design for combobox-es
        monthInput.setUI(new CustomCBUI());   
        monthInput.setRenderer(new CustomComboBox());
        specDateMonth.setUI(new CustomCBUI());   
        specDateMonth.setRenderer(new CustomComboBox());
        
       
        //get holidays
        try {  
                //read text document with holidays and fill HashMaps oneTimeHolidays and repHolidays
                this.getHolidays();

            } catch (Exception ex) {
                Logger.getLogger(SimpleCalendar.class.getName()).log(Level.SEVERE, null, ex);
            }
        //renderer for table header
        calendarTab.getTableHeader().setDefaultRenderer(new javax.swing.table.DefaultTableCellRenderer() {
        
        
            
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            //styling
            this.setText(value.toString());
            this.setBackground(new Color(0,0,0));
            this.setForeground(Color.WHITE);
            this.setPreferredSize(new Dimension(100, 48));
            this.setHorizontalAlignment(JLabel.CENTER);
            this.setFont(new Font("Open Sans", Font.BOLD, 16));
            
            return this;
        }
    });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        enterDateDiag = new javax.swing.JDialog();
        enterDateForm = new javax.swing.JPanel();
        getCalendarBtn = new javax.swing.JButton();
        specMonthLabel = new javax.swing.JLabel();
        specDateMonth = new javax.swing.JComboBox<>();
        specDayLabel = new javax.swing.JLabel();
        specDateDay = new javax.swing.JTextField();
        specYearLabel = new javax.swing.JLabel();
        cancelBtn = new javax.swing.JButton();
        specDateYear = new javax.swing.JTextField();
        enterDateHeader = new javax.swing.JPanel();
        enterDateLabel = new javax.swing.JLabel();
        wrongInputDiag = new javax.swing.JDialog();
        errorHeader = new javax.swing.JPanel();
        errorTitle = new javax.swing.JLabel();
        errorMessagePanel = new javax.swing.JPanel();
        closeDiagBtn = new javax.swing.JButton();
        errorMessage = new javax.swing.JLabel();
        headerPanel = new javax.swing.JPanel();
        appName = new javax.swing.JLabel();
        yearLabel = new javax.swing.JLabel();
        currDate = new javax.swing.JLabel();
        enterDateBtn = new javax.swing.JButton();
        generateBtn = new javax.swing.JButton();
        monthInput = new javax.swing.JComboBox<>();
        monthLabel = new javax.swing.JLabel();
        yearInput = new javax.swing.JTextField();
        headerBackground = new javax.swing.JLabel();
        calendarPanel = new javax.swing.JScrollPane();
        calendarTab = new javax.swing.JTable();

        enterDateDiag.setBackground(new java.awt.Color(255, 255, 255));
        enterDateDiag.setMinimumSize(new java.awt.Dimension(690, 280));
        enterDateDiag.setResizable(false);
        enterDateDiag.getContentPane().setLayout(null);

        enterDateForm.setBackground(new java.awt.Color(255, 255, 255));
        enterDateForm.setMinimumSize(new java.awt.Dimension(100, 100));
        enterDateForm.setLayout(null);

        getCalendarBtn.setBackground(new java.awt.Color(255, 255, 255));
        getCalendarBtn.setFont(new java.awt.Font("Open Sans", 0, 14)); // NOI18N
        getCalendarBtn.setText("Generate Calendar");
        getCalendarBtn.setBorder(null);
        getCalendarBtn.setBorderPainted(false);
        getCalendarBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                getCalendarActionPerformed(evt);
            }
        });
        enterDateForm.add(getCalendarBtn);
        getCalendarBtn.setBounds(110, 200, 170, 40);

        specMonthLabel.setFont(new java.awt.Font("Open Sans", 0, 18)); // NOI18N
        specMonthLabel.setText("Month:");
        enterDateForm.add(specMonthLabel);
        specMonthLabel.setBounds(40, 80, 70, 40);

        specDateMonth.setFont(new java.awt.Font("Open Sans", 0, 14)); // NOI18N
        specDateMonth.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" }));
        specDateMonth.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                specDateMonthActionPerformed(evt);
            }
        });
        enterDateForm.add(specDateMonth);
        specDateMonth.setBounds(110, 80, 140, 40);

        specDayLabel.setFont(new java.awt.Font("Open Sans", 0, 18)); // NOI18N
        specDayLabel.setText("Day:");
        enterDateForm.add(specDayLabel);
        specDayLabel.setBounds(60, 10, 50, 60);

        specDateDay.setFont(new java.awt.Font("Open Sans", 0, 14)); // NOI18N
        specDateDay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                specDateDayActionPerformed(evt);
            }
        });
        enterDateForm.add(specDateDay);
        specDateDay.setBounds(110, 20, 100, 40);

        specYearLabel.setFont(new java.awt.Font("Open Sans", 0, 18)); // NOI18N
        specYearLabel.setText("Year:");
        enterDateForm.add(specYearLabel);
        specYearLabel.setBounds(60, 130, 110, 40);

        cancelBtn.setBackground(new java.awt.Color(255, 255, 255));
        cancelBtn.setFont(new java.awt.Font("Open Sans", 0, 14)); // NOI18N
        cancelBtn.setText("Cancel");
        cancelBtn.setBorder(null);
        cancelBtn.setBorderPainted(false);
        cancelBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelBtnActionPerformed(evt);
            }
        });
        enterDateForm.add(cancelBtn);
        cancelBtn.setBounds(310, 200, 130, 40);

        specDateYear.setFont(new java.awt.Font("Open Sans", 0, 14)); // NOI18N
        specDateYear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                specDateYearActionPerformed(evt);
            }
        });
        enterDateForm.add(specDateYear);
        specDateYear.setBounds(110, 130, 140, 40);

        enterDateDiag.getContentPane().add(enterDateForm);
        enterDateForm.setBounds(240, 0, 450, 250);

        enterDateHeader.setBackground(new java.awt.Color(0, 0, 0));

        enterDateLabel.setFont(new java.awt.Font("Open Sans", 0, 36)); // NOI18N
        enterDateLabel.setForeground(new java.awt.Color(255, 255, 255));
        enterDateLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        enterDateLabel.setText("Enter Date");

        javax.swing.GroupLayout enterDateHeaderLayout = new javax.swing.GroupLayout(enterDateHeader);
        enterDateHeader.setLayout(enterDateHeaderLayout);
        enterDateHeaderLayout.setHorizontalGroup(
            enterDateHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(enterDateHeaderLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(enterDateLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 183, Short.MAX_VALUE)
                .addGap(32, 32, 32))
        );
        enterDateHeaderLayout.setVerticalGroup(
            enterDateHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(enterDateHeaderLayout.createSequentialGroup()
                .addGap(60, 60, 60)
                .addComponent(enterDateLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(80, Short.MAX_VALUE))
        );

        enterDateDiag.getContentPane().add(enterDateHeader);
        enterDateHeader.setBounds(0, 0, 240, 250);

        wrongInputDiag.setBackground(new java.awt.Color(255, 255, 255));
        wrongInputDiag.setMinimumSize(new java.awt.Dimension(320, 200));
        wrongInputDiag.setModal(true);
        wrongInputDiag.setResizable(false);

        errorHeader.setBackground(new java.awt.Color(0, 0, 0));

        errorTitle.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        errorTitle.setForeground(new java.awt.Color(255, 255, 255));
        errorTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        errorTitle.setText("Error");

        javax.swing.GroupLayout errorHeaderLayout = new javax.swing.GroupLayout(errorHeader);
        errorHeader.setLayout(errorHeaderLayout);
        errorHeaderLayout.setHorizontalGroup(
            errorHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, errorHeaderLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(errorTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(46, 46, 46))
        );
        errorHeaderLayout.setVerticalGroup(
            errorHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, errorHeaderLayout.createSequentialGroup()
                .addContainerGap(16, Short.MAX_VALUE)
                .addComponent(errorTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        errorMessagePanel.setBackground(new java.awt.Color(255, 255, 255));

        closeDiagBtn.setBackground(new java.awt.Color(255, 255, 255));
        closeDiagBtn.setFont(new java.awt.Font("Open Sans", 0, 14)); // NOI18N
        closeDiagBtn.setText("Ok");
        closeDiagBtn.setBorder(null);
        closeDiagBtn.setBorderPainted(false);
        closeDiagBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeDiagBtnActionPerformed(evt);
            }
        });

        errorMessage.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        errorMessage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        errorMessage.setText("Invalid input.");

        javax.swing.GroupLayout errorMessagePanelLayout = new javax.swing.GroupLayout(errorMessagePanel);
        errorMessagePanel.setLayout(errorMessagePanelLayout);
        errorMessagePanelLayout.setHorizontalGroup(
            errorMessagePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, errorMessagePanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(closeDiagBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(46, 46, 46))
            .addGroup(errorMessagePanelLayout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(errorMessage, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(57, Short.MAX_VALUE))
        );
        errorMessagePanelLayout.setVerticalGroup(
            errorMessagePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, errorMessagePanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(errorMessage, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(closeDiagBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(35, 35, 35))
        );

        javax.swing.GroupLayout wrongInputDiagLayout = new javax.swing.GroupLayout(wrongInputDiag.getContentPane());
        wrongInputDiag.getContentPane().setLayout(wrongInputDiagLayout);
        wrongInputDiagLayout.setHorizontalGroup(
            wrongInputDiagLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(wrongInputDiagLayout.createSequentialGroup()
                .addComponent(errorHeader, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(errorMessagePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        wrongInputDiagLayout.setVerticalGroup(
            wrongInputDiagLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(errorMessagePanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(errorHeader, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(0, 0, 0));
        setMinimumSize(new java.awt.Dimension(920, 700));
        setPreferredSize(new java.awt.Dimension(1330, 1000));
        getContentPane().setLayout(new java.awt.GridBagLayout());

        headerPanel.setBackground(new java.awt.Color(0, 0, 0));
        headerPanel.setLayout(new java.awt.GridBagLayout());

        appName.setFont(new java.awt.Font("Open Sans", 1, 48)); // NOI18N
        appName.setForeground(new java.awt.Color(255, 255, 255));
        appName.setText("Simple Calendar");
        appName.setAutoscrolls(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.ipadx = 178;
        gridBagConstraints.ipady = 20;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(57, 12, 0, 0);
        headerPanel.add(appName, gridBagConstraints);

        yearLabel.setFont(new java.awt.Font("Open Sans", 0, 24)); // NOI18N
        yearLabel.setForeground(new java.awt.Color(255, 255, 255));
        yearLabel.setText("Year : ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipady = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        headerPanel.add(yearLabel, gridBagConstraints);

        currDate.setBackground(new java.awt.Color(173, 224, 255));
        currDate.setFont(new java.awt.Font("Open Sans", 1, 36)); // NOI18N
        currDate.setForeground(new java.awt.Color(255, 255, 255));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 380;
        gridBagConstraints.ipady = 50;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.insets = new java.awt.Insets(32, 2, 0, 5);
        headerPanel.add(currDate, gridBagConstraints);

        enterDateBtn.setBackground(new java.awt.Color(255, 255, 255));
        enterDateBtn.setFont(new java.awt.Font("Open Sans", 0, 16)); // NOI18N
        enterDateBtn.setText("Enter Date");
        enterDateBtn.setBorder(null);
        enterDateBtn.setBorderPainted(false);
        enterDateBtn.setMaximumSize(new java.awt.Dimension(100, 30));
        enterDateBtn.setMinimumSize(new java.awt.Dimension(100, 30));
        enterDateBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                enterDateBtnActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipady = 12;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 370, 0, 0);
        headerPanel.add(enterDateBtn, gridBagConstraints);

        generateBtn.setBackground(new java.awt.Color(255, 255, 255));
        generateBtn.setFont(new java.awt.Font("Open Sans", 0, 16)); // NOI18N
        generateBtn.setText("Generate Calendar");
        generateBtn.setAlignmentY(5.0F);
        generateBtn.setBorder(null);
        generateBtn.setBorderPainted(false);
        generateBtn.setFocusPainted(false);
        generateBtn.setMargin(new java.awt.Insets(10, 14, 10, 14));
        generateBtn.setMaximumSize(new java.awt.Dimension(125, 25));
        generateBtn.setMinimumSize(new java.awt.Dimension(125, 25));
        generateBtn.setOpaque(false);
        generateBtn.setPreferredSize(new java.awt.Dimension(266, 266));
        generateBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                generateBtnActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 35;
        gridBagConstraints.ipady = 19;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 0.6;
        gridBagConstraints.insets = new java.awt.Insets(0, 200, 0, 0);
        headerPanel.add(generateBtn, gridBagConstraints);

        monthInput.setFont(new java.awt.Font("Open Sans", 0, 16)); // NOI18N
        monthInput.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" }));
        monthInput.setBorder(null);
        monthInput.setMinimumSize(new java.awt.Dimension(140, 27));
        monthInput.setName("monthInput"); // NOI18N
        monthInput.setPreferredSize(new java.awt.Dimension(140, 27));
        monthInput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                monthInputActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 10);
        headerPanel.add(monthInput, gridBagConstraints);
        monthInput.getAccessibleContext().setAccessibleName("");

        monthLabel.setBackground(new java.awt.Color(0, 0, 0));
        monthLabel.setFont(new java.awt.Font("Open Sans", 0, 24)); // NOI18N
        monthLabel.setForeground(new java.awt.Color(255, 255, 255));
        monthLabel.setText("Month : ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.RELATIVE;
        gridBagConstraints.ipady = 12;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 150);
        headerPanel.add(monthLabel, gridBagConstraints);

        yearInput.setFont(new java.awt.Font("Open Sans", 0, 16)); // NOI18N
        yearInput.setName("yearInput"); // NOI18N
        yearInput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                yearInputActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 100;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 70, 0, 0);
        headerPanel.add(yearInput, gridBagConstraints);

        headerBackground.setBackground(new java.awt.Color(0, 0, 0));
        headerBackground.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        headerBackground.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iPhone-Photos-Black-And-White-24.jpg"))); // NOI18N
        headerBackground.setAutoscrolls(true);
        headerBackground.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        headerBackground.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.8;
        gridBagConstraints.insets = new java.awt.Insets(13, 7, 0, 0);
        headerPanel.add(headerBackground, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = -20;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 2);
        getContentPane().add(headerPanel, gridBagConstraints);

        calendarPanel.setBackground(new java.awt.Color(0, 0, 0));
        calendarPanel.setBorder(null);

        calendarTab.setBackground(new java.awt.Color(0, 0, 0));
        calendarTab.setFont(new java.awt.Font("Open Sans", 0, 13)); // NOI18N
        calendarTab.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        calendarTab.setCursor(new java.awt.Cursor(java.awt.Cursor.E_RESIZE_CURSOR));
        calendarTab.setFillsViewportHeight(true);
        calendarTab.setGridColor(new java.awt.Color(0, 0, 0));
        calendarTab.setRowHeight(64);
        calendarTab.setRowSelectionAllowed(false);
        calendarTab.setSelectionBackground(new java.awt.Color(255, 255, 255));
        calendarTab.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        calendarTab.getTableHeader().setResizingAllowed(false);
        calendarTab.getTableHeader().setReorderingAllowed(false);
        calendarPanel.setViewportView(calendarTab);
        if (calendarTab.getColumnModel().getColumnCount() > 0) {
            calendarTab.getColumnModel().getColumn(0).setResizable(false);
            calendarTab.getColumnModel().getColumn(1).setResizable(false);
            calendarTab.getColumnModel().getColumn(2).setResizable(false);
            calendarTab.getColumnModel().getColumn(3).setResizable(false);
            calendarTab.getColumnModel().getColumn(4).setResizable(false);
            calendarTab.getColumnModel().getColumn(5).setResizable(false);
            calendarTab.getColumnModel().getColumn(6).setResizable(false);
        }

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 1093;
        gridBagConstraints.ipady = 434;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 2.1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 1, 2);
        getContentPane().add(calendarPanel, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    private void specDateMonthActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_specDateMonthActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_specDateMonthActionPerformed

    //get calendar based on specific input date
    private void getCalendarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_getCalendarActionPerformed
        // TODO add your handling code here:
        //get year from text input
        int year = 0;
        //check if input is even a number, else tell user he has to input a number
        try {
            year = Integer.parseInt(specDateYear.getText());
        } catch (NumberFormatException | NullPointerException npe) {
            wrongInputDiag.setVisible(true);
            //clear fields
            specDateYear.setText("");
            specDateDay.setText("");
            return;
        }
        
        
        //get month from combo box
        String selectedMonth = specDateMonth.getSelectedItem().toString();
        int monthZeller = monthToInt.get(selectedMonth);
       //first day of the month==> now we can see how many repetitions of each day there is
        int firstDayOfMonth = getFirstDay(monthZeller, year);
        //from now on we access january as 0, february as 1 and so on
        int month = monthZeller <= 12 ? (monthZeller - 1) : ((monthZeller - 1) % 12);
        int numOfDays = daysOfMonth[month];
    
        //if month is february and year is leap year we should increase number of days in february by 1
        if(isLeapYear(year) && month == 1) {
            numOfDays = 29; 
        }
        
        //check if day is valid
        try {
            int day = Integer.parseInt(specDateDay.getText());
            if(day > numOfDays) {
                wrongInputDiag.setVisible(true);
                //clear fields
                specDateYear.setText("");
                specDateDay.setText("");
                return;
            }
        } catch (NumberFormatException | NullPointerException npe) {
            wrongInputDiag.setVisible(true);
            //clear fields
            specDateYear.setText("");
            specDateDay.setText("");
            return;
        }
        
        //the day of the first in given month of given year, +5 because Zeller's congruence returns Saturday as 0, Sunday as 1 and so on
        firstDayOfMonth = (firstDayOfMonth + 5) % 7;
        //set default renderer(we can set each cell as we wish)
        calendarTab.setDefaultRenderer(Object.class, new CalendarRenderer(month, year, oneTimeHolidays, repHolidays));
        //initialization of jTable
        initializeTable(month, year, firstDayOfMonth, numOfDays);
        //generation of calendar
        generateCalendar(month, year, firstDayOfMonth, numOfDays);
        //clear fields
        specDateYear.setText("");
        specDateDay.setText("");
        //hide JDialog
        enterDateDiag.setVisible(false);
        
    }
    //initialization of calendarTab based on given month and year
    private void initializeTable(int month, int year, int firstDay, int numOfDays){
        
        //week 1
        numOfDays -= 7 - firstDay;
        //calculation of number of weeks, later we add just as many rows to our newTable
        int numOfWeeks = 1 + (int) Math.ceil(numOfDays/7.0);
        Object[][] newTable = new Object[numOfWeeks][7];
       
        for(int i = 0; i < numOfWeeks; i++) {
            for(int j = 0 ; j < 7; j++) {
                newTable[i][j] = null;
            }
        }
        //reinitialization of our calendarTab
        calendarTab.setModel(new javax.swing.table.DefaultTableModel(
            newTable,
            new String [] {
                "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"
            }
        ) {
            //calendar must be non-editable
            @Override
            public boolean isCellEditable(int row, int column) { // custom isCellEditable function
                return false;
            }
        });
        calendarPanel.setViewportView(calendarTab);
     
        
    }
    
    //function generates calendar 
    private void generateCalendar(int month, int year, int firstDay, int numOfDays) {
        //we start at day 1
        int currDay = 1;
        int column = firstDay;
        int row = 0;
        while(numOfDays != 0) {
            calendarTab.setValueAt(currDay, row,column);
            //if the current day is sunday we should move to next row
            if(column >= 6) {
                row++;
            }
            column = (column + 1) % 7;
            currDay++;
            numOfDays--;
        }
        //set currDate to month and year that calendar is showing
        currDate.setText(nameOfMonth[month]+ " " + year);
    }//GEN-LAST:event_getCalendarActionPerformed

    private void specDateDayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_specDateDayActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_specDateDayActionPerformed

    private void specDateYearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_specDateYearActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_specDateYearActionPerformed

    private void cancelBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelBtnActionPerformed
        // TODO add your handling code here:
        specDateDay.setText("");
        specDateYear.setText("");
        enterDateDiag.setVisible(false);
    }//GEN-LAST:event_cancelBtnActionPerformed

    private void closeDiagBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeDiagBtnActionPerformed
        // TODO add your handling code here:
        wrongInputDiag.setVisible(false);
    }//GEN-LAST:event_closeDiagBtnActionPerformed

    private void yearInputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_yearInputActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_yearInputActionPerformed

    //get calendar based on month and year only
    private void generateBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generateBtnActionPerformed
        // TODO add your handling code here:
        
        //get year from text input
        int year = 0;
        //check if input is even a number, else tell user he has to input a number
        try {
            year = Integer.parseInt(yearInput.getText());
        } catch (NumberFormatException | NullPointerException npe) {
            wrongInputDiag.setVisible(true);
            //clear input
            yearInput.setText("");
            return;
        }
        //get month from combo box
        String selectedMonth = monthInput.getSelectedItem().toString();
        int monthZeller = monthToInt.get(selectedMonth);
        //first day of the month==> now we can see how many repetitions of each day there is
        int firstDayOfMonth = getFirstDay(monthZeller, year);
        //from now on we access january as 0, february as 1 and so on
        int month = monthZeller <= 12 ? (monthZeller - 1) : ((monthZeller - 1) % 12);
        int numOfDays = daysOfMonth[month];
        //if month is february and year is leap year we should increase number of days in february by 1
        if(isLeapYear(year) && month == 1) {
            numOfDays = 29;
        }
        //the day of the first in given month of given year, +5 because Zeller's congruence returns Saturday as 0, Sunday as 1 and so on
        firstDayOfMonth = (firstDayOfMonth + 5) % 7;
        //set default renderer(we can set each cell as we wish)
        calendarTab.setDefaultRenderer(Object.class, new CalendarRenderer(month, year, oneTimeHolidays, repHolidays));

        //initialization of jTable
        initializeTable(month, year, firstDayOfMonth, numOfDays);
        //generation of calendar
        generateCalendar(month, year, firstDayOfMonth, numOfDays);
        //clear input
        yearInput.setText("");
    }//GEN-LAST:event_generateBtnActionPerformed

    private void enterDateBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_enterDateBtnActionPerformed
        // TODO add your handling code here:
        enterDateDiag.setVisible(true);
    }//GEN-LAST:event_enterDateBtnActionPerformed

    private void monthInputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_monthInputActionPerformed

    }//GEN-LAST:event_monthInputActionPerformed

   
   
    
    private void getTodayCalendar() {
        Date today = new Date();
        SimpleDateFormat formatToday = new SimpleDateFormat("yyyy-MM");
        String[] date = formatToday.format(today).split("-");
        int month = Integer.parseInt(date[1]) - 1;
        int year = Integer.parseInt(date[0]);
        
        //adjust month to Zeller's congruence formula
        int monthZeller = month + 1;
        if(month == 0 || month == 1) {
            monthZeller += 12;
        }
        //first day of the month==> now we can see how many repetitions of each day there is
        int firstDayOfMonth = getFirstDay(monthZeller, year);
        int numOfDays = daysOfMonth[month];
        //if month is february and year is leap year we should increase number of days in february by 1
        if(isLeapYear(year) && month == 1) {
            numOfDays = 29; 
        }
        //the day of the first in given month of given year, +5 because Zeller's congruence returns Saturday as 0, Sunday as 1 and so on
        firstDayOfMonth = (firstDayOfMonth + 5) % 7;
        //set default renderer(we can set each cell as we wish)
        calendarTab.setDefaultRenderer(Object.class, new CalendarRenderer(month, year, oneTimeHolidays, repHolidays));
        //initialization of jTable
        initializeTable(month, year, firstDayOfMonth, numOfDays);
        //generation of calendar
        generateCalendar(month, year, firstDayOfMonth, numOfDays);
    }
    
    //returns true if given year is leap year and false if not
    public static boolean isLeapYear(int year) {
        //year is a leap year if it is not divisible by 100 but divisible by 4 or if it is divisible by 400 and therefore by 100 as well
        if(year % 100 != 0) {
            return (year % 4 == 0);
        } else {
            return (year % 400 == 0);
        }
    }
    
    //calculates the first day of month using Zeller's congruence
    public static int getFirstDay(int month, int year) {
        int day = 1;    // first day of the month
        //based on Zeller's congruence we should substract 1 from year if the month is either february or january
        if(month == 13 || month == 14) {
            year--;
        }
        int k = year % 100;
        int j = (int) Math.floor(year / 100);
        //returns 0-saturday, 1-sunday, 2-monday...
        return (int) ((day + Math.floor(26.0 * (month + 1)/10.0) + k + Math.floor(k/ 4.0) + Math.floor(j/4.0) + (5 * j)) % 7);
    }
   
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) throws Exception {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                   
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(SimpleCalendar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(SimpleCalendar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(SimpleCalendar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SimpleCalendar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                SimpleCalendar f = new SimpleCalendar();
                f.setBackground(Color.red);
                f.setVisible(true);
                
                
                
                //initializing calendar to today's date
                f.getTodayCalendar();
                
            }
        });
            
    }
    
    
    //read holidays from text documents
    public  void getHolidays() throws Exception{
        //read document with holidays
        //initialize HashMaps oneTimeHolidays and repHolidays
        repHolidays = new HashMap<Integer, ArrayList<Integer>>();
        oneTimeHolidays = new HashMap<Integer, HashMap<Integer,ArrayList<Integer>>>();
        //file that is executing (jar)
        File jarFile = new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
        System.out.println(jarFile.getParent().toString());
        //when test running in editor fix path accordingly("/classes/holidays.txt")
        FileInputStream is = new FileInputStream(jarFile.getParent().toString() + "/holidays.txt");
        BufferedReader readHolidays = new BufferedReader(new InputStreamReader(is));
        String line;
        line = readHolidays.readLine();
       
        while(line != null) {
            String[] date = line.split("-");
            //holiday is repetitious
            int year = -1;
            //check whether date[2] is int
            try {
                year = Integer.parseInt(date[2]);
            } catch(NumberFormatException | NullPointerException nfe) {
                
            }
            
            //if year is not changed from -1 then we can assume, that date[2] = "+" and therefore holiday is repetitious
            if(year == -1 && date.length == 3 && date[2].equals("+")) {
                int month = Integer.parseInt(date[1]) - 1;
                int day = Integer.parseInt(date[0]);
                //check if date is valid
                if(month >= 0 && month < 12 && day <= daysOfMonth[month]) {
                    ArrayList<Integer> holidaysInMonth = repHolidays.get(month);
                    if(holidaysInMonth == null) {
                        holidaysInMonth = new ArrayList<Integer>();
                    } 
                    holidaysInMonth.add(day);
                    repHolidays.put(month, holidaysInMonth);
                }
                
            }
            //holiday is not repetitious
            else if(date.length == 3) {
                int day = Integer.parseInt(date[0]);
                int month = Integer.parseInt(date[1]) - 1;
                //check if date is valid
                if(month >= 0 && month < 12 && day <= daysOfMonth[month]) {
                    HashMap<Integer,ArrayList<Integer>> holidaysInYear = oneTimeHolidays.get(year);
                    if(holidaysInYear == null) {
                        holidaysInYear = new HashMap<Integer,ArrayList<Integer>>();
                    }
                     ArrayList<Integer> holidaysInMonth = holidaysInYear.get(month);
                    if(holidaysInMonth == null) {
                        holidaysInMonth = new ArrayList<Integer>();
                    } 
                    holidaysInMonth.add(day);
                    holidaysInYear.put(month, holidaysInMonth);
                    oneTimeHolidays.put(year, holidaysInYear);
                }
            }
            line = readHolidays.readLine();
        }
        readHolidays.close();
        is.close();
        
    }
    
    //array of names of months
    private static final String[] nameOfMonth = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
    //array of number of days in each month, the extra day in february on leap years not included
    private static final int[] daysOfMonth= {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    //maps strings to integers based on Zeller's congruence
    private static final Map<String, Integer> monthToInt;
    static {
        Map<String, Integer> m = new HashMap<String,Integer>();
        m.put("January",13);
        m.put("February", 14);
        m.put("March", 3);
        m.put("April", 4);
        m.put("May", 5);
        m.put("June", 6);
        m.put("July", 7);
        m.put("August", 8);
        m.put("September", 9);
        m.put("October", 10);
        m.put("November", 11);
        m.put("December", 12);
        monthToInt = Collections.unmodifiableMap(m);
    }
    //holidays that happen only once
    private static HashMap<Integer, HashMap<Integer,ArrayList<Integer>>> oneTimeHolidays;
    //holidays that happen every year
    private static HashMap<Integer, ArrayList<Integer>> repHolidays; 
       


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel appName;
    private javax.swing.JScrollPane calendarPanel;
    private javax.swing.JTable calendarTab;
    private javax.swing.JButton cancelBtn;
    private javax.swing.JButton closeDiagBtn;
    private javax.swing.JLabel currDate;
    private javax.swing.JButton enterDateBtn;
    private javax.swing.JDialog enterDateDiag;
    private javax.swing.JPanel enterDateForm;
    private javax.swing.JPanel enterDateHeader;
    private javax.swing.JLabel enterDateLabel;
    private javax.swing.JPanel errorHeader;
    private javax.swing.JLabel errorMessage;
    private javax.swing.JPanel errorMessagePanel;
    private javax.swing.JLabel errorTitle;
    private javax.swing.JButton generateBtn;
    private javax.swing.JButton getCalendarBtn;
    private javax.swing.JLabel headerBackground;
    private javax.swing.JPanel headerPanel;
    private javax.swing.JComboBox<String> monthInput;
    private javax.swing.JLabel monthLabel;
    private javax.swing.JTextField specDateDay;
    private javax.swing.JComboBox<String> specDateMonth;
    private javax.swing.JTextField specDateYear;
    private javax.swing.JLabel specDayLabel;
    private javax.swing.JLabel specMonthLabel;
    private javax.swing.JLabel specYearLabel;
    private javax.swing.JDialog wrongInputDiag;
    private javax.swing.JTextField yearInput;
    private javax.swing.JLabel yearLabel;
    // End of variables declaration//GEN-END:variables
}

