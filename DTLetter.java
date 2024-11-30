package dragDrop;

import enums.LetterStatus;
import enums.LetterType;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

/*
 * http://www.iitk.ac.in/esc101/05Aug/tutorial/uiswing/misc/example-1dot4/index.html#DragPictureDemo
 */

public class DTLetter extends Letter implements MouseMotionListener {

    private MouseEvent firstMouseEvent = null;

    public DTLetter(Character c, LetterType letterType) {
        super(c);
        this.letterType = letterType;
        addMouseMotionListener(this);
    }

    @Override
    public Character getChar() {
        return c;
    }

    @Override
    public void setChar(Character c) {
        this.c = c;
        this.repaint();
    }

    @Override
    public LetterStatus getStatus() {
        return letterStatus;
    }

    @Override
    public void setStatus(LetterStatus status) {
        this.letterStatus = status;
        this.repaint();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (c == null) return;

        firstMouseEvent = e;
        e.consume();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (c == null) return;

        if (firstMouseEvent != null) {
            e.consume();

            int dx = Math.abs(e.getX() - firstMouseEvent.getX());
            int dy = Math.abs(e.getY() - firstMouseEvent.getY());
            if (dx > 5 || dy > 5) {
                JComponent c = (JComponent) e.getSource();
                TransferHandler handler = c.getTransferHandler();
                handler.exportAsDrag(c, firstMouseEvent, TransferHandler.COPY);
                firstMouseEvent = null;
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // Ilk mouse etkinligi sifirlaniyor
        firstMouseEvent = null;
    }

    @Override
    public void mouseMoved(MouseEvent e) {}

}
