import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

public class PairMatchGame {
    class Card {
        String name;
        ImageIcon face;

        Card(String name, ImageIcon face) {
            this.name = name;
            this.face = face;
        }

        public String toString() {
            return name;
        }
    }

    String[] cardNames = {
        "burgerfries", "cake", "cheesecake", "eggomelet", 
        "hotchocolate", "icecream", "noodles", "pancake", 
        "popcorn", "sandwich", "takoyaki", "sushi"
    };

    int rows = 4;
    int cols = 6;
    int cardWidth = 90;
    int cardHeight = 128;

    ArrayList<Card> cards;
    ImageIcon backImage;

    int boardWidth = cols * cardWidth;
    int boardHeight = rows * cardHeight;

    JFrame frame = new JFrame("Pair Match Game");
    JLabel errorLabel = new JLabel();
    JPanel infoPanel = new JPanel();
    JPanel gamePanel = new JPanel();
    JPanel controlPanel = new JPanel();
    JButton restartButton = new JButton();

    int errorCount = 0;
    ArrayList<JButton> tiles;
    Timer hideCardsTimer;
    boolean gameActive = false;
    JButton firstSelected;
    JButton secondSelected;

    PairMatchGame() {
        initializeCards();
        shuffleCards();

        frame.setLayout(new BorderLayout());
        frame.setSize(boardWidth, boardHeight + 60);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Error Label
        errorLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        errorLabel.setHorizontalAlignment(JLabel.CENTER);
        errorLabel.setText("Errors: " + errorCount);

        infoPanel.setPreferredSize(new Dimension(boardWidth, 30));
        infoPanel.add(errorLabel);
        frame.add(infoPanel, BorderLayout.NORTH);

        // Game Board
        tiles = new ArrayList<>();
        gamePanel.setLayout(new GridLayout(rows, cols));
        for (int i = 0; i < cards.size(); i++) {
            JButton tile = new JButton();
            tile.setPreferredSize(new Dimension(cardWidth, cardHeight));
            tile.setOpaque(true);
            tile.setIcon(backImage);
            tile.setFocusable(false);
            tile.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (!gameActive) {
                        return;
                    }
                    JButton clickedTile = (JButton) e.getSource();
                    if (clickedTile.getIcon() == backImage) {
                        if (firstSelected == null) {
                            firstSelected = clickedTile;
                            int index = tiles.indexOf(firstSelected);
                            firstSelected.setIcon(cards.get(index).face);
                        } else if (secondSelected == null) {
                            secondSelected = clickedTile;
                            int index = tiles.indexOf(secondSelected);
                            secondSelected.setIcon(cards.get(index).face);

                            if (firstSelected.getIcon() != secondSelected.getIcon()) {
                                errorCount++;
                                errorLabel.setText("Errors: " + errorCount);
                                hideCardsTimer.start();
                            } else {
                                firstSelected = null;
                                secondSelected = null;
                            }
                        }
                    }
                }
            });
            tiles.add(tile);
            gamePanel.add(tile);
        }
        frame.add(gamePanel);

        // Restart Button
        restartButton.setFont(new Font("Arial", Font.PLAIN, 16));
        restartButton.setText("Restart Game");
        restartButton.setPreferredSize(new Dimension(boardWidth, 30));
        restartButton.setFocusable(false);
        restartButton.setEnabled(false);
        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!gameActive) {
                    return;
                }

                gameActive = false;
                restartButton.setEnabled(false);
                firstSelected = null;
                secondSelected = null;
                shuffleCards();

                for (JButton tile : tiles) {
                    tile.setIcon(backImage);
                }

                errorCount = 0;
                errorLabel.setText("Errors: " + errorCount);
                showCardsBriefly();
            }
        });
        controlPanel.add(restartButton);
        frame.add(controlPanel, BorderLayout.SOUTH);

        frame.pack();
        frame.setVisible(true);

        // Timer for hiding cards
        hideCardsTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                hideCards();
            }
        });
        hideCardsTimer.setRepeats(false);

        // Show cards for 1 second
        showCardsBriefly();
    }

    void initializeCards() {
        cards = new ArrayList<>();
        for (String name : cardNames) {
            
	Image img = new ImageIcon(getClass().getResource("/img/" + name + ".jpg")).getImage();


            ImageIcon faceIcon = new ImageIcon(img.getScaledInstance(cardWidth, cardHeight, Image.SCALE_SMOOTH));
            Card card = new Card(name, faceIcon);
            cards.add(card);
        }
        cards.addAll(cards); // Duplicate for pairs

        Image backImg = new ImageIcon(getClass().getResource("/img/back.jpg")).getImage();
        backImage = new ImageIcon(backImg.getScaledInstance(cardWidth, cardHeight, Image.SCALE_SMOOTH));
    }

    void shuffleCards() {
        for (int i = 0; i < cards.size(); i++) {
            int j = (int) (Math.random() * cards.size());
            Card temp = cards.get(i);
            cards.set(i, cards.get(j));
            cards.set(j, temp);
        }
    }

    void hideCards() {
        if (gameActive && firstSelected != null && secondSelected != null) {
            firstSelected.setIcon(backImage);
            firstSelected = null;
            secondSelected.setIcon(backImage);
            secondSelected = null;
        } else {
            for (JButton tile : tiles) {
                tile.setIcon(backImage);
            }
            gameActive = true;
            restartButton.setEnabled(true);
        }
    }

    void showCardsBriefly() {
        for (int i = 0; i < tiles.size(); i++) {
            tiles.get(i).setIcon(cards.get(i).face);
        }
        Timer showTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                hideCards();
            }
        });
        showTimer.setRepeats(false);
        showTimer.start();
    }

    public static void main(String[] args) {
        new PairMatchGame();
    }
}
