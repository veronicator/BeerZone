package it.unipi.dii.inginf.lsmdb.beerzone.gui;

import it.unipi.dii.inginf.lsmdb.beerzone.entities.*;
import it.unipi.dii.inginf.lsmdb.beerzone.entitiyManager.BeerManager;
import it.unipi.dii.inginf.lsmdb.beerzone.entitiyManager.BreweryManager;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.plaf.basic.ComboPopup;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import static javax.swing.BorderFactory.createEmptyBorder;

public class BreweryManagerGUI {
    private static final Integer BREWERY_MANAGER = 1;
    private static final Color BACKGROUND_COLOR = new Color(255, 170, 3);
    private static final Color BACKGROUND_COLOR_LIGHT = new Color(255, 186, 51);
    private static final Integer RECIPE_SECTION_ABV = 1;
    private static final Integer RECIPE_SECTION_AVAILABILITY = 2;
    private static final Integer RECIPE_SECTION_COLOR = 3;
    private static final Integer RECIPE_SECTION_FERMENTABLES = 4;
    private static final Integer RECIPE_SECTION_FG = 5;
    private static final Integer RECIPE_SECTION_HOPS= 6;
    private static final Integer RECIPE_SECTION_IBU = 7;
    private static final Integer RECIPE_SECTION_METHOD = 8;
    private static final Integer RECIPE_SECTION_NOTES = 9;
    private static final Integer RECIPE_SECTION_OG = 10;
    private static final Integer RECIPE_SECTION_OTHER = 11;
    private static final Integer RECIPE_SECTION_PHMASH = 12;
    private static final Integer RECIPE_RECTION_RETIRED  = 13;
    private static final Integer RECIPE_SECTION_URL = 14;
    private static final Integer RECIPE_SECTION_YEAST = 15;


    /**
     * function that creates the brewery manager section
     *
     * @param frame: frame used by the application
     * @param b: brewery information
     */
    public static void breweryManagerSection(JFrame frame, Brewery b){
        JButton[] btnArray = new JButton[5];
        frame.setTitle("BeerZone - BREWERY MANAGER");
        frame.setLayout(new GridLayout(1,2));

        JPanel ljp = new JPanel();
        JPanel rjp = new JPanel();
        rjp.setLayout(new GridBagLayout());
        ljp.setLayout(new GridBagLayout());
        try{
            BufferedImage myPicture = ImageIO.read(new File("C:/images/logobeerzone.png"));
            JLabel picLabel = new JLabel(new ImageIcon(myPicture));
            rjp.add(picLabel);
        } catch (IOException e) {
            e.printStackTrace();
        }
        btnArray[0] = new JButton("Brewery Page");
        btnArray[0].addActionListener(e -> createBreweryPage(rjp, frame, b, b.getUserID(), true));
        btnArray[1] = new JButton("Add beer");
        btnArray[1].addActionListener(e -> generateAddBeerMenu(rjp, frame, b));
        btnArray[2] = new JButton("Browse Beer");
        btnArray[2].addActionListener(e -> BeerZoneGUI.generateBrowseBeerMenu(rjp, frame, b));
        btnArray[3] = new JButton("Extract Brewery Statistics");
        btnArray[3].addActionListener(e -> generateBreweryStatisticsMenu(rjp, frame, b));
        btnArray[4] = new JButton("Logout");
        btnArray[4].addActionListener(e -> BeerZoneGUI.prepareLogRegister(frame));
        setLeftBreweryManagerButton(btnArray, ljp);
        ljp.setBorder(BorderFactory.createLineBorder(Color.black));
        ljp.setBackground(BACKGROUND_COLOR);
        frame.getContentPane().add(ljp);

        rjp.setBorder(BorderFactory.createLineBorder(Color.black));
        rjp.setBackground(BACKGROUND_COLOR);
        frame.getContentPane().add(rjp);

        frame.setVisible(true);
    }

    /**
     * function that creates the "add beer" section
     *
     * @param containerPanel: panel containing the add beer section
     * @param frame: frame used by the application
     * @param b: brewery informations
     */
    private static void generateAddBeerMenu(JPanel containerPanel, JFrame frame, Brewery b) {
        containerPanel.removeAll();
        JTextPane[] inputs = new JTextPane[2];
        String[] recipeTexts = new String[16];
        createInputField(containerPanel, inputs);
        JComboBox<String>[] recipeCB = new JComboBox[1];
        JTextArea[] inputRecipe = new JTextArea[1];
        BeerZoneGUI.createRecipeSection(containerPanel, 3, recipeTexts, BREWERY_MANAGER, true, null, recipeCB, inputRecipe, frame, inputs, b);
        JButton btn = new JButton("Add Beer to Brewery");
        btn.setFont(new Font("Arial", Font.BOLD, 16));
        JTextField errorMsg = new JTextField();
        btn.addActionListener(e->{
            recipeTexts[recipeCB[0].getSelectedIndex()] = inputRecipe[0].getText();
            boolean recipeCorrect = checkRecipe(recipeTexts);
            boolean infoCorrect = checkInfo(inputs);
            boolean retiredCorrect;
            if(recipeTexts[RECIPE_RECTION_RETIRED] == null)
                retiredCorrect = false;
            else
                retiredCorrect = (recipeTexts[RECIPE_RECTION_RETIRED].equalsIgnoreCase("Yes") || recipeTexts[RECIPE_RECTION_RETIRED].equalsIgnoreCase("No"));

            if(!recipeCorrect){
                errorMsg.setText("OG - FG - IBU - COLOR - PHMASH -ABV must be numbers");
                errorMsg.setBackground(BACKGROUND_COLOR);
                errorMsg.setBorder(createEmptyBorder());
                errorMsg.setForeground(Color.RED);
                containerPanel.add(errorMsg, new GridBagConstraints(0,6,2,1,0,0,
                        GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(20, 0, 0, 0),0,0));
                frame.repaint();
                frame.setVisible(true);
            }
            else{
                if(!retiredCorrect){
                    errorMsg.setText("Retired field must be 'Yes' or 'No'");
                    containerPanel.remove(errorMsg);
                    errorMsg.setForeground(Color.RED);
                    errorMsg.setBackground(BACKGROUND_COLOR);
                    errorMsg.setBorder(createEmptyBorder());
                    containerPanel.add(errorMsg, new GridBagConstraints(0,6,2,1,0,0,
                            GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(20, 0, 0, 0),0,0));
                    frame.repaint();
                    frame.setVisible(true);
                }
                else{
                    containerPanel.remove(errorMsg);
                    frame.repaint();
                    frame.setVisible(true);
                    if(infoCorrect) {
                        errorMsg.setText("Beer Correctly Added");
                        errorMsg.setForeground(new Color(0, 59, 16));
                        DetailedBeer db = new DetailedBeer(null, inputs[0].getText(), inputs[1].getText(), recipeTexts[RECIPE_SECTION_ABV], null, b.getUserID(),
                                recipeTexts[RECIPE_SECTION_AVAILABILITY], recipeTexts[RECIPE_SECTION_NOTES], recipeTexts[RECIPE_SECTION_URL], recipeTexts[RECIPE_RECTION_RETIRED],
                                recipeTexts[RECIPE_SECTION_METHOD], recipeTexts[RECIPE_SECTION_OG], recipeTexts[RECIPE_SECTION_FG], recipeTexts[RECIPE_SECTION_IBU],
                                recipeTexts[RECIPE_SECTION_COLOR], recipeTexts[RECIPE_SECTION_PHMASH], recipeTexts[RECIPE_SECTION_FERMENTABLES],
                                recipeTexts[RECIPE_SECTION_HOPS], recipeTexts[RECIPE_SECTION_OTHER], recipeTexts[RECIPE_SECTION_YEAST]);
                        if(BreweryManager.getInstance().addNewBeerToBrewery(b, db))
                            btn.setEnabled(false);
                    }
                }
            }
        });
        containerPanel.add(btn, new GridBagConstraints(0,5,2,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(20, 0, 0, 0),0,0));
        frame.repaint();
        frame.setVisible(true);
    }

    /**
     * function that controls if the textfields are correct
     *
     * @param inputs: all the beer info
     * @return correct: contains if the inputs are correct
     */
    public static boolean checkInfo(JTextPane[] inputs) {
        boolean correct = true;
        if(inputs[0].getText().equals("")) {
            inputs[0].setBackground(Color.RED);
            correct = false;
        }
        else
            inputs[0].setBackground(Color.WHITE);

        if(inputs[1].getText().equals("")) {
            inputs[1].setBackground(Color.RED);
            correct = false;
        }
        else
            inputs[1].setBackground(Color.WHITE);

        return correct;
    }

    /**
     * function that checks if the recipe has numbers where numbers are mandatory
     *
     * @param recipeTexts: array containing the recipe infos
     * @return correct: correctness of data
     */
    public static boolean checkRecipe(String[] recipeTexts) {
        boolean correct = true;
        for(int i = 0; i < recipeTexts.length; i++){
            if(recipeTexts[i] == null)
                continue;

            if(i == RECIPE_SECTION_OG || i == RECIPE_SECTION_FG || i == RECIPE_SECTION_IBU || i == RECIPE_SECTION_COLOR
                    || i == RECIPE_SECTION_PHMASH || i == RECIPE_SECTION_ABV) {
                try {
                    Double.parseDouble(recipeTexts[i]);
                } catch (NumberFormatException nfe) {
                    correct = false;
                }
            }
        }

        return correct;
    }

    /**
     * function that creates the input field in the "add beer" section
     *
     * @param panel: panel containing the add beer section
     * @param inputs: brewery manager's inputs
     */
    private static void createInputField(JPanel panel, JTextPane[] inputs) {
        JTextField description = new JTextField("Beer Name");
        description.setFont(new Font("Arial", Font.PLAIN ,15));
        description.setBackground(BACKGROUND_COLOR);
        description.setBorder(createEmptyBorder());
        description.setEditable(false);
        panel.add(description, new GridBagConstraints(0,1,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(20, 0, 0, 10),0,0));

        JTextPane inputField = new JTextPane();
        inputField.setPreferredSize(new Dimension(280,35));
        inputField.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        inputs[0] = inputField;
        panel.add(inputField, new GridBagConstraints(1,1,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(20, 0, 0, 0),0,5));

        description = new JTextField("Style");
        description.setFont(new Font("Arial", Font.PLAIN ,15));
        description.setBackground(BACKGROUND_COLOR);
        description.setBorder(createEmptyBorder());
        description.setEditable(false);
        panel.add(description, new GridBagConstraints(0,2,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(20, 0, 0, 10),0,0));

        inputField = new JTextPane();
        inputField.setPreferredSize(new Dimension(280, 35));
        inputField.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        inputs[1] = inputField;
        panel.add(inputField, new GridBagConstraints(1,2,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(20, 0, 0, 0),0,5));
    }

    /**
     * function that creates the buttons inside the brewery manager area
     *
     * @param btnArray: array containing the buttons inside the brewery manager area
     * @param jp: JPanel that contains the buttons in btnArray
     */
    private static void setLeftBreweryManagerButton(JButton[] btnArray, JPanel jp) {
        GridBagConstraints gbc = new GridBagConstraints(0,0,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 40, 0),70,30);
        jp.add(btnArray[0], gbc);
        gbc.gridy = 1;
        gbc.ipadx = 99;
        jp.add(btnArray[1], gbc);
        gbc.ipadx = 75;
        gbc.gridy = 2;
        jp.add(btnArray[2], gbc);
        gbc.gridy = 3;
        gbc.ipadx = 3;
        jp.add(btnArray[3], gbc);
        gbc = new GridBagConstraints(0,4,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0),110,30);
        jp.add(btnArray[4], gbc);
    }

    /**
     * function that creates the brewery statistics section
     *
     * @param containerPanel: panel containing the "brewery statistics" section
     * @param frame: frame used by the application
     * @param b: brewery informations
     */
    private static void generateBreweryStatisticsMenu(JPanel containerPanel, JFrame frame, Brewery b) {
        containerPanel.removeAll();
        String[] colHeader = {"Beer"};
        String [][] data={};

        JTextField breweryStatsTitle = new JTextField(b.getUsername());
        prepareBreweryStatsTitle(breweryStatsTitle, containerPanel);
        JTextField responseField = new JTextField("Not yet computed");

        JButton breweryStatsBtn = new JButton("Compute brewery score");
        prepareResponseField(responseField, breweryStatsBtn, containerPanel);
        breweryStatsBtn.addActionListener(e ->{
            double BreweryScore= BreweryManager.getInstance().getBreweryScore(b.getUserID());
            if(BreweryScore!=-1)
                responseField.setText(String.valueOf(BreweryScore));
            else
                responseField.setText("There are no review to compute a score!");
            frame.repaint();
            frame.setVisible(true);
        });

        JTable breweryStatsTable = new JTable(data, colHeader);
        prepareBreweryStatsTable(breweryStatsTable, containerPanel, frame, b);

        JComboBox<String>[] featureSelection = new JComboBox[1];
        String[] choices = {"Look", "Smell", "Taste", "Feel", "Overall"};
        featureSelection[0] = new JComboBox<>(choices);
        featureSelection[0].setVisible(true);
        JComboBox<String> finalfeatureSelection=featureSelection[0];
        featureSelection[0].addItemListener(e -> {
            if(e.getStateChange() == ItemEvent.SELECTED) {
                finalfeatureSelection.setForeground(Color.BLACK);
            }
        });
        JPanel featurePanel = new JPanel();
        featurePanel.add(featureSelection[0], new GridBagConstraints(0, 0,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0),0,0));

        breweryStatsBtn = new JButton("find beers under average score by feature");
        breweryStatsBtn.setFont(new Font("Arial", Font.BOLD, 14));
        setBreweryStatsBtn(breweryStatsBtn, containerPanel, featurePanel);

        breweryStatsBtn.addActionListener((e->{
            String feature=choices[finalfeatureSelection.getSelectedIndex()];
            ArrayList<Beer> beers=BeerManager.getInstance().getBeersUnderAvgFeatureScore(b,feature.toLowerCase());
            addResults(breweryStatsTable, frame,beers);
        }));

        frame.repaint();
        frame.setVisible(true);
    }

    private static void addResults(JTable breweryStatsTable, Frame frame, ArrayList<Beer> beers) {
        int n = breweryStatsTable.getModel().getRowCount();
        for (int i=0;i<n;i++){
            breweryStatsTable.getModel().setValueAt("--",i,0);
            breweryStatsTable.getModel().setValueAt("--",i,1);
            breweryStatsTable.getModel().setValueAt("--",i, 2);
        }
        int i=0;
        for (Beer beer : beers){
            if(i<8){
                breweryStatsTable.getModel().setValueAt(beer.getBeerName(),i,0);
                breweryStatsTable.getModel().setValueAt(beer.getScore(),i,1);
                breweryStatsTable.getModel().setValueAt(beer.getBeerID(), i, 2);
            }
            else
                break;
            i++;
        }

        frame.repaint();
        frame.setVisible(true);
    }

    /**
     * function that set the parameters of the title of the brewery statistics section
     *
     * @param breweryStatsTitle: JTextField containing the name of the brewery
     * @param containerPanel: panel containing the "brewery statistics" section
     */
    private static void prepareBreweryStatsTitle(JTextField breweryStatsTitle, JPanel containerPanel) {
        breweryStatsTitle.setFont(new Font("Arial", Font.BOLD, 25));
        breweryStatsTitle.setBackground(BACKGROUND_COLOR);
        breweryStatsTitle.setBorder(createEmptyBorder());
        containerPanel.add(breweryStatsTitle, new GridBagConstraints(0,0,2,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 80, 0),0,0));
    }

    /**
     * function that set the parameters of the brewery's average score section
     *
     * @param responseField: JTextField where the average score of the brewery will be written
     * @param containerPanel: panel containing the "brewery statistics" section
     */
    private static void prepareResponseField(JTextField responseField, JButton breweryStatsBtn, JPanel containerPanel) {
        JPanel breweryStats = new JPanel(new GridBagLayout());
        breweryStats.setBackground(BACKGROUND_COLOR);
        breweryStats.setBorder(createEmptyBorder());
        responseField.setHorizontalAlignment(JTextField.CENTER);
        responseField.setBackground(BACKGROUND_COLOR);
        responseField.setBorder(createEmptyBorder());
        responseField.setFont(new Font("Arial", Font.BOLD, 14));
        breweryStatsBtn.setFont(new Font("Arial", Font.BOLD, 14));
        breweryStats.add(breweryStatsBtn, new GridBagConstraints(0,0,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 20),0,0));
        breweryStats.add(responseField);
        containerPanel.add(breweryStats, new GridBagConstraints(0,1,2,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 60, 0),0,0));
    }

    /**
     * function that prepares the table of the average score for all the possible vote's options
     *
     * @param breweryStatsTable: table where the vote's options will be written
     * @param containerPanel: panel containing the "brewery statistics" section
     */
    private static void prepareBreweryStatsTable(JTable breweryStatsTable, JPanel containerPanel, JFrame frame, Brewery b) {
        DefaultTableModel tableModel = new DefaultTableModel(){
            @Override
            public boolean isCellEditable(int row, int column) {
                //all cells false
                return false;
            }
        };
        String[] col0 = {"--", "--", "--", "--", "--", "--", "--", "--"};
        String[] col1 = {"--", "--", "--", "--", "--", "--", "--", "--"};
        String[] col2 = {"--", "--", "--", "--", "--", "--", "--", "--"};
        tableModel.addColumn("Beer", col0);
        tableModel.addColumn("Feature Score",col1);
        tableModel.addColumn("BeerID",col2);
        breweryStatsTable.setRowHeight(25);
        breweryStatsTable.setModel(tableModel);
        TableColumnModel tcm = breweryStatsTable.getColumnModel();
        tcm.removeColumn(tcm.getColumn(2));
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment( JLabel.CENTER );
        breweryStatsTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        breweryStatsTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        breweryStatsTable.getColumnModel().getColumn(1).setPreferredWidth(20);

        JScrollPane jsc = new JScrollPane(breweryStatsTable);
        jsc.setPreferredSize(new Dimension(450, 225));
        containerPanel.add(jsc, new GridBagConstraints(0,3,2,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0),0,0));

        breweryStatsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 2){
                    String id = breweryStatsTable.getModel().getValueAt(breweryStatsTable.getSelectedRow(),2).toString();
                    if(!id.equals("--")){
                        DetailedBeer beer= BeerManager.getInstance().getDetailedBeer(id);
                        BeerZoneGUI.createBeerPage(containerPanel,frame,beer,b);
                    }
                }
            }
        });
    }
    /**
     * function that prepares the button that allows the brewery to compute all the statistics
     *  @param breweryStatsBtn : button that allows the brewery to compute all the statistics
     * @param containerPanel : panel containing the "brewery statistics" section
     */
    private static void setBreweryStatsBtn(JButton breweryStatsBtn, JPanel containerPanel, JPanel featurePanel) {
        featurePanel.add(breweryStatsBtn, new GridBagConstraints(1,0,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 10, 0),0,0));
        featurePanel.setBorder(createEmptyBorder());
        featurePanel.setBackground(BACKGROUND_COLOR);
        containerPanel.add(featurePanel ,new GridBagConstraints(0, 2,2,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 5, 0),0,0));
    }

    /**
     * function that creates a button that allows the user to return to the browse beer section
     *
     * @param jp : JPanel that contains the review page
     * @param frame : frame used by the application
     * @param user: logged user
     */
    public static void prepareReturnToBrowseButton(JPanel jp, JFrame frame, GeneralUser user) {
        JButton returnToBrowse = new JButton("Go To Browse");
        Brewery b = (Brewery)user;
        returnToBrowse.addActionListener(e -> BeerZoneGUI.generateBrowseBeerMenu(jp, frame, b));

        jp.add(returnToBrowse, new GridBagConstraints(0,6,2,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 0, 0, 0),0, 0));
    }


    /**
     * function that creates the personal page of the brewery
     *
     * @param containerPanel: panel containing the brewery page
     * @param frame: frame used by the application
     * @param user: logged user
     * @param breweryId: id of the requested brewery
     */
    public static void createBreweryPage(JPanel containerPanel, JFrame frame,  GeneralUser user, String breweryId, boolean editable) {
        containerPanel.removeAll();
        StandardUser su = null;
        Brewery b;
        if(user.isStandard()) {
            su = (StandardUser) user;
            b = BreweryManager.getInstance().getBrewery(breweryId);
        }
        else
            b = (editable) ? (Brewery) user : BreweryManager.getInstance().getBrewery(breweryId);

        JTextPane[] inputs = new JTextPane[4];
        JPanel jp = new JPanel(new GridBagLayout());
        jp.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        jp.setBackground(BACKGROUND_COLOR_LIGHT);

        BeerZoneGUI.addGenericFields(jp,"Brewery Name", b.getUsername(), 0, inputs, (su == null && editable));
        BeerZoneGUI.addGenericFields(jp,"Email", b.getEmail(), 1, inputs, false);
        BeerZoneGUI.addGenericFields(jp,"Location", b.getLocation(), 2, inputs, (su == null && editable));
        BeerZoneGUI.addGenericFields(jp, "Brewery Type", b.getTypes(), 3, inputs, (su == null && editable));

        //get the beers associated with the brewery
        int dim = 0;
        if(b.getBeers() != null)
            dim = b.getBeers().size();

        String[] beerId = new String[dim];
        String[] beerName = new String[dim];
        for(int i = 0; i < dim; i++){
            beerId[i] = b.getBeers().get(i).getBeerID();
            beerName[i] = b.getBeers().get(i).getBeerName();
        }
        JComboBox<String> beerListCB = new JComboBox<>(beerName);
        beerListCB.setVisible(true);
        beerListCB.setPreferredSize(new Dimension(220, 30));
        ComboPopup popup = (ComboPopup) beerListCB.getUI().getAccessibleChild(beerListCB, 0);
        ((JComponent) popup).setPreferredSize(new Dimension(220, 90));
        ((JComponent) popup).setLayout(new GridLayout(1, 1));
        containerPanel.add(beerListCB, new GridBagConstraints(0, 1,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 10, 15, 10),0,0));

        JButton goToBeer = new JButton("Go To Beer");
        goToBeer.setEnabled(dim != 0);
        goToBeer.addActionListener(e ->{
            DetailedBeer selBeer = BeerManager.getInstance().getDetailedBeer(beerId[beerListCB.getSelectedIndex()]);
            BeerZoneGUI.createBeerPage(containerPanel, frame, selBeer, user);
        });
        containerPanel.add(goToBeer,  new GridBagConstraints(1, 1,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 0, 15, 0),0,0));
        JButton updateBrewery = new JButton("Update brewery");
        updateBrewery.addActionListener(e->{
            b.setUsername(inputs[0].getText());
            b.setLocation(inputs[2].getText());
            b.setTypes(inputs[3].getText());
            BreweryManager.getInstance().updateBrewery(b);
        });
        if(su == null && editable) {
            containerPanel.add(updateBrewery, new GridBagConstraints(0, 2, 2, 1, 0, 0,
                    GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 0, 15, 0), 0, 0));
        }

        JButton deleteBrewery = new JButton("Delete Brewery");
        deleteBrewery.setFont(new Font("Arial", Font.BOLD, 15));
        deleteBrewery.setBackground(Color.RED);
        deleteBrewery.setPreferredSize(new Dimension(200, 40));
        deleteBrewery.setForeground(Color.WHITE);
        deleteBrewery.addActionListener(e-> {
            BreweryManager.getInstance().deleteBrewery(b);
            BeerZoneGUI.prepareLogRegister(frame);
        });
        if(su == null && editable) {
            containerPanel.add(deleteBrewery, new GridBagConstraints(0, 3, 2, 1, 0, 0,
                    GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 0, 15, 0), 0, 0));
        }

        containerPanel.add(jp, new GridBagConstraints(0, 0,2,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 0, 15, 0),0,0));
        frame.repaint();
        frame.setVisible(true);
    }
}
