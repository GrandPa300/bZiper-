import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;

import java.awt.event.*;

import java.io.File;

import java.util.List;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.border.CompoundBorder;

import bwz.bwZip;

public class BZiper extends JFrame
{
    // panel for dropping area. 
    private JPanel panel; 
    // manu bar
    private JMenuBar manuBar = new JMenuBar();
    private JMenu help;
    private JMenuItem about, exit;
    // button
    private JButton button;
    // dialog for open-file, and save-file
    private JFileChooser openFC, saveFC;
    // logo to custom dialog
    private ImageIcon logo, icon;
   
    // bwZip 
    private bwZip zip = new bwZip();

    public BZiper()
    {
        java.net.URL logoURL = BZiper.class.getResource("images/bziper.png");
        java.net.URL iconURL = BZiper.class.getResource("images/add_icon.png");
        
        logo = new ImageIcon(logoURL);
        icon = new ImageIcon(iconURL);
        
        menuInit(); // JManuBar Prep
        panelInit(); // JPanel Prep

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.getContentPane().add(panel, BorderLayout.CENTER);
        this.setSize(500, 500);
        this.setLocation(400, 200);
        this.setTitle("bZiper!");
        this.setJMenuBar(manuBar);

        openFC = new JFileChooser();
        openFC.setCurrentDirectory(new java.io.File(System.getProperty("user.home")));
        openFC.setDialogTitle("Open File");
        openFC.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        saveFC = new JFileChooser();
        saveFC.setCurrentDirectory(new java.io.File(System.getProperty("user.home")));
        saveFC.setDialogTitle("Save As");
        saveFC.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        //if (openFC.showOpenDialog(button) == JFileChooser.APPROVE_OPTION){}
        //System.out.println("File Name" + openFC.getSelectedFile().getAbsolutePath());
    }

    private void processClick()
    {
        button.addActionListener(new ActionListener() 
        {
            public void actionPerformed(ActionEvent e) 
            {
                if (e.getSource() == button)
                {
                    File input = null;
                    if (openFC.showOpenDialog(button) == JFileChooser.APPROVE_OPTION)
                    {
                        input = openFC.getSelectedFile();
                    }
                    savingFile(input);   
                }
            }
        });

        about.addActionListener(new ActionListener() 
        {
            public void actionPerformed(ActionEvent e) 
            {
                String info = "<html>" + 
                                "<h1><center>Thank you for using bZiper!</center></h1>" +
                                    "<div><center>" +
                                        "<p>" +
                                            "<b>bZiper!</b>" + 
                                            " is a data compression application based on Burrows-Wheeler Algorithm and Huffman Algorithm." +
                                        "</p>" +
                                        "<p>Current version is 1.1</p>" +
                                        "<p>bZiper! only supports single-file input at this stage.</p>" +
                                    "</center></div>" +
                                "</html>";

                if (e.getSource() == about)
                {
                    //custom title, custom icon
                    JOptionPane.showMessageDialog(null,
                                                  info,
                                                  "About bZiper!",
                                                  JOptionPane.INFORMATION_MESSAGE,
                                                  logo);
                }
            }
        });

        exit.addActionListener(new ActionListener() 
        {
            public void actionPerformed(ActionEvent e) 
            {
                if (e.getSource() == exit) System.exit(0);
            }
        });
    }

    // save file
    private void savingFile(File input)
    {
        int saveOption = -1 ; // yes = 2, no = 1, cancel = 0
       
        String fileDir = "", fileName = "", fileExt = "";
        String saveDir = "", saveName = "";
        boolean compress = true;

        // input a file
        if (input != null && checkLegal(input))
        {
            fileDir = input.getParent() + "/";
            fileName = input.getName();
            
            String[] parts = fileName.split("\\.");
            int size = parts.length;

            if (size > 1) 
            {
                if (parts[size - 1].equals("bwz"))
                {
                    compress = false;
                    if (size > 2) fileExt = "." + parts[size - 2];
                }

                else fileExt = "." + parts[size - 1];
            }

            saveOption = confirmOption(fileName, compress);
        }

        // no input
        else return;

        // user choose to save in non-default output
        if (saveOption == 1) 
        {
            File output = null;
            if (saveFC.showOpenDialog(button) == JFileChooser.APPROVE_OPTION)
            {
                output = saveFC.getSelectedFile();
            }

            if (output == null) return;

            else
            {
                saveDir = output.getParent() + "/";
                saveName = output.getName();
                saveName = saveNameReg(saveName, fileExt, compress);

                if (compress)
                {
                    if (checkExists(saveDir + saveName) != JOptionPane.YES_OPTION) 
                        return;
                    zip.compress(fileDir + fileName, saveDir + saveName);
                    JOptionPane.showMessageDialog(null, "File is compressed into" + saveDir + saveName);
                }

                else
                {
                    if (checkExists(saveDir + saveName) != JOptionPane.YES_OPTION) 
                        return;
                    zip.decompress(fileDir + fileName, saveDir + saveName);
                    JOptionPane.showMessageDialog(null, "File is decompressed into" + saveDir + saveName);
                }
            }  
        }

        // user choose to save in default output
        else if (saveOption == 2) 
        {
            if (compress)
            {
                saveName = saveNameReg(fileDir + fileName + ".bwz", fileExt, compress);
                if (checkExists(saveName) != JOptionPane.YES_OPTION) 
                    return;
                zip.compress(fileDir + fileName, saveName);
                JOptionPane.showMessageDialog(null, "File is compressed into " + saveName);
            }

            else
            {
                saveName = fileName.substring(0, fileName.length() - 4); // trim off .bwz
                saveName = saveNameReg(fileDir + saveName, fileExt, compress);
                if (checkExists(saveName) != JOptionPane.YES_OPTION) 
                    return;
                zip.decompress(fileDir + fileName, saveName);
                JOptionPane.showMessageDialog(null, "File is decompressed into " + saveName);
            }
        }

        else return;
    }

    // ensure file is saved in legal name to preserve ext
    private String saveNameReg(String name, String fileExt,boolean compress)
    {
        String[] parts = name.split("\\.");
        int size = parts.length;
        int extSize = 0; 

        // output file name need to be filename + original ext + bwz
        // replace last to parts
        if (compress)
        {
            // when given file name has more than 1 ext part
            if (size > 2) 
            {
                extSize = parts[size - 2].length() + parts[size - 1].length() + 2; 
                String noExtName = name.substring(0, name.length() - extSize);
                return noExtName + fileExt + ".bwz";
            }

            // when given file name has 1 or no ext
            else return parts[0] + fileExt + ".bwz";
        }

        // for decompress just make sure String ends with ext
        else
        {
            // when given file name has more than 1 ext
            // replace last part
            if (size > 2) 
            {
                extSize = parts[size - 1].length() + 1; 
                String noExtName = name.substring(0, name.length() - extSize);
                return noExtName + fileExt;
            }

            // when given file name has 1 or no ext
            else return parts[0] + fileExt;
        }
    }

    private int checkExists(String abusolutePath)
    {
        File file = new File(abusolutePath);

        if (file.exists()) 
        {
            int response = JOptionPane.showConfirmDialog
                            (null, 
                            "Do you want to replace the existing file?", 
                            "Confirm", 
                            JOptionPane.YES_NO_OPTION, 
                            JOptionPane.QUESTION_MESSAGE);

            return response;
        } 
        else return 0;
    }

    private boolean checkLegal(File input)
    {
        if (input.isDirectory() || !input.exists()) 
        {
            JOptionPane.showMessageDialog(null, 
                                          "Please select a single file.",
                                          "Input is not found or supported!",
                                          JOptionPane.ERROR_MESSAGE);
            return false;
        }
        else return true;
    }

    // confirm file and options
    private int confirmOption(String input, boolean compress)
    {
        String action = compress ? "Compress" : "Decompress";
        String message = action + " [ " + input + " ] using default output name?";

        Object[] options = {"Cancel", "No", "Yes"};
        int res = JOptionPane.showOptionDialog(null, message,"Please Confirm!",
                                               JOptionPane.YES_NO_CANCEL_OPTION, 
                                               JOptionPane.QUESTION_MESSAGE,
                                               null, options, options[2]);
        return res;
    }
    

    // panel initialize
    private void panelInit()
    {
        // border init
        Border emptyline = BorderFactory.createEmptyBorder(5, 10, 10, 10);
        Border dashline = BorderFactory.createDashedBorder(Color.GRAY, 2, 4, 1, false);
        Border title = BorderFactory.createTitledBorder
                       (dashline, "To Zip / Unzip", TitledBorder.LEFT, TitledBorder.TOP);
        // combine border lines
        CompoundBorder border = new CompoundBorder(emptyline, title); 

        panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(border);

        addButton(panel); // init button and add to panel
        addLabel(panel);  // init label and add to panel
    }

    // menu initialize
    private void menuInit()
    {
        help = new JMenu("Help"); 
        manuBar.add(help);

        about = new JMenuItem("About bZiper!");
        help.add(about);

        exit = new JMenuItem("Exit");
        help.add(exit);
    }

    // init button and add to panel with constraints
    private void addButton(JPanel target)
    {
        button = new JButton();
        button.setBounds(50,50,100,100);
        //ImageIcon icon = new ImageIcon(BZiper.class.getResource("images/add_icon.png"));
        //ImageIcon icon = new ImageIcon("add_icon.png");
        
        // resize the original icon to fit button size
        button.setIcon(resizeIcon(icon, button.getWidth(), button.getHeight()));
        // show only icon
        button.setContentAreaFilled(false); 

        GridBagConstraints setup = new GridBagConstraints();
        setup.gridx = 0;
        setup.gridy = 1;
        target.add(button, setup);
    }

    // init label and add to panel with constraints
    private void addLabel(JPanel target)
    {
        JLabel label = new JLabel("Drop a file / Click icon below"); 
        label.setFont(new Font("Helvetica", Font.PLAIN, 15));
        label.setForeground(Color.DARK_GRAY);

        GridBagConstraints setup = new GridBagConstraints();
        setup.gridx = 0;
        setup.gridy = 0;
        //setup.insets = new Insets(0,0,50,0);
        target.add(label, setup);
    }
    
    private static Icon resizeIcon(ImageIcon icon, int resizedWidth, int resizedHeight) 
    {
        Image img = icon.getImage();  
        Image resizedImage = img.getScaledInstance(resizedWidth, resizedHeight,  java.awt.Image.SCALE_SMOOTH);  
        return new ImageIcon(resizedImage);
    }
    
    public static void main(String[] args) throws Exception
    {
        UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
        BZiper run = new BZiper(); 
        run.processDrag();
        run.processClick();
        run.setVisible(true);
    }
    

    public void processDrag()
    {
        new DropTarget(panel, DnDConstants.ACTION_COPY_OR_MOVE, new DropTargetAdapter()
        {
            @Override
            public void drop(DropTargetDropEvent dtde) // overwrite drop
            {
                try
                {
                    if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) // if file is supported
                    {
                        dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE); // accept dragged data

                        List<File> list =  (List<File>) (dtde.getTransferable().getTransferData(DataFlavor.javaFileListFlavor));

                        // this version only process single file
                        if (list.size() == 1) savingFile(list.get(0));
                        else 
                        {
                            JOptionPane.showMessageDialog(null, 
                                                          "Please select a single file.",
                                                          "Input is not found or supported!",
                                                          JOptionPane.ERROR_MESSAGE);
                        }
                        dtde.dropComplete(true); // drag completed
                    }

                    else
                    {
                        dtde.rejectDrop(); // reject data
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }
}