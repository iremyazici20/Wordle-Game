package dragDrop;

import enums.LetterStatus;
import enums.LetterType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/*
 * Kaynak:
 * http://www.iitk.ac.in/esc101/05Aug/tutorial/uiswing/misc/example-1dot4/index.html#DragPictureDemo
 */


public abstract class Letter extends JComponent implements MouseListener, FocusListener {

    protected Character c; 
    protected LetterType letterType; 
    protected LetterStatus letterStatus; 

    public Letter(Character c) {
        this.c = c;
        this.letterStatus = LetterStatus.ONGOING_ANSWER;
        setFocusable(true);
        addMouseListener(this);
        addFocusListener(this);
    }

    public abstract Character getChar();

    public abstract void setChar(Character c);

    public abstract LetterStatus getStatus();

    public abstract void setStatus(LetterStatus status);

    @Override
    public void focusGained(FocusEvent e) {}

    @Override
    public void focusLost(FocusEvent e) {}

    @Override
    public void mouseClicked(MouseEvent e) {
        // Program penceresinde odak isteniyor
        requestFocusInWindow();
    }

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics g = graphics.create();
        switch (letterStatus) {
            case CORRECT -> g.setColor(Color.GREEN);
            case MISPLACED -> g.setColor(Color.YELLOW);
            case WRONG -> g.setColor(Color.GRAY);
            default -> g.setColor(Color.WHITE);
        }
        g.fillRect(0, 0, 100, 100);
        g.setColor(Color.BLACK);
        g.drawRect(0, 0, 100, 100);

        if (c != null) {
            g.drawString(String.valueOf(c), 25, 25);
        }

        g.dispose();
    }
}
